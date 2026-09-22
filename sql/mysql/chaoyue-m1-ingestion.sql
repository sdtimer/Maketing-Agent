-- M1 路径 A：平台内容收录任务与输入/提取快照。
-- 平台公共域由 scope_tenant_id=0 标记；两张表均由 @TenantIgnore 管理，不能作为租户数据表直接访问。
-- 可重复执行；执行后重启 ChaoyueServerApplication。

SET NAMES utf8mb4;

CREATE TABLE IF NOT EXISTS `marketing_ingestion_task` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '任务编号',
  `scope_tenant_id` bigint NOT NULL DEFAULT 0 COMMENT '数据域：平台公共域固定 0',
  `action` varchar(64) NOT NULL COMMENT '幂等动作，如 ingestion.manual',
  `method` varchar(32) NOT NULL COMMENT 'manual / parse-url / recognize-image',
  `market` varchar(32) NOT NULL COMMENT '市场范围',
  `channel` varchar(32) NOT NULL COMMENT '渠道',
  `status` varchar(32) NOT NULL COMMENT '任务状态；M1 创建后为 parsed_draft',
  `attempt` int NOT NULL DEFAULT 0 COMMENT '异步解析尝试次数',
  `idempotency_key` varchar(64) NOT NULL COMMENT '客户端幂等键',
  `request_fingerprint` char(64) NOT NULL COMMENT '请求 SHA-256；同键异参判 409',
  `creator` varchar(64) DEFAULT '' COMMENT '创建者',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updater` varchar(64) DEFAULT '' COMMENT '更新者',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` bit(1) NOT NULL DEFAULT b'0' COMMENT '逻辑删除',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_scope_action_idempotency` (`scope_tenant_id`, `action`, `idempotency_key`),
  KEY `idx_status_create_time` (`status`, `create_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='智造营销-平台收录任务';

