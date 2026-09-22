package cn.iocoder.yudao.module.marketing.service.asset;

import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class AssetFactsTest {

    @Test
    void blankFactsStayUnenteredAndAreNotInvented() {
        assertEquals("未录入 · 不可推测", AssetFacts.display(null));
        assertEquals("未录入 · 不可推测", AssetFacts.display(""));
        assertEquals("{}", AssetFacts.normalizeJson(""));
        assertTrue(AssetFacts.displayMap("{}").isEmpty());
    }

    @Test
    void emptyFactFieldsKeepOriginalKeys() {
        Map<String, String> displayed = AssetFacts.displayMap("{\"moq\":\"\",\"leadTime\":\"7天\"}");
        assertEquals("未录入 · 不可推测", displayed.get("moq"));
        assertEquals("7天", displayed.get("leadTime"));
    }
}
