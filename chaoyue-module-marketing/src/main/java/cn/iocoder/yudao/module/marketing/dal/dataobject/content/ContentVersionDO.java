package cn.iocoder.yudao.module.marketing.dal.dataobject.content;

import cn.iocoder.yudao.framework.mybatis.core.dataobject.BaseDO;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

/** 正文不可变版本；contentHash 是审核、复制、导出绑定证据。 */
@TableName("marketing_content_version")
@Data
@EqualsAndHashCode(callSuper = true)
public class ContentVersionDO extends BaseDO {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long packageId;
    private Integer version;
    private String content;
    private String contentHash;
    private Long tenantId;
}
