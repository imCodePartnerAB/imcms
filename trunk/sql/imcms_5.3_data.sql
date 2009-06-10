
SET sql_mode = 'NO_AUTO_VALUE_ON_ZERO';

-- ============================================================================
-- Constants section
-- ============================================================================

-- Default start page id (aka document meta id)
SET @start_document__meta_id = 1001;

-- Predefined system's users identities
SET @user_id__admin = 1;
SET @user_id__user = 2;

-- Prederined system's roles identities
SET @role_id__superadmin = 0;
SET @role_id__useradmin = 1;
SET @role_id__users = 2;

-- ============================================================================
-- Variables section
-- ============================================================================

-- User interface language, should be assigned outside
-- In v.4.x it is assigned at the build time by filtering @language@ string in init.sql file.
-- Possible values: 'eng', 'swe'
SET @ui__language = 'eng';

-- Database schema version
SET @database_version__major = 5;
SET @database_version__minor = 3;

-- Predefined i18n languages indentities
SET @language_id__english = 1;
SET @language_id__swedish = 2;
SET @language_id__default = @language_id__english;

-- Initial values for start page in English
SET @start_document__headline__english = 'Start page';
SET @start_document__text1__english = '<h3>Welcome to imCMS</h3>';
SET @start_document__text2__english = '<a href="<?imcms:contextpath?>/login/" title="Link to login-page"><b>Log in!</b></a><br><br><br>
  <a href="<?imcms:contextpath?>/servlet/SearchDocuments" target="_blank" title="Link to search-page"><b>Search-page</b></a><br>
  Opens in new window.<br><br>
  <a href="@documentationwebappurl@/" target="_blank" title="Link to documentation-site"><b>Documentation</b></a><br>
  External site, opens in new window.<br><br>
  <a href="http://www.imcms.net" target="_blank" title="Link to www.imcms.net"><b>More about imCMS</b></a><br>
  imCMS Product-site. External site, opens in new window.<br><br>
  <a href="<?imcms:contextpath?>/imcms/docs/" target="_blank" title="Link to included documentation."><b>Included documentation.</b></a><br>
  For administrators and developers, in english. Opens in new window.<br><br>
  <a href="<?imcms:contextpath?>/imcms/docs/apisamples/" target="_blank" title="Link to API-samples"><b>API examples</b></a><br>
  Only for developers, in english. Opens in new window.<br>
  Note! Only to be used in test environment - not sites for public use.';

-- Initial values for start page in Swedish
SET @start_document__headline__swedish = 'Startsidan';
SET @start_document__text1__swedish = '<h3>Välkommen till imCMS</h3>';
SET @start_document__text2__swedish = '<a href="<?imcms:contextpath?>/login/" title="Länk till inloggningssida"><b>Logga in!</b></a><br><br><br>
  <a href="<?imcms:contextpath?>/servlet/SearchDocuments" target="_blank" title="Länk till söksida"><b>Söksida</b></a><br>
  Öppnas i nytt fönster.<br><br>
  <a href="@documentationwebappurl@/" target="_blank" title="Länk till dokumentationssajt"><b>Dokumentation</b></a><br>
  Extern sajt, öppnas i nytt fönster.<br><br>
  <a href="http://www.imcms.net" target="_blank" title="Länk till www.imcms.net"><b>Mer om imCMS</b></a><br>
  Produktsajt om imCMS. Extern sajt, öppnas i nytt fönster.<br><br>
  <a href="<?imcms:contextpath?>/imcms/docs/" target="_blank" title="Länk till medföljande dokumentation."><b>Medföljande dokumentation</b></a><br>
  För administratörer och utvecklare, på engelska. Öppnas i nytt fönster.<br><br>
  <a href="<?imcms:contextpath?>/imcms/docs/apisamples/" target="_blank" title="Länk till API-exempel"><b>API-exempel</b></a><br>
  Endast för utvecklare, på engelska. Öppnas i nytt fönster.<br>
  OBS! Endast för testmiljö - använd inte på publika sajter.';


-- ============================================================================
-- Data section
-- ============================================================================

--
-- Data for table users
--

INSERT INTO users
  (user_id, login_name, login_password, first_name, last_name, title, company, address, city, zip, country, county_council, email, external, active, create_date, language, session_id)
VALUES
  (@user_id__admin, 'admin', 'admin', 'Admin', 'Super', '', '', '', '', '', '', '', '', 0, 1, NOW(), @'ui__language', NULL),
  (@user_id__user, 'user', 'user', 'User', 'Extern', '', '', '', '', '', '', '', '', 0, 1, NOW(), @'ui__language', NULL);

--
-- Data for table meta
--

