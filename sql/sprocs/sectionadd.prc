CREATE PROCEDURE SectionAdd 
  @section_word varchar(200)
AS

-- Lets check if a section already exists
DECLARE @foundCode int
SELECT @foundCode = 0
-- Lets start with to find the id for the section_name
SELECT @foundCode = section_id
FROM sections
WHERE section_name LIKE @section_word

IF ( @foundCode = 0 ) BEGIN 
 --PRINT 'Koden fanns inte'
 -- Lets start to add the sections
 INSERT INTO sections (section_name)
 VALUES (  @section_word )
END
;
