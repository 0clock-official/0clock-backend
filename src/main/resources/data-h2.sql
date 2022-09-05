SET foreign_key_checks = 0;
INSERT INTO chattingTime VALUES
(1,'22:00:00'),
(2,'22:30:00'),
(3,'23:00:00'),
(4,'23:30:00'),
(5,'00:00:00'),
(6,'00:30:00'),
(7,'01:00:00'),
(8,'01:30:00'),
(9,'02:00:00');

INSERT INTO major VALUES
(1,'cs');
INSERT INTO `memberSex` VALUES
(1,'male'),
(2,'female');
INSERT INTO `matchingSex` VALUES
(1,'male'),
(2,'female'),
(3,'all');
SET foreign_key_checks = 1;
INSERT INTO member(email,password,chattingRoomId,chattingTime,memberSex,matchingSex,major,nickName,joinStep)
VALUES ('test@test.com','dasda',NULL,1,1,3,1,'test1',6);
INSERT INTO member(email,password,chattingRoomId,chattingTime,memberSex,matchingSex,major,nickName,joinStep)
VALUES('test2@test.com','dasda',NULL,1,2,3,1,'test2',6);
INSERT INTO member(email,password,chattingRoomId,chattingTime,memberSex,matchingSex,major,nickName,joinStep)
VALUES('test3@test.com','dasda',NULL,1,2,2,1,'test3',6);
INSERT INTO member(email,password,chattingRoomId,chattingTime,memberSex,matchingSex,major,nickName,joinStep)
VALUES('test4@test.com','dasda',NULL,1,1,1,1,'test4',6);
INSERT INTO member(email,password,chattingRoomId,chattingTime,memberSex,matchingSex,major,nickName,joinStep)
VALUES('test5@test.com','dasda',NULL,1,2,1,1,'test5',6);