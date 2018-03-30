CREATE PROCEDURE SectionCount 
 @section_id int
AS
/*
Gets the number of docs that is connected to that section_id
*/
select count(meta_id) 
from meta_section
where section_id=@section_id
