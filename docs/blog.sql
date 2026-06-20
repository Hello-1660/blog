/*
 Navicat Premium Dump SQL

 Source Server         : 本地1
 Source Server Type    : MySQL
 Source Server Version : 50718 (5.7.18)
 Source Host           : localhost:3306
 Source Schema         : blog

 Target Server Type    : MySQL
 Target Server Version : 50718 (5.7.18)
 File Encoding         : 65001

 Date: 20/06/2026 11:13:07
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for admin
-- ----------------------------
DROP TABLE IF EXISTS `admin`;
CREATE TABLE `admin`  (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '管理员编号',
  `nickname` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '管理员昵称',
  `email` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '管理员邮箱',
  `password` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '管理员密码',
  `icon` mediumtext CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL COMMENT '管理员头像',
  `create_time` datetime NOT NULL COMMENT '管理员账号创建日期',
  `status` tinyint(4) NULL DEFAULT 1 COMMENT '账号状态 0禁用 1启用',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 3 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '管理员表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for admin_role_relation
-- ----------------------------
DROP TABLE IF EXISTS `admin_role_relation`;
CREATE TABLE `admin_role_relation`  (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '管理员角色关系编号',
  `admin_id` int(11) NOT NULL COMMENT '管理员编号',
  `role_id` int(11) NOT NULL COMMENT '角色编号',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `admin_id`(`admin_id`, `role_id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 7 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '管理员角色关系表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for article
-- ----------------------------
DROP TABLE IF EXISTS `article`;
CREATE TABLE `article`  (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '文章编号',
  `user_id` int(11) NOT NULL COMMENT '用户编号',
  `icon` mediumtext CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL COMMENT '文章封面',
  `title` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '文章标题',
  `content` text CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL COMMENT '文章内容',
  `create_time` datetime NOT NULL COMMENT '文章创建日期',
  `update_time` datetime NOT NULL COMMENT '修改日期',
  `sort` tinyint(4) NULL DEFAULT 0 COMMENT '文章是否置顶 0不置顶 1置顶',
  `status` tinyint(4) NULL DEFAULT 1 COMMENT '文章状态 0私有 1公开 2封禁',
  `category_id` int(11) NOT NULL COMMENT '文章分类编号',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 17 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '文章表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for article_category_relation
-- ----------------------------
DROP TABLE IF EXISTS `article_category_relation`;
CREATE TABLE `article_category_relation`  (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '文章分类关系编号',
  `article_id` int(11) NOT NULL COMMENT '文章编号',
  `category_id` int(11) NOT NULL COMMENT '分类编号',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `article_id`(`article_id`, `category_id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '文章分类关系表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for category
-- ----------------------------
DROP TABLE IF EXISTS `category`;
CREATE TABLE `category`  (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '文章分类编号',
  `name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '文章分类名称',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 6 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '文章分类表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for email
-- ----------------------------
DROP TABLE IF EXISTS `email`;
CREATE TABLE `email`  (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '邮件编号',
  `title` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '邮件标题',
  `content` text CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL COMMENT '邮件内容',
  `receiver_id` int(11) NOT NULL COMMENT '接受方编号',
  `sender_id` int(11) NOT NULL COMMENT '发送方编号',
  `create_time` datetime NOT NULL COMMENT '邮件创建时间',
  `status` tinyint(4) NULL DEFAULT 0 COMMENT '0未读 1已读',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 4 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '邮件表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for favorite
-- ----------------------------
DROP TABLE IF EXISTS `favorite`;
CREATE TABLE `favorite`  (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '收藏夹编号',
  `user_id` int(11) NOT NULL COMMENT '用户编号',
  `name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '收藏夹名称',
  `create_time` datetime NOT NULL COMMENT '创建时间',
  `status` int(11) NULL DEFAULT 0,
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 8 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '收藏夹表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for favorite_article_relation
-- ----------------------------
DROP TABLE IF EXISTS `favorite_article_relation`;
CREATE TABLE `favorite_article_relation`  (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '收藏夹文章关系表',
  `favorite_id` int(11) NOT NULL COMMENT '收藏夹编号',
  `article_id` int(11) NOT NULL COMMENT '文章编号',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `favorite_id`(`favorite_id`, `article_id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 4 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '收藏夹文章关系表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for hot_article
-- ----------------------------
DROP TABLE IF EXISTS `hot_article`;
CREATE TABLE `hot_article`  (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '推荐文章编号',
  `article_id` int(11) NOT NULL COMMENT '文章编号',
  `sort` tinyint(4) NULL DEFAULT 1 COMMENT '文章排序 0置顶 1不置顶',
  `create_time` datetime NOT NULL COMMENT '创建日期',
  `status` tinyint(4) NULL DEFAULT 0,
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `idx_article_id`(`article_id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '推荐文章表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for identify_type
-- ----------------------------
DROP TABLE IF EXISTS `identify_type`;
CREATE TABLE `identify_type`  (
  `id` tinyint(4) NOT NULL AUTO_INCREMENT COMMENT '身份类型编号',
  `type_value` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '身份类型值',
  `status` tinyint(4) NOT NULL COMMENT '身份类型状态，0禁用 1启用',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 3 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '身份类型状态表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for identify_user_relation
-- ----------------------------
DROP TABLE IF EXISTS `identify_user_relation`;
CREATE TABLE `identify_user_relation`  (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '用户身份关系编号',
  `user_id` int(11) NOT NULL COMMENT '用户编号',
  `identify_id` int(11) NOT NULL COMMENT '身份编号',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 2 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '用户身份关系表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for label
-- ----------------------------
DROP TABLE IF EXISTS `label`;
CREATE TABLE `label`  (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '举报标签编号',
  `name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '举报标签名称',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 4 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '举报标签表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for menu
-- ----------------------------
DROP TABLE IF EXISTS `menu`;
CREATE TABLE `menu`  (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '菜单编号',
  `p_id` int(11) NULL DEFAULT 0 COMMENT '父级菜单编号',
  `name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '菜单名称',
  `level` tinyint(4) NULL DEFAULT 0 COMMENT '菜单级数',
  `web_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '菜单前端名称',
  `icon` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '菜单图标',
  `sort` tinyint(4) NULL DEFAULT 0 COMMENT '菜单排序',
  `status` tinyint(4) NULL DEFAULT 1 COMMENT '菜单是否隐藏 0隐藏 1显示',
  `create_time` datetime NULL DEFAULT NULL COMMENT '菜单创建日期',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 11 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '菜单表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for permission
-- ----------------------------
DROP TABLE IF EXISTS `permission`;
CREATE TABLE `permission`  (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '权限编号',
  `name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '权限名称',
  `url` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '访问所需路径',
  `create_time` datetime NULL DEFAULT NULL COMMENT '权限创建日期',
  `description` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '权限描述',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 36 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '权限表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for report
-- ----------------------------
DROP TABLE IF EXISTS `report`;
CREATE TABLE `report`  (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '举报信息编号',
  `object_type` tinyint(4) NOT NULL COMMENT '举报对象类型 0用户 1文章 2评论',
  `object_id` int(11) NOT NULL COMMENT '举报对象编号',
  `message` text CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '举报内容',
  `user_id` int(11) NOT NULL COMMENT '举报人编号',
  `status` tinyint(4) NULL DEFAULT 0 COMMENT '举报状态 0处理中 1已处理',
  `result` text CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL COMMENT '处理结果',
  `result_admin_id` int(11) NULL DEFAULT NULL COMMENT '处理管理员编号',
  `create_time` datetime NULL DEFAULT NULL COMMENT '举报提交日期',
  `finish_time` datetime NULL DEFAULT NULL COMMENT '举报完成日期',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 6 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '举报表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for role
-- ----------------------------
DROP TABLE IF EXISTS `role`;
CREATE TABLE `role`  (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '角色编号',
  `name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '角色名称',
  `description` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '角色描述',
  `create_time` datetime NOT NULL COMMENT '角色创建日期',
  `status` tinyint(4) NULL DEFAULT 1 COMMENT '角色状态 0禁用 1启用',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 3 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '角色表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for role_menu_relation
-- ----------------------------
DROP TABLE IF EXISTS `role_menu_relation`;
CREATE TABLE `role_menu_relation`  (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '角色菜单关系编号',
  `role_id` int(11) NOT NULL COMMENT '角色编号',
  `menu_id` int(11) NOT NULL COMMENT '菜单编号',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `role_id`(`role_id`, `menu_id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 51 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '角色菜单关系表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for role_permission_relation
-- ----------------------------
DROP TABLE IF EXISTS `role_permission_relation`;
CREATE TABLE `role_permission_relation`  (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '角色权限关系编号',
  `role_id` int(11) NOT NULL COMMENT '角色编号',
  `permission_id` int(11) NOT NULL COMMENT '权限编号',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `role_id`(`role_id`, `permission_id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 175 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '角色权限关系表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for subscribe
-- ----------------------------
DROP TABLE IF EXISTS `subscribe`;
CREATE TABLE `subscribe`  (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '用户关注表',
  `user_id` int(11) NOT NULL COMMENT '用户编号',
  `sub_user_id` int(11) NOT NULL COMMENT '关注用户编号',
  `sort` tinyint(4) NULL DEFAULT 0 COMMENT '是否置顶 0不置顶 1置顶',
  `create_time` datetime NOT NULL COMMENT '关注时间',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `user_id`(`user_id`, `sub_user_id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 7 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '用户关注表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for test
-- ----------------------------
DROP TABLE IF EXISTS `test`;
CREATE TABLE `test`  (
  `message` varchar(10) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '测试' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for theme
-- ----------------------------
DROP TABLE IF EXISTS `theme`;
CREATE TABLE `theme`  (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '主题编号',
  `name` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '主题名称',
  `status` tinyint(4) NULL DEFAULT 1 COMMENT '主题状态 0禁用 1启用',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 2 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '主题表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for user
-- ----------------------------
DROP TABLE IF EXISTS `user`;
CREATE TABLE `user`  (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '用户编号',
  `nickname` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '用户昵称',
  `icon` mediumtext CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL COMMENT '用户头像',
  `email` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '用户邮箱',
  `password` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '用户密码',
  `description` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '用户简介',
  `theme_id` tinyint(4) NULL DEFAULT 0 COMMENT '用户主题',
  `create_time` datetime NOT NULL COMMENT '用户创建时间',
  `like_show_status` tinyint(4) NULL DEFAULT 1 COMMENT '用户喜欢列表展示状态 0不展示 1展示',
  `account_status` tinyint(4) NULL DEFAULT 1 COMMENT '用户状态 0禁用 1启用',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `email`(`email`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 4 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '用户表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for user_article_browse_log
-- ----------------------------
DROP TABLE IF EXISTS `user_article_browse_log`;
CREATE TABLE `user_article_browse_log`  (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '用户浏览文章日志编号',
  `user_id` int(11) NULL DEFAULT NULL COMMENT '用户编号',
  `article_id` int(11) NOT NULL COMMENT '文章编号',
  `create_time` datetime NULL DEFAULT NULL COMMENT '浏览日期',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 101 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '用户浏览文章日志表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for user_comment
-- ----------------------------
DROP TABLE IF EXISTS `user_comment`;
CREATE TABLE `user_comment`  (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '用户评论编号',
  `user_id` int(11) NOT NULL COMMENT '用户编号',
  `article_id` int(11) NOT NULL COMMENT '文章编号',
  `f_id` int(11) NULL DEFAULT NULL COMMENT '父评论编号',
  `content` text CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '评论内容',
  `sort` tinyint(4) NULL DEFAULT 0 COMMENT '是否置顶 0不置顶 1置顶',
  `create_time` datetime NOT NULL COMMENT '评论创建日期',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 11 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '用户评论表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for user_identify
-- ----------------------------
DROP TABLE IF EXISTS `user_identify`;
CREATE TABLE `user_identify`  (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '身份编号',
  `name` varchar(30) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '身份名称',
  `description` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '身份描述',
  `type` tinyint(4) NOT NULL COMMENT '身份类型',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 2 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '用户身份表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for user_like_article
-- ----------------------------
DROP TABLE IF EXISTS `user_like_article`;
CREATE TABLE `user_like_article`  (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '用户点赞文章编号',
  `user_id` int(11) NOT NULL COMMENT '用户编号',
  `article_id` int(11) NOT NULL COMMENT '文章编号',
  `like_time` datetime NULL DEFAULT NULL COMMENT '用户点赞日期',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `user_id`(`user_id`, `article_id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 15 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '用户点赞表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for user_like_comment
-- ----------------------------
DROP TABLE IF EXISTS `user_like_comment`;
CREATE TABLE `user_like_comment`  (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '用户评论关系编号',
  `user_id` int(11) NOT NULL COMMENT '用户编号',
  `user_comment_id` int(11) NOT NULL COMMENT '用户评论',
  `create_time` datetime NULL DEFAULT NULL COMMENT '点赞日期',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `user_id`(`user_id`, `user_comment_id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 14 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '用户点赞评论表' ROW_FORMAT = Dynamic;

SET FOREIGN_KEY_CHECKS = 1;
