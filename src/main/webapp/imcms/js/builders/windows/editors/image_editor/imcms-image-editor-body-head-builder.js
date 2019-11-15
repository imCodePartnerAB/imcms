const originImageHeightBlock = require('imcms-origin-image-height-block');
const originImageWidthBlock = require('imcms-origin-image-width-block');
const imageProportionsLocker = require('imcms-image-proportions-locker-button');

define(
    "imcms-image-editor-body-head-builder",
    [
        "imcms-i18n-texts", "imcms-bem-builder", "imcms-components-builder", "jquery", 'imcms-image-edit-size-controls',
        "imcms-image-rotate", "imcms-image-resize", 'imcms-editable-image', 'imcms-preview-image-area',
        'imcms-toolbar-view-builder', 'imcms-image-cropper'
    ],
    function (texts, BEM, components, $, imageEditSizeControls, imageRotate, imageResize, editableImage, previewImage,
              ToolbarViewBuilder, cropper) {

        texts = texts.editors.image;

        const toggleImageAreaToolbarViewBuilder = new ToolbarViewBuilder()
            .hide(
                getShowImageRotationControls(),
                imageEditSizeControls.getEditSizeControls(),
                getRevertButton(),
                getCroppingButton(),
            )
            .show(
                getZoomPlusButton(),
                getZoomMinusButton(),
                getZoomResetButton(),
            );

        function toggleImgArea() {

            function initPreviewImageArea() {
                const $previewImg = previewImage.getPreviewImage();
                const $editableImg = editableImage.getImage();

                $previewImg.attr('src', $editableImg.attr('src'));
                $previewImg.attr('style', $editableImg.attr('style'));
            }

            const $previewImageArea = previewImage.getPreviewImageArea();
            const $controlTabs = $(".imcms-editable-img-control-tabs__tab");

            if ($(this).data("tab") === "prev") {
                initPreviewImageArea();
                toggleImageAreaToolbarViewBuilder.build();

                $previewImageArea.css({
                    "z-index": "50",
                    "display": "block"
                });
            } else {
                toggleImageAreaToolbarViewBuilder.cancelChanges();

                $previewImageArea.css({
                    "z-index": "10",
                    "display": "none"
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

        function buildHeightWidthBlock() {
            const $heightBlock = new BEM({
                block: "imcms-img-origin-size",
                elements: {
                    "height-title": components.texts.titleText("<span>", "H:"),
                    "height-value": originImageHeightBlock.getContainer(),
                }
            }).buildBlockStructure("<div>");

            const $widthBlock = new BEM({
                block: "imcms-img-origin-size",
                elements: {
                    "width-title": components.texts.titleText("<span>", "W:"),
                    "width-value": originImageWidthBlock.getContainer(),
                }
            }).buildBlockStructure("<div>");

            return new BEM({
                block: "imcms-title imcms-image-characteristic",
                elements: {
                    "origin-size": [$heightBlock, $widthBlock]
                }
            }).buildBlockStructure("<div>", {text: "Orig "});
        }

        function zoom(delta) {
            const $previewArea = previewImage.getPreviewImage();

            if (!delta) {
                $previewArea.css('zoom', 1);
                return;
            }

            const currentZoom = parseFloat($previewArea.css('zoom'));
            $previewArea.css('zoom', currentZoom + delta);
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

        function revertImageChanges() {
            imageData.cropRegion = {
                cropX1: 0,
                cropX2: 0,
                cropY1: 0,
                cropY2: 0,
            };
            imageRotate.rotateImage("NORTH");
            imageResize.resetToOriginal(imageData);
        }

        let $switchViewControls;

        function getSwitchViewControls() {
            return $switchViewControls || ($switchViewControls = buildSwitchViewControls())
        }

        let $tabOriginal;

        function buildSwitchViewControls() {
            const $preview = components.texts.titleText("<div>", texts.preview, {
                "data-tab": "prev",
                click: toggleImgArea
            });
            $tabOriginal = components.texts.titleText("<div>", texts.original, {
                "data-tab": "origin",
                click: toggleImgArea
            });
            $tabOriginal.modifiers = ["active"];

            return new BEM({
                block: "imcms-editable-img-control-tabs",
                elements: {
                    "tab": [$preview, $tabOriginal]
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
                editableImage.getImage()[0].style.transform.split(' ')[0].replace(/\D/g, '')
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
                `${imageResize.isProportionsLockedByStyle() ? texts.presetCrop : texts.crop} format: ${imageResize.getWidth()} x ${imageResize.getHeight()}`,
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

        let $zoomResetButton;

        function getZoomResetButton() {
            return $zoomResetButton || ($zoomResetButton = components.buttons.zoomResetButton({
                title: texts.buttons.zoomReset,
                style: 'display: none;',
                click: wrapWithNoOpIfNoImageYet(zoomFit),
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
                click: wrapWithNoOpIfNoImageYet(revertImageChanges),
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

        let $scaleAndRotateControls;

        function getScaleAndRotateControls() {
            return $scaleAndRotateControls || ($scaleAndRotateControls = new BEM({
                block: "imcms-edit-image",
                elements: {
                    "button": [
                        imageProportionsLocker.getProportionsButton(),
                        imageProportionsLocker.getProportionsText(),
                        getZoomPlusButton(),
                        getZoomMinusButton(),
                        getZoomResetButton(),
                        getShowImageRotationControls(),
                        getRotateLeftButton(),
                        getRotateRightButton(),
                        getCroppingButton(),
                        getRevertButton(),
                    ]
                }
            }).buildBlockStructure("<div>"))
        }

        function buildToolbar() {

            return new BEM({
                block: "imcms-editable-img-controls",
                elements: {
                    "control-size": '',
                    'control-button': '',
                    "control-scale-n-rotate": '',
                    "control-view": '',
                }
            }).buildBlock("<div>", [
                {"control-size": imageEditSizeControls.getEditSizeControls()},
                {'control-button': getCancelChangesButton()},
                {"control-scale-n-rotate": getScaleAndRotateControls()},
                {"control-button": getApplyChangesButton()},
                {"control-view": getSwitchViewControls()}
            ]);
        }

        let $imgUrl;
        let imageData;

        module.exports = {
            showOriginalImageArea: () => toggleImgArea.call($tabOriginal),

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

                const $imgUrlContainer = bodyHeadBEM.buildElement("img-url", "<div>", {
                    text: "Url: "
                }).append($imgUrl = $("<a>"));

                const $heightWidthBlock = buildHeightWidthBlock();

                return bodyHeadBEM.buildBlock("<div>", [
                    {
                        "toolbar": buildToolbar()
                    }, {
                        "button": $showHideRightPanelBtn,
                        modifiers: ["right-panel"]
                    }, {
                        "img-url": $imgUrlContainer
                    }, {
                        "img-origin-size": $heightWidthBlock
                    }
                ]);
            },
            getImageUrl: () => $imgUrl,
            getImagePath: () => $imgUrl.attr('data-path'),

            clearData() {
                [
                    getCancelChangesButton(),
                    imageProportionsLocker.getProportionsButton(),
                    imageProportionsLocker.getProportionsText(),
                    getApplyChangesButton(),
                    getRotateLeftButton(),
                    getRotateRightButton(),
                    getZoomPlusButton(),
                    getZoomMinusButton(),
                    getZoomResetButton(),

                ].forEach($elem => $elem.hide());

                $('.imcms-image-crop-proportions-info').remove();

                getSwitchViewControls().show();

                previewImage.clearData();

                imageProportionsLocker.enableProportionsLock();

                cropper.destroyImageCropper();

            },
        }
    }
);
