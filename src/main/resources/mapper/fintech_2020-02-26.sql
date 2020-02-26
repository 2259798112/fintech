# ************************************************************
# Sequel Pro SQL dump
# Version 4541
#
# http://www.sequelpro.com/
# https://github.com/sequelpro/sequelpro
#
# Host: duwd.top (MySQL 5.7.21-0ubuntu0.16.04.1)
# Database: fintech
# Generation Time: 2020-02-26 09:54:01 +0000
# ************************************************************


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;


# Dump of table kdj_stock
# ------------------------------------------------------------

DROP TABLE IF EXISTS `kdj_stock`;

CREATE TABLE `kdj_stock` (
  `id` int(11) unsigned NOT NULL AUTO_INCREMENT,
  `k` double(20,5) NOT NULL,
  `d` double(20,5) NOT NULL,
  `j` double(20,5) NOT NULL,
  `open` double(20,5) NOT NULL,
  `high` double(20,5) NOT NULL,
  `low` double(20,5) NOT NULL,
  `close` double(20,5) NOT NULL,
  `data_date` datetime NOT NULL,
  `data_time` varchar(50) NOT NULL DEFAULT '',
  `stock_name` varchar(50) NOT NULL DEFAULT '',
  `stock_code` varchar(50) NOT NULL DEFAULT '',
  `create_date` datetime DEFAULT NULL,
  `update_date` datetime DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE KEY `code_date` (`data_time`,`stock_code`) USING BTREE,
  KEY `i_k` (`k`) USING BTREE,
  KEY `i_d` (`d`) USING BTREE,
  KEY `i_j` (`j`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 ROW_FORMAT=DYNAMIC;



# Dump of table kdj_stock_day
# ------------------------------------------------------------

DROP TABLE IF EXISTS `kdj_stock_day`;

CREATE TABLE `kdj_stock_day` (
  `id` int(11) unsigned NOT NULL AUTO_INCREMENT,
  `k` double(20,5) NOT NULL,
  `d` double(20,5) NOT NULL,
  `j` double(20,5) NOT NULL,
  `open` double(20,5) NOT NULL,
  `high` double(20,5) NOT NULL,
  `low` double(20,5) NOT NULL,
  `close` double(20,5) NOT NULL,
  `data_date` datetime NOT NULL,
  `data_time` varchar(50) NOT NULL DEFAULT '',
  `stock_name` varchar(50) NOT NULL DEFAULT '',
  `stock_code` varchar(50) NOT NULL DEFAULT '',
  `create_date` datetime DEFAULT NULL,
  `update_date` datetime DEFAULT NULL,
  `is_back` int(1) unsigned zerofill DEFAULT NULL,
  `create_day` varchar(50) NOT NULL,
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE KEY `code_date` (`data_time`,`stock_code`,`create_day`) USING BTREE,
  KEY `i_k` (`k`) USING BTREE,
  KEY `i_d` (`d`) USING BTREE,
  KEY `i_j` (`j`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 ROW_FORMAT=DYNAMIC;



# Dump of table stock_basic
# ------------------------------------------------------------

DROP TABLE IF EXISTS `stock_basic`;

CREATE TABLE `stock_basic` (
  `id` int(11) unsigned NOT NULL AUTO_INCREMENT,
  `ts_code` varchar(50) NOT NULL DEFAULT '',
  `symbol` varchar(50) NOT NULL DEFAULT '',
  `name` varchar(50) NOT NULL DEFAULT '',
  `area` varchar(50) NOT NULL DEFAULT '',
  `industry` varchar(50) NOT NULL DEFAULT '',
  `market` varchar(50) NOT NULL DEFAULT '',
  `list_date` varchar(50) NOT NULL DEFAULT '',
  `create_date` datetime NOT NULL,
  `update_date` datetime NOT NULL,
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE KEY `ts_code` (`ts_code`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 ROW_FORMAT=DYNAMIC;



# Dump of table stock_daily
# ------------------------------------------------------------

DROP TABLE IF EXISTS `stock_daily`;

CREATE TABLE `stock_daily` (
  `id` int(11) unsigned NOT NULL AUTO_INCREMENT,
  `ts_code` varchar(50) NOT NULL DEFAULT '',
  `chg` double(20,5) NOT NULL,
  `pct_chg` double(20,5) NOT NULL,
  `vol` double(20,5) NOT NULL,
  `amount` double(20,5) NOT NULL,
  `period` int(11) NOT NULL,
  `data_date` datetime NOT NULL,
  `data_time` varchar(50) NOT NULL DEFAULT '',
  `open` double(20,5) NOT NULL,
  `close` double(20,5) NOT NULL,
  `high` double(20,5) NOT NULL,
  `low` double(20,5) NOT NULL,
  `create_date` datetime NOT NULL,
  `update_date` datetime NOT NULL,
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE KEY `code_time` (`ts_code`,`data_time`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 ROW_FORMAT=DYNAMIC;



# Dump of table t_big_order
# ------------------------------------------------------------

DROP TABLE IF EXISTS `t_big_order`;

CREATE TABLE `t_big_order` (
  `id` varchar(64) NOT NULL DEFAULT '',
  `side` varchar(10) NOT NULL DEFAULT '',
  `price` double(20,2) NOT NULL,
  `size` double(20,2) NOT NULL,
  `ts` datetime NOT NULL,
  `plat` varchar(32) NOT NULL DEFAULT '',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;



# Dump of table t_config
# ------------------------------------------------------------

DROP TABLE IF EXISTS `t_config`;

CREATE TABLE `t_config` (
  `k` varchar(50) NOT NULL DEFAULT '',
  `v` text NOT NULL,
  PRIMARY KEY (`k`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;



# Dump of table zendesk
# ------------------------------------------------------------

DROP TABLE IF EXISTS `zendesk`;

CREATE TABLE `zendesk` (
  `id` varchar(100) NOT NULL DEFAULT '',
  `plat` varchar(50) NOT NULL DEFAULT '',
  `title` varchar(200) NOT NULL DEFAULT '',
  `link` varchar(1000) NOT NULL DEFAULT '',
  `create_date` datetime NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;




/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;
/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
