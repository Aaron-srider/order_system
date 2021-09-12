CREATE DATABASE  IF NOT EXISTS `order_system` /*!40100 DEFAULT CHARACTER SET latin1 */;
USE `order_system`;
-- MySQL dump 10.13  Distrib 5.6.17, for Win64 (x86_64)
--
-- Host: 127.0.0.1    Database: order_system
-- ------------------------------------------------------
-- Server version	5.7.14

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

--
-- Table structure for table `approval_record`
--

DROP TABLE IF EXISTS `approval_record`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `approval_record` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `create_time` datetime DEFAULT NULL,
  `update_time` datetime DEFAULT NULL,
  `deleted` int(11) DEFAULT NULL,
  `approval_datetime` datetime DEFAULT NULL,
  `comment` varchar(255) DEFAULT NULL,
  `work_order_id` int(11) DEFAULT NULL,
  `flow_node_id` int(11) DEFAULT NULL,
  `approver_id` int(11) DEFAULT NULL COMMENT '审批人id',
  `operation` int(11) DEFAULT NULL COMMENT '审批操作，0通过，1不通过',
  PRIMARY KEY (`id`),
  UNIQUE KEY `id_UNIQUE` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=13 DEFAULT CHARSET=utf8 COMMENT='审批记录表，记录一次审批的状态';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `approval_record`
--

