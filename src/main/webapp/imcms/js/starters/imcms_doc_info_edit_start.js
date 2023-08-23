import '../../css/imcms-imports_files.css';

/**
 * Starter for page info edit view.
 *
 * @author Serhii Maksymchuk from Ubrainians for imCode
 * 20.02.18
 */
const pageInfoBuilder = require("imcms-page-info-builder");
const events = require("imcms-events");
const imcms = require("imcms");
const $ = require("jquery");

$(function () {
    const docId = $("#targetDocId").val();

    function onPageInfoClosed() {
        const returnUrl = $("#return-url").val();
        window.location.replace(`/api/redirect?returnUrl=${returnUrl}&metaId=${docId}`);
    }

    events.on("page info closed", onPageInfoClosed);

    pageInfoBuilder.build(docId, onPageInfoClosed);
});
