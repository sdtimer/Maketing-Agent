package cn.iocoder.yudao.module.marketing.service.curation;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class CurationRulesTest {

    @Test
    void tenantCanOnlySeePublishedBatches() {
        assertTrue(CurationRules.visibleToTenant("published"));
        assertFalse(CurationRules.visibleToTenant("draft"));
        assertFalse(CurationRules.visibleToTenant("offline"));
        assertFalse(CurationRules.visibleToTenant("pending_review"));
        assertFalse(CurationRules.visibleToTenant(null));
    }

    @Test
    void emptyOrNonDraftBatchCannotPublish() {
        assertFalse(CurationRules.canPublish("draft", 0));
        assertFalse(CurationRules.canPublish("published", 3));
        assertTrue(CurationRules.canPublish("draft", 1));
    }

    @Test
    void onlyDraftCanChangeEntries() {
        assertTrue(CurationRules.canMutateEntries("draft"));
        assertFalse(CurationRules.canMutateEntries("published"));
        assertFalse(CurationRules.canMutateEntries("offline"));
    }

    @Test
    void onlyPublishedCanOffline() {
        assertTrue(CurationRules.canOffline("published"));
        assertFalse(CurationRules.canOffline("draft"));
        assertFalse(CurationRules.canOffline("offline"));
    }
}
