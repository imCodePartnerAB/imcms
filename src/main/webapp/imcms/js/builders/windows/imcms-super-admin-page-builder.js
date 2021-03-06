/**
 * @author Serhii Maksymchuk from Ubrainians for imCode
 * 22.06.18
 */
define(
    'imcms-super-admin-page-builder',
    [
        'imcms-bem-builder', 'imcms-components-builder', 'imcms-super-admin-tabs-builder', 'imcms-i18n-texts',
        'jquery', 'imcms'
    ],
    function (BEM, components, superAdminTabs, texts, $, imcms) {

        texts = texts.superAdmin;

        let panels$;

        function buildSuperAdminPanels() {
            return superAdminTabs.tabBuilders.map(function (tabBuilder, index) {
                const $tab = tabBuilder.buildTab(index);

                (index === 0) ? $tab.slideDown() : $tab.slideUp();

                return $tab;
            });
        }

        function buildHead() {
            return new BEM({
                block: 'imcms-head',
                elements: {
                    'logo': $('<a>', {
                        href: '/'
                    }),
                    'title': $('<div>', {
                        'class': 'imcms-title',
                        text: texts.head
                    }),
                    'old-admin': $('<a>', {
                        href: imcms.contextPath + '/servlet/AdminManager',
                        text: texts.oldInterface + ' \u2b95',
                    }),
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
                    'head': buildHead(),
                    'left-side': superAdminTabs.buildWindowTabs(panels$),
                    'right-side': $('<div>', {'class': 'imcms-right-side'}).append(panels$)
                }
            }).buildBlockStructure('<div>', {
                'class': 'imcms-info-page'
            });
        }

        return {
            build: () => {
                $('body').append(buildSuperAdmin());
            }
        };
    }
);
