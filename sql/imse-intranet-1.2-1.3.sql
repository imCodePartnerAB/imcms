--------------------------------------------------------------------------------
-- DBArtisan Change Manager Synchronization Script
-- FILE                : C:\Mina Dokument\imse-intranet-1.2-1.3.sql
-- DATE                : 12/19/2000 02:10:47PM
-- 
-- SOURCE DATASOURCE   : Njord.intranet
-- TARGET DATASOURCE   : Njord
--------------------------------------------------------------------------------
 
USE intranet_backup_001214
go

--
-- Procedure Drop
-- imse.meta_select
--
IF OBJECT_ID('imse.meta_select') IS NOT NULL
BEGIN
    DROP PROCEDURE imse.meta_select
    IF OBJECT_ID('imse.meta_select') IS NOT NULL
        PRINT '<<< FAILED DROPPING PROCEDURE imse.meta_select >>>'
    ELSE
        PRINT '<<< DROPPED PROCEDURE imse.meta_select >>>'
END
go

--
-- Procedure Drop
-- imse.getUserWriteRights
--
IF OBJECT_ID('imse.getUserWriteRights') IS NOT NULL
BEGIN
    DROP PROCEDURE imse.getUserWriteRights
    IF OBJECT_ID('imse.getUserWriteRights') IS NOT NULL
        PRINT '<<< FAILED DROPPING PROCEDURE imse.getUserWriteRights >>>'
    ELSE
        PRINT '<<< DROPPED PROCEDURE imse.getUserWriteRights >>>'
END
go

--
-- Procedure Drop
-- imse.RoleGetConferenceAllowed
--
IF OBJECT_ID('imse.RoleGetConferenceAllowed') IS NOT NULL
BEGIN
    DROP PROCEDURE imse.RoleGetConferenceAllowed
    IF OBJECT_ID('imse.RoleGetConferenceAllowed') IS NOT NULL
        PRINT '<<< FAILED DROPPING PROCEDURE imse.RoleGetConferenceAllowed >>>'
    ELSE
        PRINT '<<< DROPPED PROCEDURE imse.RoleGetConferenceAllowed >>>'
END
go

--
-- Procedure Drop
-- imse.GetNewUserId
--
IF OBJECT_ID('imse.GetNewUserId') IS NOT NULL
BEGIN
    DROP PROCEDURE imse.GetNewUserId
    IF OBJECT_ID('imse.GetNewUserId') IS NOT NULL
        PRINT '<<< FAILED DROPPING PROCEDURE imse.GetNewUserId >>>'
    ELSE
        PRINT '<<< DROPPED PROCEDURE imse.GetNewUserId >>>'
END
go

--
-- Procedure Drop
-- imse.GetAdminChilds
--
IF OBJECT_ID('imse.GetAdminChilds') IS NOT NULL
BEGIN
    DROP PROCEDURE imse.GetAdminChilds
    IF OBJECT_ID('imse.GetAdminChilds') IS NOT NULL
        PRINT '<<< FAILED DROPPING PROCEDURE imse.GetAdminChilds >>>'
    ELSE
        PRINT '<<< DROPPED PROCEDURE imse.GetAdminChilds >>>'
END
go

--
-- Procedure Drop
-- dbo.testProc
--
IF OBJECT_ID('dbo.testProc') IS NOT NULL
BEGIN
    DROP PROCEDURE dbo.testProc
    IF OBJECT_ID('dbo.testProc') IS NOT NULL
        PRINT '<<< FAILED DROPPING PROCEDURE dbo.testProc >>>'
    ELSE
        PRINT '<<< DROPPED PROCEDURE dbo.testProc >>>'
END
go

--
-- Procedure Drop
-- dbo.getDocsParentCount
--
IF OBJECT_ID('dbo.getDocsParentCount') IS NOT NULL
BEGIN
    DROP PROCEDURE dbo.getDocsParentCount
    IF OBJECT_ID('dbo.getDocsParentCount') IS NOT NULL
        PRINT '<<< FAILED DROPPING PROCEDURE dbo.getDocsParentCount >>>'
    ELSE
        PRINT '<<< DROPPED PROCEDURE dbo.getDocsParentCount >>>'
END
go

--
-- Table Drop
-- dbo.user_roles_OLD
--
DROP TABLE dbo.user_roles_OLD
go

--
-- Table Drop
-- dbo.urls_OLD
--
DROP TABLE dbo.urls_OLD
go

--
-- Table Drop
-- dbo.status_se_OLD
--
DROP TABLE dbo.status_se_OLD
go

--
-- Table Drop
-- dbo.status_OLD
--
DROP TABLE dbo.status_OLD
go

--
-- Table Drop
-- dbo.processings_se_OLD
--
DROP TABLE dbo.processings_se_OLD
go

--
-- Table Drop
-- dbo.processings_OLD
--
DROP TABLE dbo.processings_OLD
go

--
-- Table Drop
-- dbo.parents_OLD
--
DROP TABLE dbo.parents_OLD
go

--
-- Table Drop
-- dbo.new_images_OLD
--
DROP TABLE dbo.new_images_OLD
go

--
-- Table Drop
-- dbo.meta_logs_OLD
--
DROP TABLE dbo.meta_logs_OLD
go

--
-- Table Drop
-- dbo.meta_log_types_OLD
--
DROP TABLE dbo.meta_log_types_OLD
go

--
-- Table Drop
-- dbo.languages_se_OLD
--
DROP TABLE dbo.languages_se_OLD
go

--
-- Table Drop
-- dbo.image_docs_OLD
--
DROP TABLE dbo.image_docs_OLD
go

--
-- Table Drop
-- dbo.help_texts_OLD
--
DROP TABLE dbo.help_texts_OLD
go

--
-- Table Drop
-- dbo.help_text_se_OLD
--
DROP TABLE dbo.help_text_se_OLD
go

--
-- Table Drop
-- dbo.domains_OLD
--
DROP TABLE dbo.domains_OLD
go

--
-- Table Drop
-- dbo.doc_logs_OLD
--
DROP TABLE dbo.doc_logs_OLD
go

--
-- Table Drop
-- dbo.doc_log_types_OLD
--
DROP TABLE dbo.doc_log_types_OLD
go

--
-- Table Drop
-- dbo.categories_se_OLD
--
DROP TABLE dbo.categories_se_OLD
go

--
-- Table Drop
-- dbo.categories_OLD
--
DROP TABLE dbo.categories_OLD
go

--
-- Table Drop
-- dbo.admin_table_OLD
--
DROP TABLE dbo.admin_table_OLD
go

--
-- Table Drop
-- dbo.admin_roles_OLD
--
DROP TABLE dbo.admin_roles_OLD
go

USE intranet_backup_001214
go

--
-- Role Extended Alter
-- db_accessadmin
--
EXEC sp_addrole 'db_accessadmin', 'dbo'
go

--
-- Table Alter
-- dbo.browser_docs
--
ALTER TABLE dbo.browser_docs ADD DEFAULT 0 FOR browser_id
go
ALTER TABLE dbo.browser_docs DROP CONSTRAINT FK_browser_docs_meta
go

--
-- Table Alter
-- dbo.childs
--
ALTER TABLE dbo.childs ADD CONSTRAINT FK_childs_meta
FOREIGN KEY (to_meta_id)
REFERENCES dbo.meta (meta_id)
go
ALTER TABLE dbo.childs ADD CONSTRAINT FK_childs_meta1
FOREIGN KEY (meta_id)
REFERENCES dbo.meta (meta_id)
go

--
-- Table Create
-- dbo.doc_permission_sets
--
CREATE TABLE dbo.doc_permission_sets 
(
    meta_id       int NOT NULL,
    set_id        int NOT NULL,
    permission_id int NOT NULL,
    CONSTRAINT PK_doc_permission_sets
    PRIMARY KEY NONCLUSTERED (meta_id,set_id)
)
go

--
-- Table Create
-- dbo.doc_permission_sets_ex
--
CREATE TABLE dbo.doc_permission_sets_ex 
(
    meta_id         int NOT NULL,
    set_id          int NOT NULL,
    permission_id   int NOT NULL,
    permission_data int NOT NULL,
    CONSTRAINT PK_permission_sets_ex
    PRIMARY KEY NONCLUSTERED (meta_id,set_id,permission_id,permission_data)
)
go

--
-- Table Create
-- dbo.doc_permissions
--
CREATE TABLE dbo.doc_permissions 
(
    permission_id int         NOT NULL,
    doc_type      int         NOT NULL,
    lang_prefix   varchar(3)  NOT NULL,
    description   varchar(50) NOT NULL,
    CONSTRAINT PK_doc_permissions
    PRIMARY KEY NONCLUSTERED (permission_id,doc_type,lang_prefix)
)
go

--
-- Table Extended Alter
-- dbo.doc_types
--
EXEC sp_rename 'dbo.doc_types','doc_types_12192000100900000',OBJECT
go
CREATE TABLE dbo.doc_types 
(
    doc_type    int         NOT NULL,
    lang_prefix varchar(3)  DEFAULT 'se' NOT NULL,
    type        varchar(50) NULL
)
go
INSERT INTO dbo.doc_types(
                          doc_type,
--                        lang_prefix,
                          type
                         )
                   SELECT 
                          doc_type,
--                        'se',
                          CONVERT(varchar(50),type)
                     FROM dbo.doc_types_12192000100900000
go
ALTER TABLE dbo.doc_types ADD CONSTRAINT PK_doc_types
PRIMARY KEY NONCLUSTERED (doc_type,lang_prefix) 
go
EXEC sp_rename 'imse.ListDocsByDate','ListDocsBy_12192000100900001',OBJECT
go
CREATE PROCEDURE imse.ListDocsByDate @listMod int,  @doc_type int, @startDate varchar(10), @endDate varchar(20) AS
/*
 lists doctyps where activate = 1
 @listMod 0 = all date, 1 = only creatdat, 2 = only modifieddata
 @startDoc yyyy-mm-dd or 0 then not set
 @endDate yyyy-mm-dd or 0 then not set
*/
-- Listdate fix 
if ( @endDate <> '0') BEGIN
 SET @endDate = @endDate + ' 23:59:59'
 PRINT @endDate
END 
/* list all (not in use ) */
if ( @listMod = 0) begin
 if ( @startDate = '0' ) begin
  if ( @endDate = '0' ) begin
   select m.meta_id, dt.type, m.meta_headline, m.date_modified
 
   from meta m
   join doc_types dt
      on m.doc_type = dt.doc_type
   where m.doc_type = @doc_type and activate = 1
   order by m.date_modified
  end
  else begin
   select m.meta_id, dt.type, m.meta_headline, m.date_modified
 
   from meta m
   join doc_types dt
      on m.doc_type = dt.doc_type
   where m.doc_type = @doc_type and m.date_modified <= @endDate and activate = 1
   order by m.date_modified
  end
 end
 else if ( @startDate != '0' ) begin
  if ( @endDate = '0' ) begin
   select m.meta_id, dt.type, m.meta_headline, m.date_modified
 
   from meta m
   join doc_types dt
      on m.doc_type = dt.doc_type
   where m.doc_type = @doc_type and m.date_modified >= @startDate and activate = 1
   order by m.date_modified
  end
  else
  begin
   select m.meta_id, dt.type, m.meta_headline, m.date_modified
 
   from meta m
   join doc_types dt
      on m.doc_type = dt.doc_type
   where m.doc_type = @doc_type and m.date_modified <= @endDate and m.date_modified >= @startDate and activate = 1
   order by m.date_modified
  end
 end
end
/* list creatdate */
else if ( @listMod = 1) begin
 if ( @startDate = '0' ) begin
  if ( @endDate = '0' ) begin
   select m.meta_id, dt.type, m.meta_headline, m.date_created
 
   from meta m
   join doc_types dt
      on m.doc_type = dt.doc_type
   where m.doc_type = @doc_type and activate = 1
   order by m.date_created
  end
  else begin
   select m.meta_id, dt.type, m.meta_headline, m.date_created
 
   from meta m
   join doc_types dt
      on m.doc_type = dt.doc_type
   where m.doc_type = @doc_type and m.date_created <= @endDate and activate = 1
   order by m.date_created
  end
 end
 else if ( @startDate != '0' ) begin
  if ( @endDate = '0' ) begin
   select m.meta_id, dt.type, m.meta_headline, m.date_created
 
   from meta m
   join doc_types dt
      on m.doc_type = dt.doc_type
   where m.doc_type = @doc_type and m.date_created >= @startDate and activate = 1
   order by m.date_created
  end
  else
  begin
   select m.meta_id, dt.type, m.meta_headline, m.date_created
 
   from meta m
   join doc_types dt
      on m.doc_type = dt.doc_type
   where m.doc_type = @doc_type and m.date_created <= @endDate and m.date_created >= @startDate and activate = 1
   order by m.date_created
  end
 end
end
/* list only modified*/
else begin
 if ( @startDate = '0' ) begin
  if ( @endDate = '0' ) begin
   select m.meta_id, dt.type, m.meta_headline, m.date_modified
 
   from meta m
   join doc_types dt
      on m.doc_type = dt.doc_type
   where m.doc_type = @doc_type and activate = 1 and m.date_modified != m.date_created
   order by m.date_modified
  end
  else begin
   select m.meta_id, dt.type, m.meta_headline, m.date_modified
 
   from meta m
   join doc_types dt
      on m.doc_type = dt.doc_type
   where m.doc_type = @doc_type and m.date_modified <= @endDate and activate = 1 and m.date_modified != m.date_created
   order by m.date_modified
  end
 end
 else if ( @startDate != '0' ) begin
  if ( @endDate = '0' ) begin
   select m.meta_id, dt.type, m.meta_headline, m.date_modified
 
   from meta m
   join doc_types dt
      on m.doc_type = dt.doc_type
   where m.doc_type = @doc_type and m.date_modified >= @startDate and activate = 1 and m.date_modified != m.date_created
   order by m.date_modified
  end
  else
  begin
   select m.meta_id, dt.type, m.meta_headline, m.date_modified
 
   from meta m
   join doc_types dt
      on m.doc_type = dt.doc_type
   where m.doc_type = @doc_type and m.date_modified <= @endDate and m.date_modified >= @startDate and activate = 1 and m.date_modified != m.date_created
   order by m.date_modified
  end
 end
end
go
IF OBJECT_ID('imse.ListDocsByDate') IS NOT NULL
     DROP PROCEDURE imse.ListDocsBy_12192000100900001
ELSE 
     EXEC sp_rename 'imse.ListDocsBy_12192000100900001','ListDocsByDate',OBJECT
go
EXEC sp_rename 'imse.ListDocsGetInternalDocTypes','ListDocsGe_12192000100900002',OBJECT
go
CREATE PROCEDURE ListDocsGetInternalDocTypes AS
/* selct all internal doc types */
select doc_type, type 
from doc_types
where doc_type <= 100
go
IF OBJECT_ID('imse.ListDocsGetInternalDocTypes') IS NOT NULL
     DROP PROCEDURE imse.ListDocsGe_12192000100900002
ELSE 
     EXEC sp_rename 'imse.ListDocsGe_12192000100900002','ListDocsGetInternalDocTypes',OBJECT
go
EXEC sp_rename 'imse.ListDocsGetInternalDocTypesValue','ListDocsGe_12192000100900003',OBJECT
go
CREATE PROCEDURE ListDocsGetInternalDocTypesValue AS
/* selct all internal doc types */
select doc_type
from doc_types
where doc_type <= 100
go
IF OBJECT_ID('imse.ListDocsGetInternalDocTypesValue') IS NOT NULL
     DROP PROCEDURE imse.ListDocsGe_12192000100900003
ELSE 
     EXEC sp_rename 'imse.ListDocsGe_12192000100900003','ListDocsGetInternalDocTypesValue',OBJECT
go

--
-- Table Alter
-- dbo.fileupload_docs
--
ALTER TABLE dbo.fileupload_docs ADD CONSTRAINT FK_fileupload_docs_meta
FOREIGN KEY (meta_id)
REFERENCES dbo.meta (meta_id)
go

--
-- Table Alter
-- dbo.frameset_docs
--
ALTER TABLE dbo.frameset_docs ADD CONSTRAINT FK_frameset_docs_meta
FOREIGN KEY (meta_id)
REFERENCES dbo.meta (meta_id)
go

--
-- Table Alter
-- dbo.images
--
ALTER TABLE dbo.images ADD CONSTRAINT FK_images_meta
FOREIGN KEY (meta_id)
REFERENCES dbo.meta (meta_id)
go

--
-- Table Extended Alter
-- dbo.ip_accesses
--
EXEC sp_rename 'dbo.ip_accesses','ip_accesse_12192000100903000',OBJECT
go
CREATE TABLE dbo.ip_accesses 
(
    ip_access_id int           IDENTITY,
    user_id      int           NOT NULL,
    ip_start     decimal(18,0) NOT NULL,
    ip_end       decimal(18,0) NOT NULL
)
go
SET IDENTITY_INSERT dbo.ip_accesses ON
go
INSERT INTO dbo.ip_accesses(
                            ip_access_id,
                            user_id,
                            ip_start,
                            ip_end
                           )
                     SELECT 
                            ip_access_id,
                            user_id,
                            CONVERT(decimal(18,0),ip_start),
                            CONVERT(decimal(18,0),ip_end)
                       FROM dbo.ip_accesse_12192000100903000
go
SET IDENTITY_INSERT dbo.ip_accesses OFF
go
ALTER TABLE dbo.ip_accesses ADD CONSTRAINT PK_ip_accesses
PRIMARY KEY NONCLUSTERED (ip_access_id) 
go
EXEC sp_rename 'imse.IPAccessAdd','IPAccessAd_12192000100903001',OBJECT
go
CREATE PROCEDURE IPAccessAdd
/*
This function adds a new ip-access to the db. Used by AdminManager
*/
 @user_id int,
 @ip_start varchar(15) , 
 @ip_end varchar(15)
AS
INSERT INTO IP_ACCESSES ( user_id , ip_start , ip_end )
VALUES ( @user_id , @ip_start , @ip_end )
go
IF OBJECT_ID('imse.IPAccessAdd') IS NOT NULL
     DROP PROCEDURE imse.IPAccessAd_12192000100903001
ELSE 
     EXEC sp_rename 'imse.IPAccessAd_12192000100903001','IPAccessAdd',OBJECT
go
EXEC sp_rename 'imse.IPAccessDelete','IPAccessDe_12192000100903002',OBJECT
go
CREATE PROCEDURE IPAccessDelete
/*
 Deletes an Ip-access for a user. Used by the AdminIpAccess servlet
*/
 @ipAccessId int
AS
DELETE FROM IP_ACCESSES 
WHERE ip_access_id = @ipAccessId
go
IF OBJECT_ID('imse.IPAccessDelete') IS NOT NULL
     DROP PROCEDURE imse.IPAccessDe_12192000100903002
ELSE 
     EXEC sp_rename 'imse.IPAccessDe_12192000100903002','IPAccessDelete',OBJECT
go
EXEC sp_rename 'imse.IPAccessesGetAll','IPAccesses_12192000100903003',OBJECT
go
CREATE PROCEDURE IPAccessesGetAll AS
/*
Lets get all IPaccesses from db. Used  by the AdminIpAccesses
*/
SELECT ip.ip_access_id, ip.user_id, usr.login_name, ip.ip_start, ip.ip_end    
FROM IP_ACCESSES ip, USERS usr
WHERE ip.user_id = usr.user_id
go
IF OBJECT_ID('imse.IPAccessesGetAll') IS NOT NULL
     DROP PROCEDURE imse.IPAccesses_12192000100903003
ELSE 
     EXEC sp_rename 'imse.IPAccesses_12192000100903003','IPAccessesGetAll',OBJECT
go
EXEC sp_rename 'imse.IPAccessUpdate','IPAccessUp_12192000100903004',OBJECT
go
CREATE PROCEDURE IPAccessUpdate
/*
Updates the IPaccess table
*/
 @IpAccessId int ,
 @newUserId int,
 @newIpStart varchar(15) ,
 @newIpEnd varchar(15) 
AS
UPDATE IP_ACCESSES
SET user_id = @newUserId ,
 ip_start = @newIpStart,
 ip_end = @newIpEnd
WHERE ip_access_id = @IpAccessId
go
IF OBJECT_ID('imse.IPAccessUpdate') IS NOT NULL
     DROP PROCEDURE imse.IPAccessUp_12192000100903004
ELSE 
     EXEC sp_rename 'imse.IPAccessUp_12192000100903004','IPAccessUpdate',OBJECT
go

--
-- Table Extended Alter
-- dbo.languages
--
EXEC sp_rename 'dbo.languages','languages_12192000100904000',OBJECT
go
CREATE TABLE dbo.languages 
(
    lang_prefix varchar(3)  NOT NULL,
    user_prefix varchar(3)  NOT NULL,
    language    varchar(30) NULL
)
go
INSERT INTO dbo.languages(
                          lang_prefix,
                          user_prefix,
                          language
                         )
                   SELECT 
                          CONVERT(varchar(3),lang_prefix),
                          ' ',
                          language
                     FROM dbo.languages_12192000100904000
go
ALTER TABLE dbo.languages ADD CONSTRAINT PK_languages
PRIMARY KEY NONCLUSTERED (lang_prefix,user_prefix) 
go
EXEC sp_rename 'imse.GetLanguageList','GetLanguag_12192000100904001',OBJECT
go
CREATE PROCEDURE GetLanguageList AS
/*
 Returns all 
*/
SELECT lp.lang_id , lang.language
FROM lang_prefixes lp, languages lang
WHERE lp.lang_prefix = lang.lang_prefix
go
IF OBJECT_ID('imse.GetLanguageList') IS NOT NULL
     DROP PROCEDURE imse.GetLanguag_12192000100904001
ELSE 
     EXEC sp_rename 'imse.GetLanguag_12192000100904001','GetLanguageList',OBJECT
go
EXEC sp_rename 'imse.getLanguages','getLanguag_12192000100904002',OBJECT
go
CREATE PROCEDURE getLanguages AS
select lang_prefix,language from languages order by language
go
IF OBJECT_ID('imse.getLanguages') IS NOT NULL
     DROP PROCEDURE imse.getLanguag_12192000100904002
ELSE 
     EXEC sp_rename 'imse.getLanguag_12192000100904002','getLanguages',OBJECT
go

--
-- Table Extended Alter
-- dbo.meta
--
EXEC sp_rename 'meta.PK_meta','PK_meta_12192000100906001'
go
EXEC sp_rename 'dbo.meta','meta_12192000100906000',OBJECT
go
CREATE TABLE dbo.meta 
(
    meta_id        int           NOT NULL,
    description    varchar(80)   NOT NULL,
    doc_type       int           NOT NULL,
    meta_headline  varchar(255)  NOT NULL,
    meta_text      varchar(1000) NOT NULL,
    meta_image     varchar(255)  NOT NULL,
    owner_id       int           NOT NULL,
    permissions    int           NOT NULL,
    shared         int           NOT NULL,
    expand         int           NOT NULL,
    show_meta      int           NOT NULL,
    help_text_id   int           NOT NULL,
    archive        int           NOT NULL,
    status_id      int           NOT NULL,
    lang_prefix    varchar(3)    NOT NULL,
    classification varchar(20)   NOT NULL,
    date_created   datetime      NOT NULL,
    date_modified  datetime      NOT NULL,
    sort_position  int           NOT NULL,
    menu_position  int           NOT NULL,
    disable_search int           NULL,
    activated_date varchar(10)   NULL,
    activated_time varchar(6)    NULL,
    archived_date  varchar(10)   NULL,
    archived_time  varchar(6)    NULL,
    target         varchar(10)   NULL,
    frame_name     varchar(20)   NULL,
    activate       int           NULL
)
go
INSERT INTO dbo.meta(
                     meta_id,
                     description,
                     doc_type,
                     meta_headline,
                     meta_text,
                     meta_image,
                     owner_id,
                     permissions,
                     shared,
                     expand,
                     show_meta,
                     help_text_id,
                     archive,
                     status_id,
                     lang_prefix,
                     classification,
                     date_created,
                     date_modified,
                     sort_position,
                     menu_position,
                     disable_search,
                     activated_date,
                     activated_time,
                     archived_date,
                     archived_time,
                     target,
                     frame_name,
                     activate
                    )
              SELECT 
                     meta_id,
                     description,
                     doc_type,
                     meta_headline,
                     meta_text,
                     meta_image,
                     0,
                     0,
                     shared,
                     expand,
                     show_meta,
                     help_text_id,
                     archive,
                     status_id,
                     lang_prefix,
                     classification,
                     date_created,
                     date_modified,
                     sort_position,
                     menu_position,
                     disable_search,
                     activated_date,
                     activated_time,
                     archived_date,
                     archived_time,
                     target,
                     frame_name,
                     activate
                FROM dbo.meta_12192000100906000
go
ALTER TABLE dbo.browser_docs DROP CONSTRAINT FK_browser_docs_meta
go
ALTER TABLE dbo.domains_OLD DROP CONSTRAINT FK_domains_meta
go
ALTER TABLE dbo.meta_classification DROP CONSTRAINT FK_meta_classification_meta
go
ALTER TABLE dbo.text_docs DROP CONSTRAINT FK_text_docs_meta
go
ALTER TABLE dbo.meta ADD CONSTRAINT PK_meta
PRIMARY KEY NONCLUSTERED (meta_id) 
go
ALTER TABLE dbo.browser_docs ADD CONSTRAINT FK_browser_docs_meta
FOREIGN KEY (meta_id)
REFERENCES dbo.meta (meta_id)
go
ALTER TABLE dbo.domains_OLD ADD CONSTRAINT FK_domains_meta
FOREIGN KEY (start_meta_id)
REFERENCES dbo.meta (meta_id)
go
ALTER TABLE dbo.meta_classification ADD CONSTRAINT FK_meta_classification_meta
FOREIGN KEY (meta_id)
REFERENCES dbo.meta (meta_id)
go
ALTER TABLE dbo.text_docs ADD CONSTRAINT FK_text_docs_meta
FOREIGN KEY (meta_id)
REFERENCES dbo.meta (meta_id)
go
EXEC sp_rename 'dbo.getDocsParentCount','getDocsPar_12192000100906002',OBJECT
go
CREATE PROCEDURE getDocsParentCount @user_id int AS
-- Lists the documents that have parents, and how many, provided the user may write to them.
if exists ( -- Is user superadmin?
 select
  *
 from
  user_roles_crossref
 where
  user_id = @user_id
  and role_id = 0
) begin -- User is superadmin. Select all rows.
 select
  to_meta_id,
  count(meta_id) as parents
 from
  childs 
 group by
  to_meta_id
 union
 select
  distinct to_meta_id,
  count(meta_id) as parents
 from
  browser_docs
 group by
  to_meta_id
 order by
  to_meta_id
end else begin -- User is not superadmin.
 select
  to_meta_id,
  count(childs.meta_id) as parents
 from
  childs
  join meta
 on
  childs.to_meta_id = meta.meta_id
 where
  meta.meta_id in ( -- Is user member of role allowed to write to the document?
   select
    meta.meta_id 
   from
    meta
    join roles_rights
   on
    meta.meta_id = roles_rights.meta_id
    and roles_rights.permission_id = 3
    join user_roles_crossref
   on
    roles_rights.role_id = user_roles_crossref.role_id
    and user_roles_crossref.user_id = @user_id
  ) or meta.meta_id in ( -- Is user the owner of the document?
   select
    meta_id 
   from
    user_rights
   where
    user_id = @user_id
    and permission_id = 99
  )
 group by
  to_meta_id
 union
  select
   distinct to_meta_id,
   count(browser_docs.meta_id) as parents
  from
   browser_docs
   join meta
  on
   to_meta_id = meta.meta_id
  where
   meta.meta_id in ( -- Is user member of role allowed to write to the document?
    select
     meta.meta_id 
    from
     meta
     join roles_rights
    on
     meta.meta_id = roles_rights.meta_id
     and roles_rights.permission_id = 3
     join user_roles_crossref
    on
     roles_rights.role_id = user_roles_crossref.role_id
     and user_roles_crossref.user_id = @user_id
   ) or meta.meta_id in ( -- Is user the owner of the document?
    select
     meta_id 
    from
     user_rights
    where
     user_id = @user_id
     and permission_id = 99
   )
  group by
   to_meta_id
 order by 
  to_meta_id
end
go
IF OBJECT_ID('dbo.getDocsParentCount') IS NOT NULL
     DROP PROCEDURE dbo.getDocsPar_12192000100906002
ELSE 
     EXEC sp_rename 'dbo.getDocsPar_12192000100906002','getDocsParentCount',OBJECT
go
EXEC sp_rename 'dbo.testProc','testProc_12192000100906003',OBJECT
go
CREATE PROCEDURE testProc AS
select * from meta
go
IF OBJECT_ID('dbo.testProc') IS NOT NULL
     DROP PROCEDURE dbo.testProc_12192000100906003
ELSE 
     EXEC sp_rename 'dbo.testProc_12192000100906003','testProc',OBJECT
go
EXEC sp_rename 'imse.classification_convert','classifica_12192000100906004',OBJECT
go
CREATE PROCEDURE imse.classification_convert AS
-- Hämta alla klassificeringskoder och för över dem till nya systemet. Observera att denna inte fixar
-- de fält som har , tecken som delimiter
DECLARE @meta_id int
DECLARE @class varchar(200)
DECLARE tmpCursor CURSOR FOR
 SELECT meta_id, classification
 FROM meta
 WHERE classification IS NOT NULL
 and classification <> ''
 and classification NOT LIKE 'META NAME%'
 and classification NOT LIKE 'Test'
 -- AND meta_id = 2201
OPEN tmpCursor
FETCH NEXT FROM tmpCursor INTO @meta_id, @class
WHILE @@fetch_status = 0 BEGIN
 PRINT 'Class: ' + @class 
EXEC classification_fix @meta_id, @class 
 FETCH NEXT FROM tmpCursor INTO @meta_id, @class
END
CLOSE tmpCursor
DEALLOCATE tmpCursor
go
IF OBJECT_ID('imse.classification_convert') IS NOT NULL
     DROP PROCEDURE imse.classifica_12192000100906004
ELSE 
     EXEC sp_rename 'imse.classifica_12192000100906004','classification_convert',OBJECT
go
EXEC sp_rename 'imse.Classification_Get_All','Classifica_12192000100906005',OBJECT
go
CREATE PROCEDURE Classification_Get_All AS
/*
Get the meta_id and classifcation so we can start convert them
*/
SELECT meta_id, classification
 FROM meta
 WHERE classification IS NOT NULL
 and classification <> ''
 and classification NOT LIKE 'META NAME%'
 and classification NOT LIKE 'Test'
go
IF OBJECT_ID('imse.Classification_Get_All') IS NOT NULL
     DROP PROCEDURE imse.Classifica_12192000100906005
ELSE 
     EXEC sp_rename 'imse.Classifica_12192000100906005','Classification_Get_All',OBJECT
go
EXEC sp_rename 'imse.FindMetaId','FindMetaId_12192000100906006',OBJECT
go
CREATE PROCEDURE [FindMetaId]
 @meta_id int
 AS
SELECT meta_id 
FROM meta
WHERE meta_id = @meta_id
go
IF OBJECT_ID('imse.FindMetaId') IS NOT NULL
     DROP PROCEDURE imse.FindMetaId_12192000100906006
ELSE 
     EXEC sp_rename 'imse.FindMetaId_12192000100906006','FindMetaId',OBJECT
go
EXEC sp_rename 'imse.GetAdminChilds','GetAdminCh_12192000100906007',OBJECT
go
CREATE PROCEDURE GetAdminChilds
@meta_id int,
@user_id int
AS
select   to_meta_id
from   childs c
join   meta m    
     on   m.meta_id = c.to_meta_id    -- meta.meta_id corresponds to childs.to_meta_id
      and m.activate != 0      -- Only include the documents that are active in the meta table.
      and c.meta_id = @meta_id     -- Only include documents that are children to this particular meta_id
left join  roles_rights rr        -- We may have permission, even though we don't have anything in role-permissions... That is, if we're docowner or superadmin.
     on  c.to_meta_id = rr.meta_id     -- Only include rows with the documents we are interested in
       and rr.permission_id  = 3     -- Only include permissions that gives right to change the document
join  user_roles_crossref urc       -- This table tells us which users have which roles
     on  urc.user_id = @user_id     -- Only include the rows with the user we are interested in.
      and ( rr.role_id = urc.role_id    -- Include rows where the users roles match the roles that have permissions on the documents
       or urc.role_id = 0     -- and also include the rows that tells us this user is a superadmin (That is, return a row with urc.role_id = 0 for each document.)
       )
left join  user_rights ur        -- We may have permission, even though we don't own any documents... That is, if we're roleadmins or superadmin. When we lose ownership-based rights, this may be skipped.
     on  ur.meta_id = c.to_meta_id    -- Only include rows with the documents we are interested in
      and ur.user_id =  urc.user_id     -- Only include rows with the user  we are interested in
      and ur.permission_id = 99     -- Only include rows that mean "Ownership"
go
IF OBJECT_ID('imse.GetAdminChilds') IS NOT NULL
     DROP PROCEDURE imse.GetAdminCh_12192000100906007
ELSE 
     EXEC sp_rename 'imse.GetAdminCh_12192000100906007','GetAdminChilds',OBJECT
go
EXEC sp_rename 'imse.getBrowserDocChilds','getBrowser_12192000100906008',OBJECT
go
CREATE PROCEDURE getBrowserDocChilds @meta_id int, @user_id int AS
-- Lists the childs for a specific browser_doc
SELECT DISTINCT to_meta_id,
   meta_headline
FROM   browser_docs bd
JOIN   meta m   ON bd.to_meta_id = m.meta_id
      AND bd.meta_id = @meta_id
LEFT JOIN  user_rights ur  ON ur.meta_id = m.meta_id
      AND ur.user_id = @user_id
      AND ur.permission_id = 99
LEFT JOIN  roles_rights rr  ON rr.meta_id = m.meta_id
      AND rr.permission_id = 3
JOIN   user_roles_crossref urc ON urc.user_id = @user_id
      AND (
        urc.role_id = 0
       OR (
         urc.role_id = rr.role_id
        AND urc.user_id = ur.user_id
        )
       OR m.shared = 1
       )
WHERE m.activate = 1
ORDER BY to_meta_id
go
IF OBJECT_ID('imse.getBrowserDocChilds') IS NOT NULL
     DROP PROCEDURE imse.getBrowser_12192000100906008
ELSE 
     EXEC sp_rename 'imse.getBrowser_12192000100906008','getBrowserDocChilds',OBJECT
go
EXEC sp_rename 'imse.GetChilds','GetChilds_12192000100906009',OBJECT
go
CREATE PROCEDURE GetChilds
 @meta_id int,
 @user_id int
AS
/*
Nice little query that lists the children of a document that a particular user may see, and includes a field that tells you wether he may do something to it or not.
*/
declare @sort_by int
select @sort_by = sort_order from text_docs where meta_id = @meta_id
-- Manual sort order
if @sort_by = 2
begin
select  to_meta_id, c.menu_sort,manual_sort_order, doc_type,
  archive,target, left(convert (varchar,date_created,120),10),
  meta_headline,meta_text,meta_image,frame_name,
  activated_date+activated_time,archived_date+archived_time,
  min(            -- This field will have 0 in it, if the user may change the document
   coalesce(urc.role_id,rr.permission_id-3,ur.permission_id-99)*
   coalesce(ur.permission_id-99,rr.permission_id-3,urc.role_id)*
   coalesce(rr.permission_id-3,urc.role_id,ur.permission_id-99)
  )
from   childs c
join   meta m    
     on   m.meta_id = c.to_meta_id    -- meta.meta_id corresponds to childs.to_meta_id
      and m.activate != 0      -- Only include the documents that are active in the meta table.
      and c.meta_id = @meta_id     -- Only include documents that are children to this particular meta_id
left join  roles_rights rr         -- We may have permission, even though we don't have anything in role-permissions... That is, if we're docowner or superadmin.
     on  c.to_meta_id = rr.meta_id     -- Only include rows with the documents we are interested in
       --and (rr.permission_id & 1) != 0    -- Only include permissions where the first bit is set. "Read rights" (May skip this now, since right now, if we have anything, this matches.)
