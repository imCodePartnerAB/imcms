CREATE TABLE `imcms_ip_white_list` (
  role_id INT NOT NULL,
  ip_range_from VARCHAR(15) NOT NULL,
  ip_range_to VARCHAR(15) NOT NULL,
  UNIQUE (role_id, ip_range_from, ip_range_to),
  CONSTRAINT role_id_to_white_list_range FOREIGN KEY (role_id) REFERENCES roles(role_id)
);

UPDATE database_version SET major = 4, minor = 19;
