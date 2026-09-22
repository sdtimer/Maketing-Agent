package cn.iocoder.yudao.module.marketing.controller.app.content.vo;

import lombok.Data;

@Data
public class ContentPackageDetailRespVO {
    private Long id;
    private Long taskId;
    private String channel;
    private String status;
    private Long currentVersionId;
    private Integer currentVersion;
    private String contentHash;
    private String content;
}