join  user_roles_crossref urc        -- This table tells us which users have which roles
     on  urc.user_id = @user_id     -- Only include the rows with the user we are interested in...
      and ( 
        rr.role_id = urc.role_id    -- Include rows where the users roles match the roles that have permissions on the documents
       or urc.role_id = 0     -- and also include the rows that tells us this user is a superadmin
       or ( 
         m.show_meta != 0   -- and also include documents that are to be shown regardless of rights. (Visa även för obehöriga)
        and rr.permission_id != 3   -- Don't include show_meta if it is not needed.
        ) 
       )
left join  user_rights ur         -- We may have permission, even though we don't own any documents... That is, if we're roleadmins or superadmin. When we lose ownership-based rights, this may be skipped.
     on  ur.meta_id = c.to_meta_id    -- Only include rows with the documents we are interested in
      and ur.user_id =  urc.user_id     -- Only include rows with the user  we are interested in
      and ur.permission_id = 99     -- Only include rows that mean "Ownership"
group by to_meta_id, c.menu_sort,manual_sort_order, doc_type,
  archive,target, left(convert (varchar,date_created,120),10),
  meta_headline,meta_text,meta_image,frame_name,
  activated_date+activated_time,archived_date+archived_time
order by  menu_sort,c.manual_sort_order desc
end
else if @sort_by = 3
begin
select  to_meta_id, c.menu_sort,manual_sort_order, doc_type,
  archive,target, left(convert (varchar,date_created,120),10),
  meta_headline,meta_text,meta_image,frame_name,
  activated_date+activated_time,archived_date+archived_time,
  min(
   coalesce(urc.role_id,rr.permission_id-3,ur.permission_id-99)*
   coalesce(ur.permission_id-99,rr.permission_id-3,urc.role_id)*
   coalesce(rr.permission_id-3,urc.role_id,ur.permission_id-99)
  )
from   childs c
join   meta m    
     on   m.meta_id = c.to_meta_id    -- meta.meta_id corresponds to childs.to_meta_id
      and m.activate != 0      -- Only include the documents that are active in the meta table.
      and c.meta_id = @meta_id     -- Only include documents that are children to this particular meta_id
left join  roles_rights rr         -- We may have permission, even though we don't have anything in role-permissions... That is, if we're docowner or superadmin.
     on  c.to_meta_id = rr.meta_id     -- Only include rows with the documents we are interested in
       --and (rr.permission_id & 1) != 0    -- Only include permissions where the first bit is set. "Read rights" (May skip this now, since right now, if we have anything, this matches.)
join  user_roles_crossref urc        -- This table tells us which users have which roles
     on  urc.user_id = @user_id     -- Only include the rows with the user we are interested in...
      and ( 
        rr.role_id = urc.role_id    -- Include rows where the users roles match the roles that have permissions on the documents
       or urc.role_id = 0     -- and also include the rows that tells us this user is a superadmin
       or ( 
         m.show_meta != 0   -- and also include documents that are to be shown regardless of rights. (Visa även för obehöriga)
        and rr.permission_id != 3   -- Don't include show_meta if it is not needed.
        ) 
       )
left join  user_rights ur         -- We may have permission, even though we don't own any documents... That is, if we're roleadmins or superadmin. When we lose ownership-based rights, this may be skipped.
     on  ur.meta_id = c.to_meta_id    -- Only include rows with the documents we are interested in
      and ur.user_id =  urc.user_id     -- Only include rows with the user  we are interested in
      and ur.permission_id = 99     -- Only include rows that mean "Ownership"
group by to_meta_id, c.menu_sort,manual_sort_order, doc_type,
  archive,target, left(convert (varchar,date_created,120),10),
  meta_headline,meta_text,meta_image,frame_name,
  activated_date+activated_time,archived_date+archived_time
order by  menu_sort,left(convert (varchar,date_created,120),10) desc
end
else if @sort_by = 1
begin
select  to_meta_id, c.menu_sort,manual_sort_order, doc_type,
  archive,target, left(convert (varchar,date_created,120),10),
  meta_headline,meta_text,meta_image,frame_name,
  activated_date+activated_time,archived_date+archived_time,
  min(
   coalesce(urc.role_id,rr.permission_id-3,ur.permission_id-99)*
   coalesce(ur.permission_id-99,rr.permission_id-3,urc.role_id)*
   coalesce(rr.permission_id-3,urc.role_id,ur.permission_id-99)
  )
from   childs c
join   meta m    
     on   m.meta_id = c.to_meta_id    -- meta.meta_id corresponds to childs.to_meta_id
      and m.activate != 0      -- Only include the documents that are active in the meta table.
      and c.meta_id = @meta_id     -- Only include documents that are children to this particular meta_id
left join  roles_rights rr         -- We may have permission, even though we don't have anything in role-permissions... That is, if we're docowner or superadmin.
     on  c.to_meta_id = rr.meta_id     -- Only include rows with the documents we are interested in
       --and (rr.permission_id & 1) != 0    -- Only include permissions where the first bit is set. "Read rights" (May skip this now, since right now, if we have anything, this matches.)
join  user_roles_crossref urc        -- This table tells us which users have which roles
     on  urc.user_id = @user_id     -- Only include the rows with the user we are interested in...
      and ( 
        rr.role_id = urc.role_id    -- Include rows where the users roles match the roles that have permissions on the documents
       or urc.role_id = 0     -- and also include the rows that tells us this user is a superadmin
       or ( 
         m.show_meta != 0   -- and also include documents that are to be shown regardless of rights. (Visa även för obehöriga)
        and rr.permission_id != 3   -- Don't include show_meta if it is not needed.
        ) 
       )
left join  user_rights ur         -- We may have permission, even though we don't own any documents... That is, if we're roleadmins or superadmin. When we lose ownership-based rights, this may be skipped.
     on  ur.meta_id = c.to_meta_id    -- Only include rows with the documents we are interested in
      and ur.user_id =  urc.user_id     -- Only include rows with the user  we are interested in
      and ur.permission_id = 99     -- Only include rows that mean "Ownership"
group by to_meta_id, c.menu_sort,manual_sort_order, doc_type,
  archive,target, left(convert (varchar,date_created,120),10),
  meta_headline,meta_text,meta_image,frame_name,
  activated_date+activated_time,archived_date+archived_time
order by  menu_sort,meta_headline
end
go
IF OBJECT_ID('imse.GetChilds') IS NOT NULL
     DROP PROCEDURE imse.GetChilds_12192000100906009
ELSE 
     EXEC sp_rename 'imse.GetChilds_12192000100906009','GetChilds',OBJECT
go
EXEC sp_rename 'imse.getDocs','getDocs_12192000100906010',OBJECT
go
CREATE PROCEDURE getDocs @user_id int, @start int, @end int AS
-- Lists documents user is allowed to see.
SELECT DISTINCT m.meta_id,
   COUNT(DISTINCT c.meta_id) parentcount,
   meta_headline,
   doc_type
FROM   meta m
LEFT JOIN  childs c   ON c.to_meta_id = m.meta_id
LEFT JOIN  user_rights ur  ON ur.meta_id = m.meta_id
      AND ur.permission_id = 99
LEFT JOIN  roles_rights rr  ON rr.meta_id = m.meta_id
      AND rr.permission_id = 3
JOIN   user_roles_crossref urc ON urc.user_id = @user_id
      AND (
        urc.role_id = 0
       OR (
         urc.role_id = rr.role_id
        AND urc.user_id = ur.user_id
        )
       OR m.shared = 1
       )
WHERE  m.activate = 1
  AND m.meta_id > (@start-1) 
  AND m.meta_id < (@end+1)
GROUP BY  m.meta_id,m.meta_headline,m.doc_type,c.to_meta_id
ORDER BY  m.meta_id
go
IF OBJECT_ID('imse.getDocs') IS NOT NULL
     DROP PROCEDURE imse.getDocs_12192000100906010
ELSE 
     EXEC sp_rename 'imse.getDocs_12192000100906010','getDocs',OBJECT
go
EXEC sp_rename 'imse.GetDocType','GetDocType_12192000100906011',OBJECT
go
CREATE PROCEDURE GetDocType
 @meta_id int
AS
/*
 Used by external systems to get the docType
*/
SELECT doc_type
FROM meta
WHERE meta_id = @meta_id
go
IF OBJECT_ID('imse.GetDocType') IS NOT NULL
     DROP PROCEDURE imse.GetDocType_12192000100906011
ELSE 
     EXEC sp_rename 'imse.GetDocType_12192000100906011','GetDocType',OBJECT
go
EXEC sp_rename 'imse.GetLangPrefix','GetLangPre_12192000100906012',OBJECT
go
CREATE PROCEDURE GetLangPrefix
 @meta_id int
AS
/*
 Used by external systems to get the langprefix
*/
SELECT lang_prefix 
FROM meta
WHERE meta_id = @meta_id
go
IF OBJECT_ID('imse.GetLangPrefix') IS NOT NULL
     DROP PROCEDURE imse.GetLangPre_12192000100906012
ELSE 
     EXEC sp_rename 'imse.GetLangPre_12192000100906012','GetLangPrefix',OBJECT
go
EXEC sp_rename 'imse.getMenuDocChilds','getMenuDoc_12192000100906013',OBJECT
go
CREATE PROCEDURE getMenuDocChilds @meta_id int, @user_id int AS
-- Lists the childs of menudoc @meta_id
SELECT DISTINCT to_meta_id,
   meta_headline
FROM   childs c
JOIN   meta m   ON c.to_meta_id = m.meta_id
      AND c.meta_id = @meta_id
LEFT JOIN  user_rights ur  ON ur.meta_id = m.meta_id
      AND ur.user_id = @user_id
      AND ur.permission_id = 99
LEFT JOIN  roles_rights rr  ON rr.meta_id = m.meta_id
      AND rr.permission_id = 3
JOIN   user_roles_crossref urc ON urc.user_id = @user_id
      AND ( urc.role_id = 0
       OR (
         urc.role_id = rr.role_id
        AND urc.user_id = ur.user_id
        )
       OR m.shared = 1
       )
WHERE m.activate = 1
ORDER BY to_meta_id
go
IF OBJECT_ID('imse.getMenuDocChilds') IS NOT NULL
     DROP PROCEDURE imse.getMenuDoc_12192000100906013
ELSE 
     EXEC sp_rename 'imse.getMenuDoc_12192000100906013','getMenuDocChilds',OBJECT
go
EXEC sp_rename 'imse.GetMetaPathInfo','GetMetaPat_12192000100906014',OBJECT
go
CREATE PROCEDURE GetMetaPathInfo
 @meta_id int
AS
/*
 Used by external systems to get the meta_id dependent part of the path to
for example the image folder or to the html folder
Ex of what this function will return: 
*/
DECLARE @docType char(20)
DECLARE @langPrefix char(20)
SELECT RTRIM(lang_prefix) + '/' +  RTRIM(doc_type) + '/' 
FROM META 
WHERE meta_id = @meta_id
go
IF OBJECT_ID('imse.GetMetaPathInfo') IS NOT NULL
     DROP PROCEDURE imse.GetMetaPat_12192000100906014
ELSE 
     EXEC sp_rename 'imse.GetMetaPat_12192000100906014','GetMetaPathInfo',OBJECT
go
EXEC sp_rename 'imse.IMC_CreateNewMeta','IMC_Create_12192000100906015',OBJECT
go
CREATE PROCEDURE IMC_CreateNewMeta
/* create new metadata */
@new_meta_id int,
@description varchar(80),
@doc_type int,
@meta_head_line varchar(255),
@meta_text varchar(1000),
@meta_image varchar(255),
@category_id int,
@processing_id int,
@shared int,
@expanded int,
@show_meta int,
@help_text_id int, /* help_text_id = 1 */
@archived int,
@status_id int,   /* status_id  = 1 */
@lang_prefix varchar(3),
@classification varchar(20),
@date_created datetime,
@date_modified datetime,
@sort_position int,
@menu_position int,
@disable_search int,
@activated_date varchar(10),
@activated_time varchar(6),
@archived_date varchar(10),
@archived_time varchar(6),
@target varchar(10),
@frame_name varchar(20),
@activate int
 AS
insert into meta
values(@new_meta_id ,@description,@doc_type,@meta_head_line,@meta_text,@meta_image, @category_id,
@processing_id,@shared,@expanded,@help_text_id,@show_meta,@archived,@status_id,@lang_prefix,@classification,@date_created,
@date_modified, @sort_position,@menu_position,@disable_search,@activated_date,@activated_time,@archived_date,
@archived_time,@target,@frame_name,@activate)
go
IF OBJECT_ID('imse.IMC_CreateNewMeta') IS NOT NULL
     DROP PROCEDURE imse.IMC_Create_12192000100906015
ELSE 
     EXEC sp_rename 'imse.IMC_Create_12192000100906015','IMC_CreateNewMeta',OBJECT
go
EXEC sp_rename 'imse.IMC_GetMaxMetaID','IMC_GetMax_12192000100906016',OBJECT
go
CREATE PROCEDURE IMC_GetMaxMetaID AS
/* get max meta id */
select max(meta_id) from meta
go
IF OBJECT_ID('imse.IMC_GetMaxMetaID') IS NOT NULL
     DROP PROCEDURE imse.IMC_GetMax_12192000100906016
ELSE 
     EXEC sp_rename 'imse.IMC_GetMax_12192000100906016','IMC_GetMaxMetaID',OBJECT
go
EXEC sp_rename 'imse.ListConferences','ListConfer_12192000100906017',OBJECT
go
CREATE PROCEDURE ListConferences AS
select meta_id, meta_headline 
from meta 
where doc_type = 102
go
IF OBJECT_ID('imse.ListConferences') IS NOT NULL
     DROP PROCEDURE imse.ListConfer_12192000100906017
ELSE 
     EXEC sp_rename 'imse.ListConfer_12192000100906017','ListConferences',OBJECT
go
EXEC sp_rename 'imse.ListDocsByDate','ListDocsBy_12192000100906018',OBJECT
go
CREATE PROCEDURE imse.ListDocsByDate @listMod int,  @doc_type int, @startDate varchar(10), @endDate varchar(20) AS
/*
 lists doctyps where activate = 1
 @listMod 0 = all date, 1 = only creatdat, 2 = only modifieddata
 @startDoc yyyy-mm-dd or 0 then not set
 @endDate yyyy-mm-dd or 0 then not set
*/
-- Listdate fix 
if ( @endDate <> '0') BEGIN
 SET @endDate = @endDate + ' 23:59:59'
 PRINT @endDate
END 
/* list all (not in use ) */
if ( @listMod = 0) begin
 if ( @startDate = '0' ) begin
  if ( @endDate = '0' ) begin
   select m.meta_id, dt.type, m.meta_headline, m.date_modified
 
   from meta m
   join doc_types dt
      on m.doc_type = dt.doc_type
   where m.doc_type = @doc_type and activate = 1
   order by m.date_modified
  end
  else begin
   select m.meta_id, dt.type, m.meta_headline, m.date_modified
 
   from meta m
   join doc_types dt
      on m.doc_type = dt.doc_type
   where m.doc_type = @doc_type and m.date_modified <= @endDate and activate = 1
   order by m.date_modified
  end
 end
 else if ( @startDate != '0' ) begin
  if ( @endDate = '0' ) begin
   select m.meta_id, dt.type, m.meta_headline, m.date_modified
 
   from meta m
   join doc_types dt
      on m.doc_type = dt.doc_type
   where m.doc_type = @doc_type and m.date_modified >= @startDate and activate = 1
   order by m.date_modified
  end
  else
  begin
   select m.meta_id, dt.type, m.meta_headline, m.date_modified
 
   from meta m
   join doc_types dt
      on m.doc_type = dt.doc_type
   where m.doc_type = @doc_type and m.date_modified <= @endDate and m.date_modified >= @startDate and activate = 1
   order by m.date_modified
  end
 end
end
/* list creatdate */
else if ( @listMod = 1) begin
 if ( @startDate = '0' ) begin
  if ( @endDate = '0' ) begin
   select m.meta_id, dt.type, m.meta_headline, m.date_created
 
   from meta m
   join doc_types dt
      on m.doc_type = dt.doc_type
   where m.doc_type = @doc_type and activate = 1
   order by m.date_created
  end
  else begin
   select m.meta_id, dt.type, m.meta_headline, m.date_created
 
   from meta m
   join doc_types dt
      on m.doc_type = dt.doc_type
   where m.doc_type = @doc_type and m.date_created <= @endDate and activate = 1
   order by m.date_created
  end
 end
 else if ( @startDate != '0' ) begin
  if ( @endDate = '0' ) begin
   select m.meta_id, dt.type, m.meta_headline, m.date_created
 
   from meta m
   join doc_types dt
      on m.doc_type = dt.doc_type
   where m.doc_type = @doc_type and m.date_created >= @startDate and activate = 1
   order by m.date_created
  end
  else
  begin
   select m.meta_id, dt.type, m.meta_headline, m.date_created
 
   from meta m
   join doc_types dt
      on m.doc_type = dt.doc_type
   where m.doc_type = @doc_type and m.date_created <= @endDate and m.date_created >= @startDate and activate = 1
   order by m.date_created
  end
 end
end
/* list only modified*/
else begin
 if ( @startDate = '0' ) begin
  if ( @endDate = '0' ) begin
   select m.meta_id, dt.type, m.meta_headline, m.date_modified
 
   from meta m
   join doc_types dt
      on m.doc_type = dt.doc_type
   where m.doc_type = @doc_type and activate = 1 and m.date_modified != m.date_created
   order by m.date_modified
  end
  else begin
   select m.meta_id, dt.type, m.meta_headline, m.date_modified
 
   from meta m
   join doc_types dt
      on m.doc_type = dt.doc_type
   where m.doc_type = @doc_type and m.date_modified <= @endDate and activate = 1 and m.date_modified != m.date_created
   order by m.date_modified
  end
 end
 else if ( @startDate != '0' ) begin
  if ( @endDate = '0' ) begin
   select m.meta_id, dt.type, m.meta_headline, m.date_modified
 
   from meta m
   join doc_types dt
      on m.doc_type = dt.doc_type
   where m.doc_type = @doc_type and m.date_modified >= @startDate and activate = 1 and m.date_modified != m.date_created
   order by m.date_modified
  end
  else
  begin
   select m.meta_id, dt.type, m.meta_headline, m.date_modified
 
   from meta m
   join doc_types dt
      on m.doc_type = dt.doc_type
   where m.doc_type = @doc_type and m.date_modified <= @endDate and m.date_modified >= @startDate and activate = 1 and m.date_modified != m.date_created
   order by m.date_modified
  end
 end
end
go
IF OBJECT_ID('imse.ListDocsByDate') IS NOT NULL
     DROP PROCEDURE imse.ListDocsBy_12192000100906018
ELSE 
     EXEC sp_rename 'imse.ListDocsBy_12192000100906018','ListDocsByDate',OBJECT
go
EXEC sp_rename 'imse.meta_select','meta_selec_12192000100906019',OBJECT
go
CREATE PROCEDURE meta_select AS
select menu_sort,manual_sort_order,date_created,meta_headline,target from meta,childs,roles_rights,user_roles_crossref 
where meta.meta_id = childs.to_meta_id 
and childs.meta_id = 4260 
and meta.archive=0 
and meta.activate=1 
and  roles_rights.meta_id = childs.to_meta_id 
and roles_rights.permission_id > 0 
and roles_rights.role_id = user_roles_crossref.role_id 
and user_roles_crossref.user_id =99
and meta.activated_date + ' ' + meta.activated_time <= '2000-05-02 02:37' 
and (meta.archived_date + ' ' + meta.archived_time >= '2000-05-02 02:37' or meta.archived_date + ' ' + meta.archived_time = '') 
union 
 select menu_sort,manual_sort_order,date_created,meta_headline,target from meta,childs,user_rights 
where meta.meta_id = childs.to_meta_id 
and childs.meta_id =  4260
and meta.archive=0 
and meta.activate=1 
and user_rights.meta_id = childs.to_meta_id 
and user_rights.permission_id > 0 
and user_rights.user_id = 99
and meta.activated_date + ' ' + meta.activated_time <= '2000-05-02 02:37' 
and (meta.archived_date + ' ' + meta.archived_time >= '2000-05-02 02:37' 
or meta.archived_date + ' ' + meta.archived_time = '') 
order by menu_sort,meta.meta_headline
go
IF OBJECT_ID('imse.meta_select') IS NOT NULL
     DROP PROCEDURE imse.meta_selec_12192000100906019
ELSE 
     EXEC sp_rename 'imse.meta_selec_12192000100906019','meta_select',OBJECT
go

--
-- Table Alter
-- dbo.meta_classification
--
ALTER TABLE dbo.meta_classification DROP CONSTRAINT FK_meta_classification_classification
go
ALTER TABLE dbo.meta_classification ADD CONSTRAINT FK_meta_classification_meta
FOREIGN KEY (meta_id)
REFERENCES dbo.meta (meta_id)
go
ALTER TABLE dbo.meta_classification DROP CONSTRAINT FK_meta_classification_meta
go
ALTER TABLE dbo.meta_classification ADD CONSTRAINT FK_meta_classification_classification
FOREIGN KEY (class_id)
REFERENCES dbo.classification (class_id)
go

--
-- Table Extended Alter
-- dbo.mime_types
--
EXEC sp_rename 'dbo.mime_types','mime_types_12192000100908000',OBJECT
go
CREATE TABLE dbo.mime_types 
(
    mime_id     int         IDENTITY,
    mime_name   varchar(50) NOT NULL,
    mime        varchar(50) NOT NULL,
    lang_prefix varchar(3)  DEFAULT 'se' NOT NULL
)
go
SET IDENTITY_INSERT dbo.mime_types ON
go
INSERT INTO dbo.mime_types(
                           mime_id,
                           mime_name,
                           mime,
                           lang_prefix
                          )
                    SELECT 
                           mime_id,
                           mime_name,
                           mime,
                           lang_prefix
                      FROM dbo.mime_types_12192000100908000
go
SET IDENTITY_INSERT dbo.mime_types OFF
go
ALTER TABLE dbo.mime_types ADD CONSTRAINT PK_mime_types
PRIMARY KEY NONCLUSTERED (mime_id) 
go

--
-- Table Create
-- dbo.new_doc_permission_sets
--
CREATE TABLE dbo.new_doc_permission_sets 
(
    meta_id       int NOT NULL,
    set_id        int NOT NULL,
    permission_id int NOT NULL,
    CONSTRAINT PK_new_doc_permission_sets
    PRIMARY KEY NONCLUSTERED (meta_id,set_id)
)
go

--
-- Table Create
-- dbo.new_doc_permission_sets_ex
--
CREATE TABLE dbo.new_doc_permission_sets_ex 
(
    meta_id         int NOT NULL,
    set_id          int NOT NULL,
    permission_id   int NOT NULL,
    permission_data int NOT NULL,
    CONSTRAINT PK_new_doc_permission_sets_ex
    PRIMARY KEY NONCLUSTERED (meta_id,set_id,permission_id,permission_data)
)
go

--
-- Table Create
-- dbo.permission_sets
--
CREATE TABLE dbo.permission_sets 
(
    set_id      int         NOT NULL,
    description varchar(30) NOT NULL,
    CONSTRAINT PK_permission_types
    PRIMARY KEY NONCLUSTERED (set_id)
)
go

--
-- Table Extended Alter
-- dbo.permissions
--
EXEC sp_rename 'dbo.permissions','permission_12192000100909000',OBJECT
go
CREATE TABLE dbo.permissions 
(
    permission_id tinyint     NOT NULL,
    lang_prefix   varchar(3)  DEFAULT 'se' NOT NULL,
    description   varchar(50) NOT NULL
)
go
INSERT INTO dbo.permissions(
                            permission_id,
--                          lang_prefix,
                            description
                           )
                     SELECT 
                            permission_id,
--                          'se',
                            CONVERT(varchar(50),description)
                       FROM dbo.permission_12192000100909000
go
ALTER TABLE dbo.permissions ADD CONSTRAINT PK_permissions
PRIMARY KEY NONCLUSTERED (permission_id,lang_prefix) 
go

--
-- Table Alter
-- dbo.phones
--
ALTER TABLE dbo.phones ADD CONSTRAINT PK_phones
PRIMARY KEY NONCLUSTERED (phone_id,user_id) 
go

--
-- Table Alter
-- dbo.roles
--
ALTER TABLE dbo.roles ADD CONSTRAINT PK_roles
PRIMARY KEY NONCLUSTERED (role_id) 
go

--
-- Table Extended Alter
-- dbo.roles_rights
--
EXEC sp_rename 'dbo.roles_rights','roles_righ_12192000100912000',OBJECT
go
CREATE TABLE dbo.roles_rights 
(
    role_id int     NOT NULL,
    meta_id int     NOT NULL,
    set_id  tinyint NOT NULL
)
go
INSERT INTO dbo.roles_rights(
                             role_id,
                             meta_id,
                             set_id
                            )
                      SELECT 
                             role_id,
                             meta_id,
                             0
                        FROM dbo.roles_righ_12192000100912000
go
ALTER TABLE dbo.roles_rights ADD CONSTRAINT PK_roles_rights
PRIMARY KEY NONCLUSTERED (role_id,meta_id,set_id) 
go
EXEC sp_rename 'dbo.getDocsParentCount','getDocsPar_12192000100912001',OBJECT
go
CREATE PROCEDURE getDocsParentCount @user_id int AS
-- Lists the documents that have parents, and how many, provided the user may write to them.
if exists ( -- Is user superadmin?
 select
  *
 from
  user_roles_crossref
 where
  user_id = @user_id
  and role_id = 0
) begin -- User is superadmin. Select all rows.
 select
  to_meta_id,
  count(meta_id) as parents
 from
  childs 
 group by
  to_meta_id
 union
 select
  distinct to_meta_id,
  count(meta_id) as parents
 from
  browser_docs
 group by
  to_meta_id
 order by
  to_meta_id
end else begin -- User is not superadmin.
 select
  to_meta_id,
  count(childs.meta_id) as parents
 from
  childs
  join meta
 on
  childs.to_meta_id = meta.meta_id
 where
  meta.meta_id in ( -- Is user member of role allowed to write to the document?
   select
    meta.meta_id 
   from
    meta
    join roles_rights
   on
    meta.meta_id = roles_rights.meta_id
    and roles_rights.permission_id = 3
    join user_roles_crossref
   on
    roles_rights.role_id = user_roles_crossref.role_id
    and user_roles_crossref.user_id = @user_id
  ) or meta.meta_id in ( -- Is user the owner of the document?
   select
    meta_id 
   from
    user_rights
   where
    user_id = @user_id
    and permission_id = 99
  )
 group by
  to_meta_id
 union
  select
   distinct to_meta_id,
   count(browser_docs.meta_id) as parents
  from
   browser_docs
   join meta
  on
   to_meta_id = meta.meta_id
  where
   meta.meta_id in ( -- Is user member of role allowed to write to the document?
    select
     meta.meta_id 
    from
     meta
     join roles_rights
    on
     meta.meta_id = roles_rights.meta_id
     and roles_rights.permission_id = 3
     join user_roles_crossref
    on
     roles_rights.role_id = user_roles_crossref.role_id
     and user_roles_crossref.user_id = @user_id
   ) or meta.meta_id in ( -- Is user the owner of the document?
    select
     meta_id 
    from
     user_rights
    where
     user_id = @user_id
     and permission_id = 99
   )
  group by
   to_meta_id
 order by 
  to_meta_id
end
go
IF OBJECT_ID('dbo.getDocsParentCount') IS NOT NULL
     DROP PROCEDURE dbo.getDocsPar_12192000100912001
ELSE 
     EXEC sp_rename 'dbo.getDocsPar_12192000100912001','getDocsParentCount',OBJECT
go
EXEC sp_rename 'imse.GetAdminChilds','GetAdminCh_12192000100912002',OBJECT
go
CREATE PROCEDURE GetAdminChilds
@meta_id int,
@user_id int
AS
select   to_meta_id
from   childs c
join   meta m    
     on   m.meta_id = c.to_meta_id    -- meta.meta_id corresponds to childs.to_meta_id
      and m.activate != 0      -- Only include the documents that are active in the meta table.
      and c.meta_id = @meta_id     -- Only include documents that are children to this particular meta_id
left join  roles_rights rr        -- We may have permission, even though we don't have anything in role-permissions... That is, if we're docowner or superadmin.
     on  c.to_meta_id = rr.meta_id     -- Only include rows with the documents we are interested in
       and rr.permission_id  = 3     -- Only include permissions that gives right to change the document
join  user_roles_crossref urc       -- This table tells us which users have which roles
     on  urc.user_id = @user_id     -- Only include the rows with the user we are interested in.
      and ( rr.role_id = urc.role_id    -- Include rows where the users roles match the roles that have permissions on the documents
       or urc.role_id = 0     -- and also include the rows that tells us this user is a superadmin (That is, return a row with urc.role_id = 0 for each document.)
       )
left join  user_rights ur        -- We may have permission, even though we don't own any documents... That is, if we're roleadmins or superadmin. When we lose ownership-based rights, this may be skipped.
     on  ur.meta_id = c.to_meta_id    -- Only include rows with the documents we are interested in
      and ur.user_id =  urc.user_id     -- Only include rows with the user  we are interested in
      and ur.permission_id = 99     -- Only include rows that mean "Ownership"
go
IF OBJECT_ID('imse.GetAdminChilds') IS NOT NULL
     DROP PROCEDURE imse.GetAdminCh_12192000100912002
ELSE 
     EXEC sp_rename 'imse.GetAdminCh_12192000100912002','GetAdminChilds',OBJECT
go
EXEC sp_rename 'imse.getBrowserDocChilds','getBrowser_12192000100912003',OBJECT
go
CREATE PROCEDURE getBrowserDocChilds @meta_id int, @user_id int AS
-- Lists the childs for a specific browser_doc
SELECT DISTINCT to_meta_id,
   meta_headline
FROM   browser_docs bd
JOIN   meta m   ON bd.to_meta_id = m.meta_id
      AND bd.meta_id = @meta_id
LEFT JOIN  user_rights ur  ON ur.meta_id = m.meta_id
      AND ur.user_id = @user_id
      AND ur.permission_id = 99
LEFT JOIN  roles_rights rr  ON rr.meta_id = m.meta_id
      AND rr.permission_id = 3
JOIN   user_roles_crossref urc ON urc.user_id = @user_id
      AND (
        urc.role_id = 0
       OR (
         urc.role_id = rr.role_id
        AND urc.user_id = ur.user_id
        )
       OR m.shared = 1
       )
WHERE m.activate = 1
ORDER BY to_meta_id
go
IF OBJECT_ID('imse.getBrowserDocChilds') IS NOT NULL
     DROP PROCEDURE imse.getBrowser_12192000100912003
ELSE 
     EXEC sp_rename 'imse.getBrowser_12192000100912003','getBrowserDocChilds',OBJECT
go
EXEC sp_rename 'imse.GetChilds','GetChilds_12192000100912004',OBJECT
go
CREATE PROCEDURE GetChilds
 @meta_id int,
 @user_id int
AS
/*
Nice little query that lists the children of a document that a particular user may see, and includes a field that tells you wether he may do something to it or not.
*/
declare @sort_by int
select @sort_by = sort_order from text_docs where meta_id = @meta_id
-- Manual sort order
if @sort_by = 2
begin
select  to_meta_id, c.menu_sort,manual_sort_order, doc_type,
  archive,target, left(convert (varchar,date_created,120),10),
  meta_headline,meta_text,meta_image,frame_name,
  activated_date+activated_time,archived_date+archived_time,
  min(            -- This field will have 0 in it, if the user may change the document
   coalesce(urc.role_id,rr.permission_id-3,ur.permission_id-99)*
   coalesce(ur.permission_id-99,rr.permission_id-3,urc.role_id)*
   coalesce(rr.permission_id-3,urc.role_id,ur.permission_id-99)
  )
from   childs c
join   meta m    
     on   m.meta_id = c.to_meta_id    -- meta.meta_id corresponds to childs.to_meta_id
      and m.activate != 0      -- Only include the documents that are active in the meta table.
      and c.meta_id = @meta_id     -- Only include documents that are children to this particular meta_id
left join  roles_rights rr         -- We may have permission, even though we don't have anything in role-permissions... That is, if we're docowner or superadmin.
     on  c.to_meta_id = rr.meta_id     -- Only include rows with the documents we are interested in
       --and (rr.permission_id & 1) != 0    -- Only include permissions where the first bit is set. "Read rights" (May skip this now, since right now, if we have anything, this matches.)
join  user_roles_crossref urc        -- This table tells us which users have which roles
     on  urc.user_id = @user_id     -- Only include the rows with the user we are interested in...
      and ( 
        rr.role_id = urc.role_id    -- Include rows where the users roles match the roles that have permissions on the documents
       or urc.role_id = 0     -- and also include the rows that tells us this user is a superadmin
       or ( 
         m.show_meta != 0   -- and also include documents that are to be shown regardless of rights. (Visa även för obehöriga)
        and rr.permission_id != 3   -- Don't include show_meta if it is not needed.
        ) 
       )
left join  user_rights ur         -- We may have permission, even though we don't own any documents... That is, if we're roleadmins or superadmin. When we lose ownership-based rights, this may be skipped.
     on  ur.meta_id = c.to_meta_id    -- Only include rows with the documents we are interested in
      and ur.user_id =  urc.user_id     -- Only include rows with the user  we are interested in
      and ur.permission_id = 99     -- Only include rows that mean "Ownership"
group by to_meta_id, c.menu_sort,manual_sort_order, doc_type,
  archive,target, left(convert (varchar,date_created,120),10),
  meta_headline,meta_text,meta_image,frame_name,
  activated_date+activated_time,archived_date+archived_time
order by  menu_sort,c.manual_sort_order desc
end
else if @sort_by = 3
begin
select  to_meta_id, c.menu_sort,manual_sort_order, doc_type,
  archive,target, left(convert (varchar,date_created,120),10),
  meta_headline,meta_text,meta_image,frame_name,
  activated_date+activated_time,archived_date+archived_time,
  min(
   coalesce(urc.role_id,rr.permission_id-3,ur.permission_id-99)*
   coalesce(ur.permission_id-99,rr.permission_id-3,urc.role_id)*
   coalesce(rr.permission_id-3,urc.role_id,ur.permission_id-99)
  )
from   childs c
join   meta m    
     on   m.meta_id = c.to_meta_id    -- meta.meta_id corresponds to childs.to_meta_id
      and m.activate != 0      -- Only include the documents that are active in the meta table.
      and c.meta_id = @meta_id     -- Only include documents that are children to this particular meta_id
left join  roles_rights rr         -- We may have permission, even though we don't have anything in role-permissions... That is, if we're docowner or superadmin.
     on  c.to_meta_id = rr.meta_id     -- Only include rows with the documents we are interested in
       --and (rr.permission_id & 1) != 0    -- Only include permissions where the first bit is set. "Read rights" (May skip this now, since right now, if we have anything, this matches.)
join  user_roles_crossref urc        -- This table tells us which users have which roles
     on  urc.user_id = @user_id     -- Only include the rows with the user we are interested in...
      and ( 
        rr.role_id = urc.role_id    -- Include rows where the users roles match the roles that have permissions on the documents
       or urc.role_id = 0     -- and also include the rows that tells us this user is a superadmin
       or ( 
         m.show_meta != 0   -- and also include documents that are to be shown regardless of rights. (Visa även för obehöriga)
        and rr.permission_id != 3   -- Don't include show_meta if it is not needed.
        ) 
       )
left join  user_rights ur         -- We may have permission, even though we don't own any documents... That is, if we're roleadmins or superadmin. When we lose ownership-based rights, this may be skipped.
     on  ur.meta_id = c.to_meta_id    -- Only include rows with the documents we are interested in
      and ur.user_id =  urc.user_id     -- Only include rows with the user  we are interested in
      and ur.permission_id = 99     -- Only include rows that mean "Ownership"
group by to_meta_id, c.menu_sort,manual_sort_order, doc_type,
  archive,target, left(convert (varchar,date_created,120),10),
  meta_headline,meta_text,meta_image,frame_name,
  activated_date+activated_time,archived_date+archived_time
order by  menu_sort,left(convert (varchar,date_created,120),10) desc
end
else if @sort_by = 1
begin
select  to_meta_id, c.menu_sort,manual_sort_order, doc_type,
  archive,target, left(convert (varchar,date_created,120),10),
  meta_headline,meta_text,meta_image,frame_name,
  activated_date+activated_time,archived_date+archived_time,
  min(
   coalesce(urc.role_id,rr.permission_id-3,ur.permission_id-99)*
   coalesce(ur.permission_id-99,rr.permission_id-3,urc.role_id)*
   coalesce(rr.permission_id-3,urc.role_id,ur.permission_id-99)
  )
