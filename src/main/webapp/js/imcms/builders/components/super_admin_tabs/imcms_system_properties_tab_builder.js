/**
 * @author Serhii Maksymchuk from Ubrainians for imCode
 * 23.06.18
 */
Imcms.define(
    'imcms-system-properties-tab-builder',
    ['imcms-super-admin-tab', 'imcms-i18n-texts'],
    function (SuperAdminTab, texts) {

        texts = texts.superAdmin.systemProperties;

        return new SuperAdminTab(texts.name, []);
    }
);
