-- Add example template to database.
-- Creates a template group for example templates and add poll template to that group.

--lets create new templategroup for Example-templates
declare @example_groupId int
select @example_groupId = max(group_id)+1 from templategroups

insert into templategroups(group_id, group_name)
values(@example_groupId,'Example-templates')


--lets add default example template and then connect it to a templategroup
declare @poll_form_templateId int
select @poll_form_templateId = max(template_id)+1 from templates
INSERT INTO templates ( template_id , template_name , simple_name , lang_prefix , no_of_txt , no_of_img , no_of_url )
	values (@poll_form_templateId,'poll_form_template.html','poll_form_template','se',0,0,0)
INSERT INTO templates_cref(group_id, template_id)
	values (@example_groupId, @poll_form_templateId)

--lets add default example template and then connect it to a templategroup
declare @poll_result_default_templateId int
select @poll_result_default_templateId = max(template_id)+1 from templates
INSERT INTO templates ( template_id , template_name , simple_name , lang_prefix , no_of_txt , no_of_img , no_of_url )
	values (@poll_result_default_templateId,'poll_result_default_template.html','poll_result_default_template','swe',0,0,0)
INSERT INTO templates_cref(group_id, template_id)
	values (@example_groupId, @poll_result_default_templateId)

--lets add default example template and then connect it to a templategroup
declare @poll_confirmation_templateId int
select @poll_confirmation_templateId = max(template_id)+1 from templates
INSERT INTO templates ( template_id , template_name , simple_name , lang_prefix , no_of_txt , no_of_img , no_of_url )
	values (@poll_confirmation_templateId,'poll_confirmation_template.html','poll_confirmation_template','swe',0,0,0)
INSERT INTO templates_cref(group_id, template_id)
	values (@example_groupId, @poll_confirmation_templateId)

print ' OBS !!!!! '
print 'Följande åtgärder behöver genomföras efter detta script '
print ''
print 'Kopiera poll templates från mappen /poll/templates till WEB-INF/templates/text'
print 'och byt namn på dem enligt följande:'
print 'poll_form_template.html  till ' + convert (varchar(5), @poll_form_templateId) + '.html'
print 'poll_result_default_template.html  till ' + convert (varchar(5), @poll_result_default_templateId) + '.html'
print 'poll_confirmation_template.html  till ' + convert (varchar(5), @poll_confirmation_templateId) + '.html'
print ''
GO

-- 2003-03-13  Lennart Å
