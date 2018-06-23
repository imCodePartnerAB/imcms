/**
 * @author Serhii Maksymchuk from Ubrainians for imCode
 * 23.06.18
 */
Imcms.define(
    'imcms-roles-tab-builder',
    ['imcms-window-tab-builder', 'imcms-i18n-texts'],
    function (TabBuilder, texts) {

        texts = texts.superAdmin.roles;

        var RolesTab = function (name) {
            TabBuilder.apply(this, arguments);
        };

        RolesTab.prototype = Object.create(TabBuilder.prototype);

        return new RolesTab(texts.name);
    }
);
