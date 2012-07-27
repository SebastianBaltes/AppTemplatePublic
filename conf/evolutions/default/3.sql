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

# --- !Downs

DROP SEQUENCE log_variant_event_seq;
DROP TABLE IF EXISTS log_variant_event; 