package cn.iocoder.yudao.module.marketing.controller.app.content.vo;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ContentTaskEventRespVO {
    private Long id;
    private String fromStatus;
    private String toStatus;
    private String detail;
    private LocalDateTime createTime;
}
