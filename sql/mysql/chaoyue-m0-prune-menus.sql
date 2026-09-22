-- M0：只保留系统管理（用户/角色/菜单/部门/岗位/字典/租户/审计）。
-- 芋道商城、CRM、ERP、AI、IoT 等目录软删。可重复执行。

SET NAMES utf8mb4;

DROP TEMPORARY TABLE IF EXISTS tmp_keep_menu;
CREATE TEMPORARY TABLE tmp_keep_menu (id BIGINT PRIMARY KEY);

INSERT INTO tmp_keep_menu
WITH RECURSIVE keep AS (
  SELECT id FROM system_menu WHERE id = 1
  UNION ALL
  SELECT m.id
  FROM system_menu m
  INNER JOIN keep k ON m.parent_id = k.id
  WHERE m.id NOT IN (772, 201, 529, 271)
)
SELECT id FROM keep;

UPDATE system_menu
SET deleted = 1, updater = 'm0', update_time = NOW()
WHERE deleted = 0
  AND id NOT IN (SELECT id FROM tmp_keep_menu);

DROP TEMPORARY TABLE IF EXISTS tmp_keep_menu;
