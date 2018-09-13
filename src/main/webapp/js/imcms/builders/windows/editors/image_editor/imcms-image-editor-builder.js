/**
 * Created by Serhii Maksymchuk from Ubrainians for imCode
 * 21.08.17
 */
define(
    "imcms-image-editor-builder",
    [
        "imcms-window-builder", "imcms-images-rest-api", "imcms-image-cropper", "jquery", "imcms-events", "imcms",
        "imcms-image-editor-factory", "imcms-image-crop-angles", "imcms-image-cropping-elements", "imcms-image-rotate"
    ],
    function (WindowBuilder, imageRestApi, imageCropper, $, events, imcms, imageEditorFactory, cropAngles, cropElements,
              imageRotate) {

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
                        cropElements.$cropImg.width() * $tag.width() / cropElements.$cropArea.width()
                    );

                    imageDataContainers.$previewImg.height(
                        cropElements.$cropImg.height() * $tag.height() / cropElements.$cropArea.height()
                    );

                    // change top and left properties of preview image
                    var newTopValue = imageDataContainers.$previewImg.height() * cropElements.$cropImg.getTop()
                        / cropElements.$image.height();

                    imageDataContainers.$previewImg.css("top", newTopValue + "px");

                    var newLeftValue = imageDataContainers.$previewImg.width() * cropElements.$cropImg.getLeft()
                        / cropElements.$image.width();

                    imageDataContainers.$previewImg.css("left", newLeftValue + "px");

                    // change size of preview image container
                    imageDataContainers.$previewImgContainer.width($tag.width());
                    imageDataContainers.$previewImgContainer.height($tag.height());
                }

                imageDataContainers.$previewImgContainer.css({
                    "margin-left": 0,
                    "margin-top": 0,
                    width: cropElements.$cropArea.width(),
                    height: cropElements.$cropArea.height(),
                    left: 2,
                    top: 2
                });

                var css = imageRotate.getCurrentRotateCss();
                css.width = cropElements.$image.width();
                css.height = cropElements.$image.height();
                css.top = 2 - cropElements.$cropArea.getTop();
                css.left = 2 - cropElements.$cropArea.getLeft();

                imageDataContainers.$previewImg.css(css);

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

        function fillBodyHeadData(imageData) {
            let titleText = imageData.name;

            if (titleText.length > 15) {
                titleText = titleText.substr(0, 15) + "...";
            }

            imageDataContainers.$imgUrl.text(titleText);
            imageDataContainers.$imgUrl.attr('href', '/images' + (imageData.path.startsWith('/') ? '' : '/') + imageData.path);
            imageDataContainers.$imgUrl.attr('target', '_blank');
            imageDataContainers.$imgUrl.attr("title", imageData.path);
        }

        function fillLeftSideData(imageData) {

            imageDataContainers.$widthControlInput.getInput().val(imageData.width);
            imageDataContainers.$heightControlInput.getInput().val(imageData.height);

            imageDataContainers.$shadow.css({
                width: "100%",
                height: "100%"
            });

            if (!imageData.path) {
                cropElements.$image.removeAttr("src");
                cropElements.$image.removeAttr("style");

                cropElements.$cropImg.removeAttr("src");
                cropElements.$cropImg.removeAttr("style");

                cropElements.$cropArea.removeAttr("style").width(0);

                cropAngles.hideAll();
                events.trigger("clean crop coordinates");

                return;
            }

            cropElements.$image.css("display", "none");
            cropElements.$image.attr("src", imcms.contextPath + "/" + imcms.imagesPath + imageData.path);

            setTimeout(function () { // to let image src load
                const style = $tag.data('style');
                const resultStyleObj = {};

                // fixes to prevent stupid little scroll because of borders
                let imageWidth = cropElements.$image.width();
                let imageHeight = cropElements.$image.height();

                imageDataContainers.original = {
                    width: imageWidth,
                    height: imageHeight
                };

                imageDataContainers.$heightValue.text(imageHeight);
                imageDataContainers.$widthValue.text(imageWidth);

                // disabled because not finished
                // if (style) {
                //     style.split(';')
                //         .map(x => x.trim())
                //         .filter(x => !!x)
                //         .forEach(x => {
                //             const styleKeyAndValue = x.split(':').map(x => x.trim());
                //             resultStyleObj[styleKeyAndValue[0]] = styleKeyAndValue[1];
                //         });
                //
                //     cropElements.$image.css(resultStyleObj);
                //
                //     if (resultStyleObj.width) {
                //         imageData.width = parseInt(resultStyleObj.width);
                //         const $input = imageDataContainers.$widthControlInput.getInput();
                //         $input.val(imageData.width);
                //         $input.attr('disabled', 'disabled');
                //     }
                //     if (resultStyleObj.height) {
                //         imageData.height = parseInt(resultStyleObj.height);
                //         const $input = imageDataContainers.$heightControlInput.getInput();
                //         $input.val(imageData.height);
                //         $input.attr('disabled', 'disabled');
                //     }
                //     if (resultStyleObj['max-width']) {
                //         imageData.width = Math.max(imageData.width, parseInt(resultStyleObj['max-width']))
                //     }
                //     if (resultStyleObj['max-height']) {
                //         imageData.height = Math.max(imageData.height, parseInt(resultStyleObj['max-height']))
                //     }
                // }

                if (imageData.width && imageData.height) {
                    cropElements.$image.width(imageData.width);
                    cropElements.$image.height(imageData.height);

                    imageWidth = imageData.width;
                    imageHeight = imageData.height;
                }

                imageDataContainers.$editableImageArea.width(imageDataContainers.$editableImageArea.width());  // removes float values
                imageDataContainers.$editableImageArea.height(imageDataContainers.$editableImageArea.height());// removes float values

                var maxShadowHeight = imageHeight + cropAngles.getDoubleBorderSize();
                var maxShadowWidth = imageWidth + cropAngles.getDoubleBorderSize();

                if (imageDataContainers.$shadow.height() < maxShadowHeight) {
                    imageDataContainers.$shadow.height(maxShadowHeight);
                }

                if (imageDataContainers.$shadow.width() < maxShadowWidth) {
                    imageDataContainers.$shadow.width(maxShadowWidth);
                }

                cropElements.$image.css({
                    left: cropAngles.getBorderSize(),
                    top: cropAngles.getBorderSize()
                });

                cropElements.$cropImg.attr("src", imcms.contextPath + "/" + imcms.imagesPath + imageData.path);

                // todo: receive correct crop area
                cropElements.$cropArea.css({
                    width: cropElements.$image.width(),
                    height: cropElements.$image.height(),
                    left: cropAngles.getBorderSize(),
                    top: cropAngles.getBorderSize()
                });

                imageCropper.initImageCropper({
                    imageData: imageData,
                    $imageEditor: imageWindowBuilder.$editor
                });

                imageRotate.rotateImage(imageData.rotateDirection);

                cropElements.$image.css("display", "block");

            }, 200);
        }

        function fillData(image) {

            if (!image) {
                return;
            }

            toggleImgArea.call(imageDataContainers.$tabOriginal);

            // direct reassign because $.extend skip 'undefined' but it's needed!
            imageData.cropRegion = image.cropRegion;
            // imageData.align = image.align;
            imageData.rotateDirection = image.rotateDirection;
            imageData.rotateAngle = image.rotateAngle;

            $.extend(imageData, image);

            if (imageData.inText) {
                $tag.attr("data-index", imageData.index);
            }

            imageEditorFactory.updateImageData($tag, imageData);

            fillBodyHeadData(imageData);
            fillLeftSideData(imageData);

            imageDataContainers.$altText.$input.val(imageData.alternateText);
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
            cropElements.$image.removeAttr("src");
            cropElements.$cropImg.removeAttr("src");
            imageRotate.destroy();
        }

        var imageWindowBuilder = new WindowBuilder({
            factory: buildEditor,
            loadDataStrategy: loadData,
            clearDataStrategy: clearData,
            onEscKeyPressed: "close",
            onEnterKeyPressed: function () {
                imageWindowBuilder.$editor.find(".imcms-image_editor__footer .imcms-button--save").click();
            }
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
