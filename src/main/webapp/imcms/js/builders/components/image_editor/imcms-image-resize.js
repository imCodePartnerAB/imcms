/**
 * @author Serhii Maksymchuk from Ubrainians for imCode
 * @partner Victor Pavlenko from Ubrainians for imCode
 * 27.03.18
 */
const originalImage = require('imcms-originally-image');
const previewImage = require('imcms-preview-image-area');

let saveProportions = true; // by default
let resetToOriginal = false; // by default
const original = {};
const preview = {};
const currentSize = {};
const currentPrevSize = {};
const finalImageStylesPosition = {};
let currentFinalPrevImg = {};
let proportionsCoefficient;
let selectedImgActive = false; // default value
let existsCropRegion = true;

let maxWidth, maxHeight, minWidth, minHeight, recursionDepth = 0;
const MAX_RECURSION_DEPTH = 20;

function trimToMaxMinWidth(newWidth) {
    if (newWidth === 0) newWidth = 1;
    if (maxWidth) newWidth = Math.min(newWidth, maxWidth);
    if (minWidth) newWidth = Math.max(newWidth, minWidth);

    return newWidth;
}

function trimToMaxMinHeight(newHeight) {
    if (newHeight === 0) newHeight = 1;
    if (maxHeight) newHeight = Math.min(newHeight, maxHeight);
    if (minHeight) newHeight = Math.max(newHeight, minHeight);

    return newHeight;
}

function setWidth(newWidth, isOriginal) {
    if (isOriginal) {
        const $image = originalImage.getImage();
        $image.width(original.width);
        $widthControl.val(original.width);
    } else {
        const $image = previewImage.getPreviewImage();
        if (selectedImgActive) {
            $image.width(newWidth);
            previewImage.setBackgroundWidth(newWidth);
            previewImage.setBackgroundPositionX(0);

            $widthPreviewControl.val(newWidth);
            $widthWantedControl.val(newWidth);
        } else {
            const oldWidth = $image.width();
            const k = newWidth / oldWidth;

            const newImageLeft = k * previewImage.getBackgroundPositionX();
            const newImageBackgroundWidth = k * previewImage.getBackgroundWidth();

            $image.width(newWidth);
            previewImage.setBackgroundWidth(newImageBackgroundWidth);
            previewImage.setBackgroundPositionX(newImageLeft);

            $widthPreviewControl.val(newWidth);
            $widthWantedControl.val(newWidth);
        }
    }

}

function setHeight(newHeight, isOriginal) {
    if (isOriginal) {
        const $image = originalImage.getImage();
        $image.height(original.height);
        $heightControl.val(original.height);
    } else {
        const $image = previewImage.getPreviewImage();
        if (selectedImgActive) {
            $image.height(newHeight);
            previewImage.setBackgroundHeight(newHeight);
            previewImage.setBackgroundPositionY(0);

            $heightPreviewControl.val(newHeight);
            $heightWantedControl.val(newHeight);
        } else {
            const oldHeight = $image.height();
            const k = newHeight / oldHeight;
            const newImageTop = k * previewImage.getBackgroundPositionY();
            const newImageBackgroundHeight = k * previewImage.getBackgroundHeight();

            $image.height(newHeight);
            previewImage.setBackgroundHeight(newImageBackgroundHeight);
            previewImage.setBackgroundPositionY(newImageTop);

            $heightPreviewControl.val(newHeight);
            $heightWantedControl.val(newHeight);
        }
    }
}

function setHeightProportionally(newHeight, isOriginal) {
	newHeight = trimToMaxMinHeight(newHeight);
	setHeight(newHeight, isOriginal);
	if (saveProportions) {
		updateWidthProportionally(newHeight, isOriginal);
	}
}

function setWidthProportionally(newWidth, isOriginal) {
	newWidth = trimToMaxMinWidth(newWidth);
	setWidth(newWidth, isOriginal);
	if (saveProportions) {
		updateHeightProportionally(newWidth, isOriginal);
	}
}

function updateWidthProportionally(newHeight, isOriginal) {
	const proportionalWidth = ~~(newHeight * proportionsCoefficient);
	const fixedWidth = trimToMaxMinWidth(proportionalWidth);

    if (recursionDepth >= MAX_RECURSION_DEPTH) {
        console.debug("Max recursion depth reached. Aborting.");
        setWidth(proportionalWidth, isOriginal);
        return;
    }

    recursionDepth += 1;

	(fixedWidth === proportionalWidth)
		? setWidth(proportionalWidth, isOriginal)
		: setWidthProportionally(proportionalWidth, isOriginal); // MAY (or not) APPEAR RECURSIVE!!!11 be careful
}

