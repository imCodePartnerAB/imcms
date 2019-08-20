/**
 * @author Pavlenko Victor from Ubrainians for imCode
 * 20.08.19
 */
define(
    'imcms-menu-tab-builder',
    ['imcms-super-admin-tab', 'imcms-i18n-texts'],
    function (SuperAdminTab, texts) {

        texts = texts.superAdmin.menuTab;

        return new SuperAdminTab(texts.name, []);
    }
);
