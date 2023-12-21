/**
 * Starter for image edit view.
 *
 * @author Serhii Maksymchuk from Ubrainians for imCode
 * 19.02.18
 */
const toolbarBuilder = require('imcms-standalone-editor-toolbar-builder');
const imageEditorInitData = require("imcms-image-editor-init-data");
const $ = require("jquery");
const events = require("imcms-events");
const imcms = require("imcms");
const texts = require("imcms-i18n-texts");

$(() => {
    events.on("enable text editor blur", () => {
        const returnUrl = $("#return-url").val();
        window.location.replace(`${imcms.contextPath}/api/redirect?returnUrl=${returnUrl}&metaId=${editorData.docId}`);
    });

    const $editedTag = $(imageEditorInitData.EDIT_AREA_SELECTOR);
    const editorData = $editedTag.data();

    imageEditorInitData.editorBuilder.setTag($editedTag).build(editorData);

    const returnUrl = $("#return-url").val();
    const adminDocLink = "/servlet/AdminDoc?meta_id=" + editorData.docId;
    const toolbarContent = [
        {
            type: 'logo',
            link: adminDocLink
        }, {
            type: 'id',
            text: texts.toolbar.documentId + editorData.docId,
            title: texts.toolbar.documentIdTitle,
        }, {
            type: 'index',
            text: texts.toolbar.elementIndex + editorData.index,
            title: texts.toolbar.elementIndexTitle,
        }, {
            type: 'label',
            text: editorData.label ? ('Label ' + editorData.label) : ''
        }, {
            type: 'language'
        }, {
            type: 'close',
            link: imcms.contextPath + ((returnUrl) ? returnUrl : adminDocLink),
            showIfSeparate: true
        }
    ];

    $("body")
        .css("margin", "0")
        .addClass("standalone-editor-body")
        .prepend(toolbarBuilder.buildPanel(toolbarContent));
});
