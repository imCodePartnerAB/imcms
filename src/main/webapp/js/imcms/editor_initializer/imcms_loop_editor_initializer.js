/**
 * Created by Serhii Maksymchuk from Ubrainians for imCode
 * 04.09.17
 */
Imcms.define("imcms-loop-editor-initializer",
    ["imcms-loop-editor-init-data", "imcms-editor-init-strategy"],
    function (editorInitData, editorInitStrategy) {
        return {
            initEditor: function () {
                editorInitStrategy.initEditor(editorInitData);
            }
        };
    }
);
