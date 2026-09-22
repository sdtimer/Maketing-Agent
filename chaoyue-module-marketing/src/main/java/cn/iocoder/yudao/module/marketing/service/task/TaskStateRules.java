package cn.iocoder.yudao.module.marketing.service.task;

import java.util.Map;
import java.util.Set;

/** 推广任务允许的真实状态迁移；取消后任何迟到异步结果都必须被拒绝。 */
public final class TaskStateRules {
    private static final Map<String, Set<String>> NEXT = Map.of(
            "queued", Set.of("running", "cancelled"),
            "running", Set.of("prechecking", "failed", "cancelled"),
            "prechecking", Set.of("precheck_failed", "pending_review", "failed", "cancelled"),
            "precheck_failed", Set.of("prechecking", "cancelled"),
            "pending_review", Set.of("partial_success", "failed", "cancelled"),
            "partial_success", Set.of("pending_review", "cancelled"),
            "failed", Set.of("queued", "cancelled"),
            "cancelled", Set.of());

    private TaskStateRules() {}

    public static boolean canTransition(String from, String to) {
        return from == null ? "prechecking".equals(to) || "queued".equals(to) : NEXT.getOrDefault(from, Set.of()).contains(to);
    }
}
