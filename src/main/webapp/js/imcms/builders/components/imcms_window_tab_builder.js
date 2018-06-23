/**
 * @author Serhii Maksymchuk from Ubrainians for imCode
 * 23.06.18
 */
Imcms.define('imcms-tab-window-builder', ['imcms-bem-builder', 'jquery'], function (BEM, $) {

    var formsBEM = new BEM({
        block: 'imcms-form',
        elements: {'field': 'imcms-field'}
    });

    function setDisplay(tabIndex, displayValue) {
        $('.imcms-tabs__tab[data-window-id=' + tabIndex + ']').css('display', displayValue);
    }

    var WindowTab = function (name) {
        this.name = name;
    };

    WindowTab.prototype = {
        showTab: function () {
            setDisplay(this.tabIndex, 'block');
        },
        hideTab: function () {
            setDisplay(this.tabIndex, 'none');
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
            return formsBEM.buildBlock("<div>", tabElements$, {'data-window-id': index}, 'field');
        }
    };

    return WindowTab;
});
