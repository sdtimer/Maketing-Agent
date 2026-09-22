package cn.iocoder.yudao.module.marketing.controller.app.content.vo;

import lombok.Data;

@Data
public class ContentCopyRespVO {
    private Long contentVersionId;
    private String contentHash;
    private String content;
}
