package cn.iocoder.yudao.module.marketing.service.ingestion;

import cn.iocoder.yudao.module.marketing.controller.admin.ingestion.vo.IngestionDraftRespVO;
import cn.iocoder.yudao.module.marketing.controller.admin.ingestion.vo.IngestionDraftUpdateReqVO;

public interface IngestionDraftService {
    IngestionDraftRespVO getDraft(Long taskId);
    IngestionDraftRespVO updateDraft(Long taskId, IngestionDraftUpdateReqVO reqVO);
    void submitReview(Long taskId);
}
