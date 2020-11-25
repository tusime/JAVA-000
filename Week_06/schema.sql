-- ----------------------------
-- Table structure for t_user
-- ----------------------------
DROP TABLE IF EXISTS `t_user`;
CREATE TABLE `t_user` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `username` varchar(50) NOT NULL,
  `phone` char(11) DEFAULT NULL,
  `password` varchar(255) DEFAULT NULL,
  `status` tinyint(1) unsigned NOT NULL COMMENT '用户状态（0无效，1有效）',
  `create_time` datetime NOT NULL,
  `update_time` datetime NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 comment '用户表';

-- ----------------------------
-- Table structure for t_product
-- ----------------------------
DROP TABLE IF EXISTS `t_product`;
CREATE TABLE `t_product` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `name` varchar(255) NOT NULL,
  `price` bigint(20) NOT NULL,
  `status` tinyint(1) unsigned NOT NULL COMMENT '产品状态（0无效，1有效）',
  `create_time` datetime NOT NULL,
  `update_time` datetime NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 comment '产品表';

-- ----------------------------
-- Table structure for t_order
-- ----------------------------
DROP TABLE IF EXISTS `t_order`;
CREATE TABLE `t_order` (
  `id` bigint(20) NOT NULL,
  `snapshot_id` int(11) DEFAULT NULL COMMENT '快照id，如何使用？',
  `userid` int(11) NOT NULL,
  `total` bigint(255) DEFAULT NULL,
  `status` tinyint(1) NOT NULL,
  `create_time` datetime NOT NULL,
  `update_time` datetime NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 comment '订单表';

-- ----------------------------
-- Table structure for t_order_product
-- ----------------------------
DROP TABLE IF EXISTS `t_order_product`;
CREATE TABLE `t_order_product` (
  `order_id` bigint(20) NOT NULL,
  `product_id` bigint(20) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 comment '订单-产品关联表';
