/**
 * Starter for text edit view.
 *
 * @author Serhii Maksymchuk from Ubrainians for imCode
 * 16.02.18
 */
Imcms.require(
    ["imcms-text-editor-initializer", "imcms-image-editor-initializer", "tinyMCE", "imcms-events", "imcms", "jquery"],

    function (textEditorInitializer, imageEditorInitializer, tinyMCE, events, imcms, $) {
        imcms.textEditorFullScreenEnabled = true;
        textEditorInitializer.initEditor();
        imageEditorInitializer.initEditor();
        tinyMCE.activeEditor.fire("focus");

        events.on("imcms-version-modified", function () {
            var returnUrl = $("#return-url").val();
            window.location = (returnUrl) ? returnUrl : $("#targetDocId").val();
        })
    }
);
