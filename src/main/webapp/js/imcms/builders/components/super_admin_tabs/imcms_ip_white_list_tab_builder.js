/**
 * @author Serhii Maksymchuk from Ubrainians for imCode
 * 23.06.18
 */
Imcms.define(
    'imcms-ip-white-list-tab-builder',
    ['imcms-window-tab-builder', 'imcms-i18n-texts'],
    function (TabBuilder, texts) {

        texts = texts.superAdmin.ipWhiteList;

        var IpWhiteListTab = function (name) {
            TabBuilder.apply(this, arguments);
        };

        IpWhiteListTab.prototype = Object.create(TabBuilder.prototype);

        IpWhiteListTab.prototype.tabElementsFactory = function () {
            return [];
        };

        return new IpWhiteListTab(texts.name);
    }
);
