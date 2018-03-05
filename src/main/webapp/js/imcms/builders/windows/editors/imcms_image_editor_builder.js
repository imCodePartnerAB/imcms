/**
 * Created by Serhii Maksymchuk from Ubrainians for imCode
 * 21.08.17
 */
Imcms.define("imcms-image-editor-builder",
    [
        "imcms-window-builder", "imcms-images-rest-api", "imcms-image-cropper", "jquery", "imcms-events", "imcms",
        "imcms-image-editor-factory"
    ],
    function (WindowBuilder, imageRestApi, imageCropper, $, events, imcms,
              imageEditorFactory) {

        var imageDataContainers = {};
        var imageData = {};

        function toggleImgArea() {

            function initPreviewImageArea() {
                var $previewArea = $(".imcms-preview-img-area"),
                    $previewContainer = $previewArea.find(".imcms-preview-img-container"),
                    $previewImg = $previewArea.find(".imcms-preview-img"),
                    $cropArea = $(".imcms-crop-area"),
                    $editableImg = $(".imcms-editable-img-area__img"),
                    imgSrc = $editableImg.attr("src"),
                    previewContainerProp = {},
                    previewImgProp = {}
                ;

                previewContainerProp.width = parseInt($cropArea.css("width"));
                previewContainerProp.height = parseInt($cropArea.css("height"));
                previewContainerProp.ml = -previewContainerProp.width / 2;
                previewContainerProp.mt = -previewContainerProp.height / 2;

                previewImgProp.width = parseInt($editableImg.css("width"));
                previewImgProp.height = parseInt($editableImg.css("height"));
                previewImgProp.left = parseInt($cropArea.css("left")) - 2;
                previewImgProp.top = parseInt($cropArea.css("top")) - 2;

                if (previewContainerProp.height > $previewArea.outerHeight()) {
                    $previewContainer.css({
                        "margin-left": previewContainerProp.ml + "px",
                        "margin-top": 0,
                        "left": "50%",
                        "top": 2 + "px"
                    });
                } else if (previewContainerProp.width > $previewArea.outerWidth()) {
                    $previewContainer.css({
                        "margin-left": 0,
                        "margin-top": previewContainerProp.mt + "px",
                        "left": 0,
                        "top": "50%"
                    });
                } else {
                    $previewContainer.css({
                        "margin-left": previewContainerProp.ml + "px",
                        "margin-top": previewContainerProp.mt + "px",
                        "left": "50%",
                        "top": "50%"
                    });
                }

                $previewContainer.css({
                    "width": previewContainerProp.width + "px",
                    "height": previewContainerProp.height + "px"
                });

                $previewImg.css({
                    "width": previewImgProp.width + "px",
                    "height": previewImgProp.height + "px",
                    "left": -previewImgProp.left + "px",
                    "top": -previewImgProp.top + "px"
                });

                $previewImg.attr("src", imgSrc);
            }

            var $previewImageArea = $(".imcms-preview-img-area"),
                $controlTabs = $(".imcms-editable-img-control-tabs__tab")
            ;

            if ($(this).data("tab") === "prev") {
                initPreviewImageArea();
                $previewImageArea.css({
                    "z-index": "50",
                    "display": "block"
                });

                if ($tag.width() !== 0 && $tag.height() !== 0) {

                    // change size of preview image
                    imageDataContainers.$previewImg.width(
                        imageDataContainers.$cropImg.width() * $tag.width() / imageDataContainers.$cropArea.width()
                    );

                    imageDataContainers.$previewImg.height(
                        imageDataContainers.$cropImg.height() * $tag.height() / imageDataContainers.$cropArea.height()
                    );

                    // change top and left properties of preview image
                    var newTopValue = imageDataContainers.$previewImg.height() * parseInt(imageDataContainers.$cropImg.css("top"), 10)
                        / imageDataContainers.$image.height();

                    imageDataContainers.$previewImg.css("top", newTopValue + "px");

                    var newLeftValue = imageDataContainers.$previewImg.width() * parseInt(imageDataContainers.$cropImg.css("left"), 10)
                        / imageDataContainers.$image.width();

                    imageDataContainers.$previewImg.css("left", newLeftValue + "px");

                    // change size of preview image container
                    imageDataContainers.$previewImgContainer.width($tag.width());
                    imageDataContainers.$previewImgContainer.height($tag.height());
                }

                imageDataContainers.$previewImgContainer.css({
                    "margin-left": 0,
                    "margin-top": 0,
                    "left": 2 + "px",
                    "top": 2 + "px"
                });
            } else {
                $previewImageArea.css({
                    "z-index": "10",
                    "display": "none"
                });
            }
            $controlTabs.removeClass("imcms-editable-img-control-tabs__tab--active");
            $(this).addClass("imcms-editable-img-control-tabs__tab--active");
        }

        function buildEditor() {
            return imageEditorFactory.buildEditor({
                toggleImgArea: toggleImgArea,
                fillData: fillData,
                imageDataContainers: imageDataContainers,
                $tag: $tag,
                imageWindowBuilder: imageWindowBuilder,
                imageData: imageData
            });
        }

        function fillData(image) {

            function fillBodyHeadData(imageData) {
                imageDataContainers.$imageTitle.text(imageData.name + "." + imageData.format);
                imageDataContainers.$imgUrl.text(imageData.path);
            }

            function fillLeftSideData(imageData) {

                imageDataContainers.$widthControlInput.getInput().val(imageData.width);
                imageDataContainers.$heightControlInput.getInput().val(imageData.height);

                imageDataContainers.$shadow.css({
                    width: "100%",
                    height: "100%"
                });

                if (!imageData.path) {
                    imageDataContainers.$image.removeAttr("src");
                    imageDataContainers.$image.removeAttr("style");

                    imageDataContainers.$cropImg.removeAttr("src");
                    imageDataContainers.$cropImg.removeAttr("style");

                    $.each(imageDataContainers.angles, function (angleKey, angle) {
                        angle.css("display", "none");
                    });

                    return;
                }

                imageDataContainers.$image.attr("src", imcms.contextPath + "/" + imcms.imagesPath + imageData.path);

                setTimeout(function () { // to let image src load
                    imageDataContainers.$image.removeAttr("style");
                    // fixes to prevent stupid little scroll because of borders
                    var angleBorderSize = parseInt(imageDataContainers.angles.$topLeft.css("border-width")) || 0;
                    var imageWidth = imageDataContainers.$image.width();
                    var imageHeight = imageDataContainers.$image.height();

                    imageDataContainers.$heightValue.text(imageHeight);
                    imageDataContainers.$widthValue.text(imageWidth);

                    if (imageData.width && imageData.height) {
                        imageDataContainers.$image.width(imageData.width);
                        imageDataContainers.$image.height(imageData.height);

                        imageWidth = imageData.width;
                        imageHeight = imageData.height;
                    }

                    imageDataContainers.$editableImageArea.width(imageDataContainers.$editableImageArea.width());  // removes float values
                    imageDataContainers.$editableImageArea.height(imageDataContainers.$editableImageArea.height());// removes float values

                    var maxShadowHeight = imageHeight + angleBorderSize * 2;
                    var maxShadowWidth = imageWidth + angleBorderSize * 2;

                    if (imageDataContainers.$shadow.height() < maxShadowHeight) {
                        imageDataContainers.$shadow.height(maxShadowHeight);
                    }

                    if (imageDataContainers.$shadow.width() < maxShadowWidth) {
                        imageDataContainers.$shadow.width(maxShadowWidth);
                    }

                    imageDataContainers.$image.css({
                        left: angleBorderSize,
                        top: angleBorderSize
                    });

                    imageDataContainers.$cropImg.attr("src", imcms.contextPath + "/" + imcms.imagesPath + imageData.path);

                    // todo: receive correct crop area
                    imageDataContainers.$cropArea.css({
                        width: imageDataContainers.$image.width(),
                        height: imageDataContainers.$image.height(),
                        left: angleBorderSize,
                        top: angleBorderSize
                    });

                    imageCropper.initImageCropper({
                        imageData: imageData,
                        $imageEditor: imageWindowBuilder.$editor,
                        $croppingArea: imageDataContainers.$cropArea,
                        $cropImg: imageDataContainers.$cropImg,
                        $originImg: imageDataContainers.$image,
                        angles: imageDataContainers.angles,
                        borderWidth: angleBorderSize
                    });
                }, 200);
            }

            if (!image) {
                return;
            }

            toggleImgArea.call(imageDataContainers.$tabOriginal);

            // direct reassign because $.extend skip 'undefined' but it's needed!
            imageData.cropRegion = image.cropRegion;
            $.extend(imageData, image);

            if (imageData.inText) {
                $tag.attr("data-index", imageData.index);
            }

            imageEditorFactory.updateImageData($tag, imageData);

            fillBodyHeadData(imageData);
            fillLeftSideData(imageData);

            imageDataContainers.$altText.$input.val(image.alternateText);
            imageDataContainers.$imgLink.$input.val(image.linkUrl);

            if (image.allLanguages !== undefined) {
                imageDataContainers.$allLanguagesCheckBox.find("input").prop('checked', image.allLanguages);
            }
        }

        function loadData(opts) {
            /** @namespace opts.loopEntryIndex */
            /** @namespace opts.loopIndex */
            if (opts.loopEntryIndex && opts.loopIndex) {

                // note that this data have to be set in such way because of non-JSON GET AJAX call
                opts["loopEntryRef.loopEntryIndex"] = opts.loopEntryIndex;
                opts["loopEntryRef.loopIndex"] = opts.loopIndex;

                delete opts.loopEntryIndex;
                delete opts.loopIndex;
            }

            $.extend(imageData, opts);

            if (opts.inText) {
                imageDataContainers.$langFlags.hideLangFlagsAndCheckbox();
            } else {
                imageDataContainers.$langFlags.showLangFlagsAndCheckbox();
                imageDataContainers.$langFlags.setActive(imcms.language.code);
            }

            imageRestApi.read(opts).done(fillData);
        }

        function clearData() {
            events.trigger("enable text editor blur");
            imageCropper.destroyImageCropper();
            imageDataContainers.$image.removeAttr("src");
            imageDataContainers.$cropArea.find("img").removeAttr("src");
        }

        var imageWindowBuilder = new WindowBuilder({
            factory: buildEditor,
            loadDataStrategy: loadData,
            clearDataStrategy: clearData
        });

        var $tag;

        return {
            setTag: function ($editedTag) {
                $tag = $editedTag;
                return this;
            },
            build: function (opts) {
                events.trigger("disable text editor blur");
                imageWindowBuilder.buildWindow.applyAsync(arguments, imageWindowBuilder);
            }
        };
    }
);