INSERT INTO meta
  (meta_id, doc_type, owner_id, permissions, shared, show_meta, lang_prefix, date_created, date_modified, disable_search, target, activate, archived_datetime, publisher_id, status, publication_start_datetime, publication_end_datetime, missing_i18n_show_rule)
VALUES
  (@start_document__meta_id, 2, @user_id__admin, 0, 0, 0, @'ui__language', NOW(), NOW(), 0, '_self', 1, NULL, NULL, 2, NOW(), NULL, 'DO_NOT_SHOW');

--
-- Data for table browsers
--

INSERT INTO browsers
  (browser_id, name, user_agent, value)
VALUES
  (1, 'Internet Explorer', '%MSIE%', 2),
  (2, 'Netscape', 'Mozilla%(%;%[UIN][);]%', 2),
  (3, 'Internet Explorer 3', '%MSIE 3%', 4),
  (4, 'Internet Explorer 4', '%MSIE 4%', 4),
  (5, 'Internet Explorer 5', '%MSIE 5%', 4),
  (6, 'Internet Explorer 6', '%MSIE 6%', 4),
  (7, 'Netscape 3', 'Mozilla/3%(%;%[UIN][ );]%', 4),
  (8, 'Netscape 4', 'Mozilla/4%(%;%[UIN][ );]%', 4),
  (9, 'Netscape 6', 'Mozilla/5%(%;%[UIN][ );]%', 4),
  (10, 'Netscape 7', 'Mozilla%/5;%netscape/7%', 4),
  (11, 'Windows', '%win%', 1),
  (12, 'Macintosh', '%mac%', 1),
  (13, 'Windows Internet Explorer', '%MSIE%win%', 3),
  (14, 'Windows Internet Explorer 3', '%MSIE 3%win%', 5),
  (15, 'Windows Internet Explorer 4', '%MSIE 4%win%', 5),
  (16, 'Windows Internet Explorer 5.0', '%MSIE 5.0%win%', 5),
  (17, 'Windows Internet Explorer 5.5', '%MSIE 5.5%win%', 5),
  (18, 'Windows Internet Explorer 6', '%MSIE 6%win%', 5),
  (19, 'Windows Netscape', 'Mozilla%(%win%;%[UIN][ );]%', 3),
  (20, 'Windows Netscape 3', 'Mozilla/3%(%win%;%[UIN][ );]%', 5),
  (21, 'Windows Netscape 4', 'Mozilla/4%(%win%;%[UIN][ );]%', 5),
  (22, 'Windows Netscape 6', 'Mozilla/5%(%win%;%[UIN][ );]%', 5),
  (23, 'Windows Netscape 7', 'Mozilla/5%(%win%netscape/7%', 5),
  (24, 'Macintosh Internet Explorer', '%MSIE%mac%', 3),
  (25, 'Macintosh Internet Explorer 3', '%MSIE 3%mac%', 5),
  (26, 'Macintosh Internet Explorer 4', '%MSIE 4%mac%', 5),
  (27, 'Macintosh Internet Explorer 5', '%MSIE 5%mac%', 5),
  (28, 'Macintosh Netscape', 'Mozilla%(%mac%;%[UIN][ );]%', 3),
  (29, 'Macintosh Netscape 3', 'Mozilla/3%(%mac%;%[UIN][ );]%', 5),
  (30, 'Macintosh Netscape 4', 'Mozilla/4%(%mac%;%[UIN][ );]%', 5),
  (31, 'Macintosh Netscape 6', 'Mozilla/5%(%mac%;%[UIN][ );]%', 5);

--
-- Data for table database_version
--

INSERT INTO database_version
  (major, minor)
VALUES
  (@database_version__major, @database_version__minor);

--
-- Data for table doc_permissions
--

INSERT INTO doc_permissions
  (permission_id, doc_type, lang_prefix, description)
VALUES
  (65536, 2, 'eng', 'Edit texts'),
  (65536, 2, 'swe', '�ndra text'),
  (65536, 5, 'eng', 'Edit'),
  (65536, 5, 'swe', 'Redigera'),
  (65536, 6, 'eng', 'Edit'),
  (65536, 6, 'swe', 'Redigera'),
  (65536, 7, 'eng', 'Edit'),
  (65536, 7, 'swe', 'Redigera'),
  (65536, 8, 'eng', 'Edit'),
  (65536, 8, 'swe', 'Redigera'),
  (65536, 101, 'eng', 'Edit'),
  (65536, 101, 'swe', 'Redigera'),
  (65536, 102, 'eng', 'Edit'),
  (65536, 102, 'swe', 'Redigera'),
  (131072, 2, 'eng', 'Edit pictures'),
  (131072, 2, 'swe', '�ndra bild'),
  (262144, 2, 'eng', 'Edit menus'),
  (262144, 2, 'swe', '�ndra meny'),
  (524288, 2, 'eng', 'Change template'),
  (524288, 2, 'swe', '�ndra utseende'),
  (1048576, 2, 'eng', 'Change include'),
  (1048576, 2, 'swe', '�ndra include');

