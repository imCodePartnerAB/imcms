/**
 * Starter for text edit view.
 *
 * @author Serhii Maksymchuk from Ubrainians for imCode
 * 16.02.18
 */
const toolbarBuilder = require('imcms-standalone-editor-toolbar-builder');

const textEditorInitializer = require("imcms-text-editor-initializer");
const imageEditorInitializer = require("imcms-image-editor-initializer");
const imcms = require("imcms");
const $ = require('jquery');
const texts = require("imcms-i18n-texts");

imcms.textEditorFullScreenEnabled = true;

$(function () {
    textEditorInitializer.initEditor({autoFocus: true});
    imageEditorInitializer.initEditor();

    const $editedTag = $('.imcms-editor-content--text:first');
    const editorData = $editedTag.data();

    const returnUrl = $("#return-url").val();
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
            type: 'label',
            text: editorData.label ? ('Label ' + editorData.label) : ''
        },
        {
            type: 'placeholder',
            text: editorData.placeholder ? ('Placeholder ' + editorData.placeholder) : ''
        },
        {
            type: 'language'
        },
        {
            type: 'close',
            link: returnUrl ? returnUrl : imcms.contextPath + "/servlet/AdminDoc?meta_id=" + editorData.docId
        }
    ];

    toolbarBuilder.buildPanel(toolbarContent);
});
