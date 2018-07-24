/**
 * @author Serhii Maksymchuk from Ubrainians for imCode
 * 23.06.18
 */
Imcms.define(
    'imcms-delete-docs-tab-builder',
    ['imcms-window-tab-builder', 'imcms-i18n-texts'],
    function (TabBuilder, texts) {

        texts = texts.superAdmin.deleteDocs;

        var DeleteDocsTab = function (name) {
            TabBuilder.apply(this, arguments);
        };

        DeleteDocsTab.prototype = Object.create(TabBuilder.prototype);

        DeleteDocsTab.prototype.tabElementsFactory = function () {
            return [];
        };

        return new DeleteDocsTab(texts.name);
    }
);
