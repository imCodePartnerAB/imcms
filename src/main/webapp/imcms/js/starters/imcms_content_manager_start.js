/**
 * Starter for content manager view.
 *
 * @author Serhii Maksymchuk from Ubrainians for imCode
 * 20.02.18
 */
const contentManagerBuilder = require("imcms-content-manager-builder");
const events = require("imcms-events");
const imcms = require("imcms");
const $ = require("jquery");

imcms.disableContentManagerSaveButton = true;

events.on("content manager closed", () => {
    const returnUrl = $("#return-url").val();
    window.location.replace(`${imcms.contextPath}/api/redirect?returnUrl=${returnUrl}`);
});

$(function () {
    contentManagerBuilder.build();
});
