package cn.iocoder.yudao.module.marketing.service.ingestion;

import cn.iocoder.yudao.module.marketing.controller.admin.ingestion.vo.IngestionImageCreateReqVO;
import cn.iocoder.yudao.module.marketing.controller.admin.ingestion.vo.IngestionManualCreateReqVO;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.module.marketing.controller.admin.ingestion.vo.IngestionTaskPageReqVO;
import cn.iocoder.yudao.module.marketing.controller.admin.ingestion.vo.IngestionTaskPageRespVO;
import cn.iocoder.yudao.module.marketing.controller.admin.ingestion.vo.IngestionTaskRespVO;
import cn.iocoder.yudao.module.marketing.controller.admin.ingestion.vo.IngestionUrlCreateReqVO;

/** 平台收录任务服务。 */
public interface IngestionTaskService {

    IngestionTaskRespVO createManual(IngestionManualCreateReqVO reqVO);

    IngestionTaskRespVO createUrl(IngestionUrlCreateReqVO reqVO);

    IngestionTaskRespVO createImage(IngestionImageCreateReqVO reqVO);

    PageResult<IngestionTaskPageRespVO> getTaskPage(IngestionTaskPageReqVO reqVO);
}
