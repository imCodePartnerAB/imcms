DELETE roles_rights
FROM
(SELECT role_id,meta_id,MAX(set_id) as set_id FROM roles_rights GROUP BY meta_id,role_id HAVING count(set_id) > 1) AS t1
WHERE roles_rights.role_id = t1.role_id and roles_rights.meta_id = t1.meta_id and roles_rights.set_id = t1.set_id


select * from roles_rights order by meta_id,role_id

BEGIN TRAN
select * from roles_rights
commit TRAN