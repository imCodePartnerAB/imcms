/**
 * Created by Serhii Maksymchuk from Ubrainians for imCode
 * 04.09.17
 */
define("imcms-menu-editor-init-data", ["imcms-menu-editor-builder"], function (menuEditorBuilder) {
    return {
        context: 'menu',
        EDIT_AREA_SELECTOR: ".imcms-editor-area--menu",
        CONTROL_SELECTOR: ".imcms-control--menu",
        editorBuilder: menuEditorBuilder
    }
});
