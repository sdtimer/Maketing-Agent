package cn.iocoder.yudao.module.marketing.controller.app.content.vo;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ContentVersionItemRespVO {
    private Long id;
    private Integer version;
    private String contentHash;
    private LocalDateTime createTime;
    private boolean current;
}
