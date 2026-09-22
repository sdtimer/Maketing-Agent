package cn.iocoder.yudao.module.marketing.service.review;

import cn.iocoder.yudao.module.marketing.controller.admin.review.vo.PlatformReviewReqVO;

public interface PlatformReviewService {
    void approve(Long taskId, PlatformReviewReqVO reqVO);
    void reject(Long taskId, PlatformReviewReqVO reqVO);
}
