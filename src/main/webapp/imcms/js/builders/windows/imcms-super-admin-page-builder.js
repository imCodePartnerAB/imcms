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

        const TAB_INDEX_ATTRIBUTE = 'data-window-id';

        let panels$;

        function buildSuperAdminPanels() {
            return superAdminTabs.getAllTabBuilders().map(function (tabBuilder, index) {
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
                    'exit': components.buttons.closeButton().click(onExit),
                }
            }).buildBlockStructure('<div>', {
                'class': 'imcms-info-head'
            });
        }

        function onExit(){
            window.close()
        }

        function openDocumentationWindow(){
            const currentTabIndex = parseInt(superAdminTabs.getActiveTab().attr(TAB_INDEX_ATTRIBUTE));
            const docLink = imcms.documentationLink +
                superAdminTabs.getAllTabBuilders()[currentTabIndex].getDocLink();
            window.open(docLink)
        }

        function buildSuperAdminFooterButtons() {
            const $usageDetails = components.link.buildLinkButton({
                title: texts.documentation,
                onClick: openDocumentationWindow
            });

            return [$usageDetails];
        }

        function buildSuperAdmin() {
            panels$ = buildSuperAdminPanels();

            return new BEM({
                block: 'imcms-pop-up-super-admin-modal',
                elements: {
                    'head': buildHead(),
                    'left-side': superAdminTabs.buildWindowTabs(panels$),
                    'right-side': $('<div>', {'class': 'imcms-right-side'}).append(panels$),
                    'footer': $("<div>", {"class": "imcms-footer"}).append(buildSuperAdminFooterButtons())
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
