
# --- !Ups

create table role (
	id bigint not null,
	name varchar(64) not null,
	constraint pk_role primary key (id)
);

create sequence role_seq;
alter sequence role_seq owned by role.id;

create table user_account (
	id bigint not null,
	email varchar(255) not null,
	password_hash varchar(255) not null,
	role_id	bigint not null,
	timezone varchar(128) not null,
	last_modified timestamp,
	
	firstname varchar(255),
	surname varchar(255),
	street	varchar(255),
	address varchar(255),
	country varchar(255),
	constraint pk_user_account primary key (id)
);

alter table user_account add constraint fk_user_account_role foreign key (role_id) references role (id);
create unique index ix_user_account_email on user_account (email);

create sequence user_account_seq;
alter sequence user_account_seq owned by user_account.id;

create table ding (
	id bigint not null,
	name varchar(255) not null,
	description varchar(255) not null,
	special	boolean not null,
	price bigint not null,
	last_update timestamp,
	constraint pk_ding primary key (id)
);

create sequence ding_seq;
alter sequence ding_seq owned by ding.id;






