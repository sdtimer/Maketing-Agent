package cn.iocoder.yudao.module.marketing.controller.admin.ingestion.vo;

import cn.iocoder.yudao.framework.common.pojo.PageParam;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Schema(description = "平台端 - 收录任务分页请求")
@Data
public class IngestionTaskPageReqVO extends PageParam {

    @Schema(description = "任务状态", example = "parsed_draft")
    private String status;
    @Schema(description = "渠道", example = "wechat")
    private String channel;
}
