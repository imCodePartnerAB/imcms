/**
 * Created by Serhii Maksymchuk from Ubrainians for imCode
 * 16.08.17.
 */
Imcms.define(
    "imcms-content-manager-builder",
    [
        "imcms-bem-builder", "imcms-window-builder", "imcms-components-builder", "imcms-image-content-builder",
        "jquery", "imcms-i18n-texts"
    ],
    function (BEM, WindowBuilder, components, imageContentBuilder, $, texts) {
        var $foldersContainer;
        var $imagesContainer;

        texts = texts.editors.content;

        function buildContentManager() {
            var $footer;
            var $showHideFoldersButton;

            function saveAndCloseWindow() {
                showImageStrategy.call(showImageStrategy, imageContentBuilder.getSelectedImage());
                contentManagerWindowBuilder.closeWindow();
            }

            function buildHead() {
                return contentManagerWindowBuilder.buildHead(texts.title);
            }

            function buildFoldersContainer() {
                return new BEM({
                    block: "imcms-left-side",
                    elements: {
                        "close-button": components.buttons.closeButton({
                            id: "closeFolders",
                            click: function () {
                                $showHideFoldersButton.click();
                            }
                        })
                    }
                }).buildBlockStructure("<div>");
            }

            function buildFooter() {
                function openCloseFolders() {
                    var $btn = $(this);
                    var btnText, btnState, imagesAndFooterLeft, foldersLeft;

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

                var $fileInput = $("<input>", {
                    type: "file",
                    accept: "image/*",
                    style: "display: none;",
                    multiple: "",
                    change: function () {
                        var formData = new FormData();

                        for (var i = 0; i < this.files.length; i++) {
                            formData.append('files', this.files[i]);
                        }

                        imageContentBuilder.onImageUpload(formData);
                    }
                });

                var $uploadNewImage = components.buttons.positiveButton({
                    text: texts.upload,
                    click: function () {
                        $fileInput.click();
                    }
                });

                var $saveAndClose = components.buttons.saveButton({
                    text: texts.saveAndClose,
                    click: saveAndCloseWindow
                });

                return contentManagerWindowBuilder.buildFooter([
                    $showHideFoldersButton, $fileInput, $uploadNewImage, $saveAndClose
                ]);
            }

            return new BEM({
                block: "imcms-content-manager",
                elements: {
                    "head": buildHead(),
                    "left-side": $foldersContainer = buildFoldersContainer(),
                    "right-side": $imagesContainer = $("<div>", {"class": "imcms-right-side"}),
                    "footer": $footer = buildFooter()
                }
            }).buildBlockStructure("<div>", {"class": "imcms-editor-window"});
        }

        function buildContent() {
            imageContentBuilder.loadAndBuildContent({
                foldersContainer: $foldersContainer,
                imagesContainer: $imagesContainer
            });
        }

        function clearData() {
            imageContentBuilder.clearContent();
        }

        var contentManagerWindowBuilder = new WindowBuilder({
            factory: buildContentManager,
            loadDataStrategy: buildContent,
            clearDataStrategy: clearData
        });

        var showImageStrategy;

        return {
            build: function (imageEditorShowImageStrategy) {
                showImageStrategy = imageEditorShowImageStrategy;
                contentManagerWindowBuilder.buildWindow.applyAsync(arguments, contentManagerWindowBuilder);
            }
        };
    }
);
