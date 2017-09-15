/**
 * Created by Serhii Maksymchuk from Ubrainians for imCode
 * 21.07.17.
 */
Imcms.require(["imcms-tests", "imcms-admin-panel-builder"], function (tests, panelBuilder) {
    Imcms.tests = tests;
    console.info("%c Tests loaded.", "color: green");

    panelBuilder.buildPanel({active: 'public'});

    console.timeEnd("imCMS JS loaded");
});
