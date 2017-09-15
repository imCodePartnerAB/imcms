/**
 * Created by Serhii Maksymchuk from Ubrainians for imCode
 * 16.08.17.
 */
Imcms.define("imcms-content-manager-builder",
    ["imcms-bem-builder", "imcms-window-builder", "imcms-components-builder", "imcms-image-content-builder", "jquery"],
    function (BEM, WindowBuilder, components, imageContentBuilder, $) {
        var $foldersContainer;
        var $imagesContainer;

        function buildContentManager() {
            var $footer;
            var $showHideFoldersButton;

            function saveAndCloseWindow() {
                contentManagerWindowBuilder.closeWindow(); // fixme: just closing now, should be save and close
            }

            function buildHead() {
                return contentManagerWindowBuilder.buildHead("Content manager");
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
                        btnText = "hide folders";

                    } else {
                        foldersLeft = "-100%";
                        imagesAndFooterLeft = 0;
                        btnState = "close";
                        btnText = "show folders";
                    }

                    $foldersContainer.animate({"left": foldersLeft}, 600);
                    $imagesContainer.add($footer).animate({"left": imagesAndFooterLeft}, 600);
                    $btn.attr("data-state", btnState).text(btnText);
                }

                $showHideFoldersButton = components.buttons.neutralButton({
                    id: "openCloseFolders",
                    text: "Show folders",
                    "data-state": "close",
                    click: openCloseFolders
                });

                var $fileInput = $("<input>", {
                    type: "file",
                    style: "display: none;",
                    change: function () {
                        console.log("%c Not implemented feature: upload new image", "color: red;");
                        console.log(this.files[0]);
                    }
                });

                var $uploadNewImage = components.buttons.positiveButton({
                    text: "Upload",
                    click: function () {
                        $fileInput.click();
                    }
                });

                var $saveAndClose = components.buttons.saveButton({
                    text: "Save and close",
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

        return {
            build: function () {
                contentManagerWindowBuilder.buildWindow.applyAsync(arguments, contentManagerWindowBuilder);
            }
        };
    }
);
