-- Document's search keyword column
alter table classification modify code varchar(128);

-- User's data columns
alter table users
    modify login_name     varchar(128),
    modify first_name     varchar(64),
    modify last_name      varchar(64),
    modify title          varchar(64),
    modify company        varchar(64),
    modify address        varchar(128),
    modify city           varchar(64),
    modify zip            varchar(64),
    modify country        varchar(64),
    modify county_council varchar(128),
    modify email          varchar(128);

UPDATE database_version SET major = 4, minor = 9;
