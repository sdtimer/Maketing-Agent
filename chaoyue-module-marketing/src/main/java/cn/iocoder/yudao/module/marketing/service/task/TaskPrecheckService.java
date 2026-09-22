package cn.iocoder.yudao.module.marketing.service.task;

import cn.iocoder.yudao.module.marketing.controller.app.content.vo.ContentTaskPrecheckRespVO;
import cn.iocoder.yudao.module.marketing.dal.dataobject.content.ChannelPackageDO;
import cn.iocoder.yudao.module.marketing.dal.dataobject.content.CreationTaskDO;

import java.util.List;

public interface TaskPrecheckService {
    ContentTaskPrecheckRespVO initialCheck(CreationTaskDO task, ChannelPackageDO contentPackage, List<String> requiredFields);
    ContentTaskPrecheckRespVO recheck(CreationTaskDO task, ChannelPackageDO contentPackage, String factSnapshot, List<String> requiredFields);
    void cancel(CreationTaskDO task, ChannelPackageDO contentPackage);
}
