package cn.iocoder.yudao.module.marketing.enums;

import cn.iocoder.yudao.framework.common.exception.ErrorCode;

/**
 * marketing 错误码。分段见详细设计 00 §7：
 * 1-020-010 ingestion｜020 curation｜030 creation｜040 review/gate｜050 layout/export｜060 prompt｜070 localization｜080 usage/credential
 */
public interface ErrorCodeConstants {

    ErrorCode MARKETING_PING_UNAUTHORIZED = new ErrorCode(1_020_000_000, "未登录，无法探测营销身份");

    ErrorCode MARKETING_INGESTION_IDEMPOTENCY_CONFLICT = new ErrorCode(1_020_010_001,
            "幂等键已用于不同的收录请求，请更换幂等键后重试");
    ErrorCode MARKETING_INGESTION_NOT_FOUND = new ErrorCode(1_020_010_002, "收录任务不存在");
    ErrorCode MARKETING_INGESTION_VERSION_CONFLICT = new ErrorCode(1_020_010_003, "草稿已被其他人修改，请刷新后重试");
    ErrorCode MARKETING_INGESTION_STATUS_INVALID = new ErrorCode(1_020_010_004, "当前任务状态不支持该操作");
    ErrorCode MARKETING_INGESTION_REQUIRED_FIELD_MISSING = new ErrorCode(1_020_010_005, "请补齐账号唯一标识或内容唯一定位后再提交审核");
    ErrorCode MARKETING_REVIEW_SELF_FORBIDDEN = new ErrorCode(1_020_040_001, "创建人不能审核自己的收录任务");
    ErrorCode MARKETING_CURATION_BATCH_NOT_FOUND = new ErrorCode(1_020_020_001, "精选批次不存在");
    ErrorCode MARKETING_CURATION_BATCH_EMPTY = new ErrorCode(1_020_020_002, "空批次不能发布，请先加入审核通过的条目");
    ErrorCode MARKETING_CURATION_STATUS_INVALID = new ErrorCode(1_020_020_003, "当前批次状态不支持该操作");
    ErrorCode MARKETING_PRODUCT_NOT_FOUND = new ErrorCode(1_020_011_001, "产品资产不存在");
    ErrorCode MARKETING_ASSET_UNAUTHORIZED = new ErrorCode(1_020_011_002, "未授权素材不能入库，更不能用于创作");

    ErrorCode MARKETING_CONTENT_PACKAGE_NOT_FOUND = new ErrorCode(1_020_030_001, "内容包不存在");
    ErrorCode MARKETING_CONTENT_VERSION_INVALID = new ErrorCode(1_020_030_002, "正文版本无效或不属于当前内容包");
    ErrorCode MARKETING_CONTENT_REVIEW_STATUS_INVALID = new ErrorCode(1_020_040_002, "当前内容包状态不支持审核");
    ErrorCode MARKETING_CONTENT_REVIEW_SELF_FORBIDDEN = new ErrorCode(1_020_040_003, "创建人不能审核自己的正文版本");
    ErrorCode MARKETING_CONTENT_REJECT_COMMENT_REQUIRED = new ErrorCode(1_020_040_004, "打回必须填写说明");
}
