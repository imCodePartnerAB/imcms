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
