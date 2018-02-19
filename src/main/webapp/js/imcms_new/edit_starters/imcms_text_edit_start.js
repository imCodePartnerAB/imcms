/**
 * Starter for text edit view.
 *
 * @author Serhii Maksymchuk from Ubrainians for imCode
 * 16.02.18
 */
Imcms.require(
    ["imcms-text-editor-initializer", "imcms-image-editor-initializer", "tinyMCE"],

    function (textEditorInitializer, imageEditorInitializer, tinyMCE) {
        textEditorInitializer.initEditor();
        imageEditorInitializer.initEditor();
        tinyMCE.activeEditor.fire("focus");
    }
);
