-- Adds working version (as latest) to all documents which do not have it 
INSERT INTO meta_version (meta_id, version, version_tag) 
	SELECT meta_id, version + 1, 'WORKING' 
	FROM (SELECT vl.meta_id, vl.version, vl.version_tag 
		  FROM meta_version vl 
		  JOIN (SELECT meta_id, max(version) version 
		    	FROM meta_version 
		  		GROUP BY meta_id) vr 
		  ON vl.meta_id = vr.meta_id AND vl.version = vr.version) v 
		  WHERE v.version_tag <> 'WORKING';