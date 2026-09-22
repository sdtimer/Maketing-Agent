package cn.iocoder.yudao.module.marketing.controller.admin.review.vo;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class PlatformReviewRecordRespVO {
    private Long id;
    private Integer targetVersionId;
    private String decision;
    private String reasonCode;
    private String comment;
    private Long reviewerId;
    private LocalDateTime createTime;
}
