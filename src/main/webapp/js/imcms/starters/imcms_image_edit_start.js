import '../../../css/imcms-imports_files.css';

/**
 * Starter for image edit view.
 *
 * @author Serhii Maksymchuk from Ubrainians for imCode
 * 19.02.18
 */
var imageEditorInitData = require("imcms-image-editor-init-data");
var $ = require("jquery");
var events = require("imcms-events");
var imcms = require("imcms");

$(function () {
    events.on("enable text editor blur", function () {
        var returnUrl = $("#return-url").val();
        window.location = (returnUrl) ? returnUrl : (imcms.contextPath + "/" + editorData.docId);
    });

    var $editedTag = $(imageEditorInitData.EDIT_AREA_SELECTOR);
    var editorData = $editedTag.data();
    imageEditorInitData.editorBuilder.setTag($editedTag).build(editorData);
});
