package cn.iocoder.yudao.module.marketing.controller.admin.review;

import cn.iocoder.yudao.framework.common.pojo.CommonResult;
import cn.iocoder.yudao.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.iocoder.yudao.module.marketing.controller.admin.review.vo.PlatformReviewRecordRespVO;
import cn.iocoder.yudao.module.marketing.controller.admin.review.vo.PlatformReviewReqVO;
import cn.iocoder.yudao.module.marketing.dal.dataobject.review.PlatformDataReviewDO;
import cn.iocoder.yudao.module.marketing.dal.mysql.review.PlatformDataReviewMapper;
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
    @Resource private PlatformDataReviewMapper reviewMapper;

    @GetMapping("/{id}")
    @PreAuthorize("@ss.hasPermission('marketing:admin:review:query')")
    public CommonResult<java.util.List<PlatformReviewRecordRespVO>> list(@PathVariable Long id) {
        return success(reviewMapper.selectList(new LambdaQueryWrapperX<PlatformDataReviewDO>()
                        .eq(PlatformDataReviewDO::getTargetType, "ingestion_task")
                        .eq(PlatformDataReviewDO::getTargetId, id)
                        .orderByDesc(PlatformDataReviewDO::getId))
                .stream().map(record -> {
                    PlatformReviewRecordRespVO item = new PlatformReviewRecordRespVO();
                    item.setId(record.getId());
                    item.setTargetVersionId(record.getTargetVersionId());
                    item.setDecision(record.getDecision());
                    item.setReasonCode(record.getReasonCode());
                    item.setComment(record.getComment());
                    item.setReviewerId(record.getReviewerId());
                    item.setCreateTime(record.getCreateTime());
                    return item;
                }).toList());
    }

    @PostMapping("/{id}/approve") @PreAuthorize("@ss.hasPermission('marketing:platform:write')")
    public CommonResult<Boolean> approve(@PathVariable Long id, @Valid @RequestBody PlatformReviewReqVO reqVO) {
        reviewService.approve(id, reqVO); return success(true);
    }
    @PostMapping("/{id}/reject") @PreAuthorize("@ss.hasPermission('marketing:platform:write')")
    public CommonResult<Boolean> reject(@PathVariable Long id, @Valid @RequestBody PlatformReviewReqVO reqVO) {
        reviewService.reject(id, reqVO); return success(true);
    }
}
