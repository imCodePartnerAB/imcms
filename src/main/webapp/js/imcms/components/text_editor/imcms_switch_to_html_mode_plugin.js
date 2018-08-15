/**
 * @author Serhii Maksymchuk from Ubrainians for imCode
 * 15.08.18
 */
Imcms.define(
    'imcms-switch-to-html-mode',
    ['imcms-text-editor-toolbar-button-builder'],
    function (toolbarButtonBuilder) {

        var title = 'Switch to HTML mode'; // todo: localize!!11

        return {
            pluginName: 'switch_to_html_mode',
            initSwitchToHtmlMode: function (editor) {
                editor.addButton(this.pluginName, {
                    icon: 'switch-to-html-mode-icon',
                    tooltip: title,
                    onclick: function () {
                        alert('switch!')
                    }
                });
            },
            buildSwitchToHtmlModeButton: function ($textEditor) {
                return toolbarButtonBuilder.buildButton('switch-to-html-mode-button', title, function () {
                    alert('switch!!')
                })
            }
        }
    }
);
