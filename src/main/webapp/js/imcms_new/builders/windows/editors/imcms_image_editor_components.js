Imcms.define(
    "imcms-image-editor-components",

    ["imcms-image-editor-right-side-builder", "imcms-image-editor-left-side-builder"],

    function (rightSideBuilder, leftSideBuilder) {
        return {
            buildRightSide: function (opts) {
                return rightSideBuilder.build(opts);
            },
            buildLeftSide: function (opts) {
                return leftSideBuilder.build(opts);
            }
        }
    }
);
