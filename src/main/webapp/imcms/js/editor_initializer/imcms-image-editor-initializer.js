/**
 * Created by Serhii Maksymchuk from Ubrainians for imCode
 * 04.09.17
 */
define("imcms-image-editor-initializer",
    ["imcms-image-editor-init-data", "imcms-editor-init-strategy"],
    function (editorInitData, editorInitStrategy) {
        return {
            initEditor: () => {
                editorInitStrategy.initEditor(editorInitData);
            }
        }
    }
);
