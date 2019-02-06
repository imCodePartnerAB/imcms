import '../../../css/imcms-imports_files.css';

/**
 * Starter for content manager view.
 *
 * @author Serhii Maksymchuk from Ubrainians for imCode
 * 20.02.18
 */
var contentManagerBuilder = require("imcms-content-manager-builder");
var events = require("imcms-events");
var imcms = require("imcms");
var $ = require("jquery");

imcms.disableContentManagerSaveButton = true;

events.on("content manager closed", () => {
    var returnUrl = $("#return-url").val();
    window.location = (returnUrl) ? returnUrl : imcms.contextPath;
});

$(function () {
    contentManagerBuilder.build();
});
