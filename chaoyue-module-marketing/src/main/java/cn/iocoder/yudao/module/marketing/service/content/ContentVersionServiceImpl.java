package cn.iocoder.yudao.module.marketing.service.content;

import cn.iocoder.yudao.framework.tenant.core.context.TenantContextHolder;
import cn.iocoder.yudao.module.marketing.dal.dataobject.content.ChannelPackageDO;
import cn.iocoder.yudao.module.marketing.dal.dataobject.content.ContentVersionDO;
import cn.iocoder.yudao.module.marketing.dal.mysql.content.ChannelPackageMapper;
import cn.iocoder.yudao.module.marketing.dal.mysql.content.ContentVersionMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;

import static cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil.exception;
import static cn.iocoder.yudao.module.marketing.enums.ErrorCodeConstants.MARKETING_CONTENT_PACKAGE_NOT_FOUND;

@Service
@RequiredArgsConstructor
public class ContentVersionServiceImpl implements ContentVersionService {
    private final ChannelPackageMapper packageMapper;
    private final ContentVersionMapper versionMapper;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public ContentVersionDO saveNewVersion(Long packageId, String content) {
        ChannelPackageDO contentPackage = packageMapper.selectById(packageId);
        if (contentPackage == null) {
            throw exception(MARKETING_CONTENT_PACKAGE_NOT_FOUND);
        }
        int nextVersion = 1;
        if (contentPackage.getCurrentVersionId() != null) {
            ContentVersionDO current = versionMapper.selectById(contentPackage.getCurrentVersionId());
            if (current != null) {
                nextVersion = current.getVersion() + 1;
            }
        }
        ContentVersionDO version = new ContentVersionDO();
        version.setPackageId(packageId);
        version.setVersion(nextVersion);
        version.setContent(content);
        version.setContentHash(hash(content));
        version.setTenantId(TenantContextHolder.getTenantId());
        versionMapper.insert(version);

        contentPackage.setCurrentVersionId(version.getId());
        contentPackage.setStatus("pending_review");
        packageMapper.updateById(contentPackage);
        return version;
    }

    private static String hash(String content) {
        try {
            byte[] bytes = MessageDigest.getInstance("SHA-256").digest(content.getBytes(StandardCharsets.UTF_8));
            StringBuilder result = new StringBuilder();
            for (byte value : bytes) {
                result.append(String.format("%02x", value));
            }
            return result.toString();
        } catch (Exception e) {
            throw new IllegalStateException("SHA-256 is unavailable", e);
        }
    }
}
