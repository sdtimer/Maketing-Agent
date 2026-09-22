-- M0 演示租户 + 四账号。密码均为 admin123（与库内 admin 同哈希）。
-- 可重复执行。

SET NAMES utf8mb4;

DELETE FROM system_user_role WHERE user_id IN (2001, 2002, 2003, 2004);
DELETE FROM system_users WHERE id IN (2001, 2002, 2003, 2004);
DELETE FROM system_role_menu WHERE role_id IN (2001, 2002, 2003);
DELETE FROM system_role WHERE id IN (2001, 2002, 2003);
DELETE FROM system_tenant WHERE id IN (1001, 1002);

INSERT INTO system_tenant (
  id, name, contact_user_id, contact_name, contact_mobile, status, websites,
  package_id, expire_time, account_count, creator, create_time, updater, update_time, deleted
) VALUES
  (1001, '超悦内部', NULL, '超悦', '', 0, '', 0, '2099-12-31 23:59:59', 99, '1', NOW(), '1', NOW(), b'0'),
  (1002, '演示租户B', NULL, '演示B', '', 0, '', 0, '2099-12-31 23:59:59', 99, '1', NOW(), '1', NOW(), b'0');

INSERT INTO system_role (
  id, name, code, sort, data_scope, data_scope_dept_ids, status, type, remark,
  creator, create_time, updater, update_time, deleted, tenant_id
) VALUES
  (2001, '租户运营', 'tenant_ops', 1, 1, '', 0, 2, 'M0 超悦内部', '1', NOW(), '1', NOW(), b'0', 1001),
  (2002, '租户管理员', 'tenant_admin', 1, 1, '', 0, 1, 'M0 演示租户B', '1', NOW(), '1', NOW(), b'0', 1002);

-- 菜单绑定见 chaoyue-m0-menus.sql。这里不能复制 super_admin 的全部菜单。
-- $2a$04$... 与库内 admin / admin123 相同
INSERT INTO system_users (
  id, username, password, nickname, remark, dept_id, post_ids, email, mobile, sex, avatar,
  status, login_ip, login_date, creator, create_time, updater, update_time, deleted, tenant_id
) VALUES
  (2001, 'oper', '$2a$04$.vd8nPeLwxt6hnSzmAoAyul8BOLX7Cib6QhcxRe30rfvrIPQHH1OG',
   '内部运营', 'M0 ops', NULL, NULL, 'oper', '', 0, '', 0, '', NULL, '1', NOW(), '1', NOW(), b'0', 1001),
  (2002, 'reviewer', '$2a$04$.vd8nPeLwxt6hnSzmAoAyul8BOLX7Cib6QhcxRe30rfvrIPQHH1OG',
   '审核员', 'M0 reviewer', NULL, NULL, 'reviewer', '', 0, '', 0, '', NULL, '1', NOW(), '1', NOW(), b'0', 1001),
  (2003, 'platform', '$2a$04$.vd8nPeLwxt6hnSzmAoAyul8BOLX7Cib6QhcxRe30rfvrIPQHH1OG',
   '平台运营', 'M0 platform', NULL, NULL, 'platform', '', 0, '', 0, '', NULL, '1', NOW(), '1', NOW(), b'0', 1),
  (2004, 'tenantb', '$2a$04$.vd8nPeLwxt6hnSzmAoAyul8BOLX7Cib6QhcxRe30rfvrIPQHH1OG',
   '租户B运营', 'M0 tenant-b', NULL, NULL, 'tenantb', '', 0, '', 0, '', NULL, '1', NOW(), '1', NOW(), b'0', 1002);

INSERT INTO system_user_role (user_id, role_id, creator, create_time, updater, update_time, deleted, tenant_id)
VALUES
  (2001, 2001, '1', NOW(), '1', NOW(), b'0', 1001),
  (2002, 2001, '1', NOW(), '1', NOW(), b'0', 1001),
  (2004, 2002, '1', NOW(), '1', NOW(), b'0', 1002);

INSERT INTO system_role (
  id, name, code, sort, data_scope, data_scope_dept_ids, status, type, remark,
  creator, create_time, updater, update_time, deleted, tenant_id
) VALUES (
  2003, '平台内容运营', 'platform_ops', 2, 1, '', 0, 2, 'M0 平台壳',
  '1', NOW(), '1', NOW(), b'0', 1
);

INSERT INTO system_user_role (user_id, role_id, creator, create_time, updater, update_time, deleted, tenant_id)
VALUES (2003, 2003, '1', NOW(), '1', NOW(), b'0', 1);
