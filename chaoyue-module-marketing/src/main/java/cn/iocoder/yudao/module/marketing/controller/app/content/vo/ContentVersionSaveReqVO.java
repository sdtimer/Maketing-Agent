package cn.iocoder.yudao.module.marketing.controller.app.content.vo;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class ContentVersionSaveReqVO {
    @NotBlank(message = "正文不能为空")
    @Size(max = 200_000, message = "正文不能超过 200000 个字符")
    private String content;
}
