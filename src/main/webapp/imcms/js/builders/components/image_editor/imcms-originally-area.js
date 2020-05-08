/**
 * @author Serhii Maksymchuk from Ubrainians for imCode
 * 11.09.18
 */
const BEM = require('imcms-bem-builder');
const originallyImage = require('imcms-originally-image');

let $originalImageArea;

module.exports = {
    getOriginalImageArea() {
        return $originalImageArea || ($originalImageArea = new BEM({
            block: "imcms-editable-img-area",
            elements: {
                "img": originallyImage.getImage(),
            }
        }).buildBlockStructure('<div>'));
    },
};
