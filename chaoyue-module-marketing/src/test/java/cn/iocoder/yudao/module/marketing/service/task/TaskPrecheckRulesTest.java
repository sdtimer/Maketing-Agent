package cn.iocoder.yudao.module.marketing.service.task;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class TaskPrecheckRulesTest {
    @Test
    void onlyExplicitRequiredFieldsAreCheckedWithoutInference() {
        TaskPrecheckRules.Result result = TaskPrecheckRules.check("{\"moq\":\"100\",\"price\":\"\"}", List.of("moq", "price"));
        assertFalse(result.passed());
        assertEquals(List.of("price"), result.missingFields());
        assertTrue(TaskPrecheckRules.check("{}", List.of()).passed());
    }

    @Test
    void unenteredMarkerAndInvalidJsonFailTruthfully() {
        assertEquals(List.of("certification"), TaskPrecheckRules.check(
                "{\"certification\":\"未录入 · 不可推测\"}", List.of("certification")).missingFields());
        assertEquals(List.of(TaskPrecheckRules.INVALID_FACT_SNAPSHOT), TaskPrecheckRules.check("not-json", List.of()).missingFields());
    }
}
