-- MariaDB dump 10.19  Distrib 10.8.3-MariaDB, for osx10.17 (arm64)
--
-- Host: localhost    Database: oclock
-- ------------------------------------------------------
-- Server version	10.8.3-MariaDB

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8mb4 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

--
-- Table structure for table `chattingLog`
--

DROP TABLE IF EXISTS `chattingLog`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `chattingLog` (
  `id` int(11) unsigned NOT NULL AUTO_INCREMENT,
  `chattingRoomId` bigint(20) unsigned NOT NULL,
  `sendMember` int(11) unsigned NOT NULL,
  `receiveMember` int(11) unsigned NOT NULL,
  `chattingTime` datetime NOT NULL,
  PRIMARY KEY (`id`),
  KEY `sendMember` (`sendMember`),
  KEY `receiveMember` (`receiveMember`),
  KEY `chattingRoomId` (`chattingRoomId`),
  CONSTRAINT `chattinglog_ibfk_1` FOREIGN KEY (`sendMember`) REFERENCES `member` (`id`),
  CONSTRAINT `chattinglog_ibfk_2` FOREIGN KEY (`receiveMember`) REFERENCES `member` (`id`),
  CONSTRAINT `chattinglog_ibfk_3` FOREIGN KEY (`chattingRoomId`) REFERENCES `chattingRoom` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `chattingLog`
--

LOCK TABLES `chattingLog` WRITE;
/*!40000 ALTER TABLE `chattingLog` DISABLE KEYS */;
/*!40000 ALTER TABLE `chattingLog` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `chattingRoom`
--

DROP TABLE IF EXISTS `chattingRoom`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `chattingRoom` (
  `id` bigint(20) unsigned NOT NULL AUTO_INCREMENT,
  `chattingTime` tinyint(4) unsigned NOT NULL,
  `createTime` datetime NOT NULL DEFAULT '0000-00-00 00:00:00' ON UPDATE current_timestamp(),
  `deleteTime` datetime DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `chattingTime` (`chattingTime`),
  CONSTRAINT `chattingroom_ibfk_1` FOREIGN KEY (`chattingTime`) REFERENCES `chattingTime` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `chattingRoom`
--

LOCK TABLES `chattingRoom` WRITE;
/*!40000 ALTER TABLE `chattingRoom` DISABLE KEYS */;
/*!40000 ALTER TABLE `chattingRoom` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `chattingTime`
--

DROP TABLE IF EXISTS `chattingTime`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `chattingTime` (
  `id` tinyint(4) unsigned NOT NULL AUTO_INCREMENT,
  `startTime` time NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `chattingTime`
--

LOCK TABLES `chattingTime` WRITE;
/*!40000 ALTER TABLE `chattingTime` DISABLE KEYS */;
/*!40000 ALTER TABLE `chattingTime` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `major`
--

DROP TABLE IF EXISTS `major`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `major` (
  `id` int(11) unsigned NOT NULL AUTO_INCREMENT,
  `value` varchar(64) NOT NULL DEFAULT '',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `major`
--

LOCK TABLES `major` WRITE;
/*!40000 ALTER TABLE `major` DISABLE KEYS */;
/*!40000 ALTER TABLE `major` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `matchingSex`
--

DROP TABLE IF EXISTS `matchingSex`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `matchingSex` (
  `id` tinyint(4) unsigned NOT NULL AUTO_INCREMENT,
  `value` varchar(32) NOT NULL DEFAULT '',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `matchingSex`
--

LOCK TABLES `matchingSex` WRITE;
/*!40000 ALTER TABLE `matchingSex` DISABLE KEYS */;
/*!40000 ALTER TABLE `matchingSex` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `member`
--

DROP TABLE IF EXISTS `member`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `member` (
  `id` int(11) unsigned NOT NULL AUTO_INCREMENT,
  `email` varchar(320) NOT NULL DEFAULT '',
  `password` varchar(256) NOT NULL DEFAULT '',
  `chattingRoomId` bigint(20) unsigned DEFAULT NULL,
  `chattingTimeNumber` tinyint(4) unsigned NOT NULL,
  `memberSex` tinyint(4) unsigned NOT NULL,
  `matchingSex` tinyint(4) unsigned NOT NULL,
  `major` int(11) unsigned NOT NULL,
  `salt` varchar(32) NOT NULL DEFAULT '',
  `nickName` varchar(64) NOT NULL DEFAULT '',
  `joinStep` tinyint(4) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `email` (`email`),
  KEY `major` (`major`),
  KEY `chattingRoomId` (`chattingRoomId`),
  KEY `chattingTimeNumber` (`chattingTimeNumber`),
  KEY `memberSex` (`memberSex`),
  KEY `matchingSex` (`matchingSex`),
  CONSTRAINT `member_ibfk_1` FOREIGN KEY (`major`) REFERENCES `major` (`id`) ON UPDATE CASCADE,
  CONSTRAINT `member_ibfk_2` FOREIGN KEY (`chattingRoomId`) REFERENCES `chattingRoom` (`id`),
  CONSTRAINT `member_ibfk_3` FOREIGN KEY (`chattingTimeNumber`) REFERENCES `chattingTime` (`id`),
  CONSTRAINT `member_ibfk_4` FOREIGN KEY (`memberSex`) REFERENCES `matchingSex` (`id`),
  CONSTRAINT `member_ibfk_5` FOREIGN KEY (`matchingSex`) REFERENCES `matchingSex` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `member`
--

LOCK TABLES `member` WRITE;
/*!40000 ALTER TABLE `member` DISABLE KEYS */;
/*!40000 ALTER TABLE `member` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `memberSex`
--

DROP TABLE IF EXISTS `memberSex`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `memberSex` (
  `id` tinyint(4) unsigned NOT NULL AUTO_INCREMENT,
  `value` varchar(32) NOT NULL DEFAULT '',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `memberSex`
--

LOCK TABLES `memberSex` WRITE;
/*!40000 ALTER TABLE `memberSex` DISABLE KEYS */;
/*!40000 ALTER TABLE `memberSex` ENABLE KEYS */;
UNLOCK TABLES;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2022-07-10 19:35:17
