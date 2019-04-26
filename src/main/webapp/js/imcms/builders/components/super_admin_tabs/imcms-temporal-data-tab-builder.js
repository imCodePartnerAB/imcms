/**
 * @author Dmytro Zemlianskyi from Ubrainians for imCode
 * 23.04.19
 */
define(
    'imcms-temporal-data-tab-builder',
    [
        'imcms-super-admin-tab', 'imcms-bem-builder', 'imcms-i18n-texts', 'imcms-temporal-data-rest-api',
        'imcms-components-builder'
    ],
    function (SuperAdminTab, BEM, texts, temporalDataApi, components) {

        texts = texts.superAdmin.temporalContent;

        function buildReindexRow() {
            function buildReindexTitleMessage() {
                return components.texts.titleText('<div>', texts.actions.rebuildIndex, {});

            }

            function buildInitReindexActionButton() {
                let $button = components.buttons.warningButton({
                    'class': 'imcms-buttons imcms-form__field',
                    text: texts.init,
                    click: () => {
                        temporalDataApi.rebuildDocumentIndex();
                    }
                });

                return components.buttons.buttonsContainer('<div>', [$button]);
            }

            return new BEM({
                block: 'imcms-content-action-row',
                elements: {
                    'action-title': buildReindexTitleMessage(),
                    'action-button': buildInitReindexActionButton(),
                }
            }).buildBlockStructure('<div>');
        }

        function buildPublicDocumentCacheRow() {
            function buildPublicDocumentCacheTitleMessage() {
                return components.texts.titleText('<div>', texts.actions.deletePublicDocCache, {});

            }

            function buildInitDeletePublicDocumentActionButton() {
                let $button = components.buttons.warningButton({
                    'class': 'imcms-buttons imcms-form__field',
                    text: texts.init,
                    click: () => {
                        temporalDataApi.deletePublicDocumentCache();
                    }
                });

                return components.buttons.buttonsContainer('<div>', [$button]);
            }

            return new BEM({
                block: 'imcms-content-action-row',
                elements: {
                    'action-title': buildPublicDocumentCacheTitleMessage(),
                    'action-button': buildInitDeletePublicDocumentActionButton(),
                }
            }).buildBlockStructure('<div>');
        }

        function buildStaticContentRow() {
            function buildStaticContenTitleMessage() {
                return components.texts.titleText('<div>', texts.actions.deleteStaticCache, {});

            }

            function buildInitDeleteStaticContentActionButton() {
                let $button = components.buttons.warningButton({
                    'class': 'imcms-buttons imcms-form__field',
                    text: texts.init,
                    click: () => {
                        temporalDataApi.deleteStaticContentCache();
                    }
                });

                return components.buttons.buttonsContainer('<div>', [$button]);
            }

            return new BEM({
                block: 'imcms-content-action-row',
                elements: {
                    'action-title': buildStaticContenTitleMessage(),
                    'action-button': buildInitDeleteStaticContentActionButton(),
                }
            }).buildBlockStructure('<div>');
        }

        function buildOtherContentRow() {
            function buildOtherContenTitleMessage() {
                return components.texts.titleText('<div>', texts.actions.deleteOtherCache, {});

            }

            function buildInitDeleteOtherContentActionButton() {
                let $button = components.buttons.warningButton({
                    'class': 'imcms-buttons imcms-form__field',
                    text: texts.init,
                    click: () => {
                        temporalDataApi.deleteOtherContentCache();
                    }
                });

                return components.buttons.buttonsContainer('<div>', [$button]);
            }

            return new BEM({
                block: 'imcms-content-action-row',
                elements: {
                    'action-title': buildOtherContenTitleMessage(),
                    'action-button': buildInitDeleteOtherContentActionButton(),
                }
            }).buildBlockStructure('<div>');
        }


        return new SuperAdminTab(texts.name, [
            buildReindexRow(),
            buildPublicDocumentCacheRow(),
            buildStaticContentRow(),
            buildOtherContentRow(),
        ]);
    }
);
