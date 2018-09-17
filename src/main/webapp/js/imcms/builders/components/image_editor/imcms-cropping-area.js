/**
 * @author Serhii Maksymchuk from Ubrainians for imCode
 * 17.09.18
 */
const cropAreaClass = 'imcms-crop-area';

const BEM = require('imcms-bem-builder');
const $ = require('jquery');
const events = require('imcms-events');

let isImageProportionsInverted = false;

function getCurrentWidth($element) {
    return (isImageProportionsInverted) ? $element.height() : $element.width();
}

function getCurrentHeight($element) {
    return (isImageProportionsInverted) ? $element.width() : $element.height();
}

events.on("image proportions inverted", function () {
    isImageProportionsInverted = true;
});

events.on("regular image proportions", function () {
    isImageProportionsInverted = false;
});

function setFunctionality($element) {
    $element.getCurrentWidth = function () {
        return getCurrentWidth($element);
    };

    $element.getCurrentHeight = function () {
        return getCurrentHeight($element);
    };

    $element.getTop = function () {
        return parseInt($element.css("top"));
    };

    $element.getLeft = function () {
        return parseInt($element.css("left"));
    };

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
    $element.animate = function (params, duration, callback) {
        return oldAnimate.call($element, params, duration, function () {
            callback && callback.call();
            events.trigger(eventName);
        });
    };

    return $element;
}

let $croppingArea;
let $cropImage;
let $image;

function buildCroppingArea() {
    return new BEM({
        block: cropAreaClass,
        elements: {
            'crop-img': getCroppingImage(),
        }
    }).buildBlockStructure('<div>')
}

function getCroppingImage() {
    return $cropImage || ($cropImage = setFunctionality($("<img>")))
}

function buildImage() {
    return setFunctionality($("<img>", {"class": "imcms-editable-img"}));
}

module.exports = {
    getImage: () => $image || ($image = buildImage()),

    getCroppingImage: getCroppingImage,

    getCroppingArea: () => $croppingArea || ($croppingArea = setPositionListeners(setFunctionality(buildCroppingArea()))),
};
