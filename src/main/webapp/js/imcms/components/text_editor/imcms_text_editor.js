/**
 * @author Serhii Maksymchuk from Ubrainians for imCode
 * 15.08.18
 */
Imcms.define(
    'imcms-text-editor',
    [
        'imcms-text-history-plugin', 'imcms-text-validation-plugin', 'imcms-text-full-screen-plugin', 'jquery',
        'imcms-text-discard-changes-plugin', 'imcms-text-editor-utils', 'imcms-text-editor-toolbar-button-builder',
        'imcms-switch-to-plain-text'
    ],
    function (
        textHistory, textValidation, fullScreenPlugin, $, discardChangesPlugin, textEditorUtils, toolbarButtonBuilder,
        switchToPlainTextPlugin
    ) {

        function focusEditorOnControlClick($textEditor) {
            $textEditor.parent()
                .find('.imcms-editor-area__control-wrap')
                .click(function () {
                    $textEditor[0].focus();
                })
        }

        function autoGrow() {
            this.style.cssText = 'height:auto';
            this.style.cssText = 'height:' + this.scrollHeight + 'px';
        }

        var ImcmsTextEditor = function ($textEditor) {
            autoGrow.call($textEditor[0]);
            $textEditor.on('keydown', autoGrow);

            this.$editor = $textEditor;
            this.dirty = false;
            this.startContent = $textEditor.val();

            this.$editor.on('change keyup paste', function () {
                this.dirty = true;
            }.bind(this));

            focusEditorOnControlClick($textEditor);
            textEditorUtils.setEditorFocus(this);
            textEditorUtils.showEditButton($textEditor);
        };

        ImcmsTextEditor.prototype = {
            $: function () {
                return this.$editor
            },
            setContent: function (content) {
                this.$editor.val(content);
                this.setDirty(true);
            },
            getContent: function () {
                return this.$editor.val()
            },
            setDirty: function (isDirty) {
                this.dirty = isDirty;

                var $parent = this.$editor.parent();
                var $discard = $parent.find('.text-editor-discard-changes-button');
                var $save = $parent.find('.text-editor-save-button');

                if (isDirty) {
                    $discard.removeClass('text-toolbar__button--disabled');
                    $save.removeClass('text-toolbar__button--disabled');

                } else {
                    $discard.addClass('text-toolbar__button--disabled');
                    $save.addClass('text-toolbar__button--disabled');
                }
            },
            isDirty: function () {
                return this.dirty;
            },
            triggerBlur: function () {
                textEditorUtils.onEditorBlur(this)
            }
        };

        function buildSaveButton(activeTextEditor) {
            var onClick = function () {
                if (activeTextEditor.isDirty()) textEditorUtils.saveContent(activeTextEditor);
            };

            var $saveButton = toolbarButtonBuilder.buildButton('text-editor-save-button', 'Save', onClick, true);

            activeTextEditor.$().on('change keyup paste', function () {
                $saveButton.removeClass('text-toolbar__button--disabled');
            });

            return $saveButton
        }

        function buildToolbar($textEditor, buttons$) {
            var $toolbarWrapper = $('<div>', {
                'class': 'text-toolbar-wrapper'
            });

            $toolbarWrapper.append(buttons$);

            $textEditor.parent()
                .find('.imcms-editor-area__text-toolbar')
                .append($toolbarWrapper);
        }

        return {
            initPlainTextEditor: function ($textEditor) {
                var activeTextEditor = new ImcmsTextEditor($textEditor);

                buildToolbar($textEditor, [
                    textHistory.buildPlainTextHistoryButton($textEditor),
                    fullScreenPlugin.buildPlainTextEditorButton($textEditor),
                    buildSaveButton(activeTextEditor),
                    discardChangesPlugin.buildPlainTextButton(activeTextEditor)
                ]);
            },
            initHtmlEditor: function ($textEditor) {
                var activeTextEditor = new ImcmsTextEditor($textEditor);

                buildToolbar($textEditor, [
                    textHistory.buildPlainTextHistoryButton($textEditor),
                    textValidation.buildHtmlValidationButton(activeTextEditor),
                    fullScreenPlugin.buildPlainTextEditorButton($textEditor),
                    buildSaveButton(activeTextEditor),
                    discardChangesPlugin.buildPlainTextButton(activeTextEditor),
                    switchToPlainTextPlugin.buildSwitchToPlainTextButton($textEditor)
                ]);
            }
        };
    }
);
