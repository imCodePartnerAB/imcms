/**
 * Created by Serhii Maksymchuk from Ubrainians for imCode
 * 01.09.17
 */
Imcms.define("imcms-text-editor-initializer",
    ["tinyMCE", "imcms-uuid-generator", "jquery"],
    function (tinyMCE, uuidGenerator, $) {
        // stupid way to get contextPath! todo: receive from server
        var relativePath = window.location.pathname;
        var contextPath = ((relativePath.lastIndexOf("/") === 0) ? "" : "/" + relativePath.split("/")[1]);

        var ACTIVE_EDIT_AREA_CLASS = "imcms-editor-area--active";

        var inlineEditorConfig = {
            skin_url: contextPath + '/libs/tinymce/skins/white',
            cache_suffix: '?v=0.0.1',
            branding: false,
            skin: 'white',
            inline: true,
            toolbar_items_size: 'small',
            content_css: contextPath + '/stylesheets/imcms-text_editor.css',
            plugins: ['autolink link image lists hr code fullscreen save table contextmenu'],
            toolbar: 'code | bold italic underline | bullist numlist | hr |' +
            ' alignleft aligncenter alignright alignjustify | link image | fullscreen | save',
            menubar: false,
            statusbar: false,
            init_instance_callback: prepareEditor
        };

        function clearSaveBtnText(editor) {
            delete editor.buttons.save.text;
        }

        function setEditorFocusOnEditControlClick(editor) {
            editor.$()
                .parents('.imcms-editor-area--text')
                .find('.imcms-control--text')
                .on('click', function () {
                    editor.focus();
                });
        }

        function resizeEditorToolbar(e) {
            var $editorArea = e.target.$().closest(".imcms-editor-area--text");

            var heightPx = $editorArea.find(".mce-tinymce.mce-tinymce-inline.mce-container.mce-panel")
                .css("height");

            var height = parseInt(heightPx) || 0;

            $editorArea.find(".imcms-editor-area__text-toolbar")
                .css("top", -height + 1);
        }

        function prepareEditor(editor) {
            clearSaveBtnText(editor);
            setEditorFocusOnEditControlClick(editor);
            editor.on('focus', resizeEditorToolbar);
        }

        function toggleFocusEditArea(e) {
            var $activeTextArea = $("." + ACTIVE_EDIT_AREA_CLASS);

            if ($activeTextArea.find(".mce-edit-focus").length) {
                return;
            }

            var $closestTextArea = $(e.target).closest(".imcms-editor-area--text");
            $activeTextArea.removeClass(ACTIVE_EDIT_AREA_CLASS);

            if ($closestTextArea.length && $closestTextArea.find(".mce-edit-focus").length) {
                $closestTextArea.addClass(ACTIVE_EDIT_AREA_CLASS);
            }
        }

        function setFocusTextEditAreaToggle() {
            $(document).click(toggleFocusEditArea);
        }

        function initTextEditor() {
            var toolbarId = uuidGenerator.generateUUID();
            var textAreaId = uuidGenerator.generateUUID();

            $(this).attr("id", textAreaId)
                .closest(".imcms-editor-area--text")
                .find(".imcms-editor-area__text-toolbar")
                .attr("id", toolbarId);

            var config = $.extend({
                selector: "#" + textAreaId,
                fixed_toolbar_container: "#" + toolbarId
            }, inlineEditorConfig);

            tinyMCE.init(config);
        }

        return {
            initEditor: function () {
                $(setFocusTextEditAreaToggle);
                $(".imcms-editor-content--text").each(initTextEditor);
            }
        };
    }
);
