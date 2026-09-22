package cn.iocoder.yudao.module.marketing.controller.app.ping;

import cn.iocoder.yudao.framework.common.pojo.CommonResult;
import cn.iocoder.yudao.framework.security.core.util.SecurityFrameworkUtils;
import cn.iocoder.yudao.framework.tenant.core.context.TenantContextHolder;
import cn.iocoder.yudao.framework.web.core.util.WebFrameworkUtils;
import cn.iocoder.yudao.module.marketing.controller.app.ping.vo.AppMarketingPingRespVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static cn.iocoder.yudao.framework.common.pojo.CommonResult.success;

/**
 * M0 身份探测。详细设计 00 §2：/app-api/marketing/** 必须按 ADMIN 鉴权。
 */
@Tag(name = "用户端 - 营销身份探测")
@RestController
@RequestMapping("/marketing/ping")
@Validated
public class AppMarketingPingController {

    @GetMapping
    @Operation(summary = "探测当前登录身份与租户")
    public CommonResult<AppMarketingPingRespVO> ping() {
        AppMarketingPingRespVO vo = new AppMarketingPingRespVO();
        vo.setOk(true);
        vo.setUserId(SecurityFrameworkUtils.getLoginUserId());
        vo.setUserType(WebFrameworkUtils.getLoginUserType());
        vo.setTenantId(TenantContextHolder.getTenantId());
        return success(vo);
    }
}
