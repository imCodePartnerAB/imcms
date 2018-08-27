Imcms.define(
    "imcms-image-editor-left-side-builder",
    ["imcms-bem-builder", "jquery", "imcms-image-crop-angles", "imcms-image-cropping-elements"],

    function (BEM, $, croppingAngles, cropElements) {

        function buildPreviewImageArea(imageDataContainers) {
            var previewImageAreaBEM = new BEM({
                block: "imcms-preview-img-area",
                elements: {
                    "container": "imcms-preview-img-container",
                    "img": "imcms-preview-img"
                }
            });

            imageDataContainers.$previewImgContainer = previewImageAreaBEM.buildElement("container", "<div>");
            imageDataContainers.$previewImg = previewImageAreaBEM.buildElement("img", "<img>");
            imageDataContainers.$previewImg.appendTo(imageDataContainers.$previewImgContainer);

            return previewImageAreaBEM.buildBlock("<div>", [
                {"container": imageDataContainers.$previewImgContainer}
            ]);
        }

        function buildEditableImageArea(imageDataContainers) {
            var editableImgAreaBEM = new BEM({
                block: "imcms-editable-img-area",
                elements: {
                    "img": "imcms-editable-img",
                    "layout": "",
                    "crop-area": "imcms-crop-area",
                    "angle": "imcms-angle"
                }
            });

            imageDataContainers.$shadow = editableImgAreaBEM.buildElement("layout", "<div>");

            return editableImgAreaBEM.buildBlock("<div>", [
                {"img": cropElements.buildImage()},
                {"layout": imageDataContainers.$shadow},
                {"crop-area": cropElements.buildCropArea()},
                {"angle": croppingAngles.topLeft.buildAngle()},
                {"angle": croppingAngles.topRight.buildAngle()},
                {"angle": croppingAngles.bottomRight.buildAngle()},
                {"angle": croppingAngles.bottomLeft.buildAngle()}
            ]);
        }

        return {
            build: function (opts) {
                opts.imageDataContainers.$editableImageArea = buildEditableImageArea(opts.imageDataContainers);
                var $previewImageArea = buildPreviewImageArea(opts.imageDataContainers);

                return $("<div>").append(
                    opts.imageDataContainers.$editableImageArea,
                    $previewImageArea
                );
            }
        };
    }
);
