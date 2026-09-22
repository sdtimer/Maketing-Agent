package cn.iocoder.yudao.module.marketing.controller.app.catalog.vo;

import lombok.Data;

@Data
public class PublishedCurationEntryRespVO {
    private Long entryId;
    private Integer sortOrder;
    private String recommendText;
    private String reason;
}
