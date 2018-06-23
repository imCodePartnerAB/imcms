/**
 * @author Serhii Maksymchuk from Ubrainians for imCode
 * 23.06.18
 */
Imcms.define(
    'imcms-users-tab-builder',
    ['imcms-window-tab-builder', 'imcms-i18n-texts'],
    function (TabBuilder, texts) {

        texts = texts.superAdmin.users;

        var UsersTab = function (name) {
            TabBuilder.apply(this, arguments);
        };

        UsersTab.prototype = Object.create(TabBuilder.prototype);

        return new UsersTab(texts.name);
    }
);
