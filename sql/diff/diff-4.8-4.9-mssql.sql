-- Document's search keyword column
ALTER TABLE classification ALTER COLUMN code NVARCHAR(128);

-- User's data columns
ALTER TABLE users ALTER COLUMN login_name     NVARCHAR(128);
ALTER TABLE users ALTER COLUMN first_name     NVARCHAR(64);
ALTER TABLE users ALTER COLUMN last_name      NVARCHAR(64);
ALTER TABLE users ALTER COLUMN title          NVARCHAR(64);
ALTER TABLE users ALTER COLUMN company        NVARCHAR(64);
ALTER TABLE users ALTER COLUMN address        NVARCHAR(128);
ALTER TABLE users ALTER COLUMN city           NVARCHAR(64);
ALTER TABLE users ALTER COLUMN zip            NVARCHAR(64);
ALTER TABLE users ALTER COLUMN country        NVARCHAR(64);
ALTER TABLE users ALTER COLUMN county_council NVARCHAR(128);
ALTER TABLE users ALTER COLUMN email          NVARCHAR(128);

UPDATE database_version SET major = 4, minor = 9;
