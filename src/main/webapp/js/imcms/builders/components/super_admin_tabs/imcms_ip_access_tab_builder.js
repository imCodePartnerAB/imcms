/**
 * @author Serhii Maksymchuk from Ubrainians for imCode
 * 23.06.18
 */
Imcms.define(
    'imcms-ip-access-tab-builder',
    ['imcms-window-tab-builder', 'imcms-i18n-texts'],
    function (TabBuilder, texts) {

        texts = texts.superAdmin.ipAccess;

        var IpAccessTab = function (name) {
            TabBuilder.apply(this, arguments);
        };

        IpAccessTab.prototype = Object.create(TabBuilder.prototype);

        IpAccessTab.prototype.tabElementsFactory = function () {
            return [];
        };

        return new IpAccessTab(texts.name);
    }
);
