package cn.iocoder.yudao.module.marketing.service.gate;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ContentGateRulesTest {

    @Test
    void copyAndExportRequireApprovedHash() {
        assertTrue(ContentGateRules.canCopyOrExport("approved", "abc"));
        assertFalse(ContentGateRules.canCopyOrExport("pending_review", "abc"));
        assertFalse(ContentGateRules.canCopyOrExport("approved", ""));
        assertFalse(ContentGateRules.canCopyOrExport("approved", null));
        assertFalse(ContentGateRules.canCopyOrExport("draft", "abc"));
    }
}
