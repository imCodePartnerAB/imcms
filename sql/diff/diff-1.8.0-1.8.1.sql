-- Diff from 1_8_0-RELEASE up to 1_8_1-RELEASE

update doc_types
set type = 'Browser controlled link' where doc_type = 6 and lang_prefix = 'en'

 -- 1_8_1-RELEASE

print ' OBS !!!!! '
print 'Följande åtgärder behöver genomföras efter detta script '
print ''
print '1. Du MÅSTE köra hela "sprocs.sql" som finns i "dist" katalogen'
print ''

GO