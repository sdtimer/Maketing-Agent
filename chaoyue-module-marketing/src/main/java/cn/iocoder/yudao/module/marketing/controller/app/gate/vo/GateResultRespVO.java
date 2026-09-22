package cn.iocoder.yudao.module.marketing.controller.app.gate.vo;
import lombok.*;
@Data @AllArgsConstructor
public class GateResultRespVO { private boolean canApprove; private boolean canCopy; private boolean canExport; private String reason; }
