define(
    'imcms-image-zoom',
    [
        'jquery', 'imcms-editable-image', 'imcms-editable-area', 'imcms-preview-image-area', 'imcms-i18n-texts',
        'imcms-components-builder',
    ],
    function ($, editableImage, editableImageArea, previewImageArea, i18nTexts,
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

        function updateZoomGradeValue() {
            const isPreview = isPreviewTab();
            const $image = isPreview ? previewImageArea.getPreviewImage() : editableImage.getImage();
            const currentZoom = parseFloat($image.css('zoom'));

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

            const $imageArea = isPreview ? previewImageArea.getPreviewImageArea() : editableImageArea.getEditableImageArea();
            const $image = isPreview ? previewImageArea.getPreviewImage() : editableImage.getImage();

            const currentZoom = parseFloat($image.css('zoom'));
            const imageBorderWidth = parseInt($image.css('border-width'));

            const imageWidth = ($image.width() + imageBorderWidth * 2) * currentZoom;
            const imageHeight = ($image.height() + imageBorderWidth * 2) * currentZoom;

            if (imageWidth < $imageArea.width() && imageHeight < $imageArea.height()) {
                return;
            }

            const widthScale = imageWidth / $imageArea.width();
            const heightScale = imageHeight / $imageArea.height();

            const zoomScale = widthScale > heightScale ? (1 / widthScale) : (1 / heightScale);
            const newZoomValue = currentZoom * zoomScale;

            $image.css('zoom', newZoomValue);
            updateZoomGradeValueByCssProperty(newZoomValue);
        }

        function zoom(scale) {
            const isPreview = isPreviewTab();
            const $image = isPreview ? previewImageArea.getPreviewImage() : editableImage.getImage();

            if (!scale) {
                $image.css('zoom', 1);
                updateZoomGradeValueByCssProperty(1);
                return;
            }

            const currentZoom = parseFloat($image.css('zoom'));
            const newZoomValue = currentZoom * scale;

            $image.css('zoom', newZoomValue);
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
            zoom,
            zoomPlus,
            zoomMinus,
            resetZoom,
            clearData
        }
    }
);