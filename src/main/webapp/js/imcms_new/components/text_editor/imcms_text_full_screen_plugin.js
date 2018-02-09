/**
 * @author Serhii Maksymchuk from Ubrainians for imCode
 * 09.02.18
 */
Imcms.define(
    "imcms-text-full-screen-plugin",
    ["jquery", "tinyMCE", "imcms-events"],
    function ($, tinyMCE, events) {
        return {
            pluginName: 'fullscreen',
            initFullScreen: function () {
                /** @namespace tinyMCE.PluginManager */
                tinyMCE.PluginManager.add('fullscreen', function (editor) {
                    editor.addCommand('mceFullscreen', function () {
                        var $editorBody = $(editor.getBody());
                        var $toolBar = $editorBody.parent().children(".imcms-editor-area__text-toolbar");
                        var $body = $("body");

                        if ($editorBody.hasClass('imcms-mce-fullscreen-inline')) {
                            events.trigger("enable admin panel");
                            $editorBody.removeClass('imcms-mce-fullscreen-inline');
                            $toolBar.removeClass("mce-fullscreen-toolbar");
                            $body.css('overflow', 'auto');

                        } else {
                            events.trigger("disable admin panel");
                            $editorBody.addClass('imcms-mce-fullscreen-inline');
                            $toolBar.addClass("mce-fullscreen-toolbar");
                            $body.css('overflow', 'hidden');
                        }
                        editor.focus();
                    });
                    editor.addButton('fullscreen', {
                        icon: 'fullscreen',
                        cmd: 'mceFullscreen',
                        title: 'Fullscreen'
                    });
                });
            }
        };
    }
);
