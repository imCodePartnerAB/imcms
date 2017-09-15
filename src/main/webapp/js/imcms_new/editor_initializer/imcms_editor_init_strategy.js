/**
 * Created by Serhii Maksymchuk from Ubrainians for imCode
 * 04.09.17
 */
Imcms.define("imcms-editor-init-strategy", ["jquery"], function ($) {
    return {
        initEditor: function (editorInitData) {
            var openEditor = function () {
                var editorData = $(this).parents(editorInitData.EDIT_AREA_SELECTOR).data();
                editorInitData.editorBuilder.build(editorData);
            };

            $(editorInitData.EDIT_AREA_SELECTOR).find(editorInitData.CONTROL_SELECTOR).click(openEditor);
        }
    };
});
