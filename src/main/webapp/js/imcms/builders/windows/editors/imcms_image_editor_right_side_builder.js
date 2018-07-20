Imcms.define(
    "imcms-image-editor-right-side-builder",
    [
        "imcms-components-builder", "imcms-i18n-texts", "imcms-content-manager-builder", "imcms", "jquery",
        "imcms-images-rest-api", "imcms-bem-builder", "imcms-modal-window-builder", "imcms-events",
        "imcms-image-cropping-elements", "imcms-image-cropper", "imcms-window-builder", "imcms-image-rotate",
        "imcms-image-editor-body-head-builder"
    ],
    function (components, texts, contentManager, imcms, $, imageRestApi, BEM, modalWindowBuilder, events, cropElements,
              imageCropper, WindowBuilder, imageRotate, imageEditorBodyHeadBuilder) {

        texts = texts.editors.image;
        var $tag, imageData, $fileFormat, $textAlignmentBtnsContainer;
        var imgPosition = {
            align: "NONE",
            spaceAround: {
                top: 0,
                right: 0,
                bottom: 0,
                left: 0
            }
        };

        events.on("image data", function () {
            console.log(imageData);
        });

        var $exifInfoContainer;

        function buildExifInfoWindow() {
            return new BEM({
                block: "imcms-pop-up-modal",
                elements: {
                    "head": exifInfoWindowBuilder.buildHead("EXIF"),
                    "body": $exifInfoContainer = $("<div>")
                }
            }).buildBlockStructure("<div>", {"class": "image-exif-window"});
        }

        function loadExifData() {
            /** @namespace imageData.exifInfo */
            (imageData.exifInfo || []).forEach(function (exifDataRow) {
                $exifInfoContainer.append($("<div>", {"class": "image-exif-window__row"}).text(exifDataRow));
            });
        }

        function clearExifData() {
            $exifInfoContainer.empty();
        }

        var exifInfoWindowBuilder = new WindowBuilder({
            factory: buildExifInfoWindow,
            loadDataStrategy: loadExifData,
            clearDataStrategy: clearExifData,
            onEscKeyPressed: "close"
        });

        var alignButtonSelectorToAlignName = {
            NONE: BEM.buildClassSelector(null, "imcms-button", "align-none"),
            CENTER: BEM.buildClassSelector(null, "imcms-button", "align-center"),
            LEFT: BEM.buildClassSelector(null, "imcms-button", "align-left"),
            RIGHT: BEM.buildClassSelector(null, "imcms-button", "align-right")
        };

        return {
            updateImageData: function ($newTag, newImageData) {
                $tag = $newTag;
                imageData = newImageData;

                var spaceAround = imageData.spaceAround;
                spaceAround.top && $("#image-space-top").val(spaceAround.top).blur();
                spaceAround.right && $("#image-space-right").val(spaceAround.right).blur();
                spaceAround.bottom && $("#image-space-bottom").val(spaceAround.bottom).blur();
                spaceAround.left && $("#image-space-left").val(spaceAround.left).blur();

                $fileFormat.selectValue(imageData.format);

                $textAlignmentBtnsContainer.find(alignButtonSelectorToAlignName[imageData.align || 'NONE']).click();
            },
            build: function (opts) {

                var fillData = opts.fillData;
                var imageWindowBuilder = opts.imageWindowBuilder;
                $tag = opts.$tag;
                imageData = opts.imageData;

                function buildSelectImageBtnContainer() {

                    var $selectImageBtn = components.buttons.neutralButton({
                        text: texts.selectImage,
                        click: contentManager.build.bind(contentManager, fillData, function () {
                            return imageEditorBodyHeadBuilder.getImageUrl();
                        })
                    });
                    return components.buttons.buttonsContainer("<div>", [$selectImageBtn]);
                }

                function buildAltTextBox() {
                    return opts.imageDataContainers.$altText = components.texts.textBox("<div>", {
                        text: texts.altText,
                        name: "altText"
                    });
                }

                function buildImageLinkTextBox() {
                    return opts.imageDataContainers.$imgLink = components.texts.textBox("<div>", {
                        text: texts.imageLink,
                        name: "imageLink"
                    });
                }

                function buildImageLangFlags() {
                    imageData.langCode = imcms.language.code;

                    return components.flags.flagsContainer(function (language) {
                        return ["<div>", {
                            click: function () {
                                imageData.langCode = language.code;

                                var imageRequestData = getImageRequestData(imageData.langCode);

                                imageRestApi.read(imageRequestData).done(fillData);
                            }
                        }];
                    });
                }

                function buildAllLanguagesCheckbox() {
                    return components.checkboxes.checkboxContainer("<div>", [
                        opts.imageDataContainers.$allLanguagesCheckBox = components.checkboxes.imcmsCheckbox("<div>", {
                            name: "allLanguages",
                            text: texts.allLangs
                        })
                    ]);
                }

                function buildAdvancedModeBtn($advancedControls) {
                    return components.buttons.buttonsContainer("<div>", [
                        components.buttons.negativeButton({
                            text: texts.advanced,
                            "data-state": "false",
                            click: function () {
                                var $btn = $(this);

                                if ($btn.attr("data-state") === "false") {
                                    $advancedControls.css("display", "block");
                                    $btn.attr("data-state", "true").text(texts.simple);

                                } else {
                                    $advancedControls.css("display", "none");
                                    $btn.attr("data-state", "false").text(texts.advanced);
                                }
                            }
                        })
                    ]);
                }

                function buildTextAlignmentBtnsContainer() {
                    var $alignContainer;
                    var activeAlignClass = "imcms-button--align-active";

                    function setAlign(align, button) {
                        imgPosition.align = align;

                        $alignContainer.find("." + activeAlignClass).removeClass(activeAlignClass);
                        $(button).addClass(activeAlignClass);
                    }

                    function onAlignNoneClick() {
                        setAlign("NONE", this);
                    }

                    function onAlignCenterClick() {
                        setAlign("CENTER", this);
                    }

                    function onAlignLeftClick() {
                        setAlign("LEFT", this);
                    }

                    function onAlignRightClick() {
                        setAlign("RIGHT", this);
                    }

                    function buildAlignButton(modifiers, attributes) {
                        return components.buttons.imcmsButton(attributes, ["align"].concat(modifiers));
                    }

                    var $alignNoneBtn = buildAlignButton(["align-none", "align-active"], {
                        click: onAlignNoneClick,
                        title: texts.align.none,
                        text: texts.none
                    });
                    var $alignCenterBtn = buildAlignButton(["align-center"], {
                        click: onAlignCenterClick,
                        title: texts.align.center
                    });
                    var $alignLeftBtn = buildAlignButton(["align-left"], {
                        click: onAlignLeftClick,
                        title: texts.align.left
                    });
                    var $alignRightBtn = buildAlignButton(["align-right"], {
                        click: onAlignRightClick,
                        title: texts.align.right
                    });

                    return $alignContainer = components.buttons.buttonsContainer("<div>", [
                        $alignNoneBtn,
                        $alignCenterBtn,
                        $alignLeftBtn,
                        $alignRightBtn
                    ]);
                }

                function setSpaceAroundImg(spacePlace, element) {
                    var spaceValue = $(element).val();
                    imgPosition.spaceAround[spacePlace] = parseInt(spaceValue);
                }

                function buildSpaceAroundImageInputContainer() {
                    return components.texts.pluralInput("<div>", [
                        {
                            id: "image-space-top",
                            name: "top",
                            placeholder: texts.top,
                            blur: function () {
                                setSpaceAroundImg("top", this);
                            }
                        }, {
                            id: "image-space-right",
                            name: "right",
                            placeholder: texts.right,
                            blur: function () {
                                setSpaceAroundImg("right", this);
                            }
                        }, {
                            id: "image-space-bottom",
                            name: "bottom",
                            placeholder: texts.bottom,
                            blur: function () {
                                setSpaceAroundImg("bottom", this);
                            }
                        }, {
                            id: "image-space-left",
                            name: "left",
                            placeholder: texts.left,
                            blur: function () {
                                setSpaceAroundImg("left", this);
                            }
                        }
                    ], {text: texts.spaceAround});
                }

                function buildCropCoordinatesText(advancedModeBEM) {
                    return advancedModeBEM.buildElement("title", "<div>")
                        .append(texts.cropCoords);
                }

                function setValidation(onValid) {
                    return function () {
                        var inputField = $(this),
                            stringFieldValue = inputField.val(),
                            intFieldValue = +stringFieldValue,
                            minFieldValue = 0;

                        if (isNaN(intFieldValue)) {
                            var val = parseInt(stringFieldValue);
                            inputField.val(isNaN(val) ? 0 : val);
                            return;
                        }

                        if (intFieldValue < minFieldValue) {
                            inputField.val(minFieldValue);
                            return;
                        }

                        onValid.call(this, intFieldValue);
                    }
                }

                function buildCropCoordinatesContainer() {
                    var $xCropCoord = components.texts.textNumber("<div>", {
                        name: "cropX0",
                        placeholder: "X",
                        text: "X",
                        error: "Error",
                        onValidChange: setValidation(imageCropper.setCropX)
                    });

                    var $yCropCoord = components.texts.textNumber("<div>", {
                        name: "cropY0",
                        placeholder: "Y",
                        text: "Y",
                        error: "Error",
                        onValidChange: setValidation(imageCropper.setCropY)
                    });

                    var $x1CropCoord = components.texts.textNumber("<div>", {
                        name: "cropX1",
                        placeholder: "X1",
                        text: "X1",
                        error: "Error",
                        onValidChange: setValidation(imageCropper.setCropX1)
                    });

                    var $y1CropCoord = components.texts.textNumber("<div>", {
                        name: "cropY1",
                        placeholder: "Y1",
                        text: "Y1",
                        error: "Error",
                        onValidChange: setValidation(imageCropper.setCropY1)
                    });

                    events.on("clean crop coordinates", function () {
                        $xCropCoord.getInput().val(0);
                        $yCropCoord.getInput().val(0);
                        $x1CropCoord.getInput().val(0);
                        $y1CropCoord.getInput().val(0);

                        imageData.cropRegion = {
                            cropX1: 0,
                            cropX2: 0,
                            cropY1: 0,
                            cropY2: 0
                        }
                    });

                    events.on("crop area position changed", function () {
                        var x = cropElements.$cropArea.getLeft() - 2;
                        var y = cropElements.$cropArea.getTop() - 2;
                        var x1 = cropElements.$cropArea.width() + x;
                        var y1 = cropElements.$cropArea.height() + y;

                        $xCropCoord.getInput().val(x);
                        $yCropCoord.getInput().val(y);
                        $x1CropCoord.getInput().val(x1);
                        $y1CropCoord.getInput().val(y1);

                        imageData.cropRegion = {
                            cropX1: x,
                            cropX2: x1,
                            cropY1: y,
                            cropY2: y1
                        }
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
                        text: texts.fileFormat,
                        name: "fileFormat"
                    }, [{
                        text: "JPG",
                        "data-value": "JPEG"
                    }, {
                        text: "PNG",
                        "data-value": "PNG"
                    }, {
                        text: "BMP",
                        "data-value": "BMP"
                    }, {
                        text: "GIF",
                        "data-value": "GIF"
                    }, {
                        text: "PSD",
                        "data-value": "PSD"
                    }, {
                        text: "SVG",
                        "data-value": "SVG"
                    }, {
                        text: "TIFF",
                        "data-value": "TIFF"
                    }, {
                        text: "XCF",
                        "data-value": "XCF"
                    }, {
                        text: "PICT",
                        "data-value": "PICT"
                    }]);
                }

                function showExif() {
                    exifInfoWindowBuilder.buildWindow();
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

                    var $textAlignmentBtnsTitle = advancedModeBEM.buildElement("title", "<div>", {text: texts.alignment});
                    $textAlignmentBtnsContainer = buildTextAlignmentBtnsContainer();
                    var $spaceAroundImageInputContainer = buildSpaceAroundImageInputContainer();
                    var $cropCoordinatesText = buildCropCoordinatesText(advancedModeBEM);
                    var $cropCoordinatesContainer = buildCropCoordinatesContainer();
                    $fileFormat = buildFileFormatSelect();
                    var $showExifBtn = components.buttons.neutralButton({
                        text: texts.exif.button,
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
                    opts.imageDataContainers.$langFlags = buildImageLangFlags();
                    var $allLangs = buildAllLanguagesCheckbox();
                    var $advancedControls = buildAdvancedControls();
                    var $advancedModeBtn = buildAdvancedModeBtn($advancedControls);

                    return editableControlsBEM.buildBlock("<div>", [
                        {"buttons": $selectImageBtnContainer},
                        {"text-box": $altTextBox},
                        {"text-box": $imageLinkTextBox},
                        {"flags": opts.imageDataContainers.$langFlags},
                        {"checkboxes": $allLangs},
                        {"buttons": $advancedModeBtn},
                        {"advanced-mode": $advancedControls}
                    ]);
                }

                function removeAndClose() {
                    imageWindowBuilder.closeWindow();

                    imageData.allLanguages = opts.imageDataContainers.$allLanguagesCheckBox.isChecked();

                    imageRestApi.remove(imageData)
                        .success(onImageSaved)
                        .error(console.error.bind(console));
                }

                function getImageRequestData(langCode) {
                    var imageRequestData = {
                        docId: imageData.docId,
                        index: imageData.index,
                        langCode: langCode
                    };

                    /** @namespace imageData.loopEntryRef */
                    if (imageData.loopEntryRef) {
                        imageRequestData["loopEntryRef.loopEntryIndex"] = imageData["loopEntryRef.loopEntryIndex"];
                        imageRequestData["loopEntryRef.loopIndex"] = imageData["loopEntryRef.loopIndex"];
                    }

                    return imageRequestData;
                }

                function setOrRemoveAltAttribute($image, imageDTO) {
                    if (imageDTO.alternateText) {
                        $image.attr("alt", imageDTO.alternateText);
                    } else {
                        $image.removeAttr("alt");
                    }
                }

                function setOrRemoveHrefAttribute($image, imageDTO) {
                    var linkUrl = imageDTO.linkUrl;
                    if (linkUrl) {
                        if (!linkUrl.startsWith("//") && !linkUrl.startsWith("http")) {
                            linkUrl = "//" + linkUrl;
                        }

                        $image.parent().attr("href", linkUrl);
                    } else {
                        $image.parent().removeAttr("href");
                    }
                }

                function copyStyleToTinymceSpecificAttribute($element) {
                    $element.attr("data-mce-style", $element.attr("style"));
                }

                function removeAlign() {
                    this.css({
                        "float": "none",
                        margin: 0
                    });
                    copyStyleToTinymceSpecificAttribute(this);
                }

                function doCenterAlign() {
                    this.css({
                        "float": "none",
                        margin: "0 auto"
                    });
                    copyStyleToTinymceSpecificAttribute(this);
                }

                function doLeftAlign() {
                    this.css({
                        "float": "left",
                        margin: 0
                    });
                    copyStyleToTinymceSpecificAttribute(this);
                }

                function doRightAlign() {
                    this.css({
                        "float": "right",
                        margin: "0 auto"
                    });
                    copyStyleToTinymceSpecificAttribute(this);
                }

                var alignNameToAction = {
                    NONE: removeAlign,
                    CENTER: doCenterAlign,
                    LEFT: doLeftAlign,
                    RIGHT: doRightAlign
                };

                function reloadImageOnPage(imageDTO) {

                    var $image = $tag.find(".imcms-editor-content>a>img").first();

                    /** @namespace imageDTO.generatedFilePath */

                    var filePath = imageDTO.generatedFilePath;

                    if (filePath) {
                        filePath = location.origin + imcms.contextPath + filePath;

                        setOrRemoveAltAttribute($image, imageDTO);
                        setOrRemoveHrefAttribute($image, imageDTO);
                    } else {
                        $image.removeAttr("alt");
                        $image.parent().removeAttr("href");
                    }

                    if ($image.length) {

                        if (!filePath && $tag.hasClass("imcms-image-in-text")) {
                            $tag.remove();
                            imcms.require("tinyMCE", function (tinyMCE) {
                                tinyMCE.activeEditor.setDirty(true);
                            });
                            return;
                        }

                        $image.attr("src", filePath);

                        if ($image.attr("data-mce-src")) {
                            $image.attr("data-mce-src", filePath);
                            imcms.require("tinyMCE", function (tinyMCE) {
                                tinyMCE.activeEditor.setDirty(true);
                            });
                        }

                        if ($tag.hasClass("imcms-image-in-text") && alignNameToAction[imageDTO.align]) {
                            alignNameToAction[imageDTO.align].call($tag);
                        }
                    }
                }

                function onImageSaved() {
                    events.trigger("imcms-version-modified");

                    var imageRequestData = getImageRequestData(imcms.language.code);

                    imageRestApi.read(imageRequestData)
                        .success(reloadImageOnPage)
                        .error(console.error.bind(console));
                }

                function callBackAltText(continueSaving) {
                    if (continueSaving) {
                        imageData.width = cropElements.$image.width();
                        imageData.height = cropElements.$image.height();
                        var currentAngle = imageRotate.getCurrentAngle();
                        // these three should be done before close
                        imageWindowBuilder.closeWindow();

                        imageData.rotateAngle = currentAngle ? currentAngle.degrees : 0;
                        imageData.rotateDirection = currentAngle ? currentAngle.name : "NORTH";

                        imageData.allLanguages = opts.imageDataContainers.$allLanguagesCheckBox.isChecked();
                        imageData.alternateText = opts.imageDataContainers.$altText.$input.val();
                        imageData.linkUrl = opts.imageDataContainers.$imgLink.$input.val();

                        imageData.align = imgPosition.align;
                        imageData.spaceAround = imgPosition.spaceAround;

                        imageData.format = $fileFormat.getSelectedValue();

                        imageRestApi.create(imageData)
                            .success(onImageSaved)
                            .error(console.error.bind(console));
                    }
                }

                function saveAndClose() {
                    if (!opts.imageDataContainers.$altText.$input.val()) {
                        modalWindowBuilder.buildModalWindow(texts.altTextConfirm, callBackAltText);

                    } else {
                        callBackAltText(true);
                    }
                }

                function buildFooter() {
                    var $removeAndCloseButton = components.buttons.negativeButton({
                        text: texts.removeAndClose,
                        click: removeAndClose
                    });

                    var $saveAndCloseButton = components.buttons.saveButton({
                        text: texts.saveAndClose,
                        click: saveAndClose
                    });

                    return $("<div>").append($removeAndCloseButton, $saveAndCloseButton);
                }

                var $editableControls = buildEditableControls();
                var $footer = buildFooter().addClass(BEM.buildClass("imcms-image_editor", "footer"));

                return $("<div>").append($editableControls, $footer);
            }
        }
    }
);
