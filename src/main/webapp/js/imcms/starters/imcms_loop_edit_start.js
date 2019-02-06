import '../../../css/imcms-imports_files.css';

/**
 * Starter for loop edit view.
 *
 * @author Serhii Maksymchuk from Ubrainians for imCode
 * 19.02.18
 */

const toolbarBuilder = require('imcms-standalone-editor-toolbar-builder');

var loopEditorInitData = require("imcms-loop-editor-init-data");
var $ = require("jquery");
var events = require("imcms-events");
var imcms = require("imcms");
const texts = require("imcms-i18n-texts");

$(function () {
    events.on("loop editor closed", () => {
        var returnUrl = $("#return-url").val();
        window.location = (returnUrl) ? returnUrl : (imcms.contextPath + "/" + editorData.docId);
    });

    var $editedTag = $(loopEditorInitData.EDIT_AREA_SELECTOR);
    var editorData = $editedTag.data();

    const toolbarContent = [
        {
            type: 'id',
            text: texts.toolbar.documentId + editorData.docId,
            title: texts.toolbar.documentIdTitle,
        },
        {
            type: 'index',
            text: texts.toolbar.elementIndex + editorData.index,
            title: texts.toolbar.elementIndexTitle,
        }
    ];

    loopEditorInitData.editorBuilder.setTag($editedTag).build(editorData);
    toolbarBuilder.buildPanel(toolbarContent);
});
