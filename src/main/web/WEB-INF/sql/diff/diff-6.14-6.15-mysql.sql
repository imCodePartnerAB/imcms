-- normalizes imcms_text_doc_contents table
-- creates view on imcms_text_doc_contents

SET @schema_version__major_new = 6;
SET @schema_version__minor_new = 15;

ALTER TABLE imcms_text_doc_content_loops
    DROP COLUMN next_content_no;

CREATE TABLE imcms_text_doc_contents_new(
    id serial,
    loop_id int NOT NULL,
    no int NOT NULL,
    ix int NOT NULL,
    enabled boolean NOT NULL DEFAULT true,
    CONSTRAINT fk__content_loop FOREIGN KEY (loop_id) REFERENCES imcms_text_doc_content_loops (id),
    KEY ix__content_order (ix)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

INSERT INTO imcms_text_doc_contents_new
    SELECT c.id, l.id, c.no, c.ix, c.enabled
    FROM imcms_text_doc_content_loops l JOIN
         imcms_text_doc_contents c
         ON l.doc_id = c.doc_id AND
            l.doc_version_no = c.doc_version_no AND
            l.no = c.loop_no;

-- An attempt to drop both foreign key and an index in one statement leads to server crash and data corruption.
ALTER TABLE imcms_text_doc_images_history
    DROP FOREIGN KEY fk__imcms_text_doc_images_history__content;

-- An attempt to drop both indexe and key in one statement leads to server crash and data corruption.
ALTER TABLE imcms_text_doc_texts_history
    DROP FOREIGN KEY fk__imcms_text_doc_texts_history__content;


DROP TABLE imcms_text_doc_contents;
RENAME TABLE imcms_text_doc_contents_new TO imcms_text_doc_contents;

CREATE VIEW imcms_text_doc_contents_v AS
    SELECT l.doc_id, l.doc_version_no, l.no AS loop_no, c.no AS content_no, c.ix AS content_ix, c.enabled as content_enabled
    FROM imcms_text_doc_content_loops l JOIN
         imcms_text_doc_contents c
         ON l.id = c.loop_id;

--
-- Update schema version
--
UPDATE database_version
SET
  major = @schema_version__major_new,
  minor = @schema_version__minor_new;



