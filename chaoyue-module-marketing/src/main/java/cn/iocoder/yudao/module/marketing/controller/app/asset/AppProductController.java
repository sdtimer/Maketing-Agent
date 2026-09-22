package cn.iocoder.yudao.module.marketing.controller.app.asset;
import cn.iocoder.yudao.framework.common.pojo.CommonResult;
import cn.iocoder.yudao.framework.tenant.core.context.TenantContextHolder;
import cn.iocoder.yudao.module.marketing.dal.dataobject.asset.*;
import cn.iocoder.yudao.module.marketing.dal.mysql.asset.*;
import jakarta.annotation.Resource;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import java.util.*;
import static cn.iocoder.yudao.framework.common.pojo.CommonResult.success;
@RestController @RequestMapping("/marketing/catalog/product")
public class AppProductController {
 @Resource private ProductMapper productMapper; @Resource private ProductVersionMapper versionMapper;
 @GetMapping @PreAuthorize("@ss.hasPermission('marketing:tenant:assets:query')") public CommonResult<List<ProductDO>> list(){return success(productMapper.selectList());}
 @PostMapping @PreAuthorize("@ss.hasPermission('marketing:tenant:assets:query')") @Transactional public CommonResult<Long> create(@RequestBody Map<String,String> body){ProductDO p=new ProductDO();p.setName(body.get("name"));p.setStatus("active");p.setTenantId(TenantContextHolder.getTenantId());productMapper.insert(p);ProductVersionDO v=new ProductVersionDO();v.setProductId(p.getId());v.setVersion(1);v.setFacts(body.getOrDefault("facts","{}"));v.setPublicScope(body.getOrDefault("publicScope","restricted"));v.setTenantId(TenantContextHolder.getTenantId());versionMapper.insert(v);p.setCurrentVersionId(v.getId());productMapper.updateById(p);return success(p.getId());}
}
