import '../../../css/imcms-imports_files.css';

/**
 * Starter for document manager view.
 *
 * @author Serhii Maksymchuk from Ubrainians for imCode
 * 20.02.18
 */

var documentEditorBuilder = require("imcms-document-editor-builder");
var events = require("imcms-events");
var imcms = require("imcms");
var $ = require("jquery");

events.on("document-editor-closed", () => {
    var returnUrl = $("#return-url").val();
    window.location = (returnUrl) ? returnUrl : imcms.contextPath;
});

$(function () {
    documentEditorBuilder.build();
});
