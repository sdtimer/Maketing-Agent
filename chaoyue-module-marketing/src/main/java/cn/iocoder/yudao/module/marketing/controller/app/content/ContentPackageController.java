package cn.iocoder.yudao.module.marketing.controller.app.content;

import cn.iocoder.yudao.framework.common.pojo.CommonResult;
import cn.iocoder.yudao.framework.tenant.core.context.TenantContextHolder;
import cn.iocoder.yudao.module.marketing.controller.app.content.vo.*;
import cn.iocoder.yudao.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.iocoder.yudao.module.marketing.dal.dataobject.content.ChannelPackageDO;
import cn.iocoder.yudao.module.marketing.dal.dataobject.content.ContentReviewDO;
import cn.iocoder.yudao.module.marketing.dal.dataobject.content.ContentVersionDO;
import cn.iocoder.yudao.module.marketing.dal.dataobject.content.CreationTaskDO;
import cn.iocoder.yudao.module.marketing.dal.mysql.content.ChannelPackageMapper;
import cn.iocoder.yudao.module.marketing.dal.mysql.content.ContentReviewMapper;
import cn.iocoder.yudao.module.marketing.dal.mysql.content.ContentVersionMapper;
import cn.iocoder.yudao.module.marketing.dal.mysql.content.CreationTaskMapper;
import cn.iocoder.yudao.module.marketing.service.content.ContentDeliveryService;
import cn.iocoder.yudao.module.marketing.service.content.ContentReviewService;
import cn.iocoder.yudao.module.marketing.service.content.ContentVersionService;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil.exception;
import static cn.iocoder.yudao.framework.common.pojo.CommonResult.success;
import static cn.iocoder.yudao.module.marketing.enums.ErrorCodeConstants.MARKETING_CONTENT_PACKAGE_NOT_FOUND;

@RestController
@RequestMapping("/marketing/content-package")
@Validated
public class ContentPackageController {
    @Resource private ContentVersionService versionService;
    @Resource private ContentReviewService reviewService;
    @Resource private ContentDeliveryService deliveryService;
    @Resource private CreationTaskMapper creationTaskMapper;
    @Resource private ChannelPackageMapper packageMapper;
    @Resource private ContentVersionMapper versionMapper;
    @Resource private ContentReviewMapper reviewMapper;

    @GetMapping("/list")
    @PreAuthorize("@ss.hasPermission('marketing:tenant:create:query')")
    public CommonResult<List<ChannelPackageDO>> list() {
        return success(packageMapper.selectList());
    }

    @GetMapping("/{id}")
    @PreAuthorize("@ss.hasPermission('marketing:tenant:create:query')")
    public CommonResult<ContentPackageDetailRespVO> get(@PathVariable Long id) {
        ChannelPackageDO contentPackage = requirePackage(id);
        ContentPackageDetailRespVO respVO = new ContentPackageDetailRespVO();
        respVO.setId(contentPackage.getId());
        respVO.setTaskId(contentPackage.getTaskId());
        respVO.setChannel(contentPackage.getChannel());
        respVO.setStatus(contentPackage.getStatus());
        respVO.setCurrentVersionId(contentPackage.getCurrentVersionId());
        if (contentPackage.getCurrentVersionId() != null) {
            ContentVersionDO version = versionMapper.selectById(contentPackage.getCurrentVersionId());
            if (version != null) {
                respVO.setCurrentVersion(version.getVersion());
                respVO.setContentHash(version.getContentHash());
                respVO.setContent(version.getContent());
            }
        }
        return success(respVO);
    }

    @PostMapping
    @PreAuthorize("@ss.hasPermission('marketing:tenant:content:update')")
    @Transactional(rollbackFor = Exception.class)
    public CommonResult<ContentVersionSaveRespVO> create(@Valid @RequestBody ContentPackageCreateReqVO reqVO) {
        CreationTaskDO task = new CreationTaskDO();
        task.setTitle(reqVO.getTitle());
        task.setMarket(reqVO.getMarket());
        task.setFactSnapshot(reqVO.getFactSnapshot());
        task.setStatus("created");
        task.setTenantId(TenantContextHolder.getTenantId());
        creationTaskMapper.insert(task);

        ChannelPackageDO contentPackage = new ChannelPackageDO();
        contentPackage.setTaskId(task.getId());
        contentPackage.setChannel(reqVO.getChannel());
        contentPackage.setStatus("draft");
        contentPackage.setTenantId(TenantContextHolder.getTenantId());
        packageMapper.insert(contentPackage);
        ContentVersionDO version = versionService.saveNewVersion(contentPackage.getId(), reqVO.getContent());
        return success(toVersionResp(version));
    }

