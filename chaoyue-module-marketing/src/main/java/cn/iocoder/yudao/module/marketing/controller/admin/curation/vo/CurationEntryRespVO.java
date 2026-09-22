package cn.iocoder.yudao.module.marketing.controller.admin.curation.vo;

import lombok.Data;

@Data
public class CurationEntryRespVO {
    private Long id;
    private Long taskId;
    private Integer sortOrder;
    private String recommendText;
    private String reason;
}
