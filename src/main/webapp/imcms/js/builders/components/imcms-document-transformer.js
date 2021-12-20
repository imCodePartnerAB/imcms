define(
    'imcms-document-transformer', ['imcms-bem-builder', 'jquery', 'imcms-components-builder', 'imcms-i18n-texts'],
    function (BEM, $, components, texts) {

        texts = texts.superAdmin.files.documentData;

        function getDocumentOnClicked(document, fileEditor) {
            return function () {
                const $this = $(this);

                fileEditor.viewDoc($this, document);
            }
        }

        function buildLinkToView(id) {
            return components.texts.infoText('<a>', texts.docView, {
                href: '/' + id,
                target: '_blank'}
            );
        }

        function buildLinkToEdit(id){
            return components.texts.infoText('<a>', texts.docEdit, {
                href: 'page-info?meta-id=' + id,
                target: '_blank'}
            );
        }

        return {
            transform: (doc, fileEditor) => {

                // let infoRowAttributes = {
                //     id: 'doc-id-' + doc.id,
                //     click: getDocumentOnClicked(doc, fileEditor)
                // };

                return new BEM({
                    block: "doc-info-row",
                    elements: {
                        'document-id': $('<div>', { text: doc.id }),
                        'document-view': buildLinkToView(doc.id),
                        'document-edit': buildLinkToEdit(doc.id)
                    }
                }).buildBlockStructure("<div>");
            }
        }
    }
);