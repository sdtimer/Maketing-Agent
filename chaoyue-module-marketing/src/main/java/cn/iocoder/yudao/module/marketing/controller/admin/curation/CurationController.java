package cn.iocoder.yudao.module.marketing.controller.admin.curation;

import cn.iocoder.yudao.framework.common.pojo.CommonResult;
import cn.iocoder.yudao.module.marketing.dal.dataobject.curation.CurationBatchDO;
import cn.iocoder.yudao.module.marketing.dal.dataobject.curation.CurationEntryDO;
import cn.iocoder.yudao.module.marketing.dal.dataobject.ingestion.IngestionTaskDO;
import cn.iocoder.yudao.module.marketing.dal.mysql.curation.CurationBatchMapper;
import cn.iocoder.yudao.module.marketing.dal.mysql.curation.CurationEntryMapper;
import cn.iocoder.yudao.module.marketing.dal.mysql.ingestion.IngestionTaskMapper;
import jakarta.annotation.Resource;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDateTime;
import java.util.Map;
import static cn.iocoder.yudao.framework.common.pojo.CommonResult.success;
import static cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil.exception;
import static cn.iocoder.yudao.module.marketing.enums.ErrorCodeConstants.MARKETING_INGESTION_NOT_FOUND;

@RestController @RequestMapping("/marketing/curation")
public class CurationController {
 @Resource private CurationBatchMapper batchMapper; @Resource private CurationEntryMapper entryMapper; @Resource private IngestionTaskMapper taskMapper;
 @PostMapping("/batch") @PreAuthorize("@ss.hasPermission('marketing:platform:write')")
 public CommonResult<Long> create(@RequestBody Map<String,String> body) { CurationBatchDO b=new CurationBatchDO(); b.setMarket(body.get("market")); b.setChannel(body.get("channel")); b.setOpsNote(body.get("opsNote")); b.setStatus("draft"); batchMapper.insert(b); return success(b.getId()); }
 @PostMapping("/batch/{id}/entries") @PreAuthorize("@ss.hasPermission('marketing:platform:write')") @Transactional
 public CommonResult<Boolean> add(@PathVariable Long id,@RequestBody Map<String,Object> body) { IngestionTaskDO t=taskMapper.selectById(Long.valueOf(String.valueOf(body.get("taskId")))); if(t==null||!"approved".equals(t.getStatus())) throw exception(MARKETING_INGESTION_NOT_FOUND); CurationEntryDO e=new CurationEntryDO(); e.setBatchId(id); e.setTaskId(t.getId()); e.setSortOrder(Integer.parseInt(String.valueOf(body.getOrDefault("sortOrder",0)))); e.setRecommendText((String)body.get("recommendText")); e.setReason((String)body.get("reason")); entryMapper.insert(e); return success(true); }
 @PostMapping("/batch/{id}/publish") @PreAuthorize("@ss.hasPermission('marketing:platform:write')")
 public CommonResult<Boolean> publish(@PathVariable Long id) { CurationBatchDO b=batchMapper.selectById(id); if(b==null) throw exception(MARKETING_INGESTION_NOT_FOUND); b.setStatus("published"); b.setPublishedAt(LocalDateTime.now()); batchMapper.updateById(b); return success(true); }
 @PostMapping("/batch/{id}/offline") @PreAuthorize("@ss.hasPermission('marketing:platform:write')")
 public CommonResult<Boolean> offline(@PathVariable Long id) { CurationBatchDO b=batchMapper.selectById(id); if(b==null) throw exception(MARKETING_INGESTION_NOT_FOUND); b.setStatus("offline"); batchMapper.updateById(b); return success(true); }
}
