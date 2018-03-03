/**
 * Created by Serhii Maksymchuk from Ubrainians for imCode
 * 21.08.17
 */
Imcms.define("imcms-image-editor-builder",
    [
        "imcms-bem-builder", "imcms-components-builder", "imcms-window-builder", "imcms-images-rest-api",
        "imcms-image-cropper", "jquery", "imcms-events", "imcms", "imcms-i18n-texts", "imcms-image-editor-components"
    ],
    function (BEM, components, WindowBuilder, imageRestApi, imageCropper, $, events, imcms, texts,
              imageEditorComponents) {

        texts = texts.editors.image;

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

                    // set properties of preview image container to zero
                    imageDataContainers.$previewImgContainer.css({
                        "margin-left": 0,
                        "margin-top": 0,
                        "left": 0,
                        "top": 0
                    });
                }
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
            var $rightSidePanel;

            function buildBodyHead() {
                function showHidePanel(panelOpts) {
                    var panelAnimationOpts = {};

                    if (panelOpts.$btn.data("state")) {
                        panelAnimationOpts[panelOpts.panelSide] = "-" + panelOpts.newPanelSideValue + "px";
                        panelOpts.$panel.animate(panelAnimationOpts, 300);
                        panelOpts.$btn.data("state", false);
                        panelOpts.$btn.text(texts.panels.bottom.show);

                    } else {
                        panelAnimationOpts[panelOpts.panelSide] = 0;
                        panelOpts.$panel.animate(panelAnimationOpts, 300);
                        panelOpts.$btn.data("state", true);
                        panelOpts.$btn.text(texts.panels.bottom.hide);
                    }
                }

                function showHideRightPanel() {
                    showHidePanel({
                        $btn: $(this),
                        newPanelSideValue: $rightSidePanel.width(),
                        $panel: $rightSidePanel,
                        panelSide: "right",
                        textHide: texts.panels.right.hide,
                        textShow: texts.panels.right.show
                    });
                }

                function showHideBottomPanel() {
                    showHidePanel({
                        $btn: $(this),
                        newPanelSideValue: imageDataContainers.$bottomPanel.height(),
                        $panel: imageDataContainers.$bottomPanel,
                        panelSide: "bottom",
                        textHide: texts.panels.bottom.hide,
                        textShow: texts.panels.bottom.show
                    });
                }

                function buildHeightWidthBlock() {
                    var $heightBlock = new BEM({
                        block: "imcms-img-origin-size",
                        elements: {
                            "height-title": components.texts.titleText("<span>", "H:"),
                            "height-value": imageDataContainers.$heightValue = components.texts.titleText("<span>")
                        }
                    }).buildBlockStructure("<div>");

                    var $widthBlock = new BEM({
                        block: "imcms-img-origin-size",
                        elements: {
                            "width-title": components.texts.titleText("<span>", "W:"),
                            "width-value": imageDataContainers.$widthValue = components.texts.titleText("<span>")
                        }
                    }).buildBlockStructure("<div>");

                    return new BEM({
                        block: "imcms-title imcms-image-characteristic",
                        elements: {
                            "origin-size": [$heightBlock, $widthBlock]
                        }
                    }).buildBlockStructure("<div>", {text: "Orig "});
                }

                var bodyHeadBEM = new BEM({
                    block: "imcms-image-characteristics",
                    elements: {
                        "button": "imcms-image-characteristic",
                        "img-title": "imcms-title imcms-image-characteristic",
                        "img-url": "imcms-title imcms-image-characteristic",
                        "img-origin-size": "imcms-title imcms-image-characteristic"
                    }
                });

                var $showHideBottomPanelBtn = components.buttons.neutralButton({
                    "class": "imcms-image-characteristic",
                    text: texts.panels.bottom.show,
                    click: showHideBottomPanel
                });

                imageDataContainers.$imageTitle = bodyHeadBEM.buildElement("img-title", "<div>");

                var $showHideRightPanelBtn = components.buttons.neutralButton({
                    "class": "imcms-image-characteristic",
                    text: texts.panels.right.show,
                    click: showHideRightPanel
                });

                var $imgUrl = bodyHeadBEM.buildElement("img-url", "<div>", {
                    text: "Url: "
                }).append(imageDataContainers.$imgUrl = $("<span>"));

                var $heightWidthBlock = buildHeightWidthBlock();

                return bodyHeadBEM.buildBlock("<div>", [
                    {
                        "button": $showHideBottomPanelBtn,
                        modifiers: ["bottom-panel"]
                    }, {
                        "img-title": imageDataContainers.$imageTitle
                    }, {
                        "button": $showHideRightPanelBtn,
                        modifiers: ["right-panel"]
                    }, {
                        "img-url": $imgUrl
                    }, {
                        "img-origin-size": $heightWidthBlock
                    }
                ]);
            }

            var imageEditorBlockClass = "imcms-image_editor";

            return new BEM({
                block: imageEditorBlockClass,
                elements: {
                    "head": imageWindowBuilder.buildHead(texts.title),
                    "image-characteristics": buildBodyHead(),
                    "left-side": imageEditorComponents.buildLeftSide({
                        toggleImgArea: toggleImgArea,
                        imageDataContainers: imageDataContainers
                    }),
                    "right-side": $rightSidePanel = imageEditorComponents.buildRightSide({
                        imageEditorBlockClass: imageEditorBlockClass,
                        fillData: fillData,
                        imageDataContainers: imageDataContainers,
                        $tag: $tag,
                        imageWindowBuilder: imageWindowBuilder,
                        imageData: imageData
                    })
                }
            }).buildBlockStructure("<div>", {"class": "imcms-editor-window"});
        }

        function fillData(image) {

            function fillBodyHeadData(imageData) {
                imageDataContainers.$imageTitle.text(imageData.name + "." + imageData.format);
                imageDataContainers.$imgUrl.text(imageData.path);
                imageDataContainers.$heightValue.text(imageData.height);
                imageDataContainers.$widthValue.text(imageData.width);
            }

            function fillLeftSideData(imageData) {

                imageDataContainers.$widthControlInput.find("input").val(imageData.width);
                imageDataContainers.$heightControlInput.find("input").val(imageData.height);

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
                        angle.css({
                            "display": "none"
                        })
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

                    if (imageDataContainers.$shadow.height() < imageHeight) {
                        imageDataContainers.$shadow.height(imageHeight);
                    }

                    if (imageDataContainers.$shadow.width() < imageWidth) {
                        imageDataContainers.$shadow.width(imageWidth);
                    }

                    imageDataContainers.$image.width(imageWidth - angleBorderSize * 2);
                    imageDataContainers.$image.height(imageHeight - angleBorderSize * 2);
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