--
-- Data for table doc_types
--

INSERT INTO doc_types
  (doc_type, lang_prefix, type)
VALUES
  (2, 'eng', 'Text page'),
  (2, 'swe', 'Textsida'),
  (5, 'eng', 'External link'),
  (5, 'swe', 'Extern l�nk'),
  (6, 'eng', 'Browser controlled link'),
  (6, 'swe', 'Browserkontroll'),
  (7, 'eng', 'HTML-document'),
  (7, 'swe', 'HTML-dokument'),
  (8, 'eng', 'File'),
  (8, 'swe', 'Fil');

--
-- Data for table i18n_languages
--

INSERT INTO i18n_languages
  (language_id, code, name, native_name)
VALUES
  (@language_id__english, 'en', 'English', 'English'),
  (@language_id__swedish, 'sv', 'Swedish', 'Svenska');

UPDATE i18n_languages SET is_default = true WHERE language_id = @language_id__default;

--
-- Data for table i18n_meta
--

INSERT INTO i18n_meta
  (i18n_meta_id, meta_id, language_id, meta_enabled, meta_headline, meta_text, meta_image)
VALUES
  (1, @start_document__meta_id, @language_id__english, true, @'start_document__headline__english', '', ''),
  (2, @start_document__meta_id, @language_id__swedish, true, @'start_document__headline__swedish', '', '');

--
-- Data for table images
--

INSERT INTO images
  (image_id, meta_id, language_id, width, height, border, v_space, h_space, name, image_name, target, align, alt_text, low_scr, imgurl, linkurl, type)
VALUES
  (1, @start_document__meta_id, @language_id__english, 0, 0, 0, 0, 0, 3, '', '_blank', 'top', '', '', 'imCMSpower.gif', 'http://www.imcms.net', 0),
  (2, @start_document__meta_id, @language_id__swedish, 0, 0, 0, 0, 0, 3, '', '_blank', 'top', '', '', 'imCMSpower.gif', 'http://www.imcms.net', 0);

--
-- Data for table lang_prefixes
--

INSERT INTO lang_prefixes
  (lang_id, lang_prefix)
VALUES
  (1, 'swe'),
  (2, 'eng');

--
-- Data for table languages
--

INSERT INTO languages
  (lang_prefix, user_prefix, language)
VALUES
  ('eng', 'eng', 'English'),
  ('eng', 'swe', 'Engelska'),
  ('swe', 'eng', 'Swedish'),
  ('swe', 'swe', 'Svenska');

--
-- Data for table mime_types
--

INSERT INTO mime_types
  (mime_id, mime_name, mime, lang_prefix)
VALUES
  (0, 'Other...', 'other', 'eng'),
  (0, 'Annan...', 'other', 'swe'),
  (1, 'Plain text', 'text/plain', 'eng'),
  (1, 'Vanlig text', 'text/plain', 'swe'),
  (2, 'HTML-document', 'text/html', 'eng'),
  (2, 'HTML-dokument', 'text/html', 'swe'),
  (3, 'Binary file', 'application/octet-stream', 'eng'),
  (3, 'Bin�rfil', 'application/octet-stream', 'swe'),
  (4, 'Shockwave Flash', 'application/x-shockwave-flash', 'eng'),
  (4, 'Shockwave Flash', 'application/x-shockwave-flash', 'swe'),
  (5, 'Shockwave Director', 'application/x-director', 'eng'),
  (5, 'Shockwave Director', 'application/x-director', 'swe'),
  (6, 'PNG-image', 'image/png', 'eng'),
  (6, 'PNG-bild', 'image/png', 'swe'),
  (7, 'GIF-image', 'image/gif', 'eng'),
  (7, 'GIF-bild', 'image/gif', 'swe'),
  (8, 'JPEG-image', 'image/jpeg', 'eng'),
  (8, 'JPEG-bild', 'image/jpeg', 'swe'),
  (9, 'Adobe Acrobat-document', 'application/pdf', 'eng'),
  (9, 'Adobe Acrobat-dokument', 'application/pdf', 'swe'),
  (10, 'Wav-sound', 'audio/x-wav', 'eng'),
  (10, 'Wav-ljud', 'audio/x-wav', 'swe'),
  (11, 'Zip-file', 'application/zip', 'eng'),
  (11, 'Zip-fil', 'application/zip', 'swe'),
  (12, 'AVI-movie', 'video/x-msvideo', 'eng'),
  (12, 'AVI-film', 'video/x-msvideo', 'swe'),
  (13, 'Quicktime-movie', 'video/quicktime', 'eng'),
  (13, 'Quicktime-film', 'video/quicktime', 'swe'),
  (14, 'MPEG-movie', 'video/mpeg', 'eng'),
  (14, 'MPEG-film', 'video/mpeg', 'swe'),
  (15, 'MS Word-document', 'application/msword', 'eng'),
  (15, 'MS Word-dokument', 'application/msword', 'swe'),
  (16, 'MS Excel-document', 'application/vnd.ms-excel', 'eng'),
  (16, 'MS Excel-dokument', 'application/vnd.ms-excel', 'swe'),
  (17, 'MS Powerpoint-document', 'application/vnd.ms-powerpoint', 'eng'),
  (17, 'MS Powerpoint-dokument', 'application/vnd.ms-powerpoint', 'swe');

