/**
 * Created by Serhii Maksymchuk from Ubrainians for imCode
 * 04.09.17
 */
define("imcms-loop-editor-init-data", ["imcms-loop-editor-builder"], function (loopEditorBuilder) {
    return {
        EDIT_AREA_SELECTOR: ".imcms-editor-area--loop",
        CONTROL_SELECTOR: ".imcms-control--loop",
        editorBuilder: loopEditorBuilder
    }
});
