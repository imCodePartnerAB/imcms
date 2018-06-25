/**
 * @author Serhii Maksymchuk from Ubrainians for imCode
 * 22.06.18
 */
Imcms.define(
    'imcms-super-admin-page-builder',
    [
        'imcms-bem-builder', 'imcms-components-builder', 'imcms-super-admin-tabs-builder', 'imcms-i18n-texts', 'jquery'
    ],
    function (BEM, components, superAdminTabs, texts, $) {

        texts = texts.superAdmin;

        var panels$;

        function buildSuperAdminPanels() {
            return superAdminTabs.tabBuilders.map(function (tabBuilder, index) {
                return tabBuilder.buildTab(index);
            });
        }

        function buildHead() {
            return new BEM({
                block: 'imcms-head',
                elements: {
                    'logo': $('<a>', {
                        href: 'https://www.imcms.net/'
                    }),
                    'title': $('<div>', {
                        'class': 'imcms-title',
                        text: texts.head
                    })
                }
            }).buildBlockStructure('<div>', {
                'class': 'imcms-info-head'
            });
        }

        function buildSuperAdmin() {
            panels$ = buildSuperAdminPanels();

            return new BEM({
                block: 'imcms-pop-up-modal',
                elements: {
                    'head': buildHead(),//superAdminWindowBuilder.buildNonClosableHead(texts.head),
                    'left-side': superAdminTabs.buildWindowTabs(panels$),
                    'right-side': $('<div>', {'class': 'imcms-right-side'}).append(panels$)
                }
            }).buildBlockStructure('<div>', {
                'class': 'imcms-info-page'
            });
        }

        return {
            build: function () {
                $('body').append(buildSuperAdmin());
            }
        };
    }
);