function updateHeightProportionally(newWidth, isOriginal) {
	const proportionalHeight = ~~(newWidth / proportionsCoefficient);
	const fixedHeight = trimToMaxMinHeight(proportionalHeight);

    if (recursionDepth >= MAX_RECURSION_DEPTH) {
        console.debug("Max recursion depth reached. Aborting.");
        setHeight(proportionalHeight, isOriginal);
        return;
    }

    recursionDepth += 1;

	(fixedHeight === proportionalHeight)
		? setHeight(proportionalHeight, isOriginal)
		: setHeightProportionally(proportionalHeight, isOriginal); // MAY (or not) APPEAR RECURSIVE!!!11 be careful
}

let $heightControl, $widthControl;
let $heightPreviewControl, $widthPreviewControl;
let $heightWantedControl, $widthWantedControl;
let isRestrictedValuesChanged = true;

function changeRestrictionsForRotation(needChange){
    if(needChange){
        let currentMinHeight = minHeight;
        let currentMaxHeight = maxHeight;

        minHeight = minWidth;
        maxHeight = maxWidth;
        minWidth = currentMinHeight;
        maxWidth = currentMaxHeight;

        isRestrictedValuesChanged = true;
    }else{
        isRestrictedValuesChanged = false;
    }
}

module.exports = {
    resetToOriginal(imageData) {
        this.enableResetToOriginalFlag();
        this.setHeightStrict(0, original.height, false);
        this.setWidthStrict(0, original.width, false);

        const currentProportions = saveProportions;
        saveProportions = true;

        if (minWidth && minHeight) {
            preview.width = minWidth;
            preview.height = minHeight;
            this.setCurrentPreviewSize(minWidth, minHeight);
            this.updateSizing(imageData, true, false);
        } else if (minWidth || minHeight) {
            minWidth ? setWidthProportionally(minWidth, false) : setHeightProportionally(minHeight, false);
        } else if (maxWidth && !maxHeight) {
            original.width > maxWidth ? setWidthProportionally(maxWidth, false) : setWidthProportionally(original.width, false);
        } else if (maxHeight && !maxWidth) {
            original.height > maxHeight ? setHeightProportionally(maxHeight, false) : setHeightProportionally(original.height, false);
        } else {
            preview.width = original.width;
            preview.height = original.height;
            this.setCurrentPreviewSize(original.width, original.height);
            this.updateSizing(imageData, true, false);
        }

        saveProportions = currentProportions;
        this.disabledResetToOriginalFlag();
    },

    resetToPreview(imageData) {
        this.enableResetToOriginalFlag()
        this.checkCropRegionExist(imageData);
        const cropRegion = imageData.cropRegion;

        if (cropRegion
            && (cropRegion.cropX1 >= 0)
            && (cropRegion.cropX2 >= 1)
            && (cropRegion.cropY1 >= 0)
            && (cropRegion.cropY2 >= 1)) {
            const width = cropRegion.cropX2 - cropRegion.cropX1;
            const height = cropRegion.cropY2 - cropRegion.cropY1;

            this.setCurrentSize(width, height);
            this.setWidthStrict(cropRegion.cropX1, width, false);
            this.setHeightStrict(cropRegion.cropY1, height, false);

            this.setWidth(imageData.width, false);
            this.setHeight(imageData.height, false);
        } else {
            this.setWidthStrict(0, imageData.width, false);
            this.setHeightStrict(0, imageData.height, false);
        }

        let width, height;

        if (minWidth && minHeight) {
            width = minWidth;
            height = minHeight;

        } else {
            width = imageData.width;
            height = imageData.height;
        }

        this.setCurrentPreviewSize(width, height);
        this.setPreview(width, height);
        this.updateSizing(imageData, true, false);
        this.disabledResetToOriginalFlag();
    },
    setCurrentSize(width, height) {
        currentSize.width = width;
	    currentSize.height = height;
	    proportionsCoefficient = (currentSize.width / currentSize.height);
    },
    setCurrentPreviewSize(width, height) {
        currentPrevSize.width = width;
	    currentPrevSize.height = height;
	    proportionsCoefficient = (currentPrevSize.width / currentPrevSize.height);
    },
    setFinalPreviewImageData(image) {
        currentFinalPrevImg = JSON.parse(JSON.stringify(image));
    },
    getFinalPreviewImageData: () => currentFinalPrevImg,
    setFinalPreviewBackGroundPositionX(positionX) {
        finalImageStylesPosition.backgroundPositionX = positionX;
    },
    setFinalPreviewBackGroundPositionY(positionY) {
        finalImageStylesPosition.backgroundPositionY = positionY;
    },
    getOriginal: () => original,
    setOriginal(originalWidth, originalHeight) {
        original.width = originalWidth;
        original.height = originalHeight;

        this.setCurrentSize(originalWidth, originalHeight);
    },

    getPreview: () => preview,
    setPreview(previewWidth, previewHeight) {
        preview.width = previewWidth;
        preview.height = previewHeight;

        this.setCurrentPreviewSize(previewWidth, previewHeight);
    },
    setWidthControl($control) {
        $widthControl = $control
    },

    setHeightControl($control) {
        $heightControl = $control
    },

    setPreviewWidthControl($control) {
        $widthPreviewControl = $control
    },

    setPreviewHeightControl($control) {
        $heightPreviewControl = $control
    },

    setWantedWidthControl($control) {
        $widthWantedControl = $control
    },

    setWantedHeightControl($control) {
        $heightWantedControl = $control
    },

    isProportionsLockedByStyle() {
        return minWidth && minHeight
    },

    isAnyRestrictedStyleSize() {
        return minWidth || minHeight
    },

    isRestrictedWidthStyleSize() {
        return minWidth;
    },

    isRestrictedHeightStyleSize() {
        return minHeight;
    },

    isMaxRestrictedStyleSize(){
      return maxWidth || maxHeight;
    },

    isSaveProportionsEnabled: () => saveProportions,

    getProportionsCoefficient: () => proportionsCoefficient,

    toggleSaveProportions: () => (saveProportions = !saveProportions),

    enableSaveProportions() {
        saveProportions = true;
    },

    enableSelectedImageFlag() {
        selectedImgActive = true;
    },

    disabledSelectedImageFlag() {
        selectedImgActive = false;
    },

    getSelectedImgFlagValue() {
        return selectedImgActive;
    },

    enableResetToOriginalFlag() {
        resetToOriginal = true;
    },

    disabledResetToOriginalFlag() {
        resetToOriginal = false;
    },

    setHeight(newValue, isOriginal) {
        setHeight(trimToMaxMinHeight(newValue), isOriginal);
    },

    setWidth(newValue, isOriginal) {
        setWidth(trimToMaxMinWidth(newValue), isOriginal);
    },

    /**
     * Setting without any proportions or min/max checking
     * @param padding for cropped images
     * @param newWidth
     * @param isOriginal
     */

    setWidthStrict(padding, newWidth, isOriginal) {
        if (isOriginal) {
            originalImage.getImage().width(original.width);
            $widthControl.val(newWidth);
        } else {
            resetToOriginal && existsCropRegion
                ? previewImage.setBackgroundWidth(original.width)
                : previewImage.setBackgroundWidth(newWidth);
            previewImage.getPreviewImage().width(newWidth);

            (padding >= 0)
                ? previewImage.setBackgroundPositionX(-padding)
                : previewImage.setBackgroundPositionX(padding);

            $widthPreviewControl.val(newWidth);
            $widthWantedControl.val(newWidth);
        }
    },

    /**
     * Setting without any proportions or min/max checking
     * @param padding for cropped images
     * @param newHeight
     * @param isOriginal
     */
    setHeightStrict(padding, newHeight, isOriginal) {
        if (isOriginal) {
            originalImage.getImage().height(original.height);
            $heightControl.val(newHeight);
        } else {
            resetToOriginal && existsCropRegion
                ? previewImage.setBackgroundHeight(original.height)
                : previewImage.setBackgroundHeight(newHeight);
            previewImage.getPreviewImage().height(newHeight);

            (padding >= 0)
                ? previewImage.setBackgroundPositionY(-padding)
                : previewImage.setBackgroundPositionY(padding);

            $heightPreviewControl.val(newHeight);
            $heightWantedControl.val(newHeight);
        }
    },

    setHeightProportionally: setHeightProportionally,

    setWidthProportionally: setWidthProportionally,

    getWidth: () => originalImage.getImage().width(),

    getHeight: () => originalImage.getImage().height(),

    getPreviewWidth: () => previewImage.getPreviewImage().width(),

    getPreviewHeight: () => previewImage.getPreviewImage().height(),

    setMaxWidth(maxWidthValue) {
        maxWidth = maxWidthValue
    },

    setMaxHeight(maxHeightValue) {
        maxHeight = maxHeightValue
    },

    setMinWidth(minWidthValue) {
        minWidth = minWidthValue
    },

    setMinHeight(minHeightValue) {
        minHeight = minHeightValue
    },

    /**
     * Can be used after setting strict w/h to update all proportions and min/max restrictions
     * @param isOriginal value for original image or original tab data
     * @param imageData
     * @param ignoreCropping
     */
    updateSizing(imageData, ignoreCropping, isOriginal) {
        const originalProportionsK = isOriginal ? original.width / original.height : preview.width / preview.height;

        if (minWidth && minHeight && (originalProportionsK !== (minWidth / minHeight))) {
            const width = minWidth;
            const height = minHeight;
            const restrictedProportionsK = minWidth / minHeight;

            const dX = isOriginal ? width / original.width : width / preview.width;
            const dY = isOriginal ? height / original.height : height / preview.height;

            let newWidth, newHeight, cropHeight, cropWidth;

            const cropRegion = imageData.cropRegion || (imageData.cropRegion = {
                cropX1: 0,
                cropX2: 0,
                cropY1: 0,
                cropY2: 0,
            });

            const widthCurrentObj = isOriginal ? original.width : preview.width;
            const heightCurrentObj = isOriginal ? original.height : preview.height;

            if (dX > dY) {
                const croppedWidth = Math.max(0, cropRegion.cropX2 - cropRegion.cropX1);

                newWidth = Math.max(width, ignoreCropping ? croppedWidth : widthCurrentObj);
                newWidth = Math.min(newWidth, ignoreCropping ? croppedWidth : widthCurrentObj);
                newHeight = ~~(newWidth / proportionsCoefficient);
                cropHeight = ~~(newWidth / restrictedProportionsK);
                cropWidth = newWidth;
            } else {
                const croppedHeight = Math.max(0, cropRegion.cropY2 - cropRegion.cropY1);

                newHeight = Math.max(height, ignoreCropping ? croppedHeight : heightCurrentObj);
                newHeight = Math.min(newHeight, ignoreCropping ? croppedHeight : heightCurrentObj);
                newWidth = ~~(newHeight * proportionsCoefficient);
                cropWidth = ~~(newHeight * restrictedProportionsK);
                cropHeight = newHeight;
            }

            this.setWidthStrict(cropRegion.cropX1, newWidth, isOriginal);
            this.setHeightStrict(cropRegion.cropY1, newHeight, isOriginal);

	        if (!ignoreCropping) {
		        cropRegion.cropX2 = cropWidth;
		        cropRegion.cropY2 = cropHeight;
	        }
        }

	    const currentWidth = isOriginal ? currentSize.width : currentPrevSize.width;
	    const currentHeight = isOriginal ? currentSize.height : currentPrevSize.height;

        recursionDepth = 0;
	    setHeightProportionally(currentHeight, isOriginal);
	    setWidthProportionally(currentWidth, isOriginal);
    },

    isRestrictedValuesChanged(){
        return isRestrictedValuesChanged;
    },

    changeSizingForRotating(previousDegrees, degrees){
        const currentSaveProportions = saveProportions;
        saveProportions = true;

        if(degrees === 90 || degrees === 270){

            if(minHeight && minWidth && minWidth === minHeight){
                // skip and do the default action
            }else if((minWidth && minWidth === maxWidth) || (minHeight && minWidth === maxWidth)){
                changeRestrictionsForRotation(true);
                minWidth ? setWidthProportionally(minHeight, false)
                                : setHeightProportionally(minWidth, false);
            }else{
                degrees = previousDegrees === 180 ? 0 : 180;
                isRestrictedValuesChanged = false;
            }

        }else{

            if((minWidth && minWidth === maxWidth) || (minHeight && minWidth === maxWidth)){
                changeRestrictionsForRotation(previousDegrees === 90 || previousDegrees === 270);
                minWidth ? setWidthProportionally(minWidth, false)
                                : setHeightProportionally(minHeight, false);
            }

        }

        saveProportions = currentSaveProportions;
        return degrees;
    },

    checkCropRegionExist(image) {
        existsCropRegion = (image.cropRegion.cropX1 > 0
            || image.cropRegion.cropX2 > 0
            || image.cropRegion.cropY1 > 0
            || image.cropRegion.cropY2 > 0);
    },

    clearData() {
        maxWidth = null;
        maxHeight = null;
        minWidth = null;
        minHeight = null;
        saveProportions = true;
        proportionsCoefficient = 1;
        original.width = null;
        original.height = null;
        preview.width = null;
        preview.height = null;
        currentPrevSize.width = null;
        currentPrevSize.height = null;
        currentFinalPrevImg.width = null;
        currentFinalPrevImg.height = null;
        finalImageStylesPosition.backgroundPositionY = null;
        finalImageStylesPosition.backgroundPositionX = null;
        resetToOriginal = false;
        selectedImgActive = false;
        existsCropRegion = true;
    },
};
