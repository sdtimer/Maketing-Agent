package cn.iocoder.yudao.module.marketing.controller.app.asset;

import cn.iocoder.yudao.framework.common.pojo.CommonResult;
import cn.iocoder.yudao.framework.tenant.core.context.TenantContextHolder;
import cn.iocoder.yudao.module.marketing.controller.app.asset.vo.AuthorizedAssetCreateReqVO;
import cn.iocoder.yudao.module.marketing.dal.dataobject.asset.AuthorizedAssetDO;
import cn.iocoder.yudao.module.marketing.dal.mysql.asset.AuthorizedAssetMapper;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil.exception;
import static cn.iocoder.yudao.framework.common.pojo.CommonResult.success;
import static cn.iocoder.yudao.module.marketing.enums.ErrorCodeConstants.MARKETING_ASSET_UNAUTHORIZED;

@RestController
@RequestMapping("/marketing/catalog/library")
public class AppAuthorizedAssetController {
    @Resource private AuthorizedAssetMapper assetMapper;

    @GetMapping
    @PreAuthorize("@ss.hasPermission('marketing:tenant:library:query')")
    public CommonResult<List<AuthorizedAssetDO>> list() {
        return success(assetMapper.selectList());
    }

    @PostMapping
    @PreAuthorize("@ss.hasPermission('marketing:tenant:assets:update')")
    public CommonResult<Long> create(@Valid @RequestBody AuthorizedAssetCreateReqVO reqVO) {
        if (!Boolean.TRUE.equals(reqVO.getAuthorized())) {
            throw exception(MARKETING_ASSET_UNAUTHORIZED);
        }
        AuthorizedAssetDO asset = new AuthorizedAssetDO();
        asset.setName(reqVO.getName());
        asset.setObjectKey(reqVO.getObjectKey());
        asset.setSource(reqVO.getSource());
        asset.setUsageScope(reqVO.getUsageScope());
        asset.setAuthorized(true);
        asset.setTenantId(TenantContextHolder.getTenantId());
        assetMapper.insert(asset);
        return success(asset.getId());
    }
}
