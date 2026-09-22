package cn.iocoder.yudao.module.marketing.controller.app.asset.vo;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class ProductCreateReqVO {
    @NotBlank @Size(max = 256)
    private String name;
    @Size(max = 200_000)
    private String facts = "{}";
    @Size(max = 16)
    private String publicScope = "restricted";
}
