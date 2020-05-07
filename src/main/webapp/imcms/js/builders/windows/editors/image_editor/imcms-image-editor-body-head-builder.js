const imageProportionsLocker = require('imcms-image-proportions-locker-button');

define(
    "imcms-image-editor-body-head-builder",
    [
        "imcms-i18n-texts", "imcms-bem-builder", "imcms-components-builder", "jquery", 'imcms-image-edit-size-controls',
        "imcms-image-rotate", "imcms-image-resize", 'imcms-editable-image', 'imcms-preview-image-area',
        'imcms-toolbar-view-builder', 'imcms-image-cropper', 'imcms-editable-area',
        'imcms-image-active-tab', 'imcms-image-zoom'
    ],
    function (texts, BEM, components, $, imageEditSizeControls, imageRotate, imageResize, editableImage, previewImageArea,
              ToolbarViewBuilder, cropper, editableImageArea, checkActiveTab, imageZoom) {

        texts = texts.editors.image;
        let imageData;

        const toggleImageAreaToolbarViewBuilder = new ToolbarViewBuilder()
            .hide(
                getShowImageRotationControls(),
                getRevertButton(),
                getCroppingButton(),
                imageProportionsLocker.getProportionsButton(),
                imageProportionsLocker.getProportionsText(),
            )
            .show(
                getZoomPlusButton(),
                getZoomMinusButton(),
                getPercentageRatio(),
                getZoomResetButton(),
                getFitButton()
            )
            .originControlSizeShow(imageEditSizeControls.getOriginSizeControls());

        function toggleImgArea() {
            const $previewImageArea = previewImageArea.getPreviewImageArea();
            const $controlTabs = $(".imcms-editable-img-control-tabs__tab");
            const $editableArea = editableImageArea.getEditableImageArea();//todo rename all modules on the origin instead edit
            const $exifInfoButton = $('.imcms-image_editor__right-side').find(`[name='exifInfo']`);

            if ($(this).data("tab") === "prev") {
                toggleImageAreaToolbarViewBuilder.buildEditorElement();

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

            imageZoom.updateZoomGradeValue();
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

        function revertToPreviewImageChanges() {
            imageData = JSON.parse(JSON.stringify(imageResize.getFinalPreviewImageData()));
            imageRotate.rotateImage("NORTH");
            imageZoom.resetZoom();
            imageResize.resetToPreview(imageData);
        }

        function revertToOriginalImageChanges() {
            if (checkActiveTab.currentActiveTab() === 'prev') {
                imageData.cropRegion = {
                    cropX1: 0,
                    cropX2: 0,
                    cropY1: 0,
                    cropY2: 0,
                };
                imageRotate.rotateImage("NORTH");
                imageZoom.resetZoom();
                imageResize.resetToOriginal(imageData);
            } else {
                imageData.cropRegion = {
                    cropX1: 0,
                    cropX2: 0,
                    cropY1: 0,
                    cropY2: 0,
                };
                imageRotate.rotateImage("NORTH");
                imageZoom.resetZoom();
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
                `${imageResize.isProportionsLockedByStyle() ? texts.presetCrop : texts.crop}: ${imageResize.getPreviewWidth().toFixed()} x ${imageResize.getPreviewHeight().toFixed()}`,
                {
                    'class': 'imcms-image-crop-proportions-info',
                    style: 'display: block;'
                }
            );
        }

        let $cancelChangesButton;

        function getCancelChangesButton() {
            if ($cancelChangesButton) {
                return $cancelChangesButton;
            }
            $cancelChangesButton = components.buttons.negativeButton({
                text: texts.buttons.cancelText,
                click: onCancel,
                style: 'display: none;',
            });
            components.overlays.defaultTooltip($cancelChangesButton, texts.buttons.cancelTitle);

            return $cancelChangesButton;
        }

        let $applyChangesButton;

        function getApplyChangesButton() {
            if ($applyChangesButton) {
                return $applyChangesButton;
            }
            $applyChangesButton = components.buttons.saveButton({
                text: texts.buttons.applyChangeText,
                click: onApply,
                style: 'display: none;',
            });
            components.overlays.defaultTooltip($applyChangesButton, texts.buttons.applyChangeTitle);

            return $applyChangesButton;
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
            if ($showImageRotationControls) {
                return $showImageRotationControls;
            }
            $showImageRotationControls = components.buttons.rotationButton({
                click: onRotationActivated,
            });
            components.overlays.defaultTooltip($showImageRotationControls, texts.buttons.rotationTitle);

            return $showImageRotationControls;
        }

        let $zoomPlusButton;

        function getZoomPlusButton() {
            if ($zoomPlusButton) {
                return $zoomPlusButton;
            }
            $zoomPlusButton = components.buttons.zoomPlusButton({
                click: wrapWithNoOpIfNoImageYet(imageZoom.zoomPlus),
                style: 'display: none;',
            });
            components.overlays.defaultTooltip($zoomPlusButton, texts.buttons.zoomIn);

            return $zoomPlusButton;
        }

        let $zoomMinusButton;

        function getZoomMinusButton() {
            if ($zoomMinusButton) {
                return $zoomMinusButton;
            }
            $zoomMinusButton = components.buttons.zoomMinusButton({
                click: wrapWithNoOpIfNoImageYet(imageZoom.zoomMinus),
                style: 'display: none;',
            });
            components.overlays.defaultTooltip($zoomMinusButton, texts.buttons.zoomOut);

            return $zoomMinusButton;
        }

        let $fitButton;

        function getFitButton() {
            if ($fitButton) {
                return $fitButton;
            }
            $fitButton = components.buttons.fitButton({
                'class': 'icon-image-fit',
                click: imageZoom.fitImage,
                style: 'display: none;'
            });
            components.overlays.defaultTooltip($fitButton, 'fit');

            return $fitButton;
        }

        let $zoomResetButton;

        function getZoomResetButton() {
            if ($zoomResetButton) {
                return $zoomResetButton;
            }
            $zoomResetButton = components.buttons.zoomResetButton({
                style: 'display: none;',
                click: wrapWithNoOpIfNoImageYet(revertToOriginalImageChanges),
            });
            components.overlays.defaultTooltip($zoomResetButton, texts.buttons.reset);

            return $zoomResetButton;
        }

        let $rotateLeftButton;

        function getRotateLeftButton() {
            if ($rotateLeftButton) {
                return $rotateLeftButton;
            }
            $rotateLeftButton = components.buttons.rotateLeftButton({
                click: wrapWithNoOpIfNoImageYet(imageRotate.rotateLeft),
                style: 'display: none;',
            });
            components.overlays.defaultTooltip($rotateLeftButton, texts.buttons.rotateLeft);

            return $rotateLeftButton;
        }

        let $rotateRightButton;

        function getRotateRightButton() {
            if ($rotateRightButton) {
                return $rotateRightButton;
            }
            $rotateRightButton = components.buttons.rotateRightButton({
                click: wrapWithNoOpIfNoImageYet(imageRotate.rotateRight),
                style: 'display: none;',
            });
            components.overlays.defaultTooltip($rotateRightButton, texts.buttons.rotateRight);

            return $rotateRightButton;
        }

        let $revertButton;

        function getRevertButton() {
            if ($revertButton) {
                return $revertButton;
            }
            $revertButton = components.buttons.revertButton({
                click: wrapWithNoOpIfNoImageYet(revertToPreviewImageChanges),
            });
            components.overlays.defaultTooltip($revertButton, texts.buttons.revert);

            return $revertButton;
        }

        function showCroppingStuff() {
            cropper.initImageCropper(imageData);
            const $croppingProportionsInfo = getCroppingProportionsInfo();
            $croppingProportionsInfo.insertAfter(getApplyChangesButton());

            new ToolbarViewBuilder()
                .hide(
                    getShowImageRotationControls(),
                    getRevertButton(),
                    getCroppingButton(),
                    getSwitchViewControls(),
                )
                .show(
                    getCancelChangesButton(),
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
            if ($croppingButton) {
                return $croppingButton;
            }
            $croppingButton = components.buttons.croppingButton({
                click: showCroppingStuff,
            });
            components.overlays.defaultTooltip($croppingButton, texts.buttons.cropping);

            return $croppingButton;
        }

        let $percentageRatio;

        function getPercentageRatio() {
            if ($percentageRatio) {
                return $percentageRatio;
            }
            $percentageRatio = imageZoom.buildZoomGradeField();

            return $percentageRatio;
        }

        let $scaleAndRotateControls;

        function getScaleAndRotateControls() {
            return $scaleAndRotateControls || ($scaleAndRotateControls = new BEM({
                block: "imcms-edit-image",
                elements: {
                    'size-place': [
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
                    imageEditSizeControls.getImageSizeControlBlock(),
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

                imageZoom.clearData();

            },
        }
    }
);
