/**
 * Starter for document manager view.
 *
 * @author Serhii Maksymchuk from Ubrainians for imCode
 * 20.02.18
 */
Imcms.require(
    ["imcms-document-editor-builder", "imcms-events", "imcms", "jquery"],

    function (documentEditorBuilder, events, imcms, $) {
        events.on("document-editor-closed", function () {
            var returnUrl = $("#return-url").val();
            window.location = (returnUrl) ? returnUrl : imcms.contextPath;
        });

        documentEditorBuilder.build();
    }
);
