-- ============================================================
-- Blog 全站数据库初始化脚本
-- 适用: MySQL 5.7 / 8.x
-- 默认超管: admin@example.com / admin123
-- ============================================================

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ============================================================
-- 第一部分：建表
-- ============================================================

DROP TABLE IF EXISTS `admin`;
CREATE TABLE `admin` (
  `id` int NOT NULL AUTO_INCREMENT COMMENT '管理员编号',
  `nickname` varchar(50) NOT NULL COMMENT '管理员昵称',
  `email` varchar(100) NOT NULL COMMENT '管理员邮箱',
  `password` varchar(255) NOT NULL COMMENT '管理员密码',
  `icon` mediumtext NULL COMMENT '管理员头像',
  `create_time` datetime NOT NULL COMMENT '管理员账号创建日期',
  `status` tinyint NULL DEFAULT 1 COMMENT '账号状态 0禁用 1启用',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='管理员表';

DROP TABLE IF EXISTS `admin_role_relation`;
CREATE TABLE `admin_role_relation` (
  `id` int NOT NULL AUTO_INCREMENT COMMENT '管理员角色关系编号',
  `admin_id` int NOT NULL COMMENT '管理员编号',
  `role_id` int NOT NULL COMMENT '角色编号',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE KEY `admin_id`(`admin_id`,`role_id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='管理员角色关系表';

DROP TABLE IF EXISTS `article`;
CREATE TABLE `article` (
  `id` int NOT NULL AUTO_INCREMENT COMMENT '文章编号',
  `user_id` int NOT NULL COMMENT '用户编号',
  `icon` mediumtext NULL COMMENT '文章封面',
  `title` varchar(100) NOT NULL COMMENT '文章标题',
  `content` text NULL COMMENT '文章内容',
  `create_time` datetime NOT NULL COMMENT '文章创建日期',
  `update_time` datetime NOT NULL COMMENT '修改日期',
  `sort` tinyint NULL DEFAULT 0 COMMENT '是否置顶 0不置顶 1置顶',
  `status` tinyint NULL DEFAULT 1 COMMENT '文章状态 0私有 1公开 2封禁',
  `category_id` int NOT NULL COMMENT '文章分类编号',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='文章表';

DROP TABLE IF EXISTS `article_category_relation`;
CREATE TABLE `article_category_relation` (
  `id` int NOT NULL AUTO_INCREMENT COMMENT '文章分类关系编号',
  `article_id` int NOT NULL COMMENT '文章编号',
  `category_id` int NOT NULL COMMENT '分类编号',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE KEY `article_id`(`article_id`,`category_id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='文章分类关系表';

DROP TABLE IF EXISTS `category`;
CREATE TABLE `category` (
  `id` int NOT NULL AUTO_INCREMENT COMMENT '文章分类编号',
  `name` varchar(50) NOT NULL COMMENT '文章分类名称',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='文章分类表';

DROP TABLE IF EXISTS `email`;
CREATE TABLE `email` (
  `id` int NOT NULL AUTO_INCREMENT COMMENT '邮件编号',
  `title` varchar(50) NOT NULL COMMENT '邮件标题',
  `content` text NULL COMMENT '邮件内容',
  `receiver_id` int NOT NULL COMMENT '接受方编号',
  `sender_id` int NOT NULL COMMENT '发送方编号',
  `create_time` datetime NOT NULL COMMENT '邮件创建时间',
  `status` tinyint NULL DEFAULT 0 COMMENT '0未读 1已读',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='邮件表';

DROP TABLE IF EXISTS `favorite`;
CREATE TABLE `favorite` (
  `id` int NOT NULL AUTO_INCREMENT COMMENT '收藏夹编号',
  `user_id` int NOT NULL COMMENT '用户编号',
  `name` varchar(50) NULL DEFAULT NULL COMMENT '收藏夹名称',
  `create_time` datetime NOT NULL COMMENT '创建时间',
  `status` int NULL DEFAULT 0,
  PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='收藏夹表';

DROP TABLE IF EXISTS `favorite_article_relation`;
CREATE TABLE `favorite_article_relation` (
  `id` int NOT NULL AUTO_INCREMENT COMMENT '收藏夹文章关系表',
  `favorite_id` int NOT NULL COMMENT '收藏夹编号',
  `article_id` int NOT NULL COMMENT '文章编号',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE KEY `favorite_id`(`favorite_id`,`article_id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='收藏夹文章关系表';

DROP TABLE IF EXISTS `hot_article`;
CREATE TABLE `hot_article` (
  `id` int NOT NULL AUTO_INCREMENT COMMENT '推荐文章编号',
  `article_id` int NOT NULL COMMENT '文章编号',
  `sort` tinyint NULL DEFAULT 1 COMMENT '文章排序 0置顶 1不置顶',
  `create_time` datetime NOT NULL COMMENT '创建日期',
  `status` tinyint NULL DEFAULT 0,
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE KEY `idx_article_id`(`article_id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='推荐文章表';

DROP TABLE IF EXISTS `identify_type`;
CREATE TABLE `identify_type` (
  `id` tinyint NOT NULL AUTO_INCREMENT COMMENT '身份类型编号',
  `type_value` varchar(50) NOT NULL COMMENT '身份类型值',
  `status` tinyint NOT NULL COMMENT '身份类型状态 0禁用 1启用',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='身份类型状态表';

DROP TABLE IF EXISTS `identify_user_relation`;
CREATE TABLE `identify_user_relation` (
  `id` int NOT NULL AUTO_INCREMENT COMMENT '用户身份关系编号',
  `user_id` int NOT NULL COMMENT '用户编号',
  `identify_id` int NOT NULL COMMENT '身份编号',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='用户身份关系表';

DROP TABLE IF EXISTS `label`;
CREATE TABLE `label` (
  `id` int NOT NULL AUTO_INCREMENT COMMENT '举报标签编号',
  `name` varchar(50) NOT NULL COMMENT '举报标签名称',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='举报标签表';

DROP TABLE IF EXISTS `menu`;
CREATE TABLE `menu` (
  `id` int NOT NULL AUTO_INCREMENT COMMENT '菜单编号',
  `p_id` int NULL DEFAULT 0 COMMENT '父级菜单编号',
  `name` varchar(50) NOT NULL COMMENT '菜单名称',
  `level` tinyint NULL DEFAULT 0 COMMENT '菜单级数',
  `web_name` varchar(100) NOT NULL COMMENT '菜单前端名称',
  `icon` varchar(200) NOT NULL COMMENT '菜单图标',
  `sort` tinyint NULL DEFAULT 0 COMMENT '菜单排序',
  `status` tinyint NULL DEFAULT 1 COMMENT '菜单是否隐藏 0隐藏 1显示',
  `create_time` datetime NULL DEFAULT NULL COMMENT '菜单创建日期',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='菜单表';

DROP TABLE IF EXISTS `permission`;
CREATE TABLE `permission` (
  `id` int NOT NULL AUTO_INCREMENT COMMENT '权限编号',
  `name` varchar(255) NOT NULL COMMENT '权限名称',
  `url` varchar(255) NOT NULL COMMENT '访问所需路径',
  `create_time` datetime NULL DEFAULT NULL COMMENT '权限创建日期',
  `description` varchar(200) NULL DEFAULT NULL COMMENT '权限描述',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='权限表';

DROP TABLE IF EXISTS `report`;
CREATE TABLE `report` (
  `id` int NOT NULL AUTO_INCREMENT COMMENT '举报信息编号',
  `object_type` tinyint NOT NULL COMMENT '举报对象类型 0用户 1文章 2评论',
  `object_id` int NOT NULL COMMENT '举报对象编号',
  `message` text NOT NULL COMMENT '举报内容',
  `user_id` int NOT NULL COMMENT '举报人编号',
  `status` tinyint NULL DEFAULT 0 COMMENT '举报状态 0处理中 1已处理',
  `result` text NULL COMMENT '处理结果',
  `result_admin_id` int NULL DEFAULT NULL COMMENT '处理管理员编号',
  `create_time` datetime NULL DEFAULT NULL COMMENT '举报提交日期',
  `finish_time` datetime NULL DEFAULT NULL COMMENT '举报完成日期',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='举报表';

DROP TABLE IF EXISTS `role`;
CREATE TABLE `role` (
  `id` int NOT NULL AUTO_INCREMENT COMMENT '角色编号',
  `name` varchar(50) NOT NULL COMMENT '角色名称',
  `description` varchar(200) NULL DEFAULT NULL COMMENT '角色描述',
  `create_time` datetime NOT NULL COMMENT '角色创建日期',
  `status` tinyint NULL DEFAULT 1 COMMENT '角色状态 0禁用 1启用',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='角色表';

DROP TABLE IF EXISTS `role_menu_relation`;
CREATE TABLE `role_menu_relation` (
  `id` int NOT NULL AUTO_INCREMENT COMMENT '角色菜单关系编号',
  `role_id` int NOT NULL COMMENT '角色编号',
  `menu_id` int NOT NULL COMMENT '菜单编号',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE KEY `role_id`(`role_id`,`menu_id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='角色菜单关系表';

DROP TABLE IF EXISTS `role_permission_relation`;
CREATE TABLE `role_permission_relation` (
  `id` int NOT NULL AUTO_INCREMENT COMMENT '角色权限关系编号',
  `role_id` int NOT NULL COMMENT '角色编号',
  `permission_id` int NOT NULL COMMENT '权限编号',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE KEY `role_id`(`role_id`,`permission_id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='角色权限关系表';

DROP TABLE IF EXISTS `subscribe`;
CREATE TABLE `subscribe` (
  `id` int NOT NULL AUTO_INCREMENT COMMENT '用户关注表',
  `user_id` int NOT NULL COMMENT '用户编号',
  `sub_user_id` int NOT NULL COMMENT '关注用户编号',
  `sort` tinyint NULL DEFAULT 0 COMMENT '是否置顶 0不置顶 1置顶',
  `create_time` datetime NOT NULL COMMENT '关注时间',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE KEY `user_id`(`user_id`,`sub_user_id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='用户关注表';

DROP TABLE IF EXISTS `theme`;
CREATE TABLE `theme` (
  `id` int NOT NULL AUTO_INCREMENT COMMENT '主题编号',
  `name` varchar(20) NOT NULL COMMENT '主题名称',
  `status` tinyint NULL DEFAULT 1 COMMENT '主题状态 0禁用 1启用',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='主题表';

DROP TABLE IF EXISTS `user`;
CREATE TABLE `user` (
  `id` int NOT NULL AUTO_INCREMENT COMMENT '用户编号',
  `nickname` varchar(50) NOT NULL COMMENT '用户昵称',
  `icon` mediumtext NULL COMMENT '用户头像',
  `email` varchar(100) NOT NULL COMMENT '用户邮箱',
  `password` varchar(255) NOT NULL COMMENT '用户密码',
  `description` varchar(200) NULL DEFAULT NULL COMMENT '用户简介',
  `theme_id` tinyint NULL DEFAULT 0 COMMENT '用户主题',
  `create_time` datetime NOT NULL COMMENT '用户创建时间',
  `like_show_status` tinyint NULL DEFAULT 1 COMMENT '用户喜欢列表展示状态 0不展示 1展示',
  `account_status` tinyint NULL DEFAULT 1 COMMENT '用户状态 0禁用 1启用',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE KEY `email`(`email`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='用户表';

DROP TABLE IF EXISTS `user_article_browse_log`;
CREATE TABLE `user_article_browse_log` (
  `id` int NOT NULL AUTO_INCREMENT COMMENT '用户浏览文章日志编号',
  `user_id` int NULL DEFAULT NULL COMMENT '用户编号',
  `article_id` int NOT NULL COMMENT '文章编号',
  `create_time` datetime NULL DEFAULT NULL COMMENT '浏览日期',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='用户浏览文章日志表';

DROP TABLE IF EXISTS `user_comment`;
CREATE TABLE `user_comment` (
  `id` int NOT NULL AUTO_INCREMENT COMMENT '用户评论编号',
  `user_id` int NOT NULL COMMENT '用户编号',
  `article_id` int NOT NULL COMMENT '文章编号',
  `f_id` int NULL DEFAULT NULL COMMENT '父评论编号',
  `content` text NOT NULL COMMENT '评论内容',
  `sort` tinyint NULL DEFAULT 0 COMMENT '是否置顶 0不置顶 1置顶',
  `create_time` datetime NOT NULL COMMENT '评论创建日期',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='用户评论表';

DROP TABLE IF EXISTS `user_identify`;
CREATE TABLE `user_identify` (
  `id` int NOT NULL AUTO_INCREMENT COMMENT '身份编号',
  `name` varchar(30) NOT NULL COMMENT '身份名称',
  `description` varchar(100) NOT NULL COMMENT '身份描述',
  `type` tinyint NOT NULL COMMENT '身份类型',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='用户身份表';

DROP TABLE IF EXISTS `user_like_article`;
CREATE TABLE `user_like_article` (
  `id` int NOT NULL AUTO_INCREMENT COMMENT '用户点赞文章编号',
  `user_id` int NOT NULL COMMENT '用户编号',
  `article_id` int NOT NULL COMMENT '文章编号',
  `like_time` datetime NULL DEFAULT NULL COMMENT '用户点赞日期',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE KEY `user_id`(`user_id`,`article_id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='用户点赞表';

DROP TABLE IF EXISTS `user_like_comment`;
CREATE TABLE `user_like_comment` (
  `id` int NOT NULL AUTO_INCREMENT COMMENT '用户评论关系编号',
  `user_id` int NOT NULL COMMENT '用户编号',
  `user_comment_id` int NOT NULL COMMENT '用户评论',
  `create_time` datetime NULL DEFAULT NULL COMMENT '点赞日期',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE KEY `user_id`(`user_id`,`user_comment_id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='用户点赞评论表';

-- ============================================================
-- 第二部分：初始化数据
-- ============================================================

-- 超级管理员角色
INSERT INTO `role` (id, name, description, create_time, status) VALUES
(1, '超级管理员', '拥有所有权限', '2026-06-20 00:00:00', 1);

-- 权限记录
INSERT INTO permission (id, name, url, create_time, description) VALUES
(1,  '管理员登录',        '/admin/login',                    NOW(), '管理员登录'),
(2,  '管理员注册',        '/admin/save',                     NOW(), '新增管理员'),
(3,  '管理员详情',        '/admin/detail/**',                NOW(), '查看管理员详情'),
(4,  '管理员更新',        '/admin/update',                   NOW(), '更新管理员信息'),
(5,  '管理员菜单',        '/admin/menus',                    NOW(), '获取管理员菜单'),
(6,  '管理员列表',        '/admin/list',                     NOW(), '查看管理员列表'),
(7,  '管理员删除',        '/admin/delete/**',                NOW(), '删除管理员'),
(8,  '管理员状态切换',    '/admin/toggleStatus/**',          NOW(), '启用/禁用管理员'),
(9,  '管理员角色分配',    '/admin/assignRole',               NOW(), '分配管理员角色'),
(10, '管理员角色查询',    '/admin/roles/**',                 NOW(), '查看管理员角色'),
(11, '用户列表',          '/admin/user/list',                NOW(), '查看用户列表'),
(12, '用户详情',          '/admin/user/detail/**',           NOW(), '查看用户详情'),
(13, '用户状态切换',      '/admin/user/toggleStatus/**',     NOW(), '启用/禁用用户'),
(14, '用户删除',          '/admin/user/delete/**',           NOW(), '删除用户'),
(15, '文章列表',          '/admin/article/list',             NOW(), '查看文章列表'),
(16, '文章状态切换',      '/admin/article/toggleStatus/**',  NOW(), '封禁/解封文章'),
(17, '文章删除',          '/admin/article/delete/**',        NOW(), '删除文章'),
(18, '举报列表',          '/admin/report/list',              NOW(), '查看举报列表'),
(19, '举报详情',          '/admin/report/detail/**',         NOW(), '查看举报详情'),
(20, '举报处理',          '/admin/report/handle',            NOW(), '处理举报'),
(21, '分类列表',          '/admin/category/list',            NOW(), '查看分类列表'),
(22, '分类新增',          '/admin/category/save',            NOW(), '新增分类'),
(23, '分类更新',          '/admin/category/update',          NOW(), '更新分类'),
(24, '分类删除',          '/admin/category/delete/**',       NOW(), '删除分类'),
(25, '角色列表',          '/admin/role/list',                NOW(), '查看角色列表'),
(26, '角色详情',          '/admin/role/detail/**',           NOW(), '查看角色详情'),
(27, '角色新增',          '/admin/role/save',                NOW(), '新增角色'),
(28, '角色更新',          '/admin/role/update',              NOW(), '更新角色'),
(29, '角色删除',          '/admin/role/delete/**',           NOW(), '删除角色'),
(30, '权限分配',          '/admin/role/assignPermission',    NOW(), '角色分配权限'),
(31, '菜单树',            '/admin/menu/tree',                NOW(), '查看菜单树'),
(32, '菜单新增',          '/admin/menu/save',                NOW(), '新增菜单'),
(33, '菜单更新',          '/admin/menu/update',              NOW(), '更新菜单'),
(34, '菜单删除',          '/admin/menu/delete/**',           NOW(), '删除菜单'),
(35, '权限缓存刷新',      '/admin/permission/refresh',       NOW(), '刷新权限缓存'),
(36, '权限列表',          '/admin/permission/list',          NOW(), '查看权限列表'),
(37, '权限详情',          '/admin/permission/detail/**',     NOW(), '查看权限详情'),
(38, '权限新增',          '/admin/permission/save',          NOW(), '新增权限'),
(39, '权限更新',          '/admin/permission/update',        NOW(), '更新权限'),
(40, '权限删除',          '/admin/permission/delete/**',     NOW(), '删除权限'),
(41, '角色权限分配',      '/admin/permission/assignPermission', NOW(), '角色分配权限'),
(42, '菜单分配',          '/admin/role/assignMenu',          NOW(), '角色分配菜单'),
(43, '邮件发送',          '/admin/email/send',               NOW(), '发送邮件'),
(44, '仪表盘数据',        '/admin/dashboard',                NOW(), '查看仪表盘数据'),
(45, '身份列表',          '/admin/identify/list',            NOW(), '查看身份列表'),
(46, '用户身份查询',      '/admin/identify/user/**',         NOW(), '查看用户身份'),
(47, '身份分配',          '/admin/identify/assign',          NOW(), '分配用户身份'),
(48, '身份移除',          '/admin/identify/remove/**',       NOW(), '移除用户身份'),
(49, '身份新增',          '/admin/identify/save',            NOW(), '新增身份'),
(50, '身份更新',          '/admin/identify/update',          NOW(), '更新身份'),
(51, '身份删除',          '/admin/identify/delete/**',       NOW(), '删除身份');

-- 菜单记录
INSERT INTO menu (id, p_id, name, level, web_name, icon, sort, status, create_time) VALUES
(1,  0, 'dashboard',       1, '仪表盘',   'HomeFilled',    1, 1, NOW()),
(2,  0, 'user/list',       1, '用户管理', 'UserFilled',    2, 1, NOW()),
(3,  0, 'article/list',    1, '文章管理', 'Document',      3, 1, NOW()),
(4,  0, 'content',         1, '内容管理', 'Management',    4, 1, NOW()),
(5,  0, 'system',          1, '系统管理', 'Setting',       5, 1, NOW()),
(6,  4, 'category/list',   2, '分类管理', 'Collection',    1, 1, NOW()),
(7,  4, 'report/list',     2, '举报管理', 'WarningFilled', 2, 1, NOW()),
(12, 4, 'email/manage',    2, '邮件管理', 'Message',       3, 1, NOW()),
(8,  5, 'admin/list',      2, '管理员管理','Avatar',       1, 1, NOW()),
(9,  5, 'role/list',       2, '角色管理', 'Lock',          2, 1, NOW()),
(10, 5, 'menu/list',       2, '菜单管理', 'Menu',          3, 1, NOW()),
(11, 5, 'permission/list', 2, '权限管理', 'Key',           4, 1, NOW()),
(13, 5, 'identify/list',   2, '身份管理', 'Medal',         5, 1, NOW());

-- 超管拥有全部权限
INSERT INTO role_permission_relation (role_id, permission_id) VALUES
(1,1),(1,2),(1,3),(1,4),(1,5),(1,6),(1,7),(1,8),(1,9),(1,10),
(1,11),(1,12),(1,13),(1,14),(1,15),(1,16),(1,17),(1,18),(1,19),(1,20),
(1,21),(1,22),(1,23),(1,24),(1,25),(1,26),(1,27),(1,28),(1,29),(1,30),
(1,31),(1,32),(1,33),(1,34),(1,35),(1,36),(1,37),(1,38),(1,39),(1,40),
(1,41),(1,42),(1,43),(1,44),(1,45),(1,46),(1,47),(1,48),(1,49),(1,50),(1,51);

-- 超管拥有全部菜单
INSERT INTO role_menu_relation (role_id, menu_id) VALUES
(1,1),(1,2),(1,3),(1,4),(1,5),(1,6),(1,7),(1,12),(1,8),(1,9),(1,10),(1,11),(1,13);

-- 预设超管账户 (密码: admin123)
INSERT INTO admin (id, nickname, email, password, create_time, status) VALUES
(1, '超级管理员', 'admin@example.com', 'PBKDF2WithHmacSHA256:310000:FLw2QoY+toPjODlbR8TA0A==:GHjdJzZaOQ6gs4dq+keZQYupdW0zRuYaYPMtrtnJ45U=', NOW(), 1);

-- 超管关联角色
INSERT INTO admin_role_relation (admin_id, role_id) VALUES (1, 1);

-- 身份类型
INSERT INTO identify_type (id, type_value, status) VALUES
(1, '蓝标', 1),
(2, '黄标', 1);

-- 用户身份
INSERT INTO user_identify (id, name, description, type) VALUES
(1, '官方认证', '平台官方认证用户', 1),
(2, '优质创作者', '持续输出优质内容的创作者', 1),
(3, '资深用户', '长期活跃的社区成员', 2);

SET FOREIGN_KEY_CHECKS = 1;
