# --- Created by Ebean DDL
# To stop Ebean DDL generation, remove this comment and start using Evolutions

# --- !Ups

create table db_image (
  id                        bigint not null,
  filename                  varchar(255),
  label                     varchar(255),
  image                     bytea,
  width                     integer,
  height                    integer,
  mimetype                  varchar(255),
  thumbnail                 bytea,
  thumbnail_width           integer,
  thumbnail_height          integer,
  thumbnail_mimetype        varchar(255),
  last_update               timestamp not null,
  constraint pk_db_image primary key (id))
;

create table ding (
  id                        bigint not null,
  name                      varchar(255),
  description               varchar(255),
  special                   boolean,
  price                     decimal(38),
  user_id                   bigint,
  some_date                 date,
  some_time                 timestamp,
  ding_enum                 varchar(2),
  image_id                  bigint,
  last_update               timestamp not null,
  constraint ck_ding_ding_enum check (ding_enum in ('JS','C+','OC','VB','RU','C','PE','PY','PH','J','CS')),
  constraint pk_ding primary key (id))
;

create table unter_ding (
  id                        bigint not null,
  name                      varchar(255),
  ding_id                   bigint,
  image_id                  bigint,
  last_update               timestamp not null,
  constraint pk_unter_ding primary key (id))
;

create table user_account (
  id                        bigint not null,
  email                     varchar(255),
  password_hash             varchar(255),
  role                      varchar(255),
  timezone                  varchar(255),
  validated                 boolean,
  firstname                 varchar(255),
  surname                   varchar(255),
  address                   varchar(255),
  country                   varchar(255),
  zip_code                  varchar(255),
  city                      varchar(255),
  random_pwrecover          varchar(255),
  pwrecover_triggered       timestamp,
  last_update               timestamp not null,
  constraint pk_user_account primary key (id))
;

create sequence db_image_seq;

create sequence ding_seq;

create sequence unter_ding_seq;

create sequence user_account_seq;

alter table ding add constraint fk_ding_user_1 foreign key (user_id) references user_account (id);
create index ix_ding_user_1 on ding (user_id);
alter table ding add constraint fk_ding_image_2 foreign key (image_id) references db_image (id);
create index ix_ding_image_2 on ding (image_id);
alter table unter_ding add constraint fk_unter_ding_ding_3 foreign key (ding_id) references ding (id);
create index ix_unter_ding_ding_3 on unter_ding (ding_id);
alter table unter_ding add constraint fk_unter_ding_image_4 foreign key (image_id) references db_image (id);
create index ix_unter_ding_image_4 on unter_ding (image_id);



# --- !Downs

drop table if exists db_image cascade;

drop table if exists ding cascade;

drop table if exists unter_ding cascade;

drop table if exists user_account cascade;

drop sequence if exists db_image_seq;

drop sequence if exists ding_seq;

drop sequence if exists unter_ding_seq;

drop sequence if exists user_account_seq;

