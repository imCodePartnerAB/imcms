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
        var editors = Array.prototype.slice.call(arguments);

        function initEditor(editor) {
            editor.initEditor();
        }

        return {
            initEditors: function () {
                editors.forEach(initEditor);
            }
        };
    }
);