LOCK TABLES `approval_record` WRITE;
/*!40000 ALTER TABLE `approval_record` DISABLE KEYS */;
INSERT INTO `approval_record` VALUES (1,'2021-08-26 18:25:40',NULL,0,'2021-08-26 18:25:40','good job',9,1,1,0),(2,'2021-08-26 18:25:42',NULL,0,'2021-08-26 18:25:42','good job',9,2,1,0),(3,'2021-08-27 16:07:40',NULL,0,'2021-08-27 16:07:40','get your staff',9,3,1,1),(4,'2021-09-05 17:05:58',NULL,0,'2021-09-05 17:05:55','test pass',11,1,1,0),(5,'2021-09-05 17:06:50',NULL,0,'2021-09-05 17:06:50','test pass',11,2,1,0),(6,'2021-09-05 17:12:17',NULL,0,'2021-09-05 17:12:16','test pass',11,3,1,0),(7,'2021-09-10 16:03:48',NULL,0,'2021-09-10 16:03:48','test pass',13,1,2,0),(8,'2021-09-10 16:05:52',NULL,0,'2021-09-10 16:05:52','test pass',13,2,2,0),(9,'2021-09-10 16:07:10',NULL,0,'2021-09-10 16:07:10','test pass',13,3,3,0),(10,'2021-09-10 16:39:57',NULL,0,'2021-09-10 16:39:57','test pass',1,1,2,0),(11,'2021-09-10 16:40:01',NULL,0,'2021-09-10 16:40:01','test pass',1,2,2,0),(12,'2021-09-10 16:40:52',NULL,0,'2021-09-10 16:40:52','reject to pass',1,3,3,1);
/*!40000 ALTER TABLE `approval_record` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `class`
--

DROP TABLE IF EXISTS `class`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `class` (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '班级唯一标识id',
  `name` varchar(45) DEFAULT NULL COMMENT '班级名称',
  `major_id` varchar(45) DEFAULT NULL COMMENT '班级所属专业Id',
  PRIMARY KEY (`id`),
  UNIQUE KEY `id_UNIQUE` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8 COMMENT='班级表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `class`
--

LOCK TABLES `class` WRITE;
/*!40000 ALTER TABLE `class` DISABLE KEYS */;
INSERT INTO `class` VALUES (1,'软工1902','1');
/*!40000 ALTER TABLE `class` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `college`
--

DROP TABLE IF EXISTS `college`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `college` (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '学院唯一标识id',
  `name` varchar(45) DEFAULT NULL COMMENT '学院名称',
  PRIMARY KEY (`id`),
  UNIQUE KEY `id_UNIQUE` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8 COMMENT='学院表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `college`
--

LOCK TABLES `college` WRITE;
/*!40000 ALTER TABLE `college` DISABLE KEYS */;
INSERT INTO `college` VALUES (1,'计算机学院');
/*!40000 ALTER TABLE `college` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `flow`
--

DROP TABLE IF EXISTS `flow`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `flow` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `create_time` datetime DEFAULT NULL,
  `update_time` datetime DEFAULT NULL,
  `description` text COMMENT '流程描述',
  `name` varchar(50) DEFAULT NULL COMMENT '流程名称',
  `deleted` int(11) DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `id_UNIQUE` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8 COMMENT='流程表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `flow`
--

LOCK TABLES `flow` WRITE;
/*!40000 ALTER TABLE `flow` DISABLE KEYS */;
INSERT INTO `flow` VALUES (1,'2021-08-16 22:11:33','2021-08-16 22:11:33','test flow','申请GPU',0);
/*!40000 ALTER TABLE `flow` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `flow_node`
--

DROP TABLE IF EXISTS `flow_node`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `flow_node` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `create_time` datetime DEFAULT NULL,
  `update_time` datetime DEFAULT NULL,
  `flow_id` int(11) DEFAULT NULL COMMENT '审批节点对应的审批流程id',
  `approver_id` int(11) DEFAULT NULL COMMENT '审批者id',
  `node_order` int(11) DEFAULT NULL COMMENT '审批节点顺序（从0开始）',
  `deleted` int(11) DEFAULT NULL,
  `next_id` int(11) DEFAULT NULL COMMENT '流程中下一个节点的id，最后一个节点的next_id字段为null',
  PRIMARY KEY (`id`),
  UNIQUE KEY `id_UNIQUE` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8 COMMENT='审批节点';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `flow_node`
--

LOCK TABLES `flow_node` WRITE;
/*!40000 ALTER TABLE `flow_node` DISABLE KEYS */;
INSERT INTO `flow_node` VALUES (1,'2021-08-16 22:11:33','2021-08-16 22:11:33',1,2,0,0,2),(2,'2021-08-16 22:11:33','2021-08-16 22:11:33',1,2,1,0,3),(3,'2021-08-16 22:11:33','2021-08-16 22:11:33',1,3,2,0,NULL);
/*!40000 ALTER TABLE `flow_node` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `major`
--

DROP TABLE IF EXISTS `major`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `major` (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '专业唯一标识id',
  `name` varchar(45) DEFAULT NULL COMMENT '专业名称',
  `college_id` varchar(45) DEFAULT NULL COMMENT '专业所属学院id',
  PRIMARY KEY (`id`),
  UNIQUE KEY `id_UNIQUE` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8 COMMENT='专业表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `major`
--

LOCK TABLES `major` WRITE;
/*!40000 ALTER TABLE `major` DISABLE KEYS */;
INSERT INTO `major` VALUES (1,'软件工程','1');
/*!40000 ALTER TABLE `major` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `permission`
--

DROP TABLE IF EXISTS `permission`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `permission` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `name` varchar(128) DEFAULT NULL COMMENT '资源名称',
  `type` varchar(32) DEFAULT NULL COMMENT '资源类型：menu,button,',
  `url` varchar(128) DEFAULT NULL COMMENT '访问url地址',
  `percode` varchar(128) DEFAULT NULL COMMENT '权限代码字符串',
  `parent_id` bigint(20) DEFAULT NULL COMMENT '父结点id',
  `parentids` varchar(128) DEFAULT NULL COMMENT '父结点id列表串',
  `order` int(11) DEFAULT NULL,
  `create_time` datetime DEFAULT NULL,
  `update_time` datetime DEFAULT NULL,
  `deleted` int(11) DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `id_UNIQUE` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=36 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `permission`
--

LOCK TABLES `permission` WRITE;
/*!40000 ALTER TABLE `permission` DISABLE KEYS */;
INSERT INTO `permission` VALUES (1,'权限','','',NULL,0,'0/',NULL,NULL,NULL,0),(11,'工单管理','menu','',NULL,1,'0/1/',1,NULL,NULL,0),(12,'工单新增','permission','post /workOrder','workOrder:submission',11,'0/1/11/',NULL,NULL,NULL,0),(15,'工单查询','permission','get /workOrders','workOrder:query',11,'0/1/11/',NULL,NULL,NULL,0),(26,'工单附件上传','permission','put /workOrder/attachment/*','workOrder:attachmentUpload',11,'0/1/11/',NULL,NULL,NULL,0),(27,'工单附件下载','permission','get /workOrder/attachment/*','workOrder:attachmentDownload',11,'0/1/11/',NULL,NULL,NULL,0),(28,'查看工单历史','permission','get /workOrder/histories','workOrder:history:query',11,'0/1/11/',NULL,NULL,NULL,0),(29,'审批管理','menu',NULL,NULL,1,'0/1/',2,NULL,NULL,0),(30,'审批通过','permission','put /approval/pass','approval:pass',29,'0/1/29/',NULL,NULL,NULL,0),(31,'审批不通过','permission','put /approval/reject','approval:reject',29,'0/1/29/',NULL,NULL,NULL,0),(32,'撤回工单','permission','put /workOrder/revoke','workOrder:revoke',11,'0/1/11/',NULL,NULL,NULL,0),(33,'查看待审批工单','permission','get /approval/workOrders','approval:workOrder:query',29,'0/1/29/',NULL,NULL,NULL,0),(34,'查看工单详情','permission','get /workOrder/detail','workOrder:detail',11,'0/1/11/',NULL,NULL,NULL,0),(35,'查看历史工单详情','permission','get /workOrder/history/detail','workOrder:history:detail',11,'0/1/11/',NULL,NULL,NULL,0);
/*!40000 ALTER TABLE `permission` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `role`
--

DROP TABLE IF EXISTS `role`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `role` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `name` varchar(45) DEFAULT NULL,
  `create_time` datetime DEFAULT NULL,
  `update_time` datetime DEFAULT NULL,
  `deleted` int(11) DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `id_UNIQUE` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=8 DEFAULT CHARSET=utf8 COMMENT='用户角色';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `role`
--

LOCK TABLES `role` WRITE;
/*!40000 ALTER TABLE `role` DISABLE KEYS */;
INSERT INTO `role` VALUES (1,'ADMIN',NULL,NULL,0),(2,'OPERATOR',NULL,NULL,0),(3,'COLLEGE_LEVEL_LEADER',NULL,NULL,0),(4,'DEPT_LEVEL_LEADER',NULL,NULL,0),(5,'TEACHER',NULL,NULL,0),(6,'UNDERGRADUATE',NULL,NULL,0),(7,'POSTGRADUATE',NULL,NULL,0);
/*!40000 ALTER TABLE `role` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `role_flow`
--

DROP TABLE IF EXISTS `role_flow`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `role_flow` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `role_id` int(11) DEFAULT NULL,
  `flow_id` int(11) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `role_flow`
--

LOCK TABLES `role_flow` WRITE;
/*!40000 ALTER TABLE `role_flow` DISABLE KEYS */;
INSERT INTO `role_flow` VALUES (1,6,1);
/*!40000 ALTER TABLE `role_flow` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `role_permission`
--

DROP TABLE IF EXISTS `role_permission`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `role_permission` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `role_id` int(11) DEFAULT NULL COMMENT '角色id',
  `permission_id` int(11) DEFAULT NULL COMMENT '权限id',
  `create_time` datetime DEFAULT NULL,
  `update_time` datetime DEFAULT NULL,
  `deleted` int(11) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=14 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `role_permission`
--

LOCK TABLES `role_permission` WRITE;
/*!40000 ALTER TABLE `role_permission` DISABLE KEYS */;
INSERT INTO `role_permission` VALUES (1,6,12,NULL,NULL,0),(2,6,15,NULL,NULL,0),(3,6,28,NULL,NULL,0),(4,6,30,NULL,NULL,0),(5,6,31,NULL,NULL,0),(6,6,32,NULL,NULL,0),(7,3,33,NULL,NULL,0),(8,6,34,NULL,NULL,0),(9,6,35,NULL,NULL,0),(10,6,26,NULL,NULL,0),(11,6,27,NULL,NULL,0),(12,3,30,NULL,NULL,0),(13,3,31,NULL,NULL,0);
/*!40000 ALTER TABLE `role_permission` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `secondary_dept`
--

DROP TABLE IF EXISTS `secondary_dept`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `secondary_dept` (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '二级部门唯一标识id',
  `name` varchar(45) DEFAULT NULL COMMENT '二级部门名称',
  `college_id` varchar(45) DEFAULT NULL COMMENT '所属学院Id',
  PRIMARY KEY (`id`),
  UNIQUE KEY `id_UNIQUE` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8 COMMENT='二级部门表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `secondary_dept`
--

LOCK TABLES `secondary_dept` WRITE;
/*!40000 ALTER TABLE `secondary_dept` DISABLE KEYS */;
INSERT INTO `secondary_dept` VALUES (1,'计算机系','1');
/*!40000 ALTER TABLE `secondary_dept` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `user`
--

DROP TABLE IF EXISTS `user`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `user` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '用户表主键',
  `open_id` varchar(28) DEFAULT NULL COMMENT '微信小程序唯一用户标识',
  `session_key` varchar(255) DEFAULT NULL COMMENT '微信给私人服务器颁发的session密钥，用于解密用户私密信息，有时限',
  `name` varchar(255) DEFAULT NULL COMMENT '用户真实姓名，需要用户填写',
  `college_id` bigint(20) DEFAULT NULL COMMENT '学院Id',
  `major_id` bigint(20) DEFAULT NULL COMMENT '专业Id',
  `secondary_dept_id` bigint(20) DEFAULT NULL COMMENT '二级部门Id，教师类专有',
  `class_id` int(11) DEFAULT NULL COMMENT '班级号，学生专有',
  `grade` int(11) DEFAULT NULL COMMENT '年级号，学生专有，本科生0~5，研究生0~2',
  `gender` bit(1) DEFAULT NULL COMMENT '用户性别，从微信信息获取',
  `avatar_url` varchar(255) DEFAULT NULL COMMENT '用户头像地址，从微信信息获取',
  `nick_name` varchar(255) DEFAULT NULL COMMENT '用户昵称，从微信信息获取',
  `student_id` varchar(45) DEFAULT NULL COMMENT '学号，学生类专有',
  `info_complete` int(11) DEFAULT '0',
  `job_id` varchar(45) DEFAULT NULL COMMENT '工号，教师类专有',
  `deleted` int(11) DEFAULT NULL,
  `create_time` datetime DEFAULT NULL,
  `update_time` datetime DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `id_UNIQUE` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=8 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `user`
--

LOCK TABLES `user` WRITE;
/*!40000 ALTER TABLE `user` DISABLE KEYS */;
INSERT INTO `user` VALUES (1,'ofVnE4tSuvWvRiI2OnSra1kdh-5E','3zwZbNdXM44bvKiv62jB7w==','文超',1,1,NULL,1,1,NULL,NULL,NULL,'2019012617',1,NULL,0,'2021-08-16 22:11:33','2021-08-17 16:11:24'),(2,'lajsljf;las;d;kfjk5E','3zwZbvKiv62jw==','审批者1',1,NULL,1,NULL,NULL,NULL,NULL,NULL,NULL,1,'2830947298',0,'2021-08-16 22:11:33','2021-08-17 16:11:24'),(3,'al;sdjfiwnfnoas','auoienfasdhf9apowe','审批者2',1,NULL,1,NULL,NULL,NULL,NULL,NULL,NULL,1,'23308724298',0,'2021-08-16 22:11:33','2021-08-16 22:11:33'),(4,'ajsponfalsd;alsdf','alsdjfownfiaskjghad','李某',1,1,NULL,1,1,NULL,NULL,NULL,'2018092873',1,NULL,0,'2021-08-16 22:11:33','2021-08-16 22:11:33'),(5,'joawnf;lksd','ouhfklajjskd','张三',1,NULL,1,NULL,NULL,NULL,NULL,NULL,NULL,1,'2019010433',0,'2021-08-16 22:11:33','2021-09-06 20:46:40'),(6,'hjawofhasjdf','hjawofhasjdf','王五 ',1,NULL,1,NULL,NULL,NULL,NULL,NULL,NULL,1,'9237495027',0,'2021-08-16 22:11:33','2021-09-10 14:59:47'),(7,'a,shdfw;nfai','fhluwusbfalfui','王五 ',1,NULL,1,NULL,NULL,NULL,NULL,NULL,NULL,1,'9237495027',0,NULL,'2021-09-10 09:49:42');
/*!40000 ALTER TABLE `user` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `user_role`
--

DROP TABLE IF EXISTS `user_role`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `user_role` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `user_id` int(11) DEFAULT NULL,
  `role_id` int(11) DEFAULT NULL,
  `create_time` datetime DEFAULT NULL,
  `update_time` datetime DEFAULT NULL,
  `deleted` int(11) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=9 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `user_role`
--

LOCK TABLES `user_role` WRITE;
/*!40000 ALTER TABLE `user_role` DISABLE KEYS */;
INSERT INTO `user_role` VALUES (1,1,6,NULL,NULL,0),(2,5,3,'2021-09-06 20:46:40',NULL,0),(3,2,3,'2021-09-06 20:46:40',NULL,0),(4,3,3,'2021-09-06 20:46:40',NULL,0),(5,6,6,'2021-09-07 17:28:49',NULL,0),(6,7,3,'2021-09-10 09:50:12',NULL,0),(7,6,3,'2021-09-10 14:59:23',NULL,0),(8,6,3,'2021-09-10 14:59:47',NULL,0);
/*!40000 ALTER TABLE `user_role` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `work_order`
--

DROP TABLE IF EXISTS `work_order`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `work_order` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `is_examined` int(11) DEFAULT NULL COMMENT '标识工单是否已经被审批，初始值为0，0为未审批，1为审批',
  `initiator_id` int(11) DEFAULT NULL,
  `flow_id` int(11) DEFAULT NULL COMMENT '工单对应的流程id',
  `create_time` datetime DEFAULT NULL,
  `update_time` datetime DEFAULT NULL,
  `attachment` mediumblob COMMENT '工单携带的附件，最大16MB',
  `status` int(11) DEFAULT NULL COMMENT '工单对应的状态，0123分别表示：在审，顺利通过，不通过，被撤销',
  `content` text COMMENT '存储工单的描述信息，该信息由申请人填写，比如申请资源使用途径，资源使用时长等',
  `deleted` int(11) DEFAULT NULL,
  `title` varchar(50) DEFAULT NULL,
  `attachment_name` varchar(50) DEFAULT NULL COMMENT '附件名',
  `flow_node_id` int(11) DEFAULT NULL,
  `is_finished` int(11) DEFAULT NULL COMMENT '标识工单是否结束，1结束0未结束',
  PRIMARY KEY (`id`),
  UNIQUE KEY `id_UNIQUE` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=21 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `work_order`
--

LOCK TABLES `work_order` WRITE;
/*!40000 ALTER TABLE `work_order` DISABLE KEYS */;
INSERT INTO `work_order` VALUES (1,1,1,1,'2021-08-16 22:11:33','2021-09-10 16:40:52',NULL,2,'申请GPU',0,'测试工单1',NULL,3,1),(2,0,1,1,'2021-08-16 22:11:33','2021-08-16 22:11:33',NULL,0,'申请GPU',0,'测试工单2',NULL,1,0),(3,0,1,1,'2021-08-16 22:11:33','2021-09-11 17:43:07',NULL,0,'申请GPU',0,'测试工单3',NULL,1,0),(4,0,1,1,'2021-08-16 22:11:33','2021-08-23 21:21:56',NULL,0,'申请GPU',0,'测试工单4',NULL,1,0),(5,0,1,1,'2021-08-16 22:11:33','2021-08-16 22:11:33',NULL,0,'申请GPU',0,'测试工单5',NULL,1,0),(7,0,1,1,'2021-08-24 20:59:42','2021-08-24 21:08:42',NULL,0,'申请xxx型号GPU使用1周',0,'申请GPU',NULL,1,0),(9,1,1,1,'2021-08-26 16:04:19','2021-08-27 16:07:40',NULL,2,'申请xxx型号GPU使用2周',0,'申请GPU2',NULL,3,1),(11,1,1,1,'2021-09-05 17:05:27','2021-09-05 17:12:24',NULL,1,'测试pass接口',0,'测试pass接口',NULL,3,1),(12,0,1,1,'2021-09-10 15:27:49','2021-09-10 15:36:10',NULL,3,'测试整个工作流程',0,'测试整个工作流程',NULL,1,1),(13,1,1,1,'2021-09-10 15:58:18','2021-09-10 22:45:44',NULL,1,'测试整个工作流程1',0,'测试整个工作流程1',NULL,3,1),(14,0,1,1,'2021-09-10 21:04:20',NULL,NULL,0,'可还行',0,'hello wolrd',NULL,1,0);
/*!40000 ALTER TABLE `work_order` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `work_order_history`
--

DROP TABLE IF EXISTS `work_order_history`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `work_order_history` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `initiator_id` int(11) DEFAULT NULL,
  `flow_id` int(11) DEFAULT NULL,
  `create_time` datetime DEFAULT NULL,
  `update_time` datetime DEFAULT NULL,
  `attachment` mediumblob,
  `attachment_name` varchar(50) DEFAULT NULL,
  `title` varchar(50) DEFAULT NULL,
  `status` int(11) DEFAULT NULL,
  `content` text,
  `deleted` int(11) DEFAULT NULL,
  `work_order_id` int(11) DEFAULT NULL,
  `flow_node_id` int(11) DEFAULT NULL COMMENT '记录工单结束前的审批节点id',
  PRIMARY KEY (`id`),
  UNIQUE KEY `idwork_order_history_UNIQUE` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=6 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `work_order_history`
--

LOCK TABLES `work_order_history` WRITE;
/*!40000 ALTER TABLE `work_order_history` DISABLE KEYS */;
INSERT INTO `work_order_history` VALUES (1,1,1,'2021-08-27 16:07:40','2021-08-27 16:07:40',NULL,NULL,'申请GPU2',2,'申请xxx型号GPU使用2周',0,NULL,NULL),(2,1,1,'2021-09-05 17:12:47','2021-09-05 17:12:24',NULL,NULL,'测试pass接口',1,'测试pass接口',0,11,3),(3,1,1,'2021-09-10 15:36:11','2021-09-10 15:36:10',NULL,NULL,'测试整个工作流程',3,'测试整个工作流程',0,12,1),(4,1,1,'2021-09-10 16:07:10','2021-09-10 16:07:10',NULL,NULL,'测试整个工作流程1',1,'测试整个工作流程1',0,13,3),(5,1,1,'2021-09-10 16:40:52','2021-09-10 16:40:52',NULL,NULL,'测试工单1',2,'申请GPU',0,1,3);
/*!40000 ALTER TABLE `work_order_history` ENABLE KEYS */;
UNLOCK TABLES;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2021-09-12 12:00:57
