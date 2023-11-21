/**
 * @author Serhii Maksymchuk from Ubrainians for imCode
 * 09.02.18
 */
const imcms = require("imcms");
define(
    "imcms-text-full-screen-plugin",
    ["jquery", "tinymce", "imcms-admin-panel-state", "imcms", 'imcms-text-editor-toolbar-button-builder',
        'imcms-i18n-texts'],
    function ($, tinyMCE, adminPanelState, imcms, toolbarButtonBuilder, texts) {

        texts = texts.toolTipText;

        const title = texts.fullScreen;

        let viewUrl = $('<a>', {
            'class': 'title-url-text-editor'
        });

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
                        viewUrl.css('display', 'none');
                        $body.css('overflow', 'auto');

                    } else {
                        if (adminPanelState.state === "auto") {
                            adminPanelState.disablePanelAppearance();
                        }

                        if ($editorBody.attr('data-loop-entry-ref.loop-index')) {
                            const linkData = imcms.contextPath + '/api/admin/text?meta-id=' + $editorBody.attr('data-doc-id')
                                + '&index=' + $editorBody.attr('data-index')
                                + '&loop-index=' + $editorBody.attr('data-loop-entry-ref.loop-index')
                                + '&loop-entry-index=' + $editorBody.attr('data-loop-entry-ref.loop-entry-index');
                            viewUrl.text(texts.textEditor + ': ' + linkData);

                            viewUrl.attr('href', linkData)

                        } else {
                            const linkData = imcms.contextPath + '/api/admin/text?meta-id=' + $editorBody.attr('data-doc-id')
                                + '&index=' + $editorBody.attr('data-index');
                            viewUrl.text(texts.textEditor + ': ' + linkData);

                            viewUrl.attr('href', linkData)
                        }

                        $editorBody.addClass('imcms-mce-fullscreen-inline');
                        $toolBar.addClass("mce-fullscreen-toolbar");
                        $('.mce-stack-layout').append(viewUrl);
                        viewUrl.css('display', 'inline-block');
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

                        }
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

                    if ($textEditor.attr('data-loop-entry-ref.loop-index')) {
                        const linkData = imcms.contextPath + '/api/admin/text?meta-id=' + $textEditor.attr('data-doc-id')
                            + '&index=' + $textEditor.attr('data-index')
                            + '&loop-index=' + $textEditor.attr('data-loop-entry-ref.loop-index')
                            + '&loop-entry-index=' + $textEditor.attr('data-loop-entry-ref.loop-entry-index');
                        viewUrl.text(texts.textEditor + ': ' + linkData);

                        viewUrl.attr('href', linkData)
                    } else {
                        const linkData = imcms.contextPath + '/api/admin/text?meta-id=' + $textEditor.attr('data-doc-id')
                            + '&index=' + $textEditor.attr('data-index');
                        viewUrl.text(texts.textEditor + ': ' + linkData);

                        viewUrl.attr('href', linkData)
                    }

                    if ($toolbar.hasClass('mce-fullscreen-toolbar')) {
                        $toolbar.find('.text-toolbar-wrapper').append(viewUrl);
                        viewUrl.css('display', 'inline-block');
                    } else {
                        viewUrl.css('display', 'none');
                    }

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
