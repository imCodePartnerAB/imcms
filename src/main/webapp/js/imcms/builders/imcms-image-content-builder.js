/**
 * Created by Serhii Maksymchuk from Ubrainians for imCode
 * 16.08.17.
 */
define("imcms-image-content-builder",
    [
        "imcms-image-files-rest-api", "imcms-image-folders-rest-api", "imcms-bem-builder", "imcms-components-builder",
        "imcms-primitives-builder", "imcms-modal-window-builder", "jquery", "imcms-i18n-texts", 'imcms'
    ],
    function (imageFilesREST, imageFoldersREST, BEM, components, primitives, modalWindow, $, texts, imcms) {
        var OPENED_FOLDER_BTN_CLASS = "imcms-folder-btn--open";
        var SUBFOLDER_CLASS = "imcms-folders__subfolder";
        var ACTIVE_FOLDER_CLASS = "imcms-folder--active";
        var FOLDER_CREATION_BLOCK_ID = "imcms-folder-create-block";
        var ROOT_FOLDER_LEVEL = 0;
        var activeFolder;

        texts = texts.editors.content;

        var selectedFullImagePath;

        var $foldersContainer, $imagesContainer, selectedImage, $saveAndCloseBtn;

        var viewModel = {
            root: {},
            folders: [],
            $images: []
        };

        var rootFolderBEM = new BEM({
            block: "imcms-left-side",
            elements: {
                "controls": "imcms-main-folders-controls",
                "folders": "imcms-folders"
            }
        });

        function onFolderRenamed(response) {
            if (response) {
                this.$block.parent().attr("data-folder-name", this.name);

                this.$block.prev()
                    .find(".imcms-folder__name")
                    .text(this.name);

                this.$block.remove();

            } else {
                console.error("Folder " + this.path + "/" + this.name + " not renamed!")
            }
        }

        function onFolderCreated(response) {
            if (response) {
                var newFolder = {
                    name: this.name,
                    path: this.path,
                    $images: []
                };

                newFolder.$folder = buildSubFolder(newFolder, this.parentLevel + 1, "")
                    .addClass(SUBFOLDER_CLASS)
                    .css("display", "block");

                this.$block.replaceWith(newFolder.$folder);

                var $parent = newFolder.$folder.prev();
                if ($parent.hasClass("imcms-folder") && !$parent.children(".imcms-folder__btn").length) {
                    $("<div>", {
                            "class": "imcms-folder__btn imcms-folder-btn--open",
                            click: openSubFolders
                        }
                    ).prependTo($parent);
                }
            } else {
                console.error("Folder " + this.path + "/" + this.name + " not created!")
            }
        }

        var folderControlsBuilder = {
            move: function (folder) {
                return components.controls.move(moveFolder.bind(folder));
            },
            remove: function (folder) {
                return components.controls.remove(removeFolder.bind(folder));
            },
            edit: function (folder, level) {
                return components.controls.edit(setRenameFolder(folder, level));
            },
            create: function (folder, level) {
                return components.controls.create(setCreateFolder(folder, level));
            }
        };

        function buildRootControls(rootFile) {
            return new BEM({
                block: "imcms-main-folders-controls",
                elements: {
                    "name": $("<div>", {
                        "class": "imcms-title",
                        text: rootFile.name
                    }),
                    "control": folderControlsBuilder.create(rootFile, ROOT_FOLDER_LEVEL)
                }
            }).buildBlockStructure("<div>", {
                click: function () {
                    onFolderClick.call(this, rootFile)
                }
            });
        }

        function buildRootFolder(rootFile) {
            rootFile.$folder = $foldersContainer;
            var $rootControls = buildRootControls(rootFile);

            return rootFolderBEM.makeBlockElement("controls", $rootControls);
        }

        function moveFolder() {
            // todo: implement or delete it's control icon at all
        }

        function removeFolderFromEditor($folder) {
            $folder.remove();
        }

        function removeParentBtnIfNoSubfolders($folder) {
            if (!$folder.find("." + SUBFOLDER_CLASS).length) {
                $folder.find(".imcms-folder__btn").remove();
            }
        }

        function onDoneRemoveFolder($folder) {
            var $parentFolder = $folder.parent();
            removeFolderFromEditor($folder);
            removeParentBtnIfNoSubfolders($parentFolder);
            $(".imcms-main-folders-controls").click();
        }

        function removeFolder() { // this == folder
            var $folder = this.$folder;
            var path = getFolderPath($folder);
            var name = this.name;

            var onRemoveResponse = function (response) {
                if (response) {
                    onDoneRemoveFolder($folder);

                } else {
                    console.error("Folder " + name + " was not removed!");
                }
            };

            var onAnswer = function (answer) {
                if (answer) {
                    imageFoldersREST.remove({"path": path}).done(onRemoveResponse);
                }
            };

            imageFoldersREST.canDelete({"path": path})
                .success(function () {
                    modalWindow.buildModalWindow(texts.removeFolderMessage + name + "\"?", onAnswer);
                })
                .error(function () {
                    modalWindow.buildWarningWindow(texts.folderNotEmptyMessage, function () {});
                });
        }

        function buildFolderRenamingBlock(folder, level) {
            var currentFolderName = folder.$folder.children(".imcms-folders__folder")
                .find(".imcms-folder__name")
                .text();

            return buildFolderManageBlock({
                    folder: folder,
                    level: level,
                    previousFolderName: currentFolderName
                },
                imageFoldersREST.replace,
                onFolderRenamed
            ).css({
                position: "absolute",
                top: "0px",
                left: "0px"
            });
        }

        function setRenameFolder(folder, level) {
            return function renameFolder() {
                var $folderRenamingBlock = buildFolderRenamingBlock(folder, level);
                folder.$folder.children(".imcms-folders__folder").after($folderRenamingBlock);
            };
        }

        function setCreateFolder(folder, level) {
            return function createFolder() {
                showFolderCreationBlock(folder, level);

                var $openFolderBtn = $(this).parent()
                    .parent()
                    .children(".imcms-folder__btn");

                if ($openFolderBtn.hasClass("imcms-folder-btn--open")) {
                    return;
                }

                openSubFolders.call($openFolderBtn[0]);
            }
        }

        function getFolderPath($folder) {
            var parentNames = $folder.parents("[data-folder-name]")
                .map(function () {
                    return $(this).attr("data-folder-name");
                })
                .toArray()
                .reverse();

            var selfName = $folder.attr("data-folder-name");
            var relativePath = parentNames.concat(selfName).join("/");

            return (relativePath.length ? "/" : "") + relativePath;
        }

        function buildFolderManageBlock(opts, onConfirm, onSuccess) {
            $("#" + FOLDER_CREATION_BLOCK_ID).remove();

            var $folderNameInput = primitives.imcmsInput({
                "class": "imcms-input",
                value: opts.previousFolderName,
                placeholder: texts.newFolderName
            });
            var $confirmBtn = components.buttons.neutralButton({
                "class": "imcms-button",
                text: texts.add,
                click: function () {
                    var folderName = $folderNameInput.val();

                    if (!folderName) {
                        return;
                    }

                    var isNewFolder = !opts.previousFolderName;

                    var dataOnConfirm = {
                        name: folderName
                    };

                    var contextOnSuccess = {
                        name: folderName,
                        parentLevel: opts.level,
                        $block: $folderCreationBlock
                    };

                    var path = getFolderPath(opts.folder.$folder);

                    if (isNewFolder) {
                        dataOnConfirm.path = contextOnSuccess.path = path + "/" + folderName;

                    } else {
                        var pathSplitBySeparator = path.split("/");
                        pathSplitBySeparator[pathSplitBySeparator.length - 1] = folderName;

                        dataOnConfirm.path = path;
                        contextOnSuccess.path = pathSplitBySeparator.join("/");
                    }

                    onConfirm(dataOnConfirm).done(function (response) {
                        if (response) {
                            if (!isNewFolder) {
                                opts.folder.path = contextOnSuccess.path;
                            }
                            onSuccess.call(contextOnSuccess, response);
                        }
                    });
                }
            });

            var $folderCreationBlock = new BEM({
                block: "imcms-panel-named",
                elements: {
                    "input": $folderNameInput,
                    "button": $confirmBtn
                }
            }).buildBlockStructure("<div>", {id: FOLDER_CREATION_BLOCK_ID});

            return $folderCreationBlock;
        }

        function buildFolderCreationBlock(parentFolder, level) {
            return buildFolderManageBlock({
                folder: parentFolder,
                level: level,
                previousFolderName: ""
            }, imageFoldersREST.create, onFolderCreated);
        }

        function showFolderCreationBlock(parentFolder, level) {
            var $createFolderBlock = buildFolderCreationBlock(parentFolder, level);
            var $subFolders = parentFolder.$folder.find(".imcms-folders");

            if ($subFolders.length) {
                $subFolders.eq(0).before($createFolderBlock);
                return;
            }

            parentFolder.$folder.append($createFolderBlock);
        }

        function buildControls(subfolder, level) {
            var controlsElements = [
                folderControlsBuilder.remove(subfolder),
                folderControlsBuilder.edit(subfolder, level),
                folderControlsBuilder.create(subfolder, level)
            ];

            return components.controls.buildControlsBlock("<div>", controlsElements);
        }

        function openSubFolders() {
            var $button = $(this);
            var $subFolders = $button.toggleClass(OPENED_FOLDER_BTN_CLASS)
                .parent() // fixme: bad idea!
                .parent()
                .children("." + SUBFOLDER_CLASS);

            var isOpen = $button.hasClass(OPENED_FOLDER_BTN_CLASS);

            if ($subFolders.length) {
                $subFolders.css("display", isOpen ? "block" : "none");
            }
        }

        function showImagesIn(folder) {
            folder.$images.forEach(function ($image) {
                $image.css("display", "block");
            });
        }

        function onFolderClick(folder) {
            activeFolder = folder;

            $("." + ACTIVE_FOLDER_CLASS).removeClass(ACTIVE_FOLDER_CLASS);
            $(this).addClass(ACTIVE_FOLDER_CLASS);

            viewModel.$images.forEach(function ($image) {
                $image.css("display", "none");
            });

            folder.imagesAreLoaded ? showImagesIn(folder) : loadImages(folder);
        }

        function loadImages(folder, setCurrentImage) {
            imageFoldersREST.read({"path": folder.path}).done(
                function (imagesFolder) {
                    folder.imagesAreLoaded = true;
                    folder.files = imagesFolder.files;

                    buildImagesNotRecursive(folder);

                    $imagesContainer.append(folder.$images);

                    showImagesIn(folder);

                    setCurrentImage && setCurrentImage();
                });
        }

        function buildFolder(subfolder, level) {
            var elements = {};

            if (subfolder.folders && subfolder.folders.length) {
                elements.btn = $("<div>", {click: openSubFolders});
            }

            elements.name = $("<div>", {
                "class": "imcms-title",
                text: subfolder.name
            });

            elements.controls = buildControls(subfolder, level);

            return new BEM(
                {
                    block: "imcms-folder",
                    elements: elements
                }
            ).buildBlockStructure("<div>", {
                click: function () {
                    onFolderClick.call(this, subfolder);
                }
            });
        }

        function buildSubFolder(subfolder, level, folderPathToFind) {
            var isSubLevel = (level > 1);

            if (subfolder.path === folderPathToFind) {
                activeFolder = subfolder;
            }

            var elements = {
                "folder": buildFolder(subfolder, level)
            };

            if (subfolder.folders && subfolder.folders.length) {
                elements.subfolder = buildSubFolders(subfolder, level + 1, folderPathToFind).map(function ($subfolder) {
                    if (isSubLevel) {
                        $subfolder.modifiers = ["close"];
                    }

                    return $subfolder;
                });
            }

            return new BEM({
                block: "imcms-folders",
                elements: elements
            }).buildBlockStructure("<div>", {
                "data-folder-name": subfolder.name,
                "data-folders-lvl": level
            });
        }

        function buildSubFolders(folder, level, folderPathToFind) {
            return folder.folders.map(function (subfolder) {
                return subfolder.$folder = buildSubFolder(subfolder, level, folderPathToFind);
            });
        }

        function onImageDelete(element, imageFile) {
            imageFile.path = getFolderPath(activeFolder.$folder) + "/" + imageFile.name;

            imageFilesREST.remove(imageFile)
                .success(function (response) {
                    response && $(element).parent().parent().remove();
                })
                .error(function () {
                    modalWindow.buildWarningWindow(texts.imageStillUsed, function () {});
                });
        }

        function buildImageDescription(imageFile) {
            /** @namespace imageFile.uploaded */
            return new BEM({
                block: "imcms-choose-img-description",
                elements: {
                    "date": $("<div>", {text: imageFile.uploaded}),
                    "button": components.buttons.closeButton({
                        click: function () {
                            var element = this;
                            var question = texts.removeImageConfirm + imageFile.name + " ?";

                            modalWindow.buildModalWindow(question, function (isUserSure) {
                                if (isUserSure) {
                                    onImageDelete(element, imageFile);
                                }
                            });
                        }
                    }),
                    "img-title": $("<div>", {
                            "class": "imcms-title",
                            text: imageFile.name,
                            title: imageFile.name
                        }
                    ),
                    "img-size": $("<div>", {text: imageFile.resolution + " " + imageFile.size})
                }
            }).buildBlockStructure("<div>");
        }

        function selectImage(imageFile) {
            $saveAndCloseBtn.removeAttr('disabled').removeClass('imcms-button--disabled');
            $(".image-chosen").removeClass("image-chosen");
            $(this).addClass("image-chosen");
            selectedImage = imageFile;
        }

        function buildImage(imageFile) {
            return new BEM({
                block: "imcms-choose-img-wrap",
                elements: {
                    "img": $("<div>", {
                        "class": "imcms-choose-img",
                        style: "background-image: url('" + imcms.contextPath + "/" + imcms.imagesPath + imageFile.path + "')"
                    }),
                    "description": buildImageDescription(imageFile)
                }
            }).buildBlockStructure("<div>", {
                style: "display: none",
                click: function () {
                    selectImage.call(this, imageFile);
                }
            });
        }

        function buildImages(folder) {
            buildImagesNotRecursive(folder);
            (folder.folders || []).forEach(buildImages);
        }

        function buildImagesNotRecursive(folder) {
            folder.$images = folder.files.map(buildImage);
            viewModel.$images = viewModel.$images.concat(folder.$images);
        }

        function loadImageFoldersContent(imagesRootFolder) {
            viewModel.root = imagesRootFolder;
            viewModel.root.imagesAreLoaded = true;

            buildImages(viewModel.root);

            if (!viewModel.folders) {
                viewModel.folders = [];
            }

            viewModel.folders.push(buildRootFolder(viewModel.root));

            var slashLastIndex = selectedFullImagePath.lastIndexOf("/");

            const path = selectedFullImagePath.substring(0, slashLastIndex);
            const fixedPath = (path.startsWith('/') ? '' : '/') + path;

            var $subfolders = buildSubFolders(viewModel.root, ROOT_FOLDER_LEVEL + 1, fixedPath).map(function ($subfolder) {
                return rootFolderBEM.makeBlockElement("folders", $subfolder);
            });

            viewModel.folders = viewModel.folders.concat($subfolders);

            $foldersContainer.append(viewModel.folders);
            $imagesContainer.append(viewModel.$images);

            function scrollToSelectedImage() {
                const selectedImage = $('.image-chosen:first');
                if (selectedImage.length) {
                    $(".imcms-content-manager__right-side").scrollTop(selectedImage.position().top);
                }
            }

            var imageName = selectedFullImagePath.substring(slashLastIndex + 1);

            if (activeFolder) {
                openParentFolders(activeFolder);

                loadImages(activeFolder, function () {
                    setUpSelectedImage(activeFolder, imageName);
                    scrollToSelectedImage();
                });
            } else {
                if (slashLastIndex === -1) { // path only with image name (image from root)
                    setUpSelectedImage(imagesRootFolder, imageName);
                }

                activeFolder = imagesRootFolder;
                activeFolder.$folder.find('.imcms-main-folders-controls').addClass(ACTIVE_FOLDER_CLASS);
                showImagesIn(viewModel.root);
                scrollToSelectedImage();
            }
        }

        function openParentFolders(folder) {
            var $folder = folder.$folder;
            var imcmsFolderClassSelector = ".imcms-folder";

            $folder.children(imcmsFolderClassSelector).addClass(ACTIVE_FOLDER_CLASS);

            var parentFolders = $folder.parents("[data-folder-name]");

            parentFolders.children(imcmsFolderClassSelector)
                .find(".imcms-folder__btn").addClass(OPENED_FOLDER_BTN_CLASS);

            parentFolders.children(imcmsFolderClassSelector + "s")
                .css({"display": "block"});
        }

        function setUpSelectedImage(folder, selectedImageName) {
            folder.files.forEach(function (file) {
                if (file.name === selectedImageName) {
                    selectedImage = file;
                }
            });

            chooseSelectedImage(folder, selectedImageName);
        }

        function chooseSelectedImage(folder, selectedImageName) {
            folder.$images.forEach(function ($image) {

                if (selectedImageName === $image.find(".imcms-title").text()) {
                    $image.addClass("image-chosen");
                    $saveAndCloseBtn.removeAttr('disabled').removeClass('imcms-button--disabled');
                }
            });
        }

        return {
            getSelectedImage: function () {
                return selectedImage;
            },
            loadAndBuildContent: function (options) {
                $foldersContainer = options.foldersContainer;
                $imagesContainer = options.imagesContainer;
                selectedFullImagePath = options.selectedImagePath;
                $saveAndCloseBtn = options.$saveAndCloseBtn;

                imageFoldersREST.read().done(loadImageFoldersContent);
            },
            onImageUpload: function (formData) {
                var saveImageRequestData = formData;
                saveImageRequestData.append("folder", getFolderPath(activeFolder.$folder));

                imageFilesREST.postFiles(saveImageRequestData).done(function (uploadedImageFiles) {
                    var $newImages = uploadedImageFiles.map(function (imageFile) {
                        return buildImage(imageFile).css("display", "block");
                    });
                    activeFolder.files = (activeFolder.files || []).concat(uploadedImageFiles);
                    $imagesContainer.append($newImages);
                    activeFolder.$images = activeFolder.$images.concat($newImages);
                    viewModel.$images = viewModel.$images.concat($newImages);
                });
            },
            clearContent: function () {
                activeFolder = selectedImage = null;
                $imagesContainer.children().remove();
                $foldersContainer.children().not("#closeFolders").remove();
                viewModel = {
                    root: {},
                    $folder: [],
                    $images: []
                }
            }
        };
    }
);
