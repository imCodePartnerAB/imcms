/**
 * @author Serhii Maksymchuk from Ubrainians for imCode
 * 15.08.18
 */
Imcms.define(
    'imcms-switch-to-text-editor',
    [
        'imcms-text-editor-toolbar-button-builder', 'jquery', 'imcms-text-editor-types', 'imcms-text-editor-utils',
        'imcms', /* this must be the last ->*/'imcms-tag-replacer'
    ],
    function (toolbarButtonBuilder, $, textTypes, textUtils, imcms) {

        var title = 'Switch to text editor'; // todo: localize!!11

        function getOnSwitch(editor) {
            return function () {
                var $textEditor = $(editor.$());
                $textEditor.attr('data-type', textTypes.editor).data('type', textTypes.editor);

                textUtils.saveContent(editor, function () {
                    imcms.require('imcms-tinymce-text-editor', function (tinyMceTextEditor) {
                        var content = $textEditor.val();
                        $textEditor.parent().find('.imcms-editor-area__text-toolbar').empty();
                        $textEditor = $textEditor.replaceTagName('div').removeAttr('wrap').removeAttr('style');
                        $textEditor.html(content);

                        tinyMceTextEditor.init($textEditor).then(function (editor) {
                            editor[0].focus();
                        });
                    })
                });
            }
        }

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
            buildSwitchToTextEditorButton: function (editor) {
                return toolbarButtonBuilder.buildButton('switch-to-text-editor-button', title, getOnSwitch(editor))
            }
        }
    }
);
