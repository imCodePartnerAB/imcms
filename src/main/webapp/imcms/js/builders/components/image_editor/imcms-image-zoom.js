define(
    'imcms-image-zoom',
    [
        'jquery', 'imcms-originally-image', 'imcms-originally-area', 'imcms-preview-image-area', 'imcms-i18n-texts',
        'imcms-components-builder'
    ],
    function ($, originalImage, originallyImageArea, previewImageArea, i18nTexts,
              components) {

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

            const widthScale = imageSize.width / $imageArea.width();
            const heightScale = imageSize.height / $imageArea.height();

            const zoomScale = widthScale > heightScale ? (1 / widthScale) : (1 / heightScale);
            const newZoomValue = currentZoom * zoomScale;

            $image.css('transform', getTransformString(newZoomValue, $image));

            updateZoomGradeValueByCssProperty(newZoomValue);
        }

        function getImageSize($image, currentZoom = 1) {
            const imageBorderWidth = getBorderWidth($image);

            const width = ($image.width() + imageBorderWidth * 2) * currentZoom;
            const height = ($image.height() + imageBorderWidth * 2) * currentZoom;

            return { width, height };
        }

        function getTransformString(zoomValue, $image) {
            const imageSize = getImageSize($image);

            const horizontalTranslation = calculateTranslation(zoomValue, imageSize.width);
            const verticalTranslation = calculateTranslation(zoomValue, imageSize.height);

            return `scale(${zoomValue}) translate(${horizontalTranslation}px, ${verticalTranslation}px)`
        }

        function calculateTranslation(zoomValue, length) {
            return -length / 2 * (1 / zoomValue - 1);
        }

        function getRelativeZoomValueByOriginalImg() {
            const $imageArea = originallyImageArea.getOriginalImageArea();
            const $image = originalImage.getImage();

            const currentZoom = getCurrentZoom($image);
            const imageBorderWidth = getBorderWidth($image);

            const imageWidth = ($image.width() + imageBorderWidth * 2) * currentZoom;
            const imageHeight = ($image.height() + imageBorderWidth * 2) * currentZoom;

            if (imageWidth < $imageArea.width() && imageHeight < $imageArea.height()) {
                return 1;
            }

            const widthScale = imageWidth / $imageArea.width();
            const heightScale = imageHeight / $imageArea.height();

            const zoomScale = widthScale > heightScale ? (1 / widthScale) : (1 / heightScale);
            const newZoomValue = currentZoom * zoomScale;

            updateZoomGradeValueByCssProperty(newZoomValue);
            return newZoomValue;
        }

        function zoom(scale) {
            const isPreview = isPreviewTab();
            const $image = isPreview ? previewImageArea.getPreviewImage() : originalImage.getImage();

            if (!scale) {
                $image.css('transform', `scale(1)`);
                updateZoomGradeValueByCssProperty(1);
                return;
            }

            const currentZoom = getCurrentZoom($image);
            const newZoomValue = currentZoom * scale;

            $image.css('transform', getTransformString(newZoomValue, $image));
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
            getTransformString,
        }
    }
);