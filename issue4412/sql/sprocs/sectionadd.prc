CREATE PROCEDURE SectionAdd 
  @section_word varchar(200)
AS

 INSERT INTO sections (section_name)
 VALUES (  @section_word )
