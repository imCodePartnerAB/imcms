/**
 * Init script for admin functionality
 *
 * Created by Serhii Maksymchuk from Ubrainians for imCode
 * 07.08.17.
 */
Imcms.require(
    ["imcms-admin-panel-builder", "imcms", "imcms-tests", "imcms-events", "imcms-i18n-texts"],
    function (panelBuilder, imcms, tests, events, texts) {
        Imcms.tests = tests;
        console.info("%c Tests loaded.", "color: green");

        events.on("imcms-version-modified", function () {
            imcms.document.hasNewerVersion = true;
        });

        events.on("imcms-publish-new-version-current-doc", function () {
            window.location.href = imcms.contextPath + "/api/publish-document/" + imcms.document.id;
        });

        setTimeout(function () {
            alert(texts.sessionExpiredMessage);
            window.location.href = imcms.contextPath + "/login";
        }, imcms.expiredSessionTime);

        function detectActivePanelButton() {
            if (imcms.isEditMode) {
                return 'edit';
            }

            if (imcms.isPreviewMode) {
                return 'preview'
            }

            return 'public';
        }

        panelBuilder.buildPanel({
            active: detectActivePanelButton()
        });
        imcms.isEditMode && imcms.require(["imcms-editors-initializer", "jquery"], function (editorsInit, $) {
            $(editorsInit.initEditors);
            console.timeEnd("imCMS JS loaded");
        });
    }
);
