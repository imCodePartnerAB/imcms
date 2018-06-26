/**
 * @author Serhii Maksymchuk from Ubrainians for imCode
 * 23.06.18
 */
Imcms.define(
    'imcms-templates-admin-tab-builder',
    ['imcms-window-tab-builder', 'imcms-i18n-texts'],
    function (TabBuilder, texts) {

        texts = texts.superAdmin.templates;

        var TemplatesTab = function (name) {
            TabBuilder.apply(this, arguments);
        };

        TemplatesTab.prototype = Object.create(TabBuilder.prototype);

        return new TemplatesTab(texts.name);
    }
);
