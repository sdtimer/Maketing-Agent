package cn.iocoder.yudao.module.marketing.service.content;

import cn.iocoder.yudao.framework.tenant.core.context.TenantContextHolder;
import cn.iocoder.yudao.module.marketing.dal.dataobject.content.*;
import cn.iocoder.yudao.module.marketing.dal.mysql.content.ChannelPackageMapper;
import cn.iocoder.yudao.module.marketing.dal.mysql.content.ContentVersionMapper;
import cn.iocoder.yudao.module.marketing.dal.mysql.content.ExportManifestMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil.exception;
import static cn.iocoder.yudao.module.marketing.enums.ErrorCodeConstants.*;

@Service
@RequiredArgsConstructor
public class ContentDeliveryServiceImpl implements ContentDeliveryService {
    private final ChannelPackageMapper packageMapper;
    private final ContentVersionMapper versionMapper;
    private final ExportManifestMapper manifestMapper;

    @Override
    public ContentVersionDO copyApprovedContent(Long packageId) {
        return getApprovedCurrentVersion(packageId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public String exportApprovedHtml(Long packageId) {
        ContentVersionDO version = getApprovedCurrentVersion(packageId);
        ExportManifestDO manifest = new ExportManifestDO();
        manifest.setPackageId(packageId);
        manifest.setContentVersionId(version.getId());
        manifest.setContentHash(version.getContentHash());
        manifest.setFormat("html");
        manifest.setTenantId(TenantContextHolder.getTenantId());
        manifestMapper.insert(manifest);
        return "<!doctype html><html lang=\"zh-CN\"><head><meta charset=\"utf-8\"><title>内容包 "
                + packageId + "</title></head><body><pre>" + escape(version.getContent()) + "</pre></body></html>";
    }

    private ContentVersionDO getApprovedCurrentVersion(Long packageId) {
        ChannelPackageDO contentPackage = packageMapper.selectById(packageId);
        if (contentPackage == null) throw exception(MARKETING_CONTENT_PACKAGE_NOT_FOUND);
        if (!"approved".equals(contentPackage.getStatus()) || contentPackage.getCurrentVersionId() == null) {
            throw exception(MARKETING_CONTENT_REVIEW_STATUS_INVALID);
        }
        ContentVersionDO version = versionMapper.selectById(contentPackage.getCurrentVersionId());
        if (version == null || version.getContentHash() == null || version.getContentHash().isBlank()) {
            throw exception(MARKETING_CONTENT_VERSION_INVALID);
        }
        return version;
    }

    private static String escape(String value) {
        return value.replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;");
    }
}
