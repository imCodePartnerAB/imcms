/**
 * Starter for content manager view.
 *
 * @author Serhii Maksymchuk from Ubrainians for imCode
 * 20.02.18
 */
Imcms.require(
    ["imcms-content-manager-builder", "imcms-events", "imcms", "jquery"],

    function (contentManagerBuilder, events, imcms, $) {
        imcms.disableContentManagerSaveButton = true;
        events.on("content manager closed", function () {
            var returnUrl = $("#return-url").val();
            window.location = (returnUrl) ? returnUrl : imcms.contextPath;
        });

        contentManagerBuilder.build();
    }
);
