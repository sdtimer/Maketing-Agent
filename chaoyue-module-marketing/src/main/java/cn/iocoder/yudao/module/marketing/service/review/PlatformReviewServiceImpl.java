package cn.iocoder.yudao.module.marketing.service.review;

import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import cn.iocoder.yudao.framework.security.core.util.SecurityFrameworkUtils;
import cn.iocoder.yudao.module.marketing.controller.admin.review.vo.PlatformReviewReqVO;
import cn.iocoder.yudao.module.marketing.dal.dataobject.ingestion.IngestionTaskDO;
import cn.iocoder.yudao.module.marketing.dal.dataobject.review.PlatformDataReviewDO;
import cn.iocoder.yudao.module.marketing.dal.mysql.ingestion.IngestionTaskMapper;
import cn.iocoder.yudao.module.marketing.dal.mysql.review.PlatformDataReviewMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil.exception;
import static cn.iocoder.yudao.module.marketing.enums.ErrorCodeConstants.*;

@Service
@RequiredArgsConstructor
public class PlatformReviewServiceImpl implements PlatformReviewService {
    private final IngestionTaskMapper taskMapper;
    private final PlatformDataReviewMapper reviewMapper;

    @Override @Transactional(rollbackFor = Exception.class)
    public void approve(Long taskId, PlatformReviewReqVO reqVO) { decide(taskId, reqVO, "approved"); }

    @Override @Transactional(rollbackFor = Exception.class)
    public void reject(Long taskId, PlatformReviewReqVO reqVO) {
        reqVO.validateReject();
        decide(taskId, reqVO, "rejected");
    }

    private void decide(Long taskId, PlatformReviewReqVO reqVO, String decision) {
        IngestionTaskDO task = taskMapper.selectById(taskId);
        if (task == null) throw exception(MARKETING_INGESTION_NOT_FOUND);
        Long reviewerId = SecurityFrameworkUtils.getLoginUserId();
        if (String.valueOf(reviewerId).equals(task.getCreator())) throw exception(MARKETING_REVIEW_SELF_FORBIDDEN);
        int updated = taskMapper.update(null, new LambdaUpdateWrapper<IngestionTaskDO>()
                .eq(IngestionTaskDO::getId, taskId).eq(IngestionTaskDO::getStatus, "pending_review")
                .eq(IngestionTaskDO::getVersion, reqVO.getExpectedVersionId())
                .set(IngestionTaskDO::getStatus, "approved".equals(decision) ? "approved" : "parsed_draft"));
        if (updated == 0) throw exception(MARKETING_INGESTION_VERSION_CONFLICT);
        PlatformDataReviewDO review = new PlatformDataReviewDO();
        review.setTargetType("ingestion_task"); review.setTargetId(taskId); review.setTargetVersionId(reqVO.getExpectedVersionId());
        review.setDecision(decision); review.setReasonCode(reqVO.getReasonCode()); review.setComment(reqVO.getComment()); review.setReviewerId(reviewerId);
        reviewMapper.insert(review);
    }
}
