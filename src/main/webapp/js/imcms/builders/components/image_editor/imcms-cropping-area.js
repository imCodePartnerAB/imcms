/**
 * @author Serhii Maksymchuk from Ubrainians for imCode
 * 17.09.18
 */
const cropAreaClass = "imcms-crop-area";

const BEM = require('imcms-bem-builder');
const $ = require('jquery');

let $croppingArea;
let $cropImage;

function buildCroppingArea() {
    return new BEM({
        block: cropAreaClass,
        elements: {
            'crop-img': getCroppingImage(),
        }
    }).buildBlockStructure('<div>')
}

function getCroppingImage() {
    return $cropImage || ($cropImage = $("<img>"))
}

module.exports = {
    getCroppingImage: getCroppingImage,
    getCroppingArea: () => $croppingArea || ($croppingArea = buildCroppingArea()),
};
