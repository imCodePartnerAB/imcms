/**
 * Created by Serhii Maksymchuk from Ubrainians for imCode
 * 04.09.17
 */
Imcms.define("imcms-editor-init-strategy", ["jquery"], function ($) {
    return {
        initEditor: function (editorInitData) {
            var openEditor = function () {
                var $editedTag = $(this).parents(editorInitData.EDIT_AREA_SELECTOR);
                var editorData = $editedTag.data();
                editorInitData.editorBuilder.setTag($editedTag).build(editorData);
            };

            $(editorInitData.EDIT_AREA_SELECTOR).find(editorInitData.CONTROL_SELECTOR).click(openEditor);
        }
    };
});
