CREATE TABLE `imcms_ip_white_list` (
  is_admin TINYINT(1) NOT NULL,
  ip_range_from VARCHAR(15) NOT NULL,
  ip_range_to VARCHAR(15) NOT NULL,
  UNIQUE (is_admin, ip_range_from, ip_range_to)
);

UPDATE database_version SET major = 4, minor = 19;
