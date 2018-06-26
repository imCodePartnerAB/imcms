/**
 * Starter for image edit view.
 *
 * @author Serhii Maksymchuk from Ubrainians for imCode
 * 19.02.18
 */
Imcms.require(
    ["imcms-image-editor-init-data", "jquery", "imcms-events", "imcms"],

    function (imageEditorInitData, $, events, imcms) {
        events.on("enable text editor blur", function () {
            var returnUrl = $("#return-url").val();
            window.location = (returnUrl) ? returnUrl : (imcms.contextPath + "/" + editorData.docId);
        });

        var $editedTag = $(imageEditorInitData.EDIT_AREA_SELECTOR);
        var editorData = $editedTag.data();
        imageEditorInitData.editorBuilder.setTag($editedTag).build(editorData);
    }
);
