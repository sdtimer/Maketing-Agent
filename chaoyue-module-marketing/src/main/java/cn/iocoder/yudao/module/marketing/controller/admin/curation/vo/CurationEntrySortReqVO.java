package cn.iocoder.yudao.module.marketing.controller.admin.curation.vo;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class CurationEntrySortReqVO {
    @NotNull
    @Min(0)
    private Integer sortOrder;
}
