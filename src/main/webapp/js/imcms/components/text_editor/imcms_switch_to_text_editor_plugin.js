/**
 * @author Serhii Maksymchuk from Ubrainians for imCode
 * 15.08.18
 */
Imcms.define(
    'imcms-switch-to-text-editor',
    ['imcms-text-editor-toolbar-button-builder', 'jquery', 'imcms-text-editor-types', 'imcms-text-editor-utils'],
    function (toolbarButtonBuilder, $, textTypes, textUtils) {

        var title = 'Switch to text editor'; // todo: localize!!11

        function getOnSwitch(editor) {
            return function () {
                $(editor.$()).data('type', textTypes.editor);
                textUtils.saveContent(editor);
            }
        }

        return {
            pluginName: 'switch-to-text-editor',
            initSwitchToTextEditor: function (editor) {
                editor.addButton(this.pluginName, {
                    icon: 'switch-to-text-editor-icon',
                    tooltip: title,
                    onclick: getOnSwitch(editor),
                    onPostRender: function () {
                        this.disabled(true)
                    }
                });
            },
            buildSwitchToTextEditorButton: function (editor) {
                return toolbarButtonBuilder.buildButton('switch-to-text-editor-button', title, getOnSwitch(editor))
            }
        }
    }
);
