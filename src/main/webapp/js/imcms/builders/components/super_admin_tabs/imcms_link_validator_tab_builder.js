/**
 * @author Serhii Maksymchuk from Ubrainians for imCode
 * 23.06.18
 */
Imcms.define(
    'imcms-link-validator-tab-builder',
    ['imcms-super-admin-tab', 'imcms-i18n-texts'],
    function (SuperAdminTab, texts) {

        texts = texts.superAdmin.linkValidator;

        return new SuperAdminTab(texts.name, []);
    }
);
