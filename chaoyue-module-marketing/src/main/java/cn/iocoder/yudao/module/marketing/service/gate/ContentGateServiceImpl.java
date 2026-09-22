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
            return new GateResultRespVO(false, false, false, "内容包不存在");
        }
        if (!"approved".equals(contentPackage.getStatus())) {
            return new GateResultRespVO(false, false, false, "内容包尚未通过终审");
        }
        ContentVersionDO version = contentPackage.getCurrentVersionId() == null
                ? null : versionMapper.selectById(contentPackage.getCurrentVersionId());
        if (version == null || version.getContentHash() == null || version.getContentHash().isBlank()) {
            return new GateResultRespVO(false, false, false, "当前正文版本或内容哈希无效");
        }
        return new GateResultRespVO(false, true, true, "终审通过；复制和导出将绑定当前正文版本与哈希");
    }
}
