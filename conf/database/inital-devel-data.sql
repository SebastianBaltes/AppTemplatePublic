-- password "admin" :: sha256
insert into user_account (id, email, password_hash, role, validated) values (1, 'admin@test.test', '8c6976e5b5410415bde908bd4dee15dfb167a9c873fc4bb8a81f6f2ab448a918', 'admin', true);
-- password "test" :: sha256
insert into user_account (id, email, password_hash, role, validated) values (2, 'user@test.test', '9f86d081884c7d659a2feaa0c55ad015a3bf4f1b2b0b822cd15d6c15b0f00a08', 'user', true);

alter sequence user_account_seq restart with 3;