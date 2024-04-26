/**
 * Created by Serhii Maksymchuk from Ubrainians for imCode
 * 16.08.17.
 */
define(
    "imcms-content-manager-builder",
    [
        "imcms-bem-builder", "imcms-window-builder", "imcms-components-builder", "imcms-image-content-builder",
        "jquery", "imcms-i18n-texts", "imcms-events", "imcms", 'imcms-image-resize'
    ],
    function (BEM, WindowBuilder, components, imageContentBuilder, $, texts, events, imcms, imageResize) {
        let $foldersContainer;
        let $imagesContainer;
        let $saveAndCloseBtn;

        texts = texts.editors.content;

        function buildContentManager() {
            let $footer;
            let $showHideFoldersButton;

            function buildHead() {
                return contentManagerWindowBuilder.buildHead(texts.title);
            }

            function buildFoldersContainer() {
                return new BEM({
                    block: "imcms-left-side",
                    elements: {
                        "close-button": components.buttons.closeButton({
                            id: "closeFolders",
                            click: () => {
                                $showHideFoldersButton.click();
                            }
                        })
                    }
                }).buildBlockStructure("<div>");
            }

            function buildRightSide() {
                return new BEM({
                    block: 'imcms-right-side',
                    elements: {
                        'tools': buildToolsContainer(),
                        'images-container': $imagesContainer = $("<div>"),
                    }
                }).buildBlockStructure('<div>');
            }

            function buildToolsContainer() {
                return new BEM({
                    block: 'imcms-toolbar-images-tools',
                    elements: {
                        'search': imageContentBuilder.buildSearchField(),
                        'sorting': imageContentBuilder.buildSortingSelect(),
                    }
                }).buildBlockStructure('<div>');
            }

            function buildFooter() {
                function openCloseFolders() {
                    const $btn = $(this);
                    let btnText, btnState, imagesAndFooterLeft, foldersLeft;

                    if ($btn.attr("data-state") === "close") {
                        foldersLeft = 0;
                        imagesAndFooterLeft = "400px";
                        btnState = "open";
                        btnText = texts.hideFolders;

                    } else {
                        foldersLeft = "-100%";
                        imagesAndFooterLeft = 0;
                        btnState = "close";
                        btnText = texts.showFolders;
                    }

                    $foldersContainer.animate({"left": foldersLeft}, 600);
                    $imagesContainer.add($footer).animate({"left": imagesAndFooterLeft}, 600);
                    $btn.attr("data-state", btnState).text(btnText);
                }

                $showHideFoldersButton = components.buttons.neutralButton({
                    id: "openCloseFolders",
                    text: texts.showFolders,
                    "data-state": "close",
                    click: openCloseFolders
                });

                const $fileInput = $("<input>", {
                    type: "file",
                    accept: "image/*",
                    style: "display: none;",
                    multiple: "",
                    change: function () {
                        const formData = new FormData();

                        for (let i = 0; i < this.files.length; i++) {
                            formData.append('files', this.files[i]);
                        }

                        imageContentBuilder.onImageUpload(formData);
                        $(this).val('');
                    }
                });

                const $uploadNewImage = components.buttons.positiveButton({
                    text: texts.upload,
                    click: () => {
                        $fileInput.click();
                    }
                });

                const $cancelBtn = components.buttons.negativeButton({
                    text: texts.cancel,
                    click: closeWindow
                });

                const footerElements$ = [$fileInput, $cancelBtn];

                if (!imcms.disableContentManagerSaveButton) {
                    $saveAndCloseBtn = components.buttons.saveButton({
                        'class': 'imcms-button--disabled',
                        text: texts.useSelectedImage,
                        disabled: 'disabled',
                        click: saveAndCloseWindow
                    });

                    footerElements$.push($saveAndCloseBtn);
                }

                footerElements$.push($uploadNewImage);
                return WindowBuilder.buildFooter(footerElements$);
            }

            return new BEM({
                block: "imcms-content-manager",
                elements: {
                    "head": buildHead(),
                    "left-side": $foldersContainer = buildFoldersContainer(),
                    "right-side": buildRightSide(),
                    "footer": $footer = buildFooter()
                }
            }).buildBlockStructure("<div>", {"class": "imcms-editor-window"});
        }

        function buildContent() {
            imageContentBuilder.loadAndBuildContent({
                foldersContainer: $foldersContainer,
                imagesContainer: $imagesContainer,
                selectedImagePath: selectedImagePath,
                $saveAndCloseBtn: $saveAndCloseBtn
            });
        }

        function saveAndCloseWindow() {
	        showImageStrategyHandler(true);
	        contentManagerWindowBuilder.closeWindow();
        }

        function closeWindow() {
	        onCloseWindow();
	        contentManagerWindowBuilder.closeWindow();
		}

		function onCloseWindow() {
			if (imageContentBuilder.isSelectedImageChanged())
				showImageStrategyHandler(false);
		}

		function showImageStrategyHandler(enableSelectedImageFlag) {
			showImageStrategy && showImageStrategy(imageContentBuilder.getSelectedImage());
			enableSelectedImageFlag ? imageResize.enableSelectedImageFlag() : '';
		}

        function clearData() {
	        onCloseWindow();
            $saveAndCloseBtn && $saveAndCloseBtn.attr('disabled', 'disabled').addClass('imcms-button--disabled');
            events.trigger("content manager closed");
            imageContentBuilder.clearContent();
        }

        function onEnterKeyPressed() {
            imcms.disableContentManagerSaveButton || saveAndCloseWindow();
        }

        var contentManagerWindowBuilder = new WindowBuilder({
            factory: buildContentManager,
            loadDataStrategy: buildContent,
            clearDataStrategy: clearData,
            onEscKeyPressed: closeWindow,
            onEnterKeyPressed: onEnterKeyPressed
        });

        var showImageStrategy;
        var selectedImagePath;

        module.exports = {
            build: function (imageEditorShowImageStrategy, getSelectedImagePath) {
                showImageStrategy = imageEditorShowImageStrategy;

                selectedImagePath = (getSelectedImagePath) ? getSelectedImagePath() : '';

                contentManagerWindowBuilder.buildWindow.apply(contentManagerWindowBuilder, arguments);
            }
        };
    }
);
