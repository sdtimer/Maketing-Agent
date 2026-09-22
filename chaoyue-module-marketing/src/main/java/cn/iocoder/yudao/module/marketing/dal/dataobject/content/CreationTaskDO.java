package cn.iocoder.yudao.module.marketing.dal.dataobject.content;

import cn.iocoder.yudao.framework.mybatis.core.dataobject.BaseDO;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

@TableName("marketing_creation_task")
@Data
@EqualsAndHashCode(callSuper = true)
public class CreationTaskDO extends BaseDO {
    @TableId(type = IdType.AUTO)
    private Long id;
    private String title;
    private String status;
    private String market;
    private String factSnapshot;
    private Long tenantId;
}
