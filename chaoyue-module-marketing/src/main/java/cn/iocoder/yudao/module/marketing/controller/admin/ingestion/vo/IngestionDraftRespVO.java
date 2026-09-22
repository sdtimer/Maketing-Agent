package cn.iocoder.yudao.module.marketing.controller.admin.ingestion.vo;

import lombok.Data;

import java.util.List;
import java.util.Map;

/** 平台端 - 待校对草稿详情。 */
@Data
public class IngestionDraftRespVO {
    private Long taskId;
    private String method;
    private String market;
    private String channel;
    private String status;
    private Integer version;
    private Map<String, Object> fields;
    private List<IngestionFieldChangeRespVO> fieldChanges;
}
