set IDENTITY_INSERT C_msg_type on
insert into C_msg_type (msg_id,msg_string)
values(1,'ignorerar')
insert into C_msg_type (msg_id,msg_string)
values(2,'tarbort')
insert into C_msg_type (msg_id,msg_string)
values(100,'säger till')
insert into C_msg_type (msg_id,msg_string)
values(101,'viskar till (privat)')
insert into C_msg_type (msg_id,msg_string)
values(102,'frågar')
insert into C_msg_type (msg_id,msg_string)
values(103,'ropar till')
set IDENTITY_INSERT C_msg_type off
GO

insert into C_authorization_types(authorization_type,authorization_id)
values('oregistrerad',1)

insert into C_authorization_types(authorization_type,authorization_id)
values('imCMS-registrerad',2)
GO

