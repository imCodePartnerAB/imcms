SET @schema_version__major_new = 6;
SET @schema_version__minor_new = 88;



CREATE TABLE user_properties (
  id int NOT NULL auto_increment,
  user_id int NOT NULL,
  key_name varchar(255) NOT NULL,
  value varchar(4096) NOT NULL,
  CONSTRAINT pk__user_properties PRIMARY KEY  (id),
  CONSTRAINT fk__user_properties__users FOREIGN KEY (user_id) REFERENCES users (user_id) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8;



UPDATE database_version
SET major = @schema_version__major_new,
    minor = @schema_version__minor_new;