package cn.iocoder.yudao.module.marketing.controller.app.content;
import cn.iocoder.yudao.framework.common.pojo.CommonResult;
import cn.iocoder.yudao.module.marketing.dal.dataobject.content.ContentVersionDO;
import cn.iocoder.yudao.module.marketing.service.content.*;
import jakarta.annotation.Resource;
import jakarta.validation.constraints.NotBlank;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import java.util.Map;
import static cn.iocoder.yudao.framework.common.pojo.CommonResult.success;
@RestController @RequestMapping("/marketing/content-package") @Validated
public class ContentPackageController {
 @Resource private ContentVersionService versionService; @Resource private ContentReviewService reviewService;
 @PutMapping("/{id}/content") @PreAuthorize("@ss.hasPermission('marketing:tenant:create:query')")
 public CommonResult<Map<String,Object>> save(@PathVariable Long id,@RequestBody Map<String,@NotBlank String> body){ContentVersionDO v=versionService.saveNewVersion(id,body.get("content"));return success(Map.of("contentVersionId",v.getId(),"contentHash",v.getContentHash()));}
 @PostMapping("/{id}/approve") @PreAuthorize("@ss.hasPermission('marketing:tenant:create:query')")
 public CommonResult<Boolean> approve(@PathVariable Long id,@RequestBody Map<String,Long> body){reviewService.approve(id,body.get("versionId"));return success(true);}
 @PostMapping("/{id}/reject") @PreAuthorize("@ss.hasPermission('marketing:tenant:create:query')")
 public CommonResult<Boolean> reject(@PathVariable Long id,@RequestBody Map<String,Object> body){reviewService.reject(id,((Number)body.get("versionId")).longValue(),String.valueOf(body.get("comment")));return success(true);}
}
