/**
 * @author Serhii Maksymchuk from Ubrainians for imCode
 * 15.08.18
 */
Imcms.define(
    'imcms-switch-to-html-mode',
    ['imcms-text-editor-toolbar-button-builder', 'jquery', 'imcms-text-editor-types', 'imcms-text-editor-utils'],
    function (toolbarButtonBuilder, $, textTypes, textUtils) {

        var title = 'Switch to HTML mode'; // todo: localize!!11

        function getOnSwitch(editor) {
            return function () {
                $(editor.$()).data('type', textTypes.html);
                textUtils.saveContent(editor);
            }
        }

        return {
            pluginName: 'switch_to_html_mode',
            initSwitchToHtmlMode: function (editor) {
                editor.addButton(this.pluginName, {
                    icon: 'switch-to-html-mode-icon',
                    tooltip: title,
                    onclick: getOnSwitch(editor)
                });
            },
            buildSwitchToHtmlModeButton: function (editor, isDisabled) {
                return toolbarButtonBuilder.buildButton(
                    'switch-to-html-mode-button', title, getOnSwitch(editor), isDisabled
                )
            }
        }
    }
);
