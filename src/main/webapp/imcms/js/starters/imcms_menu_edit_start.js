/**
 * Starter for menu edit view.
 *
 * @author Serhii Maksymchuk from Ubrainians for imCode
 * 19.02.18
 */

const toolbarBuilder = require('imcms-standalone-editor-toolbar-builder');

const menuEditorInitData = require("imcms-menu-editor-init-data");
const $ = require("jquery");
const events = require("imcms-events");
const imcms = require("imcms");
const texts = require("imcms-i18n-texts");

$(function () {
    events.on("menu editor closed", () => {
        const returnUrl = $("#return-url").val();
        window.location.replace(`${imcms.contextPath}/api/redirect?returnUrl=${returnUrl}&metaId=${editorData.docId}`);
    });

    const $editedTag = $(menuEditorInitData.EDIT_AREA_SELECTOR);
    var editorData = $editedTag.data();
    menuEditorInitData.editorBuilder.standalone().setTag($editedTag).build(editorData);

    const returnUrl = $("#return-url").val();
    const adminDocLink = "/servlet/AdminDoc?meta_id=" + editorData.docId;
    const toolbarContent = [
        {
            type: 'logo',
            link: adminDocLink
        },
        {
            type: 'id',
            text: texts.toolbar.documentId + editorData.docId,
            title: texts.toolbar.documentIdTitle,
        },
        {
            type: 'index',
            text: texts.toolbar.elementIndex + editorData.menuIndex,
            title: texts.toolbar.elementIndexTitle,
        }, {
            type: 'label',
            text: editorData.label ? ('Label ' + editorData.label) : ''
        }, {
			type: 'language'
	    },
        {
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
