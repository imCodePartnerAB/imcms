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

        const LOADING_INTERVAL = 2000;
        const TIME_PER_ONE_REINDEX = 13;

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
                    text: title + ':',
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

        class TimeLabel {
            constructor(title) {
                this.title = title;
                this.$label = $('<div>', {
                    class: 'imcms-label',
                    text: title + ': ',
                });
            }

            setMillis(millis) {
                this.$label.text(this.title + ': ' + this.millisToTimeString(millis));
            }

            millisToTimeString(millis) {
                let seconds = (millis / 1000);
                let minutes = seconds / 60;
                let hours = minutes / 60;

                seconds = seconds % 60;
                minutes = minutes % 60;

                const correctS = this.numberToCorrectString(Math.round(seconds));
                const correctM = this.numberToCorrectString(Math.round(minutes));
                const correctH = Math.round(hours);

                return correctH + ':' + correctM + ':' + correctS;
            }

            numberToCorrectString(number) {
                return number < 10 ? '0' + number : '' + number;
            }

            getLabel(label) {
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

        function deleteCacheRequest(request, date, $loading, $success) {
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
                const timeLeft = new TimeLabel(texts.timeLeft);
                timeLeft.getLabel().hide();

                const $button = components.buttons.warningButton({
                    'class': 'imcms-buttons imcms-form__field',
                    text: texts.init,
                    click: () => reindexRequest($loading, $success, lastUpdate, timeLeft),
                });

                return new BEM({
                    block: 'actions',
                    elements: {
                        'button': $button,
                        'date': lastUpdate.getLabel(),
                        'loading': $loading,
                        'success': $success,
                        'time': timeLeft.getLabel(),
                    },
                }).buildBlockStructure('<div>');
            }

            function reindexRequest($loading, $success, date, time) {
                $success.hide();
                $loading.text('0%');
                $loading.show();

                temporalDataApi.rebuildDocumentIndex().done(totalAmount => {
                    time.setMillis(calculateTimeByAmount(totalAmount, totalAmount, TIME_PER_ONE_REINDEX));
                    time.getLabel().show();

                    const interval = setInterval(
                        () => updateLoading($loading, $success, interval, totalAmount, date, time),
                        LOADING_INTERVAL
                    );
                });
            }

            function updateLoading($loading, $success, interval, totalAmount, date, time) {
                temporalDataApi.getAmountOfIndexedDocuments().done(currentAmount => {

                    if (currentAmount === -1) {
                        clearInterval(interval);
                        date.updateDate();
                        $loading.hide();
                        $success.show();
                        time.getLabel().hide();

                        return;
                    }

                    time.setMillis(calculateTimeByAmount(totalAmount, currentAmount, TIME_PER_ONE_REINDEX));
                    const percent = getPercent(totalAmount, currentAmount);
                    $loading.text(percent + '%');
                });
            }

            function calculateTimeByAmount(totalAmount, currentAmount, timePerOne) {
                return (totalAmount - currentAmount) * timePerOne;
            }

            function getPercent(totalAmount, currentAmount) {
                return Math.round(100 / totalAmount * currentAmount);
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
                    click: () => deleteCacheRequest(temporalDataApi.deletePublicDocumentCache, lastUpdate, $loading, $success),
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
                    click: () => deleteCacheRequest(temporalDataApi.deleteStaticContentCache, lastUpdate, $loading, $success),
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
                    click: () => deleteCacheRequest(temporalDataApi.deleteOtherContentCache, lastUpdate, $loading, $success),
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
