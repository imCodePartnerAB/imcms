
SET IDENTITY_INSERT sys_data ON
INSERT INTO sys_data (sys_id, type_id, value) VALUES(1, 1, 0)
INSERT INTO sys_data (sys_id, type_id, value) VALUES(2, 2, GETDATE())
INSERT INTO sys_data (sys_id, type_id, value) VALUES(3, 3, '')
INSERT INTO sys_data (sys_id, type_id, value) VALUES(4, 4, '')
INSERT INTO sys_data (sys_id, type_id, value) VALUES(5, 5, '')
INSERT INTO sys_data (sys_id, type_id, value) VALUES(6, 6, '')
INSERT INTO sys_data (sys_id, type_id, value) VALUES(7, 7, '')
SET IDENTITY_INSERT sys_data OFF

INSERT INTO users VALUES (1,'admin', 'admin', 'Admin', 'Super','','','','','','','','',0,1001,0,1,1,1,GETDATE())
INSERT INTO users VALUES (2,'user', 'user', 'User', 'Extern','','','','','','','','',0,1001,0,1,1,1,GETDATE())

INSERT INTO roles VALUES(0, 'Superadmin',0)
INSERT INTO roles VALUES(1, 'Users',1)

INSERT INTO user_roles_crossref VALUES(1,0)
INSERT INTO user_roles_crossref VALUES(2,1)

INSERT INTO meta VALUES (1001, '', 2, 'Startsidan', '','', 1, 0, 0, 1, 0, 1, 0, 1, 'se','', GETDATE(), GETDATE(), 1, 1, 0,'2001-01-01','00:00','','','_self','', 1)

INSERT INTO templates VALUES (1,'start.html', 'Start', 'se', 1,1,1)

INSERT INTO templategroups VALUES (0, 'Start')

INSERT INTO templates_cref VALUES(0,1)

INSERT INTO text_docs VALUES (1001, 1, 0, 1)

INSERT INTO roles_rights VALUES (1,1001,3)

INSERT INTO texts VALUES( 1001, 1, '<h2>imCode imCMS</h2><br><a href="/login/">Logga in!</a>',1)

