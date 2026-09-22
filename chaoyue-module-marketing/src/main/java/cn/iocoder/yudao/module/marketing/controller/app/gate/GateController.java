package cn.iocoder.yudao.module.marketing.controller.app.gate;
import cn.iocoder.yudao.framework.common.pojo.CommonResult;
import cn.iocoder.yudao.module.marketing.controller.app.gate.vo.GateResultRespVO;
import cn.iocoder.yudao.module.marketing.service.gate.ContentGateService;
import jakarta.annotation.Resource;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import static cn.iocoder.yudao.framework.common.pojo.CommonResult.success;
/** M2 闸门占位契约：后续由内容包服务按版本、审核记录和操作者填充。 */
@RestController @RequestMapping("/marketing/gate")
public class GateController {
 @Resource private ContentGateService contentGateService;
 @GetMapping("/content-package/{id}") @PreAuthorize("@ss.hasPermission('marketing:tenant:create:query')")
 public CommonResult<GateResultRespVO> get(@PathVariable Long id){ return success(contentGateService.getGateResult(id)); }
}
