package cn.iocoder.yudao.module.marketing.controller.admin.review.vo;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class PlatformReviewReqVO {
    @NotNull @Min(1)
    private Integer expectedVersionId;
    @Size(max = 64)
    private String reasonCode;
    @Size(max = 1000)
    private String comment;

    public void validateReject() {
        if (reasonCode == null || reasonCode.isBlank() || comment == null || comment.isBlank()) {
            throw new jakarta.validation.ValidationException("打回必须填写原因代码和说明");
        }
    }
}
