package cn.iocoder.yudao.module.marketing.dal.dataobject.content;

import cn.iocoder.yudao.framework.mybatis.core.dataobject.BaseDO;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

@TableName("marketing_export_manifest")
@Data
@EqualsAndHashCode(callSuper = true)
public class ExportManifestDO extends BaseDO {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long packageId;
    private Long contentVersionId;
    private String contentHash;
    private String format;
    private Long tenantId;
}
