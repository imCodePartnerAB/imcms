SET @schema_version__major_new = 6;
SET @schema_version__minor_new = 85;

insert into imcms_languages (id, code, name, native_name, enabled)
values (null, 'be', 'Belarusian', 'Беларуская мова', 1);
insert into imcms_languages (id, code, name, native_name, enabled)
values (null, 'bs', 'Bosnian', 'Bosanski jezik', 1);
insert into imcms_languages (id, code, name, native_name, enabled)
values (null, 'bg', 'Bulgarian', 'български език', 1);
insert into imcms_languages (id, code, name, native_name, enabled)
values (null, 'zh', 'Chinese', '中文 (Zhōngwén), 汉语, 漢語', 1);
insert into imcms_languages (id, code, name, native_name, enabled)
values (null, 'hr', 'Croatian', 'hrvatski jezik', 1);
insert into imcms_languages (id, code, name, native_name, enabled)
values (null, 'cs', 'Czech', 'čeština, český jazyk', 1);
insert into imcms_languages (id, code, name, native_name, enabled)
values (null, 'da', 'Danish', 'dansk', 1);
insert into imcms_languages (id, code, name, native_name, enabled)
values (null, 'nl', 'Dutch, Flemish', 'Nederlands, Vlaams', 1);
insert into imcms_languages (id, code, name, native_name, enabled)
values (null, 'et', 'Estonian', 'eesti, eesti keel', 1);
insert into imcms_languages (id, code, name, native_name, enabled)
values (null, 'fi', 'Finnish', 'suomi, suomen kieli', 1);
insert into imcms_languages (id, code, name, native_name, enabled)
values (null, 'fr', 'French', 'français, langue française', 1);
insert into imcms_languages (id, code, name, native_name, enabled)
values (null, 'de', 'German', 'Deutsch', 1);
insert into imcms_languages (id, code, name, native_name, enabled)
values (null, 'el', 'Greek, Modern (1453–)', 'ελληνικά', 1);
insert into imcms_languages (id, code, name, native_name, enabled)
values (null, 'hu', 'Hungarian', 'magyar', 1);
insert into imcms_languages (id, code, name, native_name, enabled)
values (null, 'ga', 'Irish', 'Gaeilge', 1);
insert into imcms_languages (id, code, name, native_name, enabled)
values (null, 'is', 'Icelandic', 'Íslenska', 1);
insert into imcms_languages (id, code, name, native_name, enabled)
values (null, 'it', 'Italian', 'Italiano', 1);
insert into imcms_languages (id, code, name, native_name, enabled)
values (null, 'ja', 'Japanese', '日本語 (にほんご)', 1);
insert into imcms_languages (id, code, name, native_name, enabled)
values (null, 'kk', 'Kazakh', 'қазақ тілі', 1);
insert into imcms_languages (id, code, name, native_name, enabled)
values (null, 'lb', 'Luxembourgish, Letzeburgesch', 'Lëtzebuergesch', 1);
insert into imcms_languages (id, code, name, native_name, enabled)
values (null, 'lt', 'Lithuanian', 'lietuvių kalba', 1);
insert into imcms_languages (id, code, name, native_name, enabled)
values (null, 'lv', 'Latvian', 'latviešu valoda', 1);
insert into imcms_languages (id, code, name, native_name, enabled)
values (null, 'mn', 'Mongolian', 'Монгол хэл', 1);
insert into imcms_languages (id, code, name, native_name, enabled)
values (null, 'no', 'Norwegian', 'Norsk', 1);
insert into imcms_languages (id, code, name, native_name, enabled)
values (null, 'pl', 'Polish', 'język polski, polszczyzna', 1);
insert into imcms_languages (id, code, name, native_name, enabled)
values (null, 'pt', 'Portuguese', 'Português', 1);
insert into imcms_languages (id, code, name, native_name, enabled)
values (null, 'ro', 'Romanian, Moldavian, Moldovan', 'Română', 1);
insert into imcms_languages (id, code, name, native_name, enabled)
values (null, 'ru', 'Russian', 'Russia', 1);
insert into imcms_languages (id, code, name, native_name, enabled)
values (null, 'sr', 'Serbian', 'српски језик', 1);
insert into imcms_languages (id, code, name, native_name, enabled)
values (null, 'gd', 'Gaelic, Scottish Gaelic', 'Gàidhlig', 1);
insert into imcms_languages (id, code, name, native_name, enabled)
values (null, 'sk', 'Slovak', 'Slovenčina, Slovenský Jazyk', 1);
insert into imcms_languages (id, code, name, native_name, enabled)
values (null, 'sl', 'Slovenian', 'Slovenski Jezik, Slovenščina', 1);
insert into imcms_languages (id, code, name, native_name, enabled)
values (null, 'es', 'Spanish, Castilian', 'Español', 1);
insert into imcms_languages (id, code, name, native_name, enabled)
values (null, 'tr', 'Turkish', 'Türkçe', 1);
insert into imcms_languages (id, code, name, native_name, enabled)
values (null, 'uk', 'Ukrainian', 'Українська', 1);
insert into imcms_languages (id, code, name, native_name, enabled)
values (null, 'sma', 'Southern Sami', 'Åarjelsaemien', 1);
insert into imcms_languages (id, code, name, native_name, enabled)
values (null, 'smj', 'Lule Sami', 'Julevsámegielas', 1);

DELETE from imcms_test.imcms_languages where id > 2;

UPDATE database_version
SET major = @schema_version__major_new,
    minor = @schema_version__minor_new;