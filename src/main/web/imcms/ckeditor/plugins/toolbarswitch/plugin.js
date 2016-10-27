/**
 * @license Copyright (c) 2003-2013, CKSource - Frederico Knabben. All rights reserved.
 * For licensing, see LICENSE.html or http://ckeditor.com/license
 */

/**
 * @fileOverview  Plugin that changes the toolbar and maximizes the editor
 *                for the big toolbar.
 *
 *                You need a custom config to define the small and big toolbars.
 *                Also the maximize plug-in is needed but not the maximize button.
 *                For this plugin you should use the 'Toolbarswitch' button instead.
 *
 *                CKEDITOR.replace('sometextcomponentname', {
 *               		customConfig: '/...custom_ckeditor_config.js'
 *               		toolbar: 'yoursmalltoolbarname', 
 *               		smallToolbar: 'yoursmalltoolbarname',
 *               		maximizedToolbar: 'yourbigtoolbarname' });
 *
 *                Requires:
 *                - Maximize plugin. But not the button that goes with it.
 *                - All toolbars used in the ckeditor instance have to use the 'Toolbarswitch' button instead.
 *                - A custom config to define the small and big toolbars.
 *                - function CKeditor_OnComplete(ckEditorInstance){ ... your own custom code or leave empty... }
 *                  This was added to the plugin for those that wrap the ckeditor in other java script to shield
 *                  the rest of their code from ckeditor version particularities.
 *                - jQuery
 */


function switchMe(editor, callback) {
    var editorConfig = {
        customConfig: editor.config.customConfig,
        contentsCss: editor.config.contentsCss,
        toolbar: CKEDITOR.defineToolbar(editor),
        extraPlugins: editor.config.extraPlugins
    };

    // Copy data to original text element before getting rid of the old editor
    var domTextElement = editor.element;

    // Remove old editor and the DOM elements, else you get two editors
    editor.destroy();
    switch (editor.elementMode) {
        case 3: {
            editorConfig.on = {
                instanceReady: function (e) {
                    if (callback) {
                        callback.call(null, e);
                    }
                }
            };

            CKEDITOR.replace(domTextElement, editorConfig);
            break;
        }
        case 1: {
            editorConfig.on = {
                instanceReady: function (e) {
                    try {
                        e.editor.focusManager.focus();
                    } catch (ignore) {
                    }
                    e.editor.element.$.focus();
                    if (callback) {
                        callback.call(null, e);
                    }
                }
            };

            CKEDITOR.inline(domTextElement, editorConfig);
            break;
        }
    }
}

CKEDITOR.plugins.add('toolbarswitch', {
    requires: ['button', 'toolbar', 'maximize'],
    lang: 'af,ar,bg,bn,bs,ca,cs,cy,da,de,el,en,en-au,en-ca,en-gb,eo,es,et,eu,fa,fi,fo,fr,fr-ca,gl,gu,he,hi,hr,hu,id,is,it,ja,ka,km,ko,ku,lt,lv,mk,mn,ms,nb,nl,no,pl,pt,pt-br,ro,ru,si,sk,sl,sq,sr,sr-latn,sv,th,tr,ug,uk,vi,zh,zh-cn', // %REMOVE_LINE_CORE%
    icons: 'toolbarswitch',
    hidpi: true,
    init: function (editor) {
        var lang = editor.lang;
        var commandFunction = {
            exec: function (editor) {
                var previousValue;
                if (editor.checkDirty()) {
                    previousValue = editor._.previousValue;
                }
                var switchToSmall = editor.config.maxToolbar;
                if (switchToSmall) {
                    // For switching to the small toolbar first minimize
                    editor.commands.maximize.exec();
                }
                switchMe(editor, function (event) {
                    var newEditor = event.editor;
                    if (!switchToSmall) {
                        newEditor.commands.maximize.exec();
                    }
                    newEditor.fire('triggerResize');

                    if (previousValue) {
                        newEditor._.previousValue = previousValue;
                    }
                });
            }
        };

        var command = editor.addCommand('toolbarswitch', commandFunction);
        command.modes = {wysiwyg: 1, source: 1};
        command.canUndo = false;
        command.readOnly = 1;

        editor.ui.addButton && editor.ui.addButton('Toolbarswitch', {
            label: lang.toolbarswitch.toolbarswitch,
            command: 'toolbarswitch',
            toolbar: 'tools,10'
        });
    }
});

