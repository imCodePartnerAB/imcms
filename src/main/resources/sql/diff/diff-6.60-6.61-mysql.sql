# update meta default_version_no field

SET @schema_version__major_new = 6;
SET @schema_version__minor_new = 61;

DROP TABLE IF EXISTS ip_accesses;

CREATE TABLE ip_access_rules (
  rule_id    INT(11) AUTO_INCREMENT PRIMARY KEY,
  enabled    BIT DEFAULT 0      NOT NULL,
  restricted BIT DEFAULT 0      NOT NULL,
  ip_range   VARCHAR(80) DEFAULT NULL,
  role_id    INT(11) DEFAULT NULL,
  user_id    INT(11) DEFAULT NUll,
  CONSTRAINT imcms_ip_access_rule_fk_role_id FOREIGN KEY (role_id) REFERENCES roles (role_id)
    ON DELETE CASCADE,
  CONSTRAINT imcms_ip_access_rule_fk_user_id FOREIGN KEY (user_id) REFERENCES users (user_id)
    ON DELETE CASCADE
);

UPDATE database_version
SET major = @schema_version__major_new,
    minor = @schema_version__minor_new;

