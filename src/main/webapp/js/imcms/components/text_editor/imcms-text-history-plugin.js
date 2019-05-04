/**
 * Text history plugin for Text Editor
 *
 * @author Serhii Maksymchuk from Ubrainians for imCode
 * 08.02.18
 */
define(
    "imcms-text-history-plugin",
    ["imcms-text-history-window-builder", "jquery", 'imcms-text-editor-toolbar-button-builder',
        "imcms-i18n-texts"],
    function (textHistoryBuilder, $, toolbarButtonBuilder, texts) {

        texts = texts.toolTipText;

        const title = texts.textHistory;

        return {
            pluginName: 'text_history',
            initTextHistory: function (editor) {
                editor.addButton(this.pluginName, {
                    icon: 'imcms-text-history-icon',
                    tooltip: title,
                    onclick: function () {
                        const textDTO = $(this.$el).parents(".imcms-editor-area--text")
                            .find(".imcms-editor-content--text")
                            .data();

                        textHistoryBuilder.buildTextHistory(textDTO);
                    }
                });
            },
            buildPlainTextHistoryButton: $textEditor => toolbarButtonBuilder.buildButton('text-history-button', title, function () {
                const textDTO = $textEditor.data();
                textHistoryBuilder.buildTextHistory(textDTO);
            })
        };
    }
);
