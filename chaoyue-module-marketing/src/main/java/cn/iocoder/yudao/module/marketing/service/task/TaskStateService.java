package cn.iocoder.yudao.module.marketing.service.task;

import cn.iocoder.yudao.module.marketing.dal.dataobject.content.CreationTaskDO;

public interface TaskStateService {
    void recordInitial(CreationTaskDO task, String detail);
    void transition(CreationTaskDO task, String targetStatus, String detail);
}
