/**
 * @author Serhii Maksymchuk from Ubrainians for imCode
 * 15.08.18
 */

define(
    'imcms-switch-to-plain-text-plugin',
    ['imcms-text-editor-toolbar-button-builder', 'jquery', 'imcms-text-editor-types', 'imcms-text-editor-utils'],
    function (toolbarButtonBuilder, $, textTypes, textUtils) {

        require('imcms-jquery-tag-replacer');

        const title = 'Switch to plain text mode'; // todo: localize!!11

        function getOnSwitch(editor, transformEditor) {
            return () => {
                let $textEditor = $(editor.$());
                $textEditor.attr('data-type', textTypes.text).data('type', textTypes.text);

                textUtils.saveContent(editor, () => {
                    const textEditor = require('imcms-text-editor');
                    $textEditor = transformEditor($textEditor, editor);
                    textEditor.initTextFromEditor($textEditor);
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
            const $newEditor = $textEditor.clone();
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
            buildDisabledSwitchToPlainTextButton: () => toolbarButtonBuilder.buildButton('switch-to-plain-text-button', title, new Function(), true, true),
            buildSwitchToPlainTextFromHtmlButton: editor => toolbarButtonBuilder.buildButton(
                'switch-to-plain-text-button', title, getOnSwitch(editor, transformFromHtml)
            )
        }
    }
);
