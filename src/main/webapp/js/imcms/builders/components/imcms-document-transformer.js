define(
    'imcms-document-transformer', ['imcms-bem-builder', 'jquery', 'imcms-components-builder', 'imcms-i18n-texts', 'imcms'],
    function (BEM, $, components, texts, imcms) {
        texts = texts.files;

        function getDocumentOnClicked(document, fileEditor) {
            return function () {
                const $this = $(this);

                if ($this.hasClass('documents-table__document-row--active')) return;

                // fileEditor.viewFirstFilesContainer($this, document);
            }
        }

        return {
            transform: (doc, fileEditor) => {

                let infoRowAttributes = {
                    id: 'doc-id-' + doc.id,
                    click: getDocumentOnClicked(doc, fileEditor)
                };

                return new BEM({
                    block: "doc-info-row",
                    elements: {
                        'document-id': $('<div>', {
                            text: doc.id
                        }),
                        'document-type': $('<div>', {
                            text: doc.type
                        })
                    }
                }).buildBlockStructure("<div>", infoRowAttributes);
            }
        }
    }
);