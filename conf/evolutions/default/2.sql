# --- !Ups

alter table user_account add column random_pwrecover varchar(255);
alter table user_account add column pwrecover_triggered timestamp;

