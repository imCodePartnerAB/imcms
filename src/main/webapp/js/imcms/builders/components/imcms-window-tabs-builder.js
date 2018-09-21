/**
 * @author Serhii Maksymchuk from Ubrainians for imCode
 * 22.06.18
 */
const BEM = require('imcms-bem-builder');
const $ = require('jquery');

module.exports = class WindowTabsBuilder {
    constructor(opts) {
        this.tabBuilders = opts.tabBuilders;
    }

    getOnTabClick(index) {
        const context = this;
        return function () {
            var $clickedTab = $(this);

            if ($clickedTab.hasClass('imcms-title--active')) return;

            context.$tabsContainer.find('.imcms-title--active').removeClass('imcms-title--active');
            $clickedTab.addClass('imcms-title--active');

            context.panels$.forEach(($panel, number) => {
                (index === number) ? $panel.slideDown() : $panel.slideUp();
            });
        }
    }

    buildWindowTabs(panels$) {
        this.panels$ = panels$;

        const $tabs = this.tabBuilders.map((tabBuilder, index) => {
            return {
                tag: '<div>',
                'class': 'imcms-title',
                attributes: {
                    'data-window-id': index,
                    text: tabBuilder.name,
                    click: this.getOnTabClick(index)
                },
                modifiers: (index === 0 ? ['active'] : [])
            };
        });

        this.$tabsContainer = new BEM({
            block: 'imcms-tabs',
            elements: {
                'tab': $tabs
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
