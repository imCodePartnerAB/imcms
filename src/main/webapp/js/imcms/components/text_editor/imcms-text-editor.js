/**
 * @author Serhii Maksymchuk from Ubrainians for imCode
 * 15.08.18
 */
define(
    'imcms-text-editor',
    [
        'imcms-text-history-plugin', 'imcms-text-validation-plugin', 'imcms-text-full-screen-plugin', 'jquery',
        'imcms-text-discard-changes-plugin', 'imcms-text-editor-utils', 'imcms-text-editor-toolbar-button-builder',
        'imcms-switch-to-plain-text-plugin', 'imcms-switch-to-html-mode-plugin', 'imcms-switch-to-text-editor-plugin',
        'imcms-html-filtering-policy-plugin', 'imcms-i18n-texts'
    ],
    function (
        textHistory, textValidation, fullScreenPlugin, $, discardChangesPlugin, textEditorUtils, toolbarButtonBuilder,
        switchToPlainTextPlugin, switchToHtmlModePlugin, switchToTextEditorPlugin, htmlFilteringPolicyPlugin, texts
    ) {

        texts = texts.toolTipText;

        function focusEditorOnControlClick($textEditor) {
            $textEditor.parent()
                .find('.imcms-editor-area__control-wrap')
                .click(() => {
                    $textEditor[0].focus();
                });
        }

        function autoGrow(e) {
            this.style.cssText = 'height:auto';
            this.style.cssText = 'height:' + (this.scrollHeight + ((e && (e.which === 13)) ? 15 : 0)) + 'px';
        }

        const ImcmsTextEditor = function ($textEditor) {
            let rows = $textEditor.attr('rows');

            if (!rows) {
                autoGrow.call($textEditor[0]);
                $textEditor.on('keydown', autoGrow);
            }

            this.$editor = $textEditor;
            this.dirty = false;
            this.startContent = $textEditor.val();

            this.$editor.on('change keyup paste', () => this.setDirty(true));

            focusEditorOnControlClick($textEditor);
            textEditorUtils.setEditorFocus(this);
            textEditorUtils.showEditButton($textEditor);
        };

        ImcmsTextEditor.prototype = {
            $: function () {
                return this.$editor;
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

                const $parent = this.$editor.parent();
                const $discard = $parent.find('.text-editor-discard-changes-button');
                const $save = $parent.find('.text-editor-save-button');

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
                textEditorUtils.onEditorBlur(this);
            },
            then: function (onLoad) {
                onLoad(this.$editor);
            }
        };

        function buildSaveButton(activeTextEditor) {
            const onClick = () => {
                if (activeTextEditor.isDirty()) textEditorUtils.saveContent(activeTextEditor);
            };
            return toolbarButtonBuilder.buildButton('text-editor-save-button', texts.save, onClick, true);
        }

        function buildToolbar($textEditor, buttons$) {
            const $toolbarWrapper = $('<div>', {
                'class': 'text-toolbar-wrapper'
            });

            $toolbarWrapper.append(buttons$);

            $textEditor.parent()
                .find('.imcms-editor-area__text-toolbar')
                .append($toolbarWrapper);
        }

        return {
            initPlainTextEditor: $textEditor => {
                const editor = new ImcmsTextEditor($textEditor);

                buildToolbar($textEditor, [
                    textHistory.buildPlainTextHistoryButton($textEditor),
                    fullScreenPlugin.buildPlainTextEditorButton($textEditor),
                    buildSaveButton(editor),
                    discardChangesPlugin.buildPlainTextButton(editor)
                ]);

                return editor;
            },
            initHtmlEditor: $textEditor => {
                const editor = new ImcmsTextEditor($textEditor);

                buildToolbar($textEditor, [
                    textHistory.buildPlainTextHistoryButton($textEditor),
                    textValidation.buildHtmlValidationButton(editor),
                    fullScreenPlugin.buildPlainTextEditorButton($textEditor),
                    buildSaveButton(editor),
                    discardChangesPlugin.buildPlainTextButton(editor),
                    htmlFilteringPolicyPlugin.buildHtmlFilteringPolicyButton(editor)
                ]);

                return editor;
            },
            initTextFromEditor: $textEditor => {
                const editor = new ImcmsTextEditor($textEditor);

                buildToolbar($textEditor, [
                    textHistory.buildPlainTextHistoryButton($textEditor),
                    fullScreenPlugin.buildPlainTextEditorButton($textEditor),
                    buildSaveButton(editor),
                    discardChangesPlugin.buildPlainTextButton(editor),
                    switchToPlainTextPlugin.buildDisabledSwitchToPlainTextButton(),
                    switchToHtmlModePlugin.buildSwitchToHtmlModeFromPlainTextButton(editor),
                    switchToTextEditorPlugin.buildSwitchToTextEditorButton(editor)
                ]);

                return editor;
            },
            initHtmlFromEditor: $textEditor => {
                const editor = new ImcmsTextEditor($textEditor);

                buildToolbar($textEditor, [
                    textHistory.buildPlainTextHistoryButton($textEditor),
                    textValidation.buildHtmlValidationButton(editor),
                    fullScreenPlugin.buildPlainTextEditorButton($textEditor),
                    buildSaveButton(editor),
                    discardChangesPlugin.buildPlainTextButton(editor),
                    switchToPlainTextPlugin.buildSwitchToPlainTextFromHtmlButton(editor),
                    switchToHtmlModePlugin.buildDisabledSwitchToHtmlModeButton(),
                    switchToTextEditorPlugin.buildSwitchToTextEditorButton(editor),
                    htmlFilteringPolicyPlugin.buildHtmlFilteringPolicyButton(editor)
                ]);

                return editor;
            }
        };
    }
);
