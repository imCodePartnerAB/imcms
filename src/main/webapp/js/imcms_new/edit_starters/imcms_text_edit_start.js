/**
 * Starter for text edit view.
 *
 * @author Serhii Maksymchuk from Ubrainians for imCode
 * 16.02.18
 */
Imcms.require(
    ["imcms-text-editor-initializer", "imcms-image-editor-initializer"],

    function (textEditorInitializer, imageEditorInitializer) {
        textEditorInitializer.initEditor();
        imageEditorInitializer.initEditor();
    }
);
