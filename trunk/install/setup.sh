mysql -uroot < ~/projects/imcode/imcms/install/schema.sql
mysql -uroot -Dimcms_trunk < ~/projects/imcode/imcms/sql/imcms_6.0_schema.sql
mysql -uroot -Dimcms_trunk < ~/projects/imcode/imcms/sql/imcms_6.0_data.sql
