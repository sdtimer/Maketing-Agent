package cn.iocoder.yudao.module.marketing.controller.app.catalog;

import cn.iocoder.yudao.framework.common.pojo.CommonResult;
import cn.iocoder.yudao.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.iocoder.yudao.module.marketing.controller.app.catalog.vo.PublishedCurationBatchRespVO;
import cn.iocoder.yudao.module.marketing.controller.app.catalog.vo.PublishedCurationEntryRespVO;
import cn.iocoder.yudao.module.marketing.dal.dataobject.curation.CurationBatchDO;
import cn.iocoder.yudao.module.marketing.dal.dataobject.curation.CurationEntryDO;
import cn.iocoder.yudao.module.marketing.dal.mysql.curation.CurationBatchMapper;
import cn.iocoder.yudao.module.marketing.dal.mysql.curation.CurationEntryMapper;
import jakarta.annotation.Resource;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

import static cn.iocoder.yudao.framework.common.pojo.CommonResult.success;

/** 租户端只读精选目录；只返回已发布批次，不暴露收录草稿、审核记录或内部任务 ID。 */
@RestController
@RequestMapping("/marketing/catalog")
public class AppCurationCatalogController {
    private static final String DISCLAIMER = "本清单为平台运营精选，不代表官方排名或实时热度";

    @Resource private CurationBatchMapper batchMapper;
    @Resource private CurationEntryMapper entryMapper;

    @GetMapping("/published")
    @PreAuthorize("@ss.hasPermission('marketing:tenant:catalog:query')")
    public CommonResult<List<PublishedCurationBatchRespVO>> published(@RequestParam String market,
                                                                      @RequestParam String channel) {
        List<CurationBatchDO> batches = batchMapper.selectList(new LambdaQueryWrapperX<CurationBatchDO>()
                .eq(CurationBatchDO::getMarket, market)
                .eq(CurationBatchDO::getChannel, channel)
                .eq(CurationBatchDO::getStatus, "published")
                .orderByDesc(CurationBatchDO::getPublishedAt));
        return success(batches.stream().map(this::toPublishedBatch).toList());
    }

    private PublishedCurationBatchRespVO toPublishedBatch(CurationBatchDO batch) {
        List<CurationEntryDO> entries = entryMapper.selectList(new LambdaQueryWrapperX<CurationEntryDO>()
                .eq(CurationEntryDO::getBatchId, batch.getId())
                .orderByAsc(CurationEntryDO::getSortOrder));
        PublishedCurationBatchRespVO resp = new PublishedCurationBatchRespVO();
        resp.setBatchId(batch.getId());
        resp.setPublishedAt(batch.getPublishedAt());
        resp.setOpsNote(batch.getOpsNote() == null ? "" : batch.getOpsNote());
        resp.setDisclaimer(DISCLAIMER);
        resp.setEntries(entries.stream().map(entry -> {
            PublishedCurationEntryRespVO item = new PublishedCurationEntryRespVO();
            item.setEntryId(entry.getId());
            item.setSortOrder(entry.getSortOrder());
            item.setRecommendText(entry.getRecommendText() == null ? "" : entry.getRecommendText());
            item.setReason(entry.getReason() == null ? "" : entry.getReason());
            return item;
        }).toList());
        return resp;
    }
}
