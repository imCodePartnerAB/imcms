/**
 * @author Serhii Maksymchuk from Ubrainians for imCode
 * 09.02.18
 */
define(
    "imcms-text-full-screen-plugin",
    ["jquery", "tinymce", "imcms-admin-panel-state", "imcms", 'imcms-text-editor-toolbar-button-builder'],
    function ($, tinyMCE, adminPanelState, imcms, toolbarButtonBuilder) {

        var title = 'Fullscreen'; // todo: localize!

        function setEnablingStrategy() {
            if (imcms.textEditorFullScreenEnabled) {
                this.disabled(true);
                this.active(true);
            }
        }

        return {
            pluginName: 'fullscreen',
            initFullScreen: function (editor) {
                function onClick() {
                    var $editorBody = $(editor.getBody());
                    var $toolBar = $editorBody.parent().children(".imcms-editor-area__text-toolbar");
                    var $body = $("body");

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
                }

                editor.addButton(this.pluginName, {
                    icon: 'fullscreen',
                    click: onClick,
                    title: title,
                    onPostRender: function () {
                        if (imcms.textEditorFullScreenEnabled) {
                            onClick();
                        }
                        setEnablingStrategy.apply(this, arguments);
                    }
                });
            },
            buildPlainTextEditorButton: function ($textEditor) {
                var isActive = imcms.textEditorFullScreenEnabled;

                var onClick = function () {
                    var $toolbar = $textEditor.parent()
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
                )
            }
        };
    }
);