--
-- Data for table permission_sets
--

INSERT INTO permission_sets
  (set_id, description)
VALUES
  (0, 'Full'),
  (1, 'Begr�nsad 1'),
  (2, 'Begr�nsad 2'),
  (3, 'L�s');

--
-- Data for table permissions
--

INSERT INTO permissions
  (permission_id, lang_prefix, description)
VALUES
  (1, 'eng', 'Edit headline'),
  (1, 'swe', '�ndra rubrik'),
  (2, 'eng', 'Edit docinfo'),
  (2, 'swe', '�ndra dokinfo'),
  (4, 'eng', 'Edit permissions'),
  (4, 'swe', '�ndra r�ttigheter f�r roller'),
  (8, 'eng', 'Create document'),
  (8, 'swe', 'Skapa dokument');

--
-- Data for table phonetypes
--

INSERT INTO phonetypes
  (phonetype_id, typename, lang_id)
VALUES
  (0, 'Annat', 1),
  (0, 'Other', 2),
  (1, 'Bostad', 1),
  (1, 'Home', 2),
  (2, 'Arbete', 1),
  (2, 'Work', 2),
  (3, 'Mobil', 1),
  (3, 'Mobile', 2),
  (4, 'Fax', 1),
  (4, 'Fax', 2);

--
-- Data for table roles
--

INSERT INTO roles
  (role_id, role_name, permissions, admin_role)
VALUES
  (@role_id__superadmin, 'Superadmin', 0, 1),
  (@role_id__useradmin, 'Useradmin', 0, 2),
  (@role_id__users, 'Users', 1, 0);

--
-- Data for table roles_rights
--

INSERT INTO roles_rights
  (role_id, meta_id, set_id)
VALUES
  (@role_id__users, @start_document__meta_id, 3);

--
-- Data for table sys_types
--

INSERT INTO sys_types
  (type_id, name)
VALUES
  (0, 'StartDocument'),
  (1, 'SessionCounter'),
  (2, 'SessionCounterDate'),
  (3, 'SystemMessage'),
  (4, 'ServerMaster'),
  (5, 'ServerMasterAddress'),
  (6, 'WebMaster'),
  (7, 'WebMasterAddress');

--
-- Data for table sys_data
--

INSERT INTO sys_data
  (sys_id, type_id, value)
VALUES
  (0, 0, @start_document__meta_id),
  (1, 1, 2),
  (2, 2, NOW()),
  (3, 3, ''),
  (4, 4, ''),
  (5, 5, ''),
  (6, 6, ''),
  (7, 7, '');

--
-- Data for table templategroups
--

INSERT INTO templategroups
  (group_id, group_name)
VALUES
  (0, 'normal');

--
-- Data for table templates_cref
--

INSERT INTO templates_cref
  (group_id, template_name)
VALUES
  (0, 'demo');

--
-- Data for table text_docs
--

INSERT INTO text_docs
  (meta_id, template_name, group_id, default_template_1, default_template_2, default_template)
VALUES
  (@start_document__meta_id, 'demo', 0, NULL, NULL, NULL);

--
-- Data for table texts
--

INSERT INTO texts
  (counter, meta_id, language_id, name, text, type)
VALUES
  (1, @start_document__meta_id, @language_id__english, 1, @'start_document__text1__english', 1),
  (2, @start_document__meta_id, @language_id__english, 2, @'start_document__text2__english', 1),
  (3, @start_document__meta_id, @language_id__swedish, 1, @'start_document__text1__swedish', 1),
  (4, @start_document__meta_id, @language_id__swedish, 2, @'start_document__text2__swedish', 1);

--
-- Data for table texts_history
--

INSERT INTO texts_history
  (meta_id, name, text, type, modified_datetime, user_id, language_id)
SELECT
  meta_id, name, text, type, NOW(), @user_id__admin, language_id
FROM
  texts;

--
-- Data for table user_roles_crossref
--

INSERT INTO user_roles_crossref
  (user_id, role_id)
VALUES
  (@user_id__admin, @role_id__superadmin),
  (@user_id__user, @role_id__users);