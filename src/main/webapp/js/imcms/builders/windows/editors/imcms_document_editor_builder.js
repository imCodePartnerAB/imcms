/**
 * Created by Serhii Maksymchuk from Ubrainians for imCode
 * 14.08.17.
 */
Imcms.define("imcms-document-editor-builder",
    [
        "imcms-bem-builder", "imcms-page-info-builder", "imcms-components-builder", "imcms-primitives-builder",
        "imcms-documents-rest-api", "imcms-documents-search-rest-api", "imcms-users-rest-api",
        "imcms-categories-rest-api", "imcms-window-builder", "jquery", "imcms", "imcms-modal-window-builder",
        "imcms-document-type-select-window-builder", "imcms-i18n-texts", "imcms-events",
        "imcms-document-profile-select-window-builder", "imcms-document-copy-rest-api"
    ],
    function (BEM, pageInfoBuilder, components, primitives, docRestApi, docSearchRestApi, usersRestApi,
              categoriesRestApi, WindowBuilder, $, imcms, imcmsModalWindowBuilder, docTypeSelectBuilder, texts, events,
              docProfileSelectBuilder, docCopyRestApi) {

        texts = texts.editors.document;

        var isMouseDown = false,
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

        var $documentsContainer, $editorBody, $documentsList;

        var currentDocumentNumber = 0;

        var term = "term";
        var userId = "userId";
        var categoriesId = "categoriesId";
        var sortProperty = "page.property";
        var sortDirection = "page.direction";

        var pageSkip = "page.skip";

        var defaultSortPropertyValue = "meta_id";
        var asc = "ASC";
        var desc = "DESC";

        var searchQueryObj = {
            "term": "",
            "userId": null,
            "categoriesId": {},
            "page.skip": currentDocumentNumber
        };

        var sendSearchDocRequest = true;

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

            docSearchRestApi.read(searchQueryObj).done(function (documentList) {

                if (!documentList || (documentList.length === 0)) {
                    sendSearchDocRequest = false;
                    return;
                }

                incrementDocumentNumber(documentList.length);

                documentList.forEach(function (document) {
                    $documentsList.append(buildDocument(document, currentEditorOptions));
                });
            });
        }

        function setField(field, value) {
            searchQueryObj[field] = value;
        }

        function addDocumentToList(document) {
            var $document = buildDocument(document, currentEditorOptions);
            $documentsList.prepend($document); // todo: replace append by pasting into correct position in sorted list

            incrementDocumentNumber(1);
        }

        var $textField;

        function buildBodyHeadTools() {

            function onNewDocButtonClick(e) {
                e.preventDefault();
                docTypeSelectBuilder.build(function (type) {
                    docProfileSelectBuilder.build(function (parentDocId) {
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
                    var textFieldValue = $(this).val().trim();
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
                var onSelected = function (value) {
                    if (searchQueryObj[userId] !== value) {
                        appendDocuments(userId, value, true, true);
                    }
                };

                var $usersFilterSelectContainer = components.selects.selectContainer("<div>", {
                    id: "users-filter",
                    name: "users-filter",
                    text: texts.owner,
                    emptySelect: true,
                    onSelected: onSelected
                });

                usersRestApi.getAllAdmins().done(function (users) {
                    var usersDataMapped = users.map(function (user) {
                        return {
                            text: user.username,
                            "data-value": user.id
                        }
                    });

                    components.selects.addOptionsToSelect(
                        usersDataMapped, $usersFilterSelectContainer.getSelect(), onSelected
                    );
                });

                return $usersFilterSelectContainer;
            }

            function buildCategoriesFilterSelect() {
                var onSelected = function (value) {
                    if (searchQueryObj[categoriesId][0] !== value) {
                        appendDocuments(categoriesId, {0: value}, true, true);
                    }
                };

                var $categoriesFilterSelectContainer = components.selects.selectContainer("<div>", {
                    id: "categories-filter",
                    name: "categories-filter",
                    text: texts.category,
                    emptySelect: true,
                    onSelected: onSelected
                });

                categoriesRestApi.read(null).done(function (categories) {
                    var categoriesDataMapped = categories.map(function (category) {
                        return {
                            text: category.name,
                            "data-value": category.id
                        }
                    });

                    components.selects.addOptionsToSelect(
                        categoriesDataMapped, $categoriesFilterSelectContainer.getSelect(), onSelected);
                });

                return $categoriesFilterSelectContainer;
            }

            var toolBEM = new BEM({
                block: "imcms-document-editor-head-tool",
                elements: {
                    "search": "imcms-input-search",
                    "button": "imcms-button"
                }
            });

            var $newDocButtonContainer = toolBEM.buildBlock("<div>", [{"button": buildNewDocButton()}]);
            $newDocButtonContainer.modifiers = ["grid-col-2"];

            var $searchContainer = toolBEM.buildBlock("<div>", [{"search": buildSearchDocField()}]);
            $searchContainer.modifiers = ["grid-col-4"];

            var $usersFilter = toolBEM.buildBlock("<div>", [{"select": buildUsersFilterSelect()}]);
            $usersFilter.modifiers = ["grid-col-3"];

            var $categoriesFilter = toolBEM.buildBlock("<div>", [{"select": buildCategoriesFilterSelect()}]);
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
                    "tools": buildBodyHeadTools()
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
            var $defaultSortingHeader = $(".imcms-document-list-titles__title").first();
            highlightSoring($defaultSortingHeader);
        }

        function highlightSoring($sortingHeader) {
            $(".imcms-document-list-titles__title--active").removeClass("imcms-document-list-titles__title--active");
            $sortingHeader.addClass("imcms-document-list-titles__title--active");
        }

        function processSameSorting(bySorting, $sortingHeader) {
            if (isAlreadyAscendingSorting()) {
                setSortingDirection(desc);
                highlightSoring($sortingHeader);

            } else if (isDefaultSorting(bySorting)) {
                setSortingDirection(asc);
                highlightSoring($sortingHeader);

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
                highlightSoring($sortingHeader);
            }

            appendDocuments(sortProperty, searchQueryObj[sortProperty], true, false);
        }

        function buildDocumentListTitlesRow() {
            var $idColumnHead = $("<div>", {
                text: texts.sort.id,
                click: function () {
                    onClickSorting(defaultSortPropertyValue, $(this));
                }
            });
            $idColumnHead.modifiers = ["col-1"];

            var $titleColumnHead = $("<div>", {
                text: texts.sort.title,
                click: function () {
                    onClickSorting("meta_headline_" + imcms.userLanguage, $(this));
                }
            });
            $titleColumnHead.modifiers = ["col-5"];

            var $aliasColumnHead = $("<div>", {
                text: texts.sort.alias,
                click: function () {
                    onClickSorting("alias", $(this));
                }
            });
            $aliasColumnHead.modifiers = ["col-3"];

            var $typeColumnHead = $("<div>", {text: texts.sort.type});
            $typeColumnHead.modifiers = ["col-1"];

            var $statusColumnHead = $("<div>", {text: texts.sort.status});
            $statusColumnHead.modifiers = ["col-2"];

            return new BEM({
                block: "imcms-document-list-titles",
                elements: {
                    "title": [
                        $idColumnHead,
                        $titleColumnHead,
                        $aliasColumnHead,
                        $typeColumnHead,
                        $statusColumnHead
                    ]
                }
            }).buildBlockStructure("<div>");
        }

        function createFrame(event) {
            var $this = $(this),
                original = $this.closest(".imcms-document-items"),
                $frame = original.clone(),
                $frameLayout = $("<div>"),
                frameItem = $frame.find(".imcms-document-item")
            ;

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

            if (!checkDocInMenuEditor(original)) {
                event.preventDefault();
                return
            }

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

            frameItem.attr("data-id", frameItem.find(".imcms-document-item__info--id").text());
            frameItem.attr("data-title", frameItem.find(".imcms-document-item__info--title").text());
            frameItem.attr("data-type", frameItem.find(".imcms-document-item__info--type").text());
            frameItem.attr("data-status", frameItem.find(".imcms-document-item__info--status").text());
            frameItem.attr("data-original-status", frameItem.find(".imcms-document-item__info--originalStatus").text());

            $frame.addClass("imcms-document-items--frame");
            $frame.css({
                "background-color": "#e9e9f5",
                "position": "absolute",
                "z-index": 11001,
                "width": "450px",
                "top": mouseCoords.top,
                "left": mouseCoords.left
            });

            toggleUserSelect(true);
            $frame.appendTo("body");
        }

        $(document).on("mousemove", function (event) {
            if (!isMouseDown) {
                return;
            }
            moveFrame(event);
        });

        function refreshDocumentInList(document) {
            var $oldDocumentElement = $documentsList.find("[data-doc-id=" + document.id + "]");

            if ($oldDocumentElement.length === 1) {
                var $newDocumentElement = buildDocument(document, currentEditorOptions);
                $oldDocumentElement.replaceWith($newDocumentElement);
            }
        }

        function buildDocItemControls(document, opts) {
            var controls = [];
            opts = opts || {};

            if (opts.removeEnable) {
                var $controlRemove = components.controls.remove(function () {
                    removeDocument.call(this, document);
                });
                controls.push($controlRemove);
            }

            if (opts.copyEnable) {
                var $controlCopy = components.controls.copy(function () {
                    docCopyRestApi.copy(document.id).success(function (copiedDocument) {
                        addDocumentToList(copiedDocument);
                    })
                });
                controls.push($controlCopy);
            }

            if (opts.editEnable) {
                var $controlEdit = components.controls.edit(function () {
                    pageInfoBuilder.build(document.id, refreshDocumentInList, document.type);
                });
                controls.push($controlEdit);
            }

            return components.controls.buildControlsBlock("<div>", controls);
        }

        function moveFrame(event) {
            var $frame = $(".imcms-document-items--frame");
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

        function checkDocInMenuEditor(original) {
            var status = true,
                originalId = parseInt(original.find(".imcms-title").first().text())
            ;
            $(".imcms-menu-items").each(function () {
                if (parseInt($(this).attr("data-document-id")) === originalId) {
                    status = false;
                }
            });

            return status;
        }

        function detectTargetArea(event) {
            return (event.pageY > menuAreaProp.top) && (event.pageY < menuAreaProp.bottom) && (event.pageX > menuAreaProp.left) && (event.pageX < menuAreaProp.right);
        }

        function getMenuDocByObjId(obj) {
            var menuDocs = $(".imcms-menu-items-tree").find(".imcms-menu-items"),
                menuDoc = null
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
            var $menuTree = $(".imcms-menu-items-tree"),
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
            var showHidBtn = menuDoc.find(".imcms-menu-item").first().find(".children-triangle");

            if (!showHidBtn.hasClass("imcms-menu-item-btn--open")) {
                showHidBtn.trigger("click");
            }
        }

        function createMenuItemFrame(menuDoc, placeStatus) {
            var insertedParent = {
                    parent: menuDoc,
                    status: placeStatus
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
            var menuDoc = null,
                placeStatus = null
            ;

            // false -> under parent; true -> in parent; null -> under all
            function highlightMenuDoc(param, elem) {
                disableHighlightingMenuDoc();
                if (param) {
                    elem.css({
                        "border-top": "1px solid #51aeea",
                        "border-bottom": "1px solid #51aeea"
                    });
                } else {
                    elem.css({
                        "border-bottom": "1px solid #51aeea"
                    });
                }
            }

            $.each(allMenuDocObjArray, function (obj, param) {
                if (frameTop > param.top && frameTop < ((param.bottom + param.top) / 2)) {
                    menuDoc = getMenuDocByObjId(obj);
                    placeStatus = true;
                    // todo copy-frame append
                    createMenuItemFrame(menuDoc, placeStatus);
                } else if (frameTop > ((param.bottom + param.top) / 2) && frameTop < param.bottom) {
                    menuDoc = getMenuDocByObjId(obj);
                    placeStatus = false;
                    createMenuItemFrame(menuDoc, placeStatus);
                }
            });

            // highlightingMenuDoc
            if (placeStatus !== null) {
                highlightMenuDoc(placeStatus, menuDoc);
            } else {
                disableHighlightingMenuDoc();
            }

            return {
                parent: menuDoc,
                status: placeStatus
            }
        }

        function getDocumentParent() {
            var allMenuDocObjArray = {},
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
            var dataInput = $("#dataInput");

            if (insertedParent.parent !== null) {
                dataInput.attr("data-parent-id", insertedParent.parent.attr("data-document-id"));
                dataInput.attr("data-insert-place", insertedParent.status);
            } else {
                dataInput.attr("data-parent-id", "");
                dataInput.attr("data-insert-place", "");
            }

            dataInput.attr("data-id", frameItem.attr("data-id"));
            dataInput.attr("data-type", frameItem.attr("data-type"));
            dataInput.attr("data-status", frameItem.attr("data-status"));
            dataInput.attr("data-original-status", frameItem.attr("data-original-status"));
            dataInput.attr("data-title", frameItem.attr("data-title")).trigger("change");
        }

        function toggleUserSelect(flag) {
            if (flag) {
                $(".imcms-frame-layout").css({"display": "block"});
            } else {
                $(".imcms-frame-layout").remove();
            }
        }

        $(document).on("mouseup", function (event) {
            if (!isMouseDown) {
                return;
            }
            var $frame = $(".imcms-document-items--frame"),
                frameItem = $frame.find(".imcms-document-item"),
                insertedParent = null
            ;

            if ($frame.length === 0) {
                return
            }

            if (detectTargetArea(event)) {
                if ($(".imcms-menu-items-tree").find("[data-document-id=" + frameItem.attr("data-id") + "]").length === 0) {
                    insertedParent = getDocumentParent();
                    setDataInputParams(insertedParent, frameItem);
                }

                $menuArea.css({
                    "border-color": "transparent"
                });
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
            var $docItemId = components.texts.titleText("<div>", "");

            $docItemId.append(new BEM({
                block: "imcms-title",
                elements: {
                    "link": ""
                }
            }).buildBlockElement("link", "<a>", {
                href: imcms.contextPath + "/" + document.id,
                html: document.id,
                target: "_blank"
            }));

            $docItemId.modifiers = ["col-1", "id"];

            var title = (document.commonContents)
                ? document.commonContents.filter(function (commonContent) {
                    return commonContent.language.code === imcms.userLanguage;
                    })
                    .map(function (commonContent) {
                        return commonContent.headline;
                    })[0]
                : document.title;

            var $docItemTitle = components.texts.titleText("<div>", title, {title: title});
            $docItemTitle.modifiers = ["col-5", "title"];

            var $docItemAlias = components.texts.titleText("<div>", document.alias, {title: document.alias});
            $docItemAlias.modifiers = ["col-3", "alias"];

            var $docItemType = components.texts.titleText("<div>", document.type);
            $docItemType.modifiers = ["col-1", "type"];

            var $docStatus = components.texts.titleText("<div>", getDocumentStatusText(document.documentStatus));
            $docStatus.modifiers = ["col-2", "status"];

            var $originalDocStatus = components.texts.titleText("<div>", document.documentStatus);
            $originalDocStatus.modifiers = ["originalStatus"];
            $originalDocStatus.css({"display": "none"});

            var elements = [
                {
                    "info": [
                        $docItemId,
                        $docItemTitle,
                        $docItemAlias,
                        $docItemType,
                        $docStatus,
                        $originalDocStatus
                    ]
                },
                {"controls": buildDocItemControls(document, opts)}
            ];

            if (opts && opts.moveEnable) {
                var $moveControl = components.controls.move().on("mousedown", createFrame);
                var $controlsBlock = components.controls.buildControlsBlock("<div>", [$moveControl]);
                elements.unshift({controls: $controlsBlock});
            }

            return new BEM({
                block: "imcms-document-item",
                elements: elements
            }).buildBlockStructure("<div>");
        }

        function buildDocumentItemContainer(document, opts) {
            return new BEM({
                block: "imcms-document-items",
                elements: [{
                    "document-item": buildDocItem(document, opts),
                    modifiers: [document.documentStatus.replace(/_/g, "-").toLowerCase()]
                }]
            }).buildBlockStructure("<div>", {"data-doc-id": document.id});
        }

        var documentsListBEM = new BEM({
            block: "imcms-document-items-list",
            elements: {
                "document-items": ""
            }
        });

        function buildDocument(document, opts) {
            var $documentItem = buildDocumentItemContainer(document, opts);
            return documentsListBEM.makeBlockElement("document-items", $documentItem);
        }

        function buildDocumentList(documentList) {
            var $blockElements = documentList.map(function (document) {
                return buildDocumentItemContainer(document, currentEditorOptions);
            });

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
                var $this = $(this);

                var innerHeight = $this.innerHeight();
                var scrollHeight = this.scrollHeight;

                if (sendSearchDocRequest
                    && innerHeight !== scrollHeight
                    && (($this.scrollTop() + innerHeight) >= scrollHeight)
                ) {
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
            return documentWindowBuilder.buildHead(texts.title);
        }

        function buildFooter() {
            return documentWindowBuilder.buildFooter();
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
            docSearchRestApi.read().done(function (documentList) {
                incrementDocumentNumber(documentList.length);
                $editorBody = buildEditorBody(documentList, opts);
                $documentsContainer.append($editorBody);
                highlightDefaultSorting();
            });
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
            var question = "Do you want to remove document " + document.id + "?";

            imcmsModalWindowBuilder.buildModalWindow(question, function (answer) {
                if (!answer) {
                    return;
                }

                docRestApi.remove(document).done(function () {
                    $(this).parent().parent().remove();
                })
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

            $.find(".imcms-drop-down-list__select-item-value").forEach(function (selectItemValue) {
                $(selectItemValue).text("None");
            });

            $editorBody.detach();
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
            build: function () {
                documentWindowBuilder.buildWindow.applyAsync(arguments, documentWindowBuilder);
            }
        };
    }
);
