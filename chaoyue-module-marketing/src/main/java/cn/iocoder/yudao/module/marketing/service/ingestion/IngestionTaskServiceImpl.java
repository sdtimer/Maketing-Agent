package cn.iocoder.yudao.module.marketing.service.ingestion;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.common.util.json.JsonUtils;
import cn.iocoder.yudao.module.marketing.controller.admin.ingestion.vo.IngestionImageCreateReqVO;
import cn.iocoder.yudao.module.marketing.controller.admin.ingestion.vo.IngestionManualCreateReqVO;
import cn.iocoder.yudao.module.marketing.controller.admin.ingestion.vo.IngestionTaskPageReqVO;
import cn.iocoder.yudao.module.marketing.controller.admin.ingestion.vo.IngestionTaskPageRespVO;
import cn.iocoder.yudao.module.marketing.controller.admin.ingestion.vo.IngestionTaskRespVO;
import cn.iocoder.yudao.module.marketing.controller.admin.ingestion.vo.IngestionUrlCreateReqVO;
import cn.iocoder.yudao.module.marketing.dal.dataobject.ingestion.ExtractionArtifactDO;
import cn.iocoder.yudao.module.marketing.dal.dataobject.ingestion.IngestionTaskDO;
import cn.iocoder.yudao.module.marketing.dal.mysql.ingestion.ExtractionArtifactMapper;
import cn.iocoder.yudao.module.marketing.dal.mysql.ingestion.IngestionTaskMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil.exception;
import static cn.iocoder.yudao.module.marketing.enums.ErrorCodeConstants.MARKETING_INGESTION_IDEMPOTENCY_CONFLICT;

/**
 * 平台收录任务：统一创建待校对草稿。
 *
 * M1 只持久化人工提供的输入和对象键；URL、图片的实际解析将通过受控异步 worker 接入，
 * 这里不抓取页面、不绕过登录或验证码，也不向租户端发布内容。
 */
@Service
@RequiredArgsConstructor
public class IngestionTaskServiceImpl implements IngestionTaskService {

    private static final long PLATFORM_SCOPE_TENANT_ID = 0L;
    private static final String STATUS_PARSED_DRAFT = "parsed_draft";

    private final IngestionTaskMapper taskMapper;
    private final ExtractionArtifactMapper artifactMapper;

    @Override
    public PageResult<IngestionTaskPageRespVO> getTaskPage(IngestionTaskPageReqVO reqVO) {
        PageResult<IngestionTaskDO> page = taskMapper.selectPage(reqVO);
        List<IngestionTaskPageRespVO> list = page.getList().stream().map(task -> {
            IngestionTaskPageRespVO resp = new IngestionTaskPageRespVO();
            resp.setId(task.getId());
            resp.setMethod(task.getMethod());
            resp.setMarket(task.getMarket());
            resp.setChannel(task.getChannel());
            resp.setStatus(task.getStatus());
            resp.setAttempt(task.getAttempt());
            resp.setVersion(task.getVersion());
            resp.setCreateTime(task.getCreateTime());
            return resp;
        }).toList();
        return new PageResult<>(list, page.getTotal());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public IngestionTaskRespVO createManual(IngestionManualCreateReqVO reqVO) {
        Map<String, Object> payload = new LinkedHashMap<>();
        payload.put("recordType", reqVO.getRecordType());
        payload.put("subjectKey", reqVO.getSubjectKey());
        payload.put("title", reqVO.getTitle());
        payload.put("content", reqVO.getContent());
        return create("manual", reqVO.getMarket(), reqVO.getChannel(), reqVO.getIdempotencyKey(), payload, null, null);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public IngestionTaskRespVO createUrl(IngestionUrlCreateReqVO reqVO) {
        Map<String, Object> payload = new LinkedHashMap<>();
        payload.put("sourceUrl", reqVO.getSourceUrl());
        return create("parse-url", reqVO.getMarket(), reqVO.getChannel(), reqVO.getIdempotencyKey(), payload,
                reqVO.getSourceUrl(), null);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public IngestionTaskRespVO createImage(IngestionImageCreateReqVO reqVO) {
        Map<String, Object> payload = new LinkedHashMap<>();
        payload.put("objectKey", reqVO.getObjectKey());
        return create("recognize-image", reqVO.getMarket(), reqVO.getChannel(), reqVO.getIdempotencyKey(), payload,
                null, reqVO.getObjectKey());
    }

    private IngestionTaskRespVO create(String method, String market, String channel, String idempotencyKey,
                                        Map<String, Object> payload, String sourceUrl, String objectKey) {
        String action = "ingestion." + method;
        Map<String, Object> fingerprintPayload = new LinkedHashMap<>();
        fingerprintPayload.put("method", method);
        fingerprintPayload.put("market", market);
        fingerprintPayload.put("channel", channel);
        fingerprintPayload.put("payload", payload);
        String inputJson = JsonUtils.toJsonString(fingerprintPayload);
        String fingerprint = sha256(inputJson);

        IngestionTaskDO existing = taskMapper.selectByIdempotency(PLATFORM_SCOPE_TENANT_ID, action, idempotencyKey);
        if (existing != null) {
            return reuseOrReject(existing, fingerprint);
        }

        IngestionTaskDO task = new IngestionTaskDO();
        task.setScopeTenantId(PLATFORM_SCOPE_TENANT_ID);
        task.setAction(action);
        task.setMethod(method);
        task.setMarket(market);
        task.setChannel(channel);
        task.setStatus(STATUS_PARSED_DRAFT);
        task.setAttempt(0);
        task.setIdempotencyKey(idempotencyKey);
        task.setRequestFingerprint(fingerprint);
        try {
            taskMapper.insert(task);
        } catch (DuplicateKeyException ignored) {
            // 并发的同一幂等键不创建第二条任务；按既有任务的指纹决定复用或冲突。
            return reuseOrReject(taskMapper.selectByIdempotency(PLATFORM_SCOPE_TENANT_ID, action, idempotencyKey), fingerprint);
        }

        ExtractionArtifactDO artifact = new ExtractionArtifactDO();
        artifact.setTaskId(task.getId());
        artifact.setSourceUrl(sourceUrl);
        artifact.setObjectKey(objectKey);
        artifact.setInputPayload(inputJson);
        artifact.setExtracted(JsonUtils.toJsonString(Map.of("state", STATUS_PARSED_DRAFT, "requiresProofreading", true)));
        artifact.setConfidence("{}");
        artifactMapper.insert(artifact);
        return new IngestionTaskRespVO(task.getId(), task.getStatus());
    }

    private IngestionTaskRespVO reuseOrReject(IngestionTaskDO existing, String fingerprint) {
        if (existing != null && fingerprint.equals(existing.getRequestFingerprint())) {
            return new IngestionTaskRespVO(existing.getId(), existing.getStatus());
        }
        throw exception(MARKETING_INGESTION_IDEMPOTENCY_CONFLICT);
    }

    private static String sha256(String value) {
        try {
            byte[] digest = MessageDigest.getInstance("SHA-256").digest(value.getBytes(StandardCharsets.UTF_8));
            StringBuilder hex = new StringBuilder(64);
            for (byte item : digest) {
                hex.append(String.format("%02x", item));
            }
            return hex.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException("SHA-256 is required by the JVM", e);
        }
    }
}
