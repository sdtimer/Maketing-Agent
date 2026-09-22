package cn.iocoder.yudao.module.marketing.controller.admin.ingestion.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

@Schema(description = "平台端 - 收录任务分页项")
@Data
public class IngestionTaskPageRespVO {

    private Long id;
    private String method;
    private String market;
    private String channel;
    private String status;
    private Integer attempt;
    private LocalDateTime createTime;
}
