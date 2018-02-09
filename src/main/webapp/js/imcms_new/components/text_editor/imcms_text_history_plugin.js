/**
 * Text history plugin for Text Editor
 *
 * @author Serhii Maksymchuk from Ubrainians for imCode
 * 08.02.18
 * @namespace tinyMCE.activeEditor.getContent
 */
Imcms.define(
    "imcms-text-history-plugin",
    ["imcms-text-history-window-builder", "jquery"],
    function (textHistoryBuilder, $) {
        return {
            initTextHistory: function (editor) {
                editor.addButton('text_history', {
                    icon: 'imcms-text-history-icon',
                    tooltip: 'Show text history',
                    onclick: function () {
                        var textDTO = $(this.$el).parents(".imcms-editor-area--text")
                            .find(".imcms-editor-content--text")
                            .data();

                        textHistoryBuilder.buildTextHistory(textDTO);
                    }
                });
            }
        };
    }
);
