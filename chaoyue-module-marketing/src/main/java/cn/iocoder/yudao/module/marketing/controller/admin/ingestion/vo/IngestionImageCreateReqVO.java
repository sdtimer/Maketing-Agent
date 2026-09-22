package cn.iocoder.yudao.module.marketing.controller.admin.ingestion.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Schema(description = "平台端 - 图片识别收录请求")
@Data
public class IngestionImageCreateReqVO {

    @NotBlank(message = "市场范围不能为空") @Size(max = 32)
    private String market;
    @NotBlank(message = "渠道不能为空") @Size(max = 32)
    private String channel;
    @NotBlank(message = "图片对象键不能为空") @Size(max = 512)
    private String objectKey;
    @NotBlank(message = "幂等键不能为空") @Size(min = 8, max = 64)
    private String idempotencyKey;
}
