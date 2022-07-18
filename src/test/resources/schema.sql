SET foreign_key_checks = 0;
TRUNCATE TABLE chattingLog;
TRUNCATE TABLE chattingRoom;
TRUNCATE TABLE member;
SET foreign_key_checks = 1;
insert into member(email,password,chattingRoomId,chattingTime,memberSex,matchingSex,major,nickName,joinStep)
values("test@test.com","dasda",NULL,1,1,3,1,"test1",6);
insert into member(email,password,chattingRoomId,chattingTime,memberSex,matchingSex,major,nickName,joinStep)
values("test2@test.com","dasda",NULL,1,2,3,1,"test2",6);
insert into member(email,password,chattingRoomId,chattingTime,memberSex,matchingSex,major,nickName,joinStep)
values("test3@test.com","dasda",NULL,1,2,2,1,"test3",6);
insert into member(email,password,chattingRoomId,chattingTime,memberSex,matchingSex,major,nickName,joinStep)
values("test4@test.com","dasda",NULL,1,1,1,1,"test4",6);
insert into member(email,password,chattingRoomId,chattingTime,memberSex,matchingSex,major,nickName,joinStep)
values("test5@test.com","dasda",NULL,1,2,1,1,"test5",6);