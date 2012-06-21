# --- !Ups

alter table user_account add column validated boolean default false;
