/**
 * @author Serhii Maksymchuk from Ubrainians for imCode
 * 22.06.18
 */
Imcms.define('imcms-window-tabs-builder', ['imcms-bem-builder', 'jquery'], function (BEM, $) {

    var WindowTabsBuilder = function (opts) {
        this.tabBuilders = opts.tabBuilders;
    };

    WindowTabsBuilder.prototype = {
        getOnTabClick: function (index) {
            var context = this;

            function showPanel(index) {
                context.panels$.forEach(function ($panel, number) {
                    $panel.css({'display': (index === number) ? 'block' : 'none'});
                });
            }

            return function () {
                context.$tabsContainer.find('.imcms-title--active').removeClass('imcms-title--active');
                $(this).addClass('imcms-title--active');
                showPanel(index);
            }
        },
        buildWindowTabs: function (panels$) {
            this.panels$ = panels$;

            var $tabs = this.tabBuilders.map(function (tabBuilder, index) {
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
            }.bind(this));

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

    return WindowTabsBuilder;
});
