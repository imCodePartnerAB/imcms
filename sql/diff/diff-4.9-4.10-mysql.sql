alter table users add column session_id varchar(128) null;

update database_version set major = 4, minor = 10;
commit;
