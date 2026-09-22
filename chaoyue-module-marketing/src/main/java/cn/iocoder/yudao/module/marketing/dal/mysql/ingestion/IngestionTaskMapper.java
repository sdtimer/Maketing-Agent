package cn.iocoder.yudao.module.marketing.dal.mysql.ingestion;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.mybatis.core.mapper.BaseMapperX;
import cn.iocoder.yudao.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.iocoder.yudao.module.marketing.controller.admin.ingestion.vo.IngestionTaskPageReqVO;
import cn.iocoder.yudao.module.marketing.dal.dataobject.ingestion.IngestionTaskDO;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface IngestionTaskMapper extends BaseMapperX<IngestionTaskDO> {

    default IngestionTaskDO selectByIdempotency(Long scopeTenantId, String action, String idempotencyKey) {
        return selectOne(IngestionTaskDO::getScopeTenantId, scopeTenantId,
                IngestionTaskDO::getAction, action,
                IngestionTaskDO::getIdempotencyKey, idempotencyKey);
    }

    default PageResult<IngestionTaskDO> selectPage(IngestionTaskPageReqVO reqVO) {
        return selectPage(reqVO, new LambdaQueryWrapperX<IngestionTaskDO>()
                .eqIfPresent(IngestionTaskDO::getStatus, reqVO.getStatus())
                .eqIfPresent(IngestionTaskDO::getChannel, reqVO.getChannel())
                .orderByDesc(IngestionTaskDO::getId));
    }
}
