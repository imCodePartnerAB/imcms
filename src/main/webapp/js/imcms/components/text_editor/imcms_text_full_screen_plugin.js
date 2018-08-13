/**
 * @author Serhii Maksymchuk from Ubrainians for imCode
 * 09.02.18
 */
Imcms.define(
    "imcms-text-full-screen-plugin",
    ["jquery", "tinyMCE", "imcms-admin-panel-state", "imcms", 'imcms-bem-builder'],
    function ($, tinyMCE, adminPanelState, imcms, BEM) {

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
                        title: 'Fullscreen',
                        onPostRender: setEnablingStrategy
                    });
                });
            },
            buildPlainTextEditorButton: function ($textEditor) {
                return new BEM({
                    block: 'text-editor-fullscreen-button',
                    elements: {
                        'icon': $('<div>', {
                            'class': 'text-toolbar__icon'
                        })
                    }
                }).buildBlockStructure('<div>', {
                    class: 'text-toolbar__button',
                    title: 'Fullscreen',
                    click: function () {
                        $textEditor.parent()
                            .find('.imcms-editor-area__text-toolbar')
                            .toggleClass('mce-fullscreen-toolbar');

                        $textEditor.toggleClass('imcms-mce-fullscreen-inline');
                    }
                })
            }
        };
    }
);
