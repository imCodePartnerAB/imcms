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

DROP PROCEDURE AddExistingDocToMenu
GO
