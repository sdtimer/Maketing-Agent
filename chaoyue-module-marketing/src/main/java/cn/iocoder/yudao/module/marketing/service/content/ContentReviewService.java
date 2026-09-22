package cn.iocoder.yudao.module.marketing.service.content;
public interface ContentReviewService { void approve(Long packageId, Long versionId); void reject(Long packageId, Long versionId, String comment); }
