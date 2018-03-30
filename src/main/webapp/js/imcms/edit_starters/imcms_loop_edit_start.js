/**
 * Starter for loop edit view.
 *
 * @author Serhii Maksymchuk from Ubrainians for imCode
 * 19.02.18
 */
Imcms.require(
    ["imcms-loop-editor-init-data", "jquery", "imcms-events", "imcms"],

    function (loopEditorInitData, $, events, imcms) {
        var $editedTag = $(loopEditorInitData.EDIT_AREA_SELECTOR);
        var editorData = $editedTag.data();
        loopEditorInitData.editorBuilder.setTag($editedTag).build(editorData);

        events.on("loop editor closed", function () {
            var returnUrl = $("#return-url").val();
            location = (returnUrl) ? returnUrl : (imcms.contextPath + "/" + editorData.docId);
        });
    }
);
