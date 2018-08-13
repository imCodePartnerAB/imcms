/**
 * @author Serhii Maksymchuk from Ubrainians for imCode
 * 09.02.18
 */
Imcms.define(
    "imcms-text-full-screen-plugin",
    ["jquery", "tinyMCE", "imcms-admin-panel-state", "imcms", 'imcms-text-editor-toolbar-button-builder'],
    function ($, tinyMCE, adminPanelState, imcms, toolbarButtonBuilder) {

        var title = 'Fullscreen'; // todo: localize!

        function setEnablingStrategy() {
            if (imcms.textEditorFullScreenEnabled) {
                this.disabled(true);
            }
        }

        return {
            pluginName: 'fullscreen',
            initFullScreen: function () {
                var name = this.pluginName;
                /** @namespace tinyMCE.PluginManager */
                tinyMCE.PluginManager.add(name, function (editor) {
                    editor.addCommand('mceFullscreen', function () {
                        var $editorBody = $(editor.getBody());
                        var $toolBar = $editorBody.parent().children(".imcms-editor-area__text-toolbar");
                        var $body = $("body");

                        if ($editorBody.hasClass('imcms-mce-fullscreen-inline')) {

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
                    editor.addButton(name, {
                        icon: 'fullscreen',
                        cmd: 'mceFullscreen',
                        title: title,
                        onPostRender: setEnablingStrategy
                    });
                });
            },
            buildPlainTextEditorButton: function ($textEditor) {
                return toolbarButtonBuilder.buildButton('text-editor-fullscreen-button', title, function () {
                    $textEditor.parent()
                        .find('.imcms-editor-area__text-toolbar')
                        .toggleClass('mce-fullscreen-toolbar');

                    $textEditor.toggleClass('imcms-mce-fullscreen-inline');
                })
            }
        };
    }
);
