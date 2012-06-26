create table log_httpRequest (
	  id bigint not null,
	  method varchar(16),
	  url varchar(255),
	  host varchar(64), 
	  response_code smallint, 
	  user_agent varchar(255),
	  referer varchar(255),
	  from_IP varchar(16),	  
	  start_time bigint not null,
	  end_time  bigint not null,
	  constraint pk_log_httpRequest primary key (id)
);

create sequence log_httpRequest_seq;
alter sequence log_httpRequest_seq owned by log_httpRequest.id;