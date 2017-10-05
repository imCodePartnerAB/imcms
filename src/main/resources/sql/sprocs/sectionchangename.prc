CREATE PROCEDURE SectionChangeName
 @section_id int,
 @new_name varchar(200)
AS
 UPDATE sections
 set section_name= @new_name
 WHERE section_id = @section_id
