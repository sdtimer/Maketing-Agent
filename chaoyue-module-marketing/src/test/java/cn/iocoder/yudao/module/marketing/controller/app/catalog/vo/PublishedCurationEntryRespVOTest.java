package cn.iocoder.yudao.module.marketing.controller.app.catalog.vo;

import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class PublishedCurationEntryRespVOTest {

    @Test
    void tenantCatalogMustNotExposeInternalTaskId() {
        Set<String> fields = Arrays.stream(PublishedCurationEntryRespVO.class.getDeclaredFields())
                .map(Field::getName)
                .collect(Collectors.toSet());
        assertTrue(fields.contains("entryId"));
        assertFalse(fields.contains("taskId"));
    }
}
