define("imcms-image-metadata-builder",
    [
        "imcms-window-builder", "imcms-bem-builder", "imcms-components-builder", "jquery", "imcms-i18n-texts", "imcms-image-files-rest-api", "imcms-modal-window-builder"
    ],
    function (WindowBuilder, BEM, components, $, texts, imageFilesRestApi, modal) {

        texts = texts.editors.imageMetadata;

        let imageDTO;

        let onMetadataSaved;
        let metadataEditorWindowData = {};

        let exifInfoWindowData = {};
        let exifMode = {
            CUSTOM: "custom",
                ALL: "all"
        };


        // Exif Info

        function buildImageExifBody(){
            exifInfoWindowData.$bodyContent = $('<div>');
            exifInfoWindowData.$customExifInfo = buildCustomExifInfo();
            exifInfoWindowData.$allExifInfo = buildAllExifInfo();

            exifInfoWindowData.$bodyContent.append(exifInfoWindowData.$customExifInfo);
            exifInfoWindowData.currentExifMode = exifMode.CUSTOM

            return exifInfoWindowData.$bodyContent;
        }

        function buildCustomExifInfo(){
            let $photographer = components.texts.textField('<div>', {
                text: texts.photographer,
                value: imageDTO.exifInfo.customExif.photographer
            });
            $photographer.$input.attr('disabled', 'disabled');

            let $uploadedBy = components.texts.textField('<div>', {
                text: texts.uploadedBy,
                value: imageDTO.exifInfo.customExif.uploadedBy
            });
            $uploadedBy.$input.attr('disabled', 'disabled');

            let $copyright = components.texts.textField('<div>', {
                text: texts.copyright,
                value: imageDTO.exifInfo.customExif.copyright
            });
            $copyright.$input.attr('disabled', 'disabled');

            return new BEM({
                block: "imcms-image-custom-exif",
                elements: {
                    "title": components.texts.titleText('<div>', texts.titleCustomExifMode),
                    "photographer": $photographer,
                    "uploaded-by": $uploadedBy,
                    "copyright": $copyright,
                    "license-period": buildReadOnlyLicensePeriodField()
                }
            }).buildBlockStructure("<div>");
        }

        function buildReadOnlyLicensePeriodField(){
            let $dateStart = components.dateTime.dateBoxReadOnly();
            $dateStart.setDate(imageDTO.exifInfo.customExif.licensePeriodStart);

            let $dateEnd = components.dateTime.dateBoxReadOnly();
            $dateEnd.setDate(imageDTO.exifInfo.customExif.licensePeriodEnd);

            let $delimiter = components.texts.titleText('<div>', "—", {
                'class': 'imcms-delimeter'
            });

            return new BEM({
                block: "imcms-license-period",
                elements: {
                    "title": components.texts.titleText("<div>", texts.licensePeriod, {
                        'class': 'imcms-label'
                    }),
                    "range": $('<div>', {
                        html: [$dateStart, $delimiter, $dateEnd]
                    })
                }
            }).buildBlockStructure("<div>", {"class": "imcms-field"});
        }

        function buildAllExifInfo() {
            let $allExifInfo = $("<div>");
            $allExifInfo.append(components.texts.titleText('<div>', texts.titleAllExifMode));

            (imageDTO.exifInfo.allExifInfo || []).forEach(exifDataRow => {
                $allExifInfo.append($("<div>", {"class": "image-exif-window__row"}).text(exifDataRow));
            });

            return $allExifInfo;
        }

        function buildExifInfoFooter() {
            return WindowBuilder.buildFooter([
                components.buttons.negativeButton({
                    text: texts.allExifButton,
                    click: toggleExifInfoMode
                })
            ]);
        }

        function toggleExifInfoMode(){
            let $toggleButton = $(this);

            exifInfoWindowData.$bodyContent.empty();

            switch (exifInfoWindowData.currentExifMode) {
                case exifMode.CUSTOM:
                    exifInfoWindowData.$bodyContent.append(exifInfoWindowData.$allExifInfo);
                    exifInfoWindowData.currentExifMode = exifMode.ALL;
                    $toggleButton.text(texts.customExifButton);
                    break;
                case exifMode.ALL:
                    exifInfoWindowData.$bodyContent.append(exifInfoWindowData.$customExifInfo);
                    exifInfoWindowData.currentExifMode = exifMode.CUSTOM;
                    $toggleButton.text(texts.allExifButton);
                    break;
            }
        }

        function buildImageExifInfoWindow() {
            return new BEM({
                block: "imcms-pop-up-modal",
                elements: {
                    "head": imageMetadataWindowBuilder.buildHead(texts.titleExifInfo, () => imageExifWindowBuilder.closeWindow()),
                    "body": buildImageExifBody(),
                    "footer": buildExifInfoFooter()
                }
            }).buildBlockStructure("<div>", {"class": "image-exif-window"});
        }


        // Metadata Editor

        function buildMainInfo() {
            return new BEM({
                block: "imcms-image-main-info",
                elements: {
                    "image": $("<img>").attr({
                        src: imageDTO.src
                    }),
                    "name": components.texts.titleText('<div>', imageDTO.name),
                    "type": buildImageInfoField(texts.originalFileType, imageDTO.format),
                    "resolution": buildImageInfoField(texts.resolution, imageDTO.resolution),
                    "size": buildImageInfoField(texts.originalFileSize, imageDTO.size),
                    "date": buildImageInfoField(texts.modifiedDate, imageDTO.uploaded)
                }
            }).buildBlockStructure("<div>");
        }

        function buildImageInfoField(title, value){
            return new BEM({
                block: "imcms-info-field",
                elements: {
                    "name": components.texts.titleText('<div>', title),
                    "value": components.texts.titleText('<div>', value)
                }
            }).buildBlockStructure("<div>");
        }

        function buildMetadataEditor() {
            metadataEditorWindowData.$photographer = components.texts.textField('<div>', {
                text: texts.photographer,
                value: imageDTO.exifInfo.customExif.photographer
            });

            metadataEditorWindowData.$uploadedBy = components.texts.textField('<div>', {
                text: texts.uploadedBy,
                value: imageDTO.exifInfo.customExif.uploadedBy
            });

            metadataEditorWindowData.$copyright = components.texts.textField('<div>', {
                text: texts.copyright,
                value: imageDTO.exifInfo.customExif.copyright
            });

            metadataEditorWindowData.$altText = components.texts.textField('<div>', {
                text: texts.altText,
                value: imageDTO.exifInfo.customExif.alternateText
            });

            metadataEditorWindowData.$descriptionText = components.texts.textField('<div>', {
                text: texts.descriptionText,
                value: imageDTO.exifInfo.customExif.descriptionText
            });

            return new BEM({
                block: "imcms-image-metadata",
                elements: {
                    "photographer": metadataEditorWindowData.$photographer,
                    "uploaded-by": metadataEditorWindowData.$uploadedBy,
                    "copyright": metadataEditorWindowData.$copyright,
                    "alt-text": metadataEditorWindowData.$altText,
                    "description-text": metadataEditorWindowData.$descriptionText,
                    "license-period": buildLicensePeriodField()
                }
            }).buildBlockStructure("<div>");
        }

        function buildLicensePeriodField(){
            metadataEditorWindowData.$dateStart = components.dateTime.datePickerCalendar();
            metadataEditorWindowData.$dateEnd = components.dateTime.datePickerCalendar();
            let $delimiter = components.texts.titleText('<div>', "—", {
                'class': 'imcms-delimeter'
            });

            metadataEditorWindowData.$dateStart.setDate(imageDTO.exifInfo.customExif.licensePeriodStart);
            metadataEditorWindowData.$dateEnd.setDate(imageDTO.exifInfo.customExif.licensePeriodEnd);

            return new BEM({
                block: "imcms-license-period",
                elements: {
                    "title": components.texts.titleText("<div>", texts.licensePeriod, {
                        'class': 'imcms-label'
                    }),
                    "range": $('<div>', {
                        html: [metadataEditorWindowData.$dateStart, $delimiter, metadataEditorWindowData.$dateEnd]
                    })
                }
            }).buildBlockStructure("<div>", {"class": "imcms-field"});
        }

        function buildMetadataEditorFooter() {
            return WindowBuilder.buildFooter([
                components.buttons.negativeButton({
                    text: texts.cancel,
                    click: onCancel
                }),
                components.buttons.saveButton({
                    text: texts.save,
                    click: onSave
                })
            ]);
        }

        function onCancel() {
            imageMetadataWindowBuilder.closeWindow();
        }

        function onSave(){
            let path = imageDTO.path;
            let data = {
                photographer: metadataEditorWindowData.$photographer.$input.val(),
                uploadedBy: metadataEditorWindowData.$uploadedBy.$input.val(),
                copyright: metadataEditorWindowData.$copyright.$input.val(),
                alternateText: metadataEditorWindowData.$altText.$input.val(),
                descriptionText: metadataEditorWindowData.$descriptionText.$input.val(),
                licensePeriodStart: metadataEditorWindowData.$dateStart.getDate(),
                licensePeriodEnd: metadataEditorWindowData.$dateEnd.getDate()
            }

            imageFilesRestApi.editMetadata(path, data)
                .done((savedImage) => {
                    onMetadataSaved(savedImage);
                    imageMetadataWindowBuilder.closeWindow();
                })
                .fail(() => {
                    modal.buildErrorWindow(texts.error.saveMetadataFailed);
                });
        }

        function buildImageMetadataWindow() {
            return new BEM({
                block: "imcms-pop-up-modal",
                elements: {
                    "head": imageMetadataWindowBuilder.buildHead(texts.titleMetadataEditor, () => imageMetadataWindowBuilder.closeWindow()),
                    "left-side": buildMainInfo(),
                    "right-side": buildMetadataEditor(),
                    "footer": buildMetadataEditorFooter()
                }
            }).buildBlockStructure("<div>", {"class": "imcms-image-metadata-editor"});
        }

        function clearData() {
            metadataEditorWindowData = {};
            exifInfoWindowData = {};
        }

        let imageMetadataWindowBuilder = new WindowBuilder({
            factory: buildImageMetadataWindow,
            clearDataStrategy: clearData,
            onEscKeyPressed: "close"
        });

        let imageExifWindowBuilder = new WindowBuilder({
            factory: buildImageExifInfoWindow,
            clearDataStrategy: clearData,
            onEscKeyPressed: "close"
        });

        return {
            buildImageMetadataEditor: function (imageData, onMetadataSavedCallback) {
                onMetadataSaved = onMetadataSavedCallback;
                imageDTO = imageData;
                imageMetadataWindowBuilder.buildWindowWithShadow();
            },
            buildImageExifInfo: function (imageData) {
                imageDTO = imageData;
                imageExifWindowBuilder.buildWindowWithShadow();
            }
        };
    }
);
