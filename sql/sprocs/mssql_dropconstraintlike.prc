SET QUOTED_IDENTIFIER ON 
;
SET ANSI_NULLS ON 
;

if exists (select * from dbo.sysobjects where id = object_id(N'[dbo].[MSSQL_DropConstraintLike]') and OBJECTPROPERTY(id, N'IsProcedure') = 1)
drop procedure [dbo].[MSSQL_DropConstraintLike]
;

CREATE PROCEDURE MSSQL_DropConstraintLike @table VARCHAR(255), @like VARCHAR(255) AS

DECLARE @constraint VARCHAR(255)

SELECT @constraint = constraints.name
FROM sysconstraints c, sysobjects constraints, sysobjects tables
WHERE c.constid = constraints.id AND c.id = tables.id
	AND tables.name = @table
	AND constraints.name LIKE @like

EXEC ('ALTER TABLE '+@table+' DROP CONSTRAINT '+@constraint)

;
SET QUOTED_IDENTIFIER OFF 
;
SET ANSI_NULLS ON 
;
