package cn.iocoder.yudao.module.marketing.controller.app.asset;

import cn.iocoder.yudao.framework.common.pojo.CommonResult;
import cn.iocoder.yudao.framework.tenant.core.context.TenantContextHolder;
import cn.iocoder.yudao.module.marketing.controller.app.asset.vo.ProductCreateReqVO;
import cn.iocoder.yudao.module.marketing.controller.app.asset.vo.ProductDetailRespVO;
import cn.iocoder.yudao.module.marketing.dal.dataobject.asset.ProductDO;
import cn.iocoder.yudao.module.marketing.dal.dataobject.asset.ProductVersionDO;
import cn.iocoder.yudao.module.marketing.dal.mysql.asset.ProductMapper;
import cn.iocoder.yudao.module.marketing.dal.mysql.asset.ProductVersionMapper;
import cn.iocoder.yudao.module.marketing.service.asset.AssetFacts;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil.exception;
import static cn.iocoder.yudao.framework.common.pojo.CommonResult.success;
import static cn.iocoder.yudao.module.marketing.enums.ErrorCodeConstants.MARKETING_PRODUCT_NOT_FOUND;

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

    @GetMapping("/{id}")
    @PreAuthorize("@ss.hasPermission('marketing:tenant:assets:query')")
    public CommonResult<ProductDetailRespVO> get(@PathVariable Long id) {
        return success(toDetail(requireProduct(id)));
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
        saveVersion(product, 1, reqVO);
        return success(product.getId());
    }

    @PutMapping("/{id}/version")
    @PreAuthorize("@ss.hasPermission('marketing:tenant:assets:update')")
    @Transactional(rollbackFor = Exception.class)
    public CommonResult<Integer> saveVersion(@PathVariable Long id, @Valid @RequestBody ProductCreateReqVO reqVO) {
        ProductDO product = requireProduct(id);
        product.setName(reqVO.getName());
        int next = 1;
        if (product.getCurrentVersionId() != null) {
            ProductVersionDO current = versionMapper.selectById(product.getCurrentVersionId());
            if (current != null) {
                next = current.getVersion() + 1;
            }
        }
        saveVersion(product, next, reqVO);
        return success(next);
    }

    private ProductDO requireProduct(Long id) {
        ProductDO product = productMapper.selectById(id);
        if (product == null) {
            throw exception(MARKETING_PRODUCT_NOT_FOUND);
        }
        return product;
    }

    private void saveVersion(ProductDO product, int versionNo, ProductCreateReqVO reqVO) {
        ProductVersionDO version = new ProductVersionDO();
        version.setProductId(product.getId());
        version.setVersion(versionNo);
        version.setFacts(AssetFacts.normalizeJson(reqVO.getFacts()));
        version.setPublicScope(reqVO.getPublicScope() == null || reqVO.getPublicScope().isBlank() ? "restricted" : reqVO.getPublicScope());
        version.setTenantId(TenantContextHolder.getTenantId());
        versionMapper.insert(version);
        product.setCurrentVersionId(version.getId());
        productMapper.updateById(product);
    }

    private ProductDetailRespVO toDetail(ProductDO product) {
        ProductDetailRespVO resp = new ProductDetailRespVO();
        resp.setId(product.getId());
        resp.setName(product.getName());
        resp.setStatus(product.getStatus());
        resp.setCurrentVersionId(product.getCurrentVersionId());
        if (product.getCurrentVersionId() != null) {
            ProductVersionDO version = versionMapper.selectById(product.getCurrentVersionId());
            if (version != null) {
                resp.setCurrentVersion(version.getVersion());
                resp.setPublicScope(version.getPublicScope());
                resp.setFacts(version.getFacts());
                resp.setFactDisplay(AssetFacts.displayMap(version.getFacts()));
            }
        }
        if (resp.getFactDisplay() == null) {
            resp.setFactDisplay(AssetFacts.displayMap("{}"));
        }
        return resp;
    }
}
