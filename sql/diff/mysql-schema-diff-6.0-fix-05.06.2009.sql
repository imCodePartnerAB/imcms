-- Any customer product using this schema version before the fix date shoud
-- apply this script.  

-- Change meta_version engine type
CREATE TABLE __meta_version (
  id int NOT NULL auto_increment PRIMARY KEY,
  meta_id int NOT NULL,
  version int NOT NULL,
  version_tag varchar(12) NOT NULL,
  user_id int NULL,
  created_dt datetime NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

INSERT INTO __meta_version (meta_id, version, version_tag, user_id, created_dt)
SELECT meta_id, version, version_tag, user_id, created_dt FROM meta_version;

DROP TABLE meta_version;
RENAME TABLE __meta_version TO meta_version;

DELETE FROM meta_version WHERE meta_id NOT IN 
	(SELECT meta_id FROM meta);	
	
ALTER TABLE meta_version
  ADD CONSTRAINT fk__meta_version__user FOREIGN KEY (user_id) REFERENCES users (user_id) ON DELETE SET NULL,
  ADD CONSTRAINT fk__meta_version__meta FOREIGN KEY (meta_id) REFERENCES meta (meta_id) ON DELETE CASCADE;

-- Adds working version (as latest) to all documents which do not have it 
INSERT INTO meta_version (meta_id, version, version_tag, user_id, created_dt) 
	SELECT meta_id, version + 1, 'WORKING', 1, now() 
	FROM (SELECT vl.meta_id, vl.version, vl.version_tag 
		  FROM meta_version vl 
		  JOIN (SELECT meta_id, max(version) version 
		    	FROM meta_version 
		  		GROUP BY meta_id) vr 
		  ON vl.meta_id = vr.meta_id AND vl.version = vr.version) v 
		  WHERE v.version_tag <> 'WORKING';
		  
		  