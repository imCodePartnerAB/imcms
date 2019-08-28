define('imcms-cache-tab-builder',
    ["imcms-bem-builder", "imcms-components-builder", "imcms-i18n-texts", "jquery", "imcms-page-info-tab", 'imcms'],
    function (BEM, components, texts, $, PageInfoTab, imcms) {

        texts = texts.pageInfo.cache;

        function buildDeleteDocsBlock() {
            const docId = imcms.document.id;
            const alias = imcms.document.alias;
            return new BEM({
                block: 'cache-remove-document-row',
                elements: {
                    'invalidate-title': $('<div>', {
                        text: texts.invalidateTitle
                    }),
                    'invalidate': components.buttons.positiveButton({
                        text: texts.invalidateButton,
                        click: invalidateCache(docId, alias)
                    })
                }
            }).buildBlockStructure('<div>')
        }

        function invalidateCache(currentDocId, alias) {

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