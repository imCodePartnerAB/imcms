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
        const TIME_PER_ONE_REINDEX = 45;
        const TIME_PER_ONE_RECACHE = 1400;
        const DISABLED_BUTTON_CLASS_NAME = 'imcms-button--disabled';

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

            function init($button, date) {
                temporalDataApi.getAmountOfIndexedDocuments().done(currentAmount => {
                    if (currentAmount !== -1) {
                        $button
                            .attr('disabled', '')
                            .text(texts.indexing)
                            .addClass(DISABLED_BUTTON_CLASS_NAME);

                        const interval = setInterval(
                            () => disableButtonWhileIndexing($button, date, interval),
                            LOADING_INTERVAL
                        );
                    }
                });
            }

            function disableButtonWhileIndexing($button, date, interval) {
                temporalDataApi.getAmountOfIndexedDocuments().done(currentAmount => {
                    if (currentAmount === -1) {
                        clearInterval(interval);
                        date.updateDate();
                        $button
                            .removeAttr('disabled')
                            .removeClass(DISABLED_BUTTON_CLASS_NAME)
                            .text(texts.init);
                    }
                });
            }

            function buildReindexActions() {
                const $loading = buildLoadingAnimation();
                const $success = buildSuccessAnimation();

                const lastUpdate = new DateLabel(temporalDataApi.getDateDocumentIndex, texts.lastUpdate);
                const timeLeft = new TimeLabel(texts.timeLeft);
                timeLeft.getLabel().hide();

                const $button = components.buttons.warningButton({
                    'class': 'imcms-buttons imcms-form__field',
                    text: texts.initIndexing,
                    click: () => reindexRequest($button, $loading, $success, lastUpdate, timeLeft),
                });

                init($button, lastUpdate);

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

            function reindexRequest($button, $loading, $success, date, time) {
                $button
                    .attr('disabled', '')
                    .text(texts.indexing)
                    .addClass(DISABLED_BUTTON_CLASS_NAME);
                $success.hide();

                temporalDataApi.getAmountOfIndexedDocuments().done(currentAmount => {
                    if (currentAmount === -1) {
                        $loading.text('0%');
                        $loading.show();

                        temporalDataApi.rebuildDocumentIndex().done(totalAmount => {
                            time.setMillis(calculateTimeByAmount(totalAmount, 0, TIME_PER_ONE_REINDEX));
                            time.getLabel().show();

                            const interval = setInterval(
                                () => updateLoading($button, $loading, $success, interval, totalAmount, date, time),
                                LOADING_INTERVAL
                            );
                        });
                    } else {
                        const interval = setInterval(
                            () => disableButtonWhileIndexing($button, date, interval),
                            LOADING_INTERVAL
                        );
                    }
                });
            }

            function updateLoading($button, $loading, $success, interval, totalAmount, date, time) {
                temporalDataApi.getAmountOfIndexedDocuments().done(currentAmount => {

                    if (currentAmount === -1) {
                        clearInterval(interval);
                        date.updateDate();
                        $button
                            .removeAttr('disabled')
                            .removeClass(DISABLED_BUTTON_CLASS_NAME)
                            .text(texts.initIndexing);
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

        function buildReCacheRow() {
            function buildReCacheTitleMessage() {
                return components.texts.titleText('<div>', texts.actions.buildCacheDocument, {});
            }

            function init($button, date) {
                temporalDataApi.getAmountOfCachedDocuments().done(currentAmount => {
                    if (currentAmount !== -1) {
                        $button
                            .attr('disabled', '')
                            .text(texts.caching)
                            .addClass(DISABLED_BUTTON_CLASS_NAME);

                        const interval = setInterval(
                            () => disableButtonWhileCaching($button, date, interval),
                            LOADING_INTERVAL
                        );
                    }
                });
            }

            function disableButtonWhileCaching($button, date, interval) {
                temporalDataApi.getAmountOfCachedDocuments().done(currentAmount => {
                    if (currentAmount === -1) {
                        clearInterval(interval);
                        date.updateDate();
                        $button
                            .removeAttr('disabled')
                            .removeClass(DISABLED_BUTTON_CLASS_NAME)
                            .text(texts.init);
                    }
                });
            }

            function buildReCacheActions() {
                const $loading = buildLoadingAnimation();
                const $success = buildSuccessAnimation();

                const lastUpdate = new DateLabel(temporalDataApi.getDateReCacheDocuments, texts.lastBuildCache);
                const timeLeft = new TimeLabel(texts.timeLeft);
                timeLeft.getLabel().hide();

                const $button = components.buttons.warningButton({
                    'class': 'imcms-buttons imcms-form__field',
                    text: texts.initCaching,
                    click: () => buildCacheRequest($button, $loading, $success, lastUpdate, timeLeft),
                });

                init($button, lastUpdate);

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

            function buildCacheRequest($button, $loading, $success, date, time) {
                $button
                    .attr('disabled', '')
                    .text(texts.caching)
                    .addClass(DISABLED_BUTTON_CLASS_NAME);
                $success.hide();

                temporalDataApi.getAmountOfCachedDocuments().done(currentAmount => {
                    if (currentAmount === -1) {
                        $loading.text('0%');
                        $loading.show();
                        temporalDataApi.getTotalForCachingDocIdsAndAlias().done(totalAmount => {
                            time.setMillis(calculateTimeByAmount(totalAmount, 0, TIME_PER_ONE_RECACHE));
                            time.getLabel().show();

                            temporalDataApi.addDocumentsInCache().done(); //setInterval 2 sec?

                            const interval = setInterval(
                                () => updateLoading($button, $loading, $success, interval, totalAmount, date, time),
                                LOADING_INTERVAL
                            );
                        });

                    } else {
                        const interval = setInterval(
                            () => disableButtonWhileCaching($button, date, interval),
                            LOADING_INTERVAL
                        );
                    }
                });
            }

            function updateLoading($button, $loading, $success, interval, totalAmount, date, time) {
                temporalDataApi.getAmountOfCachedDocuments().done(currentAmount => {

                    if (currentAmount === -1) {
                        clearInterval(interval);
                        date.updateDate();
                        $button
                            .removeAttr('disabled')
                            .removeClass(DISABLED_BUTTON_CLASS_NAME)
                            .text(texts.initCaching);
                        $loading.hide();
                        $success.show();
                        time.getLabel().hide();

                        return;
                    }

                    time.setMillis(calculateTimeByAmount(totalAmount, currentAmount, TIME_PER_ONE_RECACHE));
                    let percent = getPercent(totalAmount, currentAmount);
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
                    'action-title': buildReCacheTitleMessage(),
                    'actions': buildReCacheActions(),
                }
            }).buildBlockStructure('<div>');
        }


        return new SuperAdminTab(texts.name, [
            buildReindexRow(),
            buildPublicDocumentCacheRow(),
            buildStaticContentRow(),
            buildOtherContentRow(),
            buildReCacheRow()
        ]);
    }
);
