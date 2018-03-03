Imcms.define(
    "imcms-image-editor-factory",
    [
        "imcms-image-editor-right-side-builder", "imcms-image-editor-left-side-builder",
        "imcms-image-editor-body-head-builder", "imcms-i18n-texts", "imcms-bem-builder"
    ],
    function (rightSideBuilder, leftSideBuilder, bodyHeadBuilder, texts, BEM) {

        texts = texts.editors.image;

        function buildRightSide(opts) {
            return rightSideBuilder.build(opts);
        }

        function buildLeftSide(opts) {
            return leftSideBuilder.build(opts);
        }

        function buildBodyHead(opts) {
            return bodyHeadBuilder.build(opts);
        }

        return {
            buildEditor: function (opts) {
                var $rightSidePanel = buildRightSide(opts);
                var $bodyHead = buildBodyHead(opts);
                var $leftSide = buildLeftSide(opts);

                return new BEM({
                    block: "imcms-image_editor",
                    elements: {
                        "head": opts.imageWindowBuilder.buildHead(texts.title),
                        "image-characteristics": $bodyHead,
                        "left-side": $leftSide,
                        "right-side": $rightSidePanel
                    }
                }).buildBlockStructure("<div>", {"class": "imcms-editor-window"});
            }
        }
    }
);
