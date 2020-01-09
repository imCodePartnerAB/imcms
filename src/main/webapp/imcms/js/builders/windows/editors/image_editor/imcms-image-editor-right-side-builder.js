define(
    "imcms-image-editor-right-side-builder",
    [
        "imcms-components-builder", "imcms-i18n-texts", "imcms-content-manager-builder", "imcms", "jquery",
        "imcms-images-rest-api", "imcms-bem-builder", "imcms-modal-window-builder", "imcms-events",
        "imcms-window-builder", "imcms-image-rotate", "imcms-image-editor-body-head-builder", 'imcms-image-resize',
        'imcms-crop-coords-controllers'
    ],
    function (components, texts, contentManager, imcms, $, imageRestApi, BEM, modal, events, WindowBuilder,
              imageRotate, imageEditorBodyHeadBuilder, imageResize, cropCoordsControllers) {

        texts = texts.editors.image;

        let $tag, imageData, $fileFormat, $textAlignmentBtnsContainer;
        const imgPosition = {
            align: "NONE",
            spaceAround: {
                top: 0,
                right: 0,
                bottom: 0,
                left: 0
            }
        };

        let $exifInfoContainer;

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
            (imageData.exifInfo || []).forEach(exifDataRow => {
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

        const alignButtonSelectorToAlignName = {
            NONE: BEM.buildClassSelector(null, "imcms-button", "align-none"),
            CENTER: BEM.buildClassSelector(null, "imcms-button", "align-center"),
            LEFT: BEM.buildClassSelector(null, "imcms-button", "align-left"),
            RIGHT: BEM.buildClassSelector(null, "imcms-button", "align-right")
        };

        module.exports = {
            updateImageData: ($newTag, newImageData) => {
                $tag = $newTag;
                imageData = newImageData;

                const spaceAround = imageData.spaceAround;
                spaceAround.top && $("#image-space-top").val(spaceAround.top).blur();
                spaceAround.right && $("#image-space-right").val(spaceAround.right).blur();
                spaceAround.bottom && $("#image-space-bottom").val(spaceAround.bottom).blur();
                spaceAround.left && $("#image-space-left").val(spaceAround.left).blur();

                $fileFormat.selectValue(imageData.format);

                $textAlignmentBtnsContainer.find(alignButtonSelectorToAlignName[imageData.align || 'NONE']).click();
            },
            build: function (opts) {

                const fillData = opts.fillData;
                const imageWindowBuilder = opts.imageWindowBuilder;
                $tag = opts.$tag;
                imageData = opts.imageData;

                function buildActionImageBtnContainer() {

                    const $selectImageBtn = components.buttons.buttonWithIcon({
                        button: components.buttons.positiveButton({
                            text: texts.selectImage,
                        }),
                        icon: components.controls.images(),
                    }, {
                        click: contentManager.build.bind(contentManager, fillData, () => imageEditorBodyHeadBuilder.getImagePath())
                    });

                    return components.buttons.buttonsContainer("<div>", [$selectImageBtn]);
                }


                function buildAltTextContainer() {
                    function generateSuggestedAltText() {
                        if (imageData.alternateText.trim() !== '') {
                            modal.buildModalWindow(texts.warnChange, confirmed => {
                                if (!confirmed) return;

                                replaceAndAddImageNameInAltText();
                            });
                        } else {
                            replaceAndAddImageNameInAltText();
                        }
                    }

                    function replaceAndAddImageNameInAltText() {
                        $(`[name='altText']`).val(`${imageData.name.replace(/[_-]/gi, ' ').replace(/.[^.]+$/, '')}`);
                    }

                    const $makeSuggestButton = components.buttons.positiveButton({
                        class: 'suggest-alt-text',
                        text: texts.suggestAltText,
                        click: generateSuggestedAltText
                    });

                    const $textBox = components.texts.textBox("<div>", {
                        text: texts.altText,
                        name: "altText"
                    });

                    opts.imageDataContainers.$altText = $textBox;

                    return new BEM({
                        block: 'action-alt-text-container',
                        elements: {
                            'alt-text-box': $textBox,
                            'alt-suggest-btn': $makeSuggestButton
                        }
                    }).buildBlockStructure("<div>");
                }

                function buildImageLinkTextBox() {
                    return opts.imageDataContainers.$imgLink = components.texts.textBox("<div>", {
                        text: texts.imageLink,
                        name: "imageLink"
                    });
                }

                function buildImageLangFlags() {
                    imageData.langCode = imcms.language.code;

                    return components.flags.flagsContainer(language => ["<div>", {
                        click: function () {
                            imageData.langCode = language.code;

                            const imageRequestData = getImageRequestData(imageData.langCode);

                            imageRestApi.read(imageRequestData)
                                .done(fillData)
                                .fail(() => modal.buildErrorWindow(texts.error.loadFailed));
                        }
                    }]);
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
                                const $btn = $(this);

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
                    let $alignContainer;
                    const activeAlignClass = "imcms-button--align-active";

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

                    const $alignNoneBtn = buildAlignButton(["align-none", "align-active"], {
                        click: onAlignNoneClick,
                        title: texts.align.none,
                        text: texts.none
                    });
                    const $alignCenterBtn = buildAlignButton(["align-center"], {
                        click: onAlignCenterClick,
                        title: texts.align.center
                    });
                    const $alignLeftBtn = buildAlignButton(["align-left"], {
                        click: onAlignLeftClick,
                        title: texts.align.left
                    });
                    const $alignRightBtn = buildAlignButton(["align-right"], {
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
                    const spaceValue = $(element).val();
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

                function buildCropCoordinatesContainer() {
                    return new BEM({
                        block: "imcms-crop-coordinates",
                        elements: {
                            "x": cropCoordsControllers.getCropCoordX(),
                            "y": cropCoordsControllers.getCropCoordY(),
                            "x1": cropCoordsControllers.getCropCoordX1(),
                            "y1": cropCoordsControllers.getCropCoordY1(),
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
                        text: "GIF",
                        "data-value": "GIF"
                    }]);
                }

                function showExif() {
                    exifInfoWindowBuilder.buildWindow();
                }

                function buildAdvancedControls() {
                    const advancedModeBEM = new BEM({
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

                    const $textAlignmentBtnsTitle = advancedModeBEM.buildElement("title", "<div>", {text: texts.alignment});
                    $textAlignmentBtnsContainer = buildTextAlignmentBtnsContainer();
                    const $spaceAroundImageInputContainer = buildSpaceAroundImageInputContainer();
                    const $cropCoordinatesText = buildCropCoordinatesText(advancedModeBEM);
                    const $cropCoordinatesContainer = buildCropCoordinatesContainer();
                    $fileFormat = buildFileFormatSelect();

                    const $showExifBtn = components.buttons.neutralButton({
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
                    const editableControlsBEM = new BEM({
                        block: "imcms-editable-controls-area",
                        elements: {
                            "buttons": "imcms-buttons",
                            "text-box": "imcms-text-box",
                            "flags": "imcms-flags",
                            "checkboxes": "imcms-checkboxes",
                            "advanced-mode": "imcms-advanced-mode"
                        }
                    });

                    const $actionImageBtnContainer = buildActionImageBtnContainer();
                    const $altTextContainer = buildAltTextContainer();
                    const $imageLinkTextBox = buildImageLinkTextBox();
                    opts.imageDataContainers.$langFlags = buildImageLangFlags();
                    const $allLangs = buildAllLanguagesCheckbox();
                    const $advancedControls = buildAdvancedControls();
                    const $advancedModeBtn = buildAdvancedModeBtn($advancedControls);

                    return editableControlsBEM.buildBlock("<div>", [
                        {"buttons": $actionImageBtnContainer},
                        {"text-box": $altTextContainer},
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
                        .done(onImageSaved)
                        .fail(() => modal.buildErrorWindow(texts.error.removeFailed));
                }

                function getImageRequestData(langCode) {
                    const imageRequestData = {
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

                function setAltAttribute($image, imageDTO) {
                    $image.attr("alt", imageDTO.alternateText);
                }

                function addOrRemoveLinkElementIfNeeded($image, imageDTO) {
                    const linkUrl = imageDTO.linkUrl;
                    const isParentLink = $image.parent().is('a');

                    if (linkUrl && !isParentLink) {
                        $image.wrap('<a></a>');
                    } else if (!linkUrl && isParentLink) {
                        $image.unwrap();
                    }
                }

                function setHrefAttribute($image, imageDTO) {
                    let linkUrl = imageDTO.linkUrl;
                    if (linkUrl) {
                        if (!linkUrl.startsWith("//") && !linkUrl.startsWith("http")) {
                            linkUrl = "//" + linkUrl;
                        }

                        $image.parent().attr("href", linkUrl);
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

                const alignNameToAction = {
                    NONE: removeAlign,
                    CENTER: doCenterAlign,
                    LEFT: doLeftAlign,
                    RIGHT: doRightAlign
                };

                function setDirty() {
                    const tinyMCE = require("tinymce");
                    tinyMCE.activeEditor.setDirty(true);
                }

                function reloadImageOnPage(imageDTO) {

                    const $image = $tag.find(".imcms-editor-content img").first();
                    const defaultPath = imcms.contextPath + '/imcms/images/icon_missing_image.png';

                    /** @namespace imageDTO.generatedFilePath */
                    let filePath = imageDTO.generatedFilePath;

                    setAltAttribute($image, imageDTO);
                    if (filePath) {
                        filePath = location.origin + imcms.contextPath + filePath;
                        addOrRemoveLinkElementIfNeeded($image, imageDTO);
                        setHrefAttribute($image, imageDTO);
                    } else {
                        $image.parent().removeAttr("href");
                    }

                    if ($image.length) {

                        if (!filePath && $tag.hasClass("imcms-image-in-text")) {
                            $tag.remove();
                            setDirty();
                            return;
                        }

                        if (filePath) {
                            $image.attr("src", filePath);
                        } else {
                            $image.attr("src", defaultPath);
                        }

                        if ($image.attr("data-mce-src")) {
                            $image.attr("data-mce-src", filePath);
                            setDirty();
                        }

                        if ($tag.hasClass("imcms-image-in-text") && alignNameToAction[imageDTO.align]) {
                            alignNameToAction[imageDTO.align].call($tag);
                            $('.imcms-image-in-text').parent().focus();
                        }
                    }
                }

                function onImageSaved() {
                    events.trigger("imcms-version-modified");

                    const imageRequestData = getImageRequestData(imcms.language.code);

                    imageRestApi.read(imageRequestData)
                        .done(reloadImageOnPage)
                        .fail(() => modal.buildErrorWindow(texts.error.loadFailed));
                }

                function callBackAltText(continueSaving) {
                    if (continueSaving) {
                        imageData.width = imageResize.getWidth();
                        imageData.height = imageResize.getHeight();
                        const currentAngle = imageRotate.getCurrentAngle();
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
                            .done(onImageSaved)
                            .fail(() => modal.buildErrorWindow(texts.error.createFailed));
                    }
                }

                function saveAndClose() {
                    if (!opts.imageDataContainers.$altText.$input.val()) {
                        modal.buildModalWindow(texts.altTextConfirm, callBackAltText);

                    } else {
                        callBackAltText(true);
                    }
                }

                function buildFooter() {
                    const $removeAndCloseButton = components.buttons.negativeButton({
                        text: texts.removeAndClose,
                        click: removeAndClose
                    });

                    const $saveAndCloseButton = components.buttons.saveButton({
                        text: texts.saveAndClose,
                        click: saveAndClose
                    });

                    return $("<div>").append($removeAndCloseButton, $saveAndCloseButton);
                }

                const $editableControls = buildEditableControls();
                const $footer = buildFooter().addClass(BEM.buildClass("imcms-image_editor", "footer"));

                return $("<div>").append($editableControls, $footer);
            }
        }
    }
);