from   childs c
join   meta m    
     on   m.meta_id = c.to_meta_id    -- meta.meta_id corresponds to childs.to_meta_id
      and m.activate != 0      -- Only include the documents that are active in the meta table.
      and c.meta_id = @meta_id     -- Only include documents that are children to this particular meta_id
left join  roles_rights rr         -- We may have permission, even though we don't have anything in role-permissions... That is, if we're docowner or superadmin.
     on  c.to_meta_id = rr.meta_id     -- Only include rows with the documents we are interested in
       --and (rr.permission_id & 1) != 0    -- Only include permissions where the first bit is set. "Read rights" (May skip this now, since right now, if we have anything, this matches.)
join  user_roles_crossref urc        -- This table tells us which users have which roles
     on  urc.user_id = @user_id     -- Only include the rows with the user we are interested in...
      and ( 
        rr.role_id = urc.role_id    -- Include rows where the users roles match the roles that have permissions on the documents
       or urc.role_id = 0     -- and also include the rows that tells us this user is a superadmin
       or ( 
         m.show_meta != 0   -- and also include documents that are to be shown regardless of rights. (Visa även för obehöriga)
        and rr.permission_id != 3   -- Don't include show_meta if it is not needed.
        ) 
       )
left join  user_rights ur         -- We may have permission, even though we don't own any documents... That is, if we're roleadmins or superadmin. When we lose ownership-based rights, this may be skipped.
     on  ur.meta_id = c.to_meta_id    -- Only include rows with the documents we are interested in
      and ur.user_id =  urc.user_id     -- Only include rows with the user  we are interested in
      and ur.permission_id = 99     -- Only include rows that mean "Ownership"
group by to_meta_id, c.menu_sort,manual_sort_order, doc_type,
  archive,target, left(convert (varchar,date_created,120),10),
  meta_headline,meta_text,meta_image,frame_name,
  activated_date+activated_time,archived_date+archived_time
order by  menu_sort,meta_headline
end
go
IF OBJECT_ID('imse.GetChilds') IS NOT NULL
     DROP PROCEDURE imse.GetChilds_12192000100912004
ELSE 
     EXEC sp_rename 'imse.GetChilds_12192000100912004','GetChilds',OBJECT
go
EXEC sp_rename 'imse.getDocs','getDocs_12192000100912005',OBJECT
go
CREATE PROCEDURE getDocs @user_id int, @start int, @end int AS
-- Lists documents user is allowed to see.
SELECT DISTINCT m.meta_id,
   COUNT(DISTINCT c.meta_id) parentcount,
   meta_headline,
   doc_type
FROM   meta m
LEFT JOIN  childs c   ON c.to_meta_id = m.meta_id
LEFT JOIN  user_rights ur  ON ur.meta_id = m.meta_id
      AND ur.permission_id = 99
LEFT JOIN  roles_rights rr  ON rr.meta_id = m.meta_id
      AND rr.permission_id = 3
JOIN   user_roles_crossref urc ON urc.user_id = @user_id
      AND (
        urc.role_id = 0
       OR (
         urc.role_id = rr.role_id
        AND urc.user_id = ur.user_id
        )
       OR m.shared = 1
       )
WHERE  m.activate = 1
  AND m.meta_id > (@start-1) 
  AND m.meta_id < (@end+1)
GROUP BY  m.meta_id,m.meta_headline,m.doc_type,c.to_meta_id
ORDER BY  m.meta_id
go
IF OBJECT_ID('imse.getDocs') IS NOT NULL
     DROP PROCEDURE imse.getDocs_12192000100912005
ELSE 
     EXEC sp_rename 'imse.getDocs_12192000100912005','getDocs',OBJECT
go
EXEC sp_rename 'imse.getMenuDocChilds','getMenuDoc_12192000100912006',OBJECT
go
CREATE PROCEDURE getMenuDocChilds @meta_id int, @user_id int AS
-- Lists the childs of menudoc @meta_id
SELECT DISTINCT to_meta_id,
   meta_headline
FROM   childs c
JOIN   meta m   ON c.to_meta_id = m.meta_id
      AND c.meta_id = @meta_id
LEFT JOIN  user_rights ur  ON ur.meta_id = m.meta_id
      AND ur.user_id = @user_id
      AND ur.permission_id = 99
LEFT JOIN  roles_rights rr  ON rr.meta_id = m.meta_id
      AND rr.permission_id = 3
JOIN   user_roles_crossref urc ON urc.user_id = @user_id
      AND ( urc.role_id = 0
       OR (
         urc.role_id = rr.role_id
        AND urc.user_id = ur.user_id
        )
       OR m.shared = 1
       )
WHERE m.activate = 1
ORDER BY to_meta_id
go
IF OBJECT_ID('imse.getMenuDocChilds') IS NOT NULL
     DROP PROCEDURE imse.getMenuDoc_12192000100912006
ELSE 
     EXEC sp_rename 'imse.getMenuDoc_12192000100912006','getMenuDocChilds',OBJECT
go
EXEC sp_rename 'imse.getUserWriteRights','getUserWri_12192000100912007',OBJECT
go
CREATE PROCEDURE getUserWriteRights AS
DECLARE @user int
DECLARE @doc int
select user_id from user_roles_crossref where user_id = @user and role_id = 0 -- Returnerar en rad om användare 1 är superadmin
select user_id from user_rights where meta_id = @doc and user_id = @user and permission_id = 99 -- Returnerar en rad om användare 1 skapade dokument 1351
select user_id from roles_rights join user_roles_crossref on roles_rights.role_id = user_roles_crossref.role_id where meta_id = @doc and user_id = @doc and permission_id = 3 -- Returnerar en rad om användare 1 är medlem i en grupp som har skrivrättigheter i dokument 1351
go
IF OBJECT_ID('imse.getUserWriteRights') IS NOT NULL
     DROP PROCEDURE imse.getUserWri_12192000100912007
ELSE 
     EXEC sp_rename 'imse.getUserWri_12192000100912007','getUserWriteRights',OBJECT
go
EXEC sp_rename 'imse.IMC_AddRole','IMC_AddRol_12192000100912008',OBJECT
go
CREATE PROCEDURE IMC_AddRole 
/*
 Lets detect if we should add a read / or a write option
*/
 @metaId int,
 @aRole int,
 @typeOfRole int
AS
IF( @typeOfRole = 1) BEGIN
 -- Lets insert a read
 INSERT INTO roles_rights
 VALUES( @aRole ,  @metaId ,@typeOfRole )
END 
IF( @typeOfRole = 3 ) BEGIN
 -- WRITE
 INSERT INTO roles_rights
 VALUES ( @aRole , @metaId , @typeOfRole)
END
go
IF OBJECT_ID('imse.IMC_AddRole') IS NOT NULL
     DROP PROCEDURE imse.IMC_AddRol_12192000100912008
ELSE 
     EXEC sp_rename 'imse.IMC_AddRol_12192000100912008','IMC_AddRole',OBJECT
go
EXEC sp_rename 'imse.meta_select','meta_selec_12192000100912009',OBJECT
go
CREATE PROCEDURE meta_select AS
select menu_sort,manual_sort_order,date_created,meta_headline,target from meta,childs,roles_rights,user_roles_crossref 
where meta.meta_id = childs.to_meta_id 
and childs.meta_id = 4260 
and meta.archive=0 
and meta.activate=1 
and  roles_rights.meta_id = childs.to_meta_id 
and roles_rights.permission_id > 0 
and roles_rights.role_id = user_roles_crossref.role_id 
and user_roles_crossref.user_id =99
and meta.activated_date + ' ' + meta.activated_time <= '2000-05-02 02:37' 
and (meta.archived_date + ' ' + meta.archived_time >= '2000-05-02 02:37' or meta.archived_date + ' ' + meta.archived_time = '') 
union 
 select menu_sort,manual_sort_order,date_created,meta_headline,target from meta,childs,user_rights 
where meta.meta_id = childs.to_meta_id 
and childs.meta_id =  4260
and meta.archive=0 
and meta.activate=1 
and user_rights.meta_id = childs.to_meta_id 
and user_rights.permission_id > 0 
and user_rights.user_id = 99
and meta.activated_date + ' ' + meta.activated_time <= '2000-05-02 02:37' 
and (meta.archived_date + ' ' + meta.archived_time >= '2000-05-02 02:37' 
or meta.archived_date + ' ' + meta.archived_time = '') 
order by menu_sort,meta.meta_headline
go
IF OBJECT_ID('imse.meta_select') IS NOT NULL
     DROP PROCEDURE imse.meta_selec_12192000100912009
ELSE 
     EXEC sp_rename 'imse.meta_selec_12192000100912009','meta_select',OBJECT
go
EXEC sp_rename 'imse.RoleCount','RoleCount_12192000100912010',OBJECT
go
CREATE PROCEDURE RoleCount
 @aRoleId int
AS
/*
 This function is used when an administrator tries to delete a role.
 This function counts in how many documents the role is used
*/
DECLARE @returnVal int
SELECT  @returnVal = COUNT(  r.role_id ) 
FROM ROLES_RIGHTS r
WHERE ROLE_ID = @aRoleId
-- Lets validate for null
SELECT @returnVal = ISNULL(  @returnVal , 0 )
SELECT @returnVal AS 'Number_of_roles'
go
IF OBJECT_ID('imse.RoleCount') IS NOT NULL
     DROP PROCEDURE imse.RoleCount_12192000100912010
ELSE 
     EXEC sp_rename 'imse.RoleCount_12192000100912010','RoleCount',OBJECT
go
EXEC sp_rename 'imse.RoleDelete','RoleDelete_12192000100912011',OBJECT
go
CREATE PROCEDURE RoleDelete
/* Deletes an role from the role table. Used by the AdminRoles servlet
*/
 @roleId int
AS
DELETE FROM ROLES_RIGHTS WHERE ROLE_ID = @roleId
DELETE FROM user_roles_crossref WHERE ROLE_ID =@roleId
DELETE FROM ROLES WHERE ROLE_ID = @roleId
go
IF OBJECT_ID('imse.RoleDelete') IS NOT NULL
     DROP PROCEDURE imse.RoleDelete_12192000100912011
ELSE 
     EXEC sp_rename 'imse.RoleDelete_12192000100912011','RoleDelete',OBJECT
go
EXEC sp_rename 'imse.RoleDeleteViewAffectedMetaIds','RoleDelete_12192000100912012',OBJECT
go
CREATE PROCEDURE RoleDeleteViewAffectedMetaIds
 @aRoleId int
AS
/*
 This function is used when an administrator tries to delete a role.
 All metaids where the role is used will be presenteted in i list
*/
SELECT  TOP 50 r.meta_id , r.meta_id
FROM ROLES_RIGHTS r
WHERE ROLE_ID = @aRoleId
-- Lets validate for null
--SELECT @returnVal = ISNULL(  @returnVal , -1 )
--SELECT @returnVal AS 'FoundRoleName'
go
IF OBJECT_ID('imse.RoleDeleteViewAffectedMetaIds') IS NOT NULL
     DROP PROCEDURE imse.RoleDelete_12192000100912012
ELSE 
     EXEC sp_rename 'imse.RoleDelete_12192000100912012','RoleDeleteViewAffectedMetaIds',OBJECT
go

--
-- Table Create
-- dbo.stats
--
CREATE TABLE dbo.stats 
(
    name varchar(120) NOT NULL,
    num  int          NOT NULL,
    CONSTRAINT stats_pk
    PRIMARY KEY CLUSTERED (name)
)
go

--
-- Table Extended Alter
-- dbo.sys_data
--
EXEC sp_rename 'dbo.sys_data','sys_data_12192000100913000',OBJECT
go
CREATE TABLE dbo.sys_data 
(
    sys_id  tinyint     IDENTITY,
    type_id tinyint     NOT NULL,
    value   varchar(80) NULL
)
go
SET IDENTITY_INSERT dbo.sys_data ON
go
INSERT INTO dbo.sys_data(
                         sys_id,
                         type_id,
                         value
                        )
                  SELECT 
                         sys_id,
                         type_id,
                         CONVERT(varchar(80),value)
                    FROM dbo.sys_data_12192000100913000
go
SET IDENTITY_INSERT dbo.sys_data OFF
go
ALTER TABLE dbo.sys_data ADD CONSTRAINT PK_sys_data
PRIMARY KEY NONCLUSTERED (sys_id,type_id) 
go
EXEC sp_rename 'imse.GetCurrentSessionCounter','GetCurrent_12192000100913001',OBJECT
go
CREATE PROCEDURE GetCurrentSessionCounter 
 
AS
 SELECT value 
 FROM sys_data
 WHERE type_id  = 1
go
IF OBJECT_ID('imse.GetCurrentSessionCounter') IS NOT NULL
     DROP PROCEDURE imse.GetCurrent_12192000100913001
ELSE 
     EXEC sp_rename 'imse.GetCurrent_12192000100913001','GetCurrentSessionCounter',OBJECT
go
EXEC sp_rename 'imse.GetCurrentSessionCounterDate','GetCurrent_12192000100913002',OBJECT
go
CREATE PROCEDURE GetCurrentSessionCounterDate
 
AS
 SELECT value 
 FROM sys_data
 WHERE type_id  = 2
go
IF OBJECT_ID('imse.GetCurrentSessionCounterDate') IS NOT NULL
     DROP PROCEDURE imse.GetCurrent_12192000100913002
ELSE 
     EXEC sp_rename 'imse.GetCurrent_12192000100913002','GetCurrentSessionCounterDate',OBJECT
go
EXEC sp_rename 'imse.IncSessionCounter','IncSession_12192000100913003',OBJECT
go
CREATE PROCEDURE IncSessionCounter 
AS
      
    DECLARE @current_value int
  select @current_value = (select value from sys_data where type_id = 1)
  set @current_value  =  @current_value +1
 update sys_data
 set value = @current_value where type_id = 1
 
  return
go
IF OBJECT_ID('imse.IncSessionCounter') IS NOT NULL
     DROP PROCEDURE imse.IncSession_12192000100913003
ELSE 
     EXEC sp_rename 'imse.IncSession_12192000100913003','IncSessionCounter',OBJECT
go
EXEC sp_rename 'imse.SetSessionCounterDate','SetSession_12192000100913004',OBJECT
go
CREATE PROCEDURE SetSessionCounterDate
   @new_date varchar(20)
AS
      
 update sys_data
 set value = @new_date where type_id = 2
 
  return
go
IF OBJECT_ID('imse.SetSessionCounterDate') IS NOT NULL
     DROP PROCEDURE imse.SetSession_12192000100913004
ELSE 
     EXEC sp_rename 'imse.SetSession_12192000100913004','SetSessionCounterDate',OBJECT
go
EXEC sp_rename 'imse.SystemMessageGet','SystemMess_12192000100913005',OBJECT
go
CREATE PROCEDURE SystemMessageGet AS
/*
 Used by the AdminSystemMessage servlet to retrieve the systemmessage
*/
SELECT s.value
FROM sys_data s
WHERE s.type_id = 3
go
IF OBJECT_ID('imse.SystemMessageGet') IS NOT NULL
     DROP PROCEDURE imse.SystemMess_12192000100913005
ELSE 
     EXEC sp_rename 'imse.SystemMess_12192000100913005','SystemMessageGet',OBJECT
go
EXEC sp_rename 'imse.SystemMessageSet','SystemMess_12192000100913006',OBJECT
go
CREATE PROCEDURE SystemMessageSet
/*
Lets update the system message table. Used by the AdminSystemMessage servlet
*/
 @newMsg varchar(80)
AS
UPDATE sys_data
SET value = @newMsg
WHERE type_id = 3
go
IF OBJECT_ID('imse.SystemMessageSet') IS NOT NULL
     DROP PROCEDURE imse.SystemMess_12192000100913006
ELSE 
     EXEC sp_rename 'imse.SystemMess_12192000100913006','SystemMessageSet',OBJECT
go

--
-- Table Extended Alter
-- dbo.sys_types
--
EXEC sp_rename 'dbo.sys_types','sys_types_12192000100914000',OBJECT
go
CREATE TABLE dbo.sys_types 
(
    type_id tinyint     IDENTITY,
    name    varchar(50) NULL
)
go
SET IDENTITY_INSERT dbo.sys_types ON
go
INSERT INTO dbo.sys_types(
                          type_id,
                          name
                         )
                   SELECT 
                          type_id,
                          CONVERT(varchar(50),name)
                     FROM dbo.sys_types_12192000100914000
go
SET IDENTITY_INSERT dbo.sys_types OFF
go
ALTER TABLE dbo.sys_types ADD CONSTRAINT PK_sys_types
PRIMARY KEY NONCLUSTERED (type_id) 
go

--
-- Table Alter
-- dbo.templates_cref
--
ALTER TABLE dbo.templates_cref DROP CONSTRAINT FK_templates_cref_templategroups
go
ALTER TABLE dbo.templates_cref ADD CONSTRAINT FK_templates_cref_templates
FOREIGN KEY (template_id)
REFERENCES dbo.templates (template_id)
go
ALTER TABLE dbo.templates_cref DROP CONSTRAINT FK_templates_cref_templates
go

--
-- Table Alter
-- dbo.text_docs
--
ALTER TABLE dbo.text_docs DROP CONSTRAINT DF__text_docs__group__72910220
go
ALTER TABLE dbo.text_docs ADD DEFAULT 1 FOR group_id
go

--
-- Table Alter
-- dbo.texts
--
ALTER TABLE dbo.texts ADD CONSTRAINT FK_texts_meta
FOREIGN KEY (meta_id)
REFERENCES dbo.meta (meta_id)
go

--
-- Table Extended Alter
-- dbo.url_docs
--
EXEC sp_rename 'url_docs.PK_url_docs','PK_url_doc_12192000100917001'
go
EXEC sp_rename 'dbo.url_docs','url_docs_12192000100917000',OBJECT
go
CREATE TABLE dbo.url_docs 
(
    meta_id     int          NOT NULL,
    frame_name  varchar(80)  NOT NULL,
    target      varchar(15)  NOT NULL,
    url_ref     varchar(255) NOT NULL,
    url_txt     varchar(255) NOT NULL,
    lang_prefix varchar(3)   NOT NULL
)
go
INSERT INTO dbo.url_docs(
                         meta_id,
                         frame_name,
                         target,
                         url_ref,
                         url_txt,
                         lang_prefix
                        )
                  SELECT 
                         meta_id,
                         frame_name,
                         target,
                         url_ref,
                         url_txt,
                         lang_prefix
                    FROM dbo.url_docs_12192000100917000
go
ALTER TABLE dbo.url_docs ADD CONSTRAINT PK_url_docs
PRIMARY KEY NONCLUSTERED (meta_id,lang_prefix) 
go

--
-- Table Alter
-- dbo.user_rights
--
ALTER TABLE dbo.user_rights ADD CONSTRAINT PK_user_rights
PRIMARY KEY NONCLUSTERED (user_id,meta_id,permission_id) 
go

--
-- Table Alter
-- dbo.user_roles_crossref
--
ALTER TABLE dbo.user_roles_crossref ADD CONSTRAINT FK_user_roles_crossref_roles
FOREIGN KEY (role_id)
REFERENCES dbo.roles (role_id)
go

--
-- Table Extended Alter
-- dbo.users
--
EXEC sp_rename 'users.PK_users','PK_users_12192000100921001'
go
EXEC sp_rename 'dbo.users','users_12192000100921000',OBJECT
go
CREATE TABLE dbo.users 
(
    user_id        int           NOT NULL,
    login_name     char(15)      NOT NULL,
    login_password char(15)      NOT NULL,
    first_name     char(25)      NOT NULL,
    last_name      char(30)      NOT NULL,
    title          char(30)      DEFAULT '' NOT NULL,
    company        char(30)      DEFAULT '' NOT NULL,
    address        char(40)      NOT NULL,
    city           char(30)      NOT NULL,
    zip            char(15)      NOT NULL,
    country        char(30)      NOT NULL,
    county_council char(30)      NOT NULL,
    email          char(50)      NOT NULL,
    admin_mode     int           NOT NULL,
    last_page      int           NOT NULL,
    archive_mode   int           NOT NULL,
    lang_id        int           NOT NULL,
    user_type      int           DEFAULT 1 NOT NULL,
    active         int           DEFAULT 1 NOT NULL,
    create_date    smalldatetime NOT NULL
)
go
INSERT INTO dbo.users(
                      user_id,
                      login_name,
                      login_password,
                      first_name,
                      last_name,
                      title,
                      company,
                      address,
                      city,
                      zip,
                      country,
                      county_council,
                      email,
                      admin_mode,
                      last_page,
                      archive_mode,
                      lang_id,
                      user_type,
                      active,
                      create_date
                     )
               SELECT 
                      user_id,
                      login_name,
                      login_password,
                      first_name,
                      last_name,
                      title,
                      company,
                      address,
                      city,
                      zip,
                      country,
                      county_council,
                      email,
                      admin_mode,
                      last_page,
                      archive_mode,
                      lang_id,
                      user_type,
                      active,
                      create_date
                 FROM dbo.users_12192000100921000
go
ALTER TABLE dbo.users ADD CONSTRAINT PK_users
PRIMARY KEY NONCLUSTERED (user_id) 
go
EXEC sp_rename 'imse.AddNewuser','AddNewuser_12192000100921002',OBJECT
go
CREATE PROCEDURE AddNewuser
/*
Adds a new user to the user table
usertype. 0=special, 1=default, 2=conferenceuser 
*/
 @user_id int,
 @login_name char(15),
 @login_password char(15),
 @first_name char(25),
 @last_name char(30),
 @title char(30),
 @company char(30),
 @address char(40),
 @city char(30),
 @zip char (15),
 @country char(30),
 @county_council char(30),
 @email char(50),
 @admin_mode int,
 @last_page int,
 @archive_mode int,
 @lang_id int,
 @user_type int,
 @active int
AS
INSERT INTO users (user_id,login_name,login_password,first_name,last_name, title, company, address,city,zip,country,county_council,email,admin_mode,last_page,archive_mode,lang_id, user_type, active, create_date)
VALUES (@user_id, @login_name, @login_password, @first_name, @last_name, @title, @company,  @address, @city, @zip, @country,
   @county_council, @email, @admin_mode, @last_page, @archive_mode, @lang_id ,@user_type, @active, getDate())
/*
EATE PROCEDURE AddNewuser
Adds a new user to the user table
usertype. 0=special, 1=default, 2=conferenceuser
 @user_id int,
 @login_name char(15),
 @login_password char(15),
 @first_name char(25),
 @last_name char(30),
 @address char(40),
 @city char(30),
 @zip char (15),
 @country char(30),
 @county_council char(30),
 @email char(50),
 @admin_mode int,
 @last_page int,
 @archive_mode int,
 @lang_id int,
 @user_type int,
 @active int
AS
INSERT INTO users (user_id,login_name,login_password,first_name,last_name,address,city,zip,country,county_council,email,admin_mode,last_page,archive_mode,lang_id, user_type, active, create_date)
VALUES (@user_id, @login_name, @login_password, @first_name, @last_name, @address, @city, @zip, @country,
   @county_council, @email, @admin_mode, @last_page, @archive_mode, @lang_id ,@user_type, @active, getDate())
*/
/*
CREATE PROCEDURE AddNewuser
Adds a new user to the user table
usertype. 0=special, 1=default, 2=conferenceuser 
 @user_id int,
 @login_name char(15),
 @login_password char(15),
 @first_name char(25),
 @last_name char(30),
 @address char(40),
 @city char(30),
 @zip char (15),
 @country char(30),
 @county_council char(30),
 @email char(50),
 @admin_mode int,
 @last_page int,
 @archive_mode int,
 @lang_id int,
 @user_type int,
 @active int
AS
INSERT INTO users (user_id,login_name,login_password,first_name,last_name,address,city,zip,country,county_council,email,admin_mode,last_page,archive_mode,lang_id, user_type, active, create_date)
VALUES (@user_id, @login_name, @login_password, @first_name, @last_name, @address, @city, @zip, @country,
   @county_council, @email, @admin_mode, @last_page, @archive_mode, @lang_id ,@user_type, @active, getDate())
*/
go
IF OBJECT_ID('imse.AddNewuser') IS NOT NULL
     DROP PROCEDURE imse.AddNewuser_12192000100921002
ELSE 
     EXEC sp_rename 'imse.AddNewuser_12192000100921002','AddNewuser',OBJECT
go
EXEC sp_rename 'imse.ChangeUserActiveStatus','ChangeUser_12192000100921003',OBJECT
go
CREATE PROCEDURE ChangeUserActiveStatus @user_id int, @active int AS
/* 
 * change users activestate
*/
UPDATE users 
SET 
active = @active
WHERE user_id = @user_id
go
IF OBJECT_ID('imse.ChangeUserActiveStatus') IS NOT NULL
     DROP PROCEDURE imse.ChangeUser_12192000100921003
ELSE 
     EXEC sp_rename 'imse.ChangeUser_12192000100921003','ChangeUserActiveStatus',OBJECT
go
EXEC sp_rename 'imse.CheckAdminRights','CheckAdmin_12192000100921004',OBJECT
go
CREATE PROCEDURE CheckAdminRights
/*
Detects if a user is administrator or not
*/
 @aUserId int
AS
SELECT users.user_id, roles.role_id
FROM users INNER JOIN
    user_roles_crossref ON 
    users.user_id = user_roles_crossref.user_id INNER JOIN
    roles ON user_roles_crossref.role_id = roles.role_id
WHERE roles.role_id = 0 AND users.user_id = @aUserId
go
IF OBJECT_ID('imse.CheckAdminRights') IS NOT NULL
     DROP PROCEDURE imse.CheckAdmin_12192000100921004
ELSE 
     EXEC sp_rename 'imse.CheckAdmin_12192000100921004','CheckAdminRights',OBJECT
go
EXEC sp_rename 'imse.DelUser','DelUser_12192000100921005',OBJECT
go
CREATE PROCEDURE DelUser
 @aUserId int
AS
 
 DELETE
 FROM user_roles_crossref
 WHERE user_id = @aUserId
 DELETE 
 FROM users
 WHERE user_id = @aUserId
go
IF OBJECT_ID('imse.DelUser') IS NOT NULL
     DROP PROCEDURE imse.DelUser_12192000100921005
ELSE 
     EXEC sp_rename 'imse.DelUser_12192000100921005','DelUser',OBJECT
go
EXEC sp_rename 'imse.FindUserName','FindUserNa_12192000100921006',OBJECT
go
CREATE PROCEDURE [FindUserName] 
 @userName char(15)
AS
/*
 This function is used from the conference when  someone is logging in to the 
conference. The system searches for the username and returns the 
userId, userName and password
*/
SELECT  u.login_name
FROM users u
WHERE u.login_name = @userName
go
IF OBJECT_ID('imse.FindUserName') IS NOT NULL
     DROP PROCEDURE imse.FindUserNa_12192000100921006
ELSE 
     EXEC sp_rename 'imse.FindUserNa_12192000100921006','FindUserName',OBJECT
go
EXEC sp_rename 'imse.GetAllUsers','GetAllUser_12192000100921007',OBJECT
go
CREATE PROCEDURE [GetAllUsers] AS
  select *
 from USERS
 
 order by  last_name
go
IF OBJECT_ID('imse.GetAllUsers') IS NOT NULL
     DROP PROCEDURE imse.GetAllUser_12192000100921007
ELSE 
     EXEC sp_rename 'imse.GetAllUser_12192000100921007','GetAllUsers',OBJECT
go
EXEC sp_rename 'imse.GetAllUsersInList','GetAllUser_12192000100921008',OBJECT
go
CREATE PROCEDURE GetAllUsersInList AS
/*
This function is used from AdminIpAcces servlet to generate a list
*/
SELECT user_id, last_name + ', ' + first_name from users
ORDER BY last_name
go
IF OBJECT_ID('imse.GetAllUsersInList') IS NOT NULL
     DROP PROCEDURE imse.GetAllUser_12192000100921008
ELSE 
     EXEC sp_rename 'imse.GetAllUser_12192000100921008','GetAllUsersInList',OBJECT
go
EXEC sp_rename 'imse.GetCategoryUsers','GetCategor_12192000100921009',OBJECT
go
CREATE PROCEDURE GetCategoryUsers
/*
Used from servlet AdminUser
*/
 @category int
AS
SELECT user_id, last_name + ', ' + first_name
FROM users
WHERE user_type = @category
ORDER BY last_name
go
IF OBJECT_ID('imse.GetCategoryUsers') IS NOT NULL
     DROP PROCEDURE imse.GetCategor_12192000100921009
ELSE 
     EXEC sp_rename 'imse.GetCategor_12192000100921009','GetCategoryUsers',OBJECT
go
EXEC sp_rename 'imse.GetHighestUserId','GetHighest_12192000100921010',OBJECT
go
CREATE PROCEDURE GetHighestUserId
AS
--DECLARE @retVal int
SELECT MAX(user_id) +1
FROM users
go
IF OBJECT_ID('imse.GetHighestUserId') IS NOT NULL
     DROP PROCEDURE imse.GetHighest_12192000100921010
ELSE 
     EXEC sp_rename 'imse.GetHighest_12192000100921010','GetHighestUserId',OBJECT
go
EXEC sp_rename 'imse.GetNewUserId','GetNewUser_12192000100921011',OBJECT
go
CREATE PROCEDURE GetNewUserId
AS
--DECLARE @retVal int
SELECT MAX(user_id) +1
FROM users
go
IF OBJECT_ID('imse.GetNewUserId') IS NOT NULL
     DROP PROCEDURE imse.GetNewUser_12192000100921011
ELSE 
     EXEC sp_rename 'imse.GetNewUser_12192000100921011','GetNewUserId',OBJECT
go
EXEC sp_rename 'imse.GetUserCreateDate','GetUserCre_12192000100921012',OBJECT
go
CREATE PROCEDURE GetUserCreateDate
/*
 Returns the date when the user was created in the system
Used by servlet AdminUserProps
*/
 @userId int
AS
DECLARE @retVal smalldatetime
SELECT @retVal = create_date
FROM users
WHERE users.user_id = @userId
-- Lets validate for null
-- SELECT @retVal = ISNULL(  @retVal , '' )
---SELECT @retVal AS 'TemplateId'
SELECT @retVal  AS 'Usercreatedate'
go
IF OBJECT_ID('imse.GetUserCreateDate') IS NOT NULL
     DROP PROCEDURE imse.GetUserCre_12192000100921012
ELSE 
     EXEC sp_rename 'imse.GetUserCre_12192000100921012','GetUserCreateDate',OBJECT
go
EXEC sp_rename 'imse.GetUserId','GetUserId_12192000100921013',OBJECT
go
CREATE PROCEDURE GetUserId 
 @aUserId int
AS
 SELECT user_id 
 FROM users
 WHERE user_id  = @aUserId
go
IF OBJECT_ID('imse.GetUserId') IS NOT NULL
     DROP PROCEDURE imse.GetUserId_12192000100921013
ELSE 
     EXEC sp_rename 'imse.GetUserId_12192000100921013','GetUserId',OBJECT
go
EXEC sp_rename 'imse.GetUserIdFromName','GetUserIdF_12192000100921014',OBJECT
go
CREATE PROCEDURE [GetUserIdFromName] 
/*
Used by the conferences loginfunction, to detect a users userid from
the username
*/
 @userName char(15),
 @userPwd char(15)
AS
SELECT  u.user_id 
FROM users u
WHERE u.login_name = @userName
AND u.login_password = @userPwd
go
IF OBJECT_ID('imse.GetUserIdFromName') IS NOT NULL
     DROP PROCEDURE imse.GetUserIdF_12192000100921014
ELSE 
     EXEC sp_rename 'imse.GetUserIdF_12192000100921014','GetUserIdFromName',OBJECT
go
EXEC sp_rename 'imse.GetUserInfo','GetUserInf_12192000100921015',OBJECT
go
CREATE PROCEDURE GetUserInfo
/* Returns all the information about a user. Used by adminsystem & conference system
*/
 @aUserId int
AS
 SELECT * 
 FROM users
 WHERE user_id = @aUserId
go
IF OBJECT_ID('imse.GetUserInfo') IS NOT NULL
     DROP PROCEDURE imse.GetUserInf_12192000100921015
ELSE 
     EXEC sp_rename 'imse.GetUserInf_12192000100921015','GetUserInfo',OBJECT
go
EXEC sp_rename 'imse.GetUserNames','GetUserNam_12192000100921016',OBJECT
go
CREATE PROCEDURE GetUserNames
/* 
This procedure is used to retrieve a users full name (first name + last name
concateneted.
*/
 @user_id int,
 @what int
AS
 DECLARE @returnVal char(25)
IF(@what = 1) BEGIN
 SELECT @returnVal = RTRIM(first_name) 
 FROM users
 WHERE users.user_id = @user_id 
END ELSE BEGIN  
 SELECT @returnVal =  RTRIM(last_name) 
 FROM users
 WHERE users.user_id = @user_id 
END
SELECT @returnVal =  ISNULL(@returnVal, -1) 
SELECT @returnVal AS 'UserName'
go
IF OBJECT_ID('imse.GetUserNames') IS NOT NULL
     DROP PROCEDURE imse.GetUserNam_12192000100921016
ELSE 
     EXEC sp_rename 'imse.GetUserNam_12192000100921016','GetUserNames',OBJECT
go
EXEC sp_rename 'imse.GetUserPassword','GetUserPas_12192000100921017',OBJECT
go
CREATE PROCEDURE GetUserPassword 
/* Used by AdminUserProps servlet to retrieve the users password 
*/
 @user_id int
AS
DECLARE @retVal char(15)
SELECT @retVal  = login_password 
FROM USERS
WHERE user_id = @user_id
SELECT @retVal =  ISNULL(@retVal , '') 
SELECT @retVal AS 'Password'
go
IF OBJECT_ID('imse.GetUserPassword') IS NOT NULL
     DROP PROCEDURE imse.GetUserPas_12192000100921017
ELSE 
     EXEC sp_rename 'imse.GetUserPas_12192000100921017','GetUserPassword',OBJECT
go
EXEC sp_rename 'imse.GetUserPhoneNumbers','GetUserPho_12192000100921018',OBJECT
go
CREATE PROCEDURE GetUserPhoneNumbers
/*
Returns a users phonenumbers. Used by AdminUserProps servlet
*/
 @user_id int
AS
-- The new version which includes phones
SELECT p.phone_id, p.country_code , p.area_code , p.number, p.user_id 
FROM users u , phones p 
WHERE u.user_id = p.user_id
AND u.user_id = @user_id
go
IF OBJECT_ID('imse.GetUserPhoneNumbers') IS NOT NULL
     DROP PROCEDURE imse.GetUserPho_12192000100921018
ELSE 
     EXEC sp_rename 'imse.GetUserPho_12192000100921018','GetUserPhoneNumbers',OBJECT
go
EXEC sp_rename 'imse.GetUserPhones','GetUserPho_12192000100921019',OBJECT
go
CREATE PROCEDURE GetUserPhones
 @user_id int
AS
/*
Used to generate a list with all type of users. Used from AdminUserProps
*/
SELECT p.phone_id, RTRIM(p.country_code) + ' ' + RTRIM(p.area_code) + ' ' + RTRIM(p.number) as numbers
FROM users u , phones p 
WHERE u.user_id = p.user_id
AND u.user_id = @user_id
go
IF OBJECT_ID('imse.GetUserPhones') IS NOT NULL
     DROP PROCEDURE imse.GetUserPho_12192000100921019
ELSE 
     EXEC sp_rename 'imse.GetUserPho_12192000100921019','GetUserPhones',OBJECT
go
EXEC sp_rename 'imse.GetUsersWhoBelongsToRole','GetUsersWh_12192000100921020',OBJECT
go
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
go
IF OBJECT_ID('imse.GetUsersWhoBelongsToRole') IS NOT NULL
     DROP PROCEDURE imse.GetUsersWh_12192000100921020
ELSE 
     EXEC sp_rename 'imse.GetUsersWh_12192000100921020','GetUsersWhoBelongsToRole',OBJECT
go
EXEC sp_rename 'imse.GetUserType','GetUserTyp_12192000100921021',OBJECT
go
CREATE PROCEDURE GetUserType
/*
Used to get a users usertype. used from adminuser
*/
 @User_id int
 AS
DECLARE @returnVal int
SELECT DISTINCT @returnVal =  user_type  
FROM users
WHERE user_id = @User_id
SELECT @returnVal =  ISNULL(@returnVal, 1) 
SELECT @returnVal AS 'Usertype'
go
IF OBJECT_ID('imse.GetUserType') IS NOT NULL
     DROP PROCEDURE imse.GetUserTyp_12192000100921021
