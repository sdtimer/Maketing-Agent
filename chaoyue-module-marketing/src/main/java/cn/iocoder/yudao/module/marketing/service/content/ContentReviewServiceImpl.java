package cn.iocoder.yudao.module.marketing.service.content;
import cn.iocoder.yudao.framework.security.core.util.SecurityFrameworkUtils;
import cn.iocoder.yudao.framework.tenant.core.context.TenantContextHolder;
import cn.iocoder.yudao.module.marketing.dal.dataobject.content.*;
import cn.iocoder.yudao.module.marketing.dal.mysql.content.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
@Service @RequiredArgsConstructor
public class ContentReviewServiceImpl implements ContentReviewService {
 private final ChannelPackageMapper packageMapper; private final ContentVersionMapper versionMapper; private final ContentReviewMapper reviewMapper;
 @Override @Transactional(rollbackFor=Exception.class) public void approve(Long packageId,Long versionId){review(packageId,versionId,"approved",null);}
 @Override @Transactional(rollbackFor=Exception.class) public void reject(Long packageId,Long versionId,String comment){if(comment==null||comment.isBlank())throw new IllegalArgumentException("打回必须填写说明");review(packageId,versionId,"rejected",comment);}
 private void review(Long packageId,Long versionId,String decision,String comment){ChannelPackageDO p=packageMapper.selectById(packageId);ContentVersionDO v=versionMapper.selectById(versionId);if(p==null||v==null||!versionId.equals(p.getCurrentVersionId())||!"pending_review".equals(p.getStatus()))throw new IllegalStateException("内容包版本或状态不支持审核");if(String.valueOf(SecurityFrameworkUtils.getLoginUserId()).equals(v.getCreator()))throw new IllegalStateException("创建者不能审核自己的正文版本");ContentReviewDO r=new ContentReviewDO();r.setPackageId(packageId);r.setContentVersionId(versionId);r.setDecision(decision);r.setComment(comment);r.setReviewerId(SecurityFrameworkUtils.getLoginUserId());r.setTenantId(TenantContextHolder.getTenantId());reviewMapper.insert(r);p.setStatus("approved".equals(decision)?"approved":"draft");packageMapper.updateById(p);}
}
