package cn.iocoder.yudao.module.marketing.controller.admin.curation;

import cn.iocoder.yudao.framework.common.pojo.CommonResult;
import cn.iocoder.yudao.module.marketing.controller.admin.curation.vo.CurationBatchCreateReqVO;
import cn.iocoder.yudao.module.marketing.controller.admin.curation.vo.CurationEntryCreateReqVO;
import cn.iocoder.yudao.module.marketing.dal.dataobject.curation.CurationBatchDO;
import cn.iocoder.yudao.module.marketing.dal.dataobject.curation.CurationEntryDO;
import cn.iocoder.yudao.module.marketing.dal.dataobject.ingestion.IngestionTaskDO;
import cn.iocoder.yudao.module.marketing.dal.mysql.curation.CurationBatchMapper;
import cn.iocoder.yudao.module.marketing.dal.mysql.curation.CurationEntryMapper;
import cn.iocoder.yudao.module.marketing.dal.mysql.ingestion.IngestionTaskMapper;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

import cn.iocoder.yudao.framework.mybatis.core.query.LambdaQueryWrapperX;

import static cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil.exception;
import static cn.iocoder.yudao.framework.common.pojo.CommonResult.success;
import static cn.iocoder.yudao.module.marketing.enums.ErrorCodeConstants.*;

@RestController
@RequestMapping("/marketing/curation")
public class CurationController {
    @Resource private CurationBatchMapper batchMapper;
    @Resource private CurationEntryMapper entryMapper;
    @Resource private IngestionTaskMapper taskMapper;

    @GetMapping("/batch/list")
    @PreAuthorize("@ss.hasPermission('marketing:admin:batch:query')")
    public CommonResult<List<CurationBatchDO>> list() {
        return success(batchMapper.selectList());
    }

    @PostMapping("/batch")
    @PreAuthorize("@ss.hasPermission('marketing:platform:write')")
    public CommonResult<Long> create(@Valid @RequestBody CurationBatchCreateReqVO reqVO) {
        CurationBatchDO batch = new CurationBatchDO();
        batch.setMarket(reqVO.getMarket()); batch.setChannel(reqVO.getChannel()); batch.setOpsNote(reqVO.getOpsNote()); batch.setStatus("draft");
        batchMapper.insert(batch); return success(batch.getId());
    }

    @PostMapping("/batch/{id}/entries")
    @PreAuthorize("@ss.hasPermission('marketing:platform:write')")
    @Transactional(rollbackFor = Exception.class)
    public CommonResult<Boolean> add(@PathVariable Long id, @Valid @RequestBody CurationEntryCreateReqVO reqVO) {
        CurationBatchDO batch = requireBatch(id);
        if (!"draft".equals(batch.getStatus())) {
            throw exception(MARKETING_CURATION_STATUS_INVALID);
        }
        IngestionTaskDO task = taskMapper.selectById(reqVO.getTaskId());
        if (task == null || !"approved".equals(task.getStatus())) throw exception(MARKETING_INGESTION_NOT_FOUND);
        CurationEntryDO entry = new CurationEntryDO(); entry.setBatchId(id); entry.setTaskId(task.getId()); entry.setSortOrder(reqVO.getSortOrder()); entry.setRecommendText(reqVO.getRecommendText()); entry.setReason(reqVO.getReason()); entryMapper.insert(entry);
        return success(true);
    }

    @PostMapping("/batch/{id}/publish")
    @PreAuthorize("@ss.hasPermission('marketing:platform:write')")
    public CommonResult<Boolean> publish(@PathVariable Long id) {
        CurationBatchDO batch = requireBatch(id);
        if (!"draft".equals(batch.getStatus())) {
            throw exception(MARKETING_CURATION_STATUS_INVALID);
        }
        Long entryCount = entryMapper.selectCount(new LambdaQueryWrapperX<CurationEntryDO>()
                .eq(CurationEntryDO::getBatchId, id));
        if (entryCount == null || entryCount == 0) {
            throw exception(MARKETING_CURATION_BATCH_EMPTY);
        }
        batch.setStatus("published");
        batch.setPublishedAt(LocalDateTime.now());
        batchMapper.updateById(batch);
        return success(true);
    }

    @PostMapping("/batch/{id}/offline")
    @PreAuthorize("@ss.hasPermission('marketing:platform:write')")
    public CommonResult<Boolean> offline(@PathVariable Long id) {
        CurationBatchDO batch = requireBatch(id);
        if (!"published".equals(batch.getStatus())) {
            throw exception(MARKETING_CURATION_STATUS_INVALID);
        }
        batch.setStatus("offline");
        batchMapper.updateById(batch);
        return success(true);
    }

    private CurationBatchDO requireBatch(Long id) {
        CurationBatchDO batch = batchMapper.selectById(id);
        if (batch == null) {
            throw exception(MARKETING_CURATION_BATCH_NOT_FOUND);
        }
        return batch;
    }
}
