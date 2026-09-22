package cn.iocoder.yudao.module.marketing.service.content;

import cn.iocoder.yudao.module.marketing.dal.dataobject.content.ContentVersionDO;

public interface ContentVersionService {
    ContentVersionDO saveNewVersion(Long packageId, String content);
}
