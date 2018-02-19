/**
 * Starter for text edit view.
 *
 * @author Serhii Maksymchuk from Ubrainians for imCode
 * 16.02.18
 */
Imcms.require(
    ["imcms-text-editor-initializer", "imcms-image-editor-initializer", "tinyMCE", "imcms-events", "imcms"],

    function (textEditorInitializer, imageEditorInitializer, tinyMCE, events, imcms) {
        imcms.textEditorFullScreenEnabled = true;
        textEditorInitializer.initEditor();
        imageEditorInitializer.initEditor();
        tinyMCE.activeEditor.fire("focus");
    }
);
