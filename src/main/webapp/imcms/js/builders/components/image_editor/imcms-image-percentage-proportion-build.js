define(
    'imcms-image-percentage-proportion-build',
    ['jquery', 'imcms-image-resize'],
    function ($, imageResize) {

        function getPercentageImageFromEditControls($inputWidth, $inputHeight, $place) {
            const originalWidth = imageResize.getWidth();
            const originalHeight = imageResize.getHeight();
            const originalRatio = originalHeight * originalWidth;

            const currentWidth = $inputWidth.getInput().val();
            const currentHeight = $inputHeight.getInput().val();

            $place.text(((currentWidth * currentHeight * 100) / originalRatio).toFixed(1) + "%");
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
            getPercentageImageFromControl: ($inputWidth, $inputHeight, $place) =>
                getPercentageImageFromEditControls($inputWidth, $inputHeight, $place),

            buildPercentageImage: (width, height, $place) => buildPercentageProportionImage(width, height, $place),

            getPercentageImageData: (width, height) => getPercentageProportionImage(width, height)

        };
    }
);