package cn.iocoder.yudao.module.marketing.service.ingestion;

import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import cn.iocoder.yudao.framework.common.util.json.JsonUtils;
import cn.iocoder.yudao.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.iocoder.yudao.module.marketing.controller.admin.ingestion.vo.IngestionDraftRespVO;
import cn.iocoder.yudao.module.marketing.controller.admin.ingestion.vo.IngestionDraftUpdateReqVO;
import cn.iocoder.yudao.module.marketing.controller.admin.ingestion.vo.IngestionFieldChangeRespVO;
import cn.iocoder.yudao.module.marketing.dal.dataobject.ingestion.ExtractionArtifactDO;
import cn.iocoder.yudao.module.marketing.dal.dataobject.ingestion.IngestionFieldChangeDO;
import cn.iocoder.yudao.module.marketing.dal.dataobject.ingestion.IngestionTaskDO;
import cn.iocoder.yudao.module.marketing.dal.mysql.ingestion.ExtractionArtifactMapper;
import cn.iocoder.yudao.module.marketing.dal.mysql.ingestion.IngestionFieldChangeMapper;
import cn.iocoder.yudao.module.marketing.dal.mysql.ingestion.IngestionTaskMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.LinkedHashMap;
import java.util.Map;

import static cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil.exception;
import static cn.iocoder.yudao.module.marketing.enums.ErrorCodeConstants.*;

@Service
@RequiredArgsConstructor
public class IngestionDraftServiceImpl implements IngestionDraftService {
    private final IngestionTaskMapper taskMapper;
    private final ExtractionArtifactMapper artifactMapper;
    private final IngestionFieldChangeMapper fieldChangeMapper;

    @Override
    public IngestionDraftRespVO getDraft(Long taskId) {
        IngestionTaskDO task = requireTask(taskId);
        return build(task, requireArtifact(taskId));
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public IngestionDraftRespVO updateDraft(Long taskId, IngestionDraftUpdateReqVO reqVO) {
        IngestionTaskDO task = requireTask(taskId);
        if (!"parsed_draft".equals(task.getStatus())) throw exception(MARKETING_INGESTION_STATUS_INVALID);
        ExtractionArtifactDO artifact = requireArtifact(taskId);
        Map<String, Object> oldFields = draftFields(artifact);
        Map<String, Object> newFields = new LinkedHashMap<>(reqVO.getFields());
        int updated = taskMapper.update(null, new LambdaUpdateWrapper<IngestionTaskDO>()
                .eq(IngestionTaskDO::getId, taskId)
                .eq(IngestionTaskDO::getVersion, reqVO.getExpectedVersionId())
                .eq(IngestionTaskDO::getStatus, "parsed_draft")
                .set(IngestionTaskDO::getVersion, reqVO.getExpectedVersionId() + 1));
        if (updated == 0) throw exception(MARKETING_INGESTION_VERSION_CONFLICT);
        for (Map.Entry<String, Object> field : newFields.entrySet()) {
            Object oldValue = oldFields.get(field.getKey());
            if (!java.util.Objects.equals(oldValue, field.getValue())) {
                IngestionFieldChangeDO change = new IngestionFieldChangeDO();
                change.setTaskId(taskId);
                change.setFieldName(field.getKey());
                change.setOldValue(oldValue == null ? null : String.valueOf(oldValue));
                change.setNewValue(field.getValue() == null ? null : String.valueOf(field.getValue()));
                fieldChangeMapper.insert(change);
            }
        }
        artifact.setExtracted(JsonUtils.toJsonString(newFields));
        artifactMapper.updateById(artifact);
        task.setVersion(reqVO.getExpectedVersionId() + 1);
        return build(task, artifact);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void submitReview(Long taskId) {
        IngestionTaskDO task = requireTask(taskId);
        if (!"parsed_draft".equals(task.getStatus())) throw exception(MARKETING_INGESTION_STATUS_INVALID);
        Map<String, Object> fields = draftFields(requireArtifact(taskId));
        Object subjectKey = fields.get("subjectKey");
        if (subjectKey == null || String.valueOf(subjectKey).isBlank()) throw exception(MARKETING_INGESTION_REQUIRED_FIELD_MISSING);
        int updated = taskMapper.update(null, new LambdaUpdateWrapper<IngestionTaskDO>()
                .eq(IngestionTaskDO::getId, taskId).eq(IngestionTaskDO::getStatus, "parsed_draft")
                .set(IngestionTaskDO::getStatus, "pending_review"));
        if (updated == 0) throw exception(MARKETING_INGESTION_STATUS_INVALID);
    }

    private IngestionTaskDO requireTask(Long taskId) {
        IngestionTaskDO task = taskMapper.selectById(taskId);
        if (task == null) throw exception(MARKETING_INGESTION_NOT_FOUND);
        return task;
    }
    private ExtractionArtifactDO requireArtifact(Long taskId) {
        ExtractionArtifactDO artifact = artifactMapper.selectLatestByTaskId(taskId);
        if (artifact == null) throw exception(MARKETING_INGESTION_NOT_FOUND);
        return artifact;
    }
    private IngestionDraftRespVO build(IngestionTaskDO task, ExtractionArtifactDO artifact) {
        IngestionDraftRespVO resp = new IngestionDraftRespVO();
        resp.setTaskId(task.getId()); resp.setMethod(task.getMethod()); resp.setMarket(task.getMarket());
        resp.setChannel(task.getChannel()); resp.setStatus(task.getStatus()); resp.setVersion(task.getVersion());
        resp.setFields(draftFields(artifact));
        resp.setFieldChanges(fieldChangeMapper.selectList(new LambdaQueryWrapperX<IngestionFieldChangeDO>()
                        .eq(IngestionFieldChangeDO::getTaskId, task.getId())
                        .orderByDesc(IngestionFieldChangeDO::getId))
                .stream().map(change -> {
                    IngestionFieldChangeRespVO item = new IngestionFieldChangeRespVO();
                    item.setFieldName(change.getFieldName());
                    item.setOldValue(change.getOldValue());
                    item.setNewValue(change.getNewValue());
                    item.setCreator(change.getCreator());
                    item.setCreateTime(change.getCreateTime());
                    return item;
                }).toList());
        return resp;
    }
    private Map<String, Object> draftFields(ExtractionArtifactDO artifact) {
        Map<String, Object> extracted = JsonUtils.parseMap(artifact.getExtracted());
        if (extracted != null && !extracted.containsKey("state")) return extracted;
        Map<String, Object> input = JsonUtils.parseMap(artifact.getInputPayload());
        Object payload = input == null ? null : input.get("payload");
        return payload instanceof Map ? new LinkedHashMap<>((Map<String, Object>) payload) : new LinkedHashMap<>();
    }
}
