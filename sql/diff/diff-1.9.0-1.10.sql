ALTER TABLE users DROP CONSTRAINT DF_users_user_type
ALTER TABLE users DROP COLUMN user_type

DROP TABLE user_types

DROP PROCEDURE GetUserType
DROP PROCEDURE GetUserTypes
DROP PROCEDURE GetCategoryUsers
DROP PROCEDURE UserPrefsChange

-- 2004-06-02 Kreiger

ALTER TABLE images
    ADD type int NOT NULL CONSTRAINT DF_images_type DEFAULT 0

-- Hasse 2004-06-14

ALTER TABLE fileupload_docs ALTER COLUMN filename VARCHAR(255)

-- 2004-06-14 Kreiger

ALTER TABLE fileupload_docs ADD created_as_image INT NOT NULL DEFAULT 0

-- 2004-07-01 Kreiger

DROP PROCEDURE InsertText

-- 2004-07-05 Kreiger
