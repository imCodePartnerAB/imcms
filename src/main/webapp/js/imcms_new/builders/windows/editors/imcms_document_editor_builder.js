/**
 * Created by Serhii Maksymchuk from Ubrainians for imCode
 * 14.08.17.
 */
Imcms.define("imcms-document-editor-builder",
    [
        "imcms-bem-builder", "imcms-page-info-builder", "imcms-components-builder", "imcms-primitives-builder",
        "imcms-documents-rest-api", "imcms-documents-search-rest-api", "imcms-controls-builder", "imcms-users-rest-api",
        "imcms-categories-rest-api", "imcms-window-builder", "jquery", "imcms", "imcms-modal-window-builder",
        "imcms-document-type-select-window-builder"
    ],
    function (BEM, pageInfoBuilder, components, primitives, docRestApi, docSearchRestApi, controlsBuilder, usersRestApi,
              categoriesRestApi, WindowBuilder, $, imcms, imcmsModalWindowBuilder, docTypeSelectBuilder) {

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

        function buildBodyHeadTools() {
            function addDocumentToList(document) {
                var $document = buildDocument(document, documentEditorOptions);
                $documentsList.append($document); // todo: replace append by pasting into correct position in sorted list
            }

            function onNewDocButtonClick(e) {
                e.preventDefault();
                docTypeSelectBuilder.build(addDocumentToList);
            }

            function buildNewDocButton() {
                return components.buttons.negativeButton({
                    text: "New",
                    click: onNewDocButtonClick
                });
            }

            function buildSearchDocField() {
                return new BEM({
                    block: "imcms-input-search",
                    elements: {
                        "text-box": primitives.imcmsInputText({
                            id: "searchText",
                            name: "search",
                            placeholder: "Type to find document"
                        }),
                        "button": components.buttons.searchButton()
                    }
                }).buildBlockStructure("<div>");
            }

            function buildUsersFilterSelect() {
                var $usersFilterSelect = components.selects.imcmsSelect("<div>", {
                    id: "users-filter",
                    name: "users-filter"
                });

                usersRestApi.read(null).done(function (users) {
                    var usersDataMapped = users.map(function (user) {
                        return {
                            text: user.username,
                            "data-value": user.id
                        }
                    });
                    components.selects.addOptionsToSelect(usersDataMapped, $usersFilterSelect);
                });

                return $usersFilterSelect;
            }

            function buildCategoriesFilterSelect() {
                var $categoriesFilterSelect = components.selects.imcmsSelect("<div>", {
                    id: "categories-filter",
                    name: "categories-filter"
                });

                categoriesRestApi.read(null).done(function (categories) {
                    var categoriesDataMapped = categories.map(function (category) {
                        return {
                            text: category.name,
                            "data-value": category.id
                        }
                    });
                    components.selects.addOptionsToSelect(categoriesDataMapped, $categoriesFilterSelect);
                });

                return $categoriesFilterSelect;
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

        function buildDocumentListTitlesRow() {
            var $idColumnHead = $("<div>", {text: "id"});
            $idColumnHead.modifiers = ["col-2"];

            var $titleColumnHead = $("<div>", {text: "Title"});
            $titleColumnHead.modifiers = ["col-3"];

            var $aliasColumnHead = $("<div>", {text: "Alias"});
            $aliasColumnHead.modifiers = ["col-3"];

            var $typeColumnHead = $("<div>", {text: "Type"});
            $typeColumnHead.modifiers = ["col-4"];

            return new BEM({
                block: "imcms-document-list-titles",
                elements: {
                    "title": [
                        $idColumnHead,
                        $titleColumnHead,
                        $aliasColumnHead,
                        $typeColumnHead
                    ]
                }
            }).buildBlockStructure("<div>");
        }

        function createFrame(event) {
            var $this = $(this),
                original = $this.closest(".imcms-document-items"),
                $frame = original.clone(),
                frameItem = $frame.find(".imcms-document-item")
            ;
            $menuArea = $(".imcms-menu-items-tree");

            if (!checkDocInMenuEditor(original)) {
                event.preventDefault();
                return
            }

            $frame.find(".imcms-title").last().remove();
            $frame.find(".imcms-title").last().remove();

            isMouseDown = true;
            mouseCoords = {
                pageX: event.clientX,
                pageY: event.clientY,
                top: $this.closest(".imcms-document-items").position().top,
                left: $this.closest(".imcms-document-items").position().left + 500
            };
            menuAreaProp = {
                top: $menuArea.position().top,
                left: $menuArea.position().left,
                right: menuAreaProp.left + $menuArea.outerWidth(),
                bottom: menuAreaProp.top + $menuArea.outerHeight()
            };

            frameItem.attr("data-id", frameItem.children().first().text());
            frameItem.attr("data-title", frameItem.children().eq(1).text());

            $frame.addClass("imcms-document-items--frame");
            $frame.css({
                "background-color": "#e9e9f5",
                "position": "absolute",
                "z-index": 11001,
                "width": "450px",
                "top": mouseCoords.top,
                "left": mouseCoords.left
            });

            $frame.appendTo("body");
        }

        $(document).on("mousemove", moveFrame)
            .on("dragstart", function () {
                return false;
            });

        function refreshDocumentInList(document) {
            var $oldDocumentElement = $documentsList.find("[data-doc-id=" + document.id + "]");

            if ($oldDocumentElement.length === 1) {
                var $newDocumentElement = buildDocument(document, documentEditorOptions);
                $oldDocumentElement.replaceWith($newDocumentElement);
            }
        }

        function buildDocItemControls(document, opts) {
            var controls = [];

            if (opts) {
                if (opts.moveEnable) {
                    var $controlMove = controlsBuilder.move().on("mousedown", createFrame);
                    controls.push($controlMove);
                }

                if (opts.removeEnable) {
                    var $controlRemove = controlsBuilder.remove(function () {
                        removeDocument.call(this, document);
                    });
                    controls.push($controlRemove);
                }

                if (opts.editEnable) {
                    var $controlEdit = controlsBuilder.edit(function () {
                        pageInfoBuilder.build(document.id, refreshDocumentInList);
                    });
                    controls.push($controlEdit);
                }
            }

            return controlsBuilder.buildControlsBlock("<div>", controls);
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
                    if ( $menuArea.css("border-color") !== "#51aeea") {
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

            if ($frameParent
                    .find("[data-menu-items-lvl]")
                    .length === 1) {
                $frameParent
                    .find(".children-triangle")
                    .remove()
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
                    })
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
            dataInput.attr("data-title", frameItem.attr("data-title")).trigger("change");
        }

        $(document).on("mouseup", function (event) {
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

            $frame.remove();
            isMouseDown = false;
        });

        function buildDocItem(document, opts) {
            var $docItemId = components.texts.titleText("<div>", document.id);
            $docItemId.modifiers = ["col-2"];

            var title = (document.commonContents) ? document.commonContents.filter(function (commonContent) {
                return commonContent.language.code === imcms.language.code;

            }).map(function (commonContent) {
                return commonContent.headline;

            })[0] : document.title;

            var $docItemTitle = components.texts.titleText("<div>", title);
            $docItemTitle.modifiers = ["col-3"];

            var $docItemAlias = components.texts.titleText("<div>", document.alias);
            $docItemAlias.modifiers = ["col-3"];

            var $docItemType = components.texts.titleText("<div>", document.type);
            $docItemType.modifiers = ["col-4"];

            return new BEM({
                block: "imcms-document-item",
                elements: {
                    "info": [
                        $docItemId,
                        $docItemTitle,
                        $docItemAlias,
                        $docItemType
                    ],
                    "controls": buildDocItemControls(document, opts)
                }
            }).buildBlockStructure("<div>");
        }

        function buildDocumentItemContainer(document, opts) {
            return new BEM({
                block: "imcms-document-items",
                elements: {
                    "document-item": buildDocItem(document, opts)
                }
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

        function buildDocumentList(documentList, opts) {
            var $blockElements = documentList.map(function (document) {
                return buildDocumentItemContainer(document, opts);
            });

            return new BEM({
                block: "imcms-document-items-list",
                elements: {
                    "document-items": $blockElements
                }
            }).buildBlockStructure("<div>");
        }

        function buildEditorBody(documentList, opts) {
            return new BEM({
                block: "imcms-document-list",
                elements: {
                    "titles": buildDocumentListTitlesRow(),
                    "items": ($documentsList = buildDocumentList(documentList, opts))
                }
            }).buildBlockStructure("<div>");
        }

        function buildHead() {
            return documentWindowBuilder.buildHead("Document editor");
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
                $editorBody = buildEditorBody(documentList, opts);
                $documentsContainer.append($editorBody);
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

        var documentEditorOptions = {
            editEnable: true,
            removeEnable: false // todo: maybe should be replaced with archivationEnable in future
        };

        function loadData() {
            loadDocumentEditorContent($documentsContainer, documentEditorOptions);
        }

        function clearData() {
            $editorBody.detach();
        }

        var documentWindowBuilder = new WindowBuilder({
            factory: buildDocumentEditor,
            loadDataStrategy: loadData,
            clearDataStrategy: clearData
        });

        return {
            buildBody: buildBody,
            loadDocumentEditorContent: loadDocumentEditorContent,
            build: function () {
                documentWindowBuilder.buildWindow.applyAsync(arguments, documentWindowBuilder);
            }
        };
    }
);
