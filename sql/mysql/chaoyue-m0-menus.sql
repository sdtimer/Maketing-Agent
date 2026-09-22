-- 产品菜单对齐芋道：目录(1) / 菜单(2) / 按钮(3)。可重复执行。
-- 顶级 path 以 / 开头；子路径不以 / 开头；菜单必须有 component 与唯一 component_name。

SET NAMES utf8mb4;

UPDATE system_role
SET name = '租户运营', code = 'tenant_ops', type = 2, updater = 'm0', update_time = NOW()
WHERE id = 2001;

UPDATE system_menu
SET type = 1, component = NULL, component_name = NULL, updater = 'm0', update_time = NOW()
WHERE id = 181 AND deleted = b'0';

-- 租户、用户、角色等直接挂在系统管理下，不再套「租户管理 / 审计日志」目录。
UPDATE system_menu SET parent_id = 1, path = 'tenant/list', sort = 1, updater = 'm0', update_time = NOW() WHERE id = 129 AND deleted = b'0';
UPDATE system_menu SET parent_id = 1, path = 'tenant/package', sort = 2, updater = 'm0', update_time = NOW() WHERE id = 182 AND deleted = b'0';
UPDATE system_menu SET sort = 3, updater = 'm0', update_time = NOW() WHERE id = 4 AND deleted = b'0';
UPDATE system_menu SET sort = 4, updater = 'm0', update_time = NOW() WHERE id = 5 AND deleted = b'0';
UPDATE system_menu SET sort = 5, updater = 'm0', update_time = NOW() WHERE id = 6 AND deleted = b'0';
UPDATE system_menu SET sort = 6, updater = 'm0', update_time = NOW() WHERE id = 7 AND deleted = b'0';
UPDATE system_menu SET sort = 7, updater = 'm0', update_time = NOW() WHERE id = 8 AND deleted = b'0';
UPDATE system_menu SET sort = 8, updater = 'm0', update_time = NOW() WHERE id = 9 AND deleted = b'0';
UPDATE system_menu SET parent_id = 1, path = 'log/operate-log', sort = 20, updater = 'm0', update_time = NOW() WHERE id = 21 AND deleted = b'0';
UPDATE system_menu SET parent_id = 1, path = 'log/login-log', sort = 21, updater = 'm0', update_time = NOW() WHERE id = 22 AND deleted = b'0';
UPDATE system_menu SET visible = b'0', updater = 'm0', update_time = NOW() WHERE id IN (181, 12) AND deleted = b'0';

DELETE FROM system_role_menu WHERE menu_id BETWEEN 3000 AND 3199 OR role_id IN (2001, 2002, 2003);
DELETE FROM system_menu WHERE id BETWEEN 3000 AND 3199;

