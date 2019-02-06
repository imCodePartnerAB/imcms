/**
 * @author Serhii Maksymchuk from Ubrainians for imCode
 * 23.06.18
 */
define('imcms-window-tab-builder', ['imcms-bem-builder', 'jquery'], function (BEM, $) {

    const formsBEM = new BEM({
        block: 'imcms-form',
        elements: {'field': 'imcms-field'}
    });

    function setDisplay(tabIndex, displayValue) {
        $('.imcms-tabs__tab[data-window-id=' + tabIndex + ']').css('display', displayValue);
    }

    const WindowTab = function (name) {
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
            return [];// override, return array of tab $elements
        },
        buildTab: function (index) {
            this.tabIndex = index;
            const tabElements$ = this.tabElementsFactory.apply(this, arguments);
            const attributes = {
                'data-window-id': index,
                style: 'display: none;'
            };
            return formsBEM.buildBlock("<div>", tabElements$, attributes, 'field');
        }
    };

    return WindowTab;
});
