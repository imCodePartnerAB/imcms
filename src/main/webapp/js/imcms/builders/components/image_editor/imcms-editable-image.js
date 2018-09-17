/**
 * @author Serhii Maksymchuk from Ubrainians for imCode
 * 11.09.18
 */

const $ = require('jquery');
const imcms = require('imcms');

let $image;

function onImageLoad() {
    const $img = $(this);
    setTimeout(() => require('imcms-image-resize').setOriginal($img.width(), $img.height()));
}

function getImage() {
    return $image || ($image = $("<img>", {
        "class": "imcms-editable-img",
        load: onImageLoad
    }))
}

module.exports = {
    setImageSource: (path) => {
        $image.removeAttr('style');
        $image.attr("src", imcms.contextPath + "/" + imcms.imagesPath + path);
    },

    getImage: getImage,

    clearData: () => {
        $image.removeAttr('src');
    },
};
