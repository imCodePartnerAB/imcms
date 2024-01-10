/**
 * @author Serhii Maksymchuk from Ubrainians for imCode
 * 17.09.18
 */
const BEM = require('imcms-bem-builder');
const $ = require('jquery');
const events = require('imcms-events');
const imageZoom = require('imcms-image-zoom');

let isImageProportionsInverted = false;

function getCurrentWidth($element) {
    return (isImageProportionsInverted) ? $element.height() : $element.width();
}

function getCurrentHeight($element) {
    return (isImageProportionsInverted) ? $element.width() : $element.height();
}

events.on("image proportions inverted", () => {
    isImageProportionsInverted = true;
});

events.on("regular image proportions", () => {
    isImageProportionsInverted = false;
});

function setFunctionality($element) {
    $element.getCurrentWidth = () => getCurrentWidth($element);

    $element.getCurrentHeight = () => getCurrentHeight($element);

    $element.getTop = () => parseInt($element.css("top"));

    $element.getLeft = () => parseInt($element.css("left"));

    $element.setLeft = (left) => $element.css('left', left);

    $element.setTop = (top) => $element.css('top', top);

    return $element;
}

function setPositionListeners($element, eventName) {
    const oldCss = $element.css;
    $element.css = function () {
        const retVal = oldCss.apply($element, arguments);

        if (!((arguments.length === 1) && (arguments[0].constructor === String))) {
            events.trigger(eventName);
        }

        return retVal;
    };

    const oldHeight = $element.height;
    $element.height = function () {
        const retVal = oldHeight.apply($element, arguments);

        if (arguments.length >= 1) {
            events.trigger(eventName);
        }

        return retVal;
    };

    const oldWidth = $element.width;
    $element.width = function () {
        const retVal = oldWidth.apply($element, arguments);

        if (arguments.length >= 1) {
            events.trigger(eventName);
        }

        return retVal;
    };

    const oldAnimate = $element.animate;
    $element.animate = (params, duration, callback) => oldAnimate.call($element, params, duration, () => {
        callback && callback.call();
        events.trigger(eventName);
    });

    return $element;
}

let $croppingBlock;
let $croppingArea;
let $cropImage;
let $image;

function buildCroppingArea() {
    return new BEM({
        block: 'imcms-crop-area',
        elements: {
            'crop-img': getCroppingImage(),
        }
    }).buildBlockStructure('<div>')
}

function getCroppingArea() {
    return $croppingArea || ($croppingArea = setPositionListeners(setFunctionality(buildCroppingArea()), 'crop area position changed'))
}

function getCroppingImage() {
    return $cropImage || ($cropImage = setFunctionality($("<img>")))
}

function onImageLoad() {
    const $img = $(this);
    const shadowLayout = getShadowLayout();
    const zoomValue = parseFloat(imageZoom.getRelativeZoomValueByOriginalImg());

    const width = $img.width();
    const height = $img.height();

    const transformStyle = imageZoom.getUpdatedTransformString(zoomValue, $img);

    shadowLayout.css({
        width,
        height,
    });

    $croppingWrap.css({
        transform: transformStyle,
        width,
        height,
    });
}

function getImage() {
    return $image || ($image = setFunctionality(
        $("<img>", {
            "class": "imcms-preview-img",
            on: {load: onImageLoad}
        })))
}

let $shadowLayout;

function getShadowLayout() {
    return $shadowLayout || ($shadowLayout = $('<div>'))
}

let $croppingWrap;

function buildCroppingBlock() {
    const angles = require('imcms-image-crop-angles');

    $croppingWrap = new BEM({
        block: 'image-cropping-wrap',
        elements: [
            {img: getImage()},
            {layout: getShadowLayout()},
            {'crop-area': getCroppingArea()},
            {angle: angles.topLeft.buildAngle()},
            {angle: angles.topRight.buildAngle()},
            {angle: angles.bottomRight.buildAngle()},
            {angle: angles.bottomLeft.buildAngle()},
        ]
    }).buildBlockStructure('<div>');

    return $('<div>', {
        'class': 'image-cropping-block',
        html: $croppingWrap,
    })
}

module.exports = {
    getImage: getImage,
    getCroppingImage: getCroppingImage,
    getCroppingArea: getCroppingArea,
    getCroppingBlock: () => $croppingBlock || ($croppingBlock = buildCroppingBlock())
};
