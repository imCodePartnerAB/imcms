/**
 * Starter for page info edit view.
 *
 * @author Serhii Maksymchuk from Ubrainians for imCode
 * 20.02.18
 */
Imcms.require(
    ["imcms-page-info-builder", "imcms-events", "imcms", "jquery"],

    function (pageInfoBuilder, events, imcms, $) {
        var docId = $("#targetDocId").val();

        function onPageInfoClosed() {
            var returnUrl = $("#return-url").val();
            window.location = (returnUrl) ? returnUrl : (imcms.contextPath + "/" + docId);
        }

        events.on("page info closed", onPageInfoClosed);

        pageInfoBuilder.build(docId, onPageInfoClosed);
    }
);
