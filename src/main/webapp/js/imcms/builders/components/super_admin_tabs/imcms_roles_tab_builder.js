/**
 * @author Serhii Maksymchuk from Ubrainians for imCode
 * 23.06.18
 */
Imcms.define(
    'imcms-roles-tab-builder',
    ['imcms-super-admin-tab', 'imcms-i18n-texts', 'jquery'],
    function (SuperAdminTab, texts, $) {

        texts = texts.superAdmin.roles;

        function buildRolesContainer() {
            return $('<div>', {text: 'test'})
        }

        return new SuperAdminTab(texts.name, [
            buildRolesContainer()
        ]);
    }
);
