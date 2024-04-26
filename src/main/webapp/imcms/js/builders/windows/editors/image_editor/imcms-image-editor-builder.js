/**
 * Created by Serhii Maksymchuk from Ubrainians for imCode
 * 21.08.17
 */
define(
    "imcms-image-editor-builder",
    [
        "imcms-window-builder", "imcms-images-rest-api", "jquery", "imcms-events", "imcms", "imcms-image-rotate",
        "imcms-image-editor-factory", 'imcms-originally-image', 'imcms-image-editor-body-head-builder',
        'imcms-image-resize', 'imcms-image-edit-size-controls', "imcms-modal-window-builder", "imcms-i18n-texts",
        'imcms-preview-image-area', 'imcms-bem-builder',
        'imcms-components-builder', 'imcms-image-zoom', 'imcms-image-content-builder'
    ],
    function (WindowBuilder, imageRestApi, $, events, imcms, imageRotate, imageEditorFactory, originalImage,
              bodyHeadBuilder, imageResize, editSizeControls, modal, texts, prevImageArea, BEM, components,
              imageZoom, imageContentBuilder) {

        texts = texts.editors.image;

        const imageDataContainers = {};
        const imageData = {};

        function buildEditor() {
            return imageEditorFactory.buildEditor({
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

            const $imgUrl = bodyHeadBuilder.getImageUrl();
            $imgUrl.text(titleText);
            $imgUrl.attr('href', `/images${(imageData.path.startsWith('/') ? '' : '/') + imageData.path}`);
            $imgUrl.attr('target', '_blank');
            $imgUrl.attr('data-path', imageData.path);
            $imgUrl.attr("title", imageData.path);
        }

        function initSize(imageData, isOriginalImage) {
            const cropRegion = imageData.cropRegion;
            const image = isOriginalImage ? imageResize.getOriginal() : imageResize.getPreview();
            imageResize.enableResetToOriginalFlag();
            //something strange happens when selected img from folder, cropRegion has crop coordinates..
            if (imageResize.getSelectedImgFlagValue()) {
                imageData.cropRegion = {
                    cropX1: 0,
                    cropX2: 0,
                    cropY1: 0,
                    cropY2: 0,
                };
                imageData.rotateDirection = 'NORTH';
            }

            imageResize.checkCropRegionExist(imageData);
            if (cropRegion
                && (cropRegion.cropX1 >= 0)
                && (cropRegion.cropX2 >= 1)
                && (cropRegion.cropY1 >= 0)
                && (cropRegion.cropY2 >= 1)) {
                const width = cropRegion.cropX2 - cropRegion.cropX1;
                const height = cropRegion.cropY2 - cropRegion.cropY1;

                imageResize.setCurrentSize(width, height);
                imageResize.setWidthStrict(cropRegion.cropX1, width, isOriginalImage);
                imageResize.setHeightStrict(cropRegion.cropY1, height, isOriginalImage);

                imageResize.setWidth(imageData.width, isOriginalImage);
                imageResize.setHeight(imageData.height, isOriginalImage);
            } else {
                imageResize.setWidthStrict(0, image.width, isOriginalImage);
                imageResize.setHeightStrict(0, image.height, isOriginalImage);
                imageResize.updateSizing(imageData, true, isOriginalImage);
            }

            if (!isOriginalImage) {
                $('.imcms-image_editor__right-side')
                    .find('.imcms-info-edit-image')
                    .append(editSizeControls.getImageSizeControlBlock().show());
                imageRotate.rotateImage(imageData.rotateDirection);
            }

            imageResize.disabledResetToOriginalFlag();
            imageZoom.fitImage();
        }

        function fillLeftSideData(imageData) {
            if (!imageData.path) return;

            prevImageArea.getPreviewImage().hide();

            originalImage.setImageSource(imageData, () => {
                initSize(imageData, true);
                //load preview image only when original is ready due to recursion in imcms-image-resize => updateSizing()
                prevImageArea.setPreviewImageSource(imageData, onPreviewImageLoad);
            });
        }

        function onPreviewImageLoad () {
            const style = $tag.data('style');
            const resultStyleObj = {};

            editSizeControls.getDisplaySizeBlock().css('display', 'none');
            if (style) {
                style.split(';')
                    .map(x => x.trim())
                    .filter(x => !!x)
                    .forEach(x => {
                        const styleKeyAndValue = x.split(':').map(x => x.trim());
                        resultStyleObj[styleKeyAndValue[0]] = styleKeyAndValue[1];
                    });

                let maxWidth = resultStyleObj['max-width'];

                if (maxWidth && !isNaN(maxWidth = parseInt(maxWidth, 10))) {
                    imageData.width = Math.min(imageData.width, maxWidth);
                    imageResize.setMaxWidth(maxWidth);
                }

                let maxHeight = resultStyleObj['max-height'];

                if (maxHeight && !isNaN(maxHeight = parseInt(maxHeight, 10))) {
                    imageData.height = Math.min(imageData.height, maxHeight);
                    imageResize.setMaxHeight(maxHeight);
                }

                let width = parseInt(resultStyleObj.width, 10);
                if (width) {
                    imageData.width = width;
                    imageResize.setMaxWidth(width);
                    imageResize.setMinWidth(width);
                    editSizeControls.getWantedWidthControl().getInput().attr('disabled', 'disabled');
                }

                let height = parseInt(resultStyleObj.height, 10);
                if (height) {
                    imageData.height = height;
                    imageResize.setMaxHeight(height);
                    imageResize.setMinHeight(height);
                    editSizeControls.getWantedHeightControl().getInput().attr('disabled', 'disabled');
                }

                if (imageResize.isProportionsLockedByStyle()) {
                    imageResize.setCurrentPreviewSize(imageData.width, imageData.height);
                    imageResize.setFinalPreviewImageData(imageData);
                }

                if (maxWidth || maxHeight || height || width) {
                    editSizeControls.getDisplaySizeBlock().css('display', 'flex');
                }
            }

            initSize(imageData, false);
            imageResize.disabledSelectedImageFlag();
            prevImageArea.getPreviewImage().show();
        }

        function fillData(image) {
            clearComponents();

            if (!image) return;

            bodyHeadBuilder.showPreviewImageArea();
	        imageContentBuilder.isSelectedImageChanged() ? imageData.path = image.path : $.extend(imageData, image);

            if (imageData.inText) $tag.attr("data-index", imageData.index);

            imageEditorFactory.updateImageData($tag, imageData);

            if(image.path !== '' && imageData.height > 0 && imageData.width > 0){
                editSizeControls.getImageSizeControlBlock().show();
            }else{
                imageDataContainers.$langFlags.show();
                editSizeControls.getImageSizeControlBlock().hide();
            }

            fillBodyHeadData(imageData);
            fillLeftSideData(imageData);

            imageDataContainers.$altText.$input.val(imageData.alternateText);
            if (imageData.alternateText.trim() === '' && imageData.exifInfo){
                imageDataContainers.$altText.$input.val(imageData.exifInfo.customExif.alternateText);
            }

            imageDataContainers.$descriptionText.$input.val(imageData.descriptionText);
            if (imageData.descriptionText.trim() === '' && imageData.exifInfo){
                imageDataContainers.$descriptionText.$input.val(imageData.exifInfo.customExif.descriptionText);
            }

            imageDataContainers.$imgLink.$input.val(image.linkUrl);

            imageDataContainers.$allLanguagesCheckBox.find("input")
                .prop('checked', image.allLanguages === undefined ? true : image.allLanguages);
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

            if (opts.inText) {
                imageDataContainers.$langFlags.hideLangFlagsAndCheckbox();
            } else {
                imageDataContainers.$langFlags.showLangFlagsAndCheckbox();
                imageDataContainers.$langFlags.setActive(imcms.language.code);
            }

            imageRestApi.read(opts)
                .done(fillData)
                .fail(() => modal.buildErrorWindow(texts.error.loadFailed));
        }

        function clearComponents() {
            bodyHeadBuilder.clearData();
            originalImage.clearData();
            imageResize.clearData();
            imageRotate.destroy();
        }

        function clearData() {
            clearComponents();
            events.trigger("enable text editor blur");
            editSizeControls.getPreviewWidthControl().getInput().val('');
            editSizeControls.getPreviewHeightControl().getInput().val('');
            editSizeControls.getWantedWidthControl().getInput().removeAttr('disabled').val('');
            editSizeControls.getWantedHeightControl().getInput().removeAttr('disabled').val('');
            $('.percentage-image-info').text('');
        }

        var imageWindowBuilder = new WindowBuilder({
            factory: buildEditor,
            loadDataStrategy: loadData,
            clearDataStrategy: clearData,
            onEscKeyPressed: "close",
            onEnterKeyPressed: () => {
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
                imageWindowBuilder.buildWindow.apply(imageWindowBuilder, arguments);
            }
        };
    }
);
