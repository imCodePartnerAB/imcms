/**
 * @author Serhii Maksymchuk from Ubrainians for imCode
 * 22.06.18
 */
Imcms.define(
    'imcms-super-admin-page-builder',
    [
        'imcms-bem-builder', 'imcms-components-builder', 'imcms-window-builder', 'imcms-super-admin-tabs-builder',
        'imcms-i18n-texts', 'jquery'
    ],
    function (BEM, components, WindowBuilder, superAdminTabs, texts, $) {

        texts = texts.superAdmin;

        var panels$;

        function buildSuperAdminPanels() {
            return superAdminTabs.tabBuilders.map(function (tabBuilder, index) {
                return tabBuilder.buildTab(index);
            });
        }

        function buildSuperAdmin() {
            panels$ = buildSuperAdminPanels();

            return new BEM({
                block: 'imcms-pop-up-modal',
                elements: {
                    'head': superAdminWindowBuilder.buildNonClosableHead(texts.head),
                    'left-side': superAdminTabs.buildWindowTabs(panels$),
                    'right-side': $('<div>', {'class': 'imcms-right-side'}).append(panels$),
                    'footer': $('<div>', {'class': 'imcms-footer'})
                }
            }).buildBlockStructure('<div>');
        }

        var superAdminWindowBuilder = new WindowBuilder({
            factory: buildSuperAdmin
        });

        return {
            build: function () {
                superAdminWindowBuilder.buildWindowWithShadow();
            }
        };
    }
);
