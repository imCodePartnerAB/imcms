define(
    'imcms-image-zoom',
    [
        'jquery', 'imcms-originally-image', 'imcms-originally-area', 'imcms-preview-image-area', 'imcms-i18n-texts',
        'imcms-components-builder', 'check-browser'
    ],
    function ($, originalImage, originallyImageArea, previewImageArea, i18nTexts,
              components, checkerBrowser) {

        const texts = i18nTexts.editors.image;

        let $zoomGradeField;

        function buildZoomGradeField() {
            $zoomGradeField = $('<div>', {
                class: 'percentage-image-info imcms-input imcms-number-box imcms-number-box__input',
            });
            components.overlays.defaultTooltip($zoomGradeField, texts.zoomGrade);

            return $zoomGradeField;
        }

        function getCurrentZoomByBrowser($image, isFireFox) {
            let currentZoomVal;

            if (isFireFox) {
                const scaleTransform = $image[0].style.transform.trim();
                if (scaleTransform !== '') {
                    currentZoomVal = parseFloat(scaleTransform.replace(/[^\d\\.]*/g, ''));
                } else {
                    currentZoomVal = parseFloat(1);
                }
            } else {
                currentZoomVal = parseFloat($image.css('zoom'));
            }
            return currentZoomVal;
        }

        function getBorderWidthByBrowser($image, isFireFox) {
            let border;
            if (isFireFox) {
                border = parseInt($image.css('border-bottom-width').replace(/[^\d\\.]*/g, ''));
            } else {
                border = parseInt($image.css('border-width'));
            }

            return border;
        }

        function updateZoomGradeValue() {
            const isPreview = isPreviewTab();
            const $image = isPreview ? previewImageArea.getPreviewImage() : originalImage.getImage();
            const currentZoom = getCurrentZoomByBrowser($image, checkerBrowser.isFirefox());

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
            const isFireFox = checkerBrowser.isFirefox();
            const $imageArea = isPreview ? previewImageArea.getPreviewImageArea() : originallyImageArea.getOriginalImageArea();
            const $image = isPreview ? previewImageArea.getPreviewImage() : originalImage.getImage();

            let currentZoom = getCurrentZoomByBrowser($image, isFireFox);
            const imageBorderWidth = getBorderWidthByBrowser($image, isFireFox);

            const imageWidth = ($image.width() + imageBorderWidth * 2) * currentZoom;
            const imageHeight = ($image.height() + imageBorderWidth * 2) * currentZoom;

            if (imageWidth < $imageArea.width() && imageHeight < $imageArea.height()) {
                return;
            }

            const widthScale = imageWidth / $imageArea.width();
            const heightScale = imageHeight / $imageArea.height();

            const zoomScale = widthScale > heightScale ? (1 / widthScale) : (1 / heightScale);
            const newZoomValue = currentZoom * zoomScale;

            isFireFox
                ? $image.css('transform', `scale(${newZoomValue}) translate(-2%, -2%)`)
                : $image.css('zoom', newZoomValue);

            updateZoomGradeValueByCssProperty(newZoomValue);
        }

        function getRelativeZoomValueByOriginalImg() {
            const $imageArea = originallyImageArea.getOriginalImageArea();
            const $image = originalImage.getImage();
            const isFirefox = checkerBrowser.isFirefox();

            const currentZoom = getCurrentZoomByBrowser($image, isFirefox);
            const imageBorderWidth = getBorderWidthByBrowser($image, isFirefox);

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
            const isFireFox = checkerBrowser.isFirefox()

            if (!scale) {
                if (isFireFox) {
                    $image.css('transform', `scale(1)`);
                } else {
                    $image.css('zoom', 1);
                }
                updateZoomGradeValueByCssProperty(1);
                return;
            }

            const currentZoom = getCurrentZoomByBrowser($image, isFireFox);
            const newZoomValue = currentZoom * scale;

            isFireFox ? $image.css('transform', `scale(${newZoomValue})`) : $image.css('zoom', newZoomValue);
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
            clearData
        }
    }
);