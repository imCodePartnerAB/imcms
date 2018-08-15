/**
 * @author Serhii Maksymchuk from Ubrainians for imCode
 * 15.08.18
 */
Imcms.define(
    'imcms-switch-to-text-editor',
    ['imcms-text-editor-toolbar-button-builder'],
    function (toolbarButtonBuilder) {

        var title = 'Switch to text editor'; // todo: localize!!11

        return {
            pluginName: 'switch-to-text-editor',
            initSwitchToTextEditor: function (editor) {
                editor.addButton(this.pluginName, {
                    icon: 'switch-to-text-editor-icon',
                    tooltip: title,
                    onclick: new Function(),
                    onPostRender: function () {
                        this.disabled(true)
                    }
                });
            },
            buildSwitchToTextEditorButton: function ($textEditor) {
                return toolbarButtonBuilder.buildButton('switch-to-text-editor-button', title, function () {
                    alert('switch!!!')
                })
            }
        }
    }
);
