/**
 * Text history plugin for Text Editor
 *
 * @author Serhii Maksymchuk from Ubrainians for imCode
 * 08.02.18
 */
define(
    "imcms-text-history-plugin",
    ["imcms-text-history-window-builder", "jquery", 'imcms-text-editor-toolbar-button-builder'],
    function (textHistoryBuilder, $, toolbarButtonBuilder) {

        var title = 'Show text history'; // todo: localize!

        return {
            pluginName: 'text_history',
            initTextHistory: function (editor) {
                editor.addButton(this.pluginName, {
                    icon: 'imcms-text-history-icon',
                    tooltip: title,
                    onclick: function () {
                        var textDTO = $(this.$el).parents(".imcms-editor-area--text")
                            .find(".imcms-editor-content--text")
                            .data();

                        textHistoryBuilder.buildTextHistory(textDTO);
                    }
                });
            },
            buildPlainTextHistoryButton: $textEditor => toolbarButtonBuilder.buildButton('text-history-button', title, function () {
                var textDTO = $textEditor.data();
                textHistoryBuilder.buildTextHistory(textDTO);
            })
        };
    }
);
