package cn.iocoder.yudao.module.marketing.service.asset;

import cn.iocoder.yudao.framework.common.util.json.JsonUtils;

import java.util.LinkedHashMap;
import java.util.Map;

/** 业务资产空值规则：空即未录入，禁止模型补写。 */
public final class AssetFacts {
    public static final String EMPTY_DISPLAY = "未录入 · 不可推测";

    private AssetFacts() {}

    public static String display(String value) {
        return value == null || value.isBlank() ? EMPTY_DISPLAY : value;
    }

    public static String normalizeJson(String facts) {
        Map<String, Object> parsed = parse(facts);
        return parsed.isEmpty() ? "{}" : JsonUtils.toJsonString(parsed);
    }

    public static Map<String, Object> parse(String facts) {
        if (facts == null || facts.isBlank()) {
            return new LinkedHashMap<>();
        }
        Map<String, Object> parsed = JsonUtils.parseMap(facts);
        return parsed == null ? new LinkedHashMap<>() : new LinkedHashMap<>(parsed);
    }

    public static Map<String, String> displayMap(String facts) {
        Map<String, String> result = new LinkedHashMap<>();
        for (Map.Entry<String, Object> entry : parse(facts).entrySet()) {
            result.put(entry.getKey(), display(entry.getValue() == null ? null : String.valueOf(entry.getValue())));
        }
        return result;
    }
}
