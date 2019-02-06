/**
 * Created by Serhii Maksymchuk from Ubrainians for imCode
 * 04.09.17
 */
define("imcms-editors-initializer",
    [
        "imcms-text-editor-initializer", "imcms-image-editor-initializer", "imcms-loop-editor-initializer",
        "imcms-menu-editor-initializer", "imcms-editor-labels-initializer"
    ],
    function (textEditorInit, imageEditorInit, loopEditorInit, menuEditorInit, editorLabelInitializer) {
        const editorInitializers = [textEditorInit, imageEditorInit, loopEditorInit, menuEditorInit];

        function initEditor(editorInitializer) {
            editorInitializer.initEditor();
        }

        return {
            initEditors: () => {
                editorLabelInitializer.initEditorLabels();
                editorInitializers.forEach(initEditor);
            }
        };
    }
);
