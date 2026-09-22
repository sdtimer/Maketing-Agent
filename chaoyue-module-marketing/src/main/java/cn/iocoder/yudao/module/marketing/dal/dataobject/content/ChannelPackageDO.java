package cn.iocoder.yudao.module.marketing.dal.dataobject.content;

import cn.iocoder.yudao.framework.mybatis.core.dataobject.BaseDO;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

/** 租户渠道内容包；审核状态与任务状态独立。 */
@TableName("marketing_channel_package")
@Data
@EqualsAndHashCode(callSuper = true)
public class ChannelPackageDO extends BaseDO {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long taskId;
    private String channel;
    private String status;
    private Long currentVersionId;
    private Long tenantId;
}
