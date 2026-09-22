package cn.iocoder.yudao.module.marketing.controller.app.catalog;

import cn.iocoder.yudao.framework.common.pojo.CommonResult;
import cn.iocoder.yudao.framework.mybatis.core.query.LambdaQueryWrapperX;
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
import java.util.Map;
import static cn.iocoder.yudao.framework.common.pojo.CommonResult.success;

/** 租户端只读精选目录；只返回已发布批次，不暴露收录草稿或审核记录。 */
@RestController @RequestMapping("/marketing/catalog")
public class AppCurationCatalogController {
 @Resource private CurationBatchMapper batchMapper; @Resource private CurationEntryMapper entryMapper;
 @GetMapping("/published") @PreAuthorize("@ss.hasPermission('marketing:tenant:catalog:query')")
 public CommonResult<List<Map<String,Object>>> published(@RequestParam String market, @RequestParam String channel) {
  List<CurationBatchDO> batches=batchMapper.selectList(new LambdaQueryWrapperX<CurationBatchDO>().eq(CurationBatchDO::getMarket,market).eq(CurationBatchDO::getChannel,channel).eq(CurationBatchDO::getStatus,"published").orderByDesc(CurationBatchDO::getPublishedAt));
  return success(batches.stream().map(b->{ List<CurationEntryDO> entries=entryMapper.selectList(new LambdaQueryWrapperX<CurationEntryDO>().eq(CurationEntryDO::getBatchId,b.getId()).orderByAsc(CurationEntryDO::getSortOrder)); return Map.<String,Object>of("batchId",b.getId(),"publishedAt",b.getPublishedAt(),"opsNote",b.getOpsNote()==null?"":b.getOpsNote(),"entries",entries.stream().map(e->Map.of("taskId",e.getTaskId(),"recommendText",e.getRecommendText()==null?"":e.getRecommendText(),"reason",e.getReason()==null?"":e.getReason())).toList(),"disclaimer","本清单为平台运营精选，不代表官方排名或实时热度"); }).toList());
 }
}
