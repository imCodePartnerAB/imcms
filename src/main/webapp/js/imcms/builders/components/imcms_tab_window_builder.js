/**
 * @author Serhii Maksymchuk from Ubrainians for imCode
 * 23.06.18
 */
Imcms.define('imcms-tab-window-builder', ['imcms-page-info-tab-form-builder'], function (tabFormBuilder) {

    var WindowTab = function (name) {
        this.name = name;
    };

    WindowTab.prototype = {
        showTab: function () {
            tabFormBuilder.showTab(this.tabIndex);
        },
        hideTab: function () {
            tabFormBuilder.hideTab(this.tabIndex);
        },
        /**
         * @returns {Array} array of tab $elements
         */
        tabElementsFactory: function () {
            // override, return array of tab $elements
        },
        buildTab: function (index) {
            this.tabIndex = index;
            var tabElements$ = this.tabElementsFactory.apply(this, arguments);
            return tabFormBuilder.buildFormBlock(tabElements$, index);
        }
    };

    return WindowTab;
});
