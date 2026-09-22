package cn.iocoder.yudao.module.marketing.controller.admin.platform;

import cn.iocoder.yudao.framework.common.pojo.CommonResult;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static cn.iocoder.yudao.framework.common.pojo.CommonResult.success;

/**
 * M0 平台写探针。租户 Token 打此接口必须 403。详细设计 00 §2。
 */
@Tag(name = "平台端 - 写权限探测")
@RestController
@RequestMapping("/marketing/platform")
@Validated
public class PlatformProbeController {

    @PostMapping("/probe")
    @Operation(summary = "探测当前身份能否写平台公共内容")
    @PreAuthorize("@ss.hasPermission('marketing:platform:write')")
    public CommonResult<Boolean> probe() {
        return success(true);
    }
}
