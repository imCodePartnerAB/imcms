/**
 * Created by Serhii Maksymchuk from Ubrainians for imCode
 * 16.08.17.
 */
Imcms.define(
    "imcms-content-manager-builder",
    [
        "imcms-bem-builder", "imcms-window-builder", "imcms-components-builder", "imcms-image-content-builder",
        "jquery", "imcms-i18n-texts", "imcms-events", "imcms"
    ],
    function (BEM, WindowBuilder, components, imageContentBuilder, $, texts, events, imcms) {
        var $foldersContainer;
        var $imagesContainer;

        texts = texts.editors.content;

        function buildContentManager() {
            var $footer;
            var $showHideFoldersButton;

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

                var footerElements$ = [$showHideFoldersButton, $fileInput];

                if (!imcms.disableContentManagerSaveButton) {
                    var $saveAndClose = components.buttons.saveButton({
                        text: texts.saveAndClose,
                        click: saveAndCloseWindow
                    });

                    footerElements$.push($saveAndClose);
                }

                footerElements$.push($uploadNewImage);
                return contentManagerWindowBuilder.buildFooter(footerElements$);
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
                imagesContainer: $imagesContainer,
                selectedImagePath: selectedImagePath
            });
        }

        function saveAndCloseWindow() {
            showImageStrategy && showImageStrategy(imageContentBuilder.getSelectedImage());
            closeWindow();
        }

        function closeWindow() {
            contentManagerWindowBuilder.closeWindow();
        }

        function clearData() {
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

        return {
            build: function (imageEditorShowImageStrategy, getSelectedImagePath) {
                showImageStrategy = imageEditorShowImageStrategy;
                selectedImagePath = getSelectedImagePath();

                contentManagerWindowBuilder.buildWindow.applyAsync(arguments, contentManagerWindowBuilder);
            }
        };
    }
);
