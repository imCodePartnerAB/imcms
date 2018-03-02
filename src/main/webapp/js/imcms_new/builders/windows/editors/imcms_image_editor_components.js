Imcms.define(
    "imcms-image-editor-components",
    ["imcms-image-editor-right-side-builder"],
    function (rightSideBuilder) {
        return {
            buildRightSide: function (opts) {
                return rightSideBuilder.build(opts);
            }
        }
    }
);