ELSE 
     EXEC sp_rename 'imse.GetUserTyp_12192000100921021','GetUserType',OBJECT
go
EXEC sp_rename 'imse.IPAccessesGetAll','IPAccesses_12192000100921022',OBJECT
go
CREATE PROCEDURE IPAccessesGetAll AS
/*
Lets get all IPaccesses from db. Used  by the AdminIpAccesses
*/
SELECT ip.ip_access_id, ip.user_id, usr.login_name, ip.ip_start, ip.ip_end    
FROM IP_ACCESSES ip, USERS usr
WHERE ip.user_id = usr.user_id
go
IF OBJECT_ID('imse.IPAccessesGetAll') IS NOT NULL
     DROP PROCEDURE imse.IPAccesses_12192000100921022
ELSE 
     EXEC sp_rename 'imse.IPAccesses_12192000100921022','IPAccessesGetAll',OBJECT
go
EXEC sp_rename 'imse.PermissionsGetPermission','Permission_12192000100921023',OBJECT
go
CREATE PROCEDURE PermissionsGetPermission @login_name varchar(15), @permission int AS
/*
*/
select login_password, first_name, last_name, email, min(permissions&@permission), lang_prefix 
from users u 
join lang_prefixes lp 
    on u.lang_id = lp.lang_id 
join user_roles_crossref urc 
    on u.user_id = urc.user_id left 
join roles r 
    on r.role_id = urc.role_id
where login_name = @login_name
group by login_password, first_name, last_name, email, lang_prefix
go
IF OBJECT_ID('imse.PermissionsGetPermission') IS NOT NULL
     DROP PROCEDURE imse.Permission_12192000100921023
ELSE 
     EXEC sp_rename 'imse.Permission_12192000100921023','PermissionsGetPermission',OBJECT
go
EXEC sp_rename 'imse.RoleCountAffectedUsers','RoleCountA_12192000100921024',OBJECT
go
CREATE PROCEDURE RoleCountAffectedUsers
 @aRoleId int
AS
/*
 This function is used when an administrator tries to delete a role.
 This function counts how many users who will be affected
*/
SELECT  DISTINCT COUNT(usr.role_id )
FROM user_roles_crossref usr, roles r, users u
WHERE usr.ROLE_ID = @aRoleId 
AND usr.user_id = u.user_id
go
IF OBJECT_ID('imse.RoleCountAffectedUsers') IS NOT NULL
     DROP PROCEDURE imse.RoleCountA_12192000100921024
ELSE 
     EXEC sp_rename 'imse.RoleCountA_12192000100921024','RoleCountAffectedUsers',OBJECT
go
EXEC sp_rename 'imse.RoleDeleteViewAffectedUsers','RoleDelete_12192000100921025',OBJECT
go
CREATE PROCEDURE RoleDeleteViewAffectedUsers
 @aRoleId int
AS
/*
 This function is used when an administrator tries to delete a role.
 All users which will be affected of the deletion will be presenteted in a list
*/
SELECT distinct TOP 50  usr.role_id , (RTRIM(last_name) + ', ' + RTRIM(first_name))
FROM user_roles_crossref usr, roles r, users u
WHERE usr.ROLE_ID = @aRoleId 
AND usr.user_id = u.user_id
--GROUP BY (RTRIM(last_name) + ', ' + RTRIM(first_name)), usr.role_id
ORDER BY (RTRIM(last_name) + ', ' + RTRIM(first_name))
go
IF OBJECT_ID('imse.RoleDeleteViewAffectedUsers') IS NOT NULL
     DROP PROCEDURE imse.RoleDelete_12192000100921025
ELSE 
     EXEC sp_rename 'imse.RoleDelete_12192000100921025','RoleDeleteViewAffectedUsers',OBJECT
go
EXEC sp_rename 'imse.test','test_12192000100921026',OBJECT
go
CREATE PROCEDURE test AS
SELECT COUNT(usr.role_id) , (RTRIM(last_name) + ', ' + RTRIM(first_name))   
FROM user_roles_crossref usr, roles r, users u
WHERE usr.ROLE_ID = 5 
AND usr.user_id = u.user_id
GROUP BY (RTRIM(last_name) + ', ' + RTRIM(first_name)), usr.role_id
go
IF OBJECT_ID('imse.test') IS NOT NULL
     DROP PROCEDURE imse.test_12192000100921026
ELSE 
     EXEC sp_rename 'imse.test_12192000100921026','test',OBJECT
go
EXEC sp_rename 'imse.UpdateUser','UpdateUser_12192000100921027',OBJECT
go
CREATE PROCEDURE imse.UpdateUser
/*
usertype. 0=special, 1=default, 2=conferenceuser 
*/
 @user_id int,
 @login_name char(15),
 @login_password char(15),
 @first_name char(25),
 @last_name char(30),
 @title char(30),
 @company char(30),
 @address char(40),
 @city char(30),
 @zip char (15),
 @country char(30),
 @county_council char(30),
 @email char(50),
 @admin_mode int,
 @last_page int,
 @archive_mode int,
 @lang_id int,
 @user_type int,
 @active int
AS
UPDATE users 
SET login_name = @login_name,
login_password = @login_password,
first_name = @first_name,
last_name = @last_name,
title = @title,
company = @company,
address =  @address,
city = @city,
zip = @zip,
country = @country,
county_council = @county_council,
email = @email,
user_type = @user_type,
active = @active,
lang_id = @lang_id
WHERE user_id = @User_id 
/****** Object:  Stored Procedure dbo.UpdateUser    Script Date: 2000-10-27 15:19:01 *****
CREATE PROCEDURE UpdateUser
--usertype. 0=special, 1=default, 2=conferenceuser 
 @user_id int,
 @login_name char(15),
 @login_password char(15),
 @first_name char(25),
 @last_name char(30),
 @title char(30),
 @company char(30),
 @address char(40),
 @city char(30),
 @zip char (15),
 @country char(30),
 @county_council char(30),
 @email char(50),
 @admin_mode int,
 @last_page int,
 @archive_mode int,
 @lang_id int,
 @user_type int,
 @active int
AS
UPDATE users 
SET login_name = @login_name,
login_password = @login_password,
first_name = @first_name,
last_name = @last_name,
title = @title,
company = @company,
address =  @address,
city = @city,
zip = @zip,
country = @country,
county_council = @county_council,
email = @email,
user_type = @user_type,
active = @active
WHERE user_id = @User_id 
*/
go
IF OBJECT_ID('imse.UpdateUser') IS NOT NULL
     DROP PROCEDURE imse.UpdateUser_12192000100921027
ELSE 
     EXEC sp_rename 'imse.UpdateUser_12192000100921027','UpdateUser',OBJECT
go
EXEC sp_rename 'imse.UserPrefsChange','UserPrefsC_12192000100921028',OBJECT
go
CREATE PROCEDURE imse.UserPrefsChange
  @aUserId int
/*
  Returns the information for a user which he is able to change self. Observer that we
  return the password as an empty string
*/
AS
-- SELECT @aUserId AS 'TEST'
SELECT user_id, login_name,  "", "", first_name, last_name,  title, company, address, city, zip, country, county_council, email --, profession, company
FROM users
WHERE user_id = @aUserId 
/****** Object:  Stored Procedure dbo.UserPrefsChange    Script Date: 2000-10-27 15:19:01 *****
CREATE PROCEDURE imse.UserPrefsChange
  @aUserId int
--  Returns the information for a user which he is able to change self. Observer that we
--  return the password as an empty string
AS
-- SELECT @aUserId AS 'TEST'
SELECT user_id, login_name,  "", "", first_name, last_name,  title, company, address, city, zip, country, county_council, email --, profession, company
FROM users
WHERE user_id = @aUserId 
*/
go
IF OBJECT_ID('imse.UserPrefsChange') IS NOT NULL
     DROP PROCEDURE imse.UserPrefsC_12192000100921028
ELSE 
     EXEC sp_rename 'imse.UserPrefsC_12192000100921028','UserPrefsChange',OBJECT
go

--
-- Procedure Recreate
-- imse.ListDocsByDate
--
EXEC sp_rename 'imse.ListDocsByDate','ListDocsBy_12192000100900001',OBJECT
go
CREATE PROCEDURE imse.ListDocsByDate @listMod int,  @doc_type int, @startDate varchar(10), @endDate varchar(20) AS
/*
 lists doctyps where activate = 1
 @listMod 0 = all date, 1 = only creatdat, 2 = only modifieddata
 @startDoc yyyy-mm-dd or 0 then not set
 @endDate yyyy-mm-dd or 0 then not set
*/
-- Listdate fix 
if ( @endDate <> '0') BEGIN
 SET @endDate = @endDate + ' 23:59:59'
 PRINT @endDate
END 
/* list all (not in use ) */
if ( @listMod = 0) begin
 if ( @startDate = '0' ) begin
  if ( @endDate = '0' ) begin
   select m.meta_id, dt.type, m.meta_headline, m.date_modified
 
   from meta m
   join doc_types dt
      on m.doc_type = dt.doc_type
   where m.doc_type = @doc_type and activate = 1
   order by m.date_modified
  end
  else begin
   select m.meta_id, dt.type, m.meta_headline, m.date_modified
 
   from meta m
   join doc_types dt
      on m.doc_type = dt.doc_type
   where m.doc_type = @doc_type and m.date_modified <= @endDate and activate = 1
   order by m.date_modified
  end
 end
 else if ( @startDate != '0' ) begin
  if ( @endDate = '0' ) begin
   select m.meta_id, dt.type, m.meta_headline, m.date_modified
 
   from meta m
   join doc_types dt
      on m.doc_type = dt.doc_type
   where m.doc_type = @doc_type and m.date_modified >= @startDate and activate = 1
   order by m.date_modified
  end
  else
  begin
   select m.meta_id, dt.type, m.meta_headline, m.date_modified
 
   from meta m
   join doc_types dt
      on m.doc_type = dt.doc_type
   where m.doc_type = @doc_type and m.date_modified <= @endDate and m.date_modified >= @startDate and activate = 1
   order by m.date_modified
  end
 end
end
/* list creatdate */
else if ( @listMod = 1) begin
 if ( @startDate = '0' ) begin
  if ( @endDate = '0' ) begin
   select m.meta_id, dt.type, m.meta_headline, m.date_created
 
   from meta m
   join doc_types dt
      on m.doc_type = dt.doc_type
   where m.doc_type = @doc_type and activate = 1
   order by m.date_created
  end
  else begin
   select m.meta_id, dt.type, m.meta_headline, m.date_created
 
   from meta m
   join doc_types dt
      on m.doc_type = dt.doc_type
   where m.doc_type = @doc_type and m.date_created <= @endDate and activate = 1
   order by m.date_created
  end
 end
 else if ( @startDate != '0' ) begin
  if ( @endDate = '0' ) begin
   select m.meta_id, dt.type, m.meta_headline, m.date_created
 
   from meta m
   join doc_types dt
      on m.doc_type = dt.doc_type
   where m.doc_type = @doc_type and m.date_created >= @startDate and activate = 1
   order by m.date_created
  end
  else
  begin
   select m.meta_id, dt.type, m.meta_headline, m.date_created
 
   from meta m
   join doc_types dt
      on m.doc_type = dt.doc_type
   where m.doc_type = @doc_type and m.date_created <= @endDate and m.date_created >= @startDate and activate = 1
   order by m.date_created
  end
 end
end
/* list only modified*/
else begin
 if ( @startDate = '0' ) begin
  if ( @endDate = '0' ) begin
   select m.meta_id, dt.type, m.meta_headline, m.date_modified
 
   from meta m
   join doc_types dt
      on m.doc_type = dt.doc_type
   where m.doc_type = @doc_type and activate = 1 and m.date_modified != m.date_created
   order by m.date_modified
  end
  else begin
   select m.meta_id, dt.type, m.meta_headline, m.date_modified
 
   from meta m
   join doc_types dt
      on m.doc_type = dt.doc_type
   where m.doc_type = @doc_type and m.date_modified <= @endDate and activate = 1 and m.date_modified != m.date_created
   order by m.date_modified
  end
 end
 else if ( @startDate != '0' ) begin
  if ( @endDate = '0' ) begin
   select m.meta_id, dt.type, m.meta_headline, m.date_modified
 
   from meta m
   join doc_types dt
      on m.doc_type = dt.doc_type
   where m.doc_type = @doc_type and m.date_modified >= @startDate and activate = 1 and m.date_modified != m.date_created
   order by m.date_modified
  end
  else
  begin
   select m.meta_id, dt.type, m.meta_headline, m.date_modified
 
   from meta m
   join doc_types dt
      on m.doc_type = dt.doc_type
   where m.doc_type = @doc_type and m.date_modified <= @endDate and m.date_modified >= @startDate and activate = 1 and m.date_modified != m.date_created
   order by m.date_modified
  end
 end
end
go
IF OBJECT_ID('imse.ListDocsByDate') IS NOT NULL
     DROP PROCEDURE imse.ListDocsBy_12192000100900001
ELSE 
     EXEC sp_rename 'imse.ListDocsBy_12192000100900001','ListDocsByDate',OBJECT
go

--
-- Procedure Recreate
-- imse.ListDocsGetInternalDocTypes
--
EXEC sp_rename 'imse.ListDocsGetInternalDocTypes','ListDocsGe_12192000100900002',OBJECT
go
CREATE PROCEDURE ListDocsGetInternalDocTypes AS
/* selct all internal doc types */
select doc_type, type 
from doc_types
where doc_type <= 100
go
IF OBJECT_ID('imse.ListDocsGetInternalDocTypes') IS NOT NULL
     DROP PROCEDURE imse.ListDocsGe_12192000100900002
ELSE 
     EXEC sp_rename 'imse.ListDocsGe_12192000100900002','ListDocsGetInternalDocTypes',OBJECT
go

--
-- Procedure Recreate
-- imse.ListDocsGetInternalDocTypesValue
--
EXEC sp_rename 'imse.ListDocsGetInternalDocTypesValue','ListDocsGe_12192000100900003',OBJECT
go
CREATE PROCEDURE ListDocsGetInternalDocTypesValue AS
/* selct all internal doc types */
select doc_type
from doc_types
where doc_type <= 100
go
IF OBJECT_ID('imse.ListDocsGetInternalDocTypesValue') IS NOT NULL
     DROP PROCEDURE imse.ListDocsGe_12192000100900003
ELSE 
     EXEC sp_rename 'imse.ListDocsGe_12192000100900003','ListDocsGetInternalDocTypesValue',OBJECT
go

--
-- Procedure Recreate
-- imse.IPAccessAdd
--
EXEC sp_rename 'imse.IPAccessAdd','IPAccessAd_12192000100903001',OBJECT
go
CREATE PROCEDURE IPAccessAdd
/*
This function adds a new ip-access to the db. Used by AdminManager
*/
 @user_id int,
 @ip_start varchar(15) , 
 @ip_end varchar(15)
AS
INSERT INTO IP_ACCESSES ( user_id , ip_start , ip_end )
VALUES ( @user_id , @ip_start , @ip_end )
go
IF OBJECT_ID('imse.IPAccessAdd') IS NOT NULL
     DROP PROCEDURE imse.IPAccessAd_12192000100903001
ELSE 
     EXEC sp_rename 'imse.IPAccessAd_12192000100903001','IPAccessAdd',OBJECT
go

--
-- Procedure Recreate
-- imse.IPAccessDelete
--
EXEC sp_rename 'imse.IPAccessDelete','IPAccessDe_12192000100903002',OBJECT
go
CREATE PROCEDURE IPAccessDelete
/*
 Deletes an Ip-access for a user. Used by the AdminIpAccess servlet
*/
 @ipAccessId int
AS
DELETE FROM IP_ACCESSES 
WHERE ip_access_id = @ipAccessId
go
IF OBJECT_ID('imse.IPAccessDelete') IS NOT NULL
     DROP PROCEDURE imse.IPAccessDe_12192000100903002
ELSE 
     EXEC sp_rename 'imse.IPAccessDe_12192000100903002','IPAccessDelete',OBJECT
go

--
-- Procedure Recreate
-- imse.IPAccessesGetAll
--
EXEC sp_rename 'imse.IPAccessesGetAll','IPAccesses_12192000100903003',OBJECT
go
CREATE PROCEDURE IPAccessesGetAll AS
/*
Lets get all IPaccesses from db. Used  by the AdminIpAccesses
*/
SELECT ip.ip_access_id, ip.user_id, usr.login_name, ip.ip_start, ip.ip_end    
FROM IP_ACCESSES ip, USERS usr
WHERE ip.user_id = usr.user_id
go
IF OBJECT_ID('imse.IPAccessesGetAll') IS NOT NULL
     DROP PROCEDURE imse.IPAccesses_12192000100903003
ELSE 
     EXEC sp_rename 'imse.IPAccesses_12192000100903003','IPAccessesGetAll',OBJECT
go

--
-- Procedure Recreate
-- imse.IPAccessUpdate
--
EXEC sp_rename 'imse.IPAccessUpdate','IPAccessUp_12192000100903004',OBJECT
go
CREATE PROCEDURE IPAccessUpdate
/*
Updates the IPaccess table
*/
 @IpAccessId int ,
 @newUserId int,
 @newIpStart varchar(15) ,
 @newIpEnd varchar(15) 
AS
UPDATE IP_ACCESSES
SET user_id = @newUserId ,
 ip_start = @newIpStart,
 ip_end = @newIpEnd
WHERE ip_access_id = @IpAccessId
go
IF OBJECT_ID('imse.IPAccessUpdate') IS NOT NULL
     DROP PROCEDURE imse.IPAccessUp_12192000100903004
ELSE 
     EXEC sp_rename 'imse.IPAccessUp_12192000100903004','IPAccessUpdate',OBJECT
go

--
-- Procedure Recreate
-- imse.GetLanguageList
--
EXEC sp_rename 'imse.GetLanguageList','GetLanguag_12192000100904001',OBJECT
go
CREATE PROCEDURE GetLanguageList AS
/*
 Returns all 
*/
SELECT lp.lang_id , lang.language
FROM lang_prefixes lp, languages lang
WHERE lp.lang_prefix = lang.lang_prefix
go
IF OBJECT_ID('imse.GetLanguageList') IS NOT NULL
     DROP PROCEDURE imse.GetLanguag_12192000100904001
ELSE 
     EXEC sp_rename 'imse.GetLanguag_12192000100904001','GetLanguageList',OBJECT
go

--
-- Procedure Recreate
-- imse.getLanguages
--
EXEC sp_rename 'imse.getLanguages','getLanguag_12192000100904002',OBJECT
go
CREATE PROCEDURE getLanguages AS
select lang_prefix,language from languages order by language
go
IF OBJECT_ID('imse.getLanguages') IS NOT NULL
     DROP PROCEDURE imse.getLanguag_12192000100904002
ELSE 
     EXEC sp_rename 'imse.getLanguag_12192000100904002','getLanguages',OBJECT
go

--
-- Procedure Recreate
-- dbo.getDocsParentCount
--
EXEC sp_rename 'dbo.getDocsParentCount','getDocsPar_12192000100906002',OBJECT
go
CREATE PROCEDURE getDocsParentCount @user_id int AS
-- Lists the documents that have parents, and how many, provided the user may write to them.
if exists ( -- Is user superadmin?
 select
  *
 from
  user_roles_crossref
 where
  user_id = @user_id
  and role_id = 0
) begin -- User is superadmin. Select all rows.
 select
  to_meta_id,
  count(meta_id) as parents
 from
  childs 
 group by
  to_meta_id
 union
 select
  distinct to_meta_id,
  count(meta_id) as parents
 from
  browser_docs
 group by
  to_meta_id
 order by
  to_meta_id
end else begin -- User is not superadmin.
 select
  to_meta_id,
  count(childs.meta_id) as parents
 from
  childs
  join meta
 on
  childs.to_meta_id = meta.meta_id
 where
  meta.meta_id in ( -- Is user member of role allowed to write to the document?
   select
    meta.meta_id 
   from
    meta
    join roles_rights
   on
    meta.meta_id = roles_rights.meta_id
    and roles_rights.permission_id = 3
    join user_roles_crossref
   on
    roles_rights.role_id = user_roles_crossref.role_id
    and user_roles_crossref.user_id = @user_id
  ) or meta.meta_id in ( -- Is user the owner of the document?
   select
    meta_id 
   from
    user_rights
   where
    user_id = @user_id
    and permission_id = 99
  )
 group by
  to_meta_id
 union
  select
   distinct to_meta_id,
   count(browser_docs.meta_id) as parents
  from
   browser_docs
   join meta
  on
   to_meta_id = meta.meta_id
  where
   meta.meta_id in ( -- Is user member of role allowed to write to the document?
    select
     meta.meta_id 
    from
     meta
     join roles_rights
    on
     meta.meta_id = roles_rights.meta_id
     and roles_rights.permission_id = 3
     join user_roles_crossref
    on
     roles_rights.role_id = user_roles_crossref.role_id
     and user_roles_crossref.user_id = @user_id
   ) or meta.meta_id in ( -- Is user the owner of the document?
    select
     meta_id 
    from
     user_rights
    where
     user_id = @user_id
     and permission_id = 99
   )
  group by
   to_meta_id
 order by 
  to_meta_id
end
go
IF OBJECT_ID('dbo.getDocsParentCount') IS NOT NULL
     DROP PROCEDURE dbo.getDocsPar_12192000100906002
ELSE 
     EXEC sp_rename 'dbo.getDocsPar_12192000100906002','getDocsParentCount',OBJECT
go

--
-- Procedure Recreate
-- dbo.testProc
--
EXEC sp_rename 'dbo.testProc','testProc_12192000100906003',OBJECT
go
CREATE PROCEDURE testProc AS
select * from meta
go
IF OBJECT_ID('dbo.testProc') IS NOT NULL
     DROP PROCEDURE dbo.testProc_12192000100906003
ELSE 
     EXEC sp_rename 'dbo.testProc_12192000100906003','testProc',OBJECT
go

--
-- Procedure Recreate
-- imse.classification_convert
--
EXEC sp_rename 'imse.classification_convert','classifica_12192000100906004',OBJECT
go
CREATE PROCEDURE imse.classification_convert AS
-- Hämta alla klassificeringskoder och för över dem till nya systemet. Observera att denna inte fixar
-- de fält som har , tecken som delimiter
DECLARE @meta_id int
DECLARE @class varchar(200)
DECLARE tmpCursor CURSOR FOR
 SELECT meta_id, classification
 FROM meta
 WHERE classification IS NOT NULL
 and classification <> ''
 and classification NOT LIKE 'META NAME%'
 and classification NOT LIKE 'Test'
 -- AND meta_id = 2201
OPEN tmpCursor
FETCH NEXT FROM tmpCursor INTO @meta_id, @class
WHILE @@fetch_status = 0 BEGIN
 PRINT 'Class: ' + @class 
EXEC classification_fix @meta_id, @class 
 FETCH NEXT FROM tmpCursor INTO @meta_id, @class
END
CLOSE tmpCursor
DEALLOCATE tmpCursor
go
IF OBJECT_ID('imse.classification_convert') IS NOT NULL
     DROP PROCEDURE imse.classifica_12192000100906004
ELSE 
     EXEC sp_rename 'imse.classifica_12192000100906004','classification_convert',OBJECT
go

--
-- Procedure Recreate
-- imse.Classification_Get_All
--
EXEC sp_rename 'imse.Classification_Get_All','Classifica_12192000100906005',OBJECT
go
CREATE PROCEDURE Classification_Get_All AS
/*
Get the meta_id and classifcation so we can start convert them
*/
SELECT meta_id, classification
 FROM meta
 WHERE classification IS NOT NULL
 and classification <> ''
 and classification NOT LIKE 'META NAME%'
 and classification NOT LIKE 'Test'
go
IF OBJECT_ID('imse.Classification_Get_All') IS NOT NULL
     DROP PROCEDURE imse.Classifica_12192000100906005
ELSE 
     EXEC sp_rename 'imse.Classifica_12192000100906005','Classification_Get_All',OBJECT
go

--
-- Procedure Recreate
-- imse.FindMetaId
--
EXEC sp_rename 'imse.FindMetaId','FindMetaId_12192000100906006',OBJECT
go
CREATE PROCEDURE [FindMetaId]
 @meta_id int
 AS
SELECT meta_id 
FROM meta
WHERE meta_id = @meta_id
go
IF OBJECT_ID('imse.FindMetaId') IS NOT NULL
     DROP PROCEDURE imse.FindMetaId_12192000100906006
ELSE 
     EXEC sp_rename 'imse.FindMetaId_12192000100906006','FindMetaId',OBJECT
go

--
-- Procedure Recreate
-- imse.GetAdminChilds
--
EXEC sp_rename 'imse.GetAdminChilds','GetAdminCh_12192000100906007',OBJECT
go
CREATE PROCEDURE GetAdminChilds
@meta_id int,
@user_id int
AS
select   to_meta_id
from   childs c
join   meta m    
     on   m.meta_id = c.to_meta_id    -- meta.meta_id corresponds to childs.to_meta_id
      and m.activate != 0      -- Only include the documents that are active in the meta table.
      and c.meta_id = @meta_id     -- Only include documents that are children to this particular meta_id
left join  roles_rights rr        -- We may have permission, even though we don't have anything in role-permissions... That is, if we're docowner or superadmin.
     on  c.to_meta_id = rr.meta_id     -- Only include rows with the documents we are interested in
       and rr.permission_id  = 3     -- Only include permissions that gives right to change the document
join  user_roles_crossref urc       -- This table tells us which users have which roles
     on  urc.user_id = @user_id     -- Only include the rows with the user we are interested in.
      and ( rr.role_id = urc.role_id    -- Include rows where the users roles match the roles that have permissions on the documents
       or urc.role_id = 0     -- and also include the rows that tells us this user is a superadmin (That is, return a row with urc.role_id = 0 for each document.)
       )
left join  user_rights ur        -- We may have permission, even though we don't own any documents... That is, if we're roleadmins or superadmin. When we lose ownership-based rights, this may be skipped.
     on  ur.meta_id = c.to_meta_id    -- Only include rows with the documents we are interested in
      and ur.user_id =  urc.user_id     -- Only include rows with the user  we are interested in
      and ur.permission_id = 99     -- Only include rows that mean "Ownership"
go
IF OBJECT_ID('imse.GetAdminChilds') IS NOT NULL
     DROP PROCEDURE imse.GetAdminCh_12192000100906007
ELSE 
     EXEC sp_rename 'imse.GetAdminCh_12192000100906007','GetAdminChilds',OBJECT
go

--
-- Procedure Recreate
-- imse.getBrowserDocChilds
--
EXEC sp_rename 'imse.getBrowserDocChilds','getBrowser_12192000100906008',OBJECT
go
CREATE PROCEDURE getBrowserDocChilds @meta_id int, @user_id int AS
-- Lists the childs for a specific browser_doc
SELECT DISTINCT to_meta_id,
   meta_headline
FROM   browser_docs bd
JOIN   meta m   ON bd.to_meta_id = m.meta_id
      AND bd.meta_id = @meta_id
LEFT JOIN  user_rights ur  ON ur.meta_id = m.meta_id
      AND ur.user_id = @user_id
      AND ur.permission_id = 99
LEFT JOIN  roles_rights rr  ON rr.meta_id = m.meta_id
      AND rr.permission_id = 3
JOIN   user_roles_crossref urc ON urc.user_id = @user_id
      AND (
        urc.role_id = 0
       OR (
         urc.role_id = rr.role_id
        AND urc.user_id = ur.user_id
        )
       OR m.shared = 1
       )
WHERE m.activate = 1
ORDER BY to_meta_id
go
IF OBJECT_ID('imse.getBrowserDocChilds') IS NOT NULL
     DROP PROCEDURE imse.getBrowser_12192000100906008
ELSE 
     EXEC sp_rename 'imse.getBrowser_12192000100906008','getBrowserDocChilds',OBJECT
go

--
-- Procedure Recreate
-- imse.GetChilds
--
EXEC sp_rename 'imse.GetChilds','GetChilds_12192000100906009',OBJECT
go
CREATE PROCEDURE GetChilds
 @meta_id int,
 @user_id int
AS
/*
Nice little query that lists the children of a document that a particular user may see, and includes a field that tells you wether he may do something to it or not.
*/
declare @sort_by int
select @sort_by = sort_order from text_docs where meta_id = @meta_id
-- Manual sort order
if @sort_by = 2
begin
select  to_meta_id, c.menu_sort,manual_sort_order, doc_type,
  archive,target, left(convert (varchar,date_created,120),10),
  meta_headline,meta_text,meta_image,frame_name,
  activated_date+activated_time,archived_date+archived_time,
  min(            -- This field will have 0 in it, if the user may change the document
   coalesce(urc.role_id,rr.permission_id-3,ur.permission_id-99)*
   coalesce(ur.permission_id-99,rr.permission_id-3,urc.role_id)*
   coalesce(rr.permission_id-3,urc.role_id,ur.permission_id-99)
  )
from   childs c
join   meta m    
     on   m.meta_id = c.to_meta_id    -- meta.meta_id corresponds to childs.to_meta_id
      and m.activate != 0      -- Only include the documents that are active in the meta table.
      and c.meta_id = @meta_id     -- Only include documents that are children to this particular meta_id
left join  roles_rights rr         -- We may have permission, even though we don't have anything in role-permissions... That is, if we're docowner or superadmin.
     on  c.to_meta_id = rr.meta_id     -- Only include rows with the documents we are interested in
       --and (rr.permission_id & 1) != 0    -- Only include permissions where the first bit is set. "Read rights" (May skip this now, since right now, if we have anything, this matches.)
join  user_roles_crossref urc        -- This table tells us which users have which roles
     on  urc.user_id = @user_id     -- Only include the rows with the user we are interested in...
      and ( 
        rr.role_id = urc.role_id    -- Include rows where the users roles match the roles that have permissions on the documents
       or urc.role_id = 0     -- and also include the rows that tells us this user is a superadmin
       or ( 
         m.show_meta != 0   -- and also include documents that are to be shown regardless of rights. (Visa även för obehöriga)
        and rr.permission_id != 3   -- Don't include show_meta if it is not needed.
        ) 
       )
left join  user_rights ur         -- We may have permission, even though we don't own any documents... That is, if we're roleadmins or superadmin. When we lose ownership-based rights, this may be skipped.
     on  ur.meta_id = c.to_meta_id    -- Only include rows with the documents we are interested in
      and ur.user_id =  urc.user_id     -- Only include rows with the user  we are interested in
      and ur.permission_id = 99     -- Only include rows that mean "Ownership"
group by to_meta_id, c.menu_sort,manual_sort_order, doc_type,
  archive,target, left(convert (varchar,date_created,120),10),
  meta_headline,meta_text,meta_image,frame_name,
  activated_date+activated_time,archived_date+archived_time
order by  menu_sort,c.manual_sort_order desc
end
else if @sort_by = 3
begin
select  to_meta_id, c.menu_sort,manual_sort_order, doc_type,
  archive,target, left(convert (varchar,date_created,120),10),
  meta_headline,meta_text,meta_image,frame_name,
  activated_date+activated_time,archived_date+archived_time,
  min(
   coalesce(urc.role_id,rr.permission_id-3,ur.permission_id-99)*
   coalesce(ur.permission_id-99,rr.permission_id-3,urc.role_id)*
   coalesce(rr.permission_id-3,urc.role_id,ur.permission_id-99)
  )
from   childs c
join   meta m    
     on   m.meta_id = c.to_meta_id    -- meta.meta_id corresponds to childs.to_meta_id
      and m.activate != 0      -- Only include the documents that are active in the meta table.
      and c.meta_id = @meta_id     -- Only include documents that are children to this particular meta_id
left join  roles_rights rr         -- We may have permission, even though we don't have anything in role-permissions... That is, if we're docowner or superadmin.
     on  c.to_meta_id = rr.meta_id     -- Only include rows with the documents we are interested in
       --and (rr.permission_id & 1) != 0    -- Only include permissions where the first bit is set. "Read rights" (May skip this now, since right now, if we have anything, this matches.)
join  user_roles_crossref urc        -- This table tells us which users have which roles
     on  urc.user_id = @user_id     -- Only include the rows with the user we are interested in...
      and ( 
        rr.role_id = urc.role_id    -- Include rows where the users roles match the roles that have permissions on the documents
       or urc.role_id = 0     -- and also include the rows that tells us this user is a superadmin
       or ( 
         m.show_meta != 0   -- and also include documents that are to be shown regardless of rights. (Visa även för obehöriga)
        and rr.permission_id != 3   -- Don't include show_meta if it is not needed.
        ) 
       )
left join  user_rights ur         -- We may have permission, even though we don't own any documents... That is, if we're roleadmins or superadmin. When we lose ownership-based rights, this may be skipped.
     on  ur.meta_id = c.to_meta_id    -- Only include rows with the documents we are interested in
      and ur.user_id =  urc.user_id     -- Only include rows with the user  we are interested in
      and ur.permission_id = 99     -- Only include rows that mean "Ownership"
group by to_meta_id, c.menu_sort,manual_sort_order, doc_type,
  archive,target, left(convert (varchar,date_created,120),10),
  meta_headline,meta_text,meta_image,frame_name,
  activated_date+activated_time,archived_date+archived_time
order by  menu_sort,left(convert (varchar,date_created,120),10) desc
end
else if @sort_by = 1
begin
select  to_meta_id, c.menu_sort,manual_sort_order, doc_type,
  archive,target, left(convert (varchar,date_created,120),10),
  meta_headline,meta_text,meta_image,frame_name,
  activated_date+activated_time,archived_date+archived_time,
  min(
   coalesce(urc.role_id,rr.permission_id-3,ur.permission_id-99)*
   coalesce(ur.permission_id-99,rr.permission_id-3,urc.role_id)*
   coalesce(rr.permission_id-3,urc.role_id,ur.permission_id-99)
  )
from   childs c
join   meta m    
     on   m.meta_id = c.to_meta_id    -- meta.meta_id corresponds to childs.to_meta_id
      and m.activate != 0      -- Only include the documents that are active in the meta table.
      and c.meta_id = @meta_id     -- Only include documents that are children to this particular meta_id
left join  roles_rights rr         -- We may have permission, even though we don't have anything in role-permissions... That is, if we're docowner or superadmin.
     on  c.to_meta_id = rr.meta_id     -- Only include rows with the documents we are interested in
       --and (rr.permission_id & 1) != 0    -- Only include permissions where the first bit is set. "Read rights" (May skip this now, since right now, if we have anything, this matches.)
join  user_roles_crossref urc        -- This table tells us which users have which roles
     on  urc.user_id = @user_id     -- Only include the rows with the user we are interested in...
      and ( 
        rr.role_id = urc.role_id    -- Include rows where the users roles match the roles that have permissions on the documents
       or urc.role_id = 0     -- and also include the rows that tells us this user is a superadmin
       or ( 
         m.show_meta != 0   -- and also include documents that are to be shown regardless of rights. (Visa även för obehöriga)
        and rr.permission_id != 3   -- Don't include show_meta if it is not needed.
        ) 
       )
left join  user_rights ur         -- We may have permission, even though we don't own any documents... That is, if we're roleadmins or superadmin. When we lose ownership-based rights, this may be skipped.
     on  ur.meta_id = c.to_meta_id    -- Only include rows with the documents we are interested in
      and ur.user_id =  urc.user_id     -- Only include rows with the user  we are interested in
      and ur.permission_id = 99     -- Only include rows that mean "Ownership"
group by to_meta_id, c.menu_sort,manual_sort_order, doc_type,
  archive,target, left(convert (varchar,date_created,120),10),
  meta_headline,meta_text,meta_image,frame_name,
  activated_date+activated_time,archived_date+archived_time
order by  menu_sort,meta_headline
end
go
IF OBJECT_ID('imse.GetChilds') IS NOT NULL
     DROP PROCEDURE imse.GetChilds_12192000100906009
ELSE 
     EXEC sp_rename 'imse.GetChilds_12192000100906009','GetChilds',OBJECT
go

--
-- Procedure Recreate
-- imse.getDocs
--
EXEC sp_rename 'imse.getDocs','getDocs_12192000100906010',OBJECT
go
CREATE PROCEDURE getDocs @user_id int, @start int, @end int AS
-- Lists documents user is allowed to see.
SELECT DISTINCT m.meta_id,
   COUNT(DISTINCT c.meta_id) parentcount,
   meta_headline,
   doc_type
