define(
    'imcms-document-transformer', ['imcms-bem-builder', 'jquery'],
    function (BEM, $) {

        function getDocumentOnClicked(document, fileEditor) {
            return function () {
                const $this = $(this);

                fileEditor.viewDoc($this, document);
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