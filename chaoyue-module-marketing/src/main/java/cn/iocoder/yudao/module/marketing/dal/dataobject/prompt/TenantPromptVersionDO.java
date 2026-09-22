package cn.iocoder.yudao.module.marketing.dal.dataobject.prompt;
import cn.iocoder.yudao.framework.mybatis.core.dataobject.BaseDO;
import com.baomidou.mybatisplus.annotation.*;
import lombok.*;
@TableName("marketing_tenant_prompt_version") @Data @EqualsAndHashCode(callSuper=true)
public class TenantPromptVersionDO extends BaseDO { @TableId(type=IdType.AUTO) private Long id; private String promptKey; private Integer version; private String title; private String content; private String status; private Long tenantId; }