FROM   meta m
LEFT JOIN  childs c   ON c.to_meta_id = m.meta_id
LEFT JOIN  user_rights ur  ON ur.meta_id = m.meta_id
      AND ur.permission_id = 99
LEFT JOIN  roles_rights rr  ON rr.meta_id = m.meta_id
      AND rr.permission_id = 3
JOIN   user_roles_crossref urc ON urc.user_id = @user_id
      AND (
        urc.role_id = 0
       OR (
         urc.role_id = rr.role_id
        AND urc.user_id = ur.user_id
        )
       OR m.shared = 1
       )
WHERE  m.activate = 1
  AND m.meta_id > (@start-1) 
  AND m.meta_id < (@end+1)
GROUP BY  m.meta_id,m.meta_headline,m.doc_type,c.to_meta_id
ORDER BY  m.meta_id
go
IF OBJECT_ID('imse.getDocs') IS NOT NULL
     DROP PROCEDURE imse.getDocs_12192000100906010
ELSE 
     EXEC sp_rename 'imse.getDocs_12192000100906010','getDocs',OBJECT
go

--
-- Procedure Recreate
-- imse.GetDocType
--
EXEC sp_rename 'imse.GetDocType','GetDocType_12192000100906011',OBJECT
go
CREATE PROCEDURE GetDocType
 @meta_id int
AS
/*
 Used by external systems to get the docType
*/
SELECT doc_type
FROM meta
WHERE meta_id = @meta_id
go
IF OBJECT_ID('imse.GetDocType') IS NOT NULL
     DROP PROCEDURE imse.GetDocType_12192000100906011
ELSE 
     EXEC sp_rename 'imse.GetDocType_12192000100906011','GetDocType',OBJECT
go

--
-- Procedure Recreate
-- imse.GetLangPrefix
--
EXEC sp_rename 'imse.GetLangPrefix','GetLangPre_12192000100906012',OBJECT
go
CREATE PROCEDURE GetLangPrefix
 @meta_id int
AS
/*
 Used by external systems to get the langprefix
*/
SELECT lang_prefix 
FROM meta
WHERE meta_id = @meta_id
go
IF OBJECT_ID('imse.GetLangPrefix') IS NOT NULL
     DROP PROCEDURE imse.GetLangPre_12192000100906012
ELSE 
     EXEC sp_rename 'imse.GetLangPre_12192000100906012','GetLangPrefix',OBJECT
go

--
-- Procedure Recreate
-- imse.getMenuDocChilds
--
EXEC sp_rename 'imse.getMenuDocChilds','getMenuDoc_12192000100906013',OBJECT
go
CREATE PROCEDURE getMenuDocChilds @meta_id int, @user_id int AS
-- Lists the childs of menudoc @meta_id
SELECT DISTINCT to_meta_id,
   meta_headline
FROM   childs c
JOIN   meta m   ON c.to_meta_id = m.meta_id
      AND c.meta_id = @meta_id
LEFT JOIN  user_rights ur  ON ur.meta_id = m.meta_id
      AND ur.user_id = @user_id
      AND ur.permission_id = 99
LEFT JOIN  roles_rights rr  ON rr.meta_id = m.meta_id
      AND rr.permission_id = 3
JOIN   user_roles_crossref urc ON urc.user_id = @user_id
      AND ( urc.role_id = 0
       OR (
         urc.role_id = rr.role_id
        AND urc.user_id = ur.user_id
        )
       OR m.shared = 1
       )
WHERE m.activate = 1
ORDER BY to_meta_id
go
IF OBJECT_ID('imse.getMenuDocChilds') IS NOT NULL
     DROP PROCEDURE imse.getMenuDoc_12192000100906013
ELSE 
     EXEC sp_rename 'imse.getMenuDoc_12192000100906013','getMenuDocChilds',OBJECT
go

--
-- Procedure Recreate
-- imse.GetMetaPathInfo
--
EXEC sp_rename 'imse.GetMetaPathInfo','GetMetaPat_12192000100906014',OBJECT
go
CREATE PROCEDURE GetMetaPathInfo
 @meta_id int
AS
/*
 Used by external systems to get the meta_id dependent part of the path to
for example the image folder or to the html folder
Ex of what this function will return: 
*/
DECLARE @docType char(20)
DECLARE @langPrefix char(20)
SELECT RTRIM(lang_prefix) + '/' +  RTRIM(doc_type) + '/' 
FROM META 
WHERE meta_id = @meta_id
go
IF OBJECT_ID('imse.GetMetaPathInfo') IS NOT NULL
     DROP PROCEDURE imse.GetMetaPat_12192000100906014
ELSE 
     EXEC sp_rename 'imse.GetMetaPat_12192000100906014','GetMetaPathInfo',OBJECT
go

--
-- Procedure Recreate
-- imse.IMC_CreateNewMeta
--
EXEC sp_rename 'imse.IMC_CreateNewMeta','IMC_Create_12192000100906015',OBJECT
go
CREATE PROCEDURE IMC_CreateNewMeta
/* create new metadata */
@new_meta_id int,
@description varchar(80),
@doc_type int,
@meta_head_line varchar(255),
@meta_text varchar(1000),
@meta_image varchar(255),
@category_id int,
@processing_id int,
@shared int,
@expanded int,
@show_meta int,
@help_text_id int, /* help_text_id = 1 */
@archived int,
@status_id int,   /* status_id  = 1 */
@lang_prefix varchar(3),
@classification varchar(20),
@date_created datetime,
@date_modified datetime,
@sort_position int,
@menu_position int,
@disable_search int,
@activated_date varchar(10),
@activated_time varchar(6),
@archived_date varchar(10),
@archived_time varchar(6),
@target varchar(10),
@frame_name varchar(20),
@activate int
 AS
insert into meta
values(@new_meta_id ,@description,@doc_type,@meta_head_line,@meta_text,@meta_image, @category_id,
@processing_id,@shared,@expanded,@help_text_id,@show_meta,@archived,@status_id,@lang_prefix,@classification,@date_created,
@date_modified, @sort_position,@menu_position,@disable_search,@activated_date,@activated_time,@archived_date,
@archived_time,@target,@frame_name,@activate)
go
IF OBJECT_ID('imse.IMC_CreateNewMeta') IS NOT NULL
     DROP PROCEDURE imse.IMC_Create_12192000100906015
ELSE 
     EXEC sp_rename 'imse.IMC_Create_12192000100906015','IMC_CreateNewMeta',OBJECT
go

--
-- Procedure Recreate
-- imse.IMC_GetMaxMetaID
--
EXEC sp_rename 'imse.IMC_GetMaxMetaID','IMC_GetMax_12192000100906016',OBJECT
go
CREATE PROCEDURE IMC_GetMaxMetaID AS
/* get max meta id */
select max(meta_id) from meta
go
IF OBJECT_ID('imse.IMC_GetMaxMetaID') IS NOT NULL
     DROP PROCEDURE imse.IMC_GetMax_12192000100906016
ELSE 
     EXEC sp_rename 'imse.IMC_GetMax_12192000100906016','IMC_GetMaxMetaID',OBJECT
go

--
-- Procedure Recreate
-- imse.ListConferences
--
EXEC sp_rename 'imse.ListConferences','ListConfer_12192000100906017',OBJECT
go
CREATE PROCEDURE ListConferences AS
select meta_id, meta_headline 
from meta 
where doc_type = 102
go
IF OBJECT_ID('imse.ListConferences') IS NOT NULL
     DROP PROCEDURE imse.ListConfer_12192000100906017
ELSE 
     EXEC sp_rename 'imse.ListConfer_12192000100906017','ListConferences',OBJECT
go

--
-- Procedure Recreate
-- imse.meta_select
--
EXEC sp_rename 'imse.meta_select','meta_selec_12192000100906019',OBJECT
go
CREATE PROCEDURE meta_select AS
select menu_sort,manual_sort_order,date_created,meta_headline,target from meta,childs,roles_rights,user_roles_crossref 
where meta.meta_id = childs.to_meta_id 
and childs.meta_id = 4260 
and meta.archive=0 
and meta.activate=1 
and  roles_rights.meta_id = childs.to_meta_id 
and roles_rights.permission_id > 0 
and roles_rights.role_id = user_roles_crossref.role_id 
and user_roles_crossref.user_id =99
and meta.activated_date + ' ' + meta.activated_time <= '2000-05-02 02:37' 
and (meta.archived_date + ' ' + meta.archived_time >= '2000-05-02 02:37' or meta.archived_date + ' ' + meta.archived_time = '') 
union 
 select menu_sort,manual_sort_order,date_created,meta_headline,target from meta,childs,user_rights 
where meta.meta_id = childs.to_meta_id 
and childs.meta_id =  4260
and meta.archive=0 
and meta.activate=1 
and user_rights.meta_id = childs.to_meta_id 
and user_rights.permission_id > 0 
and user_rights.user_id = 99
and meta.activated_date + ' ' + meta.activated_time <= '2000-05-02 02:37' 
and (meta.archived_date + ' ' + meta.archived_time >= '2000-05-02 02:37' 
or meta.archived_date + ' ' + meta.archived_time = '') 
order by menu_sort,meta.meta_headline
go
IF OBJECT_ID('imse.meta_select') IS NOT NULL
     DROP PROCEDURE imse.meta_selec_12192000100906019
ELSE 
     EXEC sp_rename 'imse.meta_selec_12192000100906019','meta_select',OBJECT
go

--
-- Procedure Recreate
-- imse.getUserWriteRights
--
EXEC sp_rename 'imse.getUserWriteRights','getUserWri_12192000100912007',OBJECT
go
CREATE PROCEDURE getUserWriteRights AS
DECLARE @user int
DECLARE @doc int
select user_id from user_roles_crossref where user_id = @user and role_id = 0 -- Returnerar en rad om användare 1 är superadmin
select user_id from user_rights where meta_id = @doc and user_id = @user and permission_id = 99 -- Returnerar en rad om användare 1 skapade dokument 1351
select user_id from roles_rights join user_roles_crossref on roles_rights.role_id = user_roles_crossref.role_id where meta_id = @doc and user_id = @doc and permission_id = 3 -- Returnerar en rad om användare 1 är medlem i en grupp som har skrivrättigheter i dokument 1351
go
IF OBJECT_ID('imse.getUserWriteRights') IS NOT NULL
     DROP PROCEDURE imse.getUserWri_12192000100912007
ELSE 
     EXEC sp_rename 'imse.getUserWri_12192000100912007','getUserWriteRights',OBJECT
go

--
-- Procedure Recreate
-- imse.IMC_AddRole
--
EXEC sp_rename 'imse.IMC_AddRole','IMC_AddRol_12192000100912008',OBJECT
go
CREATE PROCEDURE IMC_AddRole 
/*
 Lets detect if we should add a read / or a write option
*/
 @metaId int,
 @aRole int,
 @typeOfRole int
AS
IF( @typeOfRole = 1) BEGIN
 -- Lets insert a read
 INSERT INTO roles_rights
 VALUES( @aRole ,  @metaId ,@typeOfRole )
END 
IF( @typeOfRole = 3 ) BEGIN
 -- WRITE
 INSERT INTO roles_rights
 VALUES ( @aRole , @metaId , @typeOfRole)
END
go
IF OBJECT_ID('imse.IMC_AddRole') IS NOT NULL
     DROP PROCEDURE imse.IMC_AddRol_12192000100912008
ELSE 
     EXEC sp_rename 'imse.IMC_AddRol_12192000100912008','IMC_AddRole',OBJECT
go

--
-- Procedure Recreate
-- imse.RoleCount
--
EXEC sp_rename 'imse.RoleCount','RoleCount_12192000100912010',OBJECT
go
CREATE PROCEDURE RoleCount
 @aRoleId int
AS
/*
 This function is used when an administrator tries to delete a role.
 This function counts in how many documents the role is used
*/
DECLARE @returnVal int
SELECT  @returnVal = COUNT(  r.role_id ) 
FROM ROLES_RIGHTS r
WHERE ROLE_ID = @aRoleId
-- Lets validate for null
SELECT @returnVal = ISNULL(  @returnVal , 0 )
SELECT @returnVal AS 'Number_of_roles'
go
IF OBJECT_ID('imse.RoleCount') IS NOT NULL
     DROP PROCEDURE imse.RoleCount_12192000100912010
ELSE 
     EXEC sp_rename 'imse.RoleCount_12192000100912010','RoleCount',OBJECT
go

--
-- Procedure Recreate
-- imse.RoleDelete
--
EXEC sp_rename 'imse.RoleDelete','RoleDelete_12192000100912011',OBJECT
go
CREATE PROCEDURE RoleDelete
/* Deletes an role from the role table. Used by the AdminRoles servlet
*/
 @roleId int
AS
DELETE FROM ROLES_RIGHTS WHERE ROLE_ID = @roleId
DELETE FROM user_roles_crossref WHERE ROLE_ID =@roleId
DELETE FROM ROLES WHERE ROLE_ID = @roleId
go
IF OBJECT_ID('imse.RoleDelete') IS NOT NULL
     DROP PROCEDURE imse.RoleDelete_12192000100912011
ELSE 
     EXEC sp_rename 'imse.RoleDelete_12192000100912011','RoleDelete',OBJECT
go

--
-- Procedure Recreate
-- imse.RoleDeleteViewAffectedMetaIds
--
EXEC sp_rename 'imse.RoleDeleteViewAffectedMetaIds','RoleDelete_12192000100912012',OBJECT
go
CREATE PROCEDURE RoleDeleteViewAffectedMetaIds
 @aRoleId int
AS
/*
 This function is used when an administrator tries to delete a role.
 All metaids where the role is used will be presenteted in i list
*/
SELECT  TOP 50 r.meta_id , r.meta_id
FROM ROLES_RIGHTS r
WHERE ROLE_ID = @aRoleId
-- Lets validate for null
--SELECT @returnVal = ISNULL(  @returnVal , -1 )
--SELECT @returnVal AS 'FoundRoleName'
go
IF OBJECT_ID('imse.RoleDeleteViewAffectedMetaIds') IS NOT NULL
     DROP PROCEDURE imse.RoleDelete_12192000100912012
ELSE 
     EXEC sp_rename 'imse.RoleDelete_12192000100912012','RoleDeleteViewAffectedMetaIds',OBJECT
go

--
-- Procedure Recreate
-- imse.GetCurrentSessionCounter
--
EXEC sp_rename 'imse.GetCurrentSessionCounter','GetCurrent_12192000100913001',OBJECT
go
CREATE PROCEDURE GetCurrentSessionCounter 
 
AS
 SELECT value 
 FROM sys_data
 WHERE type_id  = 1
go
IF OBJECT_ID('imse.GetCurrentSessionCounter') IS NOT NULL
     DROP PROCEDURE imse.GetCurrent_12192000100913001
ELSE 
     EXEC sp_rename 'imse.GetCurrent_12192000100913001','GetCurrentSessionCounter',OBJECT
go

--
-- Procedure Recreate
-- imse.GetCurrentSessionCounterDate
--
EXEC sp_rename 'imse.GetCurrentSessionCounterDate','GetCurrent_12192000100913002',OBJECT
go
CREATE PROCEDURE GetCurrentSessionCounterDate
 
AS
 SELECT value 
 FROM sys_data
 WHERE type_id  = 2
go
IF OBJECT_ID('imse.GetCurrentSessionCounterDate') IS NOT NULL
     DROP PROCEDURE imse.GetCurrent_12192000100913002
ELSE 
     EXEC sp_rename 'imse.GetCurrent_12192000100913002','GetCurrentSessionCounterDate',OBJECT
go

--
-- Procedure Recreate
-- imse.IncSessionCounter
--
EXEC sp_rename 'imse.IncSessionCounter','IncSession_12192000100913003',OBJECT
go
CREATE PROCEDURE IncSessionCounter 
AS
      
    DECLARE @current_value int
  select @current_value = (select value from sys_data where type_id = 1)
  set @current_value  =  @current_value +1
 update sys_data
 set value = @current_value where type_id = 1
 
  return
go
IF OBJECT_ID('imse.IncSessionCounter') IS NOT NULL
     DROP PROCEDURE imse.IncSession_12192000100913003
ELSE 
     EXEC sp_rename 'imse.IncSession_12192000100913003','IncSessionCounter',OBJECT
go

--
-- Procedure Recreate
-- imse.SetSessionCounterDate
--
EXEC sp_rename 'imse.SetSessionCounterDate','SetSession_12192000100913004',OBJECT
go
CREATE PROCEDURE SetSessionCounterDate
   @new_date varchar(20)
AS
      
 update sys_data
 set value = @new_date where type_id = 2
 
  return
go
IF OBJECT_ID('imse.SetSessionCounterDate') IS NOT NULL
     DROP PROCEDURE imse.SetSession_12192000100913004
ELSE 
     EXEC sp_rename 'imse.SetSession_12192000100913004','SetSessionCounterDate',OBJECT
go

--
-- Procedure Recreate
-- imse.SystemMessageGet
--
EXEC sp_rename 'imse.SystemMessageGet','SystemMess_12192000100913005',OBJECT
go
CREATE PROCEDURE SystemMessageGet AS
/*
 Used by the AdminSystemMessage servlet to retrieve the systemmessage
*/
SELECT s.value
FROM sys_data s
WHERE s.type_id = 3
go
IF OBJECT_ID('imse.SystemMessageGet') IS NOT NULL
     DROP PROCEDURE imse.SystemMess_12192000100913005
ELSE 
     EXEC sp_rename 'imse.SystemMess_12192000100913005','SystemMessageGet',OBJECT
go

--
-- Procedure Recreate
-- imse.SystemMessageSet
--
EXEC sp_rename 'imse.SystemMessageSet','SystemMess_12192000100913006',OBJECT
go
CREATE PROCEDURE SystemMessageSet
/*
Lets update the system message table. Used by the AdminSystemMessage servlet
*/
 @newMsg varchar(80)
AS
UPDATE sys_data
SET value = @newMsg
WHERE type_id = 3
go
IF OBJECT_ID('imse.SystemMessageSet') IS NOT NULL
     DROP PROCEDURE imse.SystemMess_12192000100913006
ELSE 
     EXEC sp_rename 'imse.SystemMess_12192000100913006','SystemMessageSet',OBJECT
go

--
-- Procedure Recreate
-- imse.AddNewuser
--
EXEC sp_rename 'imse.AddNewuser','AddNewuser_12192000100921002',OBJECT
go
CREATE PROCEDURE AddNewuser
/*
Adds a new user to the user table
usertype. 0=special, 1=default, 2=conferenceuser 
*/
 @user_id int,
 @login_name char(15),
 @login_password char(15),
 @first_name char(25),
 @last_name char(30),
 @title char(30),
 @company char(30),
 @address char(40),
 @city char(30),
 @zip char (15),
 @country char(30),
 @county_council char(30),
 @email char(50),
 @admin_mode int,
 @last_page int,
 @archive_mode int,
 @lang_id int,
 @user_type int,
 @active int
AS
INSERT INTO users (user_id,login_name,login_password,first_name,last_name, title, company, address,city,zip,country,county_council,email,admin_mode,last_page,archive_mode,lang_id, user_type, active, create_date)
VALUES (@user_id, @login_name, @login_password, @first_name, @last_name, @title, @company,  @address, @city, @zip, @country,
   @county_council, @email, @admin_mode, @last_page, @archive_mode, @lang_id ,@user_type, @active, getDate())
/*
EATE PROCEDURE AddNewuser
Adds a new user to the user table
usertype. 0=special, 1=default, 2=conferenceuser
 @user_id int,
 @login_name char(15),
 @login_password char(15),
 @first_name char(25),
 @last_name char(30),
 @address char(40),
 @city char(30),
 @zip char (15),
 @country char(30),
 @county_council char(30),
 @email char(50),
 @admin_mode int,
 @last_page int,
 @archive_mode int,
 @lang_id int,
 @user_type int,
 @active int
AS
INSERT INTO users (user_id,login_name,login_password,first_name,last_name,address,city,zip,country,county_council,email,admin_mode,last_page,archive_mode,lang_id, user_type, active, create_date)
VALUES (@user_id, @login_name, @login_password, @first_name, @last_name, @address, @city, @zip, @country,
   @county_council, @email, @admin_mode, @last_page, @archive_mode, @lang_id ,@user_type, @active, getDate())
*/
/*
CREATE PROCEDURE AddNewuser
Adds a new user to the user table
usertype. 0=special, 1=default, 2=conferenceuser 
 @user_id int,
 @login_name char(15),
 @login_password char(15),
 @first_name char(25),
 @last_name char(30),
 @address char(40),
 @city char(30),
 @zip char (15),
 @country char(30),
 @county_council char(30),
 @email char(50),
 @admin_mode int,
 @last_page int,
 @archive_mode int,
 @lang_id int,
 @user_type int,
 @active int
AS
INSERT INTO users (user_id,login_name,login_password,first_name,last_name,address,city,zip,country,county_council,email,admin_mode,last_page,archive_mode,lang_id, user_type, active, create_date)
VALUES (@user_id, @login_name, @login_password, @first_name, @last_name, @address, @city, @zip, @country,
   @county_council, @email, @admin_mode, @last_page, @archive_mode, @lang_id ,@user_type, @active, getDate())
*/
go
IF OBJECT_ID('imse.AddNewuser') IS NOT NULL
     DROP PROCEDURE imse.AddNewuser_12192000100921002
ELSE 
     EXEC sp_rename 'imse.AddNewuser_12192000100921002','AddNewuser',OBJECT
go

--
-- Procedure Recreate
-- imse.ChangeUserActiveStatus
--
EXEC sp_rename 'imse.ChangeUserActiveStatus','ChangeUser_12192000100921003',OBJECT
go
CREATE PROCEDURE ChangeUserActiveStatus @user_id int, @active int AS
/* 
 * change users activestate
*/
UPDATE users 
SET 
active = @active
WHERE user_id = @user_id
go
IF OBJECT_ID('imse.ChangeUserActiveStatus') IS NOT NULL
     DROP PROCEDURE imse.ChangeUser_12192000100921003
ELSE 
     EXEC sp_rename 'imse.ChangeUser_12192000100921003','ChangeUserActiveStatus',OBJECT
go

--
-- Procedure Recreate
-- imse.CheckAdminRights
--
EXEC sp_rename 'imse.CheckAdminRights','CheckAdmin_12192000100921004',OBJECT
go
CREATE PROCEDURE CheckAdminRights
/*
Detects if a user is administrator or not
*/
 @aUserId int
AS
SELECT users.user_id, roles.role_id
FROM users INNER JOIN
    user_roles_crossref ON 
    users.user_id = user_roles_crossref.user_id INNER JOIN
    roles ON user_roles_crossref.role_id = roles.role_id
WHERE roles.role_id = 0 AND users.user_id = @aUserId
go
IF OBJECT_ID('imse.CheckAdminRights') IS NOT NULL
     DROP PROCEDURE imse.CheckAdmin_12192000100921004
ELSE 
     EXEC sp_rename 'imse.CheckAdmin_12192000100921004','CheckAdminRights',OBJECT
go

--
-- Procedure Recreate
-- imse.DelUser
--
EXEC sp_rename 'imse.DelUser','DelUser_12192000100921005',OBJECT
go
CREATE PROCEDURE DelUser
 @aUserId int
AS
 
 DELETE
 FROM user_roles_crossref
 WHERE user_id = @aUserId
 DELETE 
 FROM users
 WHERE user_id = @aUserId
go
IF OBJECT_ID('imse.DelUser') IS NOT NULL
     DROP PROCEDURE imse.DelUser_12192000100921005
ELSE 
     EXEC sp_rename 'imse.DelUser_12192000100921005','DelUser',OBJECT
go

--
-- Procedure Recreate
-- imse.FindUserName
--
EXEC sp_rename 'imse.FindUserName','FindUserNa_12192000100921006',OBJECT
go
CREATE PROCEDURE [FindUserName] 
 @userName char(15)
AS
/*
 This function is used from the conference when  someone is logging in to the 
conference. The system searches for the username and returns the 
userId, userName and password
*/
SELECT  u.login_name
FROM users u
WHERE u.login_name = @userName
go
IF OBJECT_ID('imse.FindUserName') IS NOT NULL
     DROP PROCEDURE imse.FindUserNa_12192000100921006
ELSE 
     EXEC sp_rename 'imse.FindUserNa_12192000100921006','FindUserName',OBJECT
go

--
-- Procedure Recreate
-- imse.GetAllUsers
--
EXEC sp_rename 'imse.GetAllUsers','GetAllUser_12192000100921007',OBJECT
go
CREATE PROCEDURE [GetAllUsers] AS
  select *
 from USERS
 
 order by  last_name
go
IF OBJECT_ID('imse.GetAllUsers') IS NOT NULL
     DROP PROCEDURE imse.GetAllUser_12192000100921007
ELSE 
     EXEC sp_rename 'imse.GetAllUser_12192000100921007','GetAllUsers',OBJECT
go

--
-- Procedure Recreate
-- imse.GetAllUsersInList
--
EXEC sp_rename 'imse.GetAllUsersInList','GetAllUser_12192000100921008',OBJECT
go
CREATE PROCEDURE GetAllUsersInList AS
/*
This function is used from AdminIpAcces servlet to generate a list
*/
SELECT user_id, last_name + ', ' + first_name from users
ORDER BY last_name
go
IF OBJECT_ID('imse.GetAllUsersInList') IS NOT NULL
     DROP PROCEDURE imse.GetAllUser_12192000100921008
ELSE 
     EXEC sp_rename 'imse.GetAllUser_12192000100921008','GetAllUsersInList',OBJECT
go

--
-- Procedure Recreate
-- imse.GetCategoryUsers
--
EXEC sp_rename 'imse.GetCategoryUsers','GetCategor_12192000100921009',OBJECT
go
CREATE PROCEDURE GetCategoryUsers
/*
Used from servlet AdminUser
*/
 @category int
AS
SELECT user_id, last_name + ', ' + first_name
FROM users
WHERE user_type = @category
ORDER BY last_name
go
IF OBJECT_ID('imse.GetCategoryUsers') IS NOT NULL
     DROP PROCEDURE imse.GetCategor_12192000100921009
ELSE 
     EXEC sp_rename 'imse.GetCategor_12192000100921009','GetCategoryUsers',OBJECT
go

--
-- Procedure Recreate
-- imse.GetHighestUserId
--
EXEC sp_rename 'imse.GetHighestUserId','GetHighest_12192000100921010',OBJECT
go
CREATE PROCEDURE GetHighestUserId
AS
--DECLARE @retVal int
SELECT MAX(user_id) +1
FROM users
go
IF OBJECT_ID('imse.GetHighestUserId') IS NOT NULL
     DROP PROCEDURE imse.GetHighest_12192000100921010
ELSE 
     EXEC sp_rename 'imse.GetHighest_12192000100921010','GetHighestUserId',OBJECT
go

--
-- Procedure Recreate
-- imse.GetNewUserId
--
EXEC sp_rename 'imse.GetNewUserId','GetNewUser_12192000100921011',OBJECT
go
CREATE PROCEDURE GetNewUserId
AS
--DECLARE @retVal int
SELECT MAX(user_id) +1
FROM users
go
IF OBJECT_ID('imse.GetNewUserId') IS NOT NULL
     DROP PROCEDURE imse.GetNewUser_12192000100921011
ELSE 
     EXEC sp_rename 'imse.GetNewUser_12192000100921011','GetNewUserId',OBJECT
go

--
-- Procedure Recreate
-- imse.GetUserCreateDate
--
EXEC sp_rename 'imse.GetUserCreateDate','GetUserCre_12192000100921012',OBJECT
go
CREATE PROCEDURE GetUserCreateDate
/*
 Returns the date when the user was created in the system
Used by servlet AdminUserProps
*/
 @userId int
AS
DECLARE @retVal smalldatetime
SELECT @retVal = create_date
FROM users
WHERE users.user_id = @userId
-- Lets validate for null
-- SELECT @retVal = ISNULL(  @retVal , '' )
---SELECT @retVal AS 'TemplateId'
SELECT @retVal  AS 'Usercreatedate'
go
IF OBJECT_ID('imse.GetUserCreateDate') IS NOT NULL
     DROP PROCEDURE imse.GetUserCre_12192000100921012
ELSE 
     EXEC sp_rename 'imse.GetUserCre_12192000100921012','GetUserCreateDate',OBJECT
go

--
-- Procedure Recreate
-- imse.GetUserId
--
EXEC sp_rename 'imse.GetUserId','GetUserId_12192000100921013',OBJECT
go
CREATE PROCEDURE GetUserId 
 @aUserId int
AS
 SELECT user_id 
 FROM users
 WHERE user_id  = @aUserId
go
IF OBJECT_ID('imse.GetUserId') IS NOT NULL
     DROP PROCEDURE imse.GetUserId_12192000100921013
ELSE 
     EXEC sp_rename 'imse.GetUserId_12192000100921013','GetUserId',OBJECT
go

--
-- Procedure Recreate
-- imse.GetUserIdFromName
--
EXEC sp_rename 'imse.GetUserIdFromName','GetUserIdF_12192000100921014',OBJECT
go
CREATE PROCEDURE [GetUserIdFromName] 
/*
Used by the conferences loginfunction, to detect a users userid from
the username
*/
 @userName char(15),
 @userPwd char(15)
AS
SELECT  u.user_id 
FROM users u
WHERE u.login_name = @userName
AND u.login_password = @userPwd
go
IF OBJECT_ID('imse.GetUserIdFromName') IS NOT NULL
     DROP PROCEDURE imse.GetUserIdF_12192000100921014
ELSE 
     EXEC sp_rename 'imse.GetUserIdF_12192000100921014','GetUserIdFromName',OBJECT
go

--
-- Procedure Recreate
-- imse.GetUserInfo
--
EXEC sp_rename 'imse.GetUserInfo','GetUserInf_12192000100921015',OBJECT
go
CREATE PROCEDURE GetUserInfo
/* Returns all the information about a user. Used by adminsystem & conference system
*/
 @aUserId int
AS
 SELECT * 
 FROM users
 WHERE user_id = @aUserId
go
IF OBJECT_ID('imse.GetUserInfo') IS NOT NULL
     DROP PROCEDURE imse.GetUserInf_12192000100921015
ELSE 
     EXEC sp_rename 'imse.GetUserInf_12192000100921015','GetUserInfo',OBJECT
go

--
-- Procedure Recreate
-- imse.GetUserNames
--
EXEC sp_rename 'imse.GetUserNames','GetUserNam_12192000100921016',OBJECT
go
CREATE PROCEDURE GetUserNames
/* 
This procedure is used to retrieve a users full name (first name + last name
concateneted.
*/
 @user_id int,
 @what int
AS
 DECLARE @returnVal char(25)
IF(@what = 1) BEGIN
 SELECT @returnVal = RTRIM(first_name) 
 FROM users
 WHERE users.user_id = @user_id 
END ELSE BEGIN  
 SELECT @returnVal =  RTRIM(last_name) 
 FROM users
 WHERE users.user_id = @user_id 
END
SELECT @returnVal =  ISNULL(@returnVal, -1) 
SELECT @returnVal AS 'UserName'
go
IF OBJECT_ID('imse.GetUserNames') IS NOT NULL
     DROP PROCEDURE imse.GetUserNam_12192000100921016
ELSE 
     EXEC sp_rename 'imse.GetUserNam_12192000100921016','GetUserNames',OBJECT
go

--
-- Procedure Recreate
-- imse.GetUserPassword
--
EXEC sp_rename 'imse.GetUserPassword','GetUserPas_12192000100921017',OBJECT
go
CREATE PROCEDURE GetUserPassword 
/* Used by AdminUserProps servlet to retrieve the users password 
*/
 @user_id int
AS
DECLARE @retVal char(15)
SELECT @retVal  = login_password 
FROM USERS
WHERE user_id = @user_id
SELECT @retVal =  ISNULL(@retVal , '') 
SELECT @retVal AS 'Password'
go
IF OBJECT_ID('imse.GetUserPassword') IS NOT NULL
     DROP PROCEDURE imse.GetUserPas_12192000100921017
ELSE 
     EXEC sp_rename 'imse.GetUserPas_12192000100921017','GetUserPassword',OBJECT
go

--
-- Procedure Recreate
-- imse.GetUserPhoneNumbers
--
EXEC sp_rename 'imse.GetUserPhoneNumbers','GetUserPho_12192000100921018',OBJECT
go
CREATE PROCEDURE GetUserPhoneNumbers
/*
Returns a users phonenumbers. Used by AdminUserProps servlet
*/
 @user_id int
AS
-- The new version which includes phones
SELECT p.phone_id, p.country_code , p.area_code , p.number, p.user_id 
FROM users u , phones p 
WHERE u.user_id = p.user_id
AND u.user_id = @user_id
go
IF OBJECT_ID('imse.GetUserPhoneNumbers') IS NOT NULL
     DROP PROCEDURE imse.GetUserPho_12192000100921018
ELSE 
     EXEC sp_rename 'imse.GetUserPho_12192000100921018','GetUserPhoneNumbers',OBJECT
go

--
-- Procedure Recreate
-- imse.GetUserPhones
--
EXEC sp_rename 'imse.GetUserPhones','GetUserPho_12192000100921019',OBJECT
go
CREATE PROCEDURE GetUserPhones
 @user_id int
AS
/*
Used to generate a list with all type of users. Used from AdminUserProps
*/
SELECT p.phone_id, RTRIM(p.country_code) + ' ' + RTRIM(p.area_code) + ' ' + RTRIM(p.number) as numbers
FROM users u , phones p 
WHERE u.user_id = p.user_id
AND u.user_id = @user_id
go
IF OBJECT_ID('imse.GetUserPhones') IS NOT NULL
     DROP PROCEDURE imse.GetUserPho_12192000100921019
ELSE 
     EXEC sp_rename 'imse.GetUserPho_12192000100921019','GetUserPhones',OBJECT
go

--
-- Procedure Recreate
-- imse.GetUsersWhoBelongsToRole
--
EXEC sp_rename 'imse.GetUsersWhoBelongsToRole','GetUsersWh_12192000100921020',OBJECT
go
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
go
IF OBJECT_ID('imse.GetUsersWhoBelongsToRole') IS NOT NULL
     DROP PROCEDURE imse.GetUsersWh_12192000100921020
ELSE 
     EXEC sp_rename 'imse.GetUsersWh_12192000100921020','GetUsersWhoBelongsToRole',OBJECT
go

--
-- Procedure Recreate
-- imse.GetUserType
--
EXEC sp_rename 'imse.GetUserType','GetUserTyp_12192000100921021',OBJECT
go
CREATE PROCEDURE GetUserType
/*
Used to get a users usertype. used from adminuser
*/
 @User_id int
 AS
DECLARE @returnVal int
SELECT DISTINCT @returnVal =  user_type  
FROM users
WHERE user_id = @User_id
SELECT @returnVal =  ISNULL(@returnVal, 1) 
SELECT @returnVal AS 'Usertype'
go
IF OBJECT_ID('imse.GetUserType') IS NOT NULL
     DROP PROCEDURE imse.GetUserTyp_12192000100921021
ELSE 
     EXEC sp_rename 'imse.GetUserTyp_12192000100921021','GetUserType',OBJECT
go

--
-- Procedure Recreate
-- imse.PermissionsGetPermission
--
EXEC sp_rename 'imse.PermissionsGetPermission','Permission_12192000100921023',OBJECT
go
CREATE PROCEDURE PermissionsGetPermission @login_name varchar(15), @permission int AS
/*
*/
select login_password, first_name, last_name, email, min(permissions&@permission), lang_prefix 
from users u 
join lang_prefixes lp 
    on u.lang_id = lp.lang_id 
join user_roles_crossref urc 
    on u.user_id = urc.user_id left 
join roles r 
    on r.role_id = urc.role_id
where login_name = @login_name
group by login_password, first_name, last_name, email, lang_prefix
go
IF OBJECT_ID('imse.PermissionsGetPermission') IS NOT NULL
     DROP PROCEDURE imse.Permission_12192000100921023
ELSE 
     EXEC sp_rename 'imse.Permission_12192000100921023','PermissionsGetPermission',OBJECT
go

--
-- Procedure Recreate
-- imse.RoleCountAffectedUsers
--
EXEC sp_rename 'imse.RoleCountAffectedUsers','RoleCountA_12192000100921024',OBJECT
go
CREATE PROCEDURE RoleCountAffectedUsers
 @aRoleId int
