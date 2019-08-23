/**
 * @author Dmytro Zemlianskyi from Ubrainians for imCode
 * 23.04.19
 */
define(
    'imcms-temporal-data-tab-builder',
    [
        'imcms-super-admin-tab', 'imcms-bem-builder', 'imcms-i18n-texts', 'imcms-temporal-data-rest-api',
        'imcms-components-builder', 'jquery'
    ],
    function (SuperAdminTab, BEM, texts, temporalDataApi, components, $) {

        texts = texts.superAdmin.temporalContent;

        function buildActions($button, $date, $loading, $success) {
            return new BEM({
                block: 'actions',
                elements: {
                    'button': $button,
                    'date': $date,
                    'loading': $loading,
                    'success': $success,
                },
            }).buildBlockStructure('<div>');
        }
        
        class DateLabel {
            constructor(dateRequest, title) {
                this.dateRequest = dateRequest;
                this.title = title;
                this.$label = $('<div>', {
                    class: 'imcms-label',
                    text: title,
                });

                this.updateDate();
            }

            updateDate() {
                this.dateRequest().done(dateString => {
                    this.$label.text(this.title + ': ' + dateString);
                })
            }

            getLabel() {
                return this.$label;
            }
        }

        function buildLoadingAnimation() {
            return $('<div>', {
                class: 'loading-animation',
                style: 'display: none',
            });
        }

        function buildSuccessAnimation() {
            return $('<div>', {
                class: 'success-animation',
                style: 'display: none',
            });
        }

        function animateRequest(request, date, $loading, $success) {
            $success.hide();
            $loading.show();
            request().done(() => {
                date.updateDate();
                $loading.hide();
                $success.show();
            });
        }

        function buildReindexRow() {
            function buildReindexTitleMessage() {
                return components.texts.titleText('<div>', texts.actions.rebuildIndex, {});

            }

            function buildReindexActions() {
                const $loading = buildLoadingAnimation();
                const $success = buildSuccessAnimation();

                const lastUpdate = new DateLabel(temporalDataApi.getDateDocumentIndex, texts.lastUpdate);

                const $button = components.buttons.warningButton({
                    'class': 'imcms-buttons imcms-form__field',
                    text: texts.init,
                    click: () => animateRequest(temporalDataApi.rebuildDocumentIndex, lastUpdate, $loading, $success),
                });

                return buildActions($button, lastUpdate.getLabel(), $loading, $success);
            }

            return new BEM({
                block: 'imcms-content-action-row',
                elements: {
                    'action-title': buildReindexTitleMessage(),
                    'actions': buildReindexActions(),
                }
            }).buildBlockStructure('<div>');
        }

        function buildPublicDocumentCacheRow() {
            function buildPublicDocumentCacheTitleMessage() {
                return components.texts.titleText('<div>', texts.actions.deletePublicDocCache, {});
            }

            function buildPublicDocumentActions() {
                const $loading = buildLoadingAnimation();
                const $success = buildSuccessAnimation();

                const lastUpdate = new DateLabel(temporalDataApi.getDateRemoveDocumentCache, texts.lastDeleteCache);

                const $button = components.buttons.warningButton({
                    'class': 'imcms-buttons imcms-form__field',
                    text: texts.init,
                    click: () => animateRequest(temporalDataApi.deletePublicDocumentCache, lastUpdate, $loading, $success),
                });

                return buildActions($button, lastUpdate.getLabel(), $loading, $success);
            }

            return new BEM({
                block: 'imcms-content-action-row',
                elements: {
                    'action-title': buildPublicDocumentCacheTitleMessage(),
                    'actions': buildPublicDocumentActions(),
                }
            }).buildBlockStructure('<div>');
        }

        function buildStaticContentRow() {
            function buildStaticContenTitleMessage() {
                return components.texts.titleText('<div>', texts.actions.deleteStaticCache, {});
            }

            function buildInitDeleteStaticContentActions() {
                const $loading = buildLoadingAnimation();
                const $success = buildSuccessAnimation();

                const lastUpdate = new DateLabel(temporalDataApi.getDateRemoveStaticContentCache, texts.lastDeleteCache);

                const $button = components.buttons.warningButton({
                    'class': 'imcms-buttons imcms-form__field',
                    text: texts.init,
                    click: () => animateRequest(temporalDataApi.deleteStaticContentCache, lastUpdate, $loading, $success),
                });

                return buildActions($button, lastUpdate.getLabel(), $loading, $success);
            }

            return new BEM({
                block: 'imcms-content-action-row',
                elements: {
                    'action-title': buildStaticContenTitleMessage(),
                    'actions': buildInitDeleteStaticContentActions(),
                }
            }).buildBlockStructure('<div>');
        }

        function buildOtherContentRow() {
            function buildOtherContentTitleMessage() {
                return components.texts.titleText('<div>', texts.actions.deleteOtherCache, {});
            }

            function buildInitDeleteOtherContentActions() {
                const $loading = buildLoadingAnimation();
                const $success = buildSuccessAnimation();

                const lastUpdate = new DateLabel(temporalDataApi.getDateRemoveOtherContentCache, texts.lastDeleteCache);

                const $button = components.buttons.warningButton({
                    'class': 'imcms-buttons imcms-form__field',
                    text: texts.init,
                    click: () => animateRequest(temporalDataApi.deleteOtherContentCache, lastUpdate, $loading, $success),
                });

                return buildActions($button, lastUpdate.getLabel(), $loading, $success);
            }

            return new BEM({
                block: 'imcms-content-action-row',
                elements: {
                    'action-title': buildOtherContentTitleMessage(),
                    'actions': buildInitDeleteOtherContentActions(),
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
