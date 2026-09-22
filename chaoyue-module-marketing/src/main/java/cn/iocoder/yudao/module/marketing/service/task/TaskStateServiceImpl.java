package cn.iocoder.yudao.module.marketing.service.task;

import cn.iocoder.yudao.framework.tenant.core.context.TenantContextHolder;
import cn.iocoder.yudao.module.marketing.dal.dataobject.content.CreationTaskDO;
import cn.iocoder.yudao.module.marketing.dal.dataobject.content.TaskStatusEventDO;
import cn.iocoder.yudao.module.marketing.dal.mysql.content.CreationTaskMapper;
import cn.iocoder.yudao.module.marketing.dal.mysql.content.TaskStatusEventMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import static cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil.exception;
import static cn.iocoder.yudao.module.marketing.enums.ErrorCodeConstants.MARKETING_TASK_STATUS_INVALID;

@Service
@RequiredArgsConstructor
public class TaskStateServiceImpl implements TaskStateService {
    private final CreationTaskMapper taskMapper;
    private final TaskStatusEventMapper eventMapper;

    @Override
    public void recordInitial(CreationTaskDO task, String detail) {
        TaskStatusEventDO event = new TaskStatusEventDO();
        event.setTaskId(task.getId());
        event.setToStatus(task.getStatus());
        event.setDetail(detail);
        event.setTenantId(TenantContextHolder.getTenantId());
        eventMapper.insert(event);
    }

    @Override
    public void transition(CreationTaskDO task, String targetStatus, String detail) {
        if (!TaskStateRules.canTransition(task.getStatus(), targetStatus)) {
            throw exception(MARKETING_TASK_STATUS_INVALID);
        }
        TaskStatusEventDO event = new TaskStatusEventDO();
        event.setTaskId(task.getId());
        event.setFromStatus(task.getStatus());
        event.setToStatus(targetStatus);
        event.setDetail(detail);
        event.setTenantId(TenantContextHolder.getTenantId());
        eventMapper.insert(event);
        task.setStatus(targetStatus);
        taskMapper.updateById(task);
    }
}
