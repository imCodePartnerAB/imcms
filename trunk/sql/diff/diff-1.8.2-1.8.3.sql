-- Diff from 1_8_2-RELEASE up to 1_8_3-RELEASE

CREATE TABLE menus (
    menu_id INT PRIMARY KEY IDENTITY,
    meta_id INT REFERENCES meta ( meta_id ),
    menu_index INT NOT NULL,
    sort_order INT NOT NULL,
    UNIQUE (meta_id, menu_index)
)
GO

INSERT INTO menus (meta_id, menu_index, sort_order)
SELECT DISTINCT td.meta_id, c.menu_sort, td.sort_order FROM text_docs AS td, childs AS c WHERE td.meta_id = c.meta_id
GO

ALTER TABLE childs ADD menu_id INT CONSTRAINT FK__childs__menu_id REFERENCES menus (menu_id)
GO

UPDATE childs SET menu_id = (SELECT menu_id FROM menus WHERE meta_id = childs.meta_id AND menu_index = childs.menu_sort)
GO

ALTER TABLE childs ALTER COLUMN menu_id INT NOT NULL
GO

DROP INDEX childs.childs_meta_id
ALTER TABLE childs DROP CONSTRAINT PK_childs
ALTER TABLE childs DROP CONSTRAINT FK_childs_meta1
ALTER TABLE childs DROP COLUMN meta_id, COLUMN menu_sort
GO

ALTER TABLE text_docs DROP COLUMN sort_order
GO

-- 2004-03-19 Kreiger

DROP PROCEDURE AddExistingDocToMenu
DROP PROCEDURE GetTextDocData
GO

-- 2004-03-22 Kreiger

ALTER TABLE childs ADD CONSTRAINT PK_childs PRIMARY KEY (menu_id, to_meta_id)
ALTER TABLE childs ADD CONSTRAINT PK_childs_meta FOREIGN KEY (to_meta_id) REFERENCES meta (meta_id)
GO

DROP PROCEDURE CopyDocs
DROP PROCEDURE UpdateTemplateTextsAndImages
GO

UPDATE images SET target = target_name WHERE target = '_other'
ALTER TABLE images DROP COLUMN target_name
GO

-- 2004-03-23 Kreiger

DROP PROCEDURE CheckUserAdminRole
DROP PROCEDURE GetImgs
DROP PROCEDURE GetIncludes
GO

-- 2004-03-24 Kreiger

MSSQL_DropConstraintLike 'menus', 'UQ__menus__%'
ALTER TABLE menus ALTER COLUMN meta_id INT NOT NULL
ALTER TABLE menus ADD CONSTRAINT UQ__menus__meta_id_menu_index UNIQUE (meta_id, menu_index)

-- 2004-03-26 Kreiger

ALTER TABLE roles ALTER COLUMN role_name VARCHAR(30) NOT NULL

-- 2004-04-05 Kreiger

UPDATE roles SET role_name = RTRIM(role_name)

-- 2004-04-06 Kreiger


BEGIN TRANSACTION
EXECUTE MSSQL_DropConstraintLike 'category_types', 'DF__category___max_c__%'
GO
CREATE TABLE dbo.Tmp_category_types
	(
	category_type_id int NOT NULL IDENTITY (1, 1),
	name varchar(50) NOT NULL,
	max_choices int NOT NULL
	)  ON [PRIMARY]
GO
ALTER TABLE dbo.Tmp_category_types ADD CONSTRAINT
	DF__category___max_choices DEFAULT (0) FOR max_choices
GO
SET IDENTITY_INSERT dbo.Tmp_category_types ON
GO
IF EXISTS(SELECT * FROM dbo.category_types)
	 EXEC('INSERT INTO dbo.Tmp_category_types (category_type_id, name, max_choices)
		SELECT category_type_id, name, max_choices FROM dbo.category_types TABLOCKX')
GO
SET IDENTITY_INSERT dbo.Tmp_category_types OFF
GO
EXECUTE MSSQL_DropConstraintLike 'categories', 'FK__categorie__categ__%'
GO
DROP TABLE dbo.category_types
GO
EXECUTE sp_rename N'dbo.Tmp_category_types', N'category_types', 'OBJECT'
GO
ALTER TABLE dbo.category_types ADD CONSTRAINT
	PK__category_types__68D28DBC PRIMARY KEY CLUSTERED
	(
	category_type_id
	) ON [PRIMARY]

GO
COMMIT
BEGIN TRANSACTION
ALTER TABLE dbo.categories WITH NOCHECK ADD CONSTRAINT
	FK__categorie__categ__6BAEFA67 FOREIGN KEY
	(
	category_type_id
	) REFERENCES dbo.category_types
	(
	category_type_id
	)
GO
COMMIT

BEGIN TRANSACTION
EXECUTE MSSQL_DropConstraintLike 'categories', 'FK__categorie__categ__%'
GO
COMMIT
BEGIN TRANSACTION
EXECUTE MSSQL_DropConstraintLike 'categories', 'DF__categorie__image__%'
GO
CREATE TABLE dbo.Tmp_categories
	(
	category_id int NOT NULL IDENTITY (1, 1),
	category_type_id int NOT NULL,
	name varchar(50) NOT NULL,
	description varchar(500) NULL,
	image varchar(255) NOT NULL
	)  ON [PRIMARY]
GO
ALTER TABLE dbo.Tmp_categories ADD CONSTRAINT
	DF__categorie__image__74444068 DEFAULT ('') FOR image
GO
SET IDENTITY_INSERT dbo.Tmp_categories ON
GO
IF EXISTS(SELECT * FROM dbo.categories)
	 EXEC('INSERT INTO dbo.Tmp_categories (category_id, category_type_id, name, description, image)
		SELECT category_id, category_type_id, name, description, image FROM dbo.categories TABLOCKX')
GO
SET IDENTITY_INSERT dbo.Tmp_categories OFF
GO
DROP TABLE dbo.categories
GO
EXECUTE sp_rename N'dbo.Tmp_categories', N'categories', 'OBJECT'
GO
ALTER TABLE dbo.categories ADD CONSTRAINT
	PK__categories__69C6B1F5 PRIMARY KEY CLUSTERED
	(
	category_id
	) ON [PRIMARY]

GO
ALTER TABLE dbo.categories WITH NOCHECK ADD CONSTRAINT
	FK__categorie__categ__6BAEFA67 FOREIGN KEY
	(
	category_type_id
	) REFERENCES dbo.category_types
	(
	category_type_id
	)
GO
COMMIT


-- 2004-04-07 Lennart Å
-- 1_8_3-RELEASE

print ' PLEASE NOTE !!!!! '
print ''
print 'You have to run the sql script "imcms-sprocs-1.8.3.sql" on imCMS database '
print ''

GO