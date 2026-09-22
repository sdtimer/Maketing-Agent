package cn.iocoder.yudao.module.marketing.controller.app.catalog.vo;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class PublishedCurationBatchRespVO {
    private Long batchId;
    private LocalDateTime publishedAt;
    private String opsNote;
    private String disclaimer;
    private List<PublishedCurationEntryRespVO> entries;
}
