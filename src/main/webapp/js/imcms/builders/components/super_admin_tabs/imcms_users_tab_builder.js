/**
 * @author Serhii Maksymchuk from Ubrainians for imCode
 * 23.06.18
 */
Imcms.define(
    'imcms-users-tab-builder',
    ['imcms-window-tab-builder', 'imcms-i18n-texts', 'jquery'],
    function (TabBuilder, texts, $) {

        texts = texts.superAdmin.users;

        function buildTitle() {
            return $('<div>', {
                'class': 'imcms-title',
                text: texts.title
            });
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
