CREATE TABLE internal_error (
  error_id INT IDENTITY (1, 1) NOT NULL,
  message TEXT NOT NULL,
  cause TEXT NOT NULL,
  placement TEXT NULL,
  CONSTRAINT [PK_internal_error] PRIMARY KEY CLUSTERED ([error_id] ASC));

UPDATE database_version SET major = 4, minor = 18;