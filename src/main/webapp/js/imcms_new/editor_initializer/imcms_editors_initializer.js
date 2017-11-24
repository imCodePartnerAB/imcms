/**
 * Created by Serhii Maksymchuk from Ubrainians for imCode
 * 04.09.17
 */
Imcms.define("imcms-editors-initializer",
    [
        "imcms-text-editor-initializer", "imcms-image-editor-initializer", "imcms-loop-editor-initializer",
        "imcms-menu-editor-initializer", "imcms-editor-labels-initializer"
    ],
    function (textEditorInit, imageEditorInit, loopEditorInit, menuEditorInit, editorLabelInitializer) {
        var editorInitializers = [textEditorInit, imageEditorInit, loopEditorInit, menuEditorInit];

        function initEditor(editorInitializer) {
            editorInitializer.initEditor();
        }

        return {
            initEditors: function () {
                editorLabelInitializer.initEditorLabels();
                editorInitializers.forEach(initEditor);
            }
        };
    }
);
