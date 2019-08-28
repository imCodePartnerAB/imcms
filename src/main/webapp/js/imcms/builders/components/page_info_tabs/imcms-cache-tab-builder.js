define('imcms-cache-tab-builder',
    ["imcms-bem-builder", "imcms-components-builder", "imcms-i18n-texts", "jquery",
        "imcms-page-info-tab", 'imcms', 'imcms-cache-document-rest-api', 'imcms-modal-window-builder'],
    function (BEM, components, texts, $, PageInfoTab, imcms, documentCacheRest, modal) {

        texts = texts.pageInfo.cache;

        function buildCacheData($title, $button, $cacheSuccess) {
            return new BEM({
                block: 'load-actions',
                elements: {
                    'title': $title,
                    'button': $button,
                    'success': $cacheSuccess
                },
            }).buildBlockStructure('<div>');
        }

        function clearCacheRequest(request, $cacheSuccess) {
            $cacheSuccess.hide();
            request.done(() => {
                $cacheSuccess.show();
            }).fail(() => modal.buildErrorWindow(texts.error.failedClear));
        }

        function buildDeleteDocsBlock() {

            function buildCacheActions() {
                let dataParam = {
                    docId: imcms.document.id,
                    alias: imcms.document.alias
                };
                const $cacheTitle = $('<div>', {
                    text: texts.invalidateTitle
                });

                const $cacheSuccess = $('<div>', {
                    text: texts.success,
                    style: 'display: none'
                });

                const $button = components.buttons.positiveButton({
                    text: texts.invalidateButton,
                    click: () => clearCacheRequest(documentCacheRest.invalidate(dataParam), $cacheSuccess)
                });

                return buildCacheData($cacheTitle, $button, $cacheSuccess)
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