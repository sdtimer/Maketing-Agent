package cn.iocoder.yudao.module.marketing.controller.app.asset.vo;

import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class AuthorizedAssetCreateReqVO {
    @NotBlank @Size(max = 256)
    private String name;
    @NotBlank @Size(max = 512)
    private String objectKey;
    @Size(max = 256)
    private String source;
    @Size(max = 64)
    private String usageScope;
    @AssertTrue(message = "未授权素材不能入库")
    private Boolean authorized;
}
