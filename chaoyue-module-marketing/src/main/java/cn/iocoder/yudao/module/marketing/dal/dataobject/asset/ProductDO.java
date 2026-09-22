package cn.iocoder.yudao.module.marketing.dal.dataobject.asset;
import cn.iocoder.yudao.framework.mybatis.core.dataobject.BaseDO;
import com.baomidou.mybatisplus.annotation.*;
import lombok.*;
/** 租户资产主记录，tenant_id 由框架自动隔离。 */
@TableName("marketing_product") @Data @EqualsAndHashCode(callSuper=true)
public class ProductDO extends BaseDO { @TableId(type=IdType.AUTO) private Long id; private String name; private Long currentVersionId; private String status; private Long tenantId; }
