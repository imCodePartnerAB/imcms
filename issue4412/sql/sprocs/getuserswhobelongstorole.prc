CREATE PROCEDURE GetUsersWhoBelongsToRole @role_id int AS
/*
 * select user who belongs to role role_id
*/
select us.user_id, u.last_name + ', ' + u.first_name 
from user_roles_crossref us
join users u
  on us.user_id = u.user_id
where role_id = @role_id
order by  last_name
