package cn.iocoder.yudao.module.marketing.controller.admin.ingestion.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Schema(description = "平台端 - 手工收录请求")
@Data
public class IngestionManualCreateReqVO {

    @Schema(description = "市场范围", requiredMode = Schema.RequiredMode.REQUIRED, example = "domestic")
    @NotBlank(message = "市场范围不能为空")
    @Size(max = 32)
    private String market;

    @Schema(description = "渠道", requiredMode = Schema.RequiredMode.REQUIRED, example = "wechat")
    @NotBlank(message = "渠道不能为空")
    @Size(max = 32)
    private String channel;

    @Schema(description = "记录类型", requiredMode = Schema.RequiredMode.REQUIRED, example = "account")
    @NotBlank(message = "记录类型不能为空")
    @Size(max = 32)
    private String recordType;

    @Schema(description = "账号唯一标识或内容唯一定位", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "账号唯一标识或内容唯一定位不能为空")
    @Size(max = 256)
    private String subjectKey;

    @Schema(description = "标题或账号名称")
    @Size(max = 256)
    private String title;

    @Schema(description = "手工录入正文、简介或入选理由")
    @Size(max = 20000)
    private String content;

    @Schema(description = "幂等键；同键同参返回同一任务", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "幂等键不能为空")
    @Size(min = 8, max = 64)
    private String idempotencyKey;
}
