package cn.iocoder.yudao.module.marketing.dal.dataobject.review;

import cn.iocoder.yudao.framework.mybatis.core.dataobject.BaseDO;
import cn.iocoder.yudao.framework.tenant.core.aop.TenantIgnore;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

@TableName("marketing_platform_data_review")
@TenantIgnore
@Data
@EqualsAndHashCode(callSuper = true)
public class PlatformDataReviewDO extends BaseDO {
    @TableId(type = IdType.AUTO)
    private Long id;
    private String targetType;
    private Long targetId;
    private Integer targetVersionId;
    private String decision;
    private String reasonCode;
    private String comment;
    private Long reviewerId;
}