    @PutMapping("/{id}/content")
    @PreAuthorize("@ss.hasPermission('marketing:tenant:content:update')")
    public CommonResult<ContentVersionSaveRespVO> save(@PathVariable Long id,
                                                       @Valid @RequestBody ContentVersionSaveReqVO reqVO) {
        return success(toVersionResp(versionService.saveNewVersion(id, reqVO.getContent())));
    }

    @GetMapping("/{id}/versions")
    @PreAuthorize("@ss.hasPermission('marketing:tenant:create:query')")
    public CommonResult<List<ContentVersionItemRespVO>> versions(@PathVariable Long id) {
        ChannelPackageDO contentPackage = requirePackage(id);
        return success(versionMapper.selectList(new LambdaQueryWrapperX<ContentVersionDO>()
                        .eq(ContentVersionDO::getPackageId, id)
                        .orderByDesc(ContentVersionDO::getVersion))
                .stream().map(version -> {
                    ContentVersionItemRespVO item = new ContentVersionItemRespVO();
                    item.setId(version.getId());
                    item.setVersion(version.getVersion());
                    item.setContentHash(version.getContentHash());
                    item.setCreateTime(version.getCreateTime());
                    item.setCurrent(version.getId().equals(contentPackage.getCurrentVersionId()));
                    return item;
                }).toList());
    }

    @GetMapping("/{id}/reviews")
    @PreAuthorize("@ss.hasPermission('marketing:tenant:create:query')")
    public CommonResult<List<ContentReviewRecordRespVO>> reviews(@PathVariable Long id) {
        requirePackage(id);
        return success(reviewMapper.selectList(new LambdaQueryWrapperX<ContentReviewDO>()
                        .eq(ContentReviewDO::getPackageId, id)
                        .orderByDesc(ContentReviewDO::getId))
                .stream().map(record -> {
                    ContentReviewRecordRespVO item = new ContentReviewRecordRespVO();
                    item.setId(record.getId());
                    item.setContentVersionId(record.getContentVersionId());
                    item.setDecision(record.getDecision());
                    item.setComment(record.getComment());
                    item.setReviewerId(record.getReviewerId());
                    item.setCreateTime(record.getCreateTime());
                    return item;
                }).toList());
    }

    @PostMapping("/{id}/copy")
    @PreAuthorize("@ss.hasPermission('marketing:tenant:content:copy')")
    public CommonResult<ContentCopyRespVO> copy(@PathVariable Long id) {
        ContentVersionDO version = deliveryService.copyApprovedContent(id);
        ContentCopyRespVO respVO = new ContentCopyRespVO();
        respVO.setContentVersionId(version.getId());
        respVO.setContentHash(version.getContentHash());
        respVO.setContent(version.getContent());
        return success(respVO);
    }

    @PostMapping("/{id}/export/html")
    @PreAuthorize("@ss.hasPermission('marketing:tenant:content:export')")
    public CommonResult<String> exportHtml(@PathVariable Long id) {
        return success(deliveryService.exportApprovedHtml(id));
    }

    @PostMapping("/{id}/approve")
    @PreAuthorize("@ss.hasPermission('marketing:tenant:content:review')")
    public CommonResult<Boolean> approve(@PathVariable Long id, @Valid @RequestBody ContentReviewApproveReqVO reqVO) {
        reviewService.approve(id, reqVO.getVersionId());
        return success(true);
    }

    @PostMapping("/{id}/reject")
    @PreAuthorize("@ss.hasPermission('marketing:tenant:content:review')")
    public CommonResult<Boolean> reject(@PathVariable Long id, @Valid @RequestBody ContentReviewRejectReqVO reqVO) {
        reviewService.reject(id, reqVO.getVersionId(), reqVO.getComment());
        return success(true);
    }

    private ChannelPackageDO requirePackage(Long id) {
        ChannelPackageDO contentPackage = packageMapper.selectById(id);
        if (contentPackage == null) {
            throw exception(MARKETING_CONTENT_PACKAGE_NOT_FOUND);
        }
        return contentPackage;
    }

    private static ContentVersionSaveRespVO toVersionResp(ContentVersionDO version) {
        ContentVersionSaveRespVO respVO = new ContentVersionSaveRespVO();
        respVO.setContentVersionId(version.getId());
        respVO.setContentHash(version.getContentHash());
        return respVO;
    }
}
