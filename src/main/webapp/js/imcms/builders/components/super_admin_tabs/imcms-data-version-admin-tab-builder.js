/**
 * @author Serhii Maksymchuk from Ubrainians for imCode
 * 23.06.18
 */
define(
    'imcms-data-version-admin-tab-builder',
    ['imcms-super-admin-tab', 'imcms-i18n-texts'],
    function (SuperAdminTab, texts) {

        texts = texts.superAdmin.dataVersion;

        return new SuperAdminTab(texts.name, []);
    }
);
