package cn.iocoder.yudao.module.marketing.dal.dataobject.content;

import cn.iocoder.yudao.framework.mybatis.core.dataobject.BaseDO;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

/** 推广任务状态事件；保留预检失败的真实缺失项，不以模型推测补写。 */
@TableName("marketing_task_status_event")
@Data
@EqualsAndHashCode(callSuper = true)
public class TaskStatusEventDO extends BaseDO {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long taskId;
    private String fromStatus;
    private String toStatus;
    private String detail;
    private Long tenantId;
}
