/**
 * @author Serhii Maksymchuk from Ubrainians for imCode
 * 15.08.18
 */
Imcms.define(
    'imcms-switch-to-plain-text',
    ['imcms-text-editor-toolbar-button-builder'],
    function (toolbarButtonBuilder) {

        var title = 'Switch to plain text mode'; // todo: localize!!11

        return {
            pluginName: 'switch_to_plain_text',
            initSwitchToPlainText: function (editor) {
                editor.addButton(this.pluginName, {
                    icon: 'switch-to-plain-text-icon',
                    tooltip: title,
                    onclick: function () {
                        alert('switch!')
                    }
                });
            },
            buildSwitchToPlainTextButton: function ($textEditor, isDisabled) {
                return toolbarButtonBuilder.buildButton('switch-to-plain-text-button', title, function () {
                    alert('switch!!')
                }, isDisabled)
            }
        }
    }
);
