/**
 * @author Serhii Maksymchuk from Ubrainians for imCode
 * 23.06.18
 */
Imcms.define(
    'imcms-categories-admin-tab-builder',
    ['imcms-window-tab-builder', 'imcms-i18n-texts'],
    function (TabBuilder, texts) {

        texts = texts.superAdmin.categories;

        var CategoriesTab = function (name) {
            TabBuilder.apply(this, arguments);
        };

        CategoriesTab.prototype = Object.create(TabBuilder.prototype);

        CategoriesTab.prototype.tabElementsFactory = function () {
            return [];
        };

        return new CategoriesTab(texts.name);
    }
);
