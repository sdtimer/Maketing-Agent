package cn.iocoder.yudao.module.marketing.dal.dataobject.content;

import cn.iocoder.yudao.framework.mybatis.core.dataobject.BaseDO;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

@TableName("marketing_content_review")
@Data
@EqualsAndHashCode(callSuper = true)
public class ContentReviewDO extends BaseDO {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long packageId;
    private Long contentVersionId;
    private String decision;
    private String comment;
    private Long reviewerId;
    private Long tenantId;
}
