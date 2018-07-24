Imcms.define('imcms-super-admin-tab', ['imcms-window-tab-builder'], function (TabBuilder) {

    var SuperAdminTab = function (name, tabElements) {
        TabBuilder.call(this, name);
        this.tabElements = tabElements;
    };

    SuperAdminTab.prototype = Object.create(TabBuilder.prototype);

    SuperAdminTab.prototype.tabElementsFactory = function () {
        return this.tabElements;
    };

    return SuperAdminTab;
});
