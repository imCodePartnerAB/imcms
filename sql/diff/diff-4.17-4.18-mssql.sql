CREATE TABLE errors (
  error_id BIGINT NOT NULL,
  hash BIGINT NOT NULL UNIQUE,
  message TEXT NOT NULL,
  cause TEXT NOT NULL,
  stack_trace TEXT NOT NULL,
  viewed BIT NOT NULL DEFAULT 0,
  resolved BIT NOT NULL DEFAULT 0,
  PRIMARY KEY (error_id)
);

CREATE TABLE errors_users_crossref (
  error_id BIGINT NOT NULL,
  user_id INT NOT NULL,
  url TEXT NULL,
  times INT NOT NULL DEFAULT 1,
  discover_date DATETIME NOT NULL DEFAULT NOW(),
  update_date DATETIME NOT NULL DEFAULT NOW(),
  CONSTRAINT errors_users_crossref_PK_error_id_errors_user_id_users  PRIMARY KEY (error_id, user_id),
  CONSTRAINT errors_users_crossref_FK_error_id_errors FOREIGN KEY (error_id) REFERENCES errors (error_id),
  CONSTRAINT errors_users_crossref_FK_user_id_users FOREIGN KEY (user_id) REFERENCES users (user_id),
);

UPDATE database_version SET major = 4, minor = 18;