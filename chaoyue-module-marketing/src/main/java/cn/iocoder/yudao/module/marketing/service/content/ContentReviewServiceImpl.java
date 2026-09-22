package cn.iocoder.yudao.module.marketing.service.content;

import cn.iocoder.yudao.framework.security.core.util.SecurityFrameworkUtils;
import cn.iocoder.yudao.framework.tenant.core.context.TenantContextHolder;
import cn.iocoder.yudao.module.marketing.dal.dataobject.content.ChannelPackageDO;
import cn.iocoder.yudao.module.marketing.dal.dataobject.content.ContentReviewDO;
import cn.iocoder.yudao.module.marketing.dal.dataobject.content.ContentVersionDO;
import cn.iocoder.yudao.module.marketing.dal.mysql.content.ChannelPackageMapper;
import cn.iocoder.yudao.module.marketing.dal.mysql.content.ContentReviewMapper;
import cn.iocoder.yudao.module.marketing.dal.mysql.content.ContentVersionMapper;
import cn.iocoder.yudao.module.marketing.dal.mysql.content.CreationTaskMapper;
import cn.iocoder.yudao.module.marketing.service.task.TaskStateService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil.exception;
import static cn.iocoder.yudao.module.marketing.enums.ErrorCodeConstants.*;

@Service
@RequiredArgsConstructor
public class ContentReviewServiceImpl implements ContentReviewService {
    private final ChannelPackageMapper packageMapper;
    private final ContentVersionMapper versionMapper;
    private final ContentReviewMapper reviewMapper;
    private final CreationTaskMapper taskMapper;
    private final TaskStateService taskStateService;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void approve(Long packageId, Long versionId) {
        review(packageId, versionId, "approved", null);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void reject(Long packageId, Long versionId, String comment) {
        if (comment == null || comment.isBlank()) {
            throw exception(MARKETING_CONTENT_REJECT_COMMENT_REQUIRED);
        }
        review(packageId, versionId, "rejected", comment);
    }

    private void review(Long packageId, Long versionId, String decision, String comment) {
        ChannelPackageDO contentPackage = packageMapper.selectById(packageId);
        if (contentPackage == null) {
            throw exception(MARKETING_CONTENT_PACKAGE_NOT_FOUND);
        }
        if (!"pending_review".equals(contentPackage.getStatus())) {
            throw exception(MARKETING_CONTENT_REVIEW_STATUS_INVALID);
        }
        ContentVersionDO version = versionMapper.selectById(versionId);
        if (version == null || !packageId.equals(version.getPackageId())
                || !versionId.equals(contentPackage.getCurrentVersionId())) {
            throw exception(MARKETING_CONTENT_VERSION_INVALID);
        }
        if (String.valueOf(SecurityFrameworkUtils.getLoginUserId()).equals(version.getCreator())) {
            throw exception(MARKETING_CONTENT_REVIEW_SELF_FORBIDDEN);
        }

        ContentReviewDO review = new ContentReviewDO();
        review.setPackageId(packageId);
        review.setContentVersionId(versionId);
        review.setDecision(decision);
        review.setComment(comment);
        review.setReviewerId(SecurityFrameworkUtils.getLoginUserId());
        review.setTenantId(TenantContextHolder.getTenantId());
        reviewMapper.insert(review);
        contentPackage.setStatus("approved".equals(decision) ? "approved" : "draft");
        packageMapper.updateById(contentPackage);
        // 仅在真实审核通过后记录部分成功；当前没有 AI worker，不产生伪造的生成成功事件。
        if ("approved".equals(decision)) {
            cn.iocoder.yudao.module.marketing.dal.dataobject.content.CreationTaskDO task = taskMapper.selectById(contentPackage.getTaskId());
            if (task != null && "pending_review".equals(task.getStatus())) {
                taskStateService.transition(task, "partial_success", "一个渠道正文已通过人工终审");
            }
        }
    }
}
