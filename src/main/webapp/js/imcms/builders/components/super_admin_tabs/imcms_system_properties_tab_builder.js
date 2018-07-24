/**
 * @author Serhii Maksymchuk from Ubrainians for imCode
 * 23.06.18
 */
Imcms.define(
    'imcms-system-properties-tab-builder',
    ['imcms-window-tab-builder', 'imcms-i18n-texts'],
    function (TabBuilder, texts) {

        texts = texts.superAdmin.systemProperties;

        var SystemPropertiesTab = function (name) {
            TabBuilder.apply(this, arguments);
        };

        SystemPropertiesTab.prototype = Object.create(TabBuilder.prototype);

        SystemPropertiesTab.prototype.tabElementsFactory = function () {
            return [];
        };

        return new SystemPropertiesTab(texts.name);
    }
);
