package cn.iocoder.yudao.module.marketing.dal.dataobject.curation;
import cn.iocoder.yudao.framework.mybatis.core.dataobject.BaseDO;
import cn.iocoder.yudao.framework.tenant.core.aop.TenantIgnore;
import com.baomidou.mybatisplus.annotation.*;
import lombok.*;
import java.time.LocalDateTime;
@TableName("marketing_curation_batch") @TenantIgnore @Data @EqualsAndHashCode(callSuper=true)
public class CurationBatchDO extends BaseDO {
 @TableId(type=IdType.AUTO) private Long id; private String market; private String channel; private String status; private String opsNote; private LocalDateTime publishedAt;
}
