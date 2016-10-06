CREATE TABLE errors (
  error_id INT(11) NOT NULL AUTO_INCREMENT,
  hash BIGINT(20) NOT NULL UNIQUE,
  message TEXT NOT NULL,
  cause TEXT NOT NULL,
  stack_trace TEXT NOT NULL,
  viewed TINYINT(1) NOT NULL DEFAULT 0,
  resolved TINYINT(1) NOT NULL DEFAULT 0,
  PRIMARY KEY (error_id)
) ENGINE='InnoDB'  DEFAULT CHARSET='utf8' COLLATE='utf8_swedish_ci';

CREATE TABLE errors_users_crossref (
  error_id INT(11) NOT NULL,
  user_id INT(11) NOT NULL,
  url TEXT NULL,
  times INT(11) NOT NULL DEFAULT 1,
  discover_date DATETIME NOT NULL DEFAULT NOW(),
  update_date DATETIME NOT NULL DEFAULT NOW(),
  PRIMARY KEY (error_id, user_id),
  KEY errors_users_crossref_K_error_id_errors (error_id),
  CONSTRAINT errors_users_crossref_FK_error_id_errors FOREIGN KEY (error_id) REFERENCES errors (error_id),
  CONSTRAINT errors_users_crossref_FK_user_id_users FOREIGN KEY (user_id) REFERENCES users (user_id)
) ENGINE='InnoDB'  DEFAULT CHARSET='utf8' COLLATE='utf8_swedish_ci';

UPDATE database_version SET major = 4, minor = 18;