package cn.iocoder.yudao.module.marketing.controller.app.content.vo;

import lombok.Data;

@Data
public class ContentVersionSaveRespVO {
    private Long contentVersionId;
    private String contentHash;
    private String taskStatus;
    private java.util.List<String> missingFactFields;
}
