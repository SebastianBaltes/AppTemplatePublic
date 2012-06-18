
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
	timezone varchar(128) not null DEFAULT 'Europe/Berlin',
	last_modified timestamp not null DEFAULT CURRENT_TIMESTAMP,
	
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









