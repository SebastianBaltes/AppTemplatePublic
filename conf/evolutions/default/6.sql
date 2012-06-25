# --- !Ups

alter table user_account alter column last_update DROP NOT NULL;
alter table user_account alter column last_update DROP DEFAULT;
alter table ding alter column last_update DROP NOT NULL;
alter table ding alter column last_update DROP DEFAULT;
alter table user_account add column zip_code varchar(255);
alter table user_account add column city varchar(255);
alter table user_account drop column street;