AS
/*
 This function is used when an administrator tries to delete a role.
 This function counts how many users who will be affected
*/
SELECT  DISTINCT COUNT(usr.role_id )
FROM user_roles_crossref usr, roles r, users u
WHERE usr.ROLE_ID = @aRoleId 
AND usr.user_id = u.user_id
go
IF OBJECT_ID('imse.RoleCountAffectedUsers') IS NOT NULL
     DROP PROCEDURE imse.RoleCountA_12192000100921024
ELSE 
     EXEC sp_rename 'imse.RoleCountA_12192000100921024','RoleCountAffectedUsers',OBJECT
go

--
-- Procedure Recreate
-- imse.RoleDeleteViewAffectedUsers
--
EXEC sp_rename 'imse.RoleDeleteViewAffectedUsers','RoleDelete_12192000100921025',OBJECT
go
CREATE PROCEDURE RoleDeleteViewAffectedUsers
 @aRoleId int
AS
/*
 This function is used when an administrator tries to delete a role.
 All users which will be affected of the deletion will be presenteted in a list
*/
SELECT distinct TOP 50  usr.role_id , (RTRIM(last_name) + ', ' + RTRIM(first_name))
FROM user_roles_crossref usr, roles r, users u
WHERE usr.ROLE_ID = @aRoleId 
AND usr.user_id = u.user_id
--GROUP BY (RTRIM(last_name) + ', ' + RTRIM(first_name)), usr.role_id
ORDER BY (RTRIM(last_name) + ', ' + RTRIM(first_name))
go
IF OBJECT_ID('imse.RoleDeleteViewAffectedUsers') IS NOT NULL
     DROP PROCEDURE imse.RoleDelete_12192000100921025
ELSE 
     EXEC sp_rename 'imse.RoleDelete_12192000100921025','RoleDeleteViewAffectedUsers',OBJECT
go

--
-- Procedure Recreate
-- imse.test
--
EXEC sp_rename 'imse.test','test_12192000100921026',OBJECT
go
CREATE PROCEDURE test AS
SELECT COUNT(usr.role_id) , (RTRIM(last_name) + ', ' + RTRIM(first_name))   
FROM user_roles_crossref usr, roles r, users u
WHERE usr.ROLE_ID = 5 
AND usr.user_id = u.user_id
GROUP BY (RTRIM(last_name) + ', ' + RTRIM(first_name)), usr.role_id
go
IF OBJECT_ID('imse.test') IS NOT NULL
     DROP PROCEDURE imse.test_12192000100921026
ELSE 
     EXEC sp_rename 'imse.test_12192000100921026','test',OBJECT
go

--
-- Procedure Recreate
-- imse.UpdateUser
--
EXEC sp_rename 'imse.UpdateUser','UpdateUser_12192000100921027',OBJECT
go
CREATE PROCEDURE imse.UpdateUser
/*
usertype. 0=special, 1=default, 2=conferenceuser 
*/
 @user_id int,
 @login_name char(15),
 @login_password char(15),
 @first_name char(25),
 @last_name char(30),
 @title char(30),
 @company char(30),
 @address char(40),
 @city char(30),
 @zip char (15),
 @country char(30),
 @county_council char(30),
 @email char(50),
 @admin_mode int,
 @last_page int,
 @archive_mode int,
 @lang_id int,
 @user_type int,
 @active int
AS
UPDATE users 
SET login_name = @login_name,
login_password = @login_password,
first_name = @first_name,
last_name = @last_name,
title = @title,
company = @company,
address =  @address,
city = @city,
zip = @zip,
country = @country,
county_council = @county_council,
email = @email,
user_type = @user_type,
active = @active,
lang_id = @lang_id
WHERE user_id = @User_id 
/****** Object:  Stored Procedure dbo.UpdateUser    Script Date: 2000-10-27 15:19:01 *****
CREATE PROCEDURE UpdateUser
--usertype. 0=special, 1=default, 2=conferenceuser 
 @user_id int,
 @login_name char(15),
 @login_password char(15),
 @first_name char(25),
 @last_name char(30),
 @title char(30),
 @company char(30),
 @address char(40),
 @city char(30),
 @zip char (15),
 @country char(30),
 @county_council char(30),
 @email char(50),
 @admin_mode int,
 @last_page int,
 @archive_mode int,
 @lang_id int,
 @user_type int,
 @active int
AS
UPDATE users 
SET login_name = @login_name,
login_password = @login_password,
first_name = @first_name,
last_name = @last_name,
title = @title,
company = @company,
address =  @address,
city = @city,
zip = @zip,
country = @country,
county_council = @county_council,
email = @email,
user_type = @user_type,
active = @active
WHERE user_id = @User_id 
*/
go
IF OBJECT_ID('imse.UpdateUser') IS NOT NULL
     DROP PROCEDURE imse.UpdateUser_12192000100921027
ELSE 
     EXEC sp_rename 'imse.UpdateUser_12192000100921027','UpdateUser',OBJECT
go

--
-- Procedure Recreate
-- imse.UserPrefsChange
--
EXEC sp_rename 'imse.UserPrefsChange','UserPrefsC_12192000100921028',OBJECT
go
CREATE PROCEDURE imse.UserPrefsChange
  @aUserId int
/*
  Returns the information for a user which he is able to change self. Observer that we
  return the password as an empty string
*/
AS
-- SELECT @aUserId AS 'TEST'
SELECT user_id, login_name,  "", "", first_name, last_name,  title, company, address, city, zip, country, county_council, email --, profession, company
FROM users
WHERE user_id = @aUserId 
/****** Object:  Stored Procedure dbo.UserPrefsChange    Script Date: 2000-10-27 15:19:01 *****
CREATE PROCEDURE imse.UserPrefsChange
  @aUserId int
--  Returns the information for a user which he is able to change self. Observer that we
--  return the password as an empty string
AS
-- SELECT @aUserId AS 'TEST'
SELECT user_id, login_name,  "", "", first_name, last_name,  title, company, address, city, zip, country, county_council, email --, profession, company
FROM users
WHERE user_id = @aUserId 
*/
go
IF OBJECT_ID('imse.UserPrefsChange') IS NOT NULL
     DROP PROCEDURE imse.UserPrefsC_12192000100921028
ELSE 
     EXEC sp_rename 'imse.UserPrefsC_12192000100921028','UserPrefsChange',OBJECT
go

ALTER TABLE dbo.doc_permission_sets 
    ADD CONSTRAINT FK_doc_permission_sets_meta
FOREIGN KEY (meta_id)
REFERENCES dbo.meta (meta_id)
go
ALTER TABLE dbo.doc_permission_sets_ex 
    ADD CONSTRAINT FK_doc_permission_sets_ex_meta
FOREIGN KEY (meta_id)
REFERENCES dbo.meta (meta_id)
go

--
-- Index Create
-- dbo.browsers.IX_browsers
--
CREATE NONCLUSTERED INDEX IX_browsers
    ON dbo.browsers(value)
    ON [PRIMARY]
go

--
-- Index Create
-- dbo.childs.childs_meta_id
--
CREATE CLUSTERED INDEX childs_meta_id
    ON dbo.childs(meta_id)
    ON [PRIMARY]
go

--
-- Index Create
-- dbo.doc_permission_sets.IX_doc_permission_sets
--
CREATE CLUSTERED INDEX IX_doc_permission_sets
    ON dbo.doc_permission_sets(meta_id,set_id)
    ON [PRIMARY]
go

--
-- Index Create
-- dbo.doc_permissions.IX_doc_permissions
--
CREATE CLUSTERED INDEX IX_doc_permissions
    ON dbo.doc_permissions(permission_id)
    ON [PRIMARY]
go

--
-- Index Create
-- dbo.doc_types.IX_doc_types
--
CREATE CLUSTERED INDEX IX_doc_types
    ON dbo.doc_types(lang_prefix,doc_type)
    ON [PRIMARY]
go

--
-- Index Create
-- dbo.fileupload_docs.fileupload_docs_meta_id
--
CREATE CLUSTERED INDEX fileupload_docs_meta_id
    ON dbo.fileupload_docs(meta_id)
    ON [PRIMARY]
go

--
-- Index Create
-- dbo.meta.meta_meta_id
--
CREATE CLUSTERED INDEX meta_meta_id
    ON dbo.meta(meta_id,show_meta,activate)
    ON [PRIMARY]
go

--
-- Index Create
-- dbo.roles_rights.roles_rights_meta_id
--
CREATE CLUSTERED INDEX roles_rights_meta_id
    ON dbo.roles_rights(meta_id,role_id,set_id)
    ON [PRIMARY]
go

--
-- Index Create
-- dbo.roles_rights.roles_rights_role_id
--
CREATE NONCLUSTERED INDEX roles_rights_role_id
    ON dbo.roles_rights(role_id)
    ON [PRIMARY]
go

--
-- Index Create
-- dbo.templategroups.IX_tg
--
CREATE UNIQUE CLUSTERED INDEX IX_tg
    ON dbo.templategroups(group_id)
    ON [PRIMARY]
go

--
-- Procedure Create
-- imse.AddStatistics
--
--
-- Procedure Create
-- dbo.AddStatistics
--
CREATE PROCEDURE AddStatistics @name VARCHAR(120) AS

UPDATE	stats
SET		num = num + 1
WHERE	name = @name

IF @@ROWCOUNT = 0
BEGIN
INSERT	stats
VALUES	(	@name,
			1
		)
END
go

--
-- Procedure Extended Alter
-- imse.AddUserRole
--
EXEC sp_rename 'imse.AddUserRole','AddUserRol_12192000100927000',OBJECT
go
--
-- Procedure Create
-- dbo.AddUserRole
--
CREATE PROCEDURE AddUserRole
/* Adds a role to a particular user
*/
 @aUser_id int,
 @aRole_id int
AS
 INSERT INTO  user_roles_crossref(user_id, role_id)
 VALUES( @aUser_id , @aRole_id)
go
IF OBJECT_ID('imse.AddUserRole') IS NOT NULL
     DROP PROCEDURE imse.AddUserRol_12192000100927000
ELSE 
     EXEC sp_rename 'imse.AddUserRol_12192000100927000','AddUserRole',OBJECT
go

--
-- Procedure Extended Alter
-- imse.ChangeUserActiveStatus
--
EXEC sp_rename 'imse.ChangeUserActiveStatus','ChangeUser_12192000100927000',OBJECT
go
/****** Object:  Stored Procedure dbo.ChangeUserActiveStatus    Script Date: 2000-10-27 14:21:06 ******/
CREATE PROCEDURE ChangeUserActiveStatus @user_id int, @active int AS


/* 
 * change users activestate
*/
UPDATE users 
SET 
active = @active

WHERE user_id = @user_id
go
IF OBJECT_ID('imse.ChangeUserActiveStatus') IS NOT NULL
     DROP PROCEDURE imse.ChangeUser_12192000100927000
ELSE 
     EXEC sp_rename 'imse.ChangeUser_12192000100927000','ChangeUserActiveStatus',OBJECT
go

--
-- Procedure Extended Alter
-- imse.CheckAdminRights
--
EXEC sp_rename 'imse.CheckAdminRights','CheckAdmin_12192000100927000',OBJECT
go
--
-- Procedure Create
-- dbo.CheckAdminRights
--
CREATE PROCEDURE CheckAdminRights
/*
Detects if a user is administrator or not
*/
 @aUserId int
AS
SELECT users.user_id, roles.role_id
FROM users INNER JOIN
    user_roles_crossref ON 
    users.user_id = user_roles_crossref.user_id INNER JOIN
    roles ON user_roles_crossref.role_id = roles.role_id
WHERE roles.role_id = 0 AND users.user_id = @aUserId
go
IF OBJECT_ID('imse.CheckAdminRights') IS NOT NULL
     DROP PROCEDURE imse.CheckAdmin_12192000100927000
ELSE 
     EXEC sp_rename 'imse.CheckAdmin_12192000100927000','CheckAdminRights',OBJECT
go

--
-- Procedure Extended Alter
-- imse.CheckExistsInMenu
--
EXEC sp_rename 'imse.CheckExistsInMenu','CheckExist_12192000100928000',OBJECT
go
--
-- Procedure Create
-- dbo.CheckExistsInMenu
--
CREATE PROCEDURE CheckExistsInMenu

/*
This function is used by servlet ConfAdd to check if the meta_id argument
already exists in the database. Thas because a db can be used from
different servers, and a meta_id can be used twice to be added in the 
database
*/
	@aMetaId int
 AS

DECLARE @returnVal int

SELECT @returnVal = meta_id
FROM childs
WHERE to_meta_id = @aMetaId

SELECT @returnVal = ISNULL(@returnVal, 0) 
SELECT @returnVal AS 'ExistsInMenu'
go
IF OBJECT_ID('imse.CheckExistsInMenu') IS NOT NULL
     DROP PROCEDURE imse.CheckExist_12192000100928000
ELSE 
     EXEC sp_rename 'imse.CheckExist_12192000100928000','CheckExistsInMenu',OBJECT
go

--
-- Procedure Extended Alter
-- imse.ClassificationAdd
--
EXEC sp_rename 'imse.ClassificationAdd','Classifica_12192000100928000',OBJECT
go
--
-- Procedure Create
-- dbo.ClassificationAdd
--
CREATE PROCEDURE ClassificationAdd 

	@theMetaId int,
	 @theClassCode varchar(200)
AS
/*
Adds a classification code and fix the crossreference. If a code already exists in the table, it will link to that 
code 
*/


-- Lets check if a code already exists, if so just link to that code
DECLARE @foundCode int
SELECT @foundCode = 0

-- Lets start with to find the id for the classification code
SELECT @foundCode = class_id
FROM classification
WHERE code LIKE @theClassCode

-- Lets check if the lassification code exists or if we should create it 
-- IF ( @foundCode <> 0 ) BEGIN	
	-- PRINT 'Koden fanns redan'
--END ELSE BEGIN 
-- Ok, Lets link to that code
IF ( @foundCode = 0 ) BEGIN	
	--PRINT 'Koden fanns inte'
	-- Lets start to add the classification
	INSERT INTO classification (  Code)
	VALUES (  @theClassCode )
	SELECT @foundCode = @@identity
END 

-- Lets insert the new crossreferences
INSERT INTO meta_classification (meta_id,class_id)
VALUES (  @theMetaId , @foundCode )
go
IF OBJECT_ID('imse.ClassificationAdd') IS NOT NULL
     DROP PROCEDURE imse.Classifica_12192000100928000
ELSE 
     EXEC sp_rename 'imse.Classifica_12192000100928000','ClassificationAdd',OBJECT
go

--
-- Procedure Extended Alter
-- imse.DelUser
--
EXEC sp_rename 'imse.DelUser','DelUser_12192000100928000',OBJECT
go
--
-- Procedure Create
-- dbo.DelUser
--
CREATE PROCEDURE DelUser
 @aUserId int
AS
 
 DELETE
 FROM user_roles_crossref
 WHERE user_id = @aUserId
 DELETE 
 FROM users
 WHERE user_id = @aUserId
go
IF OBJECT_ID('imse.DelUser') IS NOT NULL
     DROP PROCEDURE imse.DelUser_12192000100928000
ELSE 
     EXEC sp_rename 'imse.DelUser_12192000100928000','DelUser',OBJECT
go

--
-- Procedure Extended Alter
-- imse.DelUserRoles
--
EXEC sp_rename 'imse.DelUserRoles','DelUserRol_12192000100929000',OBJECT
go
--
-- Procedure Create
-- dbo.DelUserRoles
--
CREATE PROCEDURE DelUserRoles
 @aUserId int
AS
 DELETE 
 FROM user_roles_crossref
 WHERE user_id = @aUserId
go
IF OBJECT_ID('imse.DelUserRoles') IS NOT NULL
     DROP PROCEDURE imse.DelUserRol_12192000100929000
ELSE 
     EXEC sp_rename 'imse.DelUserRol_12192000100929000','DelUserRoles',OBJECT
go

--
-- Procedure Create
-- imse.DeleteDocPermissionSetEx
--
--
-- Procedure Create
-- dbo.DeleteDocPermissionSetEx
--
CREATE PROCEDURE DeleteDocPermissionSetEx @meta_id INT, @set_id INT AS

/*
	Delete extended permissions for a permissionset for a document
*/

DELETE FROM		doc_permission_sets_ex
WHERE		meta_id = @meta_id
		AND	set_id = @set_id
go

--
-- Procedure Create
-- imse.DeleteNewDocPermissionSetEx
--
--
-- Procedure Create
-- dbo.DeleteNewDocPermissionSetEx
--
CREATE PROCEDURE DeleteNewDocPermissionSetEx @meta_id INT, @set_id INT AS

/*
	Delete extended permissions for a permissionset for a document
*/

DELETE FROM		new_doc_permission_sets_ex
WHERE		meta_id = @meta_id
		AND	set_id = @set_id
go

--
-- Procedure Extended Alter
-- imse.FindUserName
--
EXEC sp_rename 'imse.FindUserName','FindUserNa_12192000100929000',OBJECT
go
--
-- Procedure Create
-- dbo.FindUserName
--
CREATE PROCEDURE [FindUserName] 
	@userName char(15)

AS
/*
 This function is used from the conference when  someone is logging in to the 
conference. The system searches for the username and returns the 
userId, userName and password
*/
SELECT  u.login_name
FROM users u
WHERE u.login_name = @userName
go
IF OBJECT_ID('imse.FindUserName') IS NOT NULL
     DROP PROCEDURE imse.FindUserNa_12192000100929000
ELSE 
     EXEC sp_rename 'imse.FindUserNa_12192000100929000','FindUserName',OBJECT
go

--
-- Procedure Extended Alter
-- imse.GetAllRoles
--
EXEC sp_rename 'imse.GetAllRoles','GetAllRole_12192000100929000',OBJECT
go
--
-- Procedure Create
-- dbo.GetAllRoles
--
CREATE PROCEDURE GetAllRoles AS
SELECT role_id, role_name
FROM roles
 
ORDER BY role_name
go
IF OBJECT_ID('imse.GetAllRoles') IS NOT NULL
     DROP PROCEDURE imse.GetAllRole_12192000100929000
ELSE 
     EXEC sp_rename 'imse.GetAllRole_12192000100929000','GetAllRoles',OBJECT
go

--
-- Procedure Extended Alter
-- imse.GetAllUsers
--
EXEC sp_rename 'imse.GetAllUsers','GetAllUser_12192000100930000',OBJECT
go
--
-- Procedure Create
-- dbo.GetAllUsers
--
CREATE PROCEDURE [GetAllUsers] AS
  select *
 from USERS
 
 order by  last_name
go
IF OBJECT_ID('imse.GetAllUsers') IS NOT NULL
     DROP PROCEDURE imse.GetAllUser_12192000100930000
ELSE 
     EXEC sp_rename 'imse.GetAllUser_12192000100930000','GetAllUsers',OBJECT
go

--
-- Procedure Extended Alter
-- imse.GetAllUsersInList
--
EXEC sp_rename 'imse.GetAllUsersInList','GetAllUser_12192000100930000',OBJECT
go
--
-- Procedure Create
-- dbo.GetAllUsersInList
--
CREATE PROCEDURE GetAllUsersInList AS
/*
This function is used from AdminIpAcces servlet to generate a list
*/

SELECT user_id, last_name + ', ' + first_name from users
ORDER BY last_name
go
IF OBJECT_ID('imse.GetAllUsersInList') IS NOT NULL
     DROP PROCEDURE imse.GetAllUser_12192000100930000
ELSE 
     EXEC sp_rename 'imse.GetAllUser_12192000100930000','GetAllUsersInList',OBJECT
go

--
-- Procedure Extended Alter
-- imse.GetCategoryUsers
--
EXEC sp_rename 'imse.GetCategoryUsers','GetCategor_12192000100930000',OBJECT
go
--
-- Procedure Create
-- dbo.GetCategoryUsers
--
CREATE PROCEDURE GetCategoryUsers
/*
Used from servlet AdminUser
*/
	@category int

AS
SELECT user_id, last_name + ', ' + first_name
FROM users
WHERE user_type = @category
ORDER BY last_name
go
IF OBJECT_ID('imse.GetCategoryUsers') IS NOT NULL
     DROP PROCEDURE imse.GetCategor_12192000100930000
ELSE 
     EXEC sp_rename 'imse.GetCategor_12192000100930000','GetCategoryUsers',OBJECT
go

--
-- Procedure Extended Alter
-- imse.GetChilds
--
EXEC sp_rename 'imse.GetChilds','GetChilds_12192000100930000',OBJECT
go
--
-- Procedure Create
-- dbo.GetChilds
--
CREATE PROCEDURE GetChilds
	@meta_id int,
	@user_id int
AS
/*
Nice little query that lists the children of a document that a particular user may see, and includes a field that tells you wether he may do something to it or not.
*/
declare @sort_by int

select @sort_by = sort_order from text_docs where meta_id = @meta_id

-- Manual sort order
if @sort_by = 2
begin
select  to_meta_id, c.menu_sort,manual_sort_order, doc_type,
		archive,target, left(convert (varchar,date_created,120),10),
		meta_headline,meta_text,meta_image,frame_name,
		activated_date+activated_time,archived_date+archived_time,
--		min(urc.role_id * ISNULL(~CAST(dps.permission_id AS BIT),1) * ISNULL(rr.set_id,1)),
		min(urc.role_id * ISNULL(dps.permission_id&~1,1) * ISNULL(rr.set_id,1)),
		fd.filename
from   childs c
join   meta m    
					on   	m.meta_id = c.to_meta_id					-- meta.meta_id corresponds to childs.to_meta_id
					and 	m.activate > 0							-- Only include the documents that are active in the meta table.
					and 	c.meta_id = @meta_id						-- Only include documents that are children to this particular meta_id
left join roles_rights rr												-- We may have permission, even though we don't have anything in role-permissions... That is, if we're docowner or superadmin.
					on 	c.to_meta_id = rr.meta_id						-- Only include rows with the documents we are interested in
left join doc_permission_sets dps											-- Include the permission_sets
					on 	c.to_meta_id = dps.meta_id					-- for each document
					and	dps.set_id = rr.set_id						-- and only the sets for the roles we are interested in
					and	dps.permission_id > 0						-- and only the sets that have any permission
join user_roles_crossref urc											-- This table tells us which users have which roles
					on	urc.user_id = @user_id						-- Only include the rows with the user we are interested in...
					and	( 
							rr.role_id = urc.role_id					-- Include rows where the users roles match the roles that have permissions on the documents
						or 	urc.role_id = 0						-- and also include the rows that tells us this user is a superadmin
						or 	(
								m.show_meta != 0				-- and also include documents that are to be shown regardless of rights. (Visa även för obehöriga)
							and	ISNULL(dps.permission_id&~1,1) != 1
						)
					)
left join fileupload_docs fd
					on  fd.meta_id = c.to_meta_id
group by to_meta_id, c.menu_sort,manual_sort_order, doc_type,
		archive,target, left(convert (varchar,date_created,120),10),
		meta_headline,meta_text,meta_image,frame_name,
		activated_date+activated_time,archived_date+archived_time,
		fd.filename
order by  menu_sort,c.manual_sort_order desc
end
else if @sort_by = 3
begin
select  to_meta_id, c.menu_sort,manual_sort_order, doc_type,
		archive,target, left(convert (varchar,date_created,120),10),
		meta_headline,meta_text,meta_image,frame_name,
		activated_date+activated_time,archived_date+archived_time,
		min(urc.role_id * ISNULL(dps.permission_id&~1,1) * ISNULL(rr.set_id,1)),
		fd.filename
from   childs c
join   meta m    
					on   	m.meta_id = c.to_meta_id					-- meta.meta_id corresponds to childs.to_meta_id
					and 	m.activate > 0							-- Only include the documents that are active in the meta table.
					and 	c.meta_id = @meta_id						-- Only include documents that are children to this particular meta_id
left join roles_rights rr												-- We may have permission, even though we don't have anything in role-permissions... That is, if we're docowner or superadmin.
					on 	c.to_meta_id = rr.meta_id						-- Only include rows with the documents we are interested in
left join doc_permission_sets dps											-- Include the permission_sets
					on 	c.to_meta_id = dps.meta_id					-- for each document
					and	dps.set_id = rr.set_id						-- and only the sets for the roles we are interested in
					and	dps.permission_id > 0						-- and only the sets that have any permission
join user_roles_crossref urc											-- This table tells us which users have which roles
					on	urc.user_id = @user_id						-- Only include the rows with the user we are interested in...
					and	( 
							rr.role_id = urc.role_id					-- Include rows where the users roles match the roles that have permissions on the documents
						or 	urc.role_id = 0						-- and also include the rows that tells us this user is a superadmin
						or 	(
								m.show_meta != 0				-- and also include documents that are to be shown regardless of rights. (Visa även för obehöriga)
							and	ISNULL(dps.permission_id&~1,1) != 1
						)
					)
left join fileupload_docs fd
					on  fd.meta_id = c.to_meta_id
group by to_meta_id, c.menu_sort,manual_sort_order, doc_type,
		archive,target, left(convert (varchar,date_created,120),10),
		meta_headline,meta_text,meta_image,frame_name,
		activated_date+activated_time,archived_date+archived_time,
		fd.filename
order by		menu_sort,left(convert (varchar,date_created,120),10) desc
end
else if @sort_by = 1
begin
select  to_meta_id, c.menu_sort,manual_sort_order, doc_type,
		archive,target, left(convert (varchar,date_created,120),10),
		meta_headline,meta_text,meta_image,frame_name,
		activated_date+activated_time,archived_date+archived_time,
		min(urc.role_id * ISNULL(dps.permission_id&~1,1) * ISNULL(rr.set_id,1)),
		fd.filename
from   childs c
join   meta m    
					on   	m.meta_id = c.to_meta_id					-- meta.meta_id corresponds to childs.to_meta_id
					and 	m.activate > 0							-- Only include the documents that are active in the meta table.
					and 	c.meta_id = @meta_id						-- Only include documents that are children to this particular meta_id
left join roles_rights rr												-- We may have permission, even though we don't have anything in role-permissions... That is, if we're docowner or superadmin.
					on 	c.to_meta_id = rr.meta_id						-- Only include rows with the documents we are interested in
left join doc_permission_sets dps											-- Include the permission_sets
					on 	c.to_meta_id = dps.meta_id					-- for each document
					and	dps.set_id = rr.set_id						-- and only the sets for the roles we are interested in
					and	dps.permission_id > 0						-- and only the sets that have any permission
join user_roles_crossref urc											-- This table tells us which users have which roles
					on	urc.user_id = @user_id						-- Only include the rows with the user we are interested in...
					and	( 
							rr.role_id = urc.role_id					-- Include rows where the users roles match the roles that have permissions on the documents
						or 	urc.role_id = 0						-- and also include the rows that tells us this user is a superadmin
						or 	(
								m.show_meta != 0				-- and also include documents that are to be shown regardless of rights. (Visa även för obehöriga)
							and	ISNULL(dps.permission_id&~1,1) != 1
						)
					)
left join fileupload_docs fd
					on  fd.meta_id = c.to_meta_id
group by to_meta_id, c.menu_sort,manual_sort_order, doc_type,
		archive,target, left(convert (varchar,date_created,120),10),
		meta_headline,meta_text,meta_image,frame_name,
		activated_date+activated_time,archived_date+archived_time,
		fd.filename
order by		menu_sort,meta_headline
end
go
IF OBJECT_ID('imse.GetChilds') IS NOT NULL
     DROP PROCEDURE imse.GetChilds_12192000100930000
ELSE 
     EXEC sp_rename 'imse.GetChilds_12192000100930000','GetChilds',OBJECT
go

--
-- Procedure Create
-- imse.GetDocTypes
--
--
-- Procedure Create
-- dbo.GetDocTypes
--
CREATE PROCEDURE GetDocTypes @lang_prefix VARCHAR(3) AS

SELECT doc_type,type FROM doc_types
WHERE lang_prefix = @lang_prefix
ORDER BY doc_type
go

--
-- Procedure Create
-- imse.GetDocTypesForUser
--
--
-- Procedure Create
-- dbo.GetDocTypesForUser
--
CREATE PROCEDURE GetDocTypesForUser @meta_id INT,@user_id INT, @lang_prefix VARCHAR(3) AS

/*
	Nice query that fetches all document types a user may create in a document,
	for easy insertion into an html-option-list, no less!
*/
SELECT	DISTINCT dt.doc_type, dt.type
FROM 		doc_types dt
JOIN		user_roles_crossref urc
							ON	urc.user_id = @user_id
							AND	dt.lang_prefix = @lang_prefix
LEFT JOIN	roles_rights rr
							ON	rr.meta_id = @meta_id
							AND	rr.role_id = urc.role_id
LEFT JOIN	doc_permission_sets dps
							ON	dps.meta_id = rr.meta_id
							AND	dps.set_id = rr.set_id
LEFT JOIN	doc_permission_sets_ex dpse
							ON	dpse.permission_data = dt.doc_type
							AND	dpse.meta_id = rr.meta_id
							AND	dpse.set_id = rr.set_id
							AND	dpse.permission_id = 8 -- Create document
WHERE
								dpse.permission_data IS NOT NULL
							OR	rr.set_id = 0
							OR	urc.role_id = 0
ORDER BY	dt.doc_type
go

--
-- Procedure Create
-- imse.GetDocTypesWithNewPermissions
--
--
-- Procedure Create
-- dbo.GetDocTypesWithNewPermissions
--
CREATE PROCEDURE GetDocTypesWithNewPermissions @meta_id INT,@set_id INT, @lang_prefix VARCHAR(3) AS

/*
	Retrieves a list of all doc-types, with a indicator of wether a particular permission-set may use it.
	The permission-set must still have the "Create document"-permission set, though. ( Not checked in this proc )

	Column 1:	The doc-type
	Column 2:	The name of the doc-type
	Column 3:	> -1 if this set_id may use this.
*/
SELECT	doc_type,type,ISNULL(dpse.permission_data,-1)
FROM 		doc_types dt
LEFT JOIN	new_doc_permission_sets_ex dpse
							ON	dpse.permission_data = dt.doc_type
							AND	dpse.meta_id = @meta_id
							AND	dpse.set_id = @set_id
							AND	dpse.permission_id = 8
WHERE	dt.lang_prefix = @lang_prefix
ORDER	BY	CAST(ISNULL(dpse.permission_data,-1)+1  AS BIT) DESC,doc_type
go

--
-- Procedure Create
-- imse.GetDocTypesWithPermissions
--
--
-- Procedure Create
-- dbo.GetDocTypesWithPermissions
--
CREATE PROCEDURE GetDocTypesWithPermissions @meta_id INT,@set_id INT, @lang_prefix VARCHAR(3) AS

/*
	Retrieves a list of all doc-types, with a indicator of wether a particular permission-set may use it.
	The permission-set must still have the "Create document"-permission set, though. ( Not checked in this proc )

	Column 1:	The doc-type
	Column 2:	The name of the doc-type
	Column 3:	> -1 if this set_id may use this.
*/
SELECT	doc_type,type,ISNULL(dpse.permission_data,-1)
FROM 		doc_types dt
LEFT JOIN	doc_permission_sets_ex dpse
							ON	dpse.permission_data = dt.doc_type
							AND	dpse.meta_id = @meta_id
							AND	dpse.set_id = @set_id
							AND	dpse.permission_id = 8
WHERE	dt.lang_prefix = @lang_prefix
ORDER	BY	CAST(ISNULL(dpse.permission_data,-1)+1  AS BIT) DESC,doc_type
go

--
-- Procedure Extended Alter
-- imse.GetHighestUserId
--
EXEC sp_rename 'imse.GetHighestUserId','GetHighest_12192000100931000',OBJECT
go
--
-- Procedure Create
-- dbo.GetHighestUserId
--
CREATE PROCEDURE GetHighestUserId
AS
--DECLARE @retVal int

SELECT MAX(user_id) +1
FROM users
go
IF OBJECT_ID('imse.GetHighestUserId') IS NOT NULL
     DROP PROCEDURE imse.GetHighest_12192000100931000
ELSE 
     EXEC sp_rename 'imse.GetHighest_12192000100931000','GetHighestUserId',OBJECT
go

--
-- Procedure Extended Alter
-- imse.GetImgs
--
EXEC sp_rename 'imse.GetImgs','GetImgs_12192000100931000',OBJECT
go
--
-- Procedure Create
-- dbo.GetImgs
--
CREATE PROCEDURE GetImgs
@meta_id int AS

select '#img'+convert(varchar(5), name)+'#',name,imgurl,linkurl,width,height,border,v_space,h_space,image_name,align,alt_text,low_scr,target,target_name from images where meta_id = @meta_id
go
IF OBJECT_ID('imse.GetImgs') IS NOT NULL
     DROP PROCEDURE imse.GetImgs_12192000100931000
ELSE 
     EXEC sp_rename 'imse.GetImgs_12192000100931000','GetImgs',OBJECT
go

--
-- Procedure Extended Alter
-- imse.GetLangPrefixFromId
--
EXEC sp_rename 'imse.GetLangPrefixFromId','GetLangPre_12192000100932000',OBJECT
go
--
-- Procedure Create
-- dbo.GetLangPrefixFromId
--
CREATE PROCEDURE GetLangPrefixFromId
/* Get the users preferred language. Used by the administrator functions.
Begin with getting the users langId from the userobject.
*/
	@aLangId int
 AS
SELECT lang_prefix 
FROM lang_prefixes
WHERE lang_id = @aLangId
go
IF OBJECT_ID('imse.GetLangPrefixFromId') IS NOT NULL
     DROP PROCEDURE imse.GetLangPre_12192000100932000
ELSE 
     EXEC sp_rename 'imse.GetLangPre_12192000100932000','GetLangPrefixFromId',OBJECT
go

--
-- Procedure Create
-- imse.GetNewPermissionSet
--
--
-- Procedure Create
-- dbo.GetNewPermissionSet
--
CREATE PROCEDURE [GetNewPermissionSet] @meta_id INT, @set_id INT, @lang_prefix VARCHAR(3) AS


/*
	Nice little query that returns which permissions a permissionset consists of.

	Column 1:	The id of the permission
	Column 2:	The description of the permission
	Column 3:	Wether the permission is set. 0 or 1.
*/

SELECT	p.permission_id AS p_id, p.description,CAST(ISNULL((p.permission_id & dps.permission_id),0) AS BIT)
FROM 		new_doc_permission_sets dps
RIGHT JOIN	permissions p
							ON	(p.permission_id & dps.permission_id) > 0
							AND	dps.meta_id = @meta_id
							AND	dps.set_id = @set_id
							AND	p.lang_prefix = @lang_prefix

UNION
SELECT	dp.permission_id AS p_id, dp.description,CAST(ISNULL((dp.permission_id & dps.permission_id),0) AS BIT)
FROM 		meta m
JOIN		doc_permissions dp
							ON	dp.doc_type = m.doc_type
							AND	m.meta_id = @meta_id
							AND	dp.lang_prefix = @lang_prefix
LEFT JOIN	new_doc_permission_sets dps
							ON	(dp.permission_id & dps.permission_id) > 0
							AND	dps.set_id = @set_id
							AND	dps.meta_id = m.meta_id
go

--
-- Procedure Extended Alter
-- imse.GetNoOfTemplates
--
EXEC sp_rename 'imse.GetNoOfTemplates','GetNoOfTem_12192000100932000',OBJECT
go
--
-- Procedure Create
-- dbo.GetNoOfTemplates
--
CREATE PROCEDURE GetNoOfTemplates AS
select count(*) from templates
go
IF OBJECT_ID('imse.GetNoOfTemplates') IS NOT NULL
     DROP PROCEDURE imse.GetNoOfTem_12192000100932000
ELSE 
     EXEC sp_rename 'imse.GetNoOfTem_12192000100932000','GetNoOfTemplates',OBJECT
go

--
-- Procedure Create
-- imse.GetPermissionSet
--
--
-- Procedure Create
-- dbo.GetPermissionSet
--
CREATE PROCEDURE [GetPermissionSet] @meta_id INT, @set_id INT, @lang_prefix VARCHAR(3) AS


/*
	Nice little query that returns which permissions a permissionset consists of.

	Column 1:	The id of the permission
	Column 2:	The description of the permission
	Column 3:	Wether the permission is set. 0 or 1.
*/

SELECT	p.permission_id AS p_id, p.description,CAST(ISNULL((p.permission_id & dps.permission_id),0) AS BIT)
FROM 		doc_permission_sets dps
RIGHT JOIN	permissions p
							ON	(p.permission_id & dps.permission_id) > 0
							AND	dps.meta_id = @meta_id
							AND	dps.set_id = @set_id
							AND	p.lang_prefix = @lang_prefix

