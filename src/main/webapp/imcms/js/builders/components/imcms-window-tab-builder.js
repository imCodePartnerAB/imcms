/**
 * @author Serhii Maksymchuk from Ubrainians for imCode
 * 23.06.18
 */
define('imcms-window-tab-builder', ['imcms-bem-builder', 'jquery'], function (BEM, $) {

    const TAB_SELECTOR = '.imcms-tabs__tab';
    const ADVANCED_TAB_SELECTOR = '.imcms-tabs__advanced-tab';
    const DISABLED_TAB_CLASS_NAME = 'imcms-tabs__tab--disabled';
    const ID_ATTRIBUTE_NAME = 'data-window-id';

    const formsBEM = new BEM({
        block: 'imcms-form',
        elements: {'field': 'imcms-field'}
    });

    function get$TabByIndex(tabIndex) {
        return $(`${TAB_SELECTOR}[${ID_ATTRIBUTE_NAME}=${tabIndex}], ${ADVANCED_TAB_SELECTOR}[${ID_ATTRIBUTE_NAME}=${tabIndex}]`);
    }

    function setDisplay(tabIndex, displayValue) {
        get$TabByIndex(tabIndex).css('display', displayValue);
    }

    function setEnabled(tabIndex, isEnabled) {
        const $tab = get$TabByIndex(tabIndex);
        isEnabled
            ? $tab.removeClass(DISABLED_TAB_CLASS_NAME)
            : $tab.addClass(DISABLED_TAB_CLASS_NAME);
    }

    function isEnabled(tabIndex) {
        return get$TabByIndex(tabIndex).hasClass(DISABLED_TAB_CLASS_NAME);
    }

    const WindowTab = function (name, attributes) {
        this.name = name;
    };

    WindowTab.prototype = {
        showTab: function () {
            setDisplay(this.tabIndex, 'block');
        },
        hideTab: function () {
            setDisplay(this.tabIndex, 'none');
        },
        setEnabled: function (isEnabled) {
            setEnabled(this.tabIndex, isEnabled);
        },
        isEnabled: function () {
            return isEnabled(this.tabIndex);
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

            let attributes;
            if (this.attributes) {
                this.attributes['data-window-id'] = index;
                this.attributes.style += 'display: none;';
                attributes = this.attributes;
            } else {
                attributes = {
                    'data-window-id': index,
                    style: 'display: none;'
                };
            }

            return formsBEM.buildBlock("<div>", tabElements$, attributes, 'field');
        }
    };

    return WindowTab;
});
