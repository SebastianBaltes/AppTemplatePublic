# --- !Ups

CREATE TABLE log_variant_event (
	id                        	bigint not null,
	user_id                   	bigint,
	mv_test_variant_id			bigint not null,
	date						timestamp not null,
	info						text not null,
	last_update               	timestamp not null,
	constraint pk_log_variant_event primary key (id)
);

alter table log_variant_event add constraint fk_log_variant_event_user_account foreign key (user_id) references user_account (id);
alter table log_variant_event add constraint fk_log_variant_event_mv_test_variant foreign key (mv_test_variant_id) references mv_test_variant (id);

CREATE SEQUENCE log_variant_event_seq MINVALUE 1 START 1;
-- ALTER SEQUENCE log_variant_event_seq OWNER TO log_variant_event;

CREATE TABLE report_query (
	id 							bigint not null,
	name						varchar(255) not null, 
	description					text,
	query						text not null, 
	last_update               	timestamp not null,
	constraint pk_report_query primary key (id)
);
CREATE SEQUENCE report_query_seq MINVALUE 1 START 1;

CREATE TABLE report_query_parameter (
	id 							bigint not null,
	report_query_id				bigint not null,
	name						varchar(255) not null,
	description					text,
	last_update               	timestamp not null,
	constraint pk_report_query_parameter primary key (id)
);
CREATE SEQUENCE report_query_parameter_seq MINVALUE 1 START 1;

alter table report_query_parameter add constraint fk_report_query_parameter_report_query foreign key (report_query_id) references report_query (id);

 
-- teset  2

# --- !Downs

DROP SEQUENCE IF EXISTS log_variant_event_seq;
DROP SEQUENCE IF EXISTS report_query_seq;
DROP SEQUENCE IF EXISTS report_query_parameter_seq;

DROP TABLE IF EXISTS log_variant_event;
DROP TABLE IF EXISTS report_query_parameter;
DROP TABLE IF EXISTS report_query;