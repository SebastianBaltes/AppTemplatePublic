# --- !Ups

CREATE TABLE log_httprequest (
    id bigint NOT NULL,
    method character varying(255),
    url character varying(255),
    host character varying(255),
    response_code integer,
    user_agent character varying(255),
    referer character varying(255),
    start_time bigint,
    end_time bigint,
    from_ip character varying(255)
);


CREATE SEQUENCE log_httprequest_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.log_httprequest_seq OWNER TO test;
