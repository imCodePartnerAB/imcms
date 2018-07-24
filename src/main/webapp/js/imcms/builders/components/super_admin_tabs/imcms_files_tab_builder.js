/**
 * @author Serhii Maksymchuk from Ubrainians for imCode
 * 23.06.18
 */
Imcms.define(
    'imcms-files-tab-builder',
    ['imcms-window-tab-builder', 'imcms-i18n-texts'],
    function (TabBuilder, texts) {

        texts = texts.superAdmin.files;

        var FilesTab = function (name) {
            TabBuilder.apply(this, arguments);
        };

        FilesTab.prototype = Object.create(TabBuilder.prototype);

        FilesTab.prototype.tabElementsFactory = function () {
            return [];
        };

        return new FilesTab(texts.name);
    }
);
