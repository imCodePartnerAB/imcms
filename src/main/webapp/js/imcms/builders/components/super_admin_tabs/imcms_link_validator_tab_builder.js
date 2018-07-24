/**
 * @author Serhii Maksymchuk from Ubrainians for imCode
 * 23.06.18
 */
Imcms.define(
    'imcms-link-validator-tab-builder',
    ['imcms-window-tab-builder', 'imcms-i18n-texts'],
    function (TabBuilder, texts) {

        texts = texts.superAdmin.linkValidator;

        var LinkValidatorTab = function (name) {
            TabBuilder.apply(this, arguments);
        };

        LinkValidatorTab.prototype = Object.create(TabBuilder.prototype);

        LinkValidatorTab.prototype.tabElementsFactory = function () {
            return [];
        };

        return new LinkValidatorTab(texts.name);
    }
);
