/**
 * Created by Serhii Maksymchuk from Ubrainians for imCode
 * 04.09.17
 */
define("imcms-menu-editor-initializer",
    ["imcms-menu-editor-init-data", "imcms-editor-init-strategy"],
    function (editorInitData, editorInitStrategy) {
        return {
            initEditor: function () {
                editorInitStrategy.initEditor(editorInitData);
            }
        };
    }
);
