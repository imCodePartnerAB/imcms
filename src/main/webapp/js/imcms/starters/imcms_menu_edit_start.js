import '../../../css/imcms-imports_files.css';

/**
 * Starter for menu edit view.
 *
 * @author Serhii Maksymchuk from Ubrainians for imCode
 * 19.02.18
 */

const toolbarBuilder = require('imcms-standalone-editor-toolbar-builder');

var menuEditorInitData = require("imcms-menu-editor-init-data");
var $ = require("jquery");
var events = require("imcms-events");
var imcms = require("imcms");
const texts = require("imcms-i18n-texts");

$(function () {
    events.on("menu editor closed", function () {
        var returnUrl = $("#return-url").val();
        window.location = (returnUrl) ? returnUrl : (imcms.contextPath + "/" + editorData.docId);
    });

    var $editedTag = $(menuEditorInitData.EDIT_AREA_SELECTOR);
    var editorData = $editedTag.data();
    menuEditorInitData.editorBuilder.setTag($editedTag).build(editorData);

    const toolbarContent = [
        {
            type: 'id',
            text: texts.toolbar.documentId + editorData.docId,
            title: texts.toolbar.documentIdTitle,
        },
        {
            type: 'index',
            text: texts.toolbar.elementIndex + editorData.menuIndex,
            title: texts.toolbar.elementIndexTitle,
        }
    ];

    toolbarBuilder.buildPanel(toolbarContent);
});
