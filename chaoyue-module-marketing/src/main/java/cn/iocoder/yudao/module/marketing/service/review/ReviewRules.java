package cn.iocoder.yudao.module.marketing.service.review;

/** 平台审核规则：创建人不得审核自己。 */
public final class ReviewRules {
    private ReviewRules() {}

    public static boolean isSelfReview(Long reviewerId, String creator) {
        return reviewerId != null && String.valueOf(reviewerId).equals(creator);
    }
}
