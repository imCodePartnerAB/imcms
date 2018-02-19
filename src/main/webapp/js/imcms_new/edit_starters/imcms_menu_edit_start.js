/**
 * Starter for menu edit view.
 *
 * @author Serhii Maksymchuk from Ubrainians for imCode
 * 19.02.18
 */
Imcms.require(
    ["imcms-menu-editor-init-data", "jquery", "imcms-events", "imcms"],

    function (menuEditorInitData, $, events, imcms) {
        var $editedTag = $(menuEditorInitData.EDIT_AREA_SELECTOR);
        var editorData = $editedTag.data();
        menuEditorInitData.editorBuilder.setTag($editedTag).build(editorData);

        events.on("menu editor closed", function () {
            location = imcms.contextPath + "/" + editorData.docId;
        });
    }
);
