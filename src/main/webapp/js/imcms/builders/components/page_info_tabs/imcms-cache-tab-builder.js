define('imcms-cache-tab-builder',
    ["imcms-bem-builder", "imcms-components-builder", "imcms-i18n-texts", "jquery",
        "imcms-page-info-tab", 'imcms', 'imcms-cache-document-rest-api', 'imcms-modal-window-builder'],
    function (BEM, components, texts, $, PageInfoTab, imcms, documentCacheRest, modal) {

        texts = texts.pageInfo.cache;

        function buildCacheData($title, $button) {
            return new BEM({
                block: 'init-data',
                elements: {
                    'title': $title,
                    'button': $button,
                },
            }).buildBlockStructure('<div>');
        }

        function clearCacheRequest(request, $loading, $success) {
            $success.hide();
            $loading.show();
            request().done(() => {
                $loading.hide();
                $success.show();
            }).fail(() => modal.buildErrorWindow(texts.error.failedClear));
        }

        function buildDeleteDocsBlock() {

            const $loading = $('<div>', {
                class: 'loading-animation',
                style: 'display: none',
            });
            const $success = $('<div>', {
                class: 'success-animation',
                style: 'display: none',
            });

            function buildCacheActions() {
                const dataParam = {
                    docId: imcms.document.id,
                    alias: imcms.document.alias
                };
                const $cacheTitle = $('<div>', {
                    text: texts.invalidateTitle,
                    class: 'imcms-title',
                });

                const request = () => documentCacheRest.invalidate(dataParam);

                const $button = components.buttons.positiveButton({
                    text: texts.invalidateButton,
                    click: () => clearCacheRequest(request, $loading, $success)
                });

                return buildCacheData($cacheTitle, $button)
            }

            return new BEM({
                block: 'cache-remove-document-row',
                elements: {
                    'build-data': buildCacheActions(),
                    'loading': $loading,
                    'success': $success,
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