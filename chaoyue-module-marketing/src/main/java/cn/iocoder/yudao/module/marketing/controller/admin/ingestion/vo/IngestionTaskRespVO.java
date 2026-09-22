package cn.iocoder.yudao.module.marketing.controller.admin.ingestion.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;

@Schema(description = "平台端 - 收录任务响应")
@Data
@AllArgsConstructor
public class IngestionTaskRespVO {

    @Schema(description = "任务编号", requiredMode = Schema.RequiredMode.REQUIRED)
    private Long taskId;
    @Schema(description = "任务状态", example = "parsed_draft")
    private String status;
}
