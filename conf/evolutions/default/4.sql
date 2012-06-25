# --- !Ups

alter table user_account drop column role_id;
drop table role; 
alter table user_account add column role varchar(32) not null default 'user';
