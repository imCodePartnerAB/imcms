SET QUOTED_IDENTIFIER OFF 
GO
SET ANSI_NULLS ON 
GO

if exists (select * from dbo.sysobjects where id = object_id(N'[dbo].[InheritPermissions]') and OBJECTPROPERTY(id, N'IsProcedure') = 1)
drop procedure [dbo].[InheritPermissions]
GO


CREATE PROCEDURE InheritPermissions @new_meta_id INT, @parent_meta_id INT, @doc_type INT AS
/* Inherit permissions for new documents in the parent to the new document */
INSERT INTO doc_permission_sets
SELECT  @new_meta_id,
  ndps.set_id,
  ndps.permission_id
FROM   new_doc_permission_sets ndps
WHERE ndps.meta_id = @parent_meta_id
IF @doc_type = 2 BEGIN
/* Inherit permissions for new documents in the new document to the new document */
INSERT INTO new_doc_permission_sets
SELECT @new_meta_id,
  ndps.set_id,
  ndps.permission_id
FROM  new_doc_permission_sets ndps
WHERE ndps.meta_id = @parent_meta_id
 AND @doc_type = 2
/* Inherit permissions for new documents in the parent to the new document */
INSERT INTO doc_permission_sets_ex
SELECT @new_meta_id,
  ndpse.set_id,
  ndpse.permission_id,
  ndpse.permission_data
FROM  new_doc_permission_sets_ex ndpse
WHERE ndpse.meta_id = @parent_meta_id
 AND @doc_type = 2
/* Inherit permissions for new documents in the new document to the new document */
INSERT INTO new_doc_permission_sets_ex
SELECT @new_meta_id,
  ndpse.set_id,
  ndpse.permission_id,
  ndpse.permission_data
FROM  new_doc_permission_sets_ex ndpse
WHERE ndpse.meta_id = @parent_meta_id
 AND @doc_type = 2
END ELSE BEGIN
	DECLARE @permission1 INT
	DECLARE @permission2 INT
	SELECT @permission1 = (65535 & dps.permission_id) FROM doc_permission_sets dps WHERE dps.meta_id = @new_meta_id AND dps.set_id = 1
	SELECT @permission2 = (65535 & dps.permission_id) FROM doc_permission_sets dps WHERE dps.meta_id = @new_meta_id AND dps.set_id = 2
	DELETE FROM doc_permission_sets WHERE meta_id = @new_meta_id
	IF @permission1 IS NULL BEGIN
		SET @permission1 = 0
	END
	IF @permission2 IS NULL BEGIN
		SET @permission2 = 0
	END
	IF (@permission1 != 0) OR 1 IN (SELECT set_id FROM doc_permission_sets_ex WHERE meta_id = @parent_meta_id AND permission_id = 8 AND permission_data = @doc_type) BEGIN
		INSERT INTO doc_permission_sets VALUES(@new_meta_id, 1, 65536|@permission1)
	END
	IF (@permission2 != 0) OR 2 IN (SELECT set_id FROM doc_permission_sets_ex WHERE meta_id = @parent_meta_id AND permission_id = 8 AND permission_data = @doc_type) BEGIN
		INSERT INTO doc_permission_sets VALUES(@new_meta_id, 2, 65536|@permission2)
	END
END
INSERT INTO roles_rights
SELECT role_id, @new_meta_id, set_id
FROM  roles_rights
WHERE meta_id = @parent_meta_id


GO
SET QUOTED_IDENTIFIER OFF 
GO
SET ANSI_NULLS ON 
GO

