package cn.iocoder.yudao.module.marketing.service.task;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class TaskStateRulesTest {
    @Test
    void cancelledTaskNeverAcceptsLateResult() {
        assertTrue(TaskStateRules.canTransition("prechecking", "precheck_failed"));
        assertTrue(TaskStateRules.canTransition("precheck_failed", "prechecking"));
        assertTrue(TaskStateRules.canTransition("pending_review", "partial_success"));
        assertFalse(TaskStateRules.canTransition("cancelled", "partial_success"));
        assertFalse(TaskStateRules.canTransition("cancelled", "pending_review"));
    }
}
