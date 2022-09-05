SET foreign_key_checks = 0;
drop TABLE if EXISTS chattingLog;
drop TABLE if EXISTS chattingRoom;
drop TABLE if EXISTS chattingTime;
drop TABLE if EXISTS emailCode;
drop TABLE if EXISTS major;
drop TABLE if EXISTS matchingSex;
drop TABLE if EXISTS member;
drop TABLE if EXISTS memberSex;

CREATE TABLE `chattingLog` (
  `id` bigint(20) unsigned NOT NULL AUTO_INCREMENT,
  `chattingRoomId` bigint(20) unsigned NOT NULL,
  `sendMember` int(11) unsigned NOT NULL,
  `receiveMember` int(11) unsigned NOT NULL,
  `chattingTime` datetime NOT NULL DEFAULT current_timestamp(),
  `message` longtext NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb4;
CREATE TABLE `chattingTime` (
  `id` tinyint(4) unsigned NOT NULL AUTO_INCREMENT,
  `startTime` time NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb4;
CREATE TABLE `chattingRoom` (
  `id` bigint(20) unsigned NOT NULL AUTO_INCREMENT,
  `chattingTime` tinyint(4) unsigned NOT NULL,
  `createTime` datetime NOT NULL DEFAULT current_timestamp() ON UPDATE current_timestamp(),
  `deleteTime` datetime DEFAULT NULL,
  `member1` int(11) unsigned NOT NULL,
  `member2` int(11) unsigned NOT NULL,
  PRIMARY KEY (`id`),
  KEY `chattingTime` (`chattingTime`),
  KEY `member1` (`member1`),
  KEY `member2` (`member2`),
  CONSTRAINT `chattingroom_ibfk_1` FOREIGN KEY (`chattingTime`) REFERENCES `chattingTime` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb4;
CREATE TABLE `emailCode` (
  `email` varchar(320) NOT NULL DEFAULT '',
  `code` int(11) NOT NULL,
  PRIMARY KEY (`email`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
CREATE TABLE `major` (
  `id` int(11) unsigned NOT NULL AUTO_INCREMENT,
  `value` varchar(64) NOT NULL DEFAULT '',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb4;
CREATE TABLE `matchingSex` (
  `id` tinyint(4) unsigned NOT NULL AUTO_INCREMENT,
  `value` varchar(32) NOT NULL DEFAULT '',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb4;
CREATE TABLE `memberSex` (
  `id` tinyint(4) unsigned NOT NULL AUTO_INCREMENT,
  `value` varchar(32) NOT NULL DEFAULT '',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb4;
CREATE TABLE `member` (
  `id` int(11) unsigned NOT NULL AUTO_INCREMENT,
  `email` varchar(320) NOT NULL DEFAULT '',
  `password` varchar(256) NOT NULL DEFAULT '',
  `chattingRoomId` bigint(20) unsigned DEFAULT NULL,
  `chattingTime` tinyint(4) unsigned NOT NULL,
  `memberSex` tinyint(4) unsigned NOT NULL,
  `matchingSex` tinyint(4) unsigned NOT NULL,
  `major` int(11) unsigned NOT NULL,
  `nickName` varchar(64) NOT NULL DEFAULT '',
  `joinStep` tinyint(4) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `email` (`email`),
  KEY `major` (`major`),
  KEY `chattingTimeNumber` (`chattingTime`),
  KEY `memberSex` (`memberSex`),
  KEY `matchingSex` (`matchingSex`),
  KEY `chattingRoomId` (`chattingRoomId`),
  CONSTRAINT `member_ibfk_1` FOREIGN KEY (`major`) REFERENCES `major` (`id`) ON UPDATE CASCADE,
  CONSTRAINT `member_ibfk_3` FOREIGN KEY (`chattingTime`) REFERENCES `chattingTime` (`id`),
--  CONSTRAINT `member_ibfk_4` FOREIGN KEY (`memberSex`) REFERENCES `matchingSex` (`id`),
  CONSTRAINT `member_ibfk_5` FOREIGN KEY (`matchingSex`) REFERENCES `matchingSex` (`id`),
  CONSTRAINT `member_ibfk_6` FOREIGN KEY (`chattingRoomId`) REFERENCES `chattingRoom` (`id`) ON DELETE SET NULL ON UPDATE SET NULL
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb4;
ALTER TABLE `chattingRoom` ADD CONSTRAINT `chattingroom_ibfk_2` FOREIGN KEY (`member1`) REFERENCES `member` (`id`);
ALTER TABLE `chattingRoom` ADD  CONSTRAINT `chattingroom_ibfk_3` FOREIGN KEY (`member2`) REFERENCES `member` (`id`);
ALTER TABLE `member` ADD CONSTRAINT `member_ibfk_4` FOREIGN KEY (`memberSex`) REFERENCES `matchingSex` (`id`);
--INSERT INTO chattingTime VALUES
--(1,'22:00:00'),
--(2,'22:30:00'),
--(3,'23:00:00'),
--(4,'23:30:00'),
--(5,'00:00:00'),
--(6,'00:30:00'),
--(7,'01:00:00'),
--(8,'01:30:00'),
--(9,'02:00:00');

--INSERT INTO major VALUES
--(1,'cs');
--INSERT INTO `memberSex` VALUES
--(1,'male'),
--(2,'female');
--INSERT INTO `matchingSex` VALUES
--(1,'male'),
--(2,'female'),
--(3,'all');
SET foreign_key_checks = 1;
--insert into member(email,password,chattingRoomId,chattingTime,memberSex,matchingSex,major,nickName,joinStep)
--values("test@test.com","dasda",NULL,1,1,3,1,"test1",6);
--insert into member(email,password,chattingRoomId,chattingTime,memberSex,matchingSex,major,nickName,joinStep)
--values("test2@test.com","dasda",NULL,1,2,3,1,"test2",6);
--insert into member(email,password,chattingRoomId,chattingTime,memberSex,matchingSex,major,nickName,joinStep)
--values("test3@test.com","dasda",NULL,1,2,2,1,"test3",6);
--insert into member(email,password,chattingRoomId,chattingTime,memberSex,matchingSex,major,nickName,joinStep)
--values("test4@test.com","dasda",NULL,1,1,1,1,"test4",6);
--insert into member(email,password,chattingRoomId,chattingTime,memberSex,matchingSex,major,nickName,joinStep)
--values("test5@test.com","dasda",NULL,1,2,1,1,"test5",6);