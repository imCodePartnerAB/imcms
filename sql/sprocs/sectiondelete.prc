CREATE PROCEDURE SectionDelete
 @section_id int
AS
 
 DELETE
 FROM meta_section
 WHERE section_id = @section_id
 DELETE 
 FROM sections
 WHERE section_id = @section_id
