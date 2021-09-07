define(
    'imcms-image-zoom',
    [
        'jquery', 'imcms-originally-image', 'imcms-originally-area', 'imcms-preview-image-area', 'imcms-i18n-texts',
        'imcms-components-builder', 'css-utils', 'imcms-image-rotate'
    ],
    function ($, originalImage, originallyImageArea, previewImageArea, i18nTexts,
              components, cssUtils, imageRotate) {

        const texts = i18nTexts.editors.image;

        let $zoomGradeField;

        function buildZoomGradeField() {
            $zoomGradeField = $('<div>', {
                class: 'percentage-image-info imcms-input imcms-number-box imcms-number-box__input',
            });
            components.overlays.defaultTooltip($zoomGradeField, texts.zoomGrade);

            return $zoomGradeField;
        }

        function getCurrentZoom($image) {
            const transformStyle = $image[0].style.transform;
            const scale = parseFloat(transformStyle.substring(transformStyle.indexOf('scale(') + 6));
            return scale || 1
        }

        function getBorderWidth($image) {
            return parseFloat($image.css('border-bottom-width'));
        }

        function updateZoomGradeValue() {
            const isPreview = isPreviewTab();
            const $image = isPreview ? previewImageArea.getPreviewImage() : originalImage.getImage();
            const currentZoom = getCurrentZoom($image);

            updateZoomGradeValueByCssProperty(currentZoom);
        }
        
        function updateImageToCoverContainerEditor() {
            const isPreview = isPreviewTab();
            const $image = isPreview ? previewImageArea.getPreviewImage() : originalImage.getImage();

            $image.css('transform', getUpdatedTransformString(1, $image));
            updateZoomGradeValue();
            fitImage();
        }

        function updateZoomGradeValueByCssProperty(cssZoomProperty) {
            const percentValue = Number((cssZoomProperty * 100).toFixed(1));
            $zoomGradeField.text(`${percentValue}%`);
        }

        function isPreviewTab() {
            return $('.imcms-editable-img-control-tabs__tab--active').data('tab') === 'prev';
        }

        function fitImage() {
            const isPreview = isPreviewTab();
            const $imageArea = isPreview ? previewImageArea.getPreviewImageArea() : originallyImageArea.getOriginalImageArea();
            const $image = isPreview ? previewImageArea.getPreviewImage() : originalImage.getImage();

            const currentZoom = getCurrentZoom($image);
            const imageSize = getImageSize($image, currentZoom);

            if (imageSize.width < $imageArea.width() && imageSize.height < $imageArea.height()) {
                return;
            }

            const heightScale = imageSize.width / $imageArea.width();
            const widthScale = imageSize.height / $imageArea.height();

            const zoomScale = widthScale > heightScale ? (1 / widthScale) : (1 / heightScale);
            const newZoomValue = currentZoom * zoomScale;

            $image.css('transform', getUpdatedTransformString(newZoomValue, $image));

            updateZoomGradeValueByCssProperty(newZoomValue);
        }

        function getImageSize($image, currentZoom = 1) {
            const imageBorderWidth = getBorderWidth($image);

            const width = ($image.width() + imageBorderWidth * 2) * currentZoom;
            const height = ($image.height() + imageBorderWidth * 2) * currentZoom;

            return imageRotate.getCurrentAngle().proportionsInverted
                ? { width: height, height: width }
                : { width, height };
        }

        function getUpdatedTransformString(zoomValue, $image) {
            const transformString = $image[0].style.transform;
            const cleanTransform = cssUtils.removeCssFunctionsFromString(transformString, ['scale']);
            const newScaleCssFunction = getScaleCssFunction(zoomValue);

            return `${newScaleCssFunction} ${cleanTransform}`;
        }

        function getScaleCssFunction(zoomValue) {
            return `scale(${zoomValue})`;
        }

        function getRelativeZoomValueByOriginalImg() {
            const $imageArea = originallyImageArea.getOriginalImageArea();
            const $image = originalImage.getImage();

            const imageBorderWidth = getBorderWidth($image);

            const imageWidth = ($image.width() + imageBorderWidth * 2) ;
            const imageHeight = ($image.height() + imageBorderWidth * 2);

            if (imageWidth < $imageArea.width() && imageHeight < $imageArea.height()) {
                return 1;
            }

            const widthScale = imageWidth / $imageArea.width();
            const heightScale = imageHeight / $imageArea.height();

	        const newZoomValue = widthScale > heightScale ? (1 / widthScale) : (1 / heightScale);

            updateZoomGradeValueByCssProperty(newZoomValue);
            return newZoomValue;
        }

        function zoom(scale) {
            const isPreview = isPreviewTab();
            const $image = isPreview ? previewImageArea.getPreviewImage() : originalImage.getImage();
            const newZoomValue = scale ? getCurrentZoom($image) * scale : 1;

            $image.css('transform', getUpdatedTransformString(newZoomValue, $image));
            updateZoomGradeValueByCssProperty(newZoomValue);
        }

        function zoomPlus() {
            zoom(2);
        }

        function zoomMinus() {
            zoom(0.5);
        }

        function resetZoom() {
            zoom(0);
        }

        function clearData() {
            $zoomGradeField.text('');
        }

        return {
            buildZoomGradeField,
            updateZoomGradeValue,
            fitImage,
            getRelativeZoomValueByOriginalImg,
            zoom,
            zoomPlus,
            zoomMinus,
            resetZoom,
            clearData,
            getUpdatedTransformString,
            updateImageToCoverContainerEditor,
        }
    }
);