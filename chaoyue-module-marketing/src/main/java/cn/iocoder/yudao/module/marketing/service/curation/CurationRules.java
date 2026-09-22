package cn.iocoder.yudao.module.marketing.service.curation;

/** M1A 精选批次状态规则。租户可见性只认 published。 */
public final class CurationRules {
    private CurationRules() {}

    public static boolean isDraft(String status) {
        return "draft".equals(status);
    }

    public static boolean isPublished(String status) {
        return "published".equals(status);
    }

    public static boolean visibleToTenant(String status) {
        return isPublished(status);
    }

    public static boolean canPublish(String status, long entryCount) {
        return isDraft(status) && entryCount > 0;
    }

    public static boolean canOffline(String status) {
        return isPublished(status);
    }

    public static boolean canMutateEntries(String status) {
        return isDraft(status);
    }
}
