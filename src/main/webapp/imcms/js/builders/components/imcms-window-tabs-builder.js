/**
 * @author Serhii Maksymchuk from Ubrainians for imCode
 * 22.06.18
 */
const BEM = require('imcms-bem-builder');

const ACTIVE_TAB_CLASS_NAME = 'imcms-title--active';
const ACTIVE_TAB_SELECTOR = '.imcms-title--active';
const DISABLED_TAB_CLASS_NAME = 'imcms-tabs__tab--disabled';
const TAB_INDEX_ATTRIBUTE = 'data-window-id';

module.exports = class WindowTabsBuilder {
    constructor(opts) {
        this.tabBuilders = opts.tabBuilders;
    }

    /**
     * Makes tab an active. If another tab was active before that, this another tab will be deactivated.
     *
     * @param index Attribute {@link TAB_INDEX_ATTRIBUTE} of tab
     * @param isActive
     */
    setActiveTab(index, isActive) {
        const $tab = this.getTabByIndex(index);

        if (isActive && $tab.hasClass(ACTIVE_TAB_CLASS_NAME) || $tab.hasClass(DISABLED_TAB_CLASS_NAME)) {
            return;
        }

        if (isActive) {
            this.getActiveTab().removeClass(ACTIVE_TAB_CLASS_NAME);
            $tab.addClass(ACTIVE_TAB_CLASS_NAME);

            this.panels$.forEach(($panel, number) => {
                (index === number) ? $panel.slideDown() : $panel.slideUp();
            });
        } else {
            $tab.removeClass(ACTIVE_TAB_CLASS_NAME);
            this.panels$.forEach($panel => $panel.slideUp());
        }
    }

    isActiveTab(index) {
        return this.getTabByIndex(index).hasClass(ACTIVE_TAB_CLASS_NAME);
    }

    getActiveTab() {
        return this.$tabsContainer.find(ACTIVE_TAB_SELECTOR);
    }

    getTabByIndex(index) {
        return this.$tabsContainer.find(`[${TAB_INDEX_ATTRIBUTE}=${index}]`);
    }

    isEnabledTabByIndex(index) {
        return !this.getTabByIndex(index).hasClass(DISABLED_TAB_CLASS_NAME);
    }

    buildWindowTabs(panels$) {
        this.panels$ = panels$;

        const tabs = this.tabBuilders.map((tabBuilder, index) => {
            return {
                tag: '<div>',
                'class': 'imcms-title',
                attributes: {
                    'data-window-id': index,
                    text: tabBuilder.name,
                    click: () => this.setActiveTab(index, true),
                },
                modifiers: (index === 0 ? ['active'] : [])
            };
        });

        this.$tabsContainer = new BEM({
            block: 'imcms-tabs',
            elements: {
                'tab': tabs
            }
        }).buildBlockStructure('<div>');

        return new BEM({
            block: 'imcms-left-side',
            elements: {
                'tabs': this.$tabsContainer
            }
        }).buildBlockStructure('<div>');
    }
};
