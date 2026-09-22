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

@Service
@RequiredArgsConstructor
public class ContentVersionServiceImpl implements ContentVersionService {
 private final ChannelPackageMapper packageMapper; private final ContentVersionMapper versionMapper;
 @Override @Transactional(rollbackFor=Exception.class)
 public ContentVersionDO saveNewVersion(Long packageId,String content){
  ChannelPackageDO p=packageMapper.selectById(packageId); if(p==null) throw new IllegalArgumentException("内容包不存在");
  int next=1; if(p.getCurrentVersionId()!=null){ContentVersionDO old=versionMapper.selectById(p.getCurrentVersionId());if(old!=null)next=old.getVersion()+1;}
  ContentVersionDO v=new ContentVersionDO();v.setPackageId(packageId);v.setVersion(next);v.setContent(content);v.setContentHash(hash(content));v.setTenantId(TenantContextHolder.getTenantId());versionMapper.insert(v);
  p.setCurrentVersionId(v.getId());p.setStatus("pending_review");packageMapper.updateById(p);return v;
 }
 private static String hash(String text){try{byte[] b=MessageDigest.getInstance("SHA-256").digest(text.getBytes(StandardCharsets.UTF_8));StringBuilder s=new StringBuilder();for(byte x:b)s.append(String.format("%02x",x));return s.toString();}catch(Exception e){throw new IllegalStateException(e);}}
}
