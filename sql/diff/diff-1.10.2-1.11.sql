ALTER TABLE fileupload_docs ADD variant_name VARCHAR(100) NOT NULL DEFAULT ''
ALTER TABLE fileupload_docs ADD default_variant BIT NOT NULL DEFAULT 0
ALTER TABLE fileupload_docs DROP CONSTRAINT PK_fileupload_docs
ALTER TABLE fileupload_docs ADD CONSTRAINT PK_fileupload_docs PRIMARY KEY ( meta_id, variant_name )

-- 2004-07-27 Kreiger

-- Try to add constraint, ( missing in older version of diff-script 1.8.1-1.8.2 )
ALTER TABLE [dbo].[document_categories] ADD CONSTRAINT
	[FK_document_categories_categories] FOREIGN KEY
	(
		[category_id]
	) REFERENCES [dbo].[categories] (
		[category_id]
	)

-- 2004-09-15 Lennart


-- Delete all erroneous file documents  ( activate=0 )
declare @meta_id int
declare posCursor  Cursor scroll for
	select meta_id from meta where doc_type = 8 and activate = 0
open posCursor
fetch next from posCursor
into @meta_id
while @@fetch_status = 0
begin
	exec documentdelete @meta_id
	fetch next from posCursor
	into @meta_id
end
close posCursor
deallocate posCursor


--  repair table fileupload_docs,
--  add all activated fileupload documents that is missing in table fileupload_docs.

declare @meta_id int
declare posCursor  Cursor scroll for
	select meta_id from meta
	where doc_type = 8 and activate = 1 and  meta_id not in (select meta_id from fileupload_docs)

open posCursor
fetch next from posCursor
into @meta_id
while @@fetch_status = 0
begin
	insert into fileupload_docs values (@meta_id, '', 'application/octet-stream', 0)
	fetch next from posCursor
	into @meta_id
end
close posCursor
deallocate posCursor


-- Delete all erroneous HTML-documents  ( activate=0 and missing in table frameset_docs )
declare @meta_id int
declare posCursor  Cursor scroll for
    select meta_id from meta where doc_type = 7 and
        activate = 0 and meta_id not in (select meta_id from frameset_docs)
open posCursor
fetch next from posCursor
into @meta_id
while @@fetch_status = 0
begin
	exec documentdelete @meta_id
	fetch next from posCursor
	into @meta_id
end
close posCursor
deallocate posCursor


-- Delete all erroneous URL-documents  ( missing in table url_docs )
declare @meta_id int
declare posCursor  Cursor scroll for
    select meta_id from meta where doc_type = 5 and
        meta_id not in (select meta_id from url_docs)
open posCursor
fetch next from posCursor
into @meta_id
while @@fetch_status = 0
begin
	exec documentdelete @meta_id
	fetch next from posCursor
	into @meta_id
end
close posCursor
deallocate posCursor


-- Delete all erroneous text-documents  ( missing in table text_docs )
declare @meta_id int
declare posCursor  Cursor scroll for
    select meta_id from meta where doc_type = 2 and
        meta_id not in (select meta_id from text_docs)
open posCursor
fetch next from posCursor
into @meta_id
while @@fetch_status = 0
begin
	exec documentdelete @meta_id
	fetch next from posCursor
	into @meta_id
end
close posCursor
deallocate posCursor


-- Delete all erroneous browser-documents  ( missing in table browser_docs )
declare @meta_id int
declare posCursor  Cursor scroll for
    select meta_id from meta
    where doc_type = 6 and  meta_id not in (select meta_id from browser_docs)
open posCursor
fetch next from posCursor
into @meta_id
while @@fetch_status = 0
begin
	exec documentdelete @meta_id
	fetch next from posCursor
	into @meta_id
end
close posCursor
deallocate posCursor


--- Delete erroneous rows in table childs
DELETE FROM childs
WHERE  (to_meta_id IN
            (SELECT meta_id
            FROM meta
            WHERE activate = 0)) OR
       (to_meta_id NOT IN
            (SELECT meta_id
            FROM meta))

-- 040916 Lennart Å



