define('imcms-cache-tab-builder',
    ["imcms-bem-builder", "imcms-components-builder", "imcms-i18n-texts", "jquery",
        "imcms-page-info-tab", 'imcms', 'imcms-cache-document-rest-api', 'imcms-modal-window-builder'],
    function (BEM, components, texts, $, PageInfoTab, imcms, documentCacheRest, modal) {

        texts = texts.pageInfo.cache;

        function buildActions($title, $button, $loading, $success) {
            return new BEM({
                block: 'load-actions',
                elements: {
                    'title': $title,
                    'button': $button,
                    'loading': $loading,
                    'success': $success
                },
            }).buildBlockStructure('<div>');
        }

        function buildLoadingAnimation() {
            return $('<div>', {
                class: 'loading-animation',
                style: 'display: none;',
            });
        }

        function buildSuccessAnimation() {
            return $('<div>', {
                class: 'success-animation',
                style: 'display: none;',
            });
        }

        function animateRequest(request, $loading, $success) {
            $success.hide();
            $loading.show();
            request.done(() => {
                $loading.hide();
                $success.show();
            }).fail(() => modal.buildErrorWindow('fed'));
        }

        function buildDeleteDocsBlock() {

            function buildCacheActions() {
                const $loading = buildLoadingAnimation();
                const $success = buildSuccessAnimation();

                let dataParam = {
                    docId: imcms.document.id,
                    alias: imcms.document.alias
                };
                const $cacheTitle = $('<div>', {
                    text: texts.invalidateTitle
                });

                const $button = components.buttons.positiveButton({
                    text: texts.invalidateButton,
                    click: () => animateRequest(documentCacheRest.invalidate(dataParam), $loading, $success)
                });

                return buildActions($cacheTitle, $button, $loading, $success)
            }

            return new BEM({
                block: 'cache-remove-document-row',
                elements: {
                    'build-data': buildCacheActions()
                }
            }).buildBlockStructure('<div>')
        }

        const CacheTab = function (name) {
            PageInfoTab.call(this, name);
        };

        CacheTab.prototype = Object.create(PageInfoTab.prototype);

        CacheTab.prototype.isDocumentTypeSupported = () => {
            return true; // all supported
        };

        CacheTab.prototype.tabElementsFactory = () => [
            buildDeleteDocsBlock()
        ];

        return new CacheTab(texts.name);
    }
);