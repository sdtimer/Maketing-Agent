package cn.iocoder.yudao.module.marketing.controller.app.gate.vo;

import lombok.Data;

@Data
public class GateResultRespVO {
    private boolean canApprove;
    private boolean canCopy;
    private boolean canExport;
    private String reason;
    private Long contentVersionId;
    private String contentHash;

    public GateResultRespVO() {}

    public GateResultRespVO(boolean canApprove, boolean canCopy, boolean canExport, String reason) {
        this.canApprove = canApprove;
        this.canCopy = canCopy;
        this.canExport = canExport;
        this.reason = reason;
    }
}
