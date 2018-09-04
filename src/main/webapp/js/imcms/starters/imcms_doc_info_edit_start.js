import '../../../css/imcms-imports_files.css';

/**
 * Starter for page info edit view.
 *
 * @author Serhii Maksymchuk from Ubrainians for imCode
 * 20.02.18
 */
var pageInfoBuilder = require("imcms-page-info-builder");
var events = require("imcms-events");
var imcms = require("imcms");
var $ = require("jquery");

$(function () {
    var docId = $("#targetDocId").val();

    function onPageInfoClosed() {
        var returnUrl = $("#return-url").val();
        window.location = (returnUrl) ? returnUrl : (imcms.contextPath + "/" + docId);
    }

    events.on("page info closed", onPageInfoClosed);

    pageInfoBuilder.build(docId, onPageInfoClosed);
});
