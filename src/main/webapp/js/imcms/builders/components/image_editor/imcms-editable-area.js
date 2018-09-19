/**
 * @author Serhii Maksymchuk from Ubrainians for imCode
 * 11.09.18
 */
const BEM = require('imcms-bem-builder');
const editableImage = require('imcms-editable-image');
const $ = require('jquery');

let $editableImageArea;
let $editableImageWrapper;

module.exports = {
    getEditableImageWrapper() {
        return $editableImageWrapper || ($editableImageWrapper = $('<div>', {html: editableImage.getImage()}))
    },
    getEditableImageArea() {
        return $editableImageArea || ($editableImageArea = new BEM({
            block: "imcms-editable-img-area",
            elements: {
                "img": this.getEditableImageWrapper(),
            }
        }).buildBlockStructure('<div>'));
    },
};
