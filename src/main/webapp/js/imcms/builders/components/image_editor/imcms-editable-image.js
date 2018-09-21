/**
 * @author Serhii Maksymchuk from Ubrainians for imCode
 * 11.09.18
 */

const $ = require('jquery');
const imcms = require('imcms');

let $image;

function getImage() {
    return $image || ($image = $('<div>', {'class': 'imcms-editable-img'}))
}

module.exports = {
    setImageSource: (path, onLoad) => {
        const src = imcms.contextPath + '/' + imcms.imagesPath + path;

        $image.attr('data-src', src);
        $image.removeAttr('style'); // not sure
        $image.css('background-image', `url('${src}')`);

        const actualImage = new Image();
        actualImage.src = src;

        actualImage.onload = function () {
            $image.css('background-size', `${this.width}px ${this.height}px`);
            require('imcms-image-resize').setOriginal(this.width, this.height);

            onLoad && onLoad.call();
        };
    },

    setBackgroundPositionX(newPositionX) {
        $image[0].style.backgroundPositionX = `${newPositionX}px`
    },

    setBackgroundPositionY(newPositionY) {
        $image[0].style.backgroundPositionY = `${newPositionY}px`
    },

    setBacBackgroundSize(width, height) {
        $image.css('background-size', `${width}px ${height}px`);
    },

    getBackgroundPositionX() {
        return parseInt($image[0].style.backgroundPositionX, 10)
    },

    getBackgroundPositionY() {
        return parseInt($image[0].style.backgroundPositionY, 10)
    },

    setBackgroundWidth(newWidth) {
        const backgroundSize = $image[0].style.backgroundSize;
        this.setBacBackgroundSize(newWidth, backgroundSize ? this.getBackgroundHeight() : 0);
    },

    setBackgroundHeight(newHeight) {
        const backgroundSize = $image[0].style.backgroundSize;
        this.setBacBackgroundSize(backgroundSize ? this.getBackgroundWidth() : 0, newHeight);
    },

    getBackgroundWidth() {
        const backgroundSize = $image[0].style.backgroundSize;
        return backgroundSize ? parseInt(backgroundSize.split(' ')[0], 10) : 0
    },

    getBackgroundHeight() {
        const backgroundSize = $image[0].style.backgroundSize;
        return (backgroundSize && backgroundSize.includes(' ')) ? parseInt(backgroundSize.split(' ')[1], 10) : 0
    },

    getImage: getImage,

    clearData: () => {
        $image.removeAttr('src').removeAttr('style');
    },
};
