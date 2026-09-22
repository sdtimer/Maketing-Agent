package cn.iocoder.yudao.module.marketing.controller.app.content.vo;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.util.List;

@Data
public class ContentTaskPrecheckReqVO {
    @NotBlank @Size(max = 200_000)
    private String factSnapshot;
    private List<@NotBlank @Size(max = 128) String> requiredFactFields;
}
