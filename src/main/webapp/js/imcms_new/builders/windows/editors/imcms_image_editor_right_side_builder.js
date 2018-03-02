Imcms.define(
    "imcms-image-editor-right-side-builder",
    [
        "imcms-components-builder", "imcms-i18n-texts", "imcms-content-manager-builder", "imcms", "jquery",
        "imcms-images-rest-api", "imcms-bem-builder", "imcms-modal-window-builder", "imcms-events"
    ],
    function (components, texts, contentManager, imcms, $, imageRestApi, BEM, modalWindowBuilder, events) {

        texts = texts.editors.image;

        return {
            build: function (opts) {

                var imageEditorBlockClass = opts.imageEditorBlockClass;
                var fillData = opts.fillData;
                var $tag = opts.$tag;
                var imageWindowBuilder = opts.imageWindowBuilder;
                var imageData = opts.imageData;

                function buildSelectImageBtnContainer() {
                    var $selectImageBtn = components.buttons.neutralButton({
                        text: texts.selectImage,
                        click: contentManager.build.bind(contentManager, fillData)
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
                    function buildAlignButton(modifiers, onClick) {
                        return components.buttons.imcmsButton({click: onClick}, ["align"].concat(modifiers));
                    }

                    // todo: implement onClick!
                    var $alignNoneBtn = buildAlignButton(["align-none", "align-active"]).text(texts.none);
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
                            placeholder: texts.top
                        }, {
                            id: "image-space-right",
                            name: "right",
                            placeholder: texts.right
                        }, {
                            id: "image-space-bottom",
                            name: "bottom",
                            placeholder: texts.bottom
                        }, {
                            id: "image-space-left",
                            name: "left",
                            placeholder: texts.left
                        }
                    ], {text: texts.spaceAround});
                }

                function buildCropCoordinatesText(advancedModeBEM) {
                    return advancedModeBEM.buildElement("title", "<div>")
                        .append(texts.cropCoords + " (W:")
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
                        error: "Error"
                    });

                    var $yCropCoord = components.texts.textNumber("<div>", {
                        name: "cropY0",
                        placeholder: "Y",
                        text: "Y",
                        error: "Error"
                    });

                    var $x1CropCoord = components.texts.textNumber("<div>", {
                        name: "cropX1",
                        placeholder: "X1",
                        text: "X1",
                        error: "Error"
                    });

                    var $y1CropCoord = components.texts.textNumber("<div>", {
                        name: "cropY1",
                        placeholder: "Y1",
                        text: "Y1",
                        error: "Error"
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

                    var $textAlignmentBtnsTitle = advancedModeBEM.buildElement("title", "<div>", {text: texts.alignment});
                    var $textAlignmentBtnsContainer = buildTextAlignmentBtnsContainer();
                    var $spaceAroundImageInputContainer = buildSpaceAroundImageInputContainer();
                    var $cropCoordinatesText = buildCropCoordinatesText(advancedModeBEM);
                    var $cropCoordinatesContainer = buildCropCoordinatesContainer();
                    var $fileFormat = buildFileFormatSelect();
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
                            $tag.detach();
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
                        imageWindowBuilder.closeWindow();

                        imageData.allLanguages = opts.imageDataContainers.$allLanguagesCheckBox.isChecked();
                        imageData.alternateText = opts.imageDataContainers.$altText.$input.val();
                        imageData.linkUrl = opts.imageDataContainers.$imgLink.$input.val();

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
                var $footer = buildFooter().addClass(imageEditorBlockClass + BEM.getBlockSeparator() + "footer");

                return $("<div>").append($editableControls, $footer);
            }
        }
    }
);
