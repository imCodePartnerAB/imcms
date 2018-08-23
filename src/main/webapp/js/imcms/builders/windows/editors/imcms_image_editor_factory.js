Imcms.define(
    "imcms-image-editor-factory",
    [
        "imcms-image-editor-right-side-builder", "imcms-image-editor-left-side-builder", 'jquery', "imcms-i18n-texts",
        "imcms-image-editor-body-head-builder", "imcms-bem-builder"
    ],
    function (rightSideBuilder, leftSideBuilder, $, texts, bodyHeadBuilder, BEM) {

        texts = texts.editors.image;

        var $imageInfo = $('<span>');

        return {
            buildEditor: function (opts) {
                var $rightSidePanel = rightSideBuilder.build(opts);
                var $leftSide = leftSideBuilder.build(opts);
                var $bodyHead = bodyHeadBuilder.build(opts, $rightSidePanel);
                var $head = opts.imageWindowBuilder.buildHead(texts.title);

                $head.find('.imcms-title').append($imageInfo);

                return new BEM({
                    block: "imcms-image_editor",
                    elements: {
                        "head": $head,
                        "image-characteristics": $bodyHead,
                        "left-side": $leftSide,
                        "right-side": $rightSidePanel
                    }
                }).buildBlockStructure("<div>", {"class": "imcms-editor-window"});
            },
            updateImageData: function ($tag, imageData) {
                $imageInfo.text(': ' + $tag.attr('data-doc-id') + '-' + $tag.attr('data-index'));
                rightSideBuilder.updateImageData($tag, imageData);
            }
        }
    }
);