CREATE TABLE IF NOT EXISTS `marketing_extraction_artifact` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '快照编号',
  `task_id` bigint NOT NULL COMMENT '收录任务编号',
  `source_url` varchar(2048) DEFAULT NULL COMMENT '指定公开页面地址',
  `object_key` varchar(512) DEFAULT NULL COMMENT '上传图片对象键',
  `input_payload` mediumtext NOT NULL COMMENT '受控输入 JSON',
  `extracted` mediumtext DEFAULT NULL COMMENT '提取结果 JSON',
  `confidence` mediumtext DEFAULT NULL COMMENT '字段置信度 JSON',
  `creator` varchar(64) DEFAULT '' COMMENT '创建者',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updater` varchar(64) DEFAULT '' COMMENT '更新者',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` bit(1) NOT NULL DEFAULT b'0' COMMENT '逻辑删除',
  PRIMARY KEY (`id`),
  KEY `idx_task_id` (`task_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='智造营销-收录输入与提取快照';

-- A-04 校对草稿：版本号用于乐观锁，字段修改独立审计。
SET @has_version_column = (
  SELECT COUNT(*) FROM information_schema.COLUMNS
  WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME = 'marketing_ingestion_task' AND COLUMN_NAME = 'version'
);
SET @add_version_sql = IF(@has_version_column = 0,
  'ALTER TABLE marketing_ingestion_task ADD COLUMN `version` int NOT NULL DEFAULT 1 COMMENT \'校对草稿乐观锁版本\' AFTER `attempt`',
  'SELECT 1');
PREPARE add_version_stmt FROM @add_version_sql;
EXECUTE add_version_stmt;
DEALLOCATE PREPARE add_version_stmt;

CREATE TABLE IF NOT EXISTS `marketing_ingestion_field_change` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '变更编号',
  `task_id` bigint NOT NULL COMMENT '收录任务编号',
  `field_name` varchar(128) NOT NULL COMMENT '字段名称',
  `old_value` mediumtext DEFAULT NULL COMMENT '修改前值',
  `new_value` mediumtext DEFAULT NULL COMMENT '修改后值',
  `creator` varchar(64) DEFAULT '' COMMENT '操作人',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '操作时间',
  `updater` varchar(64) DEFAULT '' COMMENT '更新者',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` bit(1) NOT NULL DEFAULT b'0' COMMENT '逻辑删除',
  PRIMARY KEY (`id`),
  KEY `idx_task_id` (`task_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='智造营销-收录校对字段变更';

CREATE TABLE IF NOT EXISTS `marketing_platform_data_review` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `target_type` varchar(32) NOT NULL, `target_id` bigint NOT NULL, `target_version_id` int NOT NULL,
  `decision` varchar(16) NOT NULL, `reason_code` varchar(64) DEFAULT NULL, `comment` varchar(1000) DEFAULT NULL,
  `reviewer_id` bigint NOT NULL, `creator` varchar(64) DEFAULT '', `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `updater` varchar(64) DEFAULT '', `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `deleted` bit(1) NOT NULL DEFAULT b'0', PRIMARY KEY (`id`), KEY `idx_target` (`target_type`, `target_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='智造营销-平台数据审核记录';

CREATE TABLE IF NOT EXISTS `marketing_curation_batch` (
 `id` bigint NOT NULL AUTO_INCREMENT, `market` varchar(32) NOT NULL, `channel` varchar(32) NOT NULL,
 `status` varchar(16) NOT NULL DEFAULT 'draft', `ops_note` varchar(1000) DEFAULT NULL, `published_at` datetime DEFAULT NULL,
 `creator` varchar(64) DEFAULT '', `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP, `updater` varchar(64) DEFAULT '', `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP, `deleted` bit(1) NOT NULL DEFAULT b'0',
 PRIMARY KEY (`id`), KEY `idx_market_channel_status` (`market`,`channel`,`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='智造营销-精选批次';
CREATE TABLE IF NOT EXISTS `marketing_curation_entry` (
 `id` bigint NOT NULL AUTO_INCREMENT, `batch_id` bigint NOT NULL, `task_id` bigint NOT NULL, `sort_order` int NOT NULL, `recommend_text` varchar(1000) DEFAULT NULL, `reason` varchar(1000) DEFAULT NULL,
 `creator` varchar(64) DEFAULT '', `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP, `updater` varchar(64) DEFAULT '', `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP, `deleted` bit(1) NOT NULL DEFAULT b'0',
 PRIMARY KEY (`id`), UNIQUE KEY `uk_batch_task` (`batch_id`,`task_id`), KEY `idx_batch_sort` (`batch_id`,`sort_order`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='智造营销-精选批次条目';

CREATE TABLE IF NOT EXISTS `marketing_product` (`id` bigint NOT NULL AUTO_INCREMENT,`name` varchar(256) NOT NULL,`current_version_id` bigint DEFAULT NULL,`status` varchar(16) NOT NULL DEFAULT 'active',`tenant_id` bigint NOT NULL,`creator` varchar(64) DEFAULT '',`create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,`updater` varchar(64) DEFAULT '',`update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,`deleted` bit(1) NOT NULL DEFAULT b'0',PRIMARY KEY (`id`),KEY `idx_tenant_status` (`tenant_id`,`status`)) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='智造营销-租户产品资产';
CREATE TABLE IF NOT EXISTS `marketing_product_version` (`id` bigint NOT NULL AUTO_INCREMENT,`product_id` bigint NOT NULL,`version` int NOT NULL,`facts` mediumtext NOT NULL,`public_scope` varchar(16) NOT NULL DEFAULT 'restricted',`tenant_id` bigint NOT NULL,`creator` varchar(64) DEFAULT '',`create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,`updater` varchar(64) DEFAULT '',`update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,`deleted` bit(1) NOT NULL DEFAULT b'0',PRIMARY KEY (`id`),UNIQUE KEY `uk_product_version` (`product_id`,`version`),KEY `idx_tenant_product` (`tenant_id`,`product_id`)) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='智造营销-产品资产版本';
CREATE TABLE IF NOT EXISTS `marketing_authorized_asset` (`id` bigint NOT NULL AUTO_INCREMENT,`name` varchar(256) NOT NULL,`object_key` varchar(512) NOT NULL,`source` varchar(256) DEFAULT '',`usage_scope` varchar(64) DEFAULT '',`authorized` bit(1) NOT NULL DEFAULT b'0',`tenant_id` bigint NOT NULL,`creator` varchar(64) DEFAULT '',`create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,`updater` varchar(64) DEFAULT '',`update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,`deleted` bit(1) NOT NULL DEFAULT b'0',PRIMARY KEY (`id`),KEY `idx_tenant_authorized` (`tenant_id`,`authorized`)) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='智造营销-已授权素材';

-- M2：公共提示词与租户提示词必须拆表，审核版本不可覆盖。
CREATE TABLE IF NOT EXISTS `marketing_public_prompt_version` (`id` bigint NOT NULL AUTO_INCREMENT,`prompt_key` varchar(128) NOT NULL,`version` int NOT NULL,`title` varchar(256) NOT NULL,`content` mediumtext NOT NULL,`status` varchar(16) NOT NULL DEFAULT 'draft',`creator` varchar(64) DEFAULT '',`create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,`updater` varchar(64) DEFAULT '',`update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,`deleted` bit(1) NOT NULL DEFAULT b'0',PRIMARY KEY (`id`),UNIQUE KEY `uk_key_version` (`prompt_key`,`version`)) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='智造营销-公共提示词版本';
CREATE TABLE IF NOT EXISTS `marketing_tenant_prompt_version` (`id` bigint NOT NULL AUTO_INCREMENT,`prompt_key` varchar(128) NOT NULL,`version` int NOT NULL,`title` varchar(256) NOT NULL,`content` mediumtext NOT NULL,`status` varchar(16) NOT NULL DEFAULT 'draft',`tenant_id` bigint NOT NULL,`creator` varchar(64) DEFAULT '',`create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,`updater` varchar(64) DEFAULT '',`update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,`deleted` bit(1) NOT NULL DEFAULT b'0',PRIMARY KEY (`id`),UNIQUE KEY `uk_tenant_key_version` (`tenant_id`,`prompt_key`,`version`)) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='智造营销-租户提示词版本';

-- M2：推广任务、渠道内容包与不可覆盖正文版本。复制/导出必须绑定 version + content_hash。
CREATE TABLE IF NOT EXISTS `marketing_creation_task` (`id` bigint NOT NULL AUTO_INCREMENT,`title` varchar(256) NOT NULL,`status` varchar(32) NOT NULL,`market` varchar(32) NOT NULL,`fact_snapshot` mediumtext NOT NULL,`required_fact_fields` mediumtext DEFAULT NULL,`tenant_id` bigint NOT NULL,`creator` varchar(64) DEFAULT '',`create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,`updater` varchar(64) DEFAULT '',`update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,`deleted` bit(1) NOT NULL DEFAULT b'0',PRIMARY KEY (`id`),KEY `idx_tenant_status` (`tenant_id`,`status`)) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='智造营销-推广创作任务';
SET @has_required_fact_fields = (SELECT COUNT(*) FROM information_schema.COLUMNS WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME = 'marketing_creation_task' AND COLUMN_NAME = 'required_fact_fields');
SET @add_required_fact_fields_sql = IF(@has_required_fact_fields = 0, 'ALTER TABLE marketing_creation_task ADD COLUMN `required_fact_fields` mediumtext DEFAULT NULL AFTER `fact_snapshot`', 'SELECT 1');
PREPARE add_required_fact_fields_stmt FROM @add_required_fact_fields_sql;
EXECUTE add_required_fact_fields_stmt;
DEALLOCATE PREPARE add_required_fact_fields_stmt;
CREATE TABLE IF NOT EXISTS `marketing_task_status_event` (`id` bigint NOT NULL AUTO_INCREMENT,`task_id` bigint NOT NULL,`from_status` varchar(32) DEFAULT NULL,`to_status` varchar(32) NOT NULL,`detail` mediumtext DEFAULT NULL,`tenant_id` bigint NOT NULL,`creator` varchar(64) DEFAULT '',`create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,`updater` varchar(64) DEFAULT '',`update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,`deleted` bit(1) NOT NULL DEFAULT b'0',PRIMARY KEY (`id`),KEY `idx_task_id` (`task_id`,`id`)) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='智造营销-推广任务状态事件';
CREATE TABLE IF NOT EXISTS `marketing_channel_package` (`id` bigint NOT NULL AUTO_INCREMENT,`task_id` bigint NOT NULL,`channel` varchar(32) NOT NULL,`status` varchar(32) NOT NULL,`current_version_id` bigint DEFAULT NULL,`tenant_id` bigint NOT NULL,`creator` varchar(64) DEFAULT '',`create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,`updater` varchar(64) DEFAULT '',`update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,`deleted` bit(1) NOT NULL DEFAULT b'0',PRIMARY KEY (`id`),UNIQUE KEY `uk_task_channel` (`task_id`,`channel`),KEY `idx_tenant_status` (`tenant_id`,`status`)) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='智造营销-渠道内容包';
CREATE TABLE IF NOT EXISTS `marketing_content_version` (`id` bigint NOT NULL AUTO_INCREMENT,`package_id` bigint NOT NULL,`version` int NOT NULL,`content` mediumtext NOT NULL,`content_hash` char(64) NOT NULL,`tenant_id` bigint NOT NULL,`creator` varchar(64) DEFAULT '',`create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,`updater` varchar(64) DEFAULT '',`update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,`deleted` bit(1) NOT NULL DEFAULT b'0',PRIMARY KEY (`id`),UNIQUE KEY `uk_package_version` (`package_id`,`version`),KEY `idx_hash` (`content_hash`)) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='智造营销-渠道正文版本';
CREATE TABLE IF NOT EXISTS `marketing_content_review` (`id` bigint NOT NULL AUTO_INCREMENT,`package_id` bigint NOT NULL,`content_version_id` bigint NOT NULL,`decision` varchar(16) NOT NULL,`comment` varchar(1000) DEFAULT NULL,`reviewer_id` bigint NOT NULL,`tenant_id` bigint NOT NULL,`creator` varchar(64) DEFAULT '',`create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,`updater` varchar(64) DEFAULT '',`update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,`deleted` bit(1) NOT NULL DEFAULT b'0',PRIMARY KEY (`id`),KEY `idx_package` (`package_id`)) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='智造营销-内容审核记录';

CREATE TABLE IF NOT EXISTS `marketing_export_manifest` (`id` bigint NOT NULL AUTO_INCREMENT,`package_id` bigint NOT NULL,`content_version_id` bigint NOT NULL,`content_hash` char(64) NOT NULL,`format` varchar(16) NOT NULL,`tenant_id` bigint NOT NULL,`creator` varchar(64) DEFAULT '',`create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,`updater` varchar(64) DEFAULT '',`update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,`deleted` bit(1) NOT NULL DEFAULT b'0',PRIMARY KEY (`id`),KEY `idx_package_version` (`package_id`,`content_version_id`)) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='智造营销-导出清单';

-- 动态菜单改为真实收录/精选页（其余菜单仍保留 M0 空壳）。
UPDATE system_menu
SET component = 'shell/AdminIngest', component_name = 'AdminIngest', updater = 'm1', update_time = NOW()
WHERE id = 3102 AND deleted = b'0';
UPDATE system_menu SET component = 'shell/TenantCatalog', component_name = 'TenantDomestic', updater = 'm1', update_time = NOW() WHERE id = 3004 AND deleted = b'0';
UPDATE system_menu SET component = 'shell/TenantCatalog', component_name = 'TenantOverseas', updater = 'm1', update_time = NOW() WHERE id = 3005 AND deleted = b'0';
