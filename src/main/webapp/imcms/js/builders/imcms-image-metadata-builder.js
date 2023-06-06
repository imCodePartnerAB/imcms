define("imcms-image-metadata-builder",
    [
        "imcms-window-builder", "imcms-bem-builder", "imcms-components-builder", "jquery", "imcms-i18n-texts", "imcms-image-files-rest-api", "imcms-modal-window-builder"
    ],
    function (WindowBuilder, BEM, components, $, texts, imageFilesRestApi, modal) {

        texts = texts.editors.content.imageMetadata;

        let imageDTO;
        let onMetadataSaved;

        let windowData = {};

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
            windowData.$photographer = components.texts.textField('<div>', {
                text: texts.photographer,
                value: imageDTO.exifInfo.customExif.photographer
            });

            windowData.$uploadedBy = components.texts.textField('<div>', {
                text: texts.uploadedBy,
                value: imageDTO.exifInfo.customExif.uploadedBy
            });

            windowData.$copyright = components.texts.textField('<div>', {
                text: texts.copyright,
                value: imageDTO.exifInfo.customExif.copyright
            });

            return new BEM({
                block: "imcms-image-metadata",
                elements: {
                    "photographer": windowData.$photographer,
                    "uploaded-by": windowData.$uploadedBy,
                    "copyright": windowData.$copyright,
                    "license-period": buildLicensePeriodField()
                }
            }).buildBlockStructure("<div>");
        }

        function buildLicensePeriodField(){
            windowData.$dateStart = components.dateTime.datePickerCalendar();
            windowData.$dateEnd = components.dateTime.datePickerCalendar();
            let $delimiter = components.texts.titleText('<div>', "—", {
                'class': 'imcms-delimeter'
            });

            windowData.$dateStart.setDate(imageDTO.exifInfo.customExif.licensePeriodStart);
            windowData.$dateEnd.setDate(imageDTO.exifInfo.customExif.licensePeriodEnd);

            return new BEM({
                block: "imcms-license-period",
                elements: {
                    "title": components.texts.titleText("<div>", texts.licensePeriod, {
                        'class': 'imcms-label'
                    }),
                    "range": $('<div>', {
                        html: [windowData.$dateStart, $delimiter, windowData.$dateEnd]
                    })
                }
            }).buildBlockStructure("<div>", {"class": "imcms-field"});
        }

        function buildFooter() {
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
                photographer: windowData.$photographer.$input.val(),
                uploadedBy: windowData.$uploadedBy.$input.val(),
                copyright: windowData.$copyright.$input.val(),
                licensePeriodStart: windowData.$dateStart.getDate(),
                licensePeriodEnd: windowData.$dateEnd.getDate()
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

        function buildImageMetadata() {
            return new BEM({
                block: "imcms-pop-up-modal",
                elements: {
                    "head": imageMetadataWindowBuilder.buildHead(texts.title),
                    "left-side": buildMainInfo(),
                    "right-side": buildMetadataEditor(),
                    "footer": buildFooter()
                }
            }).buildBlockStructure("<div>", {"class": "imcms-image-metadata-editor"});
        }

        function clearData() {
            windowData = {};
        }

        let imageMetadataWindowBuilder = new WindowBuilder({
            factory: buildImageMetadata,
            clearDataStrategy: clearData,
            onEscKeyPressed: "close"
        });

        return {
            buildImageMetadata: function (imageData, onMetadataSavedCallback) {
                onMetadataSaved = onMetadataSavedCallback;

                imageDTO = imageData;
                imageMetadataWindowBuilder.buildWindowWithShadow();
            }
        };
    }
);
