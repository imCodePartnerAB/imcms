define(
    "imcms-image-editor-right-side-builder",
    [
        "imcms-components-builder", "imcms-i18n-texts", "imcms", "jquery",
        "imcms-images-rest-api", "imcms-images-history-rest-api",
        "imcms-bem-builder", "imcms-modal-window-builder", "imcms-image-metadata-builder", "imcms-events",
        "imcms-window-builder", "imcms-image-rotate", 'imcms-image-resize',
        'imcms-crop-coords-controllers', 'path', 'imcms-image-edit-size-controls', 'imcms-image-locker-button'
    ],
    function (components, texts, imcms, $, imageRestApi, imageHistoryRestApi, BEM, modal, imageMetadataWindowBuilder, events,
              WindowBuilder, imageRotate, imageResize, cropCoordsControllers, path, imageEditSize, compressionLock) {

        texts = texts.editors.image;

        let $tag, imageData, $fileFormat, $textAlignmentBtnsContainer, $imageSizeInfo, $imageInfoPath, $compressionContainer;
        let $restrictedStyleWidth, $restrictedStyleHeight, $editableControls;
        let $altText, $imgLink, $langFlags, $allLanguagesCheckBox;
        let $advancedModeBtn, $imageHistoryBtn, $imageHistoryEntries, $cancelHistoryBtn, cancelImageData;
        const imgPosition = {
            align: "NONE",
            spaceAround: {
                top: 0,
                right: 0,
                bottom: 0,
                left: 0
            }
        };

        const alignButtonSelectorToAlignName = {
            NONE: BEM.buildClassSelector(null, "imcms-button", "align-none"),
            CENTER: BEM.buildClassSelector(null, "imcms-button", "align-center"),
            LEFT: BEM.buildClassSelector(null, "imcms-button", "align-left"),
            RIGHT: BEM.buildClassSelector(null, "imcms-button", "align-right")
        };

        function buildActiveImageSizeInfo() {
            return $imageSizeInfo = components.texts.titleText('<div>');
        }

        function buildActiveImagePathInfo() {
            return $imageInfoPath = components.texts.titleText('<div>');
        }

        let $noImageInfo;

        function buildNoImageInfo() {
            return $noImageInfo = $('<i>');
        }

        function buildRestrictedWidthStyle(prefix, width) {
            const widthText = width ? `${prefix}: ${width}` : '';
            return $restrictedStyleWidth = components.texts.titleText('<div>', widthText, {class:'imcms-restricted-width'});
        }

        function buildRestrictedHeightStyle(prefix, height) {
            const heightText = height ? `${prefix}: ${height}` : '';
            return $restrictedStyleHeight = components.texts.titleText('<div>', heightText, {class:'imcms-restricted-height'});
        }

        function isStyleExist(styles) {
            return !!styles;
        }

        function checkExistImageData(image) {
            return image.path !== '' && image.width > 0 && image.height > 0;
        }

        function getCurrentImageData(){
            const clonedImageData = $.extend(true, {}, imageData);

            clonedImageData.width = imageResize.getPreviewWidth();
            clonedImageData.height = imageResize.getPreviewHeight();
            const currentAngle = imageRotate.getCurrentAngle();

            clonedImageData.rotateAngle = currentAngle ? currentAngle.degrees : 0;
            clonedImageData.rotateDirection = currentAngle ? currentAngle.name : "NORTH";

            clonedImageData.allLanguages = $allLanguagesCheckBox.isChecked();
            clonedImageData.alternateText = $altText.$input.val();
            clonedImageData.linkUrl = $imgLink.$input.val();

            clonedImageData.align = imgPosition.align;
            clonedImageData.spaceAround = imgPosition.spaceAround;

            clonedImageData.format = $fileFormat.getSelectedValue();

            clonedImageData.compress = compressionLock.getCompress();

            return clonedImageData;
        }

        module.exports = {
            getCurrentImageData: getCurrentImageData,

            updateImageData: ($newTag, newImageData) => {

                $tag = $newTag;
                imageData = newImageData;

                const spaceAround = imageData.spaceAround;
                spaceAround.top && $("#image-space-top").val(spaceAround.top).blur();
                spaceAround.right && $("#image-space-right").val(spaceAround.right).blur();
                spaceAround.bottom && $("#image-space-bottom").val(spaceAround.bottom).blur();
                spaceAround.left && $("#image-space-left").val(spaceAround.left).blur();

                $fileFormat.selectValue(imageData.format);
                if (checkExistImageData(imageData)) {
                    $editableControls.children().not(".imcms-advanced-mode").not(".imcms-history-mode").show();
                    $imageInfoPath.text(path.normalize(`${imageData.path}`)).show();
                    $imageSizeInfo.show();
                    $noImageInfo.hide();
                } else {
                    $editableControls.children().not(".imcms-editable-controls-area__buttons").not(".imcms-history-mode").hide();
                    $(".imcms-image-advanced-button").hide();

                    $imageInfoPath.hide();
                    $imageSizeInfo.hide();
                    $noImageInfo.text(texts.noSelectedImage).show();
                }

                $textAlignmentBtnsContainer.find(alignButtonSelectorToAlignName[imageData.align || 'NONE']).click();
            },

            build: function (opts) {
                const fillData = opts.fillData;
                const imageWindowBuilder = opts.imageWindowBuilder;
                $tag = opts.$tag;
                const standaloneEditor = $tag.data('standalone');
                imageData = opts.imageData;


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

                    opts.imageDataContainers.$altText = $altText = $textBox;

                    return new BEM({
                        block: 'action-alt-text-container',
                        elements: {
                            'alt-text-box': $textBox,
                            'alt-suggest-btn': $makeSuggestButton
                        }
                    }).buildBlockStructure("<div>");
                }

                function buildImageLinkTextBox() {
                    return opts.imageDataContainers.$imgLink = $imgLink = components.texts.textBox("<div>", {
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

                            // Clear history for previous language
                            if($imageHistoryBtn.attr("data-state") === "true") $imageHistoryBtn.click();
                            $imageHistoryEntries.empty();
                        }
                    }]);
                }

                function buildAllLanguagesCheckbox() {
                    return components.checkboxes.checkboxContainer("<div>", [
                        opts.imageDataContainers.$allLanguagesCheckBox = $allLanguagesCheckBox = components.checkboxes.imcmsCheckbox("<div>", {
                            name: "allLanguages",
                            text: texts.allLangs
                        })
                    ]);
                }

                function buildAdvancedModeBtn($advancedControls) {
                    return components.buttons.negativeButton({
                        text: texts.advanced,
                        "data-state": "false",
                        "class": "imcms-image-advanced-button",
                        click: function () {
                            const $btn = $(this);

                            if ($btn.attr("data-state") === "false") {
                                $advancedControls.css("display", "block");
                                $btn.attr("data-state", "true").text(texts.simple);
                                $imageHistoryBtn.css("display", "none");

                                $(".imcms-editable-controls-area").css("height", calculateEditableControlsHeight());
                            } else {
                                $advancedControls.css("display", "none");
                                $btn.attr("data-state", "false").text(texts.advanced);
                                $imageHistoryBtn.css("display", "block");

                                $(".imcms-editable-controls-area").css("height", "inherit");
                            }
                        }
                    });
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
                        text: texts.none
                    });
                    components.overlays.defaultTooltip($alignNoneBtn, texts.align.none);

                    const $alignCenterBtn = buildAlignButton(["align-center"], {
                        click: onAlignCenterClick,
                    });
                    components.overlays.defaultTooltip($alignCenterBtn, texts.align.center);

                    const $alignLeftBtn = buildAlignButton(["align-left"], {
                        click: onAlignLeftClick,
                    });
                    components.overlays.defaultTooltip($alignLeftBtn, texts.align.left);

                    const $alignRightBtn = buildAlignButton(["align-right"], {
                        click: onAlignRightClick,
                    });
                    components.overlays.defaultTooltip($alignRightBtn, texts.align.right);

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

                function buildCompressionContainer(){
                    const $compressionContainer = $('<div>', {
                        'class': 'imcms-compression'
                    });
                    $compressionContainer.append(compressionLock.getCompressButton());
                    $compressionContainer.append(compressionLock.getCompressText());

                    return $compressionContainer;
                }

                const $showExifBtn = new BEM({
                    block: 'image-exif-info',
                    elements: {
                        'button': components.buttons.neutralButton({
                            text: texts.exif.button,
                            click: () => imageMetadataWindowBuilder.buildImageExifInfo(imageData),
                            name: 'exifInfo'
                        })
                    }
                }).buildBlockStructure('<div>', {"class": "image-exif-window"});
                components.overlays.defaultTooltip($showExifBtn, texts.exif.title);

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
                    $compressionContainer = buildCompressionContainer();

                    return advancedModeBEM.buildBlock("<div>", [
                        {"title": $textAlignmentBtnsTitle},
                        {"buttons": $textAlignmentBtnsContainer},
                        {"space-around": $spaceAroundImageInputContainer},
                        {"title": $cropCoordinatesText},
                        {"crop-coordinates": $cropCoordinatesContainer},
                        {"file-format": $fileFormat},
                        {"compression": $compressionContainer},
                        {"exif": $showExifBtn}
                    ]);
                }

                function buildImageHistoryContainer(){
                    $imageHistoryEntries = $('<div>');

                    $cancelHistoryBtn = components.buttons.neutralButton({
                        text: texts.cancel,
                        click: () => {
                            fillData(cancelImageData);
                            $imageHistoryEntries.find('.image-history-entry--active').removeClass('image-history-entry--active');
                        }
                    }).css("display", "none");

                    return new BEM({
                        block: "imcms-history-mode",
                        elements: {
                            "entries": $imageHistoryEntries,
                            "button": $cancelHistoryBtn
                        }
                    }).buildBlockStructure("<div>");
                }

                function buildHistoryModeBtn($imageHistoryContainer) {
                    const $btn = components.buttons.negativeButton({
                        text: texts.showHistory,
                        "data-state": "false",
                        "class": "imcms-image-history-button",
                        click: () => {
                            if ($btn.attr("data-state") === "false") {
                                showHistory($imageHistoryContainer);
                                $btn.attr("data-state", "true").text(texts.hideHistory);
                                $advancedModeBtn.css("display", "none");

                                $(".imcms-editable-controls-area").css("height", calculateEditableControlsHeight());
                            } else {
                                cancelImageData = null;
                                $cancelHistoryBtn.css("display", "none");

                                $imageHistoryContainer.css("display", "none");
                                $btn.attr("data-state", "false").text(texts.showHistory);
                                if(imageData.path) $advancedModeBtn.css("display", "block");

                                $(".imcms-editable-controls-area").css("height", "inherit");
                            }
                        }
                    });

                    return $btn;
                }

                function showHistory($imageHistoryContainer){
                    if($imageHistoryEntries.contents().length > 0){
                        $imageHistoryContainer.css("display", "block");
                        return;
                    }

                    imageHistoryRestApi.read(getImageRequestData(imageData.langCode))
                        .done(imagesHistory => {
                            imagesHistory.forEach(imageHistory => {
                                const $textHistoryEntry = new BEM({
                                    block: 'image-history-entry',
                                    elements: {
                                        "date": $('<div>', {text: imageHistory.modifiedAt}),
                                        "login": $('<div>', {text: imageHistory.modifiedBy.login}),
                                        "path": $('<div>', {text: imageHistory.path})
                                    }
                                }).buildBlockStructure('<div>', {
                                    click: () => {
                                        $imageHistoryEntries.find('.image-history-entry--active').removeClass('image-history-entry--active');
                                        $textHistoryEntry.addClass('image-history-entry--active');

                                        if(!cancelImageData) cancelImageData = getCurrentImageData();
                                        $cancelHistoryBtn.css("display", "block");

                                        fillData(imageHistory);
                                    }
                                });

                                $imageHistoryEntries.append($textHistoryEntry);
                            });

                            $imageHistoryContainer.css("display", "block");
                        }).fail(() => modal.buildErrorWindow(texts.error.loadHistoryFailed));
                }

                function buildInfoSizePathContainer() {
                    return new BEM({
                        block: 'imcms-info-edit-image',
                        elements: {
                            'title': components.texts.titleText('<div>', texts.activeTitle),
                            'path-info': buildActiveImagePathInfo(),
                            'size-info': buildActiveImageSizeInfo(),
                            'no-image': buildNoImageInfo(),
                        }
                    }).buildBlockStructure('<div>')
                }

                function buildRestrictedStyleInfoContainer(existStyle) {
                    const styleInfoBEM = new BEM({
                        block: 'imcms-restricted-style',
                        elements: {
                            'title': 'imcms-title',
                            'width': 'imcms-restricted-width',
                            'height': 'imcms-restricted-height',
                            'info': 'imcms-info-msg'
                        }
                    });

                    if (existStyle) {
                        const $titleStyleInfo = components.texts.titleText("<div>", texts.styleInfo.title);
                        const $info = components.texts.infoText("<div>", texts.styleInfo.info);

                        return styleInfoBEM.buildBlock('<div>', [
                            {'title': $titleStyleInfo},
                            {'width': $restrictedStyleWidth},
                            {'height': $restrictedStyleHeight},
                            {'info': $info}
                        ]);
                    } else {
                        return null;
                    }
                }

                function buildEditableControls() {
                    const editableControlsBEM = new BEM({
                        block: "imcms-editable-controls-area",
                        elements: {
                            "text-box": "imcms-text-box",
                            "flags": "imcms-flags",
                            "checkboxes": "imcms-checkboxes",
                            "advanced-mode": "imcms-advanced-mode",
                            "history-mode": "imcms-history-mode"
                        }
                    });
                    const $altTextContainer = buildAltTextContainer();
                    const $imageLinkTextBox = buildImageLinkTextBox();
                    opts.imageDataContainers.$langFlags = $langFlags = buildImageLangFlags();
                    const $allLangs = buildAllLanguagesCheckbox();

                    const $advancedControls = buildAdvancedControls();
                    $advancedModeBtn = buildAdvancedModeBtn($advancedControls);
                    const $imageHistoryContainer = buildImageHistoryContainer();
                    $imageHistoryBtn = buildHistoryModeBtn($imageHistoryContainer);

                    const $buttons = $('<div>', {html: [$advancedModeBtn, $imageHistoryBtn]});

                    return editableControlsBEM.buildBlock("<div>", [
                        {"text-box": $altTextContainer},
                        {"text-box": $imageLinkTextBox},
                        {"flags": $langFlags},
                        {"checkboxes": $allLangs},
                        {"buttons": $buttons},
                        {"advanced-mode": $advancedControls},
                        {"history-mode": $imageHistoryContainer}
                    ]);
                }

                function calculateEditableControlsHeight(){
                    let infoImageHeight = $(".imcms-info-edit-image").last().innerHeight();
                    let footerHeight = $(".imcms-image_editor__footer").last().innerHeight();
                    let restrictedImageHeight = $(".imcms-restricted-style").last().innerHeight() | 0;
                    return "calc(100% - " + (infoImageHeight + footerHeight + restrictedImageHeight + 1) + "px)";
                }

                function removeAndClose() {
                    if (!standaloneEditor) imageWindowBuilder.closeWindow();

                    imageData.allLanguages = $allLanguagesCheckBox.isChecked();

                        imageRestApi.remove(imageData)
                            .done(() => {
                                if (!standaloneEditor) {
                                    onImageSaved();
                                } else {
                                    imageWindowBuilder.closeWindow();
                                }
                            })
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
                        imageRequestData["loopEntryRef.loopEntryIndex"] = imageData.loopEntryRef.loopEntryIndex;
                        imageRequestData["loopEntryRef.loopIndex"] = imageData.loopEntryRef.loopIndex;
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
                        if (!linkUrl.startsWith("/") && !linkUrl.startsWith("http")) {
                            linkUrl = "//" + linkUrl;
                        }

                        $image.parent().attr({
                            "href": linkUrl,
                            "target": "_blank"
                        });
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
                        filePath = imcms.imagesPath + "?path=" + filePath;
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

                        if(imageDTO.spaceAround.top !== 0) $image.css("margin-top", imageDTO.spaceAround.top + "px");
                        if(imageDTO.spaceAround.bottom !== 0) $image.css("margin-bottom", imageDTO.spaceAround.bottom + "px");
                        if(imageDTO.spaceAround.left !== 0) $image.css("margin-left",imageDTO.spaceAround.left + "px");
                        if(imageDTO.spaceAround.right !== 0) $image.css("margin-right", imageDTO.spaceAround.right + "px");

                    }
                    // window.location.reload(true);
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
                        // these three should be done before close
                        const currentImageData = getCurrentImageData();

                        if(!standaloneEditor) imageWindowBuilder.closeWindow();

                        imageRestApi.create(currentImageData)
                            .done(() => {
                                if (!standaloneEditor) {
                                    onImageSaved();
                                } else {
                                    imageWindowBuilder.closeWindow();
                                }
                            })
                            .fail(() => modal.buildErrorWindow(texts.error.createFailed));
                    }
                }

                function saveAndClose() {
                    if (!$altText.$input.val()) {
                        modal.buildModalWindow(texts.altTextConfirm, callBackAltText);

                    } else {
                        callBackAltText(true);
                    }
                }

                function cancelAndClose(){
                    imageWindowBuilder.closeWindow();
                }

                function buildFooter() {
                    const $cancelButton = components.buttons.negativeButton({
                        text: texts.cancelAndClose,
                        click: cancelAndClose
                    });

                    const $removeAndCloseButton = components.buttons.negativeButton({
                        text: texts.removeAndClose,
                        click: removeAndClose
                    });

                    const $saveAndCloseButton = components.buttons.saveButton({
                        text: texts.saveAndClose,
                        click: saveAndClose
                    });

                    return $("<div>").append($cancelButton, $removeAndCloseButton, $saveAndCloseButton);
                }

                const style = $tag.data('style');
                const existsStyle = isStyleExist($tag.data('style'));
                const resultStyleObj = {};
                let isRestrictedWHStyles = false;
                if (existsStyle) {
                    style.split(';')
                        .map(x => x.trim())
                        .filter(x => !!x)
                        .forEach(x => {
                            const styleKeyAndValue = x.split(':').map(x => x.trim());
                            resultStyleObj[styleKeyAndValue[0]] = styleKeyAndValue[1];
                        });
                }

                if (resultStyleObj['max-width'] || resultStyleObj['max-height'] || resultStyleObj.width || resultStyleObj.height) {
                    isRestrictedWHStyles = true;
                    //init both fields
                    buildRestrictedWidthStyle();
                    buildRestrictedHeightStyle();

                    if(resultStyleObj['max-width']) buildRestrictedWidthStyle('max-width', resultStyleObj['max-width']);
                    if(resultStyleObj.width) buildRestrictedWidthStyle('width', resultStyleObj.width);
                    if(resultStyleObj['max-height']) buildRestrictedHeightStyle('max-height', resultStyleObj['max-height']);
                    if(resultStyleObj.height) buildRestrictedHeightStyle('height', resultStyleObj.height);
                }

                const $restrictStyleInfo = buildRestrictedStyleInfoContainer(isRestrictedWHStyles);
                const $infoImage = buildInfoSizePathContainer();
                $editableControls = buildEditableControls();
                const $footer = buildFooter().addClass(BEM.buildClass("imcms-image_editor", "footer"));

                return $("<div>").append($restrictStyleInfo, $infoImage, $footer, $editableControls);
            }
        }
    }
);
