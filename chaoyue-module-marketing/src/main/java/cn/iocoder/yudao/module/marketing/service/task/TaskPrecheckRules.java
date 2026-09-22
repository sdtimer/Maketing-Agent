package cn.iocoder.yudao.module.marketing.service.task;

import cn.iocoder.yudao.framework.common.util.json.JsonUtils;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;

/** 只验证用户显式声明的事实字段；不从标题、正文或模型补全推导事实。 */
public final class TaskPrecheckRules {
    public static final String INVALID_FACT_SNAPSHOT = "事实快照不是有效 JSON 对象";
    public static final String UNENTERED = "未录入 · 不可推测";

    private TaskPrecheckRules() {}

    public static Result check(String factSnapshot, List<String> requiredFields) {
        Map<String, Object> facts = JsonUtils.parseMap(factSnapshot);
        if (facts == null) {
            return new Result(false, List.of(INVALID_FACT_SNAPSHOT));
        }
        List<String> missing = new ArrayList<>();
        for (String field : new LinkedHashSet<>(requiredFields == null ? List.of() : requiredFields)) {
            if (field == null || field.isBlank()) {
                continue;
            }
            Object value = facts.get(field.trim());
            if (value == null || String.valueOf(value).isBlank() || UNENTERED.equals(String.valueOf(value).trim())) {
                missing.add(field.trim());
            }
        }
        return new Result(missing.isEmpty(), missing);
    }

    public record Result(boolean passed, List<String> missingFields) {}
}
