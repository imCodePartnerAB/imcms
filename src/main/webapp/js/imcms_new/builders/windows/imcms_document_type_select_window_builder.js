/**
 * @author Serhii Maksymchuk from Ubrainians for imCode
 * 20.12.17
 */
Imcms.define("imcms-document-type-select-window-builder",
    ["imcms-page-info-builder", "imcms-window-builder", "imcms-bem-builder", "imcms-components-builder"],
    function (pageInfoBuilder, WindowBuilder, BEM, components) {

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
                    "text-doc": buildButton("text", "Text Document"),
                    "internal-link": buildButton("internal-link", "Internal Link"),
                    "external-link": buildButton("external-link", "External Link"),
                    "file-doc": buildButton("file", "File Document")
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
