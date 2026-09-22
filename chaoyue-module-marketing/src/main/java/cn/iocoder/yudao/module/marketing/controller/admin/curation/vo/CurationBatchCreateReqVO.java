package cn.iocoder.yudao.module.marketing.controller.admin.curation.vo;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;
@Data public class CurationBatchCreateReqVO { @NotBlank @Size(max=32) private String market; @NotBlank @Size(max=32) private String channel; @Size(max=1000) private String opsNote; }
