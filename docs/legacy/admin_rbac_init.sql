-- ===================================================
-- Blog Admin B-end RBAC Initialization SQL
-- 默认超管: admin@example.com / admin123
-- ===================================================

-- 0. 清除已有初始数据
DELETE FROM admin_role_relation;
DELETE FROM role_permission_relation;
DELETE FROM role_menu_relation;
DELETE FROM permission;
DELETE FROM menu;
DELETE FROM `role`;
DELETE FROM admin;

-- 重置自增（可选，MySQL）
ALTER TABLE admin AUTO_INCREMENT = 1;
ALTER TABLE `role` AUTO_INCREMENT = 1;
ALTER TABLE permission AUTO_INCREMENT = 1;
ALTER TABLE menu AUTO_INCREMENT = 1;

-- 1. 超级管理员角色 (create_time 为 VARCHAR)
INSERT INTO `role` (id, name, description, create_time, status) VALUES
(1, '超级管理员', '拥有所有权限', '2026-06-20 00:00:00', 1);

-- 2. 权限记录 (包含所有 /admin/** 端点)
INSERT INTO permission (id, name, url, create_time, description) VALUES
-- 管理员账户管理（已有端点）
(1,  '管理员登录',       '/admin/login',             NOW(), '管理员登录'),
(2,  '管理员注册',       '/admin/save',              NOW(), '新增管理员'),
(3,  '管理员详情',       '/admin/detail/**',         NOW(), '查看管理员详情'),
(4,  '管理员更新',       '/admin/update',            NOW(), '更新管理员信息'),
(5,  '管理员菜单',       '/admin/menus',             NOW(), '获取管理员菜单'),
-- 管理员管理（新增端点）
(6,  '管理员列表',       '/admin/list',              NOW(), '查看管理员列表'),
(7,  '管理员删除',       '/admin/delete/**',         NOW(), '删除管理员'),
(8,  '管理员状态切换',   '/admin/toggleStatus/**',   NOW(), '启用/禁用管理员'),
(9,  '管理员角色分配',   '/admin/assignRole',        NOW(), '分配管理员角色'),
(10, '管理员角色查询',   '/admin/roles/**',          NOW(), '查看管理员角色'),
-- 用户管理
(11, '用户列表',         '/admin/user/list',            NOW(), '查看用户列表'),
(12, '用户详情',         '/admin/user/detail/**',       NOW(), '查看用户详情'),
(13, '用户状态切换',     '/admin/user/toggleStatus/**', NOW(), '启用/禁用用户'),
(14, '用户删除',         '/admin/user/delete/**',       NOW(), '删除用户'),
-- 文章管理
(15, '文章列表',         '/admin/article/list',            NOW(), '查看文章列表'),
(16, '文章状态切换',     '/admin/article/toggleStatus/**', NOW(), '封禁/解封文章'),
(17, '文章删除',         '/admin/article/delete/**',       NOW(), '删除文章'),
-- 举报处理
(18, '举报列表',         '/admin/report/list',     NOW(), '查看举报列表'),
(19, '举报详情',         '/admin/report/detail/**',NOW(), '查看举报详情'),
(20, '举报处理',         '/admin/report/handle',   NOW(), '处理举报'),
-- 分类管理
(21, '分类列表',         '/admin/category/list',   NOW(), '查看分类列表'),
(22, '分类新增',         '/admin/category/save',   NOW(), '新增分类'),
(23, '分类更新',         '/admin/category/update', NOW(), '更新分类'),
(24, '分类删除',         '/admin/category/delete/**', NOW(), '删除分类'),
-- 角色管理
(25, '角色列表',         '/admin/role/list',       NOW(), '查看角色列表'),
(26, '角色详情',         '/admin/role/detail/**',  NOW(), '查看角色详情'),
(27, '角色新增',         '/admin/role/save',       NOW(), '新增角色'),
(28, '角色更新',         '/admin/role/update',     NOW(), '更新角色'),
(29, '角色删除',         '/admin/role/delete/**',  NOW(), '删除角色'),
(30, '权限分配',         '/admin/role/assignPermission', NOW(), '角色分配权限'),
(42, '菜单分配',         '/admin/role/assignMenu',       NOW(), '角色分配菜单'),
-- 菜单管理
(31, '菜单树',           '/admin/menu/tree',       NOW(), '查看菜单树'),
(32, '菜单新增',         '/admin/menu/save',       NOW(), '新增菜单'),
(33, '菜单更新',         '/admin/menu/update',     NOW(), '更新菜单'),
(34, '菜单删除',         '/admin/menu/delete/**',  NOW(), '删除菜单'),
-- 权限刷新
(35, '权限缓存刷新',     '/admin/permission/refresh', NOW(), '刷新权限缓存'),
-- 权限管理
(36, '权限列表',         '/admin/permission/list',            NOW(), '查看权限列表'),
(37, '权限详情',         '/admin/permission/detail/**',       NOW(), '查看权限详情'),
(38, '权限新增',         '/admin/permission/save',            NOW(), '新增权限'),
(39, '权限更新',         '/admin/permission/update',          NOW(), '更新权限'),
(40, '权限删除',         '/admin/permission/delete/**',       NOW(), '删除权限'),
(41, '角色权限分配',     '/admin/permission/assignPermission', NOW(), '角色分配权限'),
-- 邮件管理
(43, '邮件发送',         '/admin/email/send',               NOW(), '发送邮件'),
-- 仪表盘
(44, '仪表盘数据',       '/admin/dashboard',                NOW(), '查看仪表盘数据'),
-- 身份管理
(45, '身份列表',         '/admin/identify/list',             NOW(), '查看身份列表'),
(46, '用户身份查询',     '/admin/identify/user/**',          NOW(), '查看用户身份'),
(47, '身份分配',         '/admin/identify/assign',           NOW(), '分配用户身份'),
(48, '身份移除',         '/admin/identify/remove/**',        NOW(), '移除用户身份'),
(49, '身份新增',         '/admin/identify/save',             NOW(), '新增身份'),
(50, '身份更新',         '/admin/identify/update',           NOW(), '更新身份'),
(51, '身份删除',         '/admin/identify/delete/**',        NOW(), '删除身份');

-- 3. 菜单记录
INSERT INTO menu (id, p_id, name, level, web_name, icon, sort, status, create_time) VALUES
(1,  0, 'dashboard',    1, '仪表盘',     'HomeFilled',     1, 1, NOW()),
(2,  0, 'user/list',    1, '用户管理',   'UserFilled',     2, 1, NOW()),
(3,  0, 'article/list', 1, '文章管理',   'Document',       3, 1, NOW()),
(4,  0, 'content',      1, '内容管理',   'Management',     4, 1, NOW()),
(5,  0, 'system',       1, '系统管理',   'Setting',        5, 1, NOW()),
(6,  4, 'category/list',2, '分类管理',   'Collection',     1, 1, NOW()),
(7,  4, 'report/list',  2, '举报管理',   'WarningFilled',  2, 1, NOW()),
(12, 4, 'email/manage', 2, '邮件管理',   'Message',        3, 1, NOW()),
(8,  5, 'admin/list',   2, '管理员管理', 'Avatar',         1, 1, NOW()),
(9,  5, 'role/list',    2, '角色管理',   'Lock',           2, 1, NOW()),
(10, 5, 'menu/list',    2, '菜单管理',   'Menu',           3, 1, NOW()),
(11, 5, 'permission/list', 2, '权限管理',  'Key',          4, 1, NOW()),
(13, 5, 'identify/list',  2, '身份管理',  'Medal',         5, 1, NOW());

-- 4. 超级管理员角色拥有全部权限
INSERT INTO role_permission_relation (role_id, permission_id) VALUES
(1,1),(1,2),(1,3),(1,4),(1,5),
(1,6),(1,7),(1,8),(1,9),(1,10),
(1,11),(1,12),(1,13),(1,14),
(1,15),(1,16),(1,17),
(1,18),(1,19),(1,20),
(1,21),(1,22),(1,23),(1,24),
(1,25),(1,26),(1,27),(1,28),(1,29),(1,30),
(1,31),(1,32),(1,33),(1,34),
(1,35),
(1,36),(1,37),(1,38),(1,39),(1,40),(1,41),(1,42),(1,43),(1,44),(1,45),(1,46),(1,47),(1,48),(1,49),(1,50),(1,51);

-- 5. 超级管理员角色拥有全部菜单
INSERT INTO role_menu_relation (role_id, menu_id) VALUES
(1,1),(1,2),(1,3),(1,4),(1,5),
(1,6),(1,7),(1,12),(1,8),(1,9),(1,10),(1,11),(1,13);

-- 6. 预设超级管理员账户 (密码: admin123)
INSERT INTO admin (id, nickname, email, password, create_time, status) VALUES
(1, '超级管理员', 'admin@example.com', 'PBKDF2WithHmacSHA256:310000:FLw2QoY+toPjODlbR8TA0A==:GHjdJzZaOQ6gs4dq+keZQYupdW0zRuYaYPMtrtnJ45U=', NOW(), 1);

-- 7. 超级管理员关联角色
INSERT INTO admin_role_relation (admin_id, role_id) VALUES (1, 1);

-- 8. 身份类型初始化
INSERT INTO identify_type (id, type_value, status) VALUES
(1, '蓝标', 1),
(2, '黄标', 1);

-- 9. 用户身份初始化
INSERT INTO user_identify (id, name, description, type) VALUES
(1, '官方认证', '平台官方认证用户', 1),
(2, '优质创作者', '持续输出优质内容的创作者', 1),
(3, '资深用户', '长期活跃的社区成员', 2);
