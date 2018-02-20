/**
 * Starter for document manager view.
 *
 * @author Serhii Maksymchuk from Ubrainians for imCode
 * 20.02.18
 */
Imcms.require(
    ["imcms-document-editor-builder", "imcms-events", "imcms"],

    function (documentEditorBuilder, events, imcms) {
        events.on("document-editor-closed", function () {
            location = imcms.contextPath;
        });

        documentEditorBuilder.build();
    }
);
