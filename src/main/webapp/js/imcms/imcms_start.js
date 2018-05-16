/**
 * Init script for admin functionality
 *
 * Created by Serhii Maksymchuk from Ubrainians for imCode
 * 07.08.17.
 */
Imcms.require(
    [
        "imcms-admin-panel-builder", "imcms", "imcms-tests", "imcms-events", "imcms-session-timeout-management",
        "date-format"
    ],
    function (panelBuilder, imcms, tests, events, sessionTimeoutManagement, dateFormatter) {
        Imcms.tests = tests;
        console.info("%c Tests loaded.", "color: green");

        events.on("imcms-version-modified", function () {
            imcms.document.hasNewerVersion = true;
        });

        events.on("imcms-publish-new-version-current-doc", function () {
            window.location.href = imcms.contextPath + "/api/publish-document/" + imcms.document.id;
            alert("The version is published with status APPROVED " + dateFormatter.format(new Date(), "yyyy-mm-dd HH:MM"));
        });

        sessionTimeoutManagement.initOrUpdateSessionTimeout();

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
