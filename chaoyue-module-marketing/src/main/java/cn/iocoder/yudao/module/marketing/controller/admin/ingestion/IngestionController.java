package cn.iocoder.yudao.module.marketing.controller.admin.ingestion;

import cn.iocoder.yudao.framework.common.pojo.CommonResult;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.module.marketing.controller.admin.ingestion.vo.IngestionDraftRespVO;
import cn.iocoder.yudao.module.marketing.controller.admin.ingestion.vo.IngestionDraftUpdateReqVO;
import cn.iocoder.yudao.module.marketing.controller.admin.ingestion.vo.IngestionImageCreateReqVO;
import cn.iocoder.yudao.module.marketing.controller.admin.ingestion.vo.IngestionManualCreateReqVO;
import cn.iocoder.yudao.module.marketing.controller.admin.ingestion.vo.IngestionTaskPageReqVO;
import cn.iocoder.yudao.module.marketing.controller.admin.ingestion.vo.IngestionTaskPageRespVO;
import cn.iocoder.yudao.module.marketing.controller.admin.ingestion.vo.IngestionTaskRespVO;
import cn.iocoder.yudao.module.marketing.controller.admin.ingestion.vo.IngestionUrlCreateReqVO;
import cn.iocoder.yudao.module.marketing.service.ingestion.IngestionDraftService;
import cn.iocoder.yudao.module.marketing.service.ingestion.IngestionTaskService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static cn.iocoder.yudao.framework.common.pojo.CommonResult.success;

@Tag(name = "平台端 - 内容收录")
@RestController
@RequestMapping("/marketing/ingestion")
@Validated
public class IngestionController {

    @Resource
    private IngestionTaskService ingestionTaskService;
    @Resource
    private IngestionDraftService ingestionDraftService;

    @GetMapping("/page")
    @Operation(summary = "获取平台收录任务分页")
    @PreAuthorize("@ss.hasPermission('marketing:admin:ingest:query')")
    public CommonResult<PageResult<IngestionTaskPageRespVO>> getPage(@Valid IngestionTaskPageReqVO reqVO) {
        return success(ingestionTaskService.getTaskPage(reqVO));
    }

    @GetMapping("/{id}")
    @Operation(summary = "获取待校对草稿")
    @PreAuthorize("@ss.hasPermission('marketing:admin:ingest:query')")
    public CommonResult<IngestionDraftRespVO> getDraft(@PathVariable("id") Long id) {
        return success(ingestionDraftService.getDraft(id));
    }

    @PutMapping("/{id}/draft")
    @Operation(summary = "保存校对草稿")
    @PreAuthorize("@ss.hasPermission('marketing:platform:write')")
    public CommonResult<IngestionDraftRespVO> updateDraft(@PathVariable("id") Long id,
                                                           @Valid @RequestBody IngestionDraftUpdateReqVO reqVO) {
        return success(ingestionDraftService.updateDraft(id, reqVO));
    }

    @PostMapping("/{id}/submit-review")
    @Operation(summary = "提交收录草稿待审")
    @PreAuthorize("@ss.hasPermission('marketing:platform:write')")
    public CommonResult<Boolean> submitReview(@PathVariable("id") Long id) {
        ingestionDraftService.submitReview(id);
        return success(true);
    }

    @PostMapping("/manual")
    @Operation(summary = "手工录入为待校对草稿")
    @PreAuthorize("@ss.hasPermission('marketing:platform:write')")
    public CommonResult<IngestionTaskRespVO> createManual(@Valid @RequestBody IngestionManualCreateReqVO reqVO) {
        return success(ingestionTaskService.createManual(reqVO));
    }

    @PostMapping("/parse-url")
    @Operation(summary = "登记单个公开页面的解析任务")
    @PreAuthorize("@ss.hasPermission('marketing:platform:write')")
    public CommonResult<IngestionTaskRespVO> createUrl(@Valid @RequestBody IngestionUrlCreateReqVO reqVO) {
        return success(ingestionTaskService.createUrl(reqVO));
    }

    @PostMapping("/recognize-image")
    @Operation(summary = "登记图片识别任务")
    @PreAuthorize("@ss.hasPermission('marketing:platform:write')")
    public CommonResult<IngestionTaskRespVO> createImage(@Valid @RequestBody IngestionImageCreateReqVO reqVO) {
        return success(ingestionTaskService.createImage(reqVO));
    }
}
