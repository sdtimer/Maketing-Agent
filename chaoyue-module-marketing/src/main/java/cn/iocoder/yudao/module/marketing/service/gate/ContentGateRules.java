package cn.iocoder.yudao.module.marketing.service.gate;

/** 复制/导出闸门。canApprove 独立计算，不由 canCopy/canExport 反推。 */
public final class ContentGateRules {
    private ContentGateRules() {}

    public static boolean canCopyOrExport(String status, String contentHash) {
        return "approved".equals(status) && contentHash != null && !contentHash.isBlank();
    }
}
