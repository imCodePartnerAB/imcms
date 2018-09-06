# update meta default_version_no field

SET @schema_version__major_new = 6;
SET @schema_version__minor_new = 60;

UPDATE meta m
	JOIN (
			SELECT v.doc_id, MAX(v.no) AS latest_version
			FROM imcms_doc_versions v
			GROUP BY v.doc_id
		) AS temp
    ON m.meta_id = temp.doc_id
SET m.default_version_no = temp.latest_version
WHERE m.meta_id = temp.doc_id;

UPDATE database_version
SET major = @schema_version__major_new,
    minor = @schema_version__minor_new;

