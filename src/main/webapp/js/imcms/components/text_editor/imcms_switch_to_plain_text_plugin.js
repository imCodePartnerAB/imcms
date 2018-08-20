/**
 * @author Serhii Maksymchuk from Ubrainians for imCode
 * 15.08.18
 */
Imcms.define(
    'imcms-switch-to-plain-text',
    [
        'imcms-text-editor-toolbar-button-builder', 'jquery', 'imcms-text-editor-types', 'imcms-text-editor-utils',
        'imcms', /* this must be the last ->*/'imcms-tag-replacer'
    ],
    function (toolbarButtonBuilder, $, textTypes, textUtils, imcms) {

        var title = 'Switch to plain text mode'; // todo: localize!!11

        function getOnSwitch(editor, transformEditor) {
            return function () {
                var $textEditor = $(editor.$());
                $textEditor.attr('data-type', textTypes.text).data('type', textTypes.text);

                textUtils.saveContent(editor, function () {
                    imcms.require('imcms-text-editor', function (textEditor) {
                        $textEditor = transformEditor($textEditor, editor);
                        textEditor.initTextFromEditor($textEditor);
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

        function transformFromHtml($textEditor) {
            $textEditor.parent().find('.imcms-editor-area__text-toolbar').empty();
            var $newEditor = $textEditor.clone();
            $textEditor.replaceWith($newEditor);

            return $newEditor
        }

        return {
            pluginName: 'switch_to_plain_text',
            initSwitchToPlainText: function (editor) {
                editor.addButton(this.pluginName, {
                    icon: 'switch-to-plain-text-icon',
                    tooltip: title,
                    onclick: getOnSwitch(editor, transformFromTinyMce)
                });
            },
            buildDisabledSwitchToPlainTextButton: function () {
                return toolbarButtonBuilder.buildButton('switch-to-plain-text-button', title, new Function(), true, true)
            },
            buildSwitchToPlainTextFromHtmlButton: function (editor) {
                return toolbarButtonBuilder.buildButton(
                    'switch-to-plain-text-button', title, getOnSwitch(editor, transformFromHtml)
                )
            }
        }
    }
);
