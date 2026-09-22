package cn.iocoder.yudao.module.marketing.controller.app.content.vo;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class ContentReviewRejectReqVO {
    @NotNull @Min(1)
    private Long versionId;
    @NotBlank(message = "打回说明不能为空")
    @Size(max = 1000)
    private String comment;
}
