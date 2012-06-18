insert into role (id, name) values (1, 'admin'); -- by default id==0 is admin group
insert into role (id, name) values (2, 'user');  -- by default id==1 is user's group
alter sequence role_seq restart with 3;

-- password "admin" :: sha256
insert into user_account (id, email, password_hash, role_id) values (1, 'admin@test.test', '8c6976e5b5410415bde908bd4dee15dfb167a9c873fc4bb8a81f6f2ab448a918', 1);
-- password "test" :: sha256
insert into user_account (id, email, password_hash, role_id) values (2, 'user@test.de', '9f86d081884c7d659a2feaa0c55ad015a3bf4f1b2b0b822cd15d6c15b0f00a08', 2);

alter sequence user_account_seq restart with 3;