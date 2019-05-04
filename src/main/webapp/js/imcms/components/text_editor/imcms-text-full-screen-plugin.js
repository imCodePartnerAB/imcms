/**
 * @author Serhii Maksymchuk from Ubrainians for imCode
 * 09.02.18
 */
define(
    "imcms-text-full-screen-plugin",
    ["jquery", "tinymce", "imcms-admin-panel-state", "imcms", 'imcms-text-editor-toolbar-button-builder',
        'imcms-i18n-texts'],
    function ($, tinyMCE, adminPanelState, imcms, toolbarButtonBuilder, texts) {

        texts = texts.toolTipText;

        const title = texts.fullScreen;

        function setEnablingStrategy() {
            if (imcms.textEditorFullScreenEnabled) {
                this.disabled(true);
                this.active(true);
            }
        }

        return {
            pluginName: 'fullscreen',
            initFullScreen: function (editor) {
                editor.addCommand('mceFullscreen', () => {
                    const $editorBody = $(editor.getBody());
                    const $toolBar = $editorBody.parent().children(".imcms-editor-area__text-toolbar");
                    const $body = $("body");

                    if ($editorBody.hasClass('imcms-mce-fullscreen-inline') && !imcms.textEditorFullScreenEnabled) {

                        if (adminPanelState.state === "auto") {
                            adminPanelState.enablePanelAppearance();
                        }

                        $editorBody.removeClass('imcms-mce-fullscreen-inline');
                        $toolBar.removeClass("mce-fullscreen-toolbar");
                        $body.css('overflow', 'auto');

                    } else {
                        if (adminPanelState.state === "auto") {
                            adminPanelState.disablePanelAppearance();
                        }

                        $editorBody.addClass('imcms-mce-fullscreen-inline');
                        $toolBar.addClass("mce-fullscreen-toolbar");
                        $body.css('overflow', 'hidden');
                    }
                    editor.focus();
                });

                editor.addButton(this.pluginName, {
                    icon: 'fullscreen',
                    cmd: 'mceFullscreen',
                    title: title,
                    onPostRender: () => {
                        if (imcms.textEditorFullScreenEnabled) {
                            onClick();
                        }
                        setEnablingStrategy();
                    }
                });
            },
            buildPlainTextEditorButton: $textEditor => {
                const isActive = imcms.textEditorFullScreenEnabled;

                const onClick = () => {
                    const $toolbar = $textEditor.parent()
                        .find('.imcms-editor-area__text-toolbar');

                    isActive ? $toolbar.addClass('mce-fullscreen-toolbar')
                        : $toolbar.toggleClass('mce-fullscreen-toolbar');

                    isActive ? $textEditor.addClass('imcms-mce-fullscreen-inline')
                        : $textEditor.toggleClass('imcms-mce-fullscreen-inline');

                    $textEditor.focus();
                };

                if (isActive) {
                    setTimeout(onClick);
                }

                return toolbarButtonBuilder.buildButton(
                    'text-editor-fullscreen-button', title, onClick, isActive, isActive
                );
            }
        };
    }
);
