/**
 * @author Serhii Maksymchuk from Ubrainians for imCode
 * 23.06.18
 */
Imcms.define(
    'imcms-search-tab-builder',
    ['imcms-window-tab-builder', 'imcms-i18n-texts'],
    function (TabBuilder, texts) {

        texts = texts.superAdmin.search;

        var SearchTab = function (name) {
            TabBuilder.apply(this, arguments);
        };

        SearchTab.prototype = Object.create(TabBuilder.prototype);

        SearchTab.prototype.tabElementsFactory = function () {
            return [];
        };

        return new SearchTab(texts.name);
    }
);
