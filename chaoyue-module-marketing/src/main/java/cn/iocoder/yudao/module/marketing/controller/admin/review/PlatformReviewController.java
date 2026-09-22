package cn.iocoder.yudao.module.marketing.controller.admin.review;

import cn.iocoder.yudao.framework.common.pojo.CommonResult;
import cn.iocoder.yudao.module.marketing.controller.admin.review.vo.PlatformReviewReqVO;
import cn.iocoder.yudao.module.marketing.service.review.PlatformReviewService;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import static cn.iocoder.yudao.framework.common.pojo.CommonResult.success;

@RestController
@RequestMapping("/marketing/platform/review")
public class PlatformReviewController {
    @Resource private PlatformReviewService reviewService;

    @PostMapping("/{id}/approve") @PreAuthorize("@ss.hasPermission('marketing:platform:write')")
    public CommonResult<Boolean> approve(@PathVariable Long id, @Valid @RequestBody PlatformReviewReqVO reqVO) {
        reviewService.approve(id, reqVO); return success(true);
    }
    @PostMapping("/{id}/reject") @PreAuthorize("@ss.hasPermission('marketing:platform:write')")
    public CommonResult<Boolean> reject(@PathVariable Long id, @Valid @RequestBody PlatformReviewReqVO reqVO) {
        reviewService.reject(id, reqVO); return success(true);
    }
}
