/**
 * Created by Serhii Maksymchuk from Ubrainians for imCode
 * 21.08.17
 */
Imcms.define("imcms-image-editor-builder",
    [
        "imcms-bem-builder", "imcms-components-builder", "imcms-window-builder", "imcms-content-manager-builder",
        "imcms-images-rest-api", "imcms-image-cropper", "jquery"
    ],
    function (BEM, components, WindowBuilder, contentManager, imageRestApi, imageCropper, $) {
        var $rightSidePanel, $bottomPanel, $editableImageArea, $previewImageArea;

        var imageDataContainers = {},
            imageData = {};

        function buildBodyHead() {
            function showHidePanel(panelOpts) {
                var panelAnimationOpts = {};

                if (panelOpts.$btn.data("state")) {
                    panelAnimationOpts[panelOpts.panelSide] = "-" + panelOpts.newPanelSideValue + "px";
                    panelOpts.$panel.animate(panelAnimationOpts, 300);
                    panelOpts.$btn.data("state", false);
                    panelOpts.$btn.text("show bottom panel");

                } else {
                    panelAnimationOpts[panelOpts.panelSide] = 0;
                    panelOpts.$panel.animate(panelAnimationOpts, 300);
                    panelOpts.$btn.data("state", true);
                    panelOpts.$btn.text("hide bottom panel");
                }
            }

            function showHideRightPanel() {
                showHidePanel({
                    $btn: $(this),
                    newPanelSideValue: $rightSidePanel.width(),
                    $panel: $rightSidePanel,
                    panelSide: "right",
                    textHide: "hide right panel",
                    textShow: "show right panel"
                });
            }

            function showHideBottomPanel() {
                showHidePanel({
                    $btn: $(this),
                    newPanelSideValue: $bottomPanel.height(),
                    $panel: $bottomPanel,
                    panelSide: "bottom",
                    textHide: "hide bottom panel",
                    textShow: "show bottom panel"
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
                text: "Show bottom panel",
                click: showHideBottomPanel
            });

            imageDataContainers.$imageTitle = bodyHeadBEM.buildElement("img-title", "<div>");

            var $showHideRightPanelBtn = components.buttons.neutralButton({
                "class": "imcms-image-characteristic",
                text: "Show right panel",
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
            previewContainerProp.ml = -previewContainerProp.width/2;
            previewContainerProp.mt = -previewContainerProp.height/2;

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

        function toggleImgArea() {
            var $previewImageArea = $(".imcms-preview-img-area"),
                $controlTabs = $(".imcms-editable-img-control-tabs__tab")
            ;

            if ($(this).data("tab") === "prev") {
                initPreviewImageArea();
                $previewImageArea.css({
                    "z-index": "50",
                    "display": "block"
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

        function buildLeftSide() {

            function buildPreviewImageArea() {
                var previewImageAreaBEM = new BEM({
                    block: "imcms-preview-img-area",
                    elements: {
                        "container": "imcms-preview-img-container",
                        "img": "imcms-preview-img"
                    }
                });

                var $previewImgContainer = previewImageAreaBEM.buildElement("container", "<div>");
                var $previewImg = previewImageAreaBEM.buildElement("img", "<img>");
                $previewImg.appendTo($previewImgContainer);

                return previewImageAreaBEM.buildBlock("<div>", [
                    {"container": $previewImgContainer}
                ]);
            }

            function buildEditableImageArea() {
                var editableImgAreaBEM = new BEM({
                    block: "imcms-editable-img-area",
                    elements: {
                        "img": "imcms-editable-img",
                        "layout": "",
                        "crop-area": "imcms-crop-area",
                        "angle": "imcms-angle"
                    }
                });

                imageDataContainers.$image = editableImgAreaBEM.buildElement("img", "<img>");
                imageDataContainers.$shadow = editableImgAreaBEM.buildElement("layout", "<div>");
                imageDataContainers.$cropArea = editableImgAreaBEM.buildElement("crop-area", "<div>")
                    .append(imageDataContainers.$cropImg = $("<img>", {"class": "imcms-crop-area__crop-img"}));

                var angleAttributes = {
                    style: "display: none;"
                };
                imageDataContainers.angles = {
                    $topLeft: editableImgAreaBEM.buildElement("angle", "<div>", angleAttributes, ["top-left"]),
                    $topRight: editableImgAreaBEM.buildElement("angle", "<div>", angleAttributes, ["top-right"]),
                    $bottomLeft: editableImgAreaBEM.buildElement("angle", "<div>", angleAttributes, ["bottom-left"]),
                    $bottomRight: editableImgAreaBEM.buildElement("angle", "<div>", angleAttributes, ["bottom-right"])
                };

                return editableImgAreaBEM.buildBlock("<div>", [
                    {"img": imageDataContainers.$image},
                    {"layout": imageDataContainers.$shadow},
                    {"crop-area": imageDataContainers.$cropArea},
                    {"angle": imageDataContainers.angles.$topLeft},
                    {"angle": imageDataContainers.angles.$topRight},
                    {"angle": imageDataContainers.angles.$bottomRight},
                    {"angle": imageDataContainers.angles.$bottomLeft}
                ]);
            }

            function buildEditSizeControls() {
                var $title = components.texts.titleText("<div>", "Display size");

                var $heightControlInput = components.texts.textNumber("<div>", {
                    name: "height",
                    placeholder: "Height",
                    text: "H",
                    error: "Error text"
                });

                var $proportionsBtn = components.buttons.proportionsButton({
                    "data-state": "active",
                    click: function () {
                        console.log("%c Not implemented: Lock/unlock image proportions!", "color: red");
                    }
                });

                var $widthControlInput = components.texts.textNumber("<div>", {
                    name: "width",
                    placeholder: "Width",
                    text: "W",
                    error: "Error text"
                });

                return new BEM({
                    block: "imcms-edit-size",
                    elements: [
                        {"title": $title},
                        {"number": $heightControlInput},
                        {"button": $proportionsBtn},
                        {"number": $widthControlInput}
                    ]
                }).buildBlockStructure("<div>");
            }

            function resizeImage(newWidth, newHeight) {
                imageDataContainers.$image.add(imageDataContainers.$cropImg)
                    .add(imageDataContainers.$cropArea)
                    .animate({
                        "width": newWidth,
                        "height": newHeight
                    }, 200);

                var angleHeight = imageDataContainers.angles.$bottomLeft.height();
                var angleWidth = imageDataContainers.angles.$bottomLeft.width();
                var angleBorderSize = parseInt(imageDataContainers.angles.$topLeft.css("border-width")) || 0;

                imageDataContainers.$cropArea.add(imageDataContainers.$image)
                    .animate({
                        "top": angleBorderSize,
                        "left": angleBorderSize
                    }, 200);
                imageDataContainers.angles.$topLeft.animate({
                    "top": 0,
                    "left": 0
                }, 200);
                imageDataContainers.angles.$bottomLeft.animate({
                    "top": newHeight - angleHeight + angleBorderSize,
                    "left": 0
                }, 200);
                imageDataContainers.angles.$topRight.animate({
                    "top": 0,
                    "left": newWidth - angleWidth + angleBorderSize
                }, 200);
                imageDataContainers.angles.$bottomRight.animate({
                    "top": newHeight - angleHeight + angleBorderSize,
                    "left": newWidth - angleWidth + angleBorderSize
                }, 200);
            }

            function zoom(zoomCoefficient) {
                var newHeight = ~~(imageDataContainers.$image.height() * zoomCoefficient),
                    newWidth = ~~(imageDataContainers.$image.width() * zoomCoefficient)
                ;
                resizeImage(newWidth, newHeight);
            }

            function zoomPlus() {
                zoom(1.1);
            }

            function zoomMinus() {
                zoom(0.9);
            }

            function zoomContain() {
                // fixme: save proportions! now image becomes just as editable area
                // only one side should be as area's side and one as needed to save proportions
                var newHeight = $editableImageArea.height(),
                    newWidth = $editableImageArea.width()
                ;
                var twiceAngleBorderSize = parseInt(imageDataContainers.angles.$topLeft.css("border-width")) * 2 || 0;
                resizeImage(newWidth - twiceAngleBorderSize, newHeight - twiceAngleBorderSize);
            }

            var angle = 0;

            function rotate(angleDelta) {
                angle += angleDelta;
                imageDataContainers.$image.css({"transform": "rotate(" + angle + "deg)"});
                imageDataContainers.$cropImg.css({"transform": "rotate(" + angle + "deg)"});
            }

            function rotateLeft() {
                rotate(-90);
            }

            function rotateRight() {
                rotate(90);
            }

            function buildScaleAndRotateControls() {
                return new BEM({
                    block: "imcms-edit-image",
                    elements: {
                        "button": [
                            components.buttons.zoomPlusButton({click: zoomPlus}),
                            components.buttons.zoomMinusButton({click: zoomMinus}),
                            components.buttons.zoomContainButton({click: zoomContain}),
                            components.buttons.rotateLeftButton({click: rotateLeft}),
                            components.buttons.rotateRightButton({click: rotateRight})
                        ]
                    }
                }).buildBlockStructure("<div>");
            }

            function buildSwitchViewControls() {
                var $preview = components.texts.titleText("<div>", "Preview", {
                    "data-tab": "prev",
                    click: toggleImgArea
                });
                var $origin = components.texts.titleText("<div>", "Original", {
                    "data-tab": "origin",
                    click: toggleImgArea
                });
                $origin.modifiers = ["active"];

                imageDataContainers.$tabOriginal = $origin;

                return new BEM({
                    block: "imcms-editable-img-control-tabs",
                    elements: {
                        "tab": [$preview, $origin]
                    }
                }).buildBlockStructure("<div>");
            }

            function buildBottomPanel() {
                return new BEM({
                    block: "imcms-editable-img-controls",
                    elements: {
                        "control-size": buildEditSizeControls(),
                        "control-scale-n-rotate": buildScaleAndRotateControls(),
                        "control-view": buildSwitchViewControls()
                    }
                }).buildBlockStructure("<div>");
            }

            $editableImageArea = buildEditableImageArea();
            $previewImageArea = buildPreviewImageArea();
            $bottomPanel = buildBottomPanel();

            return $("<div>").append($editableImageArea, $previewImageArea, $bottomPanel);
        }

        function reloadImageOnPage(imageDTO) {
            $tag.find(".imcms-editor-content>img").attr("src", Imcms.contextPath + imageDTO.generatedFilePath);
        }

        function buildRightSide(imageEditorBlockClass) {

            function buildSelectImageBtnContainer() {
                var $selectImageBtn = components.buttons.neutralButton({
                    text: "Select Image",
                    click: contentManager.build.bind(contentManager, fillData)
                });
                return components.buttons.buttonsContainer("<div>", [$selectImageBtn]);
            }

            function buildAltTextBox() {
                return components.texts.textBox("<div>", {
                    text: "Alt text",
                    name: "altText"
                });
            }

            function buildImageLinkTextBox() {
                return components.texts.textBox("<div>", {
                    text: "Image link",
                    name: "imageLink"
                });
            }

            function buildImageLangFlags() {
                imageData.langCode = "en"; // by default

                return components.flags.flagsContainer(function (language) {
                    return ["<div>", {
                        click: function () {
                            imageData.langCode = language.code;
                        }
                    }];
                });
            }

            function buildAllLanguagesCheckbox() {
                return components.checkboxes.checkboxContainer("<div>", [
                    components.checkboxes.imcmsCheckbox("<div>", {
                        name: "allLanguages",
                        text: "All languages"
                    })
                ]);
            }

            function buildAdvancedModeBtn($advancedControls) {
                return components.buttons.buttonsContainer("<div>", [
                    components.buttons.negativeButton({
                        text: "Advanced",
                        "data-state": "false",
                        click: function () {
                            var $btn = $(this);

                            if ($btn.attr("data-state") === "false") {
                                $advancedControls.css("display", "block");
                                $btn.attr("data-state", "true").text("Simple");

                            } else {
                                $advancedControls.css("display", "none");
                                $btn.attr("data-state", "false").text("Advanced");
                            }
                        }
                    })
                ]);
            }

            function buildTextAlignmentBtnsContainer() {
                function buildAlignButton(modifiers, onClick) {
                    return components.buttons.imcmsButton({click: onClick}, ["align"].concat(modifiers));
                }

                // todo: implement onClick!
                var $alignNoneBtn = buildAlignButton(["align-none", "align-active"]).text("None");
                var $alignTopBtn = buildAlignButton(["align-top"]);
                var $alignCenterBtn = buildAlignButton(["align-center"]);
                var $alignBottomBtn = buildAlignButton(["align-bottom"]);
                var $alignLeftBtn = buildAlignButton(["align-left"]);
                var $alignRightBtn = buildAlignButton(["align-right"]);

                return components.buttons.buttonsContainer("<div>", [
                    $alignNoneBtn,
                    $alignTopBtn,
                    $alignCenterBtn,
                    $alignBottomBtn,
                    $alignLeftBtn,
                    $alignRightBtn
                ]);
            }

            function buildSpaceAroundImageInputContainer() {
                return components.texts.pluralInput("<div>", [
                    {
                        id: "image-space-top",
                        name: "top",
                        placeholder: "top"
                    }, {
                        id: "image-space-right",
                        name: "right",
                        placeholder: "right"
                    }, {
                        id: "image-space-bottom",
                        name: "bottom",
                        placeholder: "bottom"
                    }, {
                        id: "image-space-left",
                        name: "left",
                        placeholder: "left"
                    }
                ], {text: "Space around image (h-vspace)"});
            }

            function buildCropCoordinatesText(advancedModeBEM) {
                return advancedModeBEM.buildElement("title", "<div>")
                    .append("Crop Coordinates (W:")
                    .append(advancedModeBEM.buildBlockElement("current-crop-width", "<span>", {text: "400"}))
                    .append(" H:")
                    .append(advancedModeBEM.buildBlockElement("current-crop-width", "<span>", {text: "100"}))
                    .append(")");
            }

            function buildCropCoordinatesContainer() {
                var $xCropCoord = components.texts.textNumber("<div>", {
                    name: "cropX0",
                    placeholder: "X",
                    text: "X",
                    error: "Error text"
                });

                var $yCropCoord = components.texts.textNumber("<div>", {
                    name: "cropY0",
                    placeholder: "Y",
                    text: "Y",
                    error: "Error text"
                });

                var $x1CropCoord = components.texts.textNumber("<div>", {
                    name: "cropX1",
                    placeholder: "X1",
                    text: "X1",
                    error: "Error text"
                });

                var $y1CropCoord = components.texts.textNumber("<div>", {
                    name: "cropY1",
                    placeholder: "Y1",
                    text: "Y1",
                    error: "Error text"
                });

                return new BEM({
                    block: "imcms-crop-coordinates",
                    elements: {
                        "x": $xCropCoord,
                        "y": $yCropCoord,
                        "x1": $x1CropCoord,
                        "y1": $y1CropCoord
                    }
                }).buildBlockStructure("<div>");
            }

            function buildFileFormatSelect() {
                return components.selects.imcmsSelect("<div>", {
                    text: "File format",
                    name: "fileFormat"
                }, [{
                    text: "GIF",
                    "data-value": 0
                }, {
                    text: "PNG",
                    "data-value": 1
                }, {
                    text: "PNG-24",
                    "data-value": 2
                }, {
                    text: "JPG",
                    "data-value": 3
                }]);
            }

            function showExif() {
                // todo: implement!!!
            }

            function buildAdvancedControls() {
                var advancedModeBEM = new BEM({
                    block: "imcms-advanced-mode",
                    elements: {
                        "title": "imcms-title",
                        "buttons": "imcms-buttons",
                        "space-around": "imcms-space-around",
                        "current-crop-width": "imcms-title",
                        "crop-coordinates": "imcms-crop-coordinates",
                        "file-format": "imcms-select",
                        "button": "imcms-button"
                    }
                });

                var $textAlignmentBtnsTitle = advancedModeBEM.buildElement("title", "<div>", {text: "Text alignment"});
                var $textAlignmentBtnsContainer = buildTextAlignmentBtnsContainer();
                var $spaceAroundImageInputContainer = buildSpaceAroundImageInputContainer();
                var $cropCoordinatesText = buildCropCoordinatesText(advancedModeBEM);
                var $cropCoordinatesContainer = buildCropCoordinatesContainer();
                var $fileFormat = buildFileFormatSelect();
                var $showExifBtn = components.buttons.neutralButton({
                    text: "Show exif",
                    click: showExif
                });

                return advancedModeBEM.buildBlock("<div>", [
                    {"title": $textAlignmentBtnsTitle},
                    {"buttons": $textAlignmentBtnsContainer},
                    {"space-around": $spaceAroundImageInputContainer},
                    {"title": $cropCoordinatesText},
                    {"crop-coordinates": $cropCoordinatesContainer},
                    {"file-format": $fileFormat},
                    {"button": $showExifBtn}
                ]);
            }

            function buildEditableControls() {
                var editableControlsBEM = new BEM({
                    block: "imcms-editable-controls-area",
                    elements: {
                        "buttons": "imcms-buttons",
                        "text-box": "imcms-text-box",
                        "flags": "imcms-flags",
                        "checkboxes": "imcms-checkboxes",
                        "advanced-mode": "imcms-advanced-mode"
                    }
                });

                var $selectImageBtnContainer = buildSelectImageBtnContainer();
                var $altTextBox = buildAltTextBox();
                var $imageLinkTextBox = buildImageLinkTextBox();
                var $langFlags = buildImageLangFlags();
                var $allLangs = buildAllLanguagesCheckbox();
                var $advancedControls = buildAdvancedControls();
                var $advancedModeBtn = buildAdvancedModeBtn($advancedControls);

                return editableControlsBEM.buildBlock("<div>", [
                    {"buttons": $selectImageBtnContainer},
                    {"text-box": $altTextBox},
                    {"text-box": $imageLinkTextBox},
                    {"flags": $langFlags},
                    {"checkboxes": $allLangs},
                    {"buttons": $advancedModeBtn},
                    {"advanced-mode": $advancedControls}
                ]);
            }

            function removeAndClose() {
                // fixme: just closing for now, should be remove and close
                imageWindowBuilder.closeWindow();
            }

            function onImageSaved() {
                var imageRequestData = {
                    docId: imageData.docId,
                    index: imageData.index,
                    langCode: imageData.langCode
                };

                if (imageData.loopEntryRef) {
                    imageRequestData["loopEntryRef.loopEntryIndex"] = imageData["loopEntryRef.loopEntryIndex"];
                    imageRequestData["loopEntryRef.loopIndex"] = imageData["loopEntryRef.loopIndex"];
                }

                imageRestApi.read(imageRequestData)
                    .success(reloadImageOnPage)
                    .error(console.error.bind(console));
            }

            function saveAndClose() {
                imageWindowBuilder.closeWindow();

                imageRestApi.create(imageData)
                    .success(onImageSaved)
                    .error(console.error.bind(console));
            }

            function buildFooter() {
                var $removeAndCloseButton = components.buttons.negativeButton({
                    text: "remove and close",
                    click: removeAndClose
                });

                var $saveAndCloseButton = components.buttons.saveButton({
                    text: "save and close",
                    click: saveAndClose
                });

                return $("<div>").append($removeAndCloseButton, $saveAndCloseButton);
            }


            var $editableControls = buildEditableControls();
            var $footer = buildFooter().addClass(imageEditorBlockClass + BEM.getBlockSeparator() + "footer");

            return $("<div>").append($editableControls, $footer);
        }

        function buildEditor() {
            var imageEditorBlockClass = "imcms-image_editor";

            return new BEM({
                block: imageEditorBlockClass,
                elements: {
                    "head": imageWindowBuilder.buildHead("Image Editor"),
                    "image-characteristics": buildBodyHead(),
                    "left-side": buildLeftSide(),
                    "right-side": $rightSidePanel = buildRightSide(imageEditorBlockClass)
                }
            }).buildBlockStructure("<div>", {"class": "imcms-editor-window"});
        }

        function fillBodyHeadData(imageData) {
            imageDataContainers.$imageTitle.text(imageData.name + "." + imageData.format);
            imageDataContainers.$imgUrl.text(imageData.path);
            imageDataContainers.$heightValue.text(imageData.height);
            imageDataContainers.$widthValue.text(imageData.width);
        }

        function fillLeftSideData(imageData) {

            imageDataContainers.$shadow.css({
                width: "100%",
                height: "100%"
            });

            if (!imageData.path) {
                return;
            }

            imageDataContainers.$image.attr("src", Imcms.contextPath + "/" + Imcms.imagesPath + imageData.path);

            setTimeout(function () { // to let image src load
                imageDataContainers.$image.removeAttr("style");
                // fixes to prevent stupid little scroll because of borders
                var angleBorderSize = parseInt(imageDataContainers.angles.$topLeft.css("border-width")) || 0;
                var imageWidth = imageDataContainers.$image.width();
                var imageHeight = imageDataContainers.$image.height();

                if (imageDataContainers.$shadow.height() < imageHeight) {
                    imageDataContainers.$shadow.height(imageHeight);
                }

                imageDataContainers.$image.width(imageWidth - angleBorderSize * 2);
                imageDataContainers.$image.height(imageHeight - angleBorderSize * 2);
                imageDataContainers.$image.css({
                    left: angleBorderSize,
                    top: angleBorderSize
                });

                imageDataContainers.$cropImg.attr("src", Imcms.contextPath + "/" + Imcms.imagesPath + imageData.path);

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

        function fillData(image) {
            if (!image) {
                return;
            }

            toggleImgArea.call(imageDataContainers.$tabOriginal);

            // direct reassign because $.extend skip 'undefined' but it's needed!
            imageData.cropRegion = image.cropRegion;
            $.extend(imageData, image);
            fillBodyHeadData(imageData);
            fillLeftSideData(imageData);
        }

        function loadData(opts) {
            if (opts.loopEntryIndex && opts.loopIndex) {

                // note that this data have to be set in such way because of non-JSON GET AJAX call
                opts["loopEntryRef.loopEntryIndex"] = opts.loopEntryIndex;
                opts["loopEntryRef.loopIndex"] = opts.loopIndex;

                delete opts.loopEntryIndex;
                delete opts.loopIndex;
            }

            $.extend(imageData, opts);
            imageRestApi.read(opts).done(fillData);
        }

        function clearData() {
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
                imageWindowBuilder.buildWindow.applyAsync(arguments, imageWindowBuilder);
            }
        };
    }
);
