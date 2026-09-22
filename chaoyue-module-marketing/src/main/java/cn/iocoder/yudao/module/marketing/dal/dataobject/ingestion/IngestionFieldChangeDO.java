package cn.iocoder.yudao.module.marketing.dal.dataobject.ingestion;

import cn.iocoder.yudao.framework.mybatis.core.dataobject.BaseDO;
import cn.iocoder.yudao.framework.tenant.core.aop.TenantIgnore;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

/** 校对草稿字段变更审计。 */
@TableName("marketing_ingestion_field_change")
@TenantIgnore
@Data
@EqualsAndHashCode(callSuper = true)
public class IngestionFieldChangeDO extends BaseDO {

    @TableId(type = IdType.AUTO)
    private Long id;
    private Long taskId;
    private String fieldName;
    private String oldValue;
    private String newValue;
}
