﻿-- Changes for v 6.0

-- Current schema version
SET @database_version__major__current = 5;
SET @database_version__minor__current = 3;

-- New schema version
SET @database_version__major__new = 6;
SET @database_version__minor__new = 0;

CREATE TABLE includes_new (
  id int auto_increment PRIMARY KEY,
  meta_id int NULL,
  include_id int NOT NULL,
  included_meta_id int NULL  
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

INSERT INTO includes_new (meta_id, include_id, included_meta_id)
SELECT meta_id, include_id, included_meta_id FROM includes;

DROP TABLE includes;
RENAME TABLE includes_new TO includes;

ALTER TABLE includes ADD UNIQUE INDEX ux__includes__meta_id__include_id(meta_id, include_id);
ALTER TABLE includes ADD FOREIGN KEY fk__includes__meta (meta_id) REFERENCES meta (meta_id) ON DELETE CASCADE;
ALTER TABLE includes ADD FOREIGN KEY fk__includes__included_meta (included_meta_id) REFERENCES meta (meta_id);


--
-- Update schema version
--
UPDATE database_version
SET
  major = @database_version__major__new,
  minor = @database_version__minor__new;