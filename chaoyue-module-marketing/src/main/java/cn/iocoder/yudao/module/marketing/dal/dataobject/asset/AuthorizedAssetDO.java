package cn.iocoder.yudao.module.marketing.dal.dataobject.asset;

import cn.iocoder.yudao.framework.mybatis.core.dataobject.BaseDO;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

@TableName("marketing_authorized_asset")
@Data
@EqualsAndHashCode(callSuper = true)
public class AuthorizedAssetDO extends BaseDO {
    @TableId(type = IdType.AUTO)
    private Long id;
    private String name;
    private String objectKey;
    private String source;
    private String usageScope;
    private Boolean authorized;
    private Long tenantId;
}
