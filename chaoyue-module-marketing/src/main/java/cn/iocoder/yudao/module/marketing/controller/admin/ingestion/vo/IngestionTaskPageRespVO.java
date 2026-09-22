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
    /** 当前草稿乐观锁版本；审核必须显式绑定该版本。 */
    private Integer version;
    private LocalDateTime createTime;
}
