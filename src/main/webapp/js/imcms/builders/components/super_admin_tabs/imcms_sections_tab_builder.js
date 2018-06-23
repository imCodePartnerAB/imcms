/**
 * @author Serhii Maksymchuk from Ubrainians for imCode
 * 23.06.18
 */
Imcms.define(
    'imcms-sections-tab-builder',
    ['imcms-window-tab-builder', 'imcms-i18n-texts'],
    function (TabBuilder, texts) {

        texts = texts.superAdmin.sections;

        var SectionsTab = function (name) {
            TabBuilder.apply(this, arguments);
        };

        SectionsTab.prototype = Object.create(TabBuilder.prototype);

        return new SectionsTab(texts.name);
    }
);

