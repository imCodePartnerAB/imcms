CREATE TABLE errors (
  error_id BIGINT(20) NOT NULL,
  message TEXT NOT NULL,
  cause TEXT NOT NULL,
  stack_trace TEXT NOT NULL,
  discover_date DATETIME NOT NULL DEFAULT NOW(),
  viewed TINYINT(1) NOT NULL DEFAULT 0,
  resolved TINYINT(1) NOT NULL DEFAULT 0,
  PRIMARY KEY (error_id)
) ENGINE='InnoDB'  DEFAULT CHARSET='utf8' COLLATE='utf8_swedish_ci';

CREATE TABLE errors_users_crossref (
  error_id BIGINT(20) NOT NULL,
  user_id INT(11) NOT NULL,
  times INT(11) NOT NULL DEFAULT 1,
  start_date DATETIME NOT NULL DEFAULT NOW(),
  update_date DATETIME NOT NULL DEFAULT NOW(),
  PRIMARY KEY (error_id, user_id),
  KEY errors_users_crossref_FK_error_id_errors (error_id),
  CONSTRAINT errors_users_crossref_FK_error_id_errors FOREIGN KEY (error_id) REFERENCES errors (error_id),
  CONSTRAINT errors_users_crossref_FK_user_id_users FOREIGN KEY (user_id) REFERENCES users (user_id)
) ENGINE='InnoDB'  DEFAULT CHARSET='utf8' COLLATE='utf8_swedish_ci';

UPDATE database_version SET major = 4, minor = 18;