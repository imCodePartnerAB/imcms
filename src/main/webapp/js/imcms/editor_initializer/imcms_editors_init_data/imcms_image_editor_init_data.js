/**
 * Created by Serhii Maksymchuk from Ubrainians for imCode
 * 04.09.17
 */
Imcms.define("imcms-image-editor-init-data", ["imcms-image-editor-builder"], function (imageEditorBuilder) {
    return {
        EDIT_AREA_SELECTOR: ".imcms-editor-area--image",
        CONTROL_SELECTOR: ".imcms-control--image",
        editorBuilder: imageEditorBuilder
    }
});
