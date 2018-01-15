/**
 * Created by Serhii Maksymchuk from Ubrainians for imCode
 * 10.08.17.
 */
Imcms.define("imcms-menu-editor-builder",
    [
        "imcms-bem-builder", "imcms-components-builder", "imcms-document-editor-builder", "imcms-modal-window-builder",
        "imcms-window-builder", "imcms-menus-rest-api", "imcms-controls-builder", "imcms-page-info-builder", "jquery",
        "imcms-primitives-builder", "imcms-jquery-element-reload", "imcms-events"
    ],
    function (BEM, components, documentEditorBuilder, imcmsModalWindow, WindowBuilder, menusRestApi,
              controls, pageInfoBuilder, $, primitivesBuilder, reloadElement, events) {

        var $title, $menuElementsContainer, $documentsContainer;
        var docId, menuIndex;
        // variables for drag
        var mouseCoords = {
                pageX: undefined,
                pageY: undefined,
                newPageX: undefined,
                newPageY: undefined
            },
            menuAreaProp = {
                top: 0,
                left: 0,
                right: 0,
                bottom: 0
            },
            $menuArea,
            isMouseDown = false,
            isPasted = false
        ;

        function reloadMenuOnPage() {
            reloadElement($tag.find(".imcms-editor-content"));
        }

        function onMenuSaved() {
            events.trigger("imcms-version-modified");
            reloadMenuOnPage();
        }

        function mapToMenuItem() {
            return {
                documentId: $(this).data("documentId"),
                children: $(this).children("[data-menu-items-lvl]").map(mapToMenuItem).toArray()
            }
        }

        function saveMenuElements() {
            var menuItems = $menuElementsContainer.find("[data-menu-items-lvl=1]")
                .map(mapToMenuItem)
                .toArray();

            var menuDTO = {
                menuIndex: menuIndex,
                docId: docId,
                menuItems: menuItems
            };

            menusRestApi.create(menuDTO).success(onMenuSaved);
        }

        function saveAndClose() {
            saveMenuElements();
            menuWindowBuilder.closeWindow();
        }

        function buildHead() {
            var $head = menuWindowBuilder.buildHead("menu editor");
            $title = $head.find(".imcms-title");

            return $head;
        }

        function buildBody() {
            return new BEM({
                block: "imcms-menu-editor-body",
                elements: {
                    "left-side": $menuElementsContainer = $("<div>", {"class": "imcms-left-side"}),
                    "right-side": $documentsContainer = $("<div>", {"class": "imcms-right-side"})
                }
            }).buildBlockStructure("<div>");
        }

        function disableDrag($frame) {
            var $originItem = $(".imcms-menu-items--is-drag");
            var $originItemParent = $originItem.parent("[data-menu-items-lvl]");
            var $originDropItem = $(".imcms-menu-items--is-drop");
            $frame.remove();
            isMouseDown = false;
            if (isPasted) {
                $originItem.remove();
                if ($originItemParent.find("[data-menu-items-lvl]").length === 0) {
                    $originItemParent.find(".children-triangle").remove();
                }
                $originDropItem.find(".children-triangle").first().trigger("click");
                $originDropItem.removeClass("imcms-menu-items--is-drop");
            } else {
                $originItem.removeClass("imcms-menu-items--is-drag");
                $originItem.find(".children-triangle").first().trigger("click");
            }
        }

        function detectTargetArea(event) {
            return (event.pageY > menuAreaProp.top) && (event.pageY < menuAreaProp.bottom) && (event.pageX > menuAreaProp.left) && (event.pageX < menuAreaProp.right);
        }

        function toggleUserSelect(flag) {
            if (flag) {
                $("body").find("*").css({"user-select": "none"});
            } else {
                $("body").find("*").css({"user-select": "auto"});
            }
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

        function getMenuItemsParam(menuDocs) {
            var allMenuDocObjArray = {};
            menuDocs.each(function () {
                if (!$(this).closest(".imcms-menu-items").hasClass("imcms-menu-items--is-drag")) {
                    allMenuDocObjArray[$(this).closest(".imcms-menu-items").attr("data-document-id")] = {
                        top: $(this).offset().top,
                        bottom: $(this).offset().top + $(this).outerHeight()
                    };
                }
            });
            return allMenuDocObjArray;
        }

        function disableHighlightingMenuDoc() {
            $(".imcms-menu-items-tree").find(".imcms-menu-items").css({
                "border": "none"
            });
        }

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

        function removedPreviousItemFrame() {
            var $menuTree = $(".imcms-menu-items-tree"),
                $frame = $(".imcms-menu-items--frame"),
                $frameParent = $(".imcms-menu-items--is-drop").closest("[data-menu-items-lvl]"),
                frameCopies
            ;

            if ($frameParent.find("[data-menu-items-lvl]").length === 1 || !$frameParent.hasClass("imcms-menu-items--is-drag")) {
                $frameParent.find(".children-triangle").remove();
            }

            frameCopies = $menuTree.find("[data-document-id=" + $frame.attr("data-document-id") + "]");

            frameCopies.each(function () {
                if (!$(this).hasClass("imcms-menu-items--is-drag")) {
                    $(this).remove();
                }
            });

            $menuTree.find("[data-menu-items-lvl]").each(function () {
                if ($(this).find("[data-menu-items-lvl]").length === 0) {
                    $(this).find(".children-triangle").remove();
                }
            });

        }

        function addShowHideBtn(menuDoc) {
            if (menuDoc.find(".imcms-menu-item").first().find(".children-triangle").length === 0) {
                menuDoc.find(".imcms-menu-item").first().prepend(buildChildrenTriangle().addClass("imcms-menu-item__btn imcms-menu-item-btn--open"));
            }
        }

        function changeDataDocumentLevel(menuDoc, $origin, placeStatus) {
            var menuDocLvl = parseInt(menuDoc.attr("data-menu-items-lvl"));

            if (placeStatus) {
                menuDocLvl++
            }
            $origin.attr("data-menu-items-lvl", menuDocLvl);
            $origin.children().each(function () {
                if ($(this).attr("data-menu-items-lvl")) {
                    changeDataDocumentLevel($origin, $(this), true);
                }
            });
        }

        function slideUpMenuDocIfItClose(menuDoc) {
            var showHidBtn = menuDoc.find(".imcms-menu-item").first().find(".children-triangle");

            if (!showHidBtn.hasClass("imcms-menu-item-btn--open")) {
                showHidBtn.trigger("click");
            }
        }

        function insertMenuCopyFrame(menuDoc, placeStatus) {
            var $frame = $(".imcms-menu-items--frame"),
                $origin = $(".imcms-menu-items--is-drag").clone(true)
            ;

            removedPreviousItemFrame();

            if (menuDoc.attr("data-document-id") === $frame.attr("data-document-id")) {
                isPasted = false;
                return
            }

            $origin.removeClass("imcms-menu-items--is-drag").addClass("imcms-menu-items--is-drop");

            if (placeStatus) {
                slideUpMenuDocIfItClose(menuDoc);
                menuDoc.append($origin);
                changeDataDocumentLevel(menuDoc, $origin, placeStatus);
                addShowHideBtn(menuDoc);
            } else {
                menuDoc.after($origin);
                changeDataDocumentLevel(menuDoc, $origin, placeStatus);
            }

            isPasted = true;
        }

        function detectPasteArea($frame) {
            var allMenuDocObjArray = {},
                itemTree = $(".imcms-menu-items-tree"),
                menuDocs = itemTree.find(".imcms-menu-item"),
                frameTop = $frame.position().top
            ;

            // get all menu doc coords
            allMenuDocObjArray = getMenuItemsParam(menuDocs, allMenuDocObjArray);

            var menuDoc = null,
                placeStatus = null
            ;

            $.each(allMenuDocObjArray, function (obj, param) {
                if (frameTop > param.top && frameTop < ((param.bottom + param.top) / 2)) {
                    menuDoc = getMenuDocByObjId(obj);
                    placeStatus = true;
                    insertMenuCopyFrame(menuDoc, placeStatus);
                } else if (frameTop > ((param.bottom + param.top) / 2) && frameTop < param.bottom) {
                    menuDoc = getMenuDocByObjId(obj);
                    placeStatus = false;
                    insertMenuCopyFrame(menuDoc, placeStatus);
                }
            });

            // highlightingMenuDoc
            if (placeStatus !== null) {
                highlightMenuDoc(placeStatus, menuDoc);
            } else {
                disableHighlightingMenuDoc();
            }
        }

        function moveFrame(event) {
            var $frame = $(".imcms-menu-items--frame");
            mouseCoords.newPageX = event.clientX;
            mouseCoords.newPageY = event.clientY;

            if (isMouseDown && detectTargetArea(event)) {
                $frame.css({
                    "top": (mouseCoords.newPageY - mouseCoords.pageY) + mouseCoords.top,
                    "left": (mouseCoords.newPageX - mouseCoords.pageX) + mouseCoords.left
                });
                detectPasteArea($frame);
            } else {
                disableDrag($frame);
                disableHighlightingMenuDoc();
            }
        }

        function closeSubItems(elem) {
            var btnTriangle = elem.find(".children-triangle").first();
            if (btnTriangle.hasClass("imcms-menu-item-btn--open")) {
                btnTriangle.trigger("click");
            }
        }

        function dragMenuItem(event) {
            var $this = $(this);

            var $originItem = $this.closest(".imcms-menu-items"),
                originItemLvl = parseInt($originItem.attr("data-menu-items-lvl"))
            ;

            if (originItemLvl === 1 && $("[data-menu-items-lvl='1']").length === 1) {
                return;
            }

            var $frame = $originItem.clone(true);

            $originItem.addClass("imcms-menu-items--is-drag");

            closeSubItems($originItem);

            mouseCoords = {
                pageX: event.clientX,
                pageY: event.clientY,
                top: $originItem.position().top,
                left: $originItem.position().left + 510
            };
            $menuArea = $(".imcms-menu-items-tree");
            menuAreaProp = {
                top: $menuArea.position().top,
                left: $menuArea.position().left,
                right: menuAreaProp.left + $menuArea.outerWidth(),
                bottom: menuAreaProp.top + $menuArea.outerHeight()
            };

            $frame.css({
                "background-color": "#e9e9f5",
                "position": "absolute",
                "z-index": 11001,
                "width": "450px",
                "top": mouseCoords.top,
                "left": mouseCoords.left
            });

            $frame.addClass("imcms-menu-items--frame");

            $frame.appendTo("body");

            closeSubItems($frame);
            toggleUserSelect(true);

            isMouseDown = true;
        }

        $(document).on("mousemove", moveFrame)
            .on("dragstart", "imcms-menu-item__info", function () {
                return false;
            });

        $(document).on("mouseup", function () {
            disableDrag($(".imcms-menu-items--frame"));
            toggleUserSelect(false);
        });

        function createItem() {
            var $dataInput = $(this),
                parentId = $dataInput.attr("data-parent-id"),
                menuElementsTree = {
                    type: $dataInput.attr("data-type"),
                    documentId: $dataInput.attr("data-id"),
                    title: $dataInput.attr("data-title")
                },
                level = ($dataInput.attr("data-parent-id") !== "")
                    ? parseInt($menuElementsContainer.find("[data-document-id=" + parentId + "]").attr("data-menu-items-lvl"))
                    : 1,
                $menuElement
            ;

            if ($dataInput.attr("data-parent-id") !== "") {
                if ($dataInput.attr("data-insert-place") === "true") {
                    $menuElement = buildMenuItemTree(menuElementsTree, level + 1);
                    $menuElementsContainer.find("[data-document-id=" + parentId + "]").append($menuElement);

                    var parent = $menuElement.parent();
                    if (parent.find(".children-triangle").length === 0) {
                        parent.find(".imcms-menu-item").first().prepend(buildChildrenTriangle().addClass("imcms-menu-item__btn imcms-menu-item-btn--open"));
                    }
                } else {
                    $menuElement = buildMenuItemTree(menuElementsTree, level);
                    $menuElementsContainer.find("[data-document-id=" + parentId + "]").after($menuElement);
                }
            } else {
                $menuElement = buildMenuItemTree(menuElementsTree, level);
                $menuElementsContainer.find(".imcms-menu-items-tree").append($menuElement);
            }

        }

        function buildFooter() {
            var $saveAndClose = components.buttons.saveButton({
                    text: "Save and close",
                    click: saveAndClose
                }),
                $dataInput = primitivesBuilder.imcmsInput({
                    "type": "hidden",
                    "id": "dataInput",
                    change: createItem
                });

            return menuWindowBuilder.buildFooter([$saveAndClose, $dataInput]);
        }

        function removeMenuItemFromEditor(currentMenuItem) {
            var submenuItem = currentMenuItem.parent().find(".imcms-menu-items"),
                parentMenuItem = currentMenuItem.closest(".imcms-menu-items"),
                currentMenuItemWrap = parentMenuItem.parent();

            submenuItem.remove();
            currentMenuItem.remove();
            parentMenuItem.remove();

            if (currentMenuItemWrap.children().length === 1) {
                currentMenuItemWrap.find(".imcms-menu-item__btn").remove();
            }
        }

        function removeMenuItem() {
            var currentMenuItem = $(this).closest(".imcms-menu-item"),
                currentMenuItemName = currentMenuItem.find(".imcms-menu-item__info").text();

            var question = "Do you want to remove menu item \"" + currentMenuItemName + "\"?";
            imcmsModalWindow.buildModalWindow(question, function (answer) {
                if (!answer) {
                    return;
                }

                removeMenuItemFromEditor(currentMenuItem)
            });
        }

        function buildMenuItemControls(menuElementTree) {
            var $controlMove = controls.move();
            var $controlRemove = controls.remove(function () {
                removeMenuItem.call(this, menuElementTree.documentId);
            });
            var $controlEdit = controls.edit(function () {
                pageInfoBuilder.build(menuElementTree.documentId, null, menuElementTree.type);
            });

            $controlMove.on("mousedown", dragMenuItem);

            return controls.buildControlsBlock("<div>", [
                $controlMove,
                $controlRemove,
                $controlEdit
            ]);
        }

        function showHideSubmenu() {
            var $btn = $(this),
                level = $btn.parents(".imcms-menu-items").attr("data-menu-items-lvl")
            ;

            level = parseInt(level) + 1;
            $btn.parents(".imcms-menu-items")
                .find(".imcms-menu-items[data-menu-items-lvl=" + level + "]")
                .each(function () {
                    $(this).slideToggle()
                });
            $btn.toggleClass("imcms-menu-item-btn--open");
        }

        function buildChildrenTriangle() {
            return $("<div>", {
                "class": "children-triangle",
                click: showHideSubmenu
            });
        }

        function buildMenuItems(menuElementTree) {
            var elements = {};

            if (menuElementTree.children.length) {
                elements.btn = buildChildrenTriangle();
            }

            elements.info = components.texts.titleText("<div>", menuElementTree.documentId + " - " + menuElementTree.title);
            elements.controls = buildMenuItemControls(menuElementTree);

            return new BEM({
                block: "imcms-menu-item",
                elements: elements
            }).buildBlockStructure("<div>");
        }

        function buildMenuItemTree(menuElementTree, level) {
            menuElementTree.children = menuElementTree.children || [];

            var treeBlock = new BEM({
                block: "imcms-menu-items",
                elements: {
                    "menu-item": buildMenuItems(menuElementTree)
                }
            }).buildBlockStructure("<div>", {
                "data-menu-items-lvl": level,
                "data-document-id": menuElementTree.documentId
            });

            ++level;

            var $childElements = menuElementTree.children.map(function (childElement) {
                return buildMenuItemTree(childElement, level).addClass("imcms-submenu-items--close");
            });

            return treeBlock.append($childElements);
        }


        function buildMenuEditorContent(menuElementsTree) {
            function buildMenuElements(menuElements) {
                var $menuItems = menuElements.map(function (menuElement) {
                    return buildMenuItemTree(menuElement, 1);
                });
                return new BEM({
                    block: "imcms-menu-items-tree",
                    elements: {
                        "menu-items": $menuItems
                    }
                }).buildBlockStructure("<div>");
            }

            function buildMenuTitlesRow() {
                var $idColumnHead = $("<div>", {
                    "class": "imcms-grid-coll-2",
                    text: "id"
                });
                var $titleColumnHead = $("<div>", {
                    "class": "imcms-grid-coll-2",
                    text: "Title"
                });

                return new BEM({
                    block: "imcms-menu-list-titles",
                    elements: {
                        "title": [$idColumnHead, $titleColumnHead]
                    }
                }).buildBlockStructure("<div>");
            }

            return new BEM({
                block: "imcms-menu-list",
                elements: {
                    "titles": buildMenuTitlesRow(),
                    "items": buildMenuElements(menuElementsTree)
                }
            }).buildBlockStructure("<div>");
        }

        function fillEditorContent(menuElementsTree) {
            var $menuElementsTree = buildMenuEditorContent(menuElementsTree);
            $menuElementsContainer.append($menuElementsTree);

            var $documentEditor = documentEditorBuilder.buildBody();
            $documentsContainer.append($documentEditor);
            documentEditorBuilder.loadDocumentEditorContent.applyAsync([$documentEditor, {moveEnable: true}]);
        }

        function loadMenuEditorContent(opts) {
            addHeadData(opts);
            menusRestApi.read(opts).done(fillEditorContent);
        }

        function addHeadData(opts) {
            $title.append(": " + opts.docId + "-" + opts.menuIndex);
        }

        function buildMenuEditor(opts) {
            docId = opts.docId;
            menuIndex = opts.menuIndex;

            return new BEM({
                block: "imcms-menu-editor",
                elements: {
                    "head": buildHead(),
                    "body": buildBody(),
                    "footer": buildFooter()
                }
            }).buildBlockStructure("<div>", {"class": "imcms-editor-window"});
        }

        function clearData() {
            $title.text("Menu Editor");
            $menuElementsContainer.add($documentsContainer).empty();
        }

        var menuWindowBuilder = new WindowBuilder({
            factory: buildMenuEditor,
            loadDataStrategy: loadMenuEditorContent,
            clearDataStrategy: clearData
        });

        var $tag;

        return {
            setTag: function ($editedTag) {
                $tag = $editedTag;
                return this;
            },
            build: function (opts) {
                menuWindowBuilder.buildWindow.applyAsync(arguments, menuWindowBuilder);
            }
        };
    }
);
