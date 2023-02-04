SET foreign_key_checks = 0;

drop TABLE if EXISTS chattingLog;
drop TABLE if EXISTS chattingRoom;
drop TABLE if EXISTS chattingTime;
drop TABLE if EXISTS major;
drop TABLE if EXISTS matchingSex;
drop TABLE if EXISTS member;
drop TABLE if EXISTS memberSex;
drop TABLE if EXISTS memberVerification;
drop TABLE if EXISTS refreshToken;
drop TABLE if EXISTS chattingTimeChangeLog;

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
CREATE TABLE `memberVerification` (
  `memberEmail` varchar(320),
  `verification` varchar(30),
  `cert` boolean default false,
  PRIMARY KEY (`memberEmail`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
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
  `isCert` tinyint(1) DEFAULT 2,
  `fcmToken` text DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `email` (`email`),
  KEY `major` (`major`),
  KEY `chattingTimeNumber` (`chattingTime`),
  KEY `memberSex` (`memberSex`),
  KEY `matchingSex` (`matchingSex`),
  KEY `chattingRoomId` (`chattingRoomId`),
  CONSTRAINT `member_ibfk_major` FOREIGN KEY (`major`) REFERENCES `major` (`id`) ON UPDATE CASCADE,
  CONSTRAINT `member_ibfk_chattingTime` FOREIGN KEY (`chattingTime`) REFERENCES `chattingTime` (`id`),
  CONSTRAINT `member_ibfk_memberSex` FOREIGN KEY (`memberSex`) REFERENCES `matchingSex` (`id`),
  CONSTRAINT `member_ibfk_matchingSex` FOREIGN KEY (`matchingSex`) REFERENCES `matchingSex` (`id`),
  CONSTRAINT `member_ibfk_chattingRoomId` FOREIGN KEY (`chattingRoomId`) REFERENCES `chattingRoom` (`id`) ON DELETE SET NULL ON UPDATE SET NULL,
  CONSTRAINT `member_ibfk_email` FOREIGN KEY (`email`) REFERENCES `memberVerification` (`memberEmail`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb4;


CREATE TABLE `refreshToken` (
  `id` int(11) unsigned,
  `refreshToken` varchar(320),
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
ALTER TABLE `refreshToken` ADD CONSTRAINT `refreshToken_ibfk_1` FOREIGN KEY (`id`) REFERENCES `member` (`id`);
ALTER TABLE `chattingRoom` ADD CONSTRAINT `chattingroom_ibfk_2` FOREIGN KEY (`member1`) REFERENCES `member` (`id`);
ALTER TABLE `chattingRoom` ADD  CONSTRAINT `chattingroom_ibfk_3` FOREIGN KEY (`member2`) REFERENCES `member` (`id`);

CREATE TABLE `chattingTimeChangeLog` (
  `id` int(11) unsigned NOT NULL AUTO_INCREMENT,
  `chattingTime` tinyint(4) unsigned NOT NULL,
  `chattingRoomId` bigint(20) unsigned NOT NULL,
  `accept` tinyint(1) NOT NULL DEFAULT 0,
  PRIMARY KEY (`id`),
  KEY `chattingTime` (`chattingTime`),
  KEY `chattingRoomId` (`chattingRoomId`),
  CONSTRAINT `chattingtimechangelog_ibfk_chattingTime` FOREIGN KEY (`chattingTime`) REFERENCES `chattingTime` (`id`),
  CONSTRAINT `chattingtimechangelog_ibfk_chattingRoom` FOREIGN KEY (`chattingRoomId`) REFERENCES `chattingRoom` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;