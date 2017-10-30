/**
 * Created by Serhii Maksymchuk from Ubrainians for imCode
 * 04.09.17
 */
Imcms.define("imcms-editors-initializer",
    [
        "imcms-text-editor-initializer", "imcms-image-editor-initializer", "imcms-loop-editor-initializer",
        "imcms-menu-editor-initializer"
    ],
    function (textEditorInit, imageEditorInit, loopEditorInit, menuEditorInit) {
        var editorInitializers = Array.prototype.slice.call(arguments);

        function initEditor(editorInitializer) {
            editorInitializer.initEditor();
        }

        return {
            initEditors: function () {
                editorInitializers.forEach(initEditor);
            }
        };
    }
);
