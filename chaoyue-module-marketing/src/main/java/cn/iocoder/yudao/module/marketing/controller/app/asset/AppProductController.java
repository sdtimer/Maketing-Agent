package cn.iocoder.yudao.module.marketing.controller.app.asset;

import cn.iocoder.yudao.framework.common.pojo.CommonResult;
import cn.iocoder.yudao.framework.tenant.core.context.TenantContextHolder;
import cn.iocoder.yudao.module.marketing.controller.app.asset.vo.ProductCreateReqVO;
import cn.iocoder.yudao.module.marketing.dal.dataobject.asset.ProductDO;
import cn.iocoder.yudao.module.marketing.dal.dataobject.asset.ProductVersionDO;
import cn.iocoder.yudao.module.marketing.dal.mysql.asset.ProductMapper;
import cn.iocoder.yudao.module.marketing.dal.mysql.asset.ProductVersionMapper;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static cn.iocoder.yudao.framework.common.pojo.CommonResult.success;

@RestController
@RequestMapping("/marketing/catalog/product")
public class AppProductController {
    @Resource private ProductMapper productMapper;
    @Resource private ProductVersionMapper versionMapper;

    @GetMapping
    @PreAuthorize("@ss.hasPermission('marketing:tenant:assets:query')")
    public CommonResult<List<ProductDO>> list() {
        return success(productMapper.selectList());
    }

    @PostMapping
    @PreAuthorize("@ss.hasPermission('marketing:tenant:assets:update')")
    @Transactional(rollbackFor = Exception.class)
    public CommonResult<Long> create(@Valid @RequestBody ProductCreateReqVO reqVO) {
        ProductDO product = new ProductDO();
        product.setName(reqVO.getName());
        product.setStatus("active");
        product.setTenantId(TenantContextHolder.getTenantId());
        productMapper.insert(product);
        ProductVersionDO version = new ProductVersionDO();
        version.setProductId(product.getId());
        version.setVersion(1);
        version.setFacts(reqVO.getFacts() == null || reqVO.getFacts().isBlank() ? "{}" : reqVO.getFacts());
        version.setPublicScope(reqVO.getPublicScope() == null || reqVO.getPublicScope().isBlank() ? "restricted" : reqVO.getPublicScope());
        version.setTenantId(TenantContextHolder.getTenantId());
        versionMapper.insert(version);
        product.setCurrentVersionId(version.getId());
        productMapper.updateById(product);
        return success(product.getId());
    }
}
