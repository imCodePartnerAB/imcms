/**
 * @author Serhii Maksymchuk from Ubrainians for imCode
 * 15.08.18
 */
Imcms.define(
    'imcms-switch-to-plain-text',
    ['imcms-text-editor-toolbar-button-builder', 'jquery', 'imcms-text-editor-types', 'imcms-text-editor-utils'],
    function (toolbarButtonBuilder, $, textTypes, textUtils) {

        var title = 'Switch to plain text mode'; // todo: localize!!11

        function getOnSwitch(editor) {
            return function () {
                $(editor.$()).data('type', textTypes.text);
                textUtils.saveContent(editor);
            }
        }

        return {
            pluginName: 'switch_to_plain_text',
            initSwitchToPlainText: function (editor) {
                editor.addButton(this.pluginName, {
                    icon: 'switch-to-plain-text-icon',
                    tooltip: title,
                    onclick: getOnSwitch(editor)
                });
            },
            buildSwitchToPlainTextButton: function (editor, isDisabled) {
                return toolbarButtonBuilder.buildButton(
                    'switch-to-plain-text-button', title, getOnSwitch(editor), isDisabled
                )
            }
        }
    }
);
