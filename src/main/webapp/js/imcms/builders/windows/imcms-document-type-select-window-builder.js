/**
 * @author Serhii Maksymchuk from Ubrainians for imCode
 * 20.12.17
 */
define("imcms-document-type-select-window-builder",
    [
        "imcms-window-builder", "imcms-bem-builder", "imcms-components-builder", "imcms-document-types",
        "imcms-i18n-texts"
    ],
    function (WindowBuilder, BEM, components, docTypes, texts) {

        texts = texts.editors.newDocument;

        function buildBody() {
            function buildButton(type, text) {
                return components.buttons.negativeButton({
                    text: text,
                    click: () => {
                        documentTypeSelectWindowBuilder.closeWindow();
                        onDocTypeSelectedCallback(type);
                    }
                });
            }

            return new BEM({
                block: "imcms-document-type",
                elements: {
                    "text-doc": buildButton(docTypes.TEXT, texts.textDoc),
                    "file-doc": buildButton(docTypes.FILE, texts.fileDoc),
                    "url-doc": buildButton(docTypes.URL, texts.urlDoc)
                }
            }).buildBlockStructure("<div>");
        }

        function buildDocumentTypeSelect() {
            return new BEM({
                block: "imcms-document-type-select-window",
                elements: {
                    "head": documentTypeSelectWindowBuilder.buildHead(texts.title),
                    "body": buildBody()
                }
            }).buildBlockStructure("<div>");
        }

        var documentTypeSelectWindowBuilder = new WindowBuilder({
            factory: buildDocumentTypeSelect,
            onEscKeyPressed: "close"
        });

        var onDocTypeSelectedCallback;

        return {
            build: onDocTypeSelected => {
                onDocTypeSelectedCallback = onDocTypeSelected;
                documentTypeSelectWindowBuilder.buildWindowWithShadow();
            }
        };
    }
);
