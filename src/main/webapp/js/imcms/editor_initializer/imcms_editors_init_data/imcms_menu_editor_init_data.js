/**
 * Created by Serhii Maksymchuk from Ubrainians for imCode
 * 04.09.17
 */
Imcms.define("imcms-menu-editor-init-data", ["imcms-menu-editor-builder"], function (menuEditorBuilder) {
    return {
        EDIT_AREA_SELECTOR: ".imcms-editor-area--menu",
        CONTROL_SELECTOR: ".imcms-control--menu",
        editorBuilder: menuEditorBuilder
    }
});
