CREATE PROCEDURE SectionChangeAndDeleteCrossref
  @new_section_id int,
  @old_section_id  int
AS
update meta_section
set section_id = @new_section_id
where section_id=@old_section_id

exec SectionDelete @old_section_id
