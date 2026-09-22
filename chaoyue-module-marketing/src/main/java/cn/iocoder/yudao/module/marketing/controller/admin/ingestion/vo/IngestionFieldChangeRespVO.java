package cn.iocoder.yudao.module.marketing.controller.admin.ingestion.vo;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class IngestionFieldChangeRespVO {
    private String fieldName;
    private String oldValue;
    private String newValue;
    private String creator;
    private LocalDateTime createTime;
}
