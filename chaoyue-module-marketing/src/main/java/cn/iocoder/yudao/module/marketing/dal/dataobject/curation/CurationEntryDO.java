package cn.iocoder.yudao.module.marketing.dal.dataobject.curation;
import cn.iocoder.yudao.framework.mybatis.core.dataobject.BaseDO;
import cn.iocoder.yudao.framework.tenant.core.aop.TenantIgnore;
import com.baomidou.mybatisplus.annotation.*;
import lombok.*;
@TableName("marketing_curation_entry") @TenantIgnore @Data @EqualsAndHashCode(callSuper=true)
public class CurationEntryDO extends BaseDO {
 @TableId(type=IdType.AUTO) private Long id; private Long batchId; private Long taskId; private Integer sortOrder; private String recommendText; private String reason;
}
