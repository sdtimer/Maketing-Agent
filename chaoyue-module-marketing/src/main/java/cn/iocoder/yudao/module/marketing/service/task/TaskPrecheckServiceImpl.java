package cn.iocoder.yudao.module.marketing.service.task;

import cn.iocoder.yudao.framework.common.util.json.JsonUtils;
import cn.iocoder.yudao.module.marketing.controller.app.content.vo.ContentTaskPrecheckRespVO;
import cn.iocoder.yudao.module.marketing.dal.dataobject.content.ChannelPackageDO;
import cn.iocoder.yudao.module.marketing.dal.dataobject.content.CreationTaskDO;
import cn.iocoder.yudao.module.marketing.dal.mysql.content.ChannelPackageMapper;
import cn.iocoder.yudao.module.marketing.dal.mysql.content.CreationTaskMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

import static cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil.exception;
import static cn.iocoder.yudao.module.marketing.enums.ErrorCodeConstants.MARKETING_TASK_STATUS_INVALID;

@Service
@RequiredArgsConstructor
public class TaskPrecheckServiceImpl implements TaskPrecheckService {
    private final CreationTaskMapper taskMapper;
    private final ChannelPackageMapper packageMapper;
    private final TaskStateService taskStateService;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public ContentTaskPrecheckRespVO initialCheck(CreationTaskDO task, ChannelPackageDO contentPackage, List<String> requiredFields) {
        taskStateService.recordInitial(task, "创建后进行事实预检；当前版本没有 AI 生成 worker");
        return finish(task, contentPackage, requiredFields);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public ContentTaskPrecheckRespVO recheck(CreationTaskDO task, ChannelPackageDO contentPackage,
                                             String factSnapshot, List<String> requiredFields) {
        if (!"precheck_failed".equals(task.getStatus())) {
            throw exception(MARKETING_TASK_STATUS_INVALID);
        }
        task.setFactSnapshot(factSnapshot);
        task.setRequiredFactFields(JsonUtils.toJsonString(requiredFields == null ? List.of() : requiredFields));
        taskMapper.updateById(task);
        taskStateService.transition(task, "prechecking", "用户更新事实快照后重新预检");
        return finish(task, contentPackage, requiredFields);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void cancel(CreationTaskDO task, ChannelPackageDO contentPackage) {
        taskStateService.transition(task, "cancelled", "用户取消任务；迟到异步结果必须丢弃");
        contentPackage.setStatus("cancelled");
        packageMapper.updateById(contentPackage);
    }

    private ContentTaskPrecheckRespVO finish(CreationTaskDO task, ChannelPackageDO contentPackage, List<String> requiredFields) {
        TaskPrecheckRules.Result result = TaskPrecheckRules.check(task.getFactSnapshot(), requiredFields);
        String detail = JsonUtils.toJsonString(Map.of("missingFields", result.missingFields()));
        if (result.passed()) {
            taskStateService.transition(task, "pending_review", detail);
            contentPackage.setStatus("pending_review");
        } else {
            taskStateService.transition(task, "precheck_failed", detail);
            contentPackage.setStatus("precheck_failed");
        }
        packageMapper.updateById(contentPackage);
        ContentTaskPrecheckRespVO resp = new ContentTaskPrecheckRespVO();
        resp.setTaskId(task.getId());
        resp.setStatus(task.getStatus());
        resp.setMissingFields(result.missingFields());
        resp.setMessage(result.passed() ? "事实预检通过；等待人工终审，尚未调用 AI 生成" : "事实不足，已停止在待补事实，系统不会推测或补写");
        return resp;
    }
}
