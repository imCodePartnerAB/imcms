const rightSideBuilder = require('imcms-image-editor-right-side-builder');
const leftSideBuilder = require('imcms-image-editor-left-side-builder');
const $ = require('jquery');
const texts = require('imcms-i18n-texts').editors.image;
const bodyHeadBuilder = require('imcms-image-editor-body-head-builder');
const BEM = require('imcms-bem-builder');

const $imageInfo = $('<a>');

module.exports = {
    buildEditor: opts => {
        const $rightSidePanel = rightSideBuilder.build(opts);
        const $leftSide = leftSideBuilder.build();
        const $bodyHead = bodyHeadBuilder.build($rightSidePanel, opts.imageData);
        const $head = opts.imageWindowBuilder.buildHead(texts.title + ": " + opts.$tag.attr('data-doc-id')
            + "-" + opts.$tag.attr('data-index') + " ");

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
    updateImageData: ($tag, imageData) => {
        if ($tag.attr('data-loop-index')) {
            const linkData = '/api/admin/image?meta-id=' + $tag.attr('data-doc-id')
                + '&index=' + $tag.attr('data-index')
                + '&loop-index=' + $tag.attr('data-loop-index')
                + '&loop-entry-index=' + $tag.attr('data-loop-entry-index');

            $imageInfo.text(linkData).css({
                'text-transform': 'lowercase',
                'color': '#0b94d8'
            });

            $imageInfo.attr('href', linkData)

        } else {
            const linkData = '/api/admin/image?meta-id='
                + $tag.attr('data-doc-id')
                + '&index=' + $tag.attr('data-index');

            $imageInfo.text(linkData).css({
                'text-transform': 'lowercase',
                'color': '#0b94d8'
            });

            $imageInfo.attr('href', linkData)
        }
        rightSideBuilder.updateImageData($tag, imageData);
    }
};
