/**
 * Created by Serhii Maksymchuk from Ubrainians for imCode
 * 14.08.17.
 */
define("imcms-document-editor-builder",
    [
        "imcms-bem-builder", "imcms-page-info-builder", "imcms-components-builder", "imcms-primitives-builder",
        "imcms-documents-rest-api", "imcms-documents-search-rest-api", "imcms-users-rest-api",
        "imcms-categories-rest-api", "imcms-window-builder", "jquery", "imcms", "imcms-modal-window-builder",
        "imcms-document-type-select-window-builder", "imcms-i18n-texts", "imcms-events",
        "imcms-document-profile-select-window-builder", "imcms-document-copy-rest-api",
        "imcms-modal-window-builder"
    ],
    function (BEM, pageInfoBuilder, components, primitives, docRestApi, docSearchRestApi, usersRestApi,
              categoriesRestApi, WindowBuilder, $, imcms, imcmsModalWindowBuilder, docTypeSelectBuilder, texts, events,
              docProfileSelectBuilder, docCopyRestApi, modal) {

        texts = texts.editors.document;

        let TREE_SORT = 'TREE_SORT';
        let WORKING_VERSION = 0;
        let topPointMenu = 221; // top point menu for set item before item in the top position. Improve it if you can

        let isMouseDown = false,
            mouseCoords = {
                pageX: undefined,
                pageY: undefined,
                newPageX: undefined,
                newPageY: undefined
            },
            $menuArea,
            menuAreaProp = {
                top: 0,
                left: 0,
                right: 0,
                bottom: 0
            }
        ;

        let $documentsContainer, $editorBody, $documentsList;

        let currentDocumentNumber = 0;

        const term = "term";
        const userId = "userId";
        const categoriesId = "categoriesId";
        const sortProperty = "page.property";
        const sortDirection = "page.direction";

        const pageSkip = "page.skip";

        const defaultSortPropertyValue = "meta_id";
        const asc = "ASC";
        const desc = "DESC";

        const searchQueryObj = {
            "term": "",
            "userId": null,
            "categoriesId": {},
            "page.skip": currentDocumentNumber
        };

        let sendSearchDocRequest = true;
        let errorMsg;

        function buildErrorBlock() {
            errorMsg = components.texts.errorText("<div>", texts.error.searchFailed, {style: 'display: none;'});
            return errorMsg;
        }

        function appendDocuments(field, value, removeOldDocuments, setDefaultSort) {
            setField(field, value);

            if (removeOldDocuments) {
                $documentsList.empty();
                sendSearchDocRequest = true;
                currentDocumentNumber = 0;
                setField(pageSkip, currentDocumentNumber);
            }

            if (setDefaultSort) {
                setDefaultSortProperties();
            }

            docSearchRestApi.read(searchQueryObj)
                .done(documentList => {
                    pushDocumentsInArray(documentList);
                    if (!documentList || (documentList.length === 0)) {
                        sendSearchDocRequest = false;
                        errorMsg.slideDown();
                        return;
                    } else {
                        errorMsg.slideUp();
                    }
                    incrementDocumentNumber(documentList.length);
                    documentList.forEach(document => {
                        $documentsList.append(buildDocument(document, currentEditorOptions));
                    });
                })
                .fail(() => {
                    errorMsg.slideDown();
                });
        }

        function setField(field, value) {
            searchQueryObj[field] = value;
        }

        function addDocumentToList(document) {
            const $document = buildDocument(document, currentEditorOptions);
            $documentsList.prepend($document); // todo: replace append by pasting into correct position in sorted list

            incrementDocumentNumber(1);
        }

        let $textField;

        function buildBodyHeadTools() {

            function onNewDocButtonClick(e) {
                e.preventDefault();
                docTypeSelectBuilder.build(type => {
                    docProfileSelectBuilder.build(parentDocId => {
                        pageInfoBuilder.build(null, addDocumentToList, type, parentDocId);
                    });
                });
            }

            function buildNewDocButton() {
                return components.buttons.negativeButton({
                    text: texts.newDoc,
                    click: onNewDocButtonClick
                });
            }

            function buildSearchDocField() {

                $textField = components.texts.textField("<div>", {
                    id: "searchText",
                    name: "search",
                    placeholder: texts.freeTextPlaceholder,
                    text: texts.freeText
                });

                $textField.$input.on("input", function () {
                    const textFieldValue = $(this).val().toLowerCase().trim();
                    if (searchQueryObj[term] !== textFieldValue) {
                        appendDocuments(term, textFieldValue, true, true);
                    }
                });

                return new BEM({
                    block: "imcms-input-search",
                    elements: {
                        "text-box": $textField,
                        "button": components.buttons.searchButton()
                    }
                }).buildBlockStructure("<div>");
            }

            function buildUsersFilterSelect() {
                const onSelected = value => {
                    if (searchQueryObj[userId] !== value) {
                        appendDocuments(userId, value, true, true);
                    }
                };

                const $usersFilterSelectContainer = components.selects.selectContainer("<div>", {
                    id: "users-filter",
                    name: "users-filter",
                    text: texts.owner,
                    emptySelect: true,
                    onSelected: onSelected
                });

                usersRestApi.getAllAdmins()
                    .done(users => {
                        const usersDataMapped = users.map(user => ({
                            text: user.login,
                            "data-value": user.id
                        }));

                        components.selects.addOptionsToSelect(
                            usersDataMapped, $usersFilterSelectContainer.getSelect(), onSelected
                        );
                    })
                    .fail(() => modal.buildErrorWindow(texts.error.userLoadFailed));

                return $usersFilterSelectContainer;
            }

            function buildCategoriesFilterSelect() {
                const onSelected = value => {
                    if (searchQueryObj[categoriesId][0] !== value) {
                        appendDocuments(categoriesId, {0: value}, true, true);
                    }
                };

                const $categoriesFilterSelectContainer = components.selects.selectContainer("<div>", {
                    id: "categories-filter",
                    name: "categories-filter",
                    text: texts.category,
                    emptySelect: true,
                    onSelected: onSelected
                });

                categoriesRestApi.read(null)
                    .done(categories => {
                        const categoriesDataMapped = categories.map(category => ({
                            text: category.name,
                            "data-value": category.id
                        }));

                        components.selects.addOptionsToSelect(
                            categoriesDataMapped, $categoriesFilterSelectContainer.getSelect(), onSelected);
                    })
                    .fail(() => modal.buildErrorWindow(texts.error.categoriesLoadFailed));

                return $categoriesFilterSelectContainer;
            }

            const toolBEM = new BEM({
                block: "imcms-document-editor-head-tool",
                elements: {
                    "search": "imcms-input-search",
                    "button": "imcms-button"
                }
            });

            const $newDocButtonContainer = toolBEM.buildBlock("<div>", [{"button": buildNewDocButton()}]);
            $newDocButtonContainer.modifiers = ["grid-col-2"];

            const $searchContainer = toolBEM.buildBlock("<div>", [{"search": buildSearchDocField()}]);
            $searchContainer.modifiers = ["grid-col-4"];

            const $usersFilter = toolBEM.buildBlock("<div>", [{"select": buildUsersFilterSelect()}]);
            $usersFilter.modifiers = ["grid-col-3"];

            const $categoriesFilter = toolBEM.buildBlock("<div>", [{"select": buildCategoriesFilterSelect()}]);
            $categoriesFilter.modifiers = ["grid-col-3"];

            return new BEM({
                block: "imcms-document-editor-head-tools",
                elements: {
                    "tool": [
                        $newDocButtonContainer,
                        $searchContainer,
                        $usersFilter,
                        $categoriesFilter
                    ]
                }
            }).buildBlockStructure("<div>");
        }

        function buildBodyHead() {
            return new BEM({
                block: "imcms-document-editor-head",
                elements: {
                    "tools": buildBodyHeadTools(),
                    "error-search": buildErrorBlock()
                }
            }).buildBlockStructure("<div>");
        }

        function setDefaultSortProperties() {
            searchQueryObj[sortProperty] = defaultSortPropertyValue;
            searchQueryObj[sortDirection] = desc;
        }

        function isAlreadySortedBy(bySorting) {
            return (searchQueryObj[sortProperty] === bySorting);
        }

        function isAlreadyAscendingSorting() {
            return (searchQueryObj[sortDirection] === asc);
        }

        function setSortingDirection(sorting) {
            searchQueryObj[sortDirection] = sorting;
        }

        function isDefaultSorting(bySorting) {
            return (bySorting === defaultSortPropertyValue);
        }

        function setSortBy(bySorting) {
            searchQueryObj[sortProperty] = bySorting;
            searchQueryObj[sortDirection] = asc;
        }

        function highlightDefaultSorting() {
            const $defaultSortingHeader = $(".imcms-document-list-titles__title").first();
            highlightSorting($defaultSortingHeader);
        }

        function highlightSorting($sortingHeader) {
            if (isActiveHeader($sortingHeader)) {
                const $sortingIcon = $sortingHeader.find(".imcms-document-list-title-row__icon");
                toggleSortingIcon($sortingIcon)
            }

            $(".imcms-document-list-titles__title--active").removeClass("imcms-document-list-titles__title--active");
            $sortingHeader.addClass("imcms-document-list-titles__title--active");
        }

        function toggleSortingIcon($sortingIcon) {
            const sortUpClassName = "imcms-control--sort-up";
            const sortDownClassName = "imcms-control--sort-down";

            $sortingIcon.hasClass(sortUpClassName)
                ? $sortingIcon.removeClass(sortUpClassName).addClass(sortDownClassName)
                : $sortingIcon.removeClass(sortDownClassName).addClass(sortUpClassName);
        }

        function isActiveHeader($sortingHeader) {
            return $sortingHeader.hasClass("imcms-document-list-titles__title--active");
        }

        function processSameSorting(bySorting, $sortingHeader) {
            if (isAlreadyAscendingSorting()) {
                setSortingDirection(desc);
                highlightSorting($sortingHeader);

            } else if (isDefaultSorting(bySorting)) {
                setSortingDirection(asc);
                highlightSorting($sortingHeader);

            } else {
                setDefaultSortProperties();
                highlightDefaultSorting();
            }
        }

        function onClickSorting(bySorting, $sortingHeader) {
            if (isAlreadySortedBy(bySorting)) {
                processSameSorting(bySorting, $sortingHeader);

            } else {
                setSortBy(bySorting);
                highlightSorting($sortingHeader);
            }

            appendDocuments(sortProperty, searchQueryObj[sortProperty], true, false);
        }

        function buildDocumentListTitlesRow() {
            const $idColumnHead = buildTitleRow({
                text: texts.sort.id,
                bySorting: defaultSortPropertyValue,
                modifiers: ["col-1"],
            });

            const $titleColumnHead = buildTitleRow({
                text: texts.sort.title,
                bySorting: "meta_headline_" + imcms.userLanguage,
                modifiers: ["col-3"],
            });

            const $aliasColumnHead = buildTitleRow({
                text: texts.sort.alias,
                bySorting: "alias",
                modifiers: ["col-2"],
            });

            const $modifiedColumnHead = buildTitleRow({
                text: texts.sort.modified,
                bySorting: "modified_datetime",
                modifiers: ["col-1"],
            });

            const $publishedColumnHead = buildTitleRow({
                text: texts.sort.published,
                bySorting: "publication_start_datetime",
                modifiers: ["col-1"],
            });

            const $versionColumnHead = buildTitleRow({
                text: texts.sort.version,
                bySorting: 'version_no',
                modifiers: ['col-1'],
            });

            const $typeColumnHead = buildTitleRow({text: texts.sort.type, modifiers: ["col-1"]});

            const $statusColumnHead = buildTitleRow({text: texts.sort.status, modifiers: ["col-2"]});

            return new BEM({
                block: "imcms-document-list-titles",
                elements: {
                    "title": [
                        $idColumnHead,
                        $titleColumnHead,
                        $aliasColumnHead,
                        $modifiedColumnHead,
                        $publishedColumnHead,
                        $versionColumnHead,
                        $typeColumnHead,
                        $statusColumnHead
                    ]
                }
            }).buildBlockStructure("<div>");
        }

        function buildTitleRow({text, bySorting, modifiers}) {
            const emptyIcon = $('<div>');

            const sortIcon = bySorting
                ? components.controls.sortDown()
                : emptyIcon;

            const titleRow = $("<div>", {
                text: text,
            });

            const titleRowBem = new BEM({
                block: "imcms-document-list-title-row",
                elements: {
                    "title": titleRow,
                    "icon": sortIcon,
                }
            }).buildBlockStructure("<div>", {
                click: function () {
                    if (bySorting) {
                        onClickSorting(bySorting, $(this))
                    }
                },
            });
            titleRowBem.modifiers = modifiers;

            return titleRowBem;
        }

        function createFrame(event) {
            const $this = $(this),
                original = $this.closest(".imcms-document-items"),
                $frame = original.clone(),
                $frameLayout = $("<div>"),
                frameItem = $frame.find(".imcms-document-item")
            ;

            const frameItemDocId = frameItem.find(".imcms-document-item__info--id").text();
            const requestDataId = {
                docId: parseInt(frameItemDocId)
            };

            $menuArea = $(".imcms-menu-items-tree");
            $frameLayout.addClass("imcms-frame-layout")
                .css({
                    "display": "none",
                    "position": "fixed",
                    "top": 0,
                    "left": 0,
                    "width": "100%",
                    "height": "100%",
                    "background": "transparent",
                    // "opacity": 0,
                    "z-index": 10101
                });
            $frameLayout.appendTo($("body"));

            original.addClass("imcms-document-items--is-drag");

            isMouseDown = true;
            mouseCoords = {
                pageX: event.clientX,
                pageY: event.clientY,
                top: $this.closest(".imcms-document-items").position().top,
                left: $this.closest(".imcms-document-items").position().left
            };
            menuAreaProp = {
                top: $menuArea.position().top,
                left: $menuArea.position().left,
                right: menuAreaProp.left + $menuArea.outerWidth(),
                bottom: menuAreaProp.top + $menuArea.outerHeight()
            };

            docRestApi.read(requestDataId).done(docById => {
                let publishedDate = docById.published.date && docById.published.time !== null
                    ? docById.published.date + ' ' + docById.published.time
                    : '';

                let modifiedDate = docById.modified.date && docById.modified.time !== null
                    ? docById.modified.date + ' ' + docById.modified.time
                    : '';
                frameItem.attr("data-publishedDate", publishedDate);
                frameItem.attr("data-modifiedDate", modifiedDate);
            });

            frameItem.attr("data-id", frameItem.find(".imcms-document-item__info--id").text());
            frameItem.attr("data-title", frameItem.find(".imcms-document-item__info--title").text());
            frameItem.attr("data-type", frameItem.find(".imcms-document-item__info--type").text());
            frameItem.attr("data-status", frameItem.find(".imcms-document-item__info--status").text());
            frameItem.attr("data-original-status", frameItem.find(".imcms-document-item__info--originalStatus").text());
            frameItem.attr("data-current-version", frameItem.find(".imcms-document-item__info--currentVersion").attr('value'));

            let widthValue = document.getElementById("type-sort").value === TREE_SORT ? '450px' : '40%';

            $frame.addClass("imcms-document-items--frame");
            $frame.css({
                "background-color": "#e9e9f5",
                "position": "absolute",
                "z-index": 11001,
                "width": widthValue,
                "top": mouseCoords.top,
                "left": mouseCoords.left
            });

            toggleUserSelect(true);
            $frame.appendTo("body");
        }

        $(document).on("mousemove", event => {
            if (!isMouseDown) {
                const $dragDoc = $(".imcms-document-items--is-drag");
                $dragDoc.removeClass("imcms-document-items--is-drag");
                return;
            }
            moveFrame(event);
        });

        function refreshDocumentInList(document) {
            if ($documentsList === undefined || null === document) return;
            const $oldDocumentElement = $documentsList.find("[data-doc-id=" + document.id + "]");

            if ($oldDocumentElement.length === 1) {
                const $newDocumentElement = buildDocument(document, currentEditorOptions);
                $oldDocumentElement.replaceWith($newDocumentElement);
            }
        }

        function buildDocItemControls(document, opts) {
            const controls = [];
            opts = opts || {};

            if (opts.removeEnable) {
                const $controlRemove = components.controls.remove(function () {
                    removeDocument.call(this, document);
                });
                controls.push($controlRemove);
            }

            if (opts.copyEnable) {
                function onConfirm() {
                    docCopyRestApi.copy(document.id)
                        .done(copiedDocument => {
                            addDocumentToList(copiedDocument);
                        })
                        .fail(() => modal.buildErrorWindow(texts.error.copyDocumentFailed));
                }

                const $controlCopy = components.controls.copy(() => {
                    modal.buildConfirmWindow(
                        texts.controls.copy.confirmMessage + document.id + '?',
                        onConfirm
                    );
                });

                $controlCopy.prop('title', texts.controls.copy.title);
                controls.push($controlCopy);
            }

            if (opts.editEnable) {
                const $controlEdit = components.controls.edit(() => {
                    pageInfoBuilder.build(document.id, refreshDocumentInList, document.type);
                });
                $controlEdit.prop('title', texts.controls.edit.title);
                controls.push($controlEdit);
            }

            return components.controls.buildControlsBlock("<div>", controls);
        }

        function moveFrame(event) {
            const $frame = $(".imcms-document-items--frame");
            mouseCoords.newPageX = event.clientX;
            mouseCoords.newPageY = event.clientY;

            if (isMouseDown) {
                $frame.css({
                    "top": (mouseCoords.newPageY - mouseCoords.pageY) + mouseCoords.top,
                    "left": (mouseCoords.newPageX - mouseCoords.pageX) + mouseCoords.left
                });

                if (detectTargetArea(event)) {
                    if ($menuArea.css("border-color") !== "#51aeea") {
                        $menuArea.css({
                            "border-color": "#51aeea"
                        });
                    }

                    getDocumentParent();

                } else {
                    $menuArea.css({
                        "border-color": "transparent"
                    });
                    disableHighlightingMenuDoc();
                }

            }
        }

        function checkByDocIdInMenuEditor(documentId) {
            let status = false;
            $(".imcms-menu-items").each(function () {
                if (parseInt($(this).attr("data-document-id")) === documentId) {
                    status = true;
                }
            });

            return status;
        }

        function detectTargetArea(event) {
            return (event.pageY > menuAreaProp.top) && (event.pageY < menuAreaProp.bottom) && (event.pageX > menuAreaProp.left) && (event.pageX < menuAreaProp.right);
        }

        function getMenuDocByObjId(obj) {
            const menuDocs = $(".imcms-menu-items-tree").find(".imcms-menu-items");
            let menuDoc = null
            ;

            menuDocs.each(function () {
                if ($(this).attr("data-document-id") === obj) {
                    menuDoc = $(this)
                }
            });

            return menuDoc;
        }

        function disableHighlightingMenuDoc() {
            $(".imcms-menu-items-tree").find(".imcms-menu-items").css({
                "border": "none"
            });
        }

        function removedPreviousItemFrame() {
            const $menuTree = $(".imcms-menu-items-tree"),
                $menuItemFrame = $(".imcms-document-items--frame").find(".imcms-document-item"),
                $frameParent = $menuTree.find("[data-document-id=" + $menuItemFrame.attr("data-id") + "]")
                    .parent("[data-menu-items-lvl]")
            ;

            if ($frameParent.find("[data-menu-items-lvl]").length === 1) {
                $frameParent.find(".children-triangle").remove();
            }
            $menuTree.find("[data-document-id=" + $menuItemFrame.attr("data-id") + "]").remove();
        }

        function slideUpMenuDocIfItClose(menuDoc) {
            const showHidBtn = menuDoc.find(".imcms-menu-item").first().find(".children-triangle");

            if (!showHidBtn.hasClass("imcms-menu-item-btn--open")) {
                showHidBtn.trigger("click");
            }
        }

        function createMenuItemFrame(menuDoc, placeStatus, frameTop) {
            const insertedParent = {
                    parent: frameTop < topPointMenu ? null : menuDoc,
                    status: placeStatus,
                    frameTopPos: frameTop
                },
                $menuItemFrame = $(".imcms-document-items--frame").find(".imcms-document-item")
            ;

            removedPreviousItemFrame();

            if (menuDoc.find("[data-document-id=" + $menuItemFrame.attr("data-id") + "]").length !== 0) {
                return
            }

            if (placeStatus) {
                slideUpMenuDocIfItClose(menuDoc);
            }

            setDataInputParams(insertedParent, $menuItemFrame);
        }

        function checkFramePositioning(allMenuDocObjArray, frameTop) {
            let menuDoc = null,
                placeStatus = null
            ;

            let isTree = TREE_SORT === document.getElementById('type-sort').value;

            // false -> under parent; true -> in parent; null -> under all
            function highlightMenuDoc(param, elem, isTree) {
                disableHighlightingMenuDoc();
                if (param && isTree) {
                    elem.css({
                        'border': '1px dashed blue'
                    });
                } else {
                    $(".imcms-menu-items-tree").find(".imcms-doc-item-copy").css({
                        'border': '1px dashed red'
                    });
                }
            }

            $.each(allMenuDocObjArray, (obj, param) => {
                if (frameTop > param.top && frameTop < ((param.bottom + param.top) / 2)) {
                    menuDoc = getMenuDocByObjId(obj);
                    placeStatus = true;
                    // todo copy-frame append
                    createMenuItemFrame(menuDoc, placeStatus, frameTop);
                }
                if (frameTop > ((param.bottom + param.top) / 2) && frameTop < param.bottom) {
                    menuDoc = getMenuDocByObjId(obj);
                    placeStatus = false;
                    createMenuItemFrame(menuDoc, placeStatus, frameTop);
                }
            });

            if (frameTop < topPointMenu) {
                placeStatus = false;
            }

            // highlightingMenuDoc
            if (placeStatus !== null) {
                highlightMenuDoc(placeStatus, menuDoc, isTree);
            } else {
                disableHighlightingMenuDoc();
            }

            return {
                parent: menuDoc,
                status: placeStatus,
                frameTopPos: frameTop
            }
        }

        function getDocumentParent() {
            const allMenuDocObjArray = {},
                itemTree = $(".imcms-menu-items-tree"),
                menuDocs = itemTree.find(".imcms-menu-item"),
                $frame = $(".imcms-document-items--frame"),
                frameTop = $frame.offset().top
            ;
            if (menuDocs.length === 0) {
                return checkFramePositioning(allMenuDocObjArray, frameTop)
            }

            // get all menu doc coords
            menuDocs.each(function () {
                allMenuDocObjArray[$(this).closest(".imcms-menu-items").attr("data-document-id")] = {
                    top: $(this).offset().top,
                    bottom: $(this).offset().top + $(this).outerHeight()
                };
            });

            return checkFramePositioning(allMenuDocObjArray, frameTop);
        }

        function setDataInputParams(insertedParent, frameItem) {
            const dataInput = $("#dataInput");
            const typeSort = document.getElementById("type-sort").value;

            if (typeSort !== TREE_SORT && insertedParent.parent !== null) {
                dataInput.attr("data-parent-id", insertedParent.parent.attr("data-document-id"));
                dataInput.attr("data-insert-place", "");
            }
            else {
                if (insertedParent.parent !== null) {
                    dataInput.attr("data-parent-id", insertedParent.parent.attr("data-document-id"));
                    dataInput.attr("data-insert-place", insertedParent.status);
                } else {
                    dataInput.attr("data-parent-id", "");
                    dataInput.attr("data-insert-place", "");
                }
            }

            dataInput.attr("data-id", frameItem.attr("data-id"));
            dataInput.attr("data-type-sort", typeSort);
            dataInput.attr("data-type", frameItem.attr("data-type"));
            dataInput.attr("data-status", frameItem.attr("data-status"));
            dataInput.attr("data-original-status", frameItem.attr("data-original-status"));
            dataInput.attr("data-publishedDate", frameItem.attr("data-publishedDate"));
            dataInput.attr("data-modifiedDate", frameItem.attr("data-modifiedDate"));
            dataInput.attr("data-title", frameItem.attr("data-title")).trigger("change");
            dataInput.attr("data-frame-top", insertedParent.frameTopPos);
            dataInput.attr('data-current-version', frameItem.attr('data-current-version'))
        }

        function toggleUserSelect(flag) {
            if (flag) {
                $(".imcms-frame-layout").css({"display": "block"});
            } else {
                $(".imcms-frame-layout").remove();
                disableHighlightingMenuDoc();
            }
        }

        $(document).on("mouseup", event => {
            if (!isMouseDown) {
                return;
            }
            const $frame = $(".imcms-document-items--frame"),
                frameItem = $frame.find(".imcms-document-item");
            let insertedParent = null
            ;

            if ($frame.length === 0) {
                return;
            }

            if (detectTargetArea(event)) {
                if ($(".imcms-menu-items-tree").find("[data-document-id=" + frameItem.attr("data-id") + "]").length === 0) {
                    insertedParent = getDocumentParent();
                    setDataInputParams(insertedParent, frameItem);
                }

                $menuArea.css({
                    "border-color": "transparent"
                });
                $(".imcms-menu-items-tree").find(".imcms-doc-item-copy").removeClass("imcms-doc-item-copy");
                disableHighlightingMenuDoc();
            }

            toggleUserSelect(false);

            $frame.remove();
            isMouseDown = false;
        });

        function getDocumentStatusText(documentStatus) {
            return {
                PUBLISHED: texts.status.published,
                PUBLISHED_WAITING: texts.status.publishedWaiting,
                IN_PROCESS: texts.status.inProcess,
                DISAPPROVED: texts.status.disapproved,
                ARCHIVED: texts.status.archived,
                PASSED: texts.status.passed

            }[documentStatus];
        }

        /** @namespace document.documentStatus */
        function buildDocItem(document, opts) {

            const $docItemId = components.texts.titleText("<a>", document.id, {
                href: imcms.contextPath + "/" + document.id,
            });
            $docItemId.modifiers = ["col-1", "id"];

            const title = (document.commonContents)
                ? document.commonContents
                    .filter(commonContent => commonContent.language.code === imcms.userLanguage)
                    .map(commonContent => commonContent.headline)[0]
                : document.title;

            const $docItemTitle = components.texts.titleText("<a>", title, {
                href: imcms.contextPath + "/" + document.id,
                title: title
            });
            $docItemTitle.modifiers = ["col-3", "title"];

            const $docItemAlias = components.texts.titleText("<div>", document.alias, {title: document.alias});
            $docItemAlias.modifiers = ["col-2", "alias"];

            const $docItemModified = components.texts.titleText("<div>", document.modified);
            $docItemModified.modifiers = ["col-1", "modified"];

            const $docItemPublished = components.texts.titleText("<div>", document.published);
            $docItemPublished.modifiers = ["col-1", "published"];

            const $currentVersion = document.currentVersion === WORKING_VERSION
                ? $('<div>', {
                    value: document.currentVersion
                }).css({'opacity': '1'})

                : $('<div>', {
                    value: document.currentVersion
                }).css({'opacity': '0'});
            $currentVersion.modifiers = ['col-1', 'currentVersion'];

            const $docItemType = components.texts.titleText("<div>", document.type);
            $docItemType.modifiers = ["col-1", "type"];

            const $docStatus = components.texts.titleText("<div>", getDocumentStatusText(document.documentStatus));
            $docStatus.modifiers = ["col-1", "status"];

            const $originalDocStatus = components.texts.titleText("<div>", document.documentStatus);
            $originalDocStatus.modifiers = ["originalStatus"];
            $originalDocStatus.css({"display": "none"});

            const elements = [
                {
                    "info": [
                        $docItemId,
                        $docItemTitle,
                        $docItemAlias,
                        $docItemModified,
                        $docItemPublished,
                        $currentVersion,
                        $docItemType,
                        $docStatus,
                        $originalDocStatus
                    ]
                },
                {"controls": buildDocItemControls(document, opts)}
            ];

            const $moveControl = components.controls.move();
            const $unMoveArrow = components.controls.left().css({"cursor": "not-allowed"});

            if (opts && opts.moveEnable) {
                $moveControl.on("mousedown", createFrame);
                let isExistDocInMenu = checkByDocIdInMenuEditor(document.id);
                let $controlsBlock = components.controls.buildControlsBlock("<div>",
                    (isExistDocInMenu) ? [$unMoveArrow] : [$moveControl]);
                elements.unshift({
                    controls: (isExistDocInMenu)
                        ? $controlsBlock.css({"display": "block"})
                        : $controlsBlock
                });
            }

            return new BEM({
                block: "imcms-document-item",
                elements: elements
            }).buildBlockStructure("<div>");
        }

        function buildDocumentItemContainer(document, opts, isUsed) {
            return new BEM({
                block: "imcms-document-items",
                elements: [{
                    "document-item": buildDocItem(document, opts),
                    modifiers: [document.documentStatus.replace(/_/g, "-").toLowerCase()]
                }]
            }).buildBlockStructure("<div>", {
                "data-doc-id": document.id,
                style: (isUsed) ? 'background-color: #b6b6b6; opacity: 0.4;' : ""
            });
        }

        const documentsListBEM = new BEM({
            block: "imcms-document-items-list",
            elements: {
                "document-items": ""
            }
        });

        function buildDocument(document, opts) {
            const $documentItem = buildDocumentItemContainer(document, opts, checkByDocIdInMenuEditor(document.id));
            return documentsListBEM.makeBlockElement("document-items", $documentItem);
        }

        function buildDocumentList(documentList) {
            const $blockElements = documentList.map(document => buildDocumentItemContainer(document, currentEditorOptions, checkByDocIdInMenuEditor(document.id)));

            return new BEM({
                block: "imcms-document-items-list",
                elements: {
                    "document-items": $blockElements
                }
            }).buildBlockStructure("<div>");
        }

        function buildEditorBody(documentList, opts) {
            currentEditorOptions = opts;
            $documentsList = buildDocumentList(documentList);

            $documentsList.scroll(function () {
                const $this = $(this);

                const innerHeight = $this.innerHeight();
                const scrollHeight = this.scrollHeight;

                if (sendSearchDocRequest
                    && innerHeight !== scrollHeight
                    && (($this.scrollTop() + innerHeight) >= scrollHeight)) {
                    appendDocuments(pageSkip, currentDocumentNumber, false, false);
                }
            });

            return new BEM({
                block: "imcms-document-list",
                elements: {
                    "titles": buildDocumentListTitlesRow(),
                    "items": $documentsList
                }
            }).buildBlockStructure("<div>");
        }

        function buildHead() {
            let $head = documentWindowBuilder.buildHead(texts.title);

            const linkData = '/api/admin/documents';
            const titleUrl = $('<a>', {
                text: ' : ' + linkData,
                href: linkData
            });

            $head.find('.imcms-title').append(titleUrl.css({
                'text-transform': 'lowercase',
                'color': '#0b94d8'
            }));

            return $head;
        }

        function buildFooter() {
            return WindowBuilder.buildFooter();
        }

        function buildBody() {
            return new BEM({
                block: "imcms-document-editor-body",
                elements: {
                    "body-head": buildBodyHead()
                }
            }).buildBlockStructure("<div>");
        }

        function loadDocumentEditorContent($documentsContainer, opts) {
            docSearchRestApi.read()
                .done(documentList => {
                    incrementDocumentNumber(documentList.length);
                    $editorBody = buildEditorBody(documentList, opts);
                    $documentsContainer.append($editorBody);
                    highlightDefaultSorting();
                    pushDocumentsInArray(documentList);
                })
                .fail(() => {
                    errorMsg.slideDown();
                });
        }

        let docs = [];

        function pushDocumentsInArray(documentList) {
            documentList.forEach(document => docs.push(document));
            return docs;
        }

        function getDocumentById(documentId) {
            let document = null;
            docs.forEach(doc => {
                if (doc.id === parseInt(documentId)) {
                    return document = doc;
                }
            });

            return document;
        }

        function buildDocumentEditor() {
            return new BEM({
                block: "imcms-document-editor",
                elements: {
                    "head": buildHead(),
                    "body": $documentsContainer = buildBody(),
                    "footer": buildFooter()
                }
            }).buildBlockStructure("<div>", {"class": "imcms-editor-window"});
        }

        function removeDocument(document) {
            const question = "Do you want to remove document " + document.id + "?";

            imcmsModalWindowBuilder.buildModalWindow(question, function (answer) {
                if (!answer) {
                    return;
                }

                docRestApi.remove(document)
                    .done(function () {
                        $(this).parent().parent().remove();
                    })
                    .fail(() => modal.buildErrorWindow(texts.error.removeDocumentFailed));
            });
        }

        var currentEditorOptions;

        function loadData() {
            loadDocumentEditorContent($documentsContainer, {
                editEnable: true,
                copyEnable: true,
                removeEnable: false // todo: maybe should be replaced with archivationEnable in future
            });
        }

        function clearData() {
            events.trigger("document-editor-closed");

            // setting default values
            searchQueryObj[pageSkip] = currentDocumentNumber = 0;
            searchQueryObj[term] = "";
            searchQueryObj[userId] = null;
            searchQueryObj[categoriesId] = {};

            sendSearchDocRequest = true;

            delete searchQueryObj[sortProperty];
            delete searchQueryObj[sortDirection];

            // clean up
            $textField.$input.val("");

            $.find(".imcms-drop-down-list__select-item-value").forEach(selectItemValue => {
                $(selectItemValue).text("None");
            });

            $editorBody.remove();
        }

        function incrementDocumentNumber(delta) {
            currentDocumentNumber += delta;
        }

        var documentWindowBuilder = new WindowBuilder({
            factory: buildDocumentEditor,
            loadDataStrategy: loadData,
            clearDataStrategy: clearData,
            onEscKeyPressed: "close"
        });

        return {
            buildBody: buildBody,
            loadDocumentEditorContent: loadDocumentEditorContent,
            clearData: clearData,
            buildDocument: buildDocument,
            incrementDocumentNumber: incrementDocumentNumber,
            addDocumentToList: addDocumentToList,
            getDocumentStatusText: getDocumentStatusText,
            refreshDocumentInList: refreshDocumentInList,
            getDocumentById: getDocumentById,
            build: function () {
                documentWindowBuilder.buildWindow.apply(documentWindowBuilder, arguments);
            }
        };
    }
);
