/**
 * @author Serhii Maksymchuk from Ubrainians for imCode
 * 23.06.18
 */
Imcms.define(
    'imcms-profiles-tab-builder',
    ['imcms-window-tab-builder', 'imcms-i18n-texts'],
    function (TabBuilder, texts) {

        texts = texts.superAdmin.profiles;

        var ProfilesTab = function (name) {
            TabBuilder.apply(this, arguments);
        };

        ProfilesTab.prototype = Object.create(TabBuilder.prototype);

        return new ProfilesTab(texts.name);
    }
);
