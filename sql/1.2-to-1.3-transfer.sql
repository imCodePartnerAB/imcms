
INSERT INTO ny_imcode_010425..meta
SELECT * FROM imcode_010425..meta

UPDATE meta SET permissions = 1

INSERT INTO ny_imcode_010425..templates
SELECT * FROM imcode_010425..templates

INSERT INTO ny_imcode_010425..templategroups
SELECT * FROM imcode_010425..templategroups

INSERT INTO ny_imcode_010425..templates_cref
SELECT * FROM imcode_010425..templates_cref

INSERT INTO ny_imcode_010425..text_docs
SELECT * FROM imcode_010425..text_docs

INSERT INTO ny_imcode_010425..url_docs
SELECT * FROM imcode_010425..url_docs

INSERT INTO ny_imcode_010425..browser_docs
SELECT * FROM imcode_010425..browser_docs

INSERT INTO ny_imcode_010425..frameset_docs
SELECT * FROM imcode_010425..frameset_docs

INSERT INTO ny_imcode_010425..fileupload_docs
SELECT * FROM imcode_010425..fileupload_docs

SET IDENTITY_INSERT ny_imcode_010425..sys_data ON

INSERT INTO ny_imcode_010425..sys_data (sys_id,type_id,value)
SELECT * FROM imcode_010425..sys_data

SET IDENTITY_INSERT ny_imcode_010425..sys_data OFF

INSERT INTO ny_imcode_010425..texts
SELECT * FROM imcode_010425..texts

INSERT INTO ny_imcode_010425..users
SELECT * FROM imcode_010425..users

INSERT INTO ny_imcode_010425..roles
SELECT * FROM imcode_010425..roles

INSERT INTO ny_imcode_010425..user_roles_crossref
SELECT * FROM imcode_010425..user_roles_crossref

DECLARE @minuser INT
SELECT @minuser = MIN(user_id) FROM users

UPDATE ny_imcode_010425..meta SET owner_id = ISNULL(ur.user_id,@minuser)
FROM ny_imcode_010425..meta LEFT JOIN imcode_010425..user_rights ur
ON ny_imcode_010425..meta.meta_id = ur.meta_id

UPDATE ny_imcode_010425..meta SET doc_type = 2
WHERE doc_type = 1

INSERT INTO ny_imcode_010425..roles_rights
SELECT role_id,meta_id,MAX(permission_id) FROM imcode_010425..roles_rights
GROUP BY role_id, meta_id

UPDATE ny_imcode_010425..roles_rights SET set_id = 0 WHERE set_id = 3
UPDATE  ny_imcode_010425..roles_rights SET set_id = 3 WHERE set_id = 1

INSERT INTO ny_imcode_010425..images
SELECT * FROM imcode_010425..images

SET IDENTITY_INSERT ny_imcode_010425..classification ON
INSERT INTO ny_imcode_010425..classification (class_id, code)
SELECT * FROM imcode_010425..classification
SET IDENTITY_INSERT ny_imcode_010425..classification OFF

INSERT INTO ny_imcode_010425..meta_classification
SELECT * FROM imcode_010425..meta_classification

INSERT INTO ny_imcode_010425..childs
SELECT * FROM imcode_010425..childs


SET IDENTITY_INSERT ny_imcode_010425..ip_accesses ON

DECLARE my_curse CURSOR
FOR (SELECT ip_access_id, user_id, ip_start, ip_end FROM imcode_010425..ip_accesses)

DECLARE @ipnum INT
DECLARE @index INT
DECLARE @oldindex INT
DECLARE @ip_access_id INT
DECLARE @user_id INT
DECLARE @ipstartstr VARCHAR(15)
DECLARE @ipendstr VARCHAR(15)
DECLARE @ipstart DECIMAL
DECLARE @ipend DECIMAL
DECLARE @exp INT

OPEN my_curse

FETCH NEXT FROM my_curse
INTO @ip_access_id,@user_id,@ipstartstr,@ipendstr

WHILE @@FETCH_STATUS = 0 BEGIN
	SET @ipstart = 0
	SET @ipend = 0

	SET @exp = 4
	SET @index = 1
	SET @oldindex = 1

	WHILE @index <= LEN(@ipstartstr)+1 BEGIN
		IF SUBSTRING(@ipstartstr,@index,1) = '.' OR @index = LEN(@ipstartstr)+1  BEGIN
			SET @exp = @exp - 1
			SET @ipnum = CAST(SUBSTRING(@ipstartstr,@oldindex,@index-@oldindex) AS INT)
			SET @oldindex = @index + 1
			SET @ipstart = @ipstart + POWER(256.0, @exp) * @ipnum
		END
		SET @index = @index+1
	END

	SET @exp = 4
	SET @index = 1
	SET @oldindex = 1

	WHILE @index <= LEN(@ipendstr)+1 BEGIN
		IF SUBSTRING(@ipendstr,@index,1) = '.' OR @index = LEN(@ipendstr)+1  BEGIN
			SET @exp = @exp - 1
			SET @ipnum = CAST(SUBSTRING(@ipendstr,@oldindex,@index-@oldindex) AS INT)
			SET @oldindex = @index + 1
			SET @ipend = @ipend + POWER(256.0, @exp) * @ipnum
		END
		SET @index = @index+1
	END

	INSERT INTO ny_imcode_010425..ip_accesses (ip_access_id,user_id,ip_start,ip_end)
	VALUES(@ip_access_id,@user_id,@ipstart,@ipend)

	FETCH NEXT FROM my_curse
	INTO @ip_access_id,@user_id,@ipstartstr,@ipendstr
END
CLOSE my_curse
DEALLOCATE my_curse

SET IDENTITY_INSERT ny_imcode_010425..ip_accesses OFF

INSERT INTO ny_imcode_010425..main_log
SELECT * FROM imcode_010425..main_log

SET IDENTITY_INSERT ny_imcode_010425..sys_data ON
INSERT INTO ny_imcode_010425..sys_data (sys_id, type_id, value) VALUES(4, 4, '')
INSERT INTO ny_imcode_010425..sys_data (sys_id, type_id, value) VALUES(5, 5, '')
INSERT INTO ny_imcode_010425..sys_data (sys_id, type_id, value) VALUES(6, 6, '')
INSERT INTO ny_imcode_010425..sys_data (sys_id, type_id, value) VALUES(7, 7, '')
SET IDENTITY_INSERT ny_imcode_010425..sys_data OFF
