package cn.iocoder.yudao.module.marketing.dal.dataobject.ingestion;

import cn.iocoder.yudao.framework.mybatis.core.dataobject.BaseDO;
import cn.iocoder.yudao.framework.tenant.core.aop.TenantIgnore;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

/** 平台收录任务。平台公共域固定为 scopeTenantId=0，不进入租户数据面。 */
@TableName("marketing_ingestion_task")
@TenantIgnore
@Data
@EqualsAndHashCode(callSuper = true)
public class IngestionTaskDO extends BaseDO {

    @TableId(type = IdType.AUTO)
    private Long id;
    private Long scopeTenantId;
    private String action;
    private String method;
    private String market;
    private String channel;
    private String status;
    private Integer attempt;
    /** 校对草稿的乐观锁版本。 */
    private Integer version;
    private String idempotencyKey;
    private String requestFingerprint;
}
