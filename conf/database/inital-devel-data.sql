-- password "admin" :: sha256
insert into user_account (id, email, password_hash, role, validated, last_update, fix_random_number) values (1, 'admin@test.test', '8c6976e5b5410415bde908bd4dee15dfb167a9c873fc4bb8a81f6f2ab448a918', 'admin', true, '2012-06-04 00:00:00', 0.8496757966);
-- password "test" :: sha256
insert into user_account (id, email, password_hash, role, validated, last_update, fix_random_number) values (2, 'user@test.test', '9f86d081884c7d659a2feaa0c55ad015a3bf4f1b2b0b822cd15d6c15b0f00a08', 'user', true, '2012-06-04 00:00:00', 0.3865549753);

alter sequence user_account_seq restart with 3;

