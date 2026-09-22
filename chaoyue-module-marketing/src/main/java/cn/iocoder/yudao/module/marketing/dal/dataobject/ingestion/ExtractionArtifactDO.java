package cn.iocoder.yudao.module.marketing.dal.dataobject.ingestion;

import cn.iocoder.yudao.framework.mybatis.core.dataobject.BaseDO;
import cn.iocoder.yudao.framework.tenant.core.aop.TenantIgnore;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

/** 收录输入及提取结果快照。M1 首版仅保存受控输入，不主动抓取第三方页面。 */
@TableName("marketing_extraction_artifact")
@TenantIgnore
@Data
@EqualsAndHashCode(callSuper = true)
public class ExtractionArtifactDO extends BaseDO {

    @TableId(type = IdType.AUTO)
    private Long id;
    private Long taskId;
    private String sourceUrl;
    private String objectKey;
    private String inputPayload;
    private String extracted;
    private String confidence;
}
