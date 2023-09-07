/**
 * @author Serhii Maksymchuk from Ubrainians for imCode
 * 22.06.18
 */
const BEM = require('imcms-bem-builder');
const $ = require('jquery');
const texts = require('imcms-i18n-texts');

const TAB_SELECTOR = '.imcms-tabs__tab';
const ADVANCED_TAB_SELECTOR = '.imcms-tabs__advanced-tab';
const ADVANCED_BUTTON_SELECTOR = '.imcms-tabs__advanced-button';

const ACTIVE_TAB_CLASS_NAME = 'imcms-title--active';
const ACTIVE_TAB_SELECTOR = '.imcms-title--active';
const DISABLED_TAB_CLASS_NAME = 'imcms-tabs__tab--disabled';
const TAB_INDEX_ATTRIBUTE = 'data-window-id';

function buildTab(tabBuilder, index, onClick){
    return {
        tag: '<div>',
        'class': 'imcms-title',
        attributes: {
            'data-window-id': index,
            text: tabBuilder.name,
            click: onClick,
        },
        modifiers: (index === 0 ? ['active'] : [])
    };
}

module.exports = class WindowTabsBuilder {
    constructor(opts) {
        this.tabBuilders = opts.tabBuilders;
        this.advancedTabBuilders = opts.advancedTabBuilders ? opts.advancedTabBuilders : [];
        this.advancedButtonText = opts.advancedButtonText ? opts.advancedButtonText : texts.windowTabs.advancedButton;
    }

    getAllTabBuilders(){
        return this.tabBuilders.concat(this.advancedTabBuilders);
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

                if(index === number){
                    $panel.slideDown();
                    $panel.closest(".imcms-right-side").animate({ scrollTop: 0 }, 200);
                }else{
                    $panel.slideUp();
                }
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

    setEnabledAdvancedButton(isActive){
        let $advancedButton = this.$tabsContainer.find(ADVANCED_BUTTON_SELECTOR);
        isActive ? $advancedButton.removeClass(DISABLED_TAB_CLASS_NAME) : $advancedButton.addClass(DISABLED_TAB_CLASS_NAME);
    }

    buildWindowTabs(panels$) {
        this.panels$ = panels$;

        let tabs = this.tabBuilders.map((tabBuilder, index) =>  buildTab(tabBuilder, index, () => this.setActiveTab(index, true)));
        let advancedButton = "";
        let advancedTabs = "";

        if(this.advancedTabBuilders.length){
            advancedButton = {
                tag: '<div>',
                'class': 'imcms-title',
                attributes: {
                    text: this.advancedButtonText,
                    click: (event) => {
                        if($(event.target).hasClass("imcms-tabs__advanced-button--enabled")){
                            this.$tabsContainer.find(ADVANCED_TAB_SELECTOR).slideUp(200);
                            $(event.target).removeClass("imcms-tabs__advanced-button--enabled");
                        }else{
                            this.$tabsContainer.find(ADVANCED_TAB_SELECTOR).slideDown(200);
                            $(event.target).addClass("imcms-tabs__advanced-button--enabled");
                        }
                    }
                }
            };

            advancedTabs = this.advancedTabBuilders.map((tabBuilder, index) =>  {
                let advancedIndex = this.tabBuilders.length + index;
                let tab = buildTab(tabBuilder, advancedIndex, () => this.setActiveTab(advancedIndex, true));
                tab.attributes.style = "display: none;";
                return tab;
            });
        }

        this.$tabsContainer = new BEM({
            block: 'imcms-tabs',
            elements: {
                'tab': tabs,
                'advanced-button': advancedButton,
                'advanced-tab': advancedTabs
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
