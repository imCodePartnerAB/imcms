/**
 * @author Serhii Maksymchuk from Ubrainians for imCode
 * 08.02.18
 */
define(
    "imcms-image-in-text-plugin",
    ["tinymce", "jquery", "imcms-image-editor-builder", "imcms-i18n-texts"],
    function (tinyMCE, $, imageEditorBuilder, texts) {

        texts = texts.toolTipText;

        function onPluginButtonClicked() {
            const uniqueId = Date.now();
            const tagHTML = '<div id="' + uniqueId + '" class="imcms-image-in-text imcms-editor-area--image" contenteditable="false">\n'
                + '   <div class="imcms-editor-content">\n'
                + '       <a>\n'
                + '         <img>\n'
                + '       </a>\n'
                + '   </div>\n'
                + '</div>\n';

            const textDTO = $(this.$el).parents(".imcms-editor-area--text")
                .find(".imcms-editor-content--text")
                .data();

            const imageDTO = $.extend({inText: true}, textDTO);
            imageDTO.index = null;

            tinyMCE.activeEditor.execCommand('mceInsertContent', false, tagHTML);

            function openEditor() {
                const $this = $(this);
                const $tag = $this.parents(".imcms-image-in-text");
                const imageDTO = { // $.data() is not used because of strange behavior in this case
                    docId: $tag.attr("data-doc-id"),
                    langCode: $tag.attr("data-lang-code"),
                    inText: true,
                    index: $tag.attr("data-index")
                };

                imageEditorBuilder.setTag($tag).build(imageDTO);
            }

            var $tag = $(tinyMCE.activeEditor.getBody()).find("#" + uniqueId);
            const $editorControl = $("<div>", {
                "class": "imcms-editor-area__control-edit imcms-control imcms-control--edit"
                    + " imcms-control--image",
                html: $("<div>", {
                    "class": "imcms-editor-area__control-title",
                    text: texts.imageEditor
                }),
                click: openEditor
            });
            const $editorControlWrapper = $("<div>", {
                "class": "imcms-editor-area__control-wrap",
                html: $editorControl
            });

            $tag.append($editorControlWrapper);
            $tag.attr("data-doc-id", imageDTO.docId);
            $tag.attr("data-lang-code", imageDTO.langCode);
            $tag.attr("data-in-text", true);
            $tag.attr("data-index", imageDTO.index);

            openEditor.call($editorControl[0]);
        }

        return {
            pluginName: 'image_editor',
            initImageInText: function (editor) {
                editor.addButton(this.pluginName, {
                    icon: 'imcms-image--in-text-editor-icon',
                    tooltip: texts.addImageText,
                    onclick: onPluginButtonClicked
                });
            }
        };
    }
);