INSERT INTO system_menu (
  id, name, permission, type, sort, parent_id, path, icon, component, component_name,
  status, visible, keep_alive, always_show, creator, create_time, updater, update_time, deleted
) VALUES
  (3000, '租户端', '', 1, 1, 0, '/tenant', 'ep:office-building', NULL, NULL, 0, b'0', b'1', b'1', '1', NOW(), '1', NOW(), b'0'),
  (3001, '工作台', 'marketing:tenant:home:query', 2, 1, 3000, 'home', 'ep:home-filled', 'shell/ShellPage', 'TenantHome', 0, b'1', b'1', b'1', '1', NOW(), '1', NOW(), b'0'),
  (3002, '组织与业务资产', 'marketing:tenant:assets:query', 2, 2, 3000, 'assets', 'ep:box', 'shell/TenantAssets', 'TenantAssets', 0, b'1', b'1', b'1', '1', NOW(), '1', NOW(), b'0'),
  (3003, '爆款素材库', '', 1, 3, 3000, 'catalog', 'ep:collection', NULL, NULL, 0, b'1', b'1', b'1', '1', NOW(), '1', NOW(), b'0'),
  (3004, '国内渠道', 'marketing:tenant:catalog:query', 2, 1, 3003, 'domestic', 'ep:chat-dot-round', 'shell/TenantCatalog', 'TenantDomestic', 0, b'1', b'1', b'1', '1', NOW(), '1', NOW(), b'0'),
  (3005, '出海渠道', 'marketing:tenant:catalog:query', 2, 2, 3003, 'overseas', 'ep:ship', 'shell/TenantCatalog', 'TenantOverseas', 0, b'1', b'1', b'1', '1', NOW(), '1', NOW(), b'0'),
  (3006, '我的素材', 'marketing:tenant:library:query', 2, 3, 3003, 'library', 'ep:picture', 'shell/TenantLibrary', 'TenantLibrary', 0, b'1', b'1', b'1', '1', NOW(), '1', NOW(), b'0'),
  (3007, '提示词库', 'marketing:tenant:prompt:query', 2, 4, 3000, 'prompts', 'ep:edit', 'shell/ShellPage', 'TenantPrompts', 0, b'1', b'1', b'1', '1', NOW(), '1', NOW(), b'0'),
  (3008, '智能创作', 'marketing:tenant:create:query', 2, 5, 3000, 'create', 'ep:magic-stick', 'shell/TenantContentWorkspace', 'TenantCreate', 0, b'1', b'1', b'1', '1', NOW(), '1', NOW(), b'0'),
  (3009, '内容审核', 'marketing:tenant:task:query', 2, 6, 3000, 'tasks', 'ep:document', 'shell/TenantContentReview', 'TenantTasks', 0, b'1', b'1', b'1', '1', NOW(), '1', NOW(), b'0'),
  (3010, '使用教程', 'marketing:tenant:guide:query', 2, 7, 3000, 'guide', 'ep:reading', 'shell/ShellPage', 'TenantGuide', 0, b'1', b'1', b'1', '1', NOW(), '1', NOW(), b'0'),
  (3011, '设置', 'marketing:tenant:settings:query', 2, 8, 3000, 'settings', 'ep:setting', 'shell/ShellPage', 'TenantSettings', 0, b'1', b'1', b'1', '1', NOW(), '1', NOW(), b'0'),
  (3021, '工作台查询', 'marketing:tenant:home:query', 3, 1, 3001, '', '#', NULL, NULL, 0, b'1', b'1', b'1', '1', NOW(), '1', NOW(), b'0'),
  (3022, '资产查询', 'marketing:tenant:assets:query', 3, 1, 3002, '', '#', NULL, NULL, 0, b'1', b'1', b'1', '1', NOW(), '1', NOW(), b'0'),
  (3024, '素材库查询', 'marketing:tenant:catalog:query', 3, 1, 3004, '', '#', NULL, NULL, 0, b'1', b'1', b'1', '1', NOW(), '1', NOW(), b'0'),
  (3026, '我的素材查询', 'marketing:tenant:library:query', 3, 1, 3006, '', '#', NULL, NULL, 0, b'1', b'1', b'1', '1', NOW(), '1', NOW(), b'0'),
  (3027, '提示词查询', 'marketing:tenant:prompt:query', 3, 1, 3007, '', '#', NULL, NULL, 0, b'1', b'1', b'1', '1', NOW(), '1', NOW(), b'0'),
  (3028, '创作查询', 'marketing:tenant:create:query', 3, 1, 3008, '', '#', NULL, NULL, 0, b'1', b'1', b'1', '1', NOW(), '1', NOW(), b'0'),
  (3029, '排版查询', 'marketing:tenant:task:query', 3, 1, 3009, '', '#', NULL, NULL, 0, b'1', b'1', b'1', '1', NOW(), '1', NOW(), b'0'),
  (3030, '教程查询', 'marketing:tenant:guide:query', 3, 1, 3010, '', '#', NULL, NULL, 0, b'1', b'1', b'1', '1', NOW(), '1', NOW(), b'0'),
  (3031, '设置查询', 'marketing:tenant:settings:query', 3, 1, 3011, '', '#', NULL, NULL, 0, b'1', b'1', b'1', '1', NOW(), '1', NOW(), b'0'),
  (3032, '编辑正文', 'marketing:tenant:content:update', 3, 2, 3008, '', '#', NULL, NULL, 0, b'1', b'1', b'1', '1', NOW(), '1', NOW(), b'0'),
  (3033, '内容终审', 'marketing:tenant:content:review', 3, 2, 3009, '', '#', NULL, NULL, 0, b'1', b'1', b'1', '1', NOW(), '1', NOW(), b'0'),
  (3034, '复制终审内容', 'marketing:tenant:content:copy', 3, 3, 3009, '', '#', NULL, NULL, 0, b'1', b'1', b'1', '1', NOW(), '1', NOW(), b'0'),
  (3035, '导出终审内容', 'marketing:tenant:content:export', 3, 4, 3009, '', '#', NULL, NULL, 0, b'1', b'1', b'1', '1', NOW(), '1', NOW(), b'0'),
  (3036, '维护业务资产', 'marketing:tenant:assets:update', 3, 2, 3002, '', '#', NULL, NULL, 0, b'1', b'1', b'1', '1', NOW(), '1', NOW(), b'0'),
  (3100, '平台端', '', 1, 2, 0, '/admin', 'ep:monitor', NULL, NULL, 0, b'0', b'1', b'1', '1', NOW(), '1', NOW(), b'0'),
  (3101, '平台工作台', 'marketing:admin:home:query', 2, 1, 3100, 'home', 'ep:home-filled', 'shell/ShellPage', 'AdminHome', 0, b'1', b'1', b'1', '1', NOW(), '1', NOW(), b'0'),
  (3102, '收录工作台', 'marketing:admin:ingest:query', 2, 2, 3100, 'ingest', 'ep:upload', 'shell/AdminIngest', 'AdminIngest', 0, b'1', b'1', b'1', '1', NOW(), '1', NOW(), b'0'),
  (3103, '精品对标对象', 'marketing:admin:account:query', 2, 3, 3100, 'accounts', 'ep:user', 'shell/ShellPage', 'AdminAccounts', 0, b'1', b'1', b'1', '1', NOW(), '1', NOW(), b'0'),
  (3104, '内容精选清单', 'marketing:admin:sample:query', 2, 4, 3100, 'samples', 'ep:list', 'shell/ShellPage', 'AdminSamples', 0, b'1', b'1', b'1', '1', NOW(), '1', NOW(), b'0'),
  (3105, '精选批次与排序', 'marketing:admin:batch:query', 2, 5, 3100, 'batches', 'ep:sort', 'shell/AdminCuration', 'AdminBatches', 0, b'1', b'1', b'1', '1', NOW(), '1', NOW(), b'0'),
  (3106, '数据审核与发布', 'marketing:admin:review:query', 2, 6, 3100, 'review', 'ep:circle-check', 'shell/AdminReview', 'AdminReview', 0, b'1', b'1', b'1', '1', NOW(), '1', NOW(), b'0'),
  (3107, '行业 / 主题 / 市场', 'marketing:admin:taxonomy:query', 2, 7, 3100, 'taxonomy', 'ep:collection-tag', 'shell/ShellPage', 'AdminTaxonomy', 0, b'1', b'1', b'1', '1', NOW(), '1', NOW(), b'0'),
  (3108, '公共提示词与教程', 'marketing:admin:prompt:query', 2, 8, 3100, 'prompts', 'ep:edit-pen', 'shell/ShellPage', 'AdminPrompts', 0, b'1', b'1', b'1', '1', NOW(), '1', NOW(), b'0'),
  (3121, '平台工作台查询', 'marketing:admin:home:query', 3, 1, 3101, '', '#', NULL, NULL, 0, b'1', b'1', b'1', '1', NOW(), '1', NOW(), b'0'),
  (3122, '收录查询', 'marketing:admin:ingest:query', 3, 1, 3102, '', '#', NULL, NULL, 0, b'1', b'1', b'1', '1', NOW(), '1', NOW(), b'0'),
  (3123, '对标查询', 'marketing:admin:account:query', 3, 1, 3103, '', '#', NULL, NULL, 0, b'1', b'1', b'1', '1', NOW(), '1', NOW(), b'0'),
  (3124, '样本查询', 'marketing:admin:sample:query', 3, 1, 3104, '', '#', NULL, NULL, 0, b'1', b'1', b'1', '1', NOW(), '1', NOW(), b'0'),
  (3125, '批次查询', 'marketing:admin:batch:query', 3, 1, 3105, '', '#', NULL, NULL, 0, b'1', b'1', b'1', '1', NOW(), '1', NOW(), b'0'),
  (3126, '审核查询', 'marketing:admin:review:query', 3, 1, 3106, '', '#', NULL, NULL, 0, b'1', b'1', b'1', '1', NOW(), '1', NOW(), b'0'),
  (3127, '字典查询', 'marketing:admin:taxonomy:query', 3, 1, 3107, '', '#', NULL, NULL, 0, b'1', b'1', b'1', '1', NOW(), '1', NOW(), b'0'),
  (3128, '公共提示词查询', 'marketing:admin:prompt:query', 3, 1, 3108, '', '#', NULL, NULL, 0, b'1', b'1', b'1', '1', NOW(), '1', NOW(), b'0');

INSERT INTO system_role_menu (role_id, menu_id, creator, create_time, updater, update_time, deleted, tenant_id)
SELECT 2001, id, '1', NOW(), '1', NOW(), b'0', 1001 FROM system_menu WHERE id BETWEEN 3000 AND 3036;

INSERT INTO system_role_menu (role_id, menu_id, creator, create_time, updater, update_time, deleted, tenant_id)
SELECT 2002, id, '1', NOW(), '1', NOW(), b'0', 1002 FROM system_menu WHERE id BETWEEN 3000 AND 3036;

INSERT INTO system_menu (
  id, name, permission, type, sort, parent_id, path, icon, component, component_name,
  status, visible, keep_alive, always_show, creator, create_time, updater, update_time, deleted
) VALUES (
  3129, '平台公共内容写', 'marketing:platform:write', 3, 9, 3101, '', '#', NULL, NULL,
  0, b'1', b'1', b'1', '1', NOW(), '1', NOW(), b'0'
);

INSERT INTO system_role_menu (role_id, menu_id, creator, create_time, updater, update_time, deleted, tenant_id)
SELECT 2003, id, '1', NOW(), '1', NOW(), b'0', 1 FROM system_menu WHERE id BETWEEN 3100 AND 3129;
