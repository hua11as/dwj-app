SET FOREIGN_KEY_CHECKS=0;

-- ----------------------------
-- Table structure for `bjl_draw_records`
-- ----------------------------
DROP TABLE IF EXISTS `bjl_draw_records`;
CREATE TABLE `bjl_draw_records` (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `award_period` char(14) NOT NULL COMMENT '开奖期号（如：201808070001）',
  `draw_result` int(1) NOT NULL COMMENT '中奖结果（0、未知；1、庄胜；2、闲胜）',
  `award_video` int(11) DEFAULT NULL COMMENT '开奖视频（关联开奖视频表主键）',
  `statistical_method` int(11) NOT NULL COMMENT '统计方式（0、前端请求统计，1、后端自动统计）',
  `add_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `remark` varchar(255) DEFAULT NULL COMMENT '备注',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='开奖结果记录表';

-- ----------------------------
-- Records of bjl_draw_records
-- ----------------------------

-- ----------------------------
-- Table structure for `bjl_end_chipin`
-- ----------------------------
DROP TABLE IF EXISTS `bjl_end_chipin`;
CREATE TABLE `bjl_end_chipin` (
  `id` int(11) NOT NULL,
  `video_serial` varchar(64) DEFAULT NULL COMMENT '视频名称',
  `play_order` int(11) NOT NULL COMMENT '播放顺序（分段载入）',
  `current_periods` int(11) NOT NULL COMMENT '当前期数（从1开始）',
  `end_chipIn_time` time NOT NULL COMMENT '截止下注时间（真实时间，只设置时分秒）',
  `current_result` int(11) NOT NULL COMMENT '当期结果 （0、庄胜；1、闲胜）',
  `player_point` varchar(64) DEFAULT NULL COMMENT '闲家牌（多个用逗号分隔）',
  `banker_point` varchar(64) DEFAULT NULL COMMENT '庄家牌（多个用逗号分隔）',
  `link_adress` varchar(200) DEFAULT NULL COMMENT '播放地址',
  `add_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `remark` varchar(255) DEFAULT NULL COMMENT '备注',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='底部播放器设置表';

-- ----------------------------
-- Records of bjl_end_chipin
-- ----------------------------

-- ----------------------------
-- Table structure for `bjl_openprize_video`
-- ----------------------------
DROP TABLE IF EXISTS `bjl_openprize_video`;
CREATE TABLE `bjl_openprize_video` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `total_play_times` int(11) NOT NULL COMMENT '视频总时长 （单位秒）',
  `result_sign` int(11) NOT NULL COMMENT '开奖结果标记 （0、庄家胜；1、闲胜）',
  `status` int(11) NOT NULL COMMENT '状态 （0、有效；1、无效）',
  `player_point` varchar(64) DEFAULT NULL COMMENT '闲家牌（多个用逗号分隔）',
  `banker_point` varchar(64) DEFAULT NULL COMMENT '庄家牌（多个用逗号分隔）',
  `link_adress` varchar(200) DEFAULT NULL COMMENT '播放地址',
  `add_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `remark` varchar(255) DEFAULT NULL COMMENT '备注',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='开奖视频登记表';

-- ----------------------------
-- Records of bjl_openprize_video
-- ----------------------------

-- ----------------------------
-- Table structure for `bjl_order`
-- ----------------------------
DROP TABLE IF EXISTS `bjl_order`;
CREATE TABLE `bjl_order` (
  `id` int(11) NOT NULL COMMENT '主键',
  `vip_id` int(11) NOT NULL COMMENT '会员id',
  `periods` int(11) NOT NULL COMMENT '购买期数',
  `buy_amount` decimal(11,2) unsigned zerofill NOT NULL COMMENT '下注金额',
  `support_win` int(11) NOT NULL COMMENT '押注哪方赢  （0、庄家胜；1、闲胜）',
  `selected_size` int(11) NOT NULL COMMENT '下注方式（1、闲 ；2、闲对；3 、和；4、庄对；5、庄）',
  `video_serial` varchar(255) NOT NULL COMMENT '视频名称',
  `final_result` int(10) unsigned NOT NULL DEFAULT '0' COMMENT ' 最终结果，是输还是赢 （0、初始值；1、赢；2、输）',
  `add_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `remark` varchar(255) DEFAULT NULL COMMENT '备注',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='用户下注百家乐订单表';


-- ----------------------------
-- Table structure for `t_money_record`
-- ----------------------------
DROP TABLE IF EXISTS `t_money_record`;
CREATE TABLE `t_money_record` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `vipId` int(11) NOT NULL COMMENT 'vip会员id',
  `amount` decimal(11,2) NOT NULL COMMENT '交易金额',
  `type` int(4) NOT NULL COMMENT '1充值，2提现，3赚入，4下注，5佣金',
  `status` int(2) NOT NULL DEFAULT '0' COMMENT '交易状态（0待入账，1成功，2失败）',
  `order_id` varchar(255) DEFAULT NULL COMMENT '支付订单号(仅针对充值、提现)',
  `remark` varchar(255) DEFAULT NULL COMMENT '备注',
  `create_time` datetime NOT NULL COMMENT '创建时间',
  PRIMARY KEY (`id`),
   KEY `t_money_record_orderId` (`order_id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='用户资金流水记录表';

-- ----------------------------
-- Records of t_money_record
-- ----------------------------

-- ----------------------------
-- Table structure for `t_recharge_record`
-- ----------------------------
DROP TABLE IF EXISTS `t_recharge_record`;
CREATE TABLE `t_recharge_record` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `vip_id` int(11) NOT NULL,
  `amount` decimal(11,2) NOT NULL,
  `create_data` datetime NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of t_recharge_record
-- ----------------------------

-- ----------------------------
-- Table structure for `t_vip_member`
-- ----------------------------
DROP TABLE IF EXISTS `t_vip_member`;
CREATE TABLE `t_vip_member` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `mobile` varchar(11) NOT NULL COMMENT '手机号码',
  `verify_code` varchar(50) DEFAULT NULL COMMENT '验证码',
  `amount` decimal(11,2) NOT NULL DEFAULT '0.00' COMMENT '账户余额',
  `version` int(11) NOT NULL DEFAULT '0' COMMENT '版本号',
  `password` varchar(128) DEFAULT NULL COMMENT '支付密码',
  `pwd_switch` int(2) NOT NULL DEFAULT '1' COMMENT '支付是否需要输入密码（0免密、1需要密码）',
  `status` int(2) NOT NULL DEFAULT '0' COMMENT '状态（0正常、1冻结）',
  `inviter_id` int(11) DEFAULT NULL COMMENT '推荐人id',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB  DEFAULT CHARSET=utf8 COMMENT='用户账户信息表';

-- ----------------------------
-- Records of t_vip_member
-- ----------------------------

-- ----------------------------
-- Table structure for `t_wechat_config`
-- ----------------------------
DROP TABLE IF EXISTS `t_wechat_config`;
CREATE TABLE `t_wechat_config` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `app_id` varchar(50) NOT NULL,
  `app_secret` varchar(50) NOT NULL,
  `status` int(4) NOT NULL DEFAULT '1' COMMENT '0不可用 1可用',
  `desc` varchar(255) DEFAULT NULL COMMENT '功能描述',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB  DEFAULT CHARSET=utf8 COMMENT='微信公众号配置信息表';

-- ----------------------------
-- Records of t_wechat_config
-- ----------------------------
INSERT INTO `t_wechat_config` VALUES (1, 'wx2753dbf09f4493d2', 'cdbfa28f9c0695c368d9b5e9f1f58835', '1','公众号测试');

-- ----------------------------
-- Table structure for `t_wechat_member`
-- ----------------------------
DROP TABLE IF EXISTS `t_wechat_member`;
CREATE TABLE `t_wechat_member` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `open_id` varchar(50) NOT NULL COMMENT '微信id',
  `nick_name` varchar(50) DEFAULT NULL COMMENT '微信昵称',
  `vip_id` int(11) DEFAULT NULL COMMENT '用户账户id',
  `head_img` varchar(255) DEFAULT NULL COMMENT '用户头像',
  `weconfig_id` int(11) NOT NULL  COMMENT '微信公众号配置表id',
  `syn_date` datetime DEFAULT NULL COMMENT '同步时间',
  `inviter_id` int(11) DEFAULT NULL  COMMENT '推荐人id',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB  DEFAULT CHARSET=utf8 COMMENT='微信账户信息表';

-- ----------------------------
-- Records of t_wechat_member
-- ----------------------------

DROP TABLE IF EXISTS `t_wechat_order`;
CREATE TABLE `t_wechat_order` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `orderId` varchar(255) NOT NULL COMMENT '支付订单号',
  `userId` int(11) NOT NULL COMMENT '用户id',
  `amount` decimal(18,2) NOT NULL COMMENT '交易金额，单位：元',
  `create_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `sign` int(2) NOT NULL DEFAULT '0' COMMENT '交易状态（0、默认值，1，成功，2，失败）',
  `type` int(2) NOT NULL COMMENT '交易类型（1、充值，2、提现）',
  PRIMARY KEY (`id`),
  KEY `t_wechat_orderId` (`orderId`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8  COMMENT='微信充值提现订单表';


DROP TABLE IF EXISTS `t_spread_info`;
CREATE TABLE `t_spread_info` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `open_id` varchar(50) NOT NULL COMMENT '微信id',
  `qrcode_url` varchar(1024) NOT NULL COMMENT '二维码图片地址',
  `ticket` varchar(256) NOT NULL COMMENT '二维码的ticket',
  `user_id` varchar(64) DEFAULT NULL COMMENT '创建人Id',
  `create_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='推广记录表';



-------start 2018.09.20
----- 重建立 bjl_draw_records
DROP TABLE IF EXISTS `bjl_draw_records`;
 CREATE TABLE `bjl_draw_records` (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `award_period` varchar(64) NOT NULL COMMENT '开奖期号（如：201808070001）',
  `draw_result` int(1) NOT NULL DEFAULT '0' COMMENT '中奖结果（0、未知；1、庄胜；2、闲胜）',
  `award_video` int(11) DEFAULT NULL COMMENT '开奖视频（关联开奖视频表主键）',
  `statistical_method` int(11) NOT NULL COMMENT '统计方式（0、前端请求统计，1、后端自动统计）',
  `remark` varchar(255) DEFAULT NULL COMMENT '备注',
  `start_order_time` timestamp NULL DEFAULT NULL COMMENT '开始下注时间（开始播放下注视频时间）',
  `end_order_time` timestamp NULL DEFAULT NULL COMMENT '截止下注时间',
  `start_wait_play_time` timestamp NULL DEFAULT NULL COMMENT '下注完成计算结果开始时间',
  `end_wait_play_time` timestamp NULL DEFAULT NULL COMMENT '下注完成计算结果结束时间',
  `start_play_time` timestamp NULL DEFAULT NULL COMMENT '开始播放发牌视频时间',
  `end_play_time` timestamp NULL DEFAULT NULL COMMENT '截止播放发牌视频时间',
  `start_show_result_time` timestamp NULL DEFAULT NULL COMMENT '开始显示当期开奖结果时间',
  `end_show_result_time` timestamp NULL DEFAULT NULL COMMENT '截止显示当期开奖结果时间',
  `pre_video_1` int(11) DEFAULT NULL COMMENT '预计预播视频_庄胜',
  `pre_video_2` int(11) DEFAULT NULL COMMENT '预计播放视频_闲胜',
  `status` int(11) DEFAULT '0' COMMENT '0:进行中 1：已经结束',
  `add_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` timestamp NULL DEFAULT CURRENT_TIMESTAMP COMMENT '更新时间',
  `del_flag` int(1) DEFAULT '0' COMMENT '删除标识 0：未删除 1：已经删除',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=8379 DEFAULT CHARSET=utf8 COMMENT='开奖结果记录表'

-----
DROP TABLE IF EXISTS `bjl_openprize_video`;
CREATE TABLE `bjl_openprize_video` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `total_play_times` int(11) NOT NULL DEFAULT '0' COMMENT '视频总时长 （单位秒）',
  `result_sign` int(11) NOT NULL COMMENT '开奖结果标记 （0、庄家胜；1、闲胜）',
  `status` int(11) NOT NULL DEFAULT '0' COMMENT '状态 （0、有效；1、无效）',
  `player_point` varchar(64) DEFAULT NULL COMMENT '闲家牌（多个用逗号分隔）',
  `banker_point` varchar(64) DEFAULT NULL COMMENT '庄家牌（多个用逗号分隔）',
  `link_adress` varchar(200) DEFAULT NULL COMMENT '播放地址',
  `add_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `remark` varchar(255) DEFAULT NULL COMMENT '备注',
  `order_times` int(11) DEFAULT '0' COMMENT '视频-投注段播放时长(单位秒)',
  `cal_order_times` int(11) DEFAULT '0' COMMENT '视频-下注完成计算开奖结果时长(单位秒)',
  `play_times` int(11) DEFAULT '0' COMMENT '视频-发牌段时长(单位秒)',
  `show_result_times` int(11) DEFAULT '0' COMMENT '视频-开奖结果显示时长',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=6 DEFAULT CHARSET=utf8 COMMENT='开奖视频登记表'


DROP TABLE IF EXISTS `bjl_order`;
CREATE TABLE `bjl_order` (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `vip_id` int(11) NOT NULL COMMENT '会员id',
  `periods` varchar(64) NOT NULL COMMENT '购买期数',
  `buy_amount` decimal(11,2) unsigned zerofill NOT NULL COMMENT '下注金额',
  `support_win` int(11) NOT NULL COMMENT '押注哪方赢  （0、庄家胜；1、闲胜）',
  `selected_size` int(11) NOT NULL COMMENT '下注方式（1、闲 ；2、闲对；3 、和；4、庄对；5、庄）',
  `video_serial` varchar(255) DEFAULT NULL COMMENT '视频名称',
  `final_result` int(10) unsigned NOT NULL DEFAULT '0' COMMENT ' 最终结果，是输还是赢 （0、初始值；1、赢；2、输）',
  `add_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `remark` varchar(255) DEFAULT NULL COMMENT '备注',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=22 DEFAULT CHARSET=utf8 COMMENT='用户下注百家乐订单表'

-------end 2018.09.20