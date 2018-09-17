const originImageHeightBlock = require('imcms-origin-image-height-block');
const originImageWidthBlock = require('imcms-origin-image-width-block');

define(
    "imcms-image-editor-body-head-builder",
    [
        "imcms-i18n-texts", "imcms-bem-builder", "imcms-components-builder", "jquery", 'imcms-image-edit-size-controls',
        "imcms-image-rotate", "imcms-image-resize", "imcms-image-crop-angles", "imcms-image-cropping-elements",
        'imcms-editable-image', 'imcms-preview-image-area'
    ],
    function (texts, BEM, components, $, imageEditSizeControls, imageRotate, imageResize, croppingAngles, cropElements,
              editableImage, previewImage) {

        texts = texts.editors.image;

        function toggleImgArea() {

            function initPreviewImageArea() {
                const $previewImg = previewImage.getPreviewImage();
                const $editableImg = editableImage.getImage();
                const imgSrc = $editableImg.attr('src');
                const imgStyle = $editableImg.attr('style');

                $previewImg.attr('src', imgSrc);
                $previewImg.attr('style', imgStyle);
            }

            const $previewImageArea = previewImage.getPreviewImageArea();
            const $controlTabs = $(".imcms-editable-img-control-tabs__tab");

            if ($(this).data("tab") === "prev") {
                initPreviewImageArea();

                $(getShowImageRotationControls())
                    .add(imageEditSizeControls.getEditSizeControls())
                    .add(getRevertButton())
                    .slideUp('fast', () => {
                        $(getZoomPlusButton()).add(getZoomMinusButton())
                            .add(getZoomResetButton())
                            .slideDown();
                    });

                $previewImageArea.css({
                    "z-index": "50",
                    "display": "block"
                });
            } else {
                $(getZoomPlusButton()).add(getZoomMinusButton())
                    .add(getZoomResetButton())
                    .slideUp('fast', () => {
                        $(getShowImageRotationControls())
                            .add(imageEditSizeControls.getEditSizeControls())
                            .add(getRevertButton())
                            .slideDown();
                    });

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
                    "height-value": originImageHeightBlock.getOriginalHeightContainer(),
                }
            }).buildBlockStructure("<div>");

            const $widthBlock = new BEM({
                block: "imcms-img-origin-size",
                elements: {
                    "width-title": components.texts.titleText("<span>", "W:"),
                    "width-value": originImageWidthBlock.getOriginalWidthContainer(),
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
            const $previewImage = previewImage.getPreviewImage();

            if (!delta) {
                $previewImage.css('zoom', 1);
                return;
            }

            const currentZoom = +$previewImage.css('zoom');
            $previewImage.css('zoom', currentZoom + delta);
        }

        function zoomPlus() {
            zoom(+0.1);
        }

        function zoomMinus() {
            zoom(-0.1);
        }

        function zoomFit() {
            zoom(0);
        }

        function revertImageChanges() {
            imageRotate.rotateImage("NORTH");
            imageResize.resetToOriginal();
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

        const onCancel = () => {
            imageRotate.rotateImageByDegrees(previousRotateDegrees);
            onApply();
        };

        const onApply = () => {
            $(getCancelChangesButton()).add(getApplyChangesButton())
                .add(getRotateLeftButton())
                .add(getRotateRightButton())
                .slideUp('fast', () => {
                    $(getShowImageRotationControls())
                        .add(imageEditSizeControls.getEditSizeControls())
                        .add(getRevertButton())
                        .slideDown();
                });
        };

        let previousRotateDegrees = null;

        const onRotationActivated = () => {
            previousRotateDegrees = parseInt(
                editableImage.getImage()[0].style.transform.split(' ')[0].replace(/\D/g, '')
            );

            $(getShowImageRotationControls())
                .add(imageEditSizeControls.getEditSizeControls())
                .add(getRevertButton())
                .slideUp('fast', () => {
                    $(getCancelChangesButton()).add(getApplyChangesButton())
                        .add(getRotateLeftButton())
                        .add(getRotateRightButton())
                        .slideDown();
                });
        };

        let $cancelChangesButton;

        function getCancelChangesButton() {
            return $cancelChangesButton || ($cancelChangesButton = components.buttons.negativeButton({
                text: 'Cancel', //todo: localize!!!111
                title: 'Cancel changes', //todo: localize!!!111
                click: onCancel,
                style: 'display: none;',
            }))
        }

        let $applyChangesButton;

        function getApplyChangesButton() {
            return $applyChangesButton || ($applyChangesButton = components.buttons.saveButton({
                text: 'Apply',//todo: localize!!!111
                title: 'Apply changes', //todo: localize!!!111
                click: onApply,
                style: 'display: none;',
            }))
        }

        let $showImageRotationControls;

        function getShowImageRotationControls() {
            return $showImageRotationControls || ($showImageRotationControls = components.buttons.rotationButton({
                title: 'Activate rotation controls',//todo: localize!!!111
                click: onRotationActivated,
            }))
        }

        let $zoomPlusButton;

        function getZoomPlusButton() {
            return $zoomPlusButton || ($zoomPlusButton = components.buttons.zoomPlusButton({
                title: texts.buttons.zoomIn,
                click: zoomPlus,
                style: 'display: none;',
            }))
        }

        let $zoomMinusButton;

        function getZoomMinusButton() {
            return $zoomMinusButton || ($zoomMinusButton = components.buttons.zoomMinusButton({
                title: texts.buttons.zoomOut,
                click: zoomMinus,
                style: 'display: none;',
            }))
        }

        let $zoomResetButton;

        function getZoomResetButton() {
            return $zoomResetButton || ($zoomResetButton = components.buttons.zoomResetButton({
                title: texts.buttons.zoomReset,
                style: 'display: none;',
                click: zoomFit,
            }))
        }

        let $rotateLeftButton;

        function getRotateLeftButton() {
            return $rotateLeftButton || ($rotateLeftButton = components.buttons.rotateLeftButton({
                title: texts.buttons.rotateLeft,
                click: imageRotate.rotateLeft,
                style: 'display: none;',
            }))
        }

        let $rotateRightButton;

        function getRotateRightButton() {
            return $rotateRightButton || ($rotateRightButton = components.buttons.rotateRightButton({
                title: texts.buttons.rotateRight,
                click: imageRotate.rotateRight,
                style: 'display: none;',
            }))
        }

        let $revertButton;

        function getRevertButton() {
            return $revertButton || ($revertButton = components.buttons.revertButton({
                title: texts.buttons.revert,
                click: revertImageChanges,
            }))
        }

        function buildScaleAndRotateControls() {
            return new BEM({
                block: "imcms-edit-image",
                elements: {
                    "button": [
                        getZoomPlusButton(),
                        getZoomMinusButton(),
                        getZoomResetButton(),
                        getShowImageRotationControls(),
                        getRotateLeftButton(),
                        getRotateRightButton(),
                        getRevertButton(),
                    ]
                }
            }).buildBlockStructure("<div>");
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
                {"control-scale-n-rotate": buildScaleAndRotateControls()},
                {"control-button": getApplyChangesButton()},
                {"control-view": buildSwitchViewControls()}
            ]);
        }

        let $imgUrl;

        module.exports = {
            showOriginalImageArea: () => toggleImgArea.call($tabOriginal),

            build: function ($rightSidePanel) {
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
        }
    }
);
