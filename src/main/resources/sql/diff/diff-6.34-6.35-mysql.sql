# removing no more used things and created new document restricted permissions table

SET @schema_version__major_new = 6;
SET @schema_version__minor_new = 35;

ALTER TABLE `meta`
  ADD COLUMN modifier_id INT;

UPDATE `meta` AS m
SET m.modifier_id = (SELECT vvv.modified_by
                     FROM (SELECT
                             v.modified_by,
                             v.doc_id,
                             v.no
                           FROM imcms_doc_versions v
                           WHERE v.no = (SELECT MAX(vv.no)
                                         FROM imcms_doc_versions AS vv
                                         WHERE vv.doc_id = v.doc_id)
                          ) AS vvv
                     WHERE vvv.doc_id = m.meta_id
);

ALTER TABLE `meta`
  CHANGE modifier_id modifier_id INT NOT NULL;


UPDATE database_version
SET
  major = @schema_version__major_new,
  minor = @schema_version__minor_new;
