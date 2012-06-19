insert into role (id, name) values (1, 'admin'); -- by default id==0 is admin group
insert into role (id, name) values (2, 'user');  -- by default id==1 is user's group
alter sequence role_seq restart with 3;

