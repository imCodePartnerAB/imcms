/**
 * @author Serhii Maksymchuk from Ubrainians for imCode
 * 23.06.18
 */
Imcms.define(
    'imcms-delete-docs-tab-builder',
    ['imcms-super-admin-tab', 'imcms-i18n-texts', 'jquery', 'imcms-field-wrapper', 'imcms-components-builder'],
    function (SuperAdminTab, texts, $, fieldWrapper, components) {

        texts = texts.superAdmin.deleteDocs;

        function buildTitle() {
            return fieldWrapper.wrap(components.texts.titleText('<div>', texts.title))
        }

        return new SuperAdminTab(texts.name, [
            buildTitle()
        ]);
    }
);
