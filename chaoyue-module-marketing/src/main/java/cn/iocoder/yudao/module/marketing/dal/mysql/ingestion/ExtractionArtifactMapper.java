package cn.iocoder.yudao.module.marketing.dal.mysql.ingestion;

import cn.iocoder.yudao.framework.mybatis.core.mapper.BaseMapperX;
import cn.iocoder.yudao.module.marketing.dal.dataobject.ingestion.ExtractionArtifactDO;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface ExtractionArtifactMapper extends BaseMapperX<ExtractionArtifactDO> {

    default ExtractionArtifactDO selectLatestByTaskId(Long taskId) {
        return selectOne(new cn.iocoder.yudao.framework.mybatis.core.query.LambdaQueryWrapperX<ExtractionArtifactDO>()
                .eq(ExtractionArtifactDO::getTaskId, taskId).orderByDesc(ExtractionArtifactDO::getId).last("LIMIT 1"));
    }
}
