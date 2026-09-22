package cn.iocoder.yudao.module.marketing.controller.app.content.vo;

import lombok.Data;

import java.util.List;

@Data
public class ContentTaskPrecheckRespVO {
    private Long taskId;
    private String status;
    private List<String> missingFields;
    private String message;
}
