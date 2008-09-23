-- Diff from 1_8_0-RELEASE up to 1_8_1-RELEASE

update doc_types
set type = 'Browser controlled link' where doc_type = 6 and lang_prefix = 'en'

 -- 1_8_1-RELEASE

print ' PLEASE NOTE !!!!! '
print ''
print 'You have to run the sql script "imcms-sprocs-1.8.1.sql" on imCMS database '
print ''


GO