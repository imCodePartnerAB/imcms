const rightSideBuilder = require('imcms-image-editor-right-side-builder');
const leftSideBuilder = require('imcms-image-editor-left-side-builder');
const $ = require('jquery');
const texts = require('imcms-i18n-texts').editors.image;
const bodyHeadBuilder = require('imcms-image-editor-body-head-builder');
const BEM = require('imcms-bem-builder');
const components = require('imcms-components-builder');
const imcms = require("imcms");


const $imageLinkInfo = $('<a>', {
    id: 'data-link-image'
});

const $infoData = $('<div>', {
    'class': 'common-info-image'
});

const $imageLinkContainerInfo = new BEM({
	block: 'image-editor-info',
	elements: {
		'icon-link': $('<a>', {
			html: components.controls.permalink(),
			target: '_blank'
		}),
		'data-link': $('<div>', {
			html: $imageLinkInfo
		})
	}
}).buildBlockStructure('<div>');

module.exports = {
    buildEditor: opts => {
        const $rightSidePanel = rightSideBuilder.build(opts);
        const $leftSide = leftSideBuilder.build();
        const $bodyHead = bodyHeadBuilder.build($rightSidePanel, opts.imageData, opts.fillData, rightSideBuilder.getCurrentImageData);
        const $head = opts.imageWindowBuilder.buildHead();
        //need for get data after build and data in the $infoData, in another way fix build this..
        const $title = $head.find('.imcms-title');
        $title.append($infoData);
        if (!opts.$tag.data('standalone')) $title.append($imageLinkContainerInfo);
        $title.css("display", "flex");

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
        const labelText = $tag.find('.imcms-editor-area__text-label').text();
        if(labelText){
            $infoData.text(labelText);
        }else{
            $infoData.text(`${texts.title} - ${texts.page} ${$tag.attr('data-doc-id')}, 
            ${texts.imageName}${$tag.attr('data-index')} - ${texts.teaser}`);
        }

        let linkData = imcms.contextPath + '/api/admin/image?meta-id=' + $tag.attr('data-doc-id')
            + '&index=' + $tag.attr('data-index');

        if ($tag.attr('data-loop-index')) {
            linkData += '&loop-index=' + $tag.attr('data-loop-index')
                + '&loop-entry-index=' + $tag.attr('data-loop-entry-index');
        }
        if ($tag.data('style')) {
            const style = $tag.data('style');
            style.split(';')
                .map(x => x.trim())
                .filter(x => !!x)
                .forEach(x => {
                    const styleKeyAndValue = x.split(':').map(x => x.trim());
                    const value = /(\d)px/.test(styleKeyAndValue[1]) ?
                        styleKeyAndValue[1].replace("px", "") : styleKeyAndValue[1]; //100px -> 100
                    linkData += '&'+styleKeyAndValue[0]+'='+value;
                });
        }
        if(labelText){
            linkData += '&label=' + labelText;
        }

        $imageLinkInfo.text(texts.editInNewWindow);
        $imageLinkInfo.attr('href', linkData);
        $('.imcms-image_editor').find('.image-editor-info__icon-link').attr('href', linkData);
        rightSideBuilder.updateImageData($tag, imageData);
    }
};
