/**
 * Created by Serhii Maksymchuk from Ubrainians for imCode
 * 21.08.17
 */
define(
    "imcms-image-editor-builder",
    [
        "imcms-window-builder", "imcms-images-rest-api", "jquery", "imcms-events", "imcms", "imcms-image-rotate",
        "imcms-image-editor-factory", 'imcms-editable-image', 'imcms-image-editor-body-head-builder',
        'imcms-image-resize'
    ],
    function (WindowBuilder, imageRestApi, $, events, imcms, imageRotate, imageEditorFactory, editableImage,
              bodyHeadBuilder, imageResize) {

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
            $imgUrl.attr('href', '/images' + (imageData.path.startsWith('/') ? '' : '/') + imageData.path);
            $imgUrl.attr('target', '_blank');
            $imgUrl.attr('data-path', imageData.path);
            $imgUrl.attr("title", imageData.path);
        }

        function fillLeftSideData(imageData) {
            if (!imageData.path) return;

            editableImage.setImageSource(imageData.path);

            setTimeout(function () { // to let image src load
                const style = $tag.data('style');
                const resultStyleObj = {};

                imageResize.setWidth(imageData.width);
                imageResize.setHeight(imageData.height);

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

                imageRotate.rotateImage(imageData.rotateDirection);

            }, 200);
        }

        function fillData(image) {

            if (!image) {
                return;
            }

            bodyHeadBuilder.showOriginalImageArea();

            // direct reassign because $.extend skip 'undefined' but it's needed!
            imageData.cropRegion = {//image.cropRegion;
                cropX1: 0,
                cropX2: 0,
                cropY1: 0,
                cropY2: 0,
            };
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
            editableImage.clearData();
            events.trigger("enable text editor blur");
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