UNION
SELECT	dp.permission_id AS p_id, dp.description,CAST(ISNULL((dp.permission_id & dps.permission_id),0) AS BIT)
FROM 		meta m
JOIN		doc_permissions dp
							ON	dp.doc_type = m.doc_type
							AND	m.meta_id = @meta_id
							AND	dp.lang_prefix = @lang_prefix
LEFT JOIN	doc_permission_sets dps
							ON	(dp.permission_id & dps.permission_id) > 0
							AND	dps.set_id = @set_id
							AND	dps.meta_id = m.meta_id
go

--
-- Procedure Create
-- imse.GetRolesDocPermissions
--
--
-- Procedure Create
-- dbo.GetRolesDocPermissions
--
CREATE PROCEDURE GetRolesDocPermissions @meta_id INT AS

/*	Selects all roles except for superadmin, and returns the permissionset each has for the document.	*/

SELECT
		r.role_id,
		r.role_name,
		ISNULL(rr.set_id,4)
FROM
		roles_rights rr 
RIGHT JOIN 
		roles r 
						ON 	rr.role_id = r.role_id
						AND	rr.meta_id = @meta_id
WHERE	r.role_id > 0
ORDER BY	role_name
go

--
-- Procedure Create
-- imse.GetTemplateGroupsForUser
--
--
-- Procedure Create
-- dbo.GetTemplateGroupsForUser
--
CREATE PROCEDURE GetTemplateGroupsForUser @meta_id INT, @user_id INT AS

/*
	Nice query that fetches all templategroups a user may use in a document,
	for easy insertion into an html-option-list, no less!
*/
SELECT	distinct group_id,group_name
FROM 		templategroups dt
JOIN		user_roles_crossref urc
							ON	urc.user_id = @user_id
LEFT JOIN	roles_rights rr
							ON	rr.meta_id = @meta_id
							AND	rr.role_id = urc.role_id
LEFT JOIN	doc_permission_sets dps
							ON	dps.meta_id = rr.meta_id
							AND	dps.set_id = rr.set_id
LEFT JOIN	doc_permission_sets_ex dpse
							ON	dpse.permission_data = dt.group_id
							AND	(dpse.permission_id & dps.permission_id) > 0
							AND	dpse.meta_id = rr.meta_id
							AND	dpse.set_id = rr.set_id
							AND	dpse.permission_id = 524288 -- Change template
WHERE
								dpse.permission_data IS NOT NULL
							OR	rr.set_id = 0
							OR	urc.role_id = 0
ORDER BY	dt.group_id
go

--
-- Procedure Create
-- imse.GetTemplateGroupsWithNewPermissions
--
--
-- Procedure Create
-- dbo.GetTemplateGroupsWithNewPermissions
--
CREATE PROCEDURE GetTemplateGroupsWithNewPermissions @meta_id INT, @set_id INT AS

/*
	Retrieves a list of all templategroups, with a indicator of wether a particular permission-set may use it.
	The permission-set must still have the "Change template"-permission set, though. ( Not checked in this proc )

	Column 1:	The templategroup
	Column 2:	The name of the templategroup
	Column 3:	> -1 if this set_id may use this.
*/

SELECT	group_id,group_name,ISNULL(dpse.permission_data,-1)
FROM 		templategroups tg
LEFT JOIN	new_doc_permission_sets_ex dpse
							ON	dpse.permission_data = tg.group_id
							AND	dpse.meta_id = @meta_id
							AND	dpse.set_id = @set_id
							AND	dpse.permission_id = 524288
ORDER		BY CAST(ISNULL(dpse.permission_data,-1)+1  AS BIT) DESC, group_name
go

--
-- Procedure Create
-- imse.GetTemplateGroupsWithPermissions
--
CREATE PROCEDURE GetTemplateGroupsWithPermissions @meta_id INT, @set_id INT AS

/*
	Retrieves a list of all templategroups, with a indicator of wether a particular permission-set may use it.
	The permission-set must still have the "Change template"-permission set, though. ( Not checked in this proc )

	Column 1:	The templategroup
	Column 2:	The name of the templategroup
	Column 3:	> -1 if this set_id may use this.
*/

SELECT	group_id,group_name,ISNULL(dpse.permission_data,-1)
FROM 		templategroups tg
LEFT JOIN	doc_permission_sets_ex dpse
							ON	dpse.permission_data = tg.group_id
							AND	dpse.meta_id = @meta_id
							AND	dpse.set_id = @set_id
							AND	dpse.permission_id = 524288
ORDER		BY CAST(ISNULL(dpse.permission_data,-1)+1  AS BIT) DESC, group_name
go

--
-- Procedure Extended Alter
-- imse.GetTextDocData
--
EXEC sp_rename 'imse.GetTextDocData','GetTextDoc_12192000100933000',OBJECT
go
--
-- Procedure Create
-- dbo.GetTextDocData
--
CREATE PROCEDURE GetTextDocData @meta_id INT AS

SELECT	t.template_id, simple_name, sort_order, t.group_id
FROM 		text_docs t 	
JOIN 		templates c 
					ON t.template_id = c.template_id
WHERE meta_id = @meta_id
go
IF OBJECT_ID('imse.GetTextDocData') IS NOT NULL
     DROP PROCEDURE imse.GetTextDoc_12192000100933000
ELSE 
     EXEC sp_rename 'imse.GetTextDoc_12192000100933000','GetTextDocData',OBJECT
go

--
-- Procedure Extended Alter
-- imse.GetTexts
--
EXEC sp_rename 'imse.GetTexts','GetTexts_12192000100933000',OBJECT
go
--
-- Procedure Create
-- dbo.GetTexts
--
CREATE PROCEDURE GetTexts
@meta_id int AS

select '#txt'+convert(varchar(5), name)+'#',name,type,text from texts where meta_id = @meta_id
go
IF OBJECT_ID('imse.GetTexts') IS NOT NULL
     DROP PROCEDURE imse.GetTexts_12192000100933000
ELSE 
     EXEC sp_rename 'imse.GetTexts_12192000100933000','GetTexts',OBJECT
go

--
-- Procedure Extended Alter
-- imse.GetUserCreateDate
--
EXEC sp_rename 'imse.GetUserCreateDate','GetUserCre_12192000100934000',OBJECT
go
--
-- Procedure Create
-- dbo.GetUserCreateDate
--
CREATE PROCEDURE GetUserCreateDate
/*
 Returns the date when the user was created in the system
Used by servlet AdminUserProps
*/
	@userId int
AS

DECLARE @retVal smalldatetime
SELECT @retVal = create_date
FROM users
WHERE users.user_id = @userId

-- Lets validate for null
-- SELECT @retVal = ISNULL(  @retVal , '' )
---SELECT @retVal AS 'TemplateId'
SELECT @retVal  AS 'Usercreatedate'
go
IF OBJECT_ID('imse.GetUserCreateDate') IS NOT NULL
     DROP PROCEDURE imse.GetUserCre_12192000100934000
ELSE 
     EXEC sp_rename 'imse.GetUserCre_12192000100934000','GetUserCreateDate',OBJECT
go

--
-- Procedure Extended Alter
-- imse.GetUserId
--
EXEC sp_rename 'imse.GetUserId','GetUserId_12192000100934000',OBJECT
go
--
-- Procedure Create
-- dbo.GetUserId
--
CREATE PROCEDURE GetUserId 
 @aUserId int
AS
 SELECT user_id 
 FROM users
 WHERE user_id  = @aUserId
go
IF OBJECT_ID('imse.GetUserId') IS NOT NULL
     DROP PROCEDURE imse.GetUserId_12192000100934000
ELSE 
     EXEC sp_rename 'imse.GetUserId_12192000100934000','GetUserId',OBJECT
go

--
-- Procedure Extended Alter
-- imse.GetUserIdFromName
--
EXEC sp_rename 'imse.GetUserIdFromName','GetUserIdF_12192000100934000',OBJECT
go
--
-- Procedure Create
-- dbo.GetUserIdFromName
--
CREATE PROCEDURE [GetUserIdFromName] 
/*
Used by the conferences loginfunction, to detect a users userid from
the username
*/
	@userName char(15),
	@userPwd char(15)

AS

SELECT  u.user_id 
FROM users u
WHERE u.login_name = @userName
AND u.login_password = @userPwd
go
IF OBJECT_ID('imse.GetUserIdFromName') IS NOT NULL
     DROP PROCEDURE imse.GetUserIdF_12192000100934000
ELSE 
     EXEC sp_rename 'imse.GetUserIdF_12192000100934000','GetUserIdFromName',OBJECT
go

--
-- Procedure Extended Alter
-- imse.GetUserInfo
--
EXEC sp_rename 'imse.GetUserInfo','GetUserInf_12192000100934000',OBJECT
go
--
-- Procedure Create
-- dbo.GetUserInfo
--
CREATE PROCEDURE GetUserInfo
/* Returns all the information about a user. Used by adminsystem & conference system
*/

 @aUserId int
AS
 SELECT * 
 FROM users
 WHERE user_id = @aUserId
go
IF OBJECT_ID('imse.GetUserInfo') IS NOT NULL
     DROP PROCEDURE imse.GetUserInf_12192000100934000
ELSE 
     EXEC sp_rename 'imse.GetUserInf_12192000100934000','GetUserInfo',OBJECT
go

--
-- Procedure Extended Alter
-- imse.GetUserNames
--
EXEC sp_rename 'imse.GetUserNames','GetUserNam_12192000100934000',OBJECT
go
--
-- Procedure Create
-- dbo.GetUserNames
--
CREATE PROCEDURE GetUserNames
/* 
This procedure is used to retrieve a users full name (first name + last name
concateneted.
*/
	@user_id int,
	@what int
AS
	DECLARE @returnVal char(25)

IF(@what = 1) BEGIN
	SELECT @returnVal = RTRIM(first_name) 
	FROM users
	WHERE users.user_id = @user_id 
END ELSE BEGIN  
	SELECT @returnVal =  RTRIM(last_name) 
	FROM users
	WHERE users.user_id = @user_id 
END

SELECT @returnVal =  ISNULL(@returnVal, -1) 
SELECT @returnVal AS 'UserName'
go
IF OBJECT_ID('imse.GetUserNames') IS NOT NULL
     DROP PROCEDURE imse.GetUserNam_12192000100934000
ELSE 
     EXEC sp_rename 'imse.GetUserNam_12192000100934000','GetUserNames',OBJECT
go

--
-- Procedure Extended Alter
-- imse.GetUserPassword
--
EXEC sp_rename 'imse.GetUserPassword','GetUserPas_12192000100934000',OBJECT
go
--
-- Procedure Create
-- dbo.GetUserPassword
--
CREATE PROCEDURE GetUserPassword 
/* Used by AdminUserProps servlet to retrieve the users password 
*/
	@user_id int
AS

DECLARE @retVal char(15)
SELECT @retVal  = login_password 
FROM USERS
WHERE user_id = @user_id

SELECT @retVal =  ISNULL(@retVal , '') 
SELECT @retVal AS 'Password'
go
IF OBJECT_ID('imse.GetUserPassword') IS NOT NULL
     DROP PROCEDURE imse.GetUserPas_12192000100934000
ELSE 
     EXEC sp_rename 'imse.GetUserPas_12192000100934000','GetUserPassword',OBJECT
go

--
-- Procedure Create
-- imse.GetUserPermissionSet
--
--
-- Procedure Create
-- dbo.GetUserPermissionSet
--
CREATE PROCEDURE GetUserPermissionSet @meta_id INT, @user_id INT AS

/*
	Finds out what is the most privileged permission_set a user has for a document.

	Column 1:	The users most privileged set_id
	Column 2:	The users permission-set for this set_id
	Column 3:	The permissions for this document. ( At the time of this writing, the only permission there is is wether or not set_id 1 is more privileged than set_id 2, and it's stored in bit 0 )

	set_id's:

	0 - most privileged (full rights)
	1 & 2 - misc. They may be equal, and 1 may have permission to modify 2.
	3 - only read rights
	4 - least privileged (no rights)
*/

SELECT TOP 1	ISNULL((MIN(ISNULL(rr.set_id,4))*CAST(MIN(ISNULL(urc.role_id,1)) AS BIT)),4),
		ISNULL(dps.permission_id,0),
		ISNULL(m.permissions,0)
FROM 		roles_rights rr
RIGHT JOIN 	user_roles_crossref urc
						ON	urc.user_id = @user_id
						AND	rr.meta_id = @meta_id
						AND	(
								rr.role_id = urc.role_id
							OR	urc.role_id < 1
							)						
JOIN		meta m
						ON	m.meta_id = @meta_id
						AND	urc.user_id = @user_id
						AND	(
								rr.meta_id = @meta_id
							OR	urc.role_id = 0
							)
LEFT JOIN	doc_permission_sets dps
						ON	dps.meta_id = @meta_id
						AND	rr.set_id = dps.set_id
GROUP BY	ISNULL(dps.permission_id,0),m.permissions
ORDER BY	ISNULL((MIN(ISNULL(rr.set_id,4))*CAST(MIN(ISNULL(urc.role_id,1)) AS BIT)),4)
go

--
-- Procedure Create
-- imse.GetUserPermissionSetEx
--
--
-- Procedure Create
-- dbo.GetUserPermissionSetEx
--
CREATE PROCEDURE GetUserPermissionSetEx @meta_id INT, @user_id INT AS

/*
	Finds out what extended permissions (extra permissiondata) the user has for this document.
	Does not return correct data for a superadmin, or full admin, so check that first.
*/
SELECT	dps.permission_id, dps.permission_data
FROM 		roles_rights rr
JOIN 		user_roles_crossref urc
						ON	urc.user_id = @user_id
						AND	rr.role_id = urc.role_id
JOIN		meta m
						ON	m.meta_id = @meta_id
						AND	rr.meta_id = m.meta_id
JOIN		doc_permission_sets_ex dps
						ON	dps.meta_id = m.meta_id
						AND	rr.set_id = dps.set_id
go

--
-- Procedure Extended Alter
-- imse.GetUserPhoneNumbers
--
EXEC sp_rename 'imse.GetUserPhoneNumbers','GetUserPho_12192000100935000',OBJECT
go
/****** Object:  Stored Procedure dbo.GetUserPhoneNumbers    Script Date: 2000-10-27 15:43:09 ******/
CREATE PROCEDURE GetUserPhoneNumbers
/*
Returns a users phonenumbers. Used by AdminUserProps servlet
*/
	@user_id int

AS

-- The new version which includes phones
SELECT p.phone_id, p.country_code , p.area_code , p.number, p.user_id 
FROM users u , phones p 
WHERE u.user_id = p.user_id
AND u.user_id = @user_id
go
IF OBJECT_ID('imse.GetUserPhoneNumbers') IS NOT NULL
     DROP PROCEDURE imse.GetUserPho_12192000100935000
ELSE 
     EXEC sp_rename 'imse.GetUserPho_12192000100935000','GetUserPhoneNumbers',OBJECT
go

--
-- Procedure Extended Alter
-- imse.GetUserPhones
--
EXEC sp_rename 'imse.GetUserPhones','GetUserPho_12192000100935000',OBJECT
go
/****** Object:  Stored Procedure dbo.GetUserPhones    Script Date: 2000-10-27 15:43:09 ******/
CREATE PROCEDURE GetUserPhones
	@user_id int
AS
/*
Used to generate a list with all type of users. Used from AdminUserProps
*/

SELECT p.phone_id, RTRIM(p.country_code) + ' ' + RTRIM(p.area_code) + ' ' + RTRIM(p.number) as numbers
FROM users u , phones p 
WHERE u.user_id = p.user_id
AND u.user_id = @user_id
go
IF OBJECT_ID('imse.GetUserPhones') IS NOT NULL
     DROP PROCEDURE imse.GetUserPho_12192000100935000
ELSE 
     EXEC sp_rename 'imse.GetUserPho_12192000100935000','GetUserPhones',OBJECT
go

--
-- Procedure Create
-- imse.GetUserRoles
--
CREATE PROCEDURE GetUserRoles
/*
Used to get all roles for a user
*/

 @aUserId int
 AS
 SELECT role_name 
 FROM roles,user_roles_crossref 
 WHERE roles.role_id = user_roles_crossref.role_id
  AND user_roles_crossref.user_id = @aUserId
go

--
-- Procedure Create
-- imse.GetUserRolesDocPermissions
--
--
-- Procedure Create
-- dbo.GetUserRolesDocPermissions
--
CREATE PROCEDURE GetUserRolesDocPermissions @meta_id INT, @user_id INT AS

SELECT
		r.role_id,
		r.role_name,
		ISNULL(rr.set_id,4),
		ISNULL(urc.role_id,0)
FROM
		roles_rights rr 
RIGHT JOIN 
		roles r 
						ON 	rr.role_id = r.role_id
						AND	rr.meta_id = @meta_id
LEFT JOIN	user_roles_crossref urc
						ON	r.role_id = urc.role_id
						AND	urc.user_id = @user_id
WHERE	r.role_id > 0
ORDER BY	role_name
go

--
-- Procedure Extended Alter
-- imse.GetUserRolesIds
--
EXEC sp_rename 'imse.GetUserRolesIds','GetUserRol_12192000100935000',OBJECT
go
--
-- Procedure Create
-- dbo.GetUserRolesIds
--
CREATE PROCEDURE GetUserRolesIds
/* Returns the roles id:s for a user 
*/
 @aUserId int
 AS
 SELECT roles.role_id, role_name 
 FROM roles, user_roles_crossref 
 WHERE roles.role_id = user_roles_crossref.role_id
  AND user_roles_crossref.user_id = @aUserId
go
IF OBJECT_ID('imse.GetUserRolesIds') IS NOT NULL
     DROP PROCEDURE imse.GetUserRol_12192000100935000
ELSE 
     EXEC sp_rename 'imse.GetUserRol_12192000100935000','GetUserRolesIds',OBJECT
go

--
-- Procedure Extended Alter
-- imse.GetUserType
--
EXEC sp_rename 'imse.GetUserType','GetUserTyp_12192000100936000',OBJECT
go
--
-- Procedure Create
-- dbo.GetUserType
--
CREATE PROCEDURE GetUserType
/*
Used to get a users usertype. used from adminuser
*/
	@User_id int
 AS
DECLARE @returnVal int
SELECT DISTINCT @returnVal =  user_type  
FROM users
WHERE user_id = @User_id


SELECT @returnVal =  ISNULL(@returnVal, 1) 
SELECT @returnVal AS 'Usertype'
go
IF OBJECT_ID('imse.GetUserType') IS NOT NULL
     DROP PROCEDURE imse.GetUserTyp_12192000100936000
ELSE 
     EXEC sp_rename 'imse.GetUserTyp_12192000100936000','GetUserType',OBJECT
go

--
-- Procedure Extended Alter
-- imse.GetUserTypes
--
EXEC sp_rename 'imse.GetUserTypes','GetUserTyp_12192000100936000',OBJECT
go
--
-- Procedure Create
-- dbo.GetUserTypes
--
CREATE PROCEDURE GetUserTypes
/*
Used to generate a list with all type of users. Used from AdminUserProps
*/

 AS
 SELECT DISTINCT user_type, type_name 
 FROM user_types
go
IF OBJECT_ID('imse.GetUserTypes') IS NOT NULL
     DROP PROCEDURE imse.GetUserTyp_12192000100936000
ELSE 
     EXEC sp_rename 'imse.GetUserTyp_12192000100936000','GetUserTypes',OBJECT
go

--
-- Procedure Extended Alter
-- imse.GetUsersWhoBelongsToRole
--
EXEC sp_rename 'imse.GetUsersWhoBelongsToRole','GetUsersWh_12192000100936000',OBJECT
go
/****** Object:  Stored Procedure dbo.GetUsersWhoBelongsToRole    Script Date: 2000-10-27 14:21:05 ******/
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
go
IF OBJECT_ID('imse.GetUsersWhoBelongsToRole') IS NOT NULL
     DROP PROCEDURE imse.GetUsersWh_12192000100936000
ELSE 
     EXEC sp_rename 'imse.GetUsersWh_12192000100936000','GetUsersWhoBelongsToRole',OBJECT
go

--
-- Procedure Extended Alter
-- imse.IMC_AddChild
--
EXEC sp_rename 'imse.IMC_AddChild','IMC_AddChi_12192000100936000',OBJECT
go
--
-- Procedure Create
-- dbo.IMC_AddChild
--
CREATE PROCEDURE IMC_AddChild 
	@meta_id int , 
	@newMetaId int ,
	@doc_menu_no int,
	@newSortNo int
AS
INSERT INTO childs(meta_id,to_meta_id,menu_sort,manual_sort_order)
VALUES ( @meta_id , @newMetaId , @doc_menu_no , @newSortNo )
go
IF OBJECT_ID('imse.IMC_AddChild') IS NOT NULL
     DROP PROCEDURE imse.IMC_AddChi_12192000100936000
ELSE 
     EXEC sp_rename 'imse.IMC_AddChi_12192000100936000','IMC_AddChild',OBJECT
go

--
-- Procedure Extended Alter
-- imse.IMC_AddImage
--
EXEC sp_rename 'imse.IMC_AddImage','IMC_AddIma_12192000100936000',OBJECT
go
--
-- Procedure Create
-- dbo.IMC_AddImage
--
CREATE PROCEDURE IMC_AddImage
	@newMetaId int ,
	@t int
 AS
/*
INSERT INTO images( meta_id, width, height, border, v_space, h_space,
	name, image_name,target,target_name,align,alt_text,low_scr,imgurl,linkurl)
VALUES ( @newMetaId, 0, 0, 0, 0, 0, @t, '_self' ,  '_top', '' , '' )	
*/
go
IF OBJECT_ID('imse.IMC_AddImage') IS NOT NULL
     DROP PROCEDURE imse.IMC_AddIma_12192000100936000
ELSE 
     EXEC sp_rename 'imse.IMC_AddIma_12192000100936000','IMC_AddImage',OBJECT
go

--
-- Procedure Extended Alter
-- imse.IMC_AddImageRef
--
EXEC sp_rename 'imse.IMC_AddImageRef','IMC_AddIma_12192000100936000',OBJECT
go
--
-- Procedure Create
-- dbo.IMC_AddImageRef
--
CREATE PROCEDURE IMC_AddImageRef
	@newMetaId int ,
	@t int
 AS

-- add imageref to database     	
INSERT INTO images (meta_id, width, height, border, v_space, h_space,
name,image_name,target,target_name,align,alt_text,low_scr,imgurl,linkurl)
VALUES ( @newMetaId, 0, 0, 0, 0, 0,  @t, '',  '_self' , '',  '_top',  '','' ,'' , '' )
go
IF OBJECT_ID('imse.IMC_AddImageRef') IS NOT NULL
     DROP PROCEDURE imse.IMC_AddIma_12192000100936000
ELSE 
     EXEC sp_rename 'imse.IMC_AddIma_12192000100936000','IMC_AddImageRef',OBJECT
go

--
-- Procedure Extended Alter
-- imse.IMC_AddOwnerRights
--
EXEC sp_rename 'imse.IMC_AddOwnerRights','IMC_AddOwn_12192000100937000',OBJECT
go
--
-- Procedure Create
-- dbo.IMC_AddOwnerRights
--
CREATE PROCEDURE IMC_AddOwnerRights
	@metaId int,
	@userId int
AS

INSERT INTO user_rights
VALUES ( @userId , @metaId , 99 )
go
IF OBJECT_ID('imse.IMC_AddOwnerRights') IS NOT NULL
     DROP PROCEDURE imse.IMC_AddOwn_12192000100937000
ELSE 
     EXEC sp_rename 'imse.IMC_AddOwn_12192000100937000','IMC_AddOwnerRights',OBJECT
go

--
-- Procedure Extended Alter
-- imse.IMC_AddTextDoc
--
EXEC sp_rename 'imse.IMC_AddTextDoc','IMC_AddTex_12192000100937000',OBJECT
go
--
-- Procedure Create
-- dbo.IMC_AddTextDoc
--
CREATE PROCEDURE IMC_AddTextDoc
	@meta_id int ,
	@template_id int ,
	@sort_order int
AS
INSERT INTO text_docs ( meta_id,template_id,sort_order )
VALUES ( @meta_id , @template_id , @sort_order )
go
IF OBJECT_ID('imse.IMC_AddTextDoc') IS NOT NULL
     DROP PROCEDURE imse.IMC_AddTex_12192000100937000
ELSE 
     EXEC sp_rename 'imse.IMC_AddTex_12192000100937000','IMC_AddTextDoc',OBJECT
go

--
-- Procedure Extended Alter
-- imse.IMC_AddTexts
--
EXEC sp_rename 'imse.IMC_AddTexts','IMC_AddTex_12192000100937000',OBJECT
go
--
-- Procedure Create
-- dbo.IMC_AddTexts
--
CREATE PROCEDURE IMC_AddTexts
/*
Adds a new texttype  to the texts table
*/
	@newMetaId int ,
	@name int ,
	@text text
 AS

-- add texts to database     	
INSERT INTO texts(meta_id,name,text,type)
VALUES ( @newMetaId , @name , @text , 1 )
go
IF OBJECT_ID('imse.IMC_AddTexts') IS NOT NULL
     DROP PROCEDURE imse.IMC_AddTex_12192000100937000
ELSE 
     EXEC sp_rename 'imse.IMC_AddTex_12192000100937000','IMC_AddTexts',OBJECT
go

--
-- Procedure Extended Alter
-- imse.IMC_AddUserRights
--
EXEC sp_rename 'imse.IMC_AddUserRights','IMC_AddUse_12192000100937000',OBJECT
go
--
-- Procedure Create
-- dbo.IMC_AddUserRights
--
CREATE PROCEDURE IMC_AddUserRights
	@metaId int,
	@userRight int,
	@typeOfRight int
AS


-- READ
IF( @typeOfRight = 1 ) BEGIN
	INSERT INTO user_rights
	VALUES ( @userRight , @metaId , @typeOfRight ) 
END

-- WRITE
IF( @typeOfRight = 3 ) BEGIN
	INSERT INTO user_rights
	VALUES ( @userRight , @metaId , @typeOfRight ) 
END
go
IF OBJECT_ID('imse.IMC_AddUserRights') IS NOT NULL
     DROP PROCEDURE imse.IMC_AddUse_12192000100937000
ELSE 
     EXEC sp_rename 'imse.IMC_AddUse_12192000100937000','IMC_AddUserRights',OBJECT
go

--
-- Procedure Extended Alter
-- imse.IMC_CheckMenuSort
--
EXEC sp_rename 'imse.IMC_CheckMenuSort','IMC_CheckM_12192000100937000',OBJECT
go
--
-- Procedure Create
-- dbo.IMC_CheckMenuSort
--
CREATE PROCEDURE IMC_CheckMenuSort
	@meta_id int,
	@doc_menu_no int
AS

-- test if this is the first child with this  menusort
SELECT to_meta_id 
FROM childs
WHERE meta_id =  @meta_id 
AND menu_sort = @doc_menu_no
go
IF OBJECT_ID('imse.IMC_CheckMenuSort') IS NOT NULL
     DROP PROCEDURE imse.IMC_CheckM_12192000100937000
ELSE 
     EXEC sp_rename 'imse.IMC_CheckM_12192000100937000','IMC_CheckMenuSort',OBJECT
go

--
-- Procedure Extended Alter
-- imse.IMC_ExecuteExample
--
EXEC sp_rename 'imse.IMC_ExecuteExample','IMC_Execut_12192000100938000',OBJECT
go
--
-- Procedure Create
-- dbo.IMC_ExecuteExample
--
CREATE PROCEDURE [IMC_ExecuteExample] AS

-- Lets create the templates library path as well
EXEC AddNewTemplateLib 1
go
IF OBJECT_ID('imse.IMC_ExecuteExample') IS NOT NULL
     DROP PROCEDURE imse.IMC_Execut_12192000100938000
ELSE 
     EXEC sp_rename 'imse.IMC_Execut_12192000100938000','IMC_ExecuteExample',OBJECT
go

--
-- Procedure Extended Alter
-- imse.IMC_GetNbrOfText
--
EXEC sp_rename 'imse.IMC_GetNbrOfText','IMC_GetNbr_12192000100938000',OBJECT
go
--
-- Procedure Create
-- dbo.IMC_GetNbrOfText
--
CREATE PROCEDURE IMC_GetNbrOfText
	@meta_id int 
 AS

-- find no_of_txt for the template
SELECT no_of_txt,no_of_img,no_of_url 
FROM text_docs,templates
WHERE meta_id = @meta_id
AND templates.template_id = text_docs.template_id ;
go
IF OBJECT_ID('imse.IMC_GetNbrOfText') IS NOT NULL
     DROP PROCEDURE imse.IMC_GetNbr_12192000100938000
ELSE 
     EXEC sp_rename 'imse.IMC_GetNbr_12192000100938000','IMC_GetNbrOfText',OBJECT
go

--
-- Procedure Extended Alter
-- imse.IMC_GetSortOrderNum
--
EXEC sp_rename 'imse.IMC_GetSortOrderNum','IMC_GetSor_12192000100938000',OBJECT
go
--
-- Procedure Create
-- dbo.IMC_GetSortOrderNum
--
CREATE PROCEDURE IMC_GetSortOrderNum
/* 
 Returns the highest sortOrderNumber
*/
	@meta_id int,
	@doc_menu_no int
 AS

-- update child table
SELECT MAX(manual_sort_order) 
FROM childs
WHERE meta_id = @meta_id 
AND menu_sort = @doc_menu_no
go
IF OBJECT_ID('imse.IMC_GetSortOrderNum') IS NOT NULL
     DROP PROCEDURE imse.IMC_GetSor_12192000100938000
ELSE 
     EXEC sp_rename 'imse.IMC_GetSor_12192000100938000','IMC_GetSortOrderNum',OBJECT
go

--
-- Procedure Extended Alter
-- imse.IMC_GetTemplateId
--
EXEC sp_rename 'imse.IMC_GetTemplateId','IMC_GetTem_12192000100938000',OBJECT
go
--
-- Procedure Create
-- dbo.IMC_GetTemplateId
--
CREATE PROCEDURE IMC_GetTemplateId
	@meta_id int
AS

SELECT template_id
FROM text_docs
WHERE meta_id = @meta_id
go
IF OBJECT_ID('imse.IMC_GetTemplateId') IS NOT NULL
     DROP PROCEDURE imse.IMC_GetTem_12192000100938000
ELSE 
     EXEC sp_rename 'imse.IMC_GetTem_12192000100938000','IMC_GetTemplateId',OBJECT
go

--
-- Procedure Create
-- imse.InheritPermissions
--
--
-- Procedure Create
-- dbo.InheritPermissions
--
CREATE PROCEDURE InheritPermissions @new_meta_id INT, @parent_meta_id INT, @doc_type INT AS

INSERT INTO	doc_permission_sets
SELECT 	@new_meta_id,
		ndps.set_id,
		ndps.permission_id | (ISNULL(CAST(permission_data AS BIT),0) * 65536)
FROM 		new_doc_permission_sets ndps
LEFT JOIN 	new_doc_permission_sets_ex ndpse	ON	ndps.meta_id = ndpse.meta_id
							AND	ndps.set_id = ndpse.set_id
							AND	ndpse.permission_id = 8
							AND	ndpse.permission_data = @doc_type
							AND	@doc_type != 2
WHERE	ndps.meta_id = @parent_meta_id
GROUP BY	ndps.meta_id,
		ndps.set_id,
		ndps.permission_id,
		ndpse.permission_id,
		ndpse.permission_data

INSERT INTO	doc_permission_sets_ex
SELECT	@new_meta_id,
		ndpse.set_id,
		ndpse.permission_id,
		ndpse.permission_data
FROM		new_doc_permission_sets_ex ndpse
WHERE	ndpse.meta_id = @parent_meta_id
	AND	@doc_type = 2

INSERT INTO	new_doc_permission_sets
SELECT	@new_meta_id,
		ndps.set_id,
		ndps.permission_id
FROM		new_doc_permission_sets ndps
WHERE	ndps.meta_id = @parent_meta_id
	AND	@doc_type = 2

INSERT INTO	new_doc_permission_sets_ex
SELECT	@new_meta_id,
		ndpse.set_id,
		ndpse.permission_id,
		ndpse.permission_data
FROM		new_doc_permission_sets_ex ndpse
WHERE	ndpse.meta_id = @parent_meta_id
	AND	@doc_type = 2

INSERT INTO	roles_rights
SELECT	role_id, @new_meta_id, set_id
FROM		roles_rights
WHERE	meta_id = @parent_meta_id
go

--
-- Procedure Extended Alter
-- imse.ListDocsByDate
--
EXEC sp_rename 'imse.ListDocsByDate','ListDocsBy_12192000100940000',OBJECT
go
/****** Object:  Stored Procedure dbo.ListDocsByDate    Script Date: 2000-10-27 14:21:06 ******/
CREATE PROCEDURE imse.ListDocsByDate @listMod int,  @doc_type int, @startDate varchar(10), @endDate varchar(20) AS
/*
 lists doctyps where activate = 1
 @listMod 0 = all date, 1 = only creatdat, 2 = only modifieddata
 @startDoc yyyy-mm-dd or 0 then not set
 @endDate yyyy-mm-dd or 0 then not set
*/


-- Listdate fix 
if ( @endDate <> '0') BEGIN
	SET @endDate = @endDate + ' 23:59:59'
	PRINT @endDate
END 

/* list all (not in use ) */
if ( @listMod = 0) begin
	if ( @startDate = '0' ) begin
		if ( @endDate = '0' ) begin
			select m.meta_id, dt.type, m.meta_headline, m.date_modified
	
			from meta m

			join doc_types dt
			 		on m.doc_type = dt.doc_type
			where m.doc_type = @doc_type and activate = 1

			order by m.date_modified
		end
		else begin
			select m.meta_id, dt.type, m.meta_headline, m.date_modified
	
			from meta m

			join doc_types dt
			 		on m.doc_type = dt.doc_type
			where m.doc_type = @doc_type and m.date_modified <= @endDate and activate = 1

			order by m.date_modified
		end
	end
	else if ( @startDate != '0' ) begin
		if ( @endDate = '0' ) begin
			select m.meta_id, dt.type, m.meta_headline, m.date_modified
	
			from meta m

			join doc_types dt
			 		on m.doc_type = dt.doc_type
			where m.doc_type = @doc_type and m.date_modified >= @startDate and activate = 1

			order by m.date_modified
		end
		else
		begin
			select m.meta_id, dt.type, m.meta_headline, m.date_modified
	
			from meta m

			join doc_types dt
			 		on m.doc_type = dt.doc_type
			where m.doc_type = @doc_type and m.date_modified <= @endDate and m.date_modified >= @startDate and activate = 1

			order by m.date_modified
		end
	end
end

/* list creatdate */
else if ( @listMod = 1) begin
	if ( @startDate = '0' ) begin
		if ( @endDate = '0' ) begin
			select m.meta_id, dt.type, m.meta_headline, m.date_created
	
			from meta m

			join doc_types dt
			 		on m.doc_type = dt.doc_type
			where m.doc_type = @doc_type and activate = 1

			order by m.date_created
		end
		else begin
			select m.meta_id, dt.type, m.meta_headline, m.date_created
	
			from meta m

			join doc_types dt
			 		on m.doc_type = dt.doc_type
			where m.doc_type = @doc_type and m.date_created <= @endDate and activate = 1

			order by m.date_created
		end
	end
	else if ( @startDate != '0' ) begin
		if ( @endDate = '0' ) begin
			select m.meta_id, dt.type, m.meta_headline, m.date_created
	
			from meta m

			join doc_types dt
			 		on m.doc_type = dt.doc_type
			where m.doc_type = @doc_type and m.date_created >= @startDate and activate = 1

			order by m.date_created
		end
		else
		begin
			select m.meta_id, dt.type, m.meta_headline, m.date_created
	
			from meta m

			join doc_types dt
			 		on m.doc_type = dt.doc_type
			where m.doc_type = @doc_type and m.date_created <= @endDate and m.date_created >= @startDate and activate = 1

			order by m.date_created
		end
	end
