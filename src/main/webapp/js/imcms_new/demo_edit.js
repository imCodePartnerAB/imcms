/**
 * Created by Serhii Maksymchuk from Ubrainians for imCode
 * 01.09.17
 */
Imcms.require(
    ["imcms-tests", "imcms-admin-panel-builder", "imcms-editors-initializer"],
    function (tests, panelBuilder, editorsInit) {
        Imcms.tests = tests;
        console.info("%c Tests loaded.", "color: green");

        panelBuilder.buildPanel({active: 'edit'});
        editorsInit.initEditors();

        console.timeEnd("imCMS JS loaded");
    }
);
