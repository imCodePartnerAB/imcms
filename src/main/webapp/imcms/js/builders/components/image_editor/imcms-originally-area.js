/**
 * @author Serhii Maksymchuk from Ubrainians for imCode
 * 11.09.18
 */
const BEM = require('imcms-bem-builder');
const originallyImage = require('imcms-originally-image');
const $ = require("jquery");

let $originalImageArea;
let $originalImgContainer;

function getOriginalImageContainer(){
		return $originalImgContainer || ($originalImgContainer = $('<div>', {
			'class': 'imcms-original-img-container',
			html: originallyImage.getImage()
		}));
}

module.exports = {
    getOriginalImageArea() {
        return $originalImageArea || ($originalImageArea = new BEM({
            block: "imcms-originally-img-area",
            elements: {
                "container": getOriginalImageContainer(),
            }
        }).buildBlockStructure('<div>'));
    },
};
