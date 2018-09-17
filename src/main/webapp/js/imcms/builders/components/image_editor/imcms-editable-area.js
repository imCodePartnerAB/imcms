/**
 * @author Serhii Maksymchuk from Ubrainians for imCode
 * 11.09.18
 */
const BEM = require('imcms-bem-builder');
const editableImage = require('imcms-editable-image');

let $editableImageArea;

module.exports = {
    getEditableImageArea: () => {
        return $editableImageArea || ($editableImageArea = new BEM({
            block: "imcms-editable-img-area",
            elements: {
                "img": editableImage.getImage(),
            }
        }).buildBlockStructure('<div>'));
    },
};
