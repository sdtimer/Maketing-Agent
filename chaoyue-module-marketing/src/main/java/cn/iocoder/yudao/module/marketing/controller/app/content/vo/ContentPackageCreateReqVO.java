package cn.iocoder.yudao.module.marketing.controller.app.content.vo;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class ContentPackageCreateReqVO {
    @NotBlank @Size(max = 256)
    private String title;
    @NotBlank @Size(max = 32)
    private String market;
    @NotBlank @Size(max = 32)
    private String channel;
    @NotBlank @Size(max = 200_000)
    private String content;
    @Size(max = 200_000)
    private String factSnapshot = "{}";
}
