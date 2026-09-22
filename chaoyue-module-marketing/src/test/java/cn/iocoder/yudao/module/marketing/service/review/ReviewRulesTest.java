package cn.iocoder.yudao.module.marketing.service.review;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ReviewRulesTest {

    @Test
    void creatorCannotReviewOwnTask() {
        assertTrue(ReviewRules.isSelfReview(2001L, "2001"));
        assertFalse(ReviewRules.isSelfReview(2002L, "2001"));
        assertFalse(ReviewRules.isSelfReview(null, "2001"));
        assertFalse(ReviewRules.isSelfReview(2001L, null));
    }
}
