define(
    'imcms-image-percentage-proportion-build',
    ['jquery', 'imcms-image-resize'],
    function ($, imageResize) {

        function getNewVal($input) {
            const newVal = +$input.val();

            if (isNaN(newVal) || newVal < 0) {
                $input.val($input.val().replace(/[^0-9]/g, ''));
                return;
            }

            return newVal
        }

        function getPercentageImageFromEditControls($inputWidth, $inputHeight, $place) {
            const originalWidth = imageResize.getWidth();
            const originalHeight = imageResize.getHeight();
            const originalRatio = originalHeight * originalWidth;

            const currentNewWidth = getNewVal($inputWidth.getInput());
            const currentNewHeight = getNewVal($inputHeight.getInput());

            $place.text(((currentNewWidth * currentNewHeight * 100) / originalRatio).toFixed(1) + "%");
        }

        function buildPercentageProportionImage(width, height, $place) {
            const originalWidth = imageResize.getWidth();
            const originalHeight = imageResize.getHeight();
            const originalRatio = originalHeight * originalWidth;

            $place.text(((width * height * 100) / originalRatio).toFixed(1) + "%");
        }

        function getPercentageProportionImage(image) {
            const originalWidth = imageResize.getWidth();
            const originalHeight = imageResize.getHeight();
            const originalRatio = originalHeight * originalWidth;

            const currentWidth = image.width;
            const currentHeight = image.height;

            return ((currentWidth * currentHeight * 100) / originalRatio).toFixed(1);
        }

        return {
            buildPercentageFromEditControl: ($inputWidth, $inputHeight, $place) =>
                getPercentageImageFromEditControls($inputWidth, $inputHeight, $place),

            buildPercentageImage: (width, height, $place) => buildPercentageProportionImage(width, height, $place),

            getPercentageImageData: (width, height) => getPercentageProportionImage(width, height)

        };
    }
);