define(
    'imcms-image-zoom',
    ['jquery', 'imcms-editable-image',  'imcms-editable-area', 'imcms-preview-image-area'],
    function ($, editableImage,  editableImageArea, previewImageArea) {

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

            $image.css('zoom', currentZoom * zoomScale);
        }

        function zoom(scale) {
            if (isPreviewTab()) {
                const $previewArea = previewImageArea.getPreviewImage();

                if (!scale) {
                    $previewArea.css('zoom', 1);
                    return;
                }

                const currentZoom = parseFloat($previewArea.css('zoom'));
                $previewArea.css('zoom', currentZoom * scale);

            } else {
                const $originArea = editableImage.getImage();

                if (!scale) {
                    $originArea.css('zoom', 1);
                    return;
                }

                const currentZoom = parseFloat($originArea.css('zoom'));
                $originArea.css('zoom', currentZoom * scale);
            }
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

        return {
            fitImage,
            zoom,
            zoomPlus,
            zoomMinus,
            resetZoom,
        }
    }
);