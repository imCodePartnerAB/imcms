/**
 * Created by Serhii Maksymchuk from Ubrainians for imCode
 * 16.08.17.
 */
define("imcms-image-content-builder",
    [
        "imcms-image-files-rest-api", "imcms-image-folders-rest-api", "imcms-bem-builder", "imcms-components-builder",
        "imcms-primitives-builder", "imcms-modal-window-builder", "jquery", "imcms-i18n-texts", 'imcms'
    ],
    function (imageFilesREST, imageFoldersREST, BEM, components, primitives, modal, $, texts, imcms) {
        const OPENED_FOLDER_BTN_CLASS = "imcms-folder-btn--open";
        const SUBFOLDER_CLASS = "imcms-folders__subfolder";
        const ACTIVE_FOLDER_CLASS = "imcms-folder--active";
        const FOLDER_CREATION_BLOCK_ID = "imcms-folder-create-block";
        const ROOT_FOLDER_LEVEL = 0;
        let activeFolder;

        texts = texts.editors.content;

        let selectedFullImagePath;

        let $foldersContainer, $imagesContainer, selectedImage, $saveAndCloseBtn;

        let viewModel = {
            root: {},
            folders: [],
            $images: []
        };

        const rootFolderBEM = new BEM({
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
                console.error(`Folder ${this.path}/${this.name} not renamed!`)
            }
        }

        function onFolderCreated(response) {
            if (response) {
                const newFolder = {
                    name: this.name,
                    path: this.path,
                    $images: []
                };

                newFolder.$folder = buildSubFolder(newFolder, this.parentLevel + 1, "")
                    .addClass(SUBFOLDER_CLASS)
                    .css("display", "block");

                this.$block.replaceWith(newFolder.$folder);

                const $parent = newFolder.$folder.prev();
                if ($parent.hasClass("imcms-folder") && !$parent.children(".imcms-folder__btn").length) {
                    $("<div>", {
                            "class": "imcms-folder__btn imcms-folder-btn--open",
                            click: openSubFolders
                        }
                    ).prependTo($parent);
                }
            } else {
                console.error(`Folder ${this.path}/${this.name} not created!`)
            }
        }

        const folderControlsBuilder = {
            move: folder => components.controls.move(moveFolder.bind(folder)),

            remove: folder => components.controls.remove(removeFolder.bind(folder))
                .attr("title", texts.deleteFolderImage),

            edit: (folder, level) => components.controls.edit(setRenameFolder(folder, level))
                .attr("title", texts.editFolderImage),

            create: (folder, level) =>
                components.controls.create(setCreateFolder(folder, level)).attr("title", texts.createFolderImage),

            check: folder => {
                return components.controls
                    .check(setCheckFolder(folder))
                    .attr("title", texts.checkFolderImagesUsage);
            }
        };

        function buildRootControls(rootFile) {
            return new BEM({
                block: "imcms-main-folders-controls",
                elements: {
                    name: $("<div>", {
                        "class": "imcms-title",
                        text: rootFile.name
                    }),
                    controls: buildRootFolderControlElements(rootFile)
                }
            }).buildBlockStructure("<div>", {
                click: function () {
                    onFolderClick.call(this, rootFile)
                }
            });
        }

        function buildRootFolder(rootFile) {
            rootFile.$folder = $foldersContainer;
            const $rootControls = buildRootControls(rootFile);

            return rootFolderBEM.makeBlockElement("controls", $rootControls);
        }

        function moveFolder() {
            // todo: implement or delete it's control icon at all
        }

        function removeFolderFromEditor($folder) {
            $folder.remove();
        }

        function removeParentBtnIfNoSubfolders($folder) {
            if (!$folder.find(`.${SUBFOLDER_CLASS}`).length) {
                $folder.find(".imcms-folder__btn").remove();
            }
        }

        function onDoneRemoveFolder($folder) {
            const $parentFolder = $folder.parent();
            removeFolderFromEditor($folder);
            removeParentBtnIfNoSubfolders($parentFolder);
            $(".imcms-main-folders-controls").click();
        }

        function removeFolder() { // this == folder
            const $folder = this.$folder;
            const path = getFolderPath($folder);
            const name = this.name;

            const onRemoveResponse = response => {
                if (response) {
                    onDoneRemoveFolder($folder);
                }
            };

            const onAnswer = answer => {
                if (answer) {
                    imageFoldersREST.remove({"path": path})
                        .done(onRemoveResponse)
                        .fail(() => {
                            console.error(`Folder ${name} was not removed!`);
                            return modal.buildErrorWindow(texts.error.removeFailed);
                        });
                }
            };

            imageFoldersREST.canDelete({"path": path})
                .done(() => modal.buildModalWindow(`${texts.removeFolderMessage}${name}"?`, onAnswer))
                .fail(() => modal.buildWarningWindow(texts.folderNotEmptyMessage));
        }

        function buildFolderRenamingBlock(folder, level) {
            const currentFolderName = folder.$folder.children(".imcms-folders__folder")
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
                const $folderRenamingBlock = buildFolderRenamingBlock(folder, level);
                folder.$folder.children(".imcms-folders__folder").after($folderRenamingBlock);
            };
        }

        function setCreateFolder(folder, level) {
            return function createFolder() {
                showFolderCreationBlock(folder, level);

                const $openFolderBtn = $(this).parent()
                    .parent()
                    .children(".imcms-folder__btn");

                if ($openFolderBtn.hasClass("imcms-folder-btn--open")) {
                    return;
                }

                openSubFolders.call($openFolderBtn[0]);
            };
        }

        function setCheckFolder(folder) {
            return () => {
                imageFoldersREST.check({"path": folder.path})
                    .done(response => {
                        $imagesContainer.find('.imcms-control--warning').remove();
                        response.forEach(usedImage => {
                            let $image = $imagesContainer.children(`[data-image-name="${usedImage.imageName}"]`);
                            buildImageUsageInfoIcon($image, usedImage.usages);
                        });
                    })
                    .fail(() => modal.buildErrorWindow(texts.error.checkFailed));
            };
        }

        function buildImageUsageInfoIcon($image, usages) {
            const warningButton = components.controls.warning(() => buildImageUsagesModal(usages));
            $image.find('.imcms-choose-img-description').append(warningButton);
        }

        function getFolderPath($folder) {
            const parentNames = $folder.parents("[data-folder-name]")
                .map(function () {
                    return $(this).attr("data-folder-name");
                })
                .toArray()
                .reverse();

            const selfName = $folder.attr("data-folder-name");
            const relativePath = parentNames.concat(selfName).join("/");

            return `${relativePath.length ? "/" : ""}${relativePath}`;
        }

        function buildFolderManageBlock(opts, onConfirm, onSuccess) {
            $(`#${FOLDER_CREATION_BLOCK_ID}`).remove();

            const $folderNameInput = primitives.imcmsInput({
                "class": "imcms-input",
                value: opts.previousFolderName,
                placeholder: texts.newFolderName
            });
            const $confirmBtn = components.buttons.neutralButton({
                "class": "imcms-button",
                text: texts.add,
                click: () => {
                    let folderName = $folderNameInput.val();

                    if (!folderName) {
                        return;
                    }

                    let isNewFolder = !opts.previousFolderName;

                    const dataOnConfirm = {
                        name: folderName
                    };

                    const contextOnSuccess = {
                        name: folderName,
                        parentLevel: opts.level,
                        $block: $folderCreationBlock
                    };

                    const path = getFolderPath(opts.folder.$folder);

                    if (isNewFolder) {
                        dataOnConfirm.path = contextOnSuccess.path = `${path}/${folderName}`;

                    } else {
                        const pathSplitBySeparator = path.split("/");
                        pathSplitBySeparator[pathSplitBySeparator.length - 1] = folderName;

                        dataOnConfirm.path = path;
                        contextOnSuccess.path = pathSplitBySeparator.join("/");
                    }

                    onConfirm(dataOnConfirm)
                        .done(response => {
                            if (response) {
                                if (!isNewFolder) {
                                    opts.folder.path = contextOnSuccess.path;
                                }
                                onSuccess.call(contextOnSuccess, response);
                            }
                        })
                        .fail(() => modal.buildErrorWindow(texts.error.addFolderFailed));
                }
            });

            const $folderCreationBlock = new BEM({
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
            const $createFolderBlock = buildFolderCreationBlock(parentFolder, level);
            const $subFolders = parentFolder.$folder.find(".imcms-folders");

            if ($subFolders.length) {
                $subFolders.eq(0).before($createFolderBlock);
                return;
            }

            parentFolder.$folder.append($createFolderBlock);
        }

        function buildControls(subfolder, level) {
            const controlsElements = [
                folderControlsBuilder.remove(subfolder),
                folderControlsBuilder.edit(subfolder, level),
                folderControlsBuilder.create(subfolder, level),
                folderControlsBuilder.check(subfolder)
            ];

            return components.controls.buildControlsBlock("<div>", controlsElements);
        }

        function buildRootFolderControlElements(rootFile) {
            const controlsElements = [
                folderControlsBuilder.create(rootFile, ROOT_FOLDER_LEVEL),
                folderControlsBuilder.check(rootFile)
            ];

            return components.controls.buildControlsBlock("<div>", controlsElements);
        }

        function openSubFolders() {
            const $button = $(this);
            const $subFolders = $button.toggleClass(OPENED_FOLDER_BTN_CLASS)
                .parent() // fixme: bad idea!
                .parent()
                .children(`.${SUBFOLDER_CLASS}`);

            const isOpen = $button.hasClass(OPENED_FOLDER_BTN_CLASS);

            if ($subFolders.length) {
                $subFolders.css("display", isOpen ? "block" : "none");
            }
        }

        function showImagesIn(folder) {
            folder.$images.forEach($image => {
                $image.css("display", "block");
            });
        }

        function onFolderClick(folder) {
            activeFolder = folder;

            $(`.${ACTIVE_FOLDER_CLASS}`).removeClass(ACTIVE_FOLDER_CLASS);
            $(this).addClass(ACTIVE_FOLDER_CLASS);

            viewModel.$images.forEach($image => {
                $image.css("display", "none");
            });

            folder.imagesAreLoaded ? showImagesIn(folder) : loadImages(folder);
        }

        function loadImages(folder, setCurrentImage) {
            imageFoldersREST.read({"path": folder.path})
                .done(
                    imagesFolder => {
                        folder.imagesAreLoaded = true;
                        folder.files = imagesFolder.files;

                        buildImagesNotRecursive(folder);

                        $imagesContainer.append(folder.$images);

                        showImagesIn(folder);

                        setCurrentImage && setCurrentImage();
                    })
                .fail(() => modal.buildErrorWindow(texts.error.loadImagesFailed));
        }

        function buildFolder(subfolder, level) {
            const elements = {};

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
            const isSubLevel = (level > 1);

            if (subfolder.path === folderPathToFind) {
                activeFolder = subfolder;
            }

            const elements = {
                "folder": buildFolder(subfolder, level)
            };

            if (subfolder.folders && subfolder.folders.length) {
                elements.subfolder = buildSubFolders(subfolder, level + 1, folderPathToFind).map($subfolder => {
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
            return folder.folders.map(subfolder => subfolder.$folder = buildSubFolder(subfolder, level, folderPathToFind));
        }

        function onImageDelete(element, imageFile) {
            imageFile.path = `${getFolderPath(activeFolder.$folder)}/${imageFile.name}`;

            imageFilesREST.remove(imageFile)
                .done(response => response && $(element).parent().parent().remove())
                .fail(response => buildImageUsagesModal(response.responseJSON));
        }

        function buildImageUsagesModal(usagesList) {
            let usages = "";
            usagesList.forEach(usage => {
                if (usage.docId) {
                    if (usage.elementIndex) {
                        usages += `<div>Doc: ${usage.docId} Version: ${usage.version} Index:${usage.elementIndex}</div>`;
                    } else {
                        //Menu icon
                        usages += `<div>Doc: ${usage.docId} Version: ${usage.version}</div>`;
                    }
                } else {
                    //Cache usage
                    usages += `<div>Doc: undefined ${usage.comment}</div>`;
                }
            });
            modal.buildWarningWindow(texts.imageStillUsed + usages);
        }

        function buildImageDescription(imageFile) {
            /** @namespace imageFile.uploaded */
            return new BEM({
                block: "imcms-choose-img-description",
                elements: {
                    "date": $("<div>", {text: imageFile.uploaded}),
                    "button": components.buttons.closeButton({
                        click: function () {
                            const element = this;
                            const question = `${texts.removeImageConfirm} ${imageFile.name} ?`;

                            modal.buildModalWindow(question, isUserSure => {
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
                    "img-size": $("<div>", {text: `${imageFile.resolution} ${imageFile.size}`}),
                    'open-image': components.buttons.openInNewWindow('<a>', {
                        href: `${imcms.contextPath}/${imcms.imagesPath}/${imageFile.path}`,
                        title: texts.openImage,
                        target: '_blank',
                    }),
                }
            }).buildBlockStructure("<div>");
        }

        function selectImage(imageFile) {
            $saveAndCloseBtn && $saveAndCloseBtn.removeAttr('disabled').removeClass('imcms-button--disabled');
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
                        style: `background-image: url('${imcms.contextPath}/${imcms.imagesPath}/${imageFile.path}')`
                    }),
                    "description": buildImageDescription(imageFile)
                }
            }).buildBlockStructure("<div>", {
                "data-image-name": imageFile.name,
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

            const slashLastIndex = selectedFullImagePath.lastIndexOf("/");

            const path = selectedFullImagePath.substring(0, slashLastIndex);
            const fixedPath = `${path.startsWith('/') ? '' : '/'}${path}`;

            const $subfolders = buildSubFolders(viewModel.root, ROOT_FOLDER_LEVEL + 1, fixedPath).map($subfolder => rootFolderBEM.makeBlockElement("folders", $subfolder));

            viewModel.folders = viewModel.folders.concat($subfolders);

            $foldersContainer.append(viewModel.folders);
            $imagesContainer.append(viewModel.$images);

            function scrollToSelectedImage() {
                const selectedImage = $('.image-chosen:first');
                if (selectedImage.length) {
                    $(".imcms-content-manager__right-side").scrollTop(selectedImage.position().top);
                }
            }

            const imageName = selectedFullImagePath.substring(slashLastIndex + 1);

            if (activeFolder) {
                openParentFolders(activeFolder);

                loadImages(activeFolder, () => {
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
            const $folder = folder.$folder;
            const imcmsFolderClassSelector = ".imcms-folder";

            $folder.children(imcmsFolderClassSelector).addClass(ACTIVE_FOLDER_CLASS);

            const parentFolders = $folder.parents("[data-folder-name]");

            parentFolders.children(imcmsFolderClassSelector)
                .find(".imcms-folder__btn").addClass(OPENED_FOLDER_BTN_CLASS);

            parentFolders.children(`${imcmsFolderClassSelector}s`)
                .css({"display": "block"});
        }

        function setUpSelectedImage(folder, selectedImageName) {
            folder.files.forEach(file => {
                if (file.name === selectedImageName) {
                    selectedImage = file;
                }
            });

            chooseSelectedImage(folder, selectedImageName);
        }

        function chooseSelectedImage(folder, selectedImageName) {
            folder.$images.forEach($image => {
                if (selectedImageName === $image.find(".imcms-title").text()) {
                    $image.addClass("image-chosen");
                    $saveAndCloseBtn && $saveAndCloseBtn.removeAttr('disabled').removeClass('imcms-button--disabled');
                }
            });
        }

        return {
            getSelectedImage: () => selectedImage,
            loadAndBuildContent: options => {
                $foldersContainer = options.foldersContainer;
                $imagesContainer = options.imagesContainer;
                selectedFullImagePath = options.selectedImagePath;
                $saveAndCloseBtn = options.$saveAndCloseBtn;

                imageFoldersREST.read()
                    .done(loadImageFoldersContent)
                    .fail(() => modal.buildErrorWindow(texts.error.loadImagesFailed));
            },
            onImageUpload: formData => {
                const saveImageRequestData = formData;
                saveImageRequestData.append("folder", getFolderPath(activeFolder.$folder));

                imageFilesREST.postFiles(saveImageRequestData)
                    .done(uploadedImageFiles => {
                        const $newImages = uploadedImageFiles.map(imageFile => buildImage(imageFile).css("display", "block"));
                        activeFolder.files = (activeFolder.files || []).concat(uploadedImageFiles);
                        $imagesContainer.append($newImages);
                        activeFolder.$images = activeFolder.$images.concat($newImages);
                        viewModel.$images = viewModel.$images.concat($newImages);
                    })
                    .fail(() => modal.buildErrorWindow(texts.error.uploadImagesFailed));
            },
            clearContent: () => {
                activeFolder = selectedImage = null;
                $imagesContainer.children().remove();
                $foldersContainer.children().not("#closeFolders").remove();
                viewModel = {
                    root: {},
                    $folder: [],
                    $images: []
                };
            }
        };
    }
)
;
