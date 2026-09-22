package cn.iocoder.yudao.module.marketing.controller.admin.ingestion.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;
import org.hibernate.validator.constraints.URL;

@Schema(description = "平台端 - 指定单页解析收录请求")
@Data
public class IngestionUrlCreateReqVO {

    @NotBlank(message = "市场范围不能为空") @Size(max = 32)
    private String market;
    @NotBlank(message = "渠道不能为空") @Size(max = 32)
    private String channel;
    @NotBlank(message = "公开页面地址不能为空") @URL(message = "请输入合法的公开页面地址") @Size(max = 2048)
    private String sourceUrl;
    @NotBlank(message = "幂等键不能为空") @Size(min = 8, max = 64)
    private String idempotencyKey;
}
