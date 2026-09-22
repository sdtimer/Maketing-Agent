package cn.iocoder.yudao.module.marketing.controller.app.ping.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Schema(description = "用户端 - 营销身份探测 Response VO")
@Data
public class AppMarketingPingRespVO {

    @Schema(description = "是否连通")
    private Boolean ok;

    @Schema(description = "登录用户编号")
    private Long userId;

    @Schema(description = "用户类型。1=MEMBER 2=ADMIN。本路径应始终为 ADMIN")
    private Integer userType;

    @Schema(description = "租户编号，来自 Token 上下文")
    private Long tenantId;
}
