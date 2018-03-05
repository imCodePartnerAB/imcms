Imcms.define(
    "imcms-image-editor-factory",
    [
        "imcms-image-editor-right-side-builder", "imcms-image-editor-left-side-builder",
        "imcms-image-editor-body-head-builder", "imcms-i18n-texts", "imcms-bem-builder"
    ],
    function (rightSideBuilder, leftSideBuilder, bodyHeadBuilder, texts, BEM) {

        texts = texts.editors.image;

        return {
            buildEditor: function (opts) {
                var $rightSidePanel = rightSideBuilder.build(opts);
                var $leftSide = leftSideBuilder.build(opts);
                var $bodyHead = bodyHeadBuilder.build(opts, $rightSidePanel);

                return new BEM({
                    block: "imcms-image_editor",
                    elements: {
                        "head": opts.imageWindowBuilder.buildHead(texts.title),
                        "image-characteristics": $bodyHead,
                        "left-side": $leftSide,
                        "right-side": $rightSidePanel
                    }
                }).buildBlockStructure("<div>", {"class": "imcms-editor-window"});
            },
            updateImageData: function ($tag, imageData) {
                rightSideBuilder.updateImageData($tag, imageData);
            }
        }
    }
);
