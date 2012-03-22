-- Document's search keyword column
ALTER TABLE classification MODIFY code VARCHAR(128);

-- User's data columns
ALTER TABLE users
    modify login_name     VARCHAR(128),
    modify first_name     VARCHAR(64),
    modify last_name      VARCHAR(64),
    modify title          VARCHAR(64),
    modify company        VARCHAR(64),
    modify address        VARCHAR(128),
    modify city           VARCHAR(64),
    modify zip            VARCHAR(64),
    modify country        VARCHAR(64),
    modify county_council VARCHAR(128),
    modify email          VARCHAR(128);

UPDATE database_version SET major = 4, minor = 9;
