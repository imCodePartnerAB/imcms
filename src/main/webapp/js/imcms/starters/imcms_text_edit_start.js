import '../../../css/imcms-imports_files.css';

/**
 * Starter for text edit view.
 *
 * @author Serhii Maksymchuk from Ubrainians for imCode
 * 16.02.18
 */
const toolbarBuilder = require('imcms-standalone-editor-toolbar-builder');

var textEditorInitializer = require("imcms-text-editor-initializer");
var imageEditorInitializer = require("imcms-image-editor-initializer");
var imcms = require("imcms");
var $ = require('jquery');
const texts = require("imcms-i18n-texts");

imcms.textEditorFullScreenEnabled = true;

$(function () {
    textEditorInitializer.initEditor({autoFocus: true});
    imageEditorInitializer.initEditor();

    var $editedTag = $('.imcms-editor-content--text:first');
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
        },
        {
            type: 'language'
        }
    ];

    toolbarBuilder.buildPanel(toolbarContent);
});
