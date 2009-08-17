mysql -uroot < ~/projects/imcode/imcms/install/schema.sql
mysql -uroot -Dimcms_trunk < ~/projects/imcode/imcms/sql/imcms_6.1_schema.sql
mysql -uroot -Dimcms_trunk < ~/projects/imcode/imcms/sql/imcms_6.1_data.sql
