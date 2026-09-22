package cn.iocoder.yudao.module.marketing.service.gate;

import cn.iocoder.yudao.module.marketing.controller.app.gate.vo.GateResultRespVO;
import cn.iocoder.yudao.module.marketing.dal.dataobject.content.ChannelPackageDO;
import cn.iocoder.yudao.module.marketing.dal.dataobject.content.ContentVersionDO;
import cn.iocoder.yudao.module.marketing.dal.mysql.content.ChannelPackageMapper;
import cn.iocoder.yudao.module.marketing.dal.mysql.content.ContentVersionMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * M2 的最低闸门：只有当前包已终审且当前正文版本存在时才允许复制或导出。
 * canApprove 独立计算，绝不由 canCopy/canExport 反推。
 */
@Service
@RequiredArgsConstructor
public class ContentGateServiceImpl implements ContentGateService {
    private final ChannelPackageMapper packageMapper;
    private final ContentVersionMapper versionMapper;

    @Override
    public GateResultRespVO getGateResult(Long packageId) {
        ChannelPackageDO contentPackage = packageMapper.selectById(packageId);
        if (contentPackage == null) {
            return denied("内容包不存在");
        }
        ContentVersionDO version = contentPackage.getCurrentVersionId() == null
                ? null : versionMapper.selectById(contentPackage.getCurrentVersionId());
        String hash = version == null ? null : version.getContentHash();
        boolean pass = ContentGateRules.canCopyOrExport(contentPackage.getStatus(), hash);
        GateResultRespVO result = new GateResultRespVO();
        result.setCanApprove(false);
        result.setCanCopy(pass);
        result.setCanExport(pass);
        result.setReason(pass ? "终审通过；复制和导出将绑定当前正文版本与哈希" : "内容包尚未通过终审或当前哈希无效");
        if (version != null) {
            result.setContentVersionId(version.getId());
            result.setContentHash(hash);
        }
        return result;
    }

    private static GateResultRespVO denied(String reason) {
        return new GateResultRespVO(false, false, false, reason);
    }
}
