/**
 * @author Serhii Maksymchuk from Ubrainians for imCode
 * 15.08.18
 */
Imcms.define(
    'imcms-switch-to-html-mode',
    [
        'imcms-text-editor-toolbar-button-builder', 'jquery', 'imcms-text-editor-types', 'imcms-text-editor-utils',
        'imcms', /* this must be the last ->*/'imcms-tag-replacer'
    ],
    function (toolbarButtonBuilder, $, textTypes, textUtils, imcms) {

        var title = 'Switch to HTML mode'; // todo: localize!!11

        function getOnSwitch(editor, transformEditor) {
            return function () {
                var $textEditor = $(editor.$());
                $textEditor.attr('data-type', textTypes.html).data('type', textTypes.html);

                textUtils.saveContent(editor, function () {
                    imcms.require('imcms-text-editor', function (textEditor) {
                        $textEditor = transformEditor($textEditor, editor);
                        textEditor.initHtmlFromEditor($textEditor);
                    })
                });
            }
        }

        function transformFromTinyMce($textEditor, editor) {
            editor.remove();

            return $textEditor.replaceTagName('textarea')
                .removeAttr('style')
                .removeAttr('contenteditable')
                .attr('wrap', 'hard')
        }

        function transformFromPlainText($textEditor) {
            $textEditor.parent().find('.imcms-editor-area__text-toolbar').empty();
            var $newEditor = $textEditor.clone();
            $textEditor.replaceWith($newEditor);

            return $newEditor
        }

        return {
            pluginName: 'switch_to_html_mode',
            initSwitchToHtmlMode: function (editor) {
                editor.addButton(this.pluginName, {
                    icon: 'switch-to-html-mode-icon',
                    tooltip: title,
                    onclick: getOnSwitch(editor, transformFromTinyMce)
                });
            },
            buildDisabledSwitchToHtmlModeButton: function () {
                return toolbarButtonBuilder.buildButton('switch-to-html-mode-button', title, new Function(), true, true)
            },
            buildSwitchToHtmlModeFromPlainTextButton: function (editor) {
                return toolbarButtonBuilder.buildButton(
                    'switch-to-html-mode-button', title, getOnSwitch(editor, transformFromPlainText)
                )
            }
        }
    }
);
