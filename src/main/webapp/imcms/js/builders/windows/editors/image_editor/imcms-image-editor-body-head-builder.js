const imageProportionsLocker = require('imcms-image-proportions-locker-button');

define(
    "imcms-image-editor-body-head-builder",
    [
        "imcms-i18n-texts", "imcms-bem-builder", "imcms-components-builder", "jquery", 'imcms-image-edit-size-controls',
        "imcms-image-rotate", "imcms-image-resize", 'imcms-editable-image', 'imcms-preview-image-area',
        'imcms-toolbar-view-builder', 'imcms-image-cropper', 'imcms-editable-area',
        'imcms-image-percentage-proportion-build', 'imcms-image-active-tab'
    ],
    function (texts, BEM, components, $, imageEditSizeControls, imageRotate, imageResize, editableImage, previewImageArea,
              ToolbarViewBuilder, cropper, editableImageArea, percentagePropBuild, checkActiveTab) {

        texts = texts.editors.image;
        let imageData;

        const toggleImageAreaToolbarViewBuilder = new ToolbarViewBuilder()
            .hide(
                getShowImageRotationControls(),
                getRevertButton(),
                getCroppingButton()
            )
            .show(
                getZoomPlusButton(),
                getZoomMinusButton(),
                getPercentageRatio(),
                getZoomResetButton(),
                getFitButton()
            )
            .originControlSizeShow(imageEditSizeControls.getOriginSizeControls())
            .prevControlSizeHide(imageEditSizeControls.getEditSizeControls());

        function toggleImgArea() {
            const $previewImageArea = previewImageArea.getPreviewImageArea();
            const $controlTabs = $(".imcms-editable-img-control-tabs__tab");
            const $editableArea = editableImageArea.getEditableImageArea();//todo rename all modules on the origin instead edit
            const $exifInfoButton = $('.imcms-image_editor__right-side').find(`[name='exifInfo']`);
            if ($(this).data("tab") === "prev") {
                toggleImageAreaToolbarViewBuilder.buildEditorElement();

                const prevWidth = imageResize.getPreviewWidth();
                const prevHeight = imageResize.getPreviewHeight();

                if (prevWidth > 0 && prevHeight > 0) {
                    percentagePropBuild.buildPercentageImage(prevWidth, prevHeight, $percentageRatio);
                }

                $exifInfoButton.css({
                    'display': 'none'
                });

                $editableArea.css({
                    "z-index": "10",
                    'display': "none"
                });

                $previewImageArea.css({
                    "z-index": "50",
                    "display": "block"
                });
            } else {
                const width = imageResize.getWidth();
                const height = imageResize.getHeight();
                imageEditSizeControls.setWidth(width, true);
                imageEditSizeControls.setHeight(height, true);
                toggleImageAreaToolbarViewBuilder.build();
                $exifInfoButton.show();
                if (imageData.path !== '') {
                    percentagePropBuild.buildPercentageImage(width, height, $percentageRatio);
                    if (imageData.exifInfo && imageData.exifInfo.length !== 0) {
                        $exifInfoButton.removeAttr('disabled').removeClass('imcms-button--disabled');
                    } else {
                        $exifInfoButton.addClass('imcms-button--disabled').attr('disabled', '');
                    }

                } else {
                    $exifInfoButton.hide();
                }
                $previewImageArea.css({
                    "z-index": "10",
                    "display": "none"
                });

                $editableArea.css({
                    "z-index": "50",
                    'display': "block"
                });
            }

            $controlTabs.removeClass("imcms-editable-img-control-tabs__tab--active");
            $(this).addClass("imcms-editable-img-control-tabs__tab--active");
        }

        function showHidePanel(panelOpts) {
            const panelAnimationOpts = {};

            if (panelOpts.$btn.data("state")) {
                panelAnimationOpts[panelOpts.panelSide] = "-" + panelOpts.newPanelSideValue + "px";
                panelOpts.$panel.animate(panelAnimationOpts, 300);
                panelOpts.$btn.data("state", false);
                panelOpts.$btn.text(panelOpts.textShow);

            } else {
                panelAnimationOpts[panelOpts.panelSide] = 0;
                panelOpts.$panel.animate(panelAnimationOpts, 300);
                panelOpts.$btn.data("state", true);
                panelOpts.$btn.text(panelOpts.textHide);
            }
        }

        function showHideRightPanel($rightSidePanel) {
            showHidePanel({
                $btn: $(this),
                newPanelSideValue: $rightSidePanel.width(),
                $panel: $rightSidePanel,
                panelSide: "right",
                textHide: texts.panels.right.hide,
                textShow: texts.panels.right.show
            });
        }

        function zoom(delta) {
            if ($('.imcms-editable-img-control-tabs__tab--active').data('tab') === "prev") {
                const $previewArea = previewImageArea.getPreviewImage();

                if (!delta) {
                    $previewArea.css('zoom', 1);
                    return;
                }

                const currentZoom = parseFloat($previewArea.css('zoom'));
                $previewArea.css('zoom', currentZoom + delta);

            } else {
                const $originArea = editableImage.getImage();

                if (!delta) {
                    $originArea.css('zoom', 1);
                    return;
                }

                const currentZoom = parseFloat($originArea.css('zoom'));
                $originArea.css('zoom', currentZoom + delta);
            }
        }

        function zoomPlus() {
            zoom(+0.5);
        }

        function zoomMinus() {
            zoom(-0.5);
        }

        function zoomFit() {
            zoom(0);
        }

        function revertToPreviewImageChanges() {
            imageData.cropRegion = {
                cropX1: 0,
                cropX2: 0,
                cropY1: 0,
                cropY2: 0,
            };
            imageRotate.rotateImage("NORTH");
            zoomFit();
            imageResize.resetToPreview(imageData);
        }

        function revertToOriginalImageChanges() {
            imageData.cropRegion = {
                cropX1: 0,
                cropX2: 0,
                cropY1: 0,
                cropY2: 0,
            };
            imageRotate.rotateImage("NORTH");
            zoomFit();
            imageResize.resetToOriginal(imageData);
        }

        function buildFitImage() {
            const $previewArea = previewImageArea.getPreviewImageArea();
            const clientPreviewAreaWidth = parseInt($previewArea[0].offsetWidth);
            const clientPreviewAreaHeight = parseInt($previewArea[0].offsetHeight);

            if (checkActiveTab.currentActiveTab() === 'prev') {
                setStrictWidthHeightCurrentImage(false, clientPreviewAreaWidth, clientPreviewAreaHeight);
            } else {
                setStrictWidthHeightCurrentImage(true, clientPreviewAreaWidth, clientPreviewAreaHeight);
            }
        }

        function setStrictWidthHeightCurrentImage(isOriginal, clientPreviewAreaWidth, clientPreviewAreaHeight) {
            if (imageData.width >= clientPreviewAreaWidth) {
                imageResize.setWidthStrict(0, clientPreviewAreaWidth - 30, isOriginal, true);
            } else if (imageData.height >= clientPreviewAreaHeight) {
                imageResize.setHeightStrict(0, clientPreviewAreaHeight - 30, isOriginal, true);
            }
        }

        let $switchViewControls;

        function getSwitchViewControls() {
            return $switchViewControls || ($switchViewControls = buildSwitchViewControls())
        }

        let $tabOriginal;
        let $tabPreview;

        function buildSwitchViewControls() {
            $tabPreview = components.texts.titleText("<div>", texts.preview, {
                "data-tab": "prev",
                click: toggleImgArea
            });
            $tabOriginal = components.texts.titleText("<div>", texts.original, {
                "data-tab": "origin",
                click: toggleImgArea
            });
            $tabPreview.modifiers = ["active"];

            return new BEM({
                block: "imcms-editable-img-control-tabs",
                elements: {
                    "tab": [$tabOriginal, $tabPreview]
                }
            }).buildBlockStructure("<div>");
        }

        function wrapWithNoOpIfNoImageYet(wrapMe) {
            return function () {
                if (imageData && imageData.path) wrapMe.apply(this, arguments)
            };
        }

        function onCancel() {
            ToolbarViewBuilder.getCurrentToolbarView().cancelChanges();
        }

        function onApply() {
            ToolbarViewBuilder.getCurrentToolbarView().applyChanges();
        }

        function onRotationActivated() {
            let previousRotateDegrees = parseInt(
                previewImageArea.getPreviewImage()[0].style.transform.split(' ')[0].replace(/\D/g, '')
            );

            new ToolbarViewBuilder()
                .hide(
                    getShowImageRotationControls(),
                    imageEditSizeControls.getEditSizeControls(),
                    getRevertButton(),
                    getCroppingButton(),
                    getSwitchViewControls(),
                )
                .show(
                    getCancelChangesButton(),
                    getApplyChangesButton(),
                    getRotateLeftButton(),
                    getRotateRightButton(),
                )
                .onCancel(() => imageRotate.rotateImageByDegrees(previousRotateDegrees))
                .build();
        }

        function getCroppingProportionsInfo() {
            return components.texts.infoText(
                '<div>',
                `${imageResize.isProportionsLockedByStyle() ? texts.presetCrop : texts.crop}: ${imageResize.getPreviewWidth()} x ${imageResize.getPreviewHeight()}`,
                {
                    'class': 'imcms-image-crop-proportions-info',
                    style: 'display: block;'
                }
            );
        }

        let $cancelChangesButton;

        function getCancelChangesButton() {
            return $cancelChangesButton || ($cancelChangesButton = components.buttons.negativeButton({
                text: texts.buttons.cancelText,
                title: texts.buttons.cancelTitle,
                click: onCancel,
                style: 'display: none;',
            }));
        }

        let $applyChangesButton;

        function getApplyChangesButton() {
            return $applyChangesButton || ($applyChangesButton = components.buttons.saveButton({
                text: texts.buttons.applyChangeText,
                title: texts.buttons.applyChangeTitle,
                click: onApply,
                style: 'display: none;',
            }));
        }

        let $removeCroppingButton;

        function getRemoveCroppingButton() {
            return $removeCroppingButton || ($removeCroppingButton = components.buttons.negativeButton({
                text: texts.buttons.removeCropping,
                style: 'display: none;',
                click: () => cropper.initImageCropper(imageData),
            }));
        }

        let $showImageRotationControls;

        function getShowImageRotationControls() {
            return $showImageRotationControls || ($showImageRotationControls = components.buttons.rotationButton({
                title: texts.buttons.rotationTitle,
                click: onRotationActivated,
            }));
        }

        let $zoomPlusButton;

        function getZoomPlusButton() {
            return $zoomPlusButton || ($zoomPlusButton = components.buttons.zoomPlusButton({
                title: texts.buttons.zoomIn,
                click: wrapWithNoOpIfNoImageYet(zoomPlus),
                style: 'display: none;',
            }));
        }

        let $zoomMinusButton;

        function getZoomMinusButton() {
            return $zoomMinusButton || ($zoomMinusButton = components.buttons.zoomMinusButton({
                title: texts.buttons.zoomOut,
                click: wrapWithNoOpIfNoImageYet(zoomMinus),
                style: 'display: none;',
            }))
        }

        let $fitButton;

        function getFitButton() {
            return $fitButton || ($fitButton = components.buttons.fitButton({
                title: 'change that on bootstrap',
                click: buildFitImage,
                style: 'display: none;'
            }))
        }

        let $zoomResetButton;

        function getZoomResetButton() {
            return $zoomResetButton || ($zoomResetButton = components.buttons.zoomResetButton({
                title: texts.buttons.reset,
                style: 'display: none;',
                click: wrapWithNoOpIfNoImageYet(revertToOriginalImageChanges),
            }))
        }

        let $rotateLeftButton;

        function getRotateLeftButton() {
            return $rotateLeftButton || ($rotateLeftButton = components.buttons.rotateLeftButton({
                title: texts.buttons.rotateLeft,
                click: wrapWithNoOpIfNoImageYet(imageRotate.rotateLeft),
                style: 'display: none;',
            }))
        }

        let $rotateRightButton;

        function getRotateRightButton() {
            return $rotateRightButton || ($rotateRightButton = components.buttons.rotateRightButton({
                title: texts.buttons.rotateRight,
                click: wrapWithNoOpIfNoImageYet(imageRotate.rotateRight),
                style: 'display: none;',
            }))
        }

        let $revertButton;

        function getRevertButton() {
            return $revertButton || ($revertButton = components.buttons.revertButton({
                title: texts.buttons.revert,
                click: wrapWithNoOpIfNoImageYet(revertToPreviewImageChanges),
            }))
        }

        function showCroppingStuff() {
            cropper.initImageCropper(imageData);
            const $croppingProportionsInfo = getCroppingProportionsInfo();
            $croppingProportionsInfo.insertAfter(getApplyChangesButton());

            new ToolbarViewBuilder()
                .hide(
                    getShowImageRotationControls(),
                    imageEditSizeControls.getEditSizeControls(),
                    getRevertButton(),
                    getCroppingButton(),
                    getSwitchViewControls(),
                )
                .show(
                    getCancelChangesButton(),
                    imageProportionsLocker.getProportionsButton(),
                    imageProportionsLocker.getProportionsText(),
                    getRemoveCroppingButton(),
                    getApplyChangesButton(),
                )
                .onCancel(() => {
                    cropper.destroyImageCropper();
                    $croppingProportionsInfo.remove();
                })
                .onApply(() => {
                    $croppingProportionsInfo.remove();
                    cropper.applyCropping();
                    cropper.destroyImageCropper();
                })
                .build();

            $croppingProportionsInfo.css('display', 'inline-block');
        }

        let $croppingButton;

        function getCroppingButton() {
            return $croppingButton || ($croppingButton = components.buttons.croppingButton({
                title: texts.buttons.cropping,
                click: showCroppingStuff,
            }))
        }

        let $percentageRatio;

        function getPercentageRatio() {
            return $percentageRatio || ($percentageRatio = $('<div>', {
                'class': 'percentage-image-info',
                title: 'bootstrap percentage',
            }));
        }

        let $scaleAndRotateControls;

        function getScaleAndRotateControls() {
            return $scaleAndRotateControls || ($scaleAndRotateControls = new BEM({
                block: "imcms-edit-image",
                elements: {
                    'size-place': [
                        imageEditSizeControls.getEditSizeControls(),
                        imageEditSizeControls.getOriginSizeControls(),
                    ],
                    "button": [
                        imageProportionsLocker.getProportionsButton(),
                        imageProportionsLocker.getProportionsText(),
                        getRemoveCroppingButton(),
                        getZoomPlusButton(),
                        getZoomMinusButton(),
                        getPercentageRatio(),
                        getZoomResetButton(),
                        getFitButton(),
                        getShowImageRotationControls(),
                        getRotateLeftButton(),
                        getRotateRightButton(),
                        getCroppingButton(),
                        getRevertButton(),
                    ],
                }
            }).buildBlockStructure("<div>"))
        }

        function buildToolbar() {

            return new BEM({
                block: "imcms-editable-img-controls",
                elements: {
                    'control-button': '',
                    "control-scale-n-rotate": '',
                    "control-view": '',
                }
            }).buildBlock("<div>", [
                {'control-button': getCancelChangesButton()},
                {"control-scale-n-rotate": getScaleAndRotateControls()},
                {"control-button": getApplyChangesButton()},
                {"control-view": getSwitchViewControls()}
            ]);
        }

        const $imgUrl = $("<a>");

        module.exports = {
            showOriginalImageArea: () => toggleImgArea.call($tabOriginal),

            showPreviewImageArea: () => toggleImgArea.call($tabPreview),

            build: function ($rightSidePanel, _imageData) {
                imageData = _imageData;

                const bodyHeadBEM = new BEM({
                    block: "imcms-image-toolbar",
                    elements: {
                        "button": "imcms-image-characteristic",
                        "img-title": "imcms-title imcms-image-characteristic",
                        "img-url": "imcms-title imcms-image-characteristic",
                        "img-origin-size": "imcms-title imcms-image-characteristic"
                    }
                });

                const $showHideRightPanelBtn = components.buttons.neutralButton({
                    "class": "imcms-image-characteristic",
                    text: texts.panels.right.show,
                    click: function () {
                        showHideRightPanel.call(this, $rightSidePanel);
                    }
                });

                return bodyHeadBEM.buildBlock("<div>", [
                    {
                        "toolbar": buildToolbar()
                    }, {
                        "button": $showHideRightPanelBtn,
                        modifiers: ["right-panel"]
                    }
                ]);
            },
            getImageUrl: () => $imgUrl,
            getImagePath: () => $imgUrl.attr('data-path'),

            clearData() {
                [
                    imageEditSizeControls.getEditSizeControls(),
                    imageEditSizeControls.getOriginSizeControls(),
                    getCancelChangesButton(),
                    imageProportionsLocker.getProportionsButton(),
                    imageProportionsLocker.getProportionsText(),
                    getApplyChangesButton(),
                    getRemoveCroppingButton(),
                    getRotateLeftButton(),
                    getRotateRightButton(),
                    getPercentageRatio(),
                    getZoomPlusButton(),
                    getZoomMinusButton(),
                    getZoomResetButton(),
                    getFitButton(),

                ].forEach($elem => $elem.hide());

                $('.imcms-image-crop-proportions-info').remove();

                getSwitchViewControls().show();

                previewImageArea.clearData();

                imageProportionsLocker.enableProportionsLock();

                cropper.destroyImageCropper();

            },
        }
    }
);
