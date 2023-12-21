/**
 * @author Serhii Maksymchuk from Ubrainians for imCode
 * 09.02.18
 */
const imcms = require("imcms");
define(
    "imcms-text-full-screen-plugin",
    ["jquery", "tinymce", "imcms-admin-panel-state", "imcms", 'imcms-text-editor-toolbar-button-builder',
        'imcms-i18n-texts', 'imcms-standalone-editor-toolbar-builder', 'imcms-events'],
    function ($, tinyMCE, adminPanelState, imcms, toolbarButtonBuilder, texts, imcmsStandaloneToolbarBuilder,
              events) {
        const title = texts.toolTipText.fullScreen;

        let viewUrl = $('<a>', {
            'class': 'title-url-text-editor'
        });

        function buildImcmsStandaloneToolbar(editorData) {
            const imcmsStandaloneToolbarContent = [
                {
                    type: 'logo',
                    link: ''
                },
                {
                    type: 'id',
                    text: texts.toolbar.documentId + editorData.docId,
                    title: texts.toolbar.documentIdTitle,
                },
                {
                    type: 'index',
                    text: texts.toolbar.elementIndex + editorData.index,
                    title: texts.toolbar.elementIndexTitle,
                },
                {
                    type: 'label',
                    text: editorData.label ? ('Label ' + editorData.label) : ''
                },
            ];

            return  imcmsStandaloneToolbarBuilder.buildPanel(imcmsStandaloneToolbarContent).addClass("imcms-editor-toolbar__fullscreen");
        }

        function removeImcmsStandaloneToolbar() {
            $('#imcms-editor-toolbar').remove();
        }

        return {
            pluginName: 'fullscreen',
            initFullScreen: function (editor) {
                editor.addCommand('mceFullscreen', () => {
                    const $editorBody = $(editor.getBody());
                    const editorData = $editorBody.data();
                    const $toolBar = $editorBody.parent().children(".imcms-editor-area__text-toolbar");
                    const $body = $("body");

                    const $imcmsStandaloneToolbarBuilder = editorData.standalone ? $("<div>") : buildImcmsStandaloneToolbar(editorData);
                    if ($editorBody.hasClass('imcms-mce-fullscreen-inline') && !imcms.textEditorFullScreenEnabled) {

                        if (adminPanelState.state === "auto") {
                            adminPanelState.enablePanelAppearance();
                        }

                        $editorBody.removeClass('imcms-mce-fullscreen-inline');
                        $toolBar.removeClass("mce-fullscreen-toolbar");
                        viewUrl.css('display', 'none');
                        $body.css('overflow', 'auto');
                        removeImcmsStandaloneToolbar();
                        $editorBody.off("blur");
                    } else {
                        if (adminPanelState.state === "auto") {
                            adminPanelState.disablePanelAppearance();
                        }

                        if ($editorBody.attr('data-loop-entry-ref.loop-index')) {
                            const linkData = imcms.contextPath + '/api/admin/text?meta-id=' + $editorBody.attr('data-doc-id')
                                + '&index=' + $editorBody.attr('data-index')
                                + '&loop-index=' + $editorBody.attr('data-loop-entry-ref.loop-index')
                                + '&loop-entry-index=' + $editorBody.attr('data-loop-entry-ref.loop-entry-index')
                                + '&label=' + $editorBody.attr("data-label");
                            viewUrl.text(texts.toolTipText.textEditor + ': ' + linkData);

                            viewUrl.attr('href', linkData)

                        } else {
                            const linkData = imcms.contextPath + '/api/admin/text?meta-id=' + $editorBody.attr('data-doc-id')
                                + '&index=' + $editorBody.attr('data-index')
                                + '&label=' + $editorBody.attr("data-label");
                            viewUrl.text(texts.toolTipText.textEditor + ': ' + linkData);

                            viewUrl.attr('href', linkData)
                        }

                        $editorBody.addClass('imcms-mce-fullscreen-inline');
                        $toolBar.addClass("mce-fullscreen-toolbar");
                        $editorBody.parent().find('.mce-stack-layout').append(viewUrl);
                        viewUrl.css('display', 'inline-block');
                        $body.css('overflow', 'hidden');
                        $imcmsStandaloneToolbarBuilder.appendTo($body).show();
                        $editorBody.blur(function (e) {
                            e.target.focus();
                        });
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
                const editorData = $textEditor.data();
                const $body = $("body");
                const $imcmsStandaloneToolbarBuilder = editorData.standalone ? $("<div>") : buildImcmsStandaloneToolbar(editorData);

                const onClick = () => {
                    const $toolbar = $textEditor.parent().find('.imcms-editor-area__text-toolbar');

                    if ($textEditor.attr('data-loop-entry-ref.loop-index')) {
                        const linkData = imcms.contextPath + '/api/admin/text?meta-id=' + $textEditor.attr('data-doc-id')
                            + '&index=' + $textEditor.attr('data-index')
                            + '&loop-index=' + $textEditor.attr('data-loop-entry-ref.loop-index')
                            + '&loop-entry-index=' + $textEditor.attr('data-loop-entry-ref.loop-entry-index')
                            + '&label=' + $textEditor.attr("data-label");
                        viewUrl.text(texts.toolTipText.textEditor + ': ' + linkData);

                        viewUrl.attr('href', linkData)
                    } else {
                        const linkData = imcms.contextPath + '/api/admin/text?meta-id=' + $textEditor.attr('data-doc-id')
                            + '&index=' + $textEditor.attr('data-index')
                            + '&label=' + $textEditor.attr("data-label");
                        viewUrl.text(texts.toolTipText.textEditor + ': ' + linkData);

                        viewUrl.attr('href', linkData)
                    }

                    if ($textEditor.hasClass('imcms-mce-fullscreen-inline') && !isActive) {
                        $toolbar.removeClass('mce-fullscreen-toolbar');
                        $textEditor.removeClass('imcms-mce-fullscreen-inline');
                        viewUrl.css('display', 'none');
                        removeImcmsStandaloneToolbar();
                        $textEditor.off("blur");
                    } else {
                        $toolbar.addClass('mce-fullscreen-toolbar')
                        $textEditor.addClass('imcms-mce-fullscreen-inline')
                        $toolbar.find('.text-toolbar-wrapper').append(viewUrl);
                        viewUrl.css('display', 'inline-block');
                        $imcmsStandaloneToolbarBuilder.appendTo($body).show();
                        $textEditor.blur(function (e) {
                            e.target.focus();
                        });
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
