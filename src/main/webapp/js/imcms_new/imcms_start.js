/**
 * Init script for admin functionality
 *
 * Created by Serhii Maksymchuk from Ubrainians for imCode
 * 07.08.17.
 */
Imcms.require(["imcms-admin-panel-builder", "imcms", "imcms-tests"], function (panelBuilder, imcms, tests) {
    Imcms.tests = tests;
    console.info("%c Tests loaded.", "color: green");

    function detectActiveMenuItem() {
        if (imcms.isEditMode) {
            return 'edit';
        } else if (imcms.isPreviewMode) {
            return 'preview'
        }

        return 'public';
    }

    panelBuilder.buildPanel({
        active: detectActiveMenuItem()
    });
    imcms.isEditMode && imcms.require("imcms-editors-initializer", function (editorsInit) {
        editorsInit.initEditors();
        console.timeEnd("imCMS JS loaded");
    });
});
