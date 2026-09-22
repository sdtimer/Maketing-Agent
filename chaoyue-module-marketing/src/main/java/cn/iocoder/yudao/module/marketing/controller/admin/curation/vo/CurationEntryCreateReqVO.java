package cn.iocoder.yudao.module.marketing.controller.admin.curation.vo;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;
@Data public class CurationEntryCreateReqVO { @NotNull @Min(1) private Long taskId; @Min(0) private Integer sortOrder=0; @Size(max=1000) private String recommendText; @Size(max=1000) private String reason; }
