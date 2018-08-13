/**
 * Text history plugin for Text Editor
 *
 * @author Serhii Maksymchuk from Ubrainians for imCode
 * 08.02.18
 * @namespace tinyMCE.activeEditor.getContent
 */
Imcms.define(
    "imcms-text-history-plugin",
    ["imcms-text-history-window-builder", "jquery", 'imcms-bem-builder'],
    function (textHistoryBuilder, $, BEM) {
        return {
            pluginName: 'text_history',
            initTextHistory: function (editor) {
                editor.addButton(this.pluginName, {
                    icon: 'imcms-text-history-icon',
                    tooltip: 'Show text history',
                    onclick: function () {
                        var textDTO = $(this.$el).parents(".imcms-editor-area--text")
                            .find(".imcms-editor-content--text")
                            .data();

                        textHistoryBuilder.buildTextHistory(textDTO);
                    }
                });
            },
            buildPlainTextHistoryButton: function ($textEditor) {
                return new BEM({
                    block: 'text-history-button',
                    elements: {
                        'icon': $('<div>', {
                            'class': 'text-toolbar__icon'
                        })
                    }
                }).buildBlockStructure('<div>', {
                    class: 'text-toolbar__button',
                    click: function () {
                        var textDTO = $textEditor.data();
                        console.log(textDTO);
                        textHistoryBuilder.buildTextHistory(textDTO);
                    }
                })
            }
        };
    }
);
