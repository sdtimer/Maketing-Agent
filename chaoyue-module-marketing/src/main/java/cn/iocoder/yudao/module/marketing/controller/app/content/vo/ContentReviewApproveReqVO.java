package cn.iocoder.yudao.module.marketing.controller.app.content.vo;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class ContentReviewApproveReqVO {
    @NotNull @Min(1)
    private Long versionId;
}
