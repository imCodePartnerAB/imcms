-- Diff from 1_7_4-RELEASE to 1_7_5-RELEASE

if exists (select * from dbo.sysobjects where id = object_id(N'[dbo].[AddPhoneNr]') and OBJECTPROPERTY(id, N'IsProcedure') = 1)
drop procedure [dbo].[AddPhoneNr]
GO

-- 2003-11-28 Lennart
-- 1_7_5-RELEASE


print ' PLEASE NOTE !!!!! '
print ''
print 'You have to run the sql script "sprocs.sql" on imCMS database '
print ''
GO