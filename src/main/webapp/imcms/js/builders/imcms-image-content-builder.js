/**
 * Created by Serhii Maksymchuk from Ubrainians for imCode
 * 16.08.17.
 */
define("imcms-image-content-builder",
    [
        "imcms-image-files-rest-api", "imcms-image-folders-rest-api", "imcms-bem-builder", "imcms-components-builder",
        "imcms-image-metadata-builder", "imcms-primitives-builder", "imcms-modal-window-builder", "jquery", "imcms-i18n-texts", 'imcms'
    ],
    function (imageFilesREST, imageFoldersREST, BEM, components,
              imageMetadataWindowBuilder, primitives, modal, $, texts, imcms) {
        const OPENED_FOLDER_BTN_CLASS = "imcms-folder-btn--open";
        const SUBFOLDER_CLASS = "imcms-folders__subfolder";
        const ACTIVE_FOLDER_CLASS = "imcms-folder--active";
        const FOLDER_CREATION_BLOCK_ID = "imcms-folder-create-block";
        const ROOT_FOLDER_LEVEL = 0;
        let activeFolder;

        texts = texts.editors.content;

        let selectedFullImagePath;
	    let selectedImageChanged = false;

        let $foldersContainer, $imagesContainer, selectedImage, $saveAndCloseBtn, $sortingSelect;

        let viewModel = {
            root: {},
            folders: [],
            $images: []
        };

        const sortingValues = {
            default: 'default',
            nameAsc: 'name-asc',
            nameDesc: 'name-desc',
            dateNewFirst: 'date-new-first',
            dateOldFirst: 'date-old-first',
        };

        const rootFolderBEM = new BEM({
            block: "imcms-left-side",
            elements: {
                "controls": "imcms-main-folders-controls",
                "folders": "imcms-folders"
            }
        });

        function onFolderRenamed(oldFolderPath, response) {

            this.$block.parent().attr("data-folder-name", this.name);

            this.$block.prev()
                .find(".imcms-folder__name")
                .text(this.name);

            this.$block.remove();

            if (selectedImage) {
                const newFolderPath = this.path;
                const oldImageFullPath = selectedImage.path;

                selectedFullImagePath = oldImageFullPath.replace(oldFolderPath, newFolderPath)
                selectedImage.path = selectedFullImagePath;
                selectedImageChanged = true;
            }
        }

        function onFolderCreated(oldFolderPath, response) {
            const newFolder = {
                name: this.name,
                path: this.path,
                $images: [],
                files: []
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
        }

        const folderControlsBuilder = {
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
            }).buildBlockStructure("<div>", {}).on({
	            'dragenter': onDragEnterFolderHandler,
	            'dragleave': onDragLeaveRootFolderHandler,
	            'dragover': onDragOverFolderHandler,
	            'drop': (event) => {
		            onDropFolderHandler(event, rootFile);
		            removeBorderFromRootFolder(event.target);
	            },
	            'click':function (e) {
					e.preventDefault();
					e.stopPropagation();
					onFolderClick.call(this, rootFile)
	            }
            });
        }

        function buildRootFolder(rootFile) {
            rootFile.$folder = $foldersContainer;
            const $rootControls = buildRootControls(rootFile);

            return rootFolderBEM.makeBlockElement("controls", $rootControls);
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
                onDoneRemoveFolder($folder);
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

            return `${relativePath}`;
        }

        function buildFolderManageBlock(opts, onConfirm, onSuccess) {
            $(`#${FOLDER_CREATION_BLOCK_ID}`).remove();

            const $folderNameInput = primitives.imcmsInput({
                "class": "imcms-input",
                value: opts.previousFolderName,
                placeholder: texts.newFolderName
            });

            const $closeBtn = components.controls.remove(() => $(`#${FOLDER_CREATION_BLOCK_ID}`).remove());
            components.controls.create($closeBtn.attr("title", texts.createFolderImage));

            const $loadingAnimation = $('<div>', {
                class: 'loading-animation',
                style: 'display: none'
            });

            const $confirmBtn = components.buttons.neutralButton({
                "class": "imcms-button",
                text: texts.add,
                click: () => {
                    let folderName = $folderNameInput.val().replace(/\s/g, '_');

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
                        dataOnConfirm.path = contextOnSuccess.path = `${path}/${folderName}/`;

                    } else {
                        const pathSplitBySeparator = path.split("/");
                        pathSplitBySeparator[pathSplitBySeparator.length - 1] = folderName;

                        dataOnConfirm.path = path;
                        contextOnSuccess.name = folderName;
                        contextOnSuccess.path = pathSplitBySeparator.join("/") + "/";
                    }

                    $confirmBtn.css("display", "none");
                    $closeBtn.attr("style", "display: none !important");
                    $loadingAnimation.css("display", "");

                    onConfirm(dataOnConfirm)
                        .done(response => {
                            const oldFolderPath = opts.folder.path;

                            if (!isNewFolder) {
                                opts.folder.name = contextOnSuccess.name;
                                opts.folder.path = contextOnSuccess.path;

                                if(opts.folder.folders) opts.folder.folders.forEach(subFolder =>
                                    subFolder.path = subFolder.path.replace(oldFolderPath, contextOnSuccess.path));
                            }
                            onSuccess.call(contextOnSuccess, oldFolderPath, response);
                        })
                        .fail(() => modal.buildErrorWindow(texts.error.addFolderFailed))
                        .always(() => {
                            $confirmBtn.css("display", "");
                            $closeBtn.css("display", "");
                            $loadingAnimation.css("display", "none");
                        });
                }
            });

            const $folderCreationBlock = new BEM({
                block: "imcms-panel-named",
                elements: {
                    "input": $folderNameInput,
                    "button": $confirmBtn,
                    "control-close": $closeBtn,
                    "loading": $loadingAnimation
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

        function openSubFolders(e) {
			e.preventDefault();
			e.stopPropagation();
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
            sortImagesBySortingValue(getSelectedSortingBy());

            setUpSelectedImage(activeFolder, getImageNameFromPath(selectedFullImagePath));
            scrollToSelectedImage();
        }

        function onFolderClick(folder) {
            activeFolder = folder;
	        selectedImageChanged = false;
            $(`.${ACTIVE_FOLDER_CLASS}`).removeClass(ACTIVE_FOLDER_CLASS);
            $(this).addClass(ACTIVE_FOLDER_CLASS);

            viewModel.$images.forEach($image => {
                $image.css("display", "none");
            });

            folder.imagesAreLoaded ? showImagesIn(folder) : loadImages(folder);
        }

        function loadImages(folder) {
            imageFoldersREST.read({"path": folder.path})
                .done(
                    imagesFolder => {
                        folder.imagesAreLoaded = true;
                        folder.files = imagesFolder.files;

                        showImagesIn(folder);
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
            }).on({
	            'dragenter': onDragEnterFolderHandler,
	            'dragleave': onDragLeaveFolderHandler,
	            'dragover': onDragOverFolderHandler,
	            'drop': (event) => {
					onDropFolderHandler(event, subfolder);
		            removeBorderFromFolder(event.target);
	            },
	            'click': function (e) {
					e.preventDefault();
					e.stopPropagation();
					onFolderClick.call(this, subfolder);
		            openSubFoldersOnDoubleClick(this)
	            }
            });
        }

	    function onDragEnterFolderHandler(event) {
		    if (!isTargetFolderTheSameAsCurrent(this))
			    addBorderToFolder(this);
		    openSubFoldersOnDoubleClick(this);
	    }

	    function onDragLeaveFolderHandler(event) {
		    if (!isTargetFolderTheSameAsCurrent(this))
			    removeBorderFromFolder(this);
	    }

	    function onDragLeaveRootFolderHandler(event) {
		    if (!isTargetFolderTheSameAsCurrent(this)) {
			    removeBorderFromRootFolder(this);
		    }
	    }

	    function onDragOverFolderHandler(event) {
		    event.preventDefault();
	    }

	    const $loadingAnimation = $("<div class='loading-animation'></div>");

	    function onDropFolderHandler(event, subfolder) {
		    if (!isTargetFolderTheSameAsCurrent(event.target)) {
			    const imageFileName = dragged.imageData.name;
			    const imageFileFolder = dragged.imageData.path;
			    const destinationFolder = subfolder.path;

			    dragged.imageData.name = imageFileFolder; //file path
			    dragged.imageData.path = destinationFolder + imageFileName; //destination path

			    showLoadingAnimation(dragged.imageElement);
			    imageFilesREST.moveImageFile(dragged.imageData)
				    .done((imageFile) => {
					    selectedImageChanged = false;
					    dragged.folderData.files = removeElementFromArray(dragged.folderData.files, dragged.imageData);
					    dragged.folderData.$images = removeElementFromArray(dragged.folderData.$images, dragged.imageElement);

					    subfolder.files.push(imageFile);
					    if(subfolder.$images) subfolder.$images.push(buildImage(imageFile, subfolder));

						refreshOnFolderClickListener(dragged.folderData);
						refreshOnFolderClickListener(subfolder);

					    dragged.imageElement.remove();
					    selectedFullImagePath = selectedFullImagePath.startsWith('/') ? selectedFullImagePath.substring(1) : selectedFullImagePath;
						if (selectedFullImagePath === dragged.imageData.name) {
						    selectedFullImagePath = imageFile.path;
						    selectedImage = imageFile;
						    selectedImageChanged = true;
					    }

					    $saveAndCloseBtn && $saveAndCloseBtn.removeAttr('disabled', 'disabled').addClass('imcms-button--disabled');
				    }).fail(()=>modal.buildErrorWindow("Error occurred"))
		    }
	    }

		function showLoadingAnimation(imageElement) {
			$(imageElement).find('.imcms-choose-img-description').append($loadingAnimation);
		}

	    function refreshOnFolderClickListener(folder) {
		    $(folder.$folder.children()[0]).off('click').on('click', function (e) {
				e.preventDefault();
				e.stopPropagation();
				onFolderClick.call(this, folder);
			    openSubFoldersOnDoubleClick(this)
		    })
	    }

	    function removeElementFromArray(array, elementToRemove) {
		    return $.grep(array, (element) => element !== elementToRemove)
	    }

	    function isTargetFolderTheSameAsCurrent(currentFolder) {
		    return dragged.folderData.$folder.children()[0] === $(currentFolder).parent().children()[0];
		}

		function openSubFoldersOnDoubleClick(folder) {
			$(folder).find(".imcms-folder__btn:not(.imcms-folder-btn--open)").click();
		}

	    function addBorderToFolder(folder) {
		    $(folder).css({
			    'border': '2px #0b94d8 dotted'
		    });
	    }

	    function removeBorderFromFolder(folder) {
		    $(folder).css({
			    'border': '2px solid transparent'
		    });
	    }

	    function removeBorderFromRootFolder(rootFolder) {
		    $(rootFolder).css({
			    'border': '2px solid transparent',
			    'border-bottom':'2px solid #d3d8de'
		    })
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
                .done(response => {
                    response && $(element).parent().parent().remove();

                    const indexOfImageFileForDeleting = activeFolder.files.findIndex(file => file.name === imageFile.name);
                    activeFolder.files.splice(indexOfImageFileForDeleting, 1);
                })
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
                        href: `${imcms.imagesPath}?path=${imageFile.path}`,
                        title: texts.openImage,
                        target: '_blank'
                    }),
                    'edit-metadata': components.buttons.editMetadataButton({
                        title: texts.editMetadata,
                        click: (event) => {
                            let $deleteImage = $(event.target).closest(".imcms-choose-img-wrap");
                            let onMetadataSavedCallback = imageFile => onMetadataSaved(imageFile, $deleteImage);
                            imageMetadataWindowBuilder.buildImageMetadataEditor(selectedImage, onMetadataSavedCallback);
                        },
                    })
                }
            }).buildBlockStructure("<div>");
        }

        function onMetadataSaved(imageFile, $deleteImage){
            const indexOfImageFileForDeleting = activeFolder.files.findIndex(file => file.name === imageFile.name);
            activeFolder.files.splice(indexOfImageFileForDeleting, 1);

            const $newImage = buildImageImmediately(imageFile, activeFolder);
            highlightLastAddedImage($newImage);
            $deleteImage.replaceWith($newImage);
            selectImage.call($newImage, imageFile);

            activeFolder.files = (activeFolder.files || []).concat(imageFile);
            activeFolder.$images = activeFolder.$images.concat($newImage);
            viewModel.$images = viewModel.$images.concat($newImage);
        }

        function selectImage(imageFile) {
            $saveAndCloseBtn && $saveAndCloseBtn.removeAttr('disabled').removeClass('imcms-button--disabled');
            $(".image-chosen").removeClass("image-chosen");
            $(this).addClass("image-chosen");
            selectedImage = imageFile;
        }

	    let $imageContent,
		    dragged={}
	    ;

        //build image with lazy loading!
        function buildImage(imageFile, folder) {
            let dataSrc = `${imcms.imagesPath}?path=${imageFile.path}`;
            //Reduce image weight by resizing
            if(imageFile.size.toLowerCase().endsWith("mb")){
                dataSrc += "&height=146";
            }
            imageFile.src = dataSrc;

            return new BEM({
                block: "imcms-choose-img-wrap",
                elements: {
                    "img": $("<img>", {
                        "class": "imcms-choose-img",
                        "data-src": dataSrc,    //for lazy loading
                        style: `background-image: url('${imcms.contextPath}/imcms/images/ic_loader.gif')`,
                        "draggable": "false"
                    }),
                    "description": buildImageDescription(imageFile)
                }
            }).buildBlockStructure("<div>", {
                "data-image-name": imageFile.name,
                style: "display: none",
            }).on({
	            'mousedown': function (event) {
		            $imageContent = $(this);
		            onMouseDownImageHandler(imageFile)
	            },
	            'mouseup dragend': onMouseUpAndDragEndImageHandler,
	            'dragstart': (event)=> onDragStartImageHandler(event, folder, imageFile),
	            'drag': function (event) {
	            }
            });
        }

        function buildImageImmediately(imageFile, folder) {
            let $imgContainer = buildImage(imageFile, folder).css("display", "block");

            let $img = $imgContainer.find('img');
            $img.attr("src", $img.data("src")).removeAttr("data-src").removeAttr("style");

            return $imgContainer;
        }

	    function onMouseDownImageHandler( imageFile) {
		    selectImage.call($imageContent, imageFile);
		    selectedImageChanged = false;
			$imageContent.css({'border': '2px #0b94d8 dotted'}).attr('draggable', true);
	    }

		function onMouseUpAndDragEndImageHandler(event) {
			$imageContent.css('border', '2px transparent solid').attr('draggable', false)
		}

	    function onDragStartImageHandler(event, folder, imageFile) {
		    dragged.imageElement = event.target;
		    dragged.imageData = imageFile;
		    dragged.folderData = folder;
	    }

        function buildImages(folder) {
            buildImagesNotRecursive(folder);
            (folder.folders || []).forEach(buildImages);
        }

        function buildImagesNotRecursive(folder) {
	        folder.$images = folder.files.map((imageFile) => buildImage(imageFile, folder));
            viewModel.$images = viewModel.$images.concat(folder.$images);

            //Start lazy loading
            let numberOfUnloaded = folder.$images.length;
            let loadedImages = [];

            folder.$images.forEach($imageContainer => {
                $imageContainer.css("display", "block");

                let $img = $imageContainer.find('img');
                //Preload
                let newImg = new Image;
                newImg.onload = function () {
                    loadedImages.unshift($img);

                    numberOfUnloaded -= 1;
                }
                newImg.src = $img.data("src");
            });

            let setImage = function (){
                if(numberOfUnloaded || loadedImages.length){
                    let $img = loadedImages.pop();
                    if($img){
                        $img.attr("src", $img.data("src")).on("load", () => setImage());
                        $img.removeAttr("data-src").removeAttr("style");
                    }else{
                        setTimeout(setImage, 300);
                    }
                }
            }
            //Limit the number of images putted at the same time
            for(let i = 0; i < 3; i++){
                setTimeout(setImage, 300);
            }
            //End lazy loading
        }

	    function scrollToSelectedImage() {
		    const selectedImage = $('.image-chosen:first');
		    if (selectedImage.length) {
			    $(".imcms-right-side__images-container").scrollTop(selectedImage.offset().top - selectedImage.height());
		    }
	    }

        function loadImageFoldersContent(imagesRootFolder) {
            viewModel.root = imagesRootFolder;
            viewModel.root.imagesAreLoaded = true;

            if (!viewModel.folders) {
                viewModel.folders = [];
            }

            viewModel.folders.push(buildRootFolder(viewModel.root));

            selectedFullImagePath = selectedFullImagePath ? selectedFullImagePath : '';
            const slashLastIndex = selectedFullImagePath.lastIndexOf("/");

            const path = selectedFullImagePath.substring(0, slashLastIndex);
            const fixedPath = `${path}${path.slice(-1) === '/' ? '' : '/'}`;

            const $subfolders = buildSubFolders(viewModel.root, ROOT_FOLDER_LEVEL + 1, fixedPath).map($subfolder => rootFolderBEM.makeBlockElement("folders", $subfolder));

            viewModel.folders = viewModel.folders.concat($subfolders);

            $foldersContainer.append(viewModel.folders);
            $imagesContainer.append(viewModel.$images);

            if (activeFolder) {
                openParentFolders(activeFolder);
                loadImages(activeFolder);
            } else {
                activeFolder = imagesRootFolder;
                activeFolder.$folder.find('.imcms-main-folders-controls').addClass(ACTIVE_FOLDER_CLASS);
                showImagesIn(viewModel.root);
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

        function buildSortingSelect() {
            return $sortingSelect = components.selects.imcmsSelect("<div>", {
                text: texts.sortBy,
                onSelected: sortImagesBySortingValue,
            }, [{
                text: texts.sorting.default,
                "data-value": sortingValues.default,
            }, {
                text: texts.sorting.az,
                "data-value": sortingValues.nameAsc,
            }, {
                text: texts.sorting.za,
                "data-value": sortingValues.nameDesc,
            }, {
                text: texts.sorting.dateNewFirst,
                "data-value": sortingValues.dateNewFirst,
            }, {
                text: texts.sorting.dateOldFirst,
                "data-value": sortingValues.dateOldFirst,
            }]);
        }

        function getSelectedSortingBy() {
            return $sortingSelect.getSelectedValue() || sortingValues.default;
        }

        function sortImagesBySortingValue(sortingValue) {
            switch (sortingValue) {
                case sortingValues.nameAsc: {
                    sortImagesWithCompareFunction((v1, v2) => v1.name.localeCompare(v2.name));
                    return;
                }
                case sortingValues.nameDesc: {
                    sortImagesWithCompareFunction((v1, v2) => v2.name.localeCompare(v1.name));
                    return;
                }
                case sortingValues.dateNewFirst: {
                    sortImagesWithCompareFunction((v1, v2) => Date.parse(v2.uploaded) - Date.parse(v1.uploaded));
                    return;
                }
                case sortingValues.dateOldFirst: {
                    sortImagesWithCompareFunction((v1, v2) => Date.parse(v1.uploaded) - Date.parse(v2.uploaded));
                    return;
                }
                case sortingValue.default:
                default: {
                    sortByDefault();
                    return;
                }
            }
        }

        function sortByDefault() {
            const lastAddedFiles = activeFolder.files
                .sort((v1, v2) => Date.parse(v2.uploaded) - Date.parse(v1.uploaded))
                .slice(0, 5);

            const lastAddedFilesNames = lastAddedFiles.map(file => file.name);

            const sortedByNameWithoutLastAddedFiles = activeFolder.files
                .sort((v1, v2) => v1.name.localeCompare(v2.name))
                .filter(file => !lastAddedFilesNames.includes(file.name));

            sortedByNameWithoutLastAddedFiles.unshift(...lastAddedFiles);

            activeFolder.files = sortedByNameWithoutLastAddedFiles;

            rebuildImagesContainerContent();

            activeFolder.$images.slice(0, 5).forEach(highlightLastAddedImage);
        }

        function highlightLastAddedImage($image) {
            $image.addClass('imcms-last-added-img');
        }

        function sortImagesWithCompareFunction(compareFunction) {
            activeFolder.files = activeFolder.files.sort(compareFunction);

            rebuildImagesContainerContent();
        }

		function getImageNameFromPath(path) {
			const slashLastIndex = path.lastIndexOf("/");
			return path.substring(slashLastIndex + 1);
		}

        function rebuildImagesContainerContent() {
            $saveAndCloseBtn && $saveAndCloseBtn.attr('disabled', 'disabled').addClass('imcms-button--disabled');

            viewModel.$images = viewModel.$images.filter($image => $image.css('display') === 'none');
            if(activeFolder.$images) activeFolder.$images.forEach($image => $image.remove());
            activeFolder.$images = null;

            buildImagesNotRecursive(activeFolder);

            activeFolder.$images.forEach($image => {
                if (getImageNameFromPath(selectedFullImagePath) === $image.find(".imcms-title").text()) {
                    $image.addClass("image-chosen");
                    $saveAndCloseBtn && $saveAndCloseBtn.removeAttr('disabled').removeClass('imcms-button--disabled');
                }
                $image.css('display', 'block')
            });
            $imagesContainer.append(activeFolder.$images);
        }

        return {
            buildSortingSelect,
            getSelectedImage: () => selectedImage,
	        isSelectedImageChanged: () => selectedImageChanged,
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
                        const $newImages = uploadedImageFiles.map(imageFile => buildImageImmediately(imageFile, activeFolder));
                        $newImages.forEach(highlightLastAddedImage);
                        activeFolder.files = (activeFolder.files || []).concat(uploadedImageFiles);
                        $imagesContainer.prepend($newImages);
                        activeFolder.$images = activeFolder.$images.concat($newImages);
                        viewModel.$images = viewModel.$images.concat($newImages);
                        selectImage.call($newImages[0], uploadedImageFiles[0]);
                        selectedImageChanged = false;
                    })
                    .fail(() => modal.buildErrorWindow(texts.error.uploadImagesFailed));
            },
            clearContent: () => {
                activeFolder = selectedImage = null;
	            selectedImageChanged = false;
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
