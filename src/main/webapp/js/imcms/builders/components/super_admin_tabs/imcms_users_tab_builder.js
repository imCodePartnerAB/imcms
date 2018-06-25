/**
 * @author Serhii Maksymchuk from Ubrainians for imCode
 * 23.06.18
 */
Imcms.define(
    'imcms-users-tab-builder',
    ['imcms-window-tab-builder', 'imcms-i18n-texts', 'jquery', 'imcms-bem-builder', 'imcms-components-builder'],
    function (TabBuilder, texts, $, BEM, components) {

        texts = texts.superAdmin.users;

        function buildTitle() {
            return components.texts.titleText('<div>', texts.title);
        }

        var UsersTab = function (name) {
            TabBuilder.apply(this, arguments);
        };

        UsersTab.prototype = Object.create(TabBuilder.prototype);

        UsersTab.prototype.tabElementsFactory = function () {
            return [
                buildTitle()
            ];
        };

        return new UsersTab(texts.name);
    }
);
