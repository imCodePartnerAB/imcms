define('imcms-super-admin-tab', ['imcms-window-tab-builder'], function (TabBuilder) {

    const SuperAdminTab = function (name, tabElements, attributes) {
        TabBuilder.call(this, name);
        this.tabElements = tabElements;
        this.attributes = attributes;
    };

    SuperAdminTab.prototype = Object.create(TabBuilder.prototype);

    SuperAdminTab.prototype.tabElementsFactory = function () {
        return this.tabElements;
    };

    return SuperAdminTab;
});
