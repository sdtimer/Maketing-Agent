package cn.iocoder.yudao.module.marketing.service.content;

import cn.iocoder.yudao.module.marketing.dal.dataobject.content.ContentVersionDO;

public interface ContentDeliveryService {
    ContentVersionDO copyApprovedContent(Long packageId);
    String exportApprovedHtml(Long packageId);
}
