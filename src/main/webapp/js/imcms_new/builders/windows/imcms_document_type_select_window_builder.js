/**
 * @author Serhii Maksymchuk from Ubrainians for imCode
 * 20.12.17
 */
Imcms.define("imcms-document-type-select-window-builder",
    [
        "imcms-page-info-builder", "imcms-window-builder", "imcms-bem-builder", "imcms-components-builder",
        "imcms-document-types"
    ],
    function (pageInfoBuilder, WindowBuilder, BEM, components, docTypes) {

        function buildBody(onDocTypeSelected) {
            function buildButton(type, text) {
                return components.buttons.negativeButton({
                    text: text,
                    click: function () {
                        documentTypeSelectWindowBuilder.closeWindow();
                        pageInfoBuilder.build(null, onDocTypeSelected, type);
                    }
                });
            }

            return new BEM({
                block: "imcms-document-type",
                elements: {
                    "text-doc": buildButton(docTypes.text, "Text Document"),
                    "url-doc": buildButton(docTypes.url, "URL document"),
                    "file-doc": buildButton(docTypes.file, "File Document")
                }
            }).buildBlockStructure("<div>");
        }

        function buildDocumentTypeSelect(onDocTypeSelected) {
            return new BEM({
                block: "imcms-document-type-select-window",
                elements: {
                    "head": documentTypeSelectWindowBuilder.buildHead("Create new document"),
                    "body": buildBody(onDocTypeSelected)
                }
            }).buildBlockStructure("<div>");
        }

        var documentTypeSelectWindowBuilder = new WindowBuilder({
            factory: buildDocumentTypeSelect
        });

        return {
            build: function (onDocTypeSelected) {
                documentTypeSelectWindowBuilder.buildWindowWithShadow(onDocTypeSelected);
            }
        };
    }
);
