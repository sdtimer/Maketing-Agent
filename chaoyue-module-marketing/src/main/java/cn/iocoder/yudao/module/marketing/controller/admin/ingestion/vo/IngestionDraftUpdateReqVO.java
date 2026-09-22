package cn.iocoder.yudao.module.marketing.controller.admin.ingestion.vo;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;
import java.util.Map;

/** 平台端 - 校对草稿保存请求。 */
@Data
public class IngestionDraftUpdateReqVO {
    @NotNull(message = "草稿版本不能为空")
    @Min(value = 1, message = "草稿版本不合法")
    private Integer expectedVersionId;
    @NotNull(message = "草稿字段不能为空")
    @Size(max = 100, message = "单次最多修改 100 个字段")
    private Map<String, Object> fields;
}
