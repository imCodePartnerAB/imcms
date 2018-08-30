import '../../../css/imcms-imports_files.css';

/**
 * Starter for menu edit view.
 *
 * @author Serhii Maksymchuk from Ubrainians for imCode
 * 19.02.18
 */

var menuEditorInitData = require("imcms-menu-editor-init-data");
var $ = require("jquery");
var events = require("imcms-events");
var imcms = require("imcms");

events.on("menu editor closed", function () {
    var returnUrl = $("#return-url").val();
    window.location = (returnUrl) ? returnUrl : (imcms.contextPath + "/" + editorData.docId);
});

$(function () {
    var $editedTag = $(menuEditorInitData.EDIT_AREA_SELECTOR);
    var editorData = $editedTag.data();
    menuEditorInitData.editorBuilder.setTag($editedTag).build(editorData);
});
