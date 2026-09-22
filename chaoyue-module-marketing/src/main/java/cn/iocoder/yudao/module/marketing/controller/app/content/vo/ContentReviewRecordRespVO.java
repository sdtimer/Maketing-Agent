package cn.iocoder.yudao.module.marketing.controller.app.content.vo;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ContentReviewRecordRespVO {
    private Long id;
    private Long contentVersionId;
    private String decision;
    private String comment;
    private Long reviewerId;
    private LocalDateTime createTime;
}
