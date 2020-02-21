/**
 * @author Serhii Maksymchuk from Ubrainians for imCode
 * 11.09.18
 */
const $ = require('jquery');
const BEM = require('imcms-bem-builder');
const imcms = require('imcms');

let savePrePreviewPosition = false;
let $previewImageArea;
let $previewImgContainer;
let $previewImg;

function getPreviewImage() {
    return $previewImg || ($previewImg = $('<div>', {'class': 'imcms-preview-img'}))
}

function getPreviewImageContainer() {
    return $previewImgContainer || ($previewImgContainer = $('<div>', {
        'class': 'imcms-preview-img-container',
        html: getPreviewImage()
    }))
}

function buildPreviewImageArea() {
    return new BEM({
        block: 'imcms-preview-img-area',
        elements: {
            'container': getPreviewImageContainer(),
        }
    }).buildBlockStructure('<div>');
}

function getPreviewImageArea() {
    return $previewImageArea || ($previewImageArea = buildPreviewImageArea())
}

function clear() {
    getPreviewImage().removeAttr('style');
}

module.exports = {
    setPreviewImageSource: (imageData, onLoad) => {
        const src = `${imcms.contextPath}/${imcms.imagesPath}/${imageData.path}`;

        $previewImg.attr('data-src', src);
        $previewImg.css('background-image', `url('${src}')`);

        const actualImage = new Image();
        actualImage.src = src;

        actualImage.onload = function () {
            $previewImg.css('background-size', `${this.width}px ${this.height}px`);

            require('imcms-image-resize').setPreview(imageData.width, imageData.height);
            require('imcms-image-resize').setFinalPreviewImageData(imageData);

            onLoad && onLoad.call();
        };
    },

    setBackgroundPositionX(newPositionX) {
        if ($previewImg[0].style.backgroundPositionX.trim().length === 0) {
            savePrePreviewPosition = true;
        }
        $previewImg[0].style.backgroundPositionX = `${newPositionX}px`;
        if (savePrePreviewPosition) {
            require('imcms-image-resize').setFinalPreviewBackGroundPositionX(newPositionX);
            savePrePreviewPosition = false;
        }
    },

    setBackgroundPositionY(newPositionY) {
        if ($previewImg[0].style.backgroundPositionY.trim().length === 0) {
            savePrePreviewPosition = true;
        }
        $previewImg[0].style.backgroundPositionY = `${newPositionY}px`;
        if (savePrePreviewPosition) {
            require('imcms-image-resize').setFinalPreviewBackGroundPositionY(newPositionY);
            savePrePreviewPosition = false;
        }
    },

    setBacBackgroundSize(width, height) {
        $previewImg.css('background-size', `${width}px ${height}px`);
    },

    getBackgroundPositionX() {
        return parseInt($previewImg[0].style.backgroundPositionX, 10)
    },

    getBackgroundPositionY() {
        return parseInt($previewImg[0].style.backgroundPositionY, 10)
    },

    setBackgroundWidth(newWidth) {
        const backgroundSize = $previewImg[0].style.backgroundSize;
        this.setBacBackgroundSize(newWidth, backgroundSize ? this.getBackgroundHeight() : 0);
    },

    setBackgroundHeight(newHeight) {
        const backgroundSize = $previewImg[0].style.backgroundSize;
        this.setBacBackgroundSize(backgroundSize ? this.getBackgroundWidth() : 0, newHeight);
    },

    getBackgroundWidth() {
        const backgroundSize = $previewImg[0].style.backgroundSize;
        return backgroundSize ? parseInt(backgroundSize.split(' ')[0], 10) : 0
    },

    getBackgroundHeight() {
        const backgroundSize = $previewImg[0].style.backgroundSize;
        return (backgroundSize && backgroundSize.includes(' ')) ? parseInt(backgroundSize.split(' ')[1], 10) : 0
    },
    getPreviewImage: getPreviewImage,
    getPreviewImageArea: getPreviewImageArea,
    getPreviewImageContainer: getPreviewImageContainer,
    clearData: clear,
};
