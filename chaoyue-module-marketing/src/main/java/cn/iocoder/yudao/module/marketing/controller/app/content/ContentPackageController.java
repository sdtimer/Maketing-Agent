package cn.iocoder.yudao.module.marketing.controller.app.content;

import cn.iocoder.yudao.framework.common.pojo.CommonResult;
import cn.iocoder.yudao.module.marketing.controller.app.content.vo.ContentReviewApproveReqVO;
import cn.iocoder.yudao.module.marketing.controller.app.content.vo.ContentReviewRejectReqVO;
import cn.iocoder.yudao.module.marketing.controller.app.content.vo.ContentVersionSaveReqVO;
import cn.iocoder.yudao.module.marketing.controller.app.content.vo.ContentVersionSaveRespVO;
import cn.iocoder.yudao.module.marketing.dal.dataobject.content.ContentVersionDO;
import cn.iocoder.yudao.module.marketing.service.content.ContentReviewService;
import cn.iocoder.yudao.module.marketing.service.content.ContentVersionService;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import static cn.iocoder.yudao.framework.common.pojo.CommonResult.success;

@RestController
@RequestMapping("/marketing/content-package")
@Validated
public class ContentPackageController {
    @Resource private ContentVersionService versionService;
    @Resource private ContentReviewService reviewService;

    @PutMapping("/{id}/content")
    @PreAuthorize("@ss.hasPermission('marketing:tenant:content:update')")
    public CommonResult<ContentVersionSaveRespVO> save(@PathVariable Long id,
                                                       @Valid @RequestBody ContentVersionSaveReqVO reqVO) {
        ContentVersionDO version = versionService.saveNewVersion(id, reqVO.getContent());
        ContentVersionSaveRespVO respVO = new ContentVersionSaveRespVO();
        respVO.setContentVersionId(version.getId());
        respVO.setContentHash(version.getContentHash());
        return success(respVO);
    }

    @PostMapping("/{id}/approve")
    @PreAuthorize("@ss.hasPermission('marketing:tenant:content:review')")
    public CommonResult<Boolean> approve(@PathVariable Long id,
                                         @Valid @RequestBody ContentReviewApproveReqVO reqVO) {
        reviewService.approve(id, reqVO.getVersionId());
        return success(true);
    }

    @PostMapping("/{id}/reject")
    @PreAuthorize("@ss.hasPermission('marketing:tenant:content:review')")
    public CommonResult<Boolean> reject(@PathVariable Long id,
                                        @Valid @RequestBody ContentReviewRejectReqVO reqVO) {
        reviewService.reject(id, reqVO.getVersionId(), reqVO.getComment());
        return success(true);
    }
}