end
/* list only modified*/
else begin
	if ( @startDate = '0' ) begin
		if ( @endDate = '0' ) begin
			select m.meta_id, dt.type, m.meta_headline, m.date_modified
	
			from meta m

			join doc_types dt
			 		on m.doc_type = dt.doc_type
			where m.doc_type = @doc_type and activate = 1 and m.date_modified != m.date_created

			order by m.date_modified
		end
		else begin
			select m.meta_id, dt.type, m.meta_headline, m.date_modified
	
			from meta m

			join doc_types dt
			 		on m.doc_type = dt.doc_type
			where m.doc_type = @doc_type and m.date_modified <= @endDate and activate = 1 and m.date_modified != m.date_created

			order by m.date_modified
		end
	end
	else if ( @startDate != '0' ) begin
		if ( @endDate = '0' ) begin
			select m.meta_id, dt.type, m.meta_headline, m.date_modified
	
			from meta m

			join doc_types dt
			 		on m.doc_type = dt.doc_type
			where m.doc_type = @doc_type and m.date_modified >= @startDate and activate = 1 and m.date_modified != m.date_created

			order by m.date_modified
		end
		else
		begin
			select m.meta_id, dt.type, m.meta_headline, m.date_modified
	
			from meta m

			join doc_types dt
			 		on m.doc_type = dt.doc_type
			where m.doc_type = @doc_type and m.date_modified <= @endDate and m.date_modified >= @startDate and activate = 1 and m.date_modified != m.date_created

			order by m.date_modified
		end
	end
end
go
IF OBJECT_ID('imse.ListDocsByDate') IS NOT NULL
     DROP PROCEDURE imse.ListDocsBy_12192000100940000
ELSE 
     EXEC sp_rename 'imse.ListDocsBy_12192000100940000','ListDocsByDate',OBJECT
go

--
-- Procedure Extended Alter
-- imse.ListDocsGetInternalDocTypes
--
EXEC sp_rename 'imse.ListDocsGetInternalDocTypes','ListDocsGe_12192000100940000',OBJECT
go
/****** Object:  Stored Procedure dbo.ListDocsGetInternalDocTypes    Script Date: 2000-10-27 14:21:06 ******/
CREATE PROCEDURE ListDocsGetInternalDocTypes AS

/* selct all internal doc types */
select doc_type, type 

from doc_types

where doc_type <= 100
go
IF OBJECT_ID('imse.ListDocsGetInternalDocTypes') IS NOT NULL
     DROP PROCEDURE imse.ListDocsGe_12192000100940000
ELSE 
     EXEC sp_rename 'imse.ListDocsGe_12192000100940000','ListDocsGetInternalDocTypes',OBJECT
go

--
-- Procedure Extended Alter
-- imse.ListDocsGetInternalDocTypesValue
--
EXEC sp_rename 'imse.ListDocsGetInternalDocTypesValue','ListDocsGe_12192000100940000',OBJECT
go
/****** Object:  Stored Procedure dbo.ListDocsGetInternalDocTypesValue    Script Date: 2000-10-27 14:21:06 ******/
CREATE PROCEDURE ListDocsGetInternalDocTypesValue AS

/* selct all internal doc types */
select doc_type

from doc_types

where doc_type <= 100
go
IF OBJECT_ID('imse.ListDocsGetInternalDocTypesValue') IS NOT NULL
     DROP PROCEDURE imse.ListDocsGe_12192000100940000
ELSE 
     EXEC sp_rename 'imse.ListDocsGe_12192000100940000','ListDocsGetInternalDocTypesValue',OBJECT
go

--
-- Procedure Extended Alter
-- imse.PermissionsGetPermission
--
EXEC sp_rename 'imse.PermissionsGetPermission','Permission_12192000100941000',OBJECT
go
/****** Object:  Stored Procedure dbo.PermissionsGetPermission    Script Date: 2000-10-27 14:21:06 ******/
CREATE PROCEDURE PermissionsGetPermission @login_name varchar(15), @permission int AS
/*

*/
select login_password, first_name, last_name, email, min(permissions&@permission), lang_prefix 
from users u 
join lang_prefixes lp 
				on u.lang_id = lp.lang_id 
join user_roles_crossref urc 
				on u.user_id = urc.user_id left 
join roles r 
				on r.role_id = urc.role_id
where login_name = @login_name
group by login_password, first_name, last_name, email, lang_prefix
go
IF OBJECT_ID('imse.PermissionsGetPermission') IS NOT NULL
     DROP PROCEDURE imse.Permission_12192000100941000
ELSE 
     EXEC sp_rename 'imse.Permission_12192000100941000','PermissionsGetPermission',OBJECT
go

--
-- Procedure Extended Alter
-- imse.PhoneNbrDelete
--
EXEC sp_rename 'imse.PhoneNbrDelete','PhoneNbrDe_12192000100941000',OBJECT
go
--
-- Procedure Create
-- dbo.PhoneNbrDelete
--
CREATE PROCEDURE PhoneNbrDelete
/*
 Deletes an Ip-access for a user. Used by the PhoneNbrDelete
*/

	@phoneId int
AS

DELETE FROM PHONES 
WHERE phone_id = @phoneId
go
IF OBJECT_ID('imse.PhoneNbrDelete') IS NOT NULL
     DROP PROCEDURE imse.PhoneNbrDe_12192000100941000
ELSE 
     EXEC sp_rename 'imse.PhoneNbrDe_12192000100941000','PhoneNbrDelete',OBJECT
go

--
-- Procedure Extended Alter
-- imse.PhoneNbrUpdate
--
EXEC sp_rename 'imse.PhoneNbrUpdate','PhoneNbrUp_12192000100941000',OBJECT
go
--
-- Procedure Create
-- dbo.PhoneNbrUpdate
--
CREATE PROCEDURE PhoneNbrUpdate 
/*
This function adds a new phone numbers to the db. Used by AdminUserPhones
*/
	@user_id int,
	@phone_id int,
	@country varchar(15) ,
	@area varchar(15) , 
	@nbr varchar(15)
AS

UPDATE phones
	SET country_code = @country,
	area_code = @area ,
	number = @nbr
WHERE phones.user_id = @user_id
AND phones.phone_id = @phone_id
go
IF OBJECT_ID('imse.PhoneNbrUpdate') IS NOT NULL
     DROP PROCEDURE imse.PhoneNbrUp_12192000100941000
ELSE 
     EXEC sp_rename 'imse.PhoneNbrUp_12192000100941000','PhoneNbrUpdate',OBJECT
go

--
-- Procedure Extended Alter
-- imse.RemoveUserFromRole
--
EXEC sp_rename 'imse.RemoveUserFromRole','RemoveUser_12192000100941000',OBJECT
go
--
-- Procedure Create
-- dbo.RemoveUserFromRole
--
CREATE PROCEDURE RemoveUserFromRole
	@userId int, @role_id int
AS

/* removes user from role */
DELETE 
FROM user_roles_crossref
WHERE user_id = @userId and role_id = @role_id
go
IF OBJECT_ID('imse.RemoveUserFromRole') IS NOT NULL
     DROP PROCEDURE imse.RemoveUser_12192000100941000
ELSE 
     EXEC sp_rename 'imse.RemoveUser_12192000100941000','RemoveUserFromRole',OBJECT
go

--
-- Procedure Extended Alter
-- imse.RoleAddNew
--
EXEC sp_rename 'imse.RoleAddNew','RoleAddNew_12192000100941000',OBJECT
go
--
-- Procedure Create
-- dbo.RoleAddNew
--
CREATE PROCEDURE RoleAddNew
	@newRoleName char(25)
/* Adds a new role */
AS
DECLARE @newRoleId int
SELECT @newRoleId = MAX(r.role_id) + 1
FROM roles r

INSERT INTO roles (  role_id , role_name )
VALUES( @newRoleId , @newRoleName )
go
IF OBJECT_ID('imse.RoleAddNew') IS NOT NULL
     DROP PROCEDURE imse.RoleAddNew_12192000100941000
ELSE 
     EXEC sp_rename 'imse.RoleAddNew_12192000100941000','RoleAddNew',OBJECT
go

--
-- Procedure Extended Alter
-- imse.RoleAdminGetAll
--
EXEC sp_rename 'imse.RoleAdminGetAll','RoleAdminG_12192000100941000',OBJECT
go
--
-- Procedure Create
-- dbo.RoleAdminGetAll
--
CREATE PROCEDURE RoleAdminGetAll AS
/*
 Used by AdminRoles servlet to retrieve all roles except the Superadmin role
*/
SELECT role_id , role_name FROM ROLES
WHERE role_id != 0
ORDER BY role_name
go
IF OBJECT_ID('imse.RoleAdminGetAll') IS NOT NULL
     DROP PROCEDURE imse.RoleAdminG_12192000100941000
ELSE 
     EXEC sp_rename 'imse.RoleAdminG_12192000100941000','RoleAdminGetAll',OBJECT
go

--
-- Procedure Extended Alter
-- imse.RoleCountAffectedUsers
--
EXEC sp_rename 'imse.RoleCountAffectedUsers','RoleCountA_12192000100942000',OBJECT
go
--
-- Procedure Create
-- dbo.RoleCountAffectedUsers
--
CREATE PROCEDURE RoleCountAffectedUsers
	@aRoleId int
AS
/*
 This function is used when an administrator tries to delete a role.
 This function counts how many users who will be affected
*/

SELECT  DISTINCT COUNT(usr.role_id )
FROM user_roles_crossref usr, roles r, users u
WHERE usr.ROLE_ID = @aRoleId 
AND usr.user_id = u.user_id
go
IF OBJECT_ID('imse.RoleCountAffectedUsers') IS NOT NULL
     DROP PROCEDURE imse.RoleCountA_12192000100942000
ELSE 
     EXEC sp_rename 'imse.RoleCountA_12192000100942000','RoleCountAffectedUsers',OBJECT
go

--
-- Procedure Extended Alter
-- imse.RoleDelete
--
EXEC sp_rename 'imse.RoleDelete','RoleDelete_12192000100942000',OBJECT
go
/****** Object:  Stored Procedure dbo.RoleDelete    Script Date: 2000-10-27 14:21:05 ******/
CREATE PROCEDURE RoleDelete
/* Deletes an role from the role table. Used by the AdminRoles servlet
*/
	@roleId int
AS

DELETE FROM ROLES_RIGHTS WHERE ROLE_ID = @roleId
DELETE FROM user_roles_crossref WHERE ROLE_ID =@roleId
DELETE FROM ROLES WHERE ROLE_ID = @roleId
go
IF OBJECT_ID('imse.RoleDelete') IS NOT NULL
     DROP PROCEDURE imse.RoleDelete_12192000100942000
ELSE 
     EXEC sp_rename 'imse.RoleDelete_12192000100942000','RoleDelete',OBJECT
go

--
-- Procedure Extended Alter
-- imse.RoleDeleteViewAffectedUsers
--
EXEC sp_rename 'imse.RoleDeleteViewAffectedUsers','RoleDelete_12192000100942000',OBJECT
go
--
-- Procedure Create
-- dbo.RoleDeleteViewAffectedUsers
--
CREATE PROCEDURE RoleDeleteViewAffectedUsers
	@aRoleId int
AS
/*
 This function is used when an administrator tries to delete a role.
 All users which will be affected of the deletion will be presenteted in a list
*/

SELECT distinct TOP 50  usr.role_id , (RTRIM(last_name) + ', ' + RTRIM(first_name))
FROM user_roles_crossref usr, roles r, users u
WHERE usr.ROLE_ID = @aRoleId 
AND usr.user_id = u.user_id
--GROUP BY (RTRIM(last_name) + ', ' + RTRIM(first_name)), usr.role_id
ORDER BY (RTRIM(last_name) + ', ' + RTRIM(first_name))
go
IF OBJECT_ID('imse.RoleDeleteViewAffectedUsers') IS NOT NULL
     DROP PROCEDURE imse.RoleDelete_12192000100942000
ELSE 
     EXEC sp_rename 'imse.RoleDelete_12192000100942000','RoleDeleteViewAffectedUsers',OBJECT
go

--
-- Procedure Extended Alter
-- imse.RoleFindName
--
EXEC sp_rename 'imse.RoleFindName','RoleFindNa_12192000100942000',OBJECT
go
--
-- Procedure Create
-- dbo.RoleFindName
--
CREATE PROCEDURE RoleFindName
	@newRoleName char(25)
AS
/*
This function is when an administrator tries to add a new roleName.  
The system searches for the rolename and returns the the id it exists otherwize -1
*/
DECLARE @returnVal int
SELECT  @returnVal = r.role_id
FROM roles r
WHERE r.role_name = @newRoleName

-- Lets validate for null
SELECT @returnVal = ISNULL(  @returnVal , -1 )
SELECT @returnVal AS 'FoundRoleName'
go
IF OBJECT_ID('imse.RoleFindName') IS NOT NULL
     DROP PROCEDURE imse.RoleFindNa_12192000100942000
ELSE 
     EXEC sp_rename 'imse.RoleFindNa_12192000100942000','RoleFindName',OBJECT
go

--
-- Procedure Extended Alter
-- imse.RoleGetAllApartFromRole
--
EXEC sp_rename 'imse.RoleGetAllApartFromRole','RoleGetAll_12192000100942000',OBJECT
go
--
-- Procedure Create
-- dbo.RoleGetAllApartFromRole
--
CREATE PROCEDURE RoleGetAllApartFromRole @role_id int AS
/*
 Used by AdminRoleBelongings servlet to retrieve all roles except the Superadmin role and role role_id
*/
SELECT role_id , role_name FROM ROLES
WHERE role_id != 0 and role_id != @role_id
ORDER BY role_id
go
IF OBJECT_ID('imse.RoleGetAllApartFromRole') IS NOT NULL
     DROP PROCEDURE imse.RoleGetAll_12192000100942000
ELSE 
     EXEC sp_rename 'imse.RoleGetAll_12192000100942000','RoleGetAllApartFromRole',OBJECT
go

--
-- Procedure Extended Alter
-- imse.RoleGetName
--
EXEC sp_rename 'imse.RoleGetName','RoleGetNam_12192000100943000',OBJECT
go
--
-- Procedure Create
-- dbo.RoleGetName
--
CREATE PROCEDURE RoleGetName
	@roleId int
AS
/*
This function is when an administrator tries to add a new roleName.  
The system searches for the rolename and returns the the id it exists otherwize -1
*/
DECLARE @returnStr char(25)
SELECT  @returnStr = r.role_name
FROM roles r
WHERE r.role_id = @roleId

-- Lets validate for null
SELECT @returnStr = ISNULL(  @returnStr , '---' )
SELECT @returnStr AS 'Rolename'
go
IF OBJECT_ID('imse.RoleGetName') IS NOT NULL
     DROP PROCEDURE imse.RoleGetNam_12192000100943000
ELSE 
     EXEC sp_rename 'imse.RoleGetNam_12192000100943000','RoleGetName',OBJECT
go

--
-- Procedure Extended Alter
-- imse.RoleGetPermissionsByLanguage
--
EXEC sp_rename 'imse.RoleGetPermissionsByLanguage','RoleGetPer_12192000100943000',OBJECT
go
--
-- Procedure Create
-- dbo.RoleGetPermissionsByLanguage
--
CREATE PROCEDURE RoleGetPermissionsByLanguage @lang_prefix varchar(3) AS
/*select permissions by language prefix.*/
select	permission_id, description

from	roles_permissions 

where	lang_prefix = @lang_prefix

order by permission_id
go
IF OBJECT_ID('imse.RoleGetPermissionsByLanguage') IS NOT NULL
     DROP PROCEDURE imse.RoleGetPer_12192000100943000
ELSE 
     EXEC sp_rename 'imse.RoleGetPer_12192000100943000','RoleGetPermissionsByLanguage',OBJECT
go

--
-- Procedure Extended Alter
-- imse.RoleGetPermissionsFromRole
--
EXEC sp_rename 'imse.RoleGetPermissionsFromRole','RoleGetPer_12192000100943000',OBJECT
go
--
-- Procedure Create
-- dbo.RoleGetPermissionsFromRole
--
CREATE PROCEDURE RoleGetPermissionsFromRole @role_id int, @lang_prefix varchar(3) AS

/*
  select rolepermission from role id
*/
SELECT		ISNULL(r.permissions & rp.permission_id,0) AS value,rp.permission_id,rp.description
FROM			roles_permissions rp
LEFT JOIN		roles r
					ON	rp.permission_id & r.permissions != 0
					AND	r.role_id = @role_id
WHERE lang_prefix = @lang_prefix
go
IF OBJECT_ID('imse.RoleGetPermissionsFromRole') IS NOT NULL
     DROP PROCEDURE imse.RoleGetPer_12192000100943000
ELSE 
     EXEC sp_rename 'imse.RoleGetPer_12192000100943000','RoleGetPermissionsFromRole',OBJECT
go

--
-- Procedure Extended Alter
-- imse.RolePermissionsAddNew
--
EXEC sp_rename 'imse.RolePermissionsAddNew','RolePermis_12192000100943000',OBJECT
go
--
-- Procedure Create
-- dbo.RolePermissionsAddNew
--
CREATE PROCEDURE RolePermissionsAddNew
	@newRoleName char(25), @permissions int
/* Adds a new role */
AS
DECLARE @newRoleId int
SELECT @newRoleId = MAX(r.role_id) + 1
FROM roles r

INSERT INTO roles (  role_id , role_name, permissions )
VALUES( @newRoleId , @newRoleName, @permissions )
go
IF OBJECT_ID('imse.RolePermissionsAddNew') IS NOT NULL
     DROP PROCEDURE imse.RolePermis_12192000100943000
ELSE 
     EXEC sp_rename 'imse.RolePermis_12192000100943000','RolePermissionsAddNew',OBJECT
go

--
-- Procedure Extended Alter
-- imse.RoleUpdateName
--
EXEC sp_rename 'imse.RoleUpdateName','RoleUpdate_12192000100943000',OBJECT
go
--
-- Procedure Create
-- dbo.RoleUpdateName
--
CREATE PROCEDURE RoleUpdateName
/*
Updates the name on a role in the db
*/
	@role_id int,
	@newRole_name char(25)
AS

UPDATE ROLES
SET role_name = @newRole_name
WHERE role_id = @role_id
go
IF OBJECT_ID('imse.RoleUpdateName') IS NOT NULL
     DROP PROCEDURE imse.RoleUpdate_12192000100943000
ELSE 
     EXEC sp_rename 'imse.RoleUpdate_12192000100943000','RoleUpdateName',OBJECT
go

--
-- Procedure Extended Alter
-- imse.RoleUpdatePermissions
--
EXEC sp_rename 'imse.RoleUpdatePermissions','RoleUpdate_12192000100944000',OBJECT
go
--
-- Procedure Create
-- dbo.RoleUpdatePermissions
--
CREATE PROCEDURE RoleUpdatePermissions @role_id int,  @permissions int AS

/* update permissions for role */
update roles 

Set permissions = @permissions 

where role_id = @role_id
go
IF OBJECT_ID('imse.RoleUpdatePermissions') IS NOT NULL
     DROP PROCEDURE imse.RoleUpdate_12192000100944000
ELSE 
     EXEC sp_rename 'imse.RoleUpdate_12192000100944000','RoleUpdatePermissions',OBJECT
go

--
-- Procedure Create
-- imse.SetDocPermissionSet
--
--
-- Procedure Create
-- dbo.SetDocPermissionSet
--
CREATE PROCEDURE SetDocPermissionSet @meta_id INT, @set_id INT, @permission_id INT AS

/*
	Updates a permissionset for a document.
*/

-- Delete the previous value
DELETE FROM doc_permission_sets
WHERE	meta_id = @meta_id
AND		set_id = @set_id

-- Insert new value
INSERT INTO	doc_permission_sets
VALUES	(@meta_id,@set_id,@permission_id)
go

--
-- Procedure Create
-- imse.SetDocPermissionSetEx
--
--
-- Procedure Create
-- dbo.SetDocPermissionSetEx
--
CREATE PROCEDURE SetDocPermissionSetEx @meta_id INT, @set_id INT, @permission_id INT, @permission_data INT AS

/*
	Updates an extended permissionset for a document.
*/

-- Insert new value
INSERT INTO	doc_permission_sets_ex
VALUES	(@meta_id,@set_id,@permission_id, @permission_data)
go

--
-- Procedure Create
-- imse.SetNewDocPermissionSet
--
--
-- Procedure Create
-- dbo.SetNewDocPermissionSet
--
CREATE PROCEDURE SetNewDocPermissionSet @meta_id INT, @set_id INT, @permission_id INT AS

/*
	Updates a permissionset for a document.
*/

-- Delete the previous value
DELETE FROM new_doc_permission_sets
WHERE	meta_id = @meta_id
AND		set_id = @set_id

-- Insert new value
INSERT INTO	new_doc_permission_sets
VALUES	(@meta_id,@set_id,@permission_id)
go

--
-- Procedure Create
-- imse.SetNewDocPermissionSetEx
--
--
-- Procedure Create
-- dbo.SetNewDocPermissionSetEx
--
CREATE PROCEDURE SetNewDocPermissionSetEx @meta_id INT, @set_id INT, @permission_id INT, @permission_data INT AS

/*
	Updates an extended permissionset for a document.
*/

-- Insert new value
INSERT INTO	new_doc_permission_sets_ex
VALUES	(@meta_id,@set_id,@permission_id, @permission_data)
go

--
-- Procedure Create
-- imse.SetRoleDocPermissionSetId
--
--
-- Procedure Create
-- dbo.SetRoleDocPermissionSetId
--
CREATE PROCEDURE SetRoleDocPermissionSetId @role_id INT, @meta_id INT, @set_id INT AS

-- First delete the previous set_id
DELETE FROM 		roles_rights 
WHERE 		meta_id = @meta_id
		AND 	role_id = @role_id


-- Now insert the new one
IF @set_id < 4
BEGIN
	INSERT INTO roles_rights (role_id, meta_id, set_id)
	VALUES ( @role_id, @meta_id, @set_id )
END
go

--
-- Procedure Extended Alter
-- imse.TestJanusDB
--
EXEC sp_rename 'imse.TestJanusDB','TestJanusD_12192000100945000',OBJECT
go
--
-- Procedure Create
-- dbo.TestJanusDB
--
CREATE PROCEDURE TestJanusDB

AS

select 'Hurra!'
go
IF OBJECT_ID('imse.TestJanusDB') IS NOT NULL
     DROP PROCEDURE imse.TestJanusD_12192000100945000
ELSE 
     EXEC sp_rename 'imse.TestJanusD_12192000100945000','TestJanusDB',OBJECT
go

--
-- Procedure Create
-- imse.UpdateParentsDateModified
--
--
-- Procedure Create
-- dbo.UpdateParentsDateModified
--
CREATE PROCEDURE [UpdateParentsDateModified] @meta_id INT AS

UPDATE meta
SET date_modified = GETDATE() 
FROM meta JOIN childs c
ON meta.meta_id = c.meta_id 
WHERE c.to_meta_id = @meta_id
go

--
-- Procedure Extended Alter
-- imse.UpdateTemplateTextsAndImages
--
EXEC sp_rename 'imse.UpdateTemplateTextsAndImages','UpdateTemp_12192000100945000',OBJECT
go
--
-- Procedure Create
-- dbo.UpdateTemplateTextsAndImages
--
CREATE PROCEDURE UpdateTemplateTextsAndImages
@t_id int AS

declare @new_no_txt int
declare @new_no_img int

select @new_no_txt = no_of_txt, @new_no_img = no_of_img from templates where template_id = @t_id

declare tmp cursor for
select td.meta_id,max(t.name),max(i.name) from text_docs td
left join texts t on td.meta_id = t.meta_id
left join images i on td.meta_id = i.meta_id
where td.template_id = @t_id
group by td.meta_id
having max(t.name) < @new_no_txt
or max(i.name) < @new_no_img

open tmp
declare @meta_id int
declare @max_txt int
declare @max_img int
fetch next from tmp
into @meta_id,@max_txt,@max_img

while @@fetch_status = 0
begin
	declare @no_txt int	
	declare @no_img int
	set @no_txt = @max_txt
	set @no_img = @max_img
	while @no_txt < @new_no_txt
	begin
		set @no_txt = @no_txt + 1
		insert into texts values (@meta_id,@no_txt,'',1)
	end
	while @no_img < @new_no_img
	begin
		set @no_img = @no_img + 1
		insert into images values (@meta_id,0,0,0,0,0,@no_img,'','_self','','_top','','','','')
	end
	fetch next from tmp
	into @meta_id,@max_txt,@max_img
end

close tmp
deallocate tmp
go
IF OBJECT_ID('imse.UpdateTemplateTextsAndImages') IS NOT NULL
     DROP PROCEDURE imse.UpdateTemp_12192000100945000
ELSE 
     EXEC sp_rename 'imse.UpdateTemp_12192000100945000','UpdateTemplateTextsAndImages',OBJECT
go

--
-- Procedure Extended Alter
-- imse.getBrowserDocChilds
--
EXEC sp_rename 'imse.getBrowserDocChilds','getBrowser_12192000100946000',OBJECT
go
--
-- Procedure Create
-- dbo.getBrowserDocChilds
--
CREATE PROCEDURE getBrowserDocChilds @meta_id int, @user_id int AS
-- Lists the childs for a specific browser_doc
SELECT DISTINCT	to_meta_id,
			meta_headline
FROM	browser_docs bd
JOIN meta m
						ON 	bd.to_meta_id = m.meta_id
						AND 	bd.meta_id = @meta_id
LEFT JOIN roles_rights rr
						ON	rr.meta_id = m.meta_id
						AND	rr.set_id < 4
JOIN user_roles_crossref urc
						ON	urc.user_id = @user_id
						AND (
								urc.role_id = 0
							OR	urc.role_id = rr.role_id
							OR	m.shared = 1
						)
WHERE m.activate = 1
ORDER BY to_meta_id
go
IF OBJECT_ID('imse.getBrowserDocChilds') IS NOT NULL
     DROP PROCEDURE imse.getBrowser_12192000100946000
ELSE 
     EXEC sp_rename 'imse.getBrowser_12192000100946000','getBrowserDocChilds',OBJECT
go

--
-- Procedure Extended Alter
-- imse.getDocs
--
EXEC sp_rename 'imse.getDocs','getDocs_12192000100946000',OBJECT
go
--
-- Procedure Create
-- dbo.getDocs
--
CREATE PROCEDURE getDocs @user_id int, @start int, @end int AS

-- Lists documents user is allowed to see.

SELECT DISTINCT	m.meta_id,
			COUNT(DISTINCT c.meta_id) parentcount,
			meta_headline,
			doc_type
FROM			meta m
LEFT JOIN		childs c			ON	c.to_meta_id = m.meta_id
LEFT JOIN		roles_rights rr		ON	rr.meta_id = m.meta_id
						AND	rr.set_id < 4
JOIN			user_roles_crossref urc	ON	urc.user_id = @user_id
						AND	(
								urc.role_id = 0
							OR	(
									urc.role_id = rr.role_id
								)
							OR	m.shared = 1
							)
WHERE		m.activate = 1
		AND	m.meta_id > (@start-1) 
		AND	m.meta_id < (@end+1)
GROUP BY		m.meta_id,m.meta_headline,m.doc_type,c.to_meta_id
ORDER BY		m.meta_id
go
IF OBJECT_ID('imse.getDocs') IS NOT NULL
     DROP PROCEDURE imse.getDocs_12192000100946000
ELSE 
     EXEC sp_rename 'imse.getDocs_12192000100946000','getDocs',OBJECT
go

--
-- Procedure Extended Alter
-- imse.getMenuDocChilds
--
EXEC sp_rename 'imse.getMenuDocChilds','getMenuDoc_12192000100946000',OBJECT
go
--
-- Procedure Create
-- dbo.getMenuDocChilds
--
CREATE PROCEDURE getMenuDocChilds @meta_id int, @user_id int AS
-- Lists the childs of menudoc @meta_id
SELECT DISTINCT	to_meta_id,
			meta_headline
FROM		childs c
JOIN		meta m
					ON	c.to_meta_id = m.meta_id
	      				AND	c.meta_id = @meta_id
LEFT JOIN	roles_rights rr
					ON	rr.meta_id = m.meta_id
					AND	rr.set_id < 4
JOIN		user_roles_crossref urc
					ON	urc.user_id = @user_id
      					AND (		urc.role_id = 0
						OR	urc.role_id = rr.role_id
						OR 	m.shared = 1
					)
WHERE m.activate = 1
ORDER BY to_meta_id
go
IF OBJECT_ID('imse.getMenuDocChilds') IS NOT NULL
     DROP PROCEDURE imse.getMenuDoc_12192000100946000
ELSE 
     EXEC sp_rename 'imse.getMenuDoc_12192000100946000','getMenuDocChilds',OBJECT
go

--
-- Procedure Extended Alter
-- imse.getTemplategroups
--
EXEC sp_rename 'imse.getTemplategroups','getTemplat_12192000100946000',OBJECT
go
--
-- Procedure Create
-- dbo.getTemplategroups
--
CREATE PROCEDURE getTemplategroups AS
select group_id,group_name from templategroups order by group_name
go
IF OBJECT_ID('imse.getTemplategroups') IS NOT NULL
     DROP PROCEDURE imse.getTemplat_12192000100946000
ELSE 
     EXEC sp_rename 'imse.getTemplat_12192000100946000','getTemplategroups',OBJECT
go

--
-- Procedure Extended Alter
-- imse.getTemplates
--
EXEC sp_rename 'imse.getTemplates','getTemplat_12192000100947000',OBJECT
go
--
-- Procedure Create
-- dbo.getTemplates
--
CREATE PROCEDURE getTemplates AS
select template_id, simple_name from templates
go
IF OBJECT_ID('imse.getTemplates') IS NOT NULL
     DROP PROCEDURE imse.getTemplat_12192000100947000
ELSE 
     EXEC sp_rename 'imse.getTemplat_12192000100947000','getTemplates',OBJECT
go

--
-- Procedure Extended Alter
-- imse.getTemplatesInGroup
--
EXEC sp_rename 'imse.getTemplatesInGroup','getTemplat_12192000100947000',OBJECT
go
--
-- Procedure Create
-- dbo.getTemplatesInGroup
--
CREATE PROCEDURE getTemplatesInGroup @grp_id INT AS
SELECT	t.template_id,simple_name
FROM		templates t	JOIN
		templates_cref c
ON		t.template_id = c.template_id
WHERE	c.group_id = @grp_id
ORDER BY	simple_name
go
IF OBJECT_ID('imse.getTemplatesInGroup') IS NOT NULL
     DROP PROCEDURE imse.getTemplat_12192000100947000
ELSE 
     EXEC sp_rename 'imse.getTemplat_12192000100947000','getTemplatesInGroup',OBJECT
go

--
-- Procedure Extended Alter
-- imse.magnustest
--
EXEC sp_rename 'imse.magnustest','magnustest_12192000100947000',OBJECT
go
--
-- Procedure Create
-- dbo.magnustest
--
CREATE PROCEDURE magnustest 


@text varchar(80)

AS 

update texts
set text = @text
where meta_id = 4260
and name = 1
go
IF OBJECT_ID('imse.magnustest') IS NOT NULL
     DROP PROCEDURE imse.magnustest_12192000100947000
ELSE 
     EXEC sp_rename 'imse.magnustest_12192000100947000','magnustest',OBJECT
go

--
-- Procedure Extended Alter
-- imse.phoneNbrAdd
--
EXEC sp_rename 'imse.phoneNbrAdd','phoneNbrAd_12192000100947000',OBJECT
go
--
-- Procedure Create
-- dbo.phoneNbrAdd
--
CREATE PROCEDURE phoneNbrAdd
/*
This function adds a new phone numbers to the db. Used by AdminUserPhones
*/
	@user_id int,
	@country varchar(15) ,
	@area varchar(15) , 
	@nbr varchar(15)
AS

DECLARE @newPhoneId int
SELECT @newPhoneId = MAX(phone_id) + 1
FROM phones

IF @newPhoneId IS NULL 
	SET @newPhoneId = 1

INSERT INTO PHONES ( phone_id , country_code, area_code, number , user_id )
VALUES (@newPhoneId , @country, @area,  @nbr, @user_id )
go
IF OBJECT_ID('imse.phoneNbrAdd') IS NOT NULL
     DROP PROCEDURE imse.phoneNbrAd_12192000100947000
ELSE 
     EXEC sp_rename 'imse.phoneNbrAd_12192000100947000','phoneNbrAdd',OBJECT
go

--
-- Procedure Extended Alter
-- imse.test
--
EXEC sp_rename 'imse.test','test_12192000100948000',OBJECT
go
--
-- Procedure Create
-- dbo.test
--
CREATE PROCEDURE test AS

SELECT COUNT(usr.role_id) , (RTRIM(last_name) + ', ' + RTRIM(first_name))   
FROM user_roles_crossref usr, roles r, users u
WHERE usr.ROLE_ID = 5 
AND usr.user_id = u.user_id
GROUP BY (RTRIM(last_name) + ', ' + RTRIM(first_name)), usr.role_id
go
IF OBJECT_ID('imse.test') IS NOT NULL
     DROP PROCEDURE imse.test_12192000100948000
ELSE 
     EXEC sp_rename 'imse.test_12192000100948000','test',OBJECT
go

--
-- Procedure Create
-- imse.testProc
--
CREATE PROCEDURE testProc AS
select * from meta
go

--
-- Procedure Create
-- imse.AddBrowserStatistics
--
--
-- Procedure Create
-- dbo.AddBrowserStatistics
--
CREATE PROCEDURE AddBrowserStatistics @os VARCHAR(30), @browser varchar(30), @version varchar(30) AS

DECLARE @newline CHAR(2)
SET @newline = CHAR(13)+CHAR(10)

DECLARE @browserstring VARCHAR(120)
SET @browserstring = 	'Os: '+@os+@newline+
			'Browser: '+@browser+@newline+
			'Version: '+@version

EXEC AddStatistics @browserstring
go

--
-- Procedure Create
-- imse.AddScreenStatistics
--
--
-- Procedure Create
-- dbo.AddScreenStatistics
--
CREATE PROCEDURE AddScreenStatistics @width INT, @height INT, @bits INT AS

DECLARE @screen VARCHAR(20) 
SET @screen = 'Screen: '+LTRIM(STR(@width))+'x'+LTRIM(STR(@height))+'x'+LTRIM(STR(@bits))

EXEC AddStatistics @screen
go

--
-- Procedure Create
-- imse.AddStatisticsCount
--
--
-- Procedure Create
-- dbo.AddStatisticsCount
--
CREATE PROCEDURE AddStatisticsCount AS

EXEC AddStatistics 'Count'
go

--
-- Procedure Create
-- imse.AddVersionStatistics
--
--
-- Procedure Create
-- dbo.AddVersionStatistics
--
CREATE PROCEDURE AddVersionStatistics @name VARCHAR(30), @version VARCHAR(30) AS

DECLARE @string VARCHAR(62)
SET @string = @name+': '+@version

EXEC AddStatistics @string
go

--
-- Procedure Extended Alter
-- imse.Classification_Fix
--
EXEC sp_rename 'imse.Classification_Fix','Classifica_12192000100928000',OBJECT
go
--
-- Procedure Create
-- dbo.Classification_Fix
--
CREATE PROCEDURE Classification_Fix
	@meta_id int ,
	@string varchar(2000)
AS

declare @value varchar(50)
declare @pos int

-- Lets delete all current crossreferences, if any
DELETE 
FROM meta_classification 
WHERE meta_id = @meta_id

--SELECT @string = 'ett;två;tre;fyra;fem'
-- Lets search for semicolon, if not found then look for a , This is relevant 
-- when we convert the db. After convertion, only look for semicolons
SELECT @pos = PATINDEX('%;%', @string)
IF( @pos = 0 ) BEGIN
	SELECT @pos = PATINDEX('%,%', @string)
END
WHILE @pos > 0
BEGIN
	SELECT @value = LEFT(@string,@pos-1)
	SELECT @pos = LEN(@string) - @pos
	SELECT @string = RIGHT(@string,@pos)
	SELECT  @value  = lTrim(rTrim( ( @value ) ))	

	EXEC ClassificationAdd @meta_id , @value
	--INSERT INTO data (value) VALUES (@value)
	SELECT @pos = PATINDEX('%;%', @string)
	-- PRINT @value
END
-- Lets get the last part of the string
--PRINT @string
SELECT @value = @string
SELECT  @value  = lTrim(rTrim( ( @value ) ))	
EXEC ClassificationAdd @meta_id , @value

-- INSERT INTO data (value) VALUES (@string)
go
IF OBJECT_ID('imse.Classification_Fix') IS NOT NULL
     DROP PROCEDURE imse.Classifica_12192000100928000
ELSE 
     EXEC sp_rename 'imse.Classifica_12192000100928000','Classification_Fix',OBJECT
go

