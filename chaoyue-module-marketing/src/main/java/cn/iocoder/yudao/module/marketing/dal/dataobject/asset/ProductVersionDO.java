package cn.iocoder.yudao.module.marketing.dal.dataobject.asset;
import cn.iocoder.yudao.framework.mybatis.core.dataobject.BaseDO;
import com.baomidou.mybatisplus.annotation.*;
import lombok.*;
@TableName("marketing_product_version") @Data @EqualsAndHashCode(callSuper=true)
public class ProductVersionDO extends BaseDO { @TableId(type=IdType.AUTO) private Long id; private Long productId; private Integer version; private String facts; private String publicScope; private Long tenantId; }
