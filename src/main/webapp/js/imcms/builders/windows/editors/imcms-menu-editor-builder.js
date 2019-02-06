/**
 * Created by Serhii Maksymchuk from Ubrainians for imCode
 * 10.08.17.
 */
define("imcms-menu-editor-builder",
    [
        "imcms-bem-builder", "imcms-components-builder", "imcms-document-editor-builder", "imcms-modal-window-builder",
        "imcms-window-builder", "imcms-menus-rest-api", "imcms-page-info-builder", "jquery", "imcms-primitives-builder",
        "imcms-jquery-element-reload", "imcms-events", "imcms-i18n-texts", "imcms-document-copy-rest-api", "imcms",
        "imcms-document-type-select-window-builder", "imcms-document-profile-select-window-builder"
    ],
    function (BEM, components, documentEditorBuilder, imcmsModalWindow, WindowBuilder, menusRestApi, pageInfoBuilder, $,
              primitivesBuilder, reloadElement, events, texts, docCopyRestApi, imcms, docTypeSelectBuilder,
              docProfileSelectBuilder) {

        texts = texts.editors.menu;

        var $title, $menuElementsContainer, $documentsContainer, $documentEditor;
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

            menusRestApi.create(menuDTO).done(onMenuSaved);
        }

        function saveAndClose() {
            saveMenuElements();
            menuWindowBuilder.closeWindow();
        }

        function buildHead() {
            var $head = menuWindowBuilder.buildHead(texts.title);
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
            toggleUserSelect(false);
            isMouseDown = false;
            if (isPasted && $originDropItem.length !== 0) {
                $originItem.remove();
                if ($originItemParent.find("[data-menu-items-lvl]").length === 0) {
                    $originItemParent.find(".children-triangle").remove();
                }
                $originDropItem.find(".children-triangle").first().click();
                $originDropItem.removeClass("imcms-menu-items--is-drop");
            } else {
                $originItem.removeClass("imcms-menu-items--is-drag");
            }
        }

        function detectTargetArea(event) {
            return (event.pageY > menuAreaProp.top) &&
                (event.pageY < menuAreaProp.bottom) &&
                (event.pageX > menuAreaProp.left) &&
                (event.pageX < menuAreaProp.right);
        }

        function toggleUserSelect(flag) {
            if (flag) {
                $(".imcms-frame-layout").css({"display": "block"});
            } else {
                $(".imcms-frame-layout").remove();
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
            var $menuItem = menuDoc.find(".imcms-menu-item").first();

            if ($menuItem.find(".children-triangle").length === 0) {
                $menuItem.find(".imcms-controls").first().after(
                    buildChildrenTriangle().addClass("imcms-menu-item__btn imcms-menu-item-btn--open")
                );
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

            $.each(allMenuDocObjArray, (obj, param) => {
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

            mouseCoords.deltaPageX = mouseCoords.newPageX - mouseCoords.pageX;
            mouseCoords.deltaPageY = mouseCoords.newPageY - mouseCoords.pageY;

            if (Math.abs(mouseCoords.deltaPageX) > 7 || Math.abs(mouseCoords.deltaPageY) > 7) {
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

            var $frame = $originItem.clone(true),
                $frameLayout = $("<div>");

            $frameLayout.addClass("imcms-frame-layout")
                .css({
                    "display": "none",
                    "position": "fixed",
                    "top": 0,
                    "left": 0,
                    "width": "100%",
                    "height": "100%",
                    "background": "transparent",
                    "z-index": 10101
                });
            $frameLayout.appendTo($("body"));

            $originItem.addClass("imcms-menu-items--is-drag");

            closeSubItems($originItem);

            mouseCoords = {
                pageX: event.clientX,
                pageY: event.clientY,
                top: $originItem.position().top,
                left: $originItem.position().left
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

        $(document).on("mousemove", event => {
            if (!isMouseDown) return;
            moveFrame(event);
        });

        $(document).on("mouseup", () => {
            if (!isMouseDown) return;
            disableDrag($(".imcms-menu-items--frame"));
            toggleUserSelect(false);
        });

        function createItem() {
            var $dataInput = $(this),
                parentId = $dataInput.attr("data-parent-id"),
                menuElementsTree = {
                    type: $dataInput.attr("data-type"),
                    documentId: $dataInput.attr("data-id"),
                    title: $dataInput.attr("data-title"),
                    documentStatus: $dataInput.attr("data-original-status")
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
                        parent.find(".imcms-menu-item").first().find(".imcms-controls").first().after(
                            buildChildrenTriangle().addClass("imcms-menu-item__btn imcms-menu-item-btn--open")
                        );
                    }
                } else {
                    $menuElement = buildMenuItemTree(menuElementsTree, level);
                    $menuElementsContainer.find("[data-document-id=" + parentId + "]").after($menuElement);
                }
            } else {
                $menuElement = buildMenuItemTree(menuElementsTree, level);
                $menuElementsContainer.find(".imcms-menu-items-tree").append($menuElement);
            }
            $menuElement.addClass("imcms-menu-items-tree__menu-items");
        }

        function buildFooter() {
            var $saveAndClose = components.buttons.saveButton({
                    text: texts.saveAndClose,
                    click: saveAndClose
                }),
                $dataInput = primitivesBuilder.imcmsInput({
                    "type": "hidden",
                    "id": "dataInput",
                    change: createItem
                });

            return WindowBuilder.buildFooter([$saveAndClose, $dataInput]);
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

            var question = texts.removeConfirmation + currentMenuItemName + "\"?";
            imcmsModalWindow.buildModalWindow(question, answer => {
                if (!answer) {
                    return;
                }

                removeMenuItemFromEditor(currentMenuItem)
            });
        }

        function getMenuElementTree(document) {
            var menuElementTree = {
                documentId: document.id,
                link: "/" + document.id,
                target: document.target,
                type: document.type,
                documentStatus: document.documentStatus,
                children: []
            };

            document.commonContents.forEach(commonContent => {
                if (commonContent["language"]["code"] === imcms.userLanguage) {
                    menuElementTree["title"] = commonContent["headline"];
                }
            });

            return menuElementTree;
        }

        function appendNewMenuItem(document) {
            $menuItemsBlock.append(buildMenuItemTree(getMenuElementTree(document), 1));
        }

        function refreshMenuItem(document) {
            var $oldMenuItem = $menuItemsBlock
                .find("[data-document-id=" + document.id + "]");

            if ($oldMenuItem.length === 1) {

                function changeTitle() {
                    var titleValue = "";
                    document.commonContents.forEach(commonContent => {
                        if (commonContent.language.code === imcms.userLanguage) {
                            titleValue = commonContent.headline;
                        }
                    });

                    var $info = $oldMenuItem.find(".imcms-menu-item__info").first();
                    $info.text(document.id + " - " + titleValue);
                    $info.attr("title", titleValue);
                }

                function changeStatus() {
                    var $status = $oldMenuItem.find(".imcms-menu-item__status").first();
                    $status.text(documentEditorBuilder.getDocumentStatusText(document.documentStatus));
                }

                function toggleClass() {
                    var menuItemClass = "imcms-menu-items__menu-item";
                    var $menuItem = $oldMenuItem.find("." + menuItemClass).first();

                    $menuItem.removeClass((index, className) => (className.match(/\imcms-menu-items__menu-item--\S+/g) || []).join(' '));

                    $menuItem.addClass(
                        menuItemClass + "--" + document.documentStatus.replace(/_/g, "-").toLowerCase()
                    );
                }

                changeTitle();
                changeStatus();
                toggleClass();

                documentEditorBuilder.refreshDocumentInList(document);
            }
        }

        function buildMenuItemControls(menuElementTree) {
            var $controlRemove = components.controls.remove(function () {
                removeMenuItem.call(this, menuElementTree.documentId);
            });

            var $controlEdit = components.controls.edit(() => {
                pageInfoBuilder.build(menuElementTree.documentId, refreshMenuItem, menuElementTree.type);
            });

            var $controlCopy = components.controls.copy(() => {
                docCopyRestApi.copy(menuElementTree.documentId).done(copiedDocument => {

                    documentEditorBuilder.incrementDocumentNumber(1);

                    var $documentItemContainer = documentEditorBuilder
                        .buildDocument(copiedDocument, {moveEnable: true});

                    $documentEditor.find(".imcms-document-list__items").prepend($documentItemContainer);

                    appendNewMenuItem(copiedDocument);
                })
            });

            return components.controls.buildControlsBlock("<div>", [$controlRemove, $controlCopy, $controlEdit]);
        }

        function showHideSubmenu() {
            var $btn = $(this),
                level = $btn.parents(".imcms-menu-items").attr("data-menu-items-lvl")
            ;

            level = parseInt(level) + 1;
            var submenus = $btn.closest(".imcms-menu-items")
                .find(".imcms-menu-items[data-menu-items-lvl=" + level + "]");
            if (!submenus.is(":animated")) {
                submenus.each(function () {
                    $(this).slideToggle();
                    $(this).toggleClass("imcms-submenu-items--close");
                });
                $btn.toggleClass("imcms-menu-item-btn--open");
            }
        }

        function buildChildrenTriangle() {
            return $("<div>", {
                "class": "children-triangle",
                click: showHideSubmenu
            });
        }

        function buildMoveControl() {
            var $controlMove = components.controls.move();
            $controlMove.on("mousedown", dragMenuItem);

            return components.controls.buildControlsBlock("<div>", [$controlMove]);
        }

        function buildMenuItems(menuElementTree) {
            var elements = [{controls: buildMoveControl()}];

            if (menuElementTree.children.length) {
                elements.push({btn: buildChildrenTriangle()});
            }

            var titleText = menuElementTree.documentId + " - "
                + menuElementTree.title;

            elements.push({
                info: components.texts.titleText("<a>", titleText, {
                    title: menuElementTree.title,
                    href: '/' + menuElementTree.documentId,
                    target: '_blank'
                })
            });

            elements.push({
                status: components.texts.titleText(
                    "<div>",
                    documentEditorBuilder.getDocumentStatusText(menuElementTree.documentStatus)
                )
            });

            elements.push({controls: buildMenuItemControls(menuElementTree)});

            return new BEM({
                block: "imcms-menu-item",
                elements: elements
            }).buildBlockStructure("<div>");
        }

        function buildMenuItemTree(menuElementTree, level) {
            menuElementTree.children = menuElementTree.children || [];

            var treeBlock = new BEM({
                block: "imcms-menu-items",
                elements: [{
                    "menu-item": buildMenuItems(menuElementTree),
                    modifiers: [menuElementTree.documentStatus.replace(/_/g, "-").toLowerCase()]
                }]
            }).buildBlockStructure("<div>", {
                "data-menu-items-lvl": level,
                "data-document-id": menuElementTree.documentId
            });

            ++level;

            var $childElements = menuElementTree.children.map(childElement => buildMenuItemTree(childElement, level).addClass("imcms-submenu-items--close"));

            return treeBlock.append($childElements);
        }

        var $menuItemsBlock;

        function buildMenuEditorContent(menuElementsTree) {
            function buildMenuElements(menuElements) {
                var $menuItems = menuElements.map(menuElement => buildMenuItemTree(menuElement, 1));
                return new BEM({
                    block: "imcms-menu-items-tree",
                    elements: {
                        "menu-items": $menuItems
                    }
                }).buildBlockStructure("<div>");
            }

            function buildMenuTitlesRow() {
                var $idColumnHead = $("<div>", {
                    "class": "imcms-grid-coll-8",
                    text: texts.id + " - " + texts.docTitle
                });
                var $statusColumnHead = $("<div>", {
                    "class": "imcms-grid-coll-2",
                    text: texts.status
                });

                return new BEM({
                    block: "imcms-menu-list-titles",
                    elements: {
                        "title": [$idColumnHead, $statusColumnHead]
                    }
                }).buildBlockStructure("<div>");
            }

            return new BEM({
                block: "imcms-menu-list",
                elements: {
                    "titles": buildMenuTitlesRow(),
                    "items": $menuItemsBlock = buildMenuElements(menuElementsTree)
                }
            }).buildBlockStructure("<div>");
        }

        function buildMenuItemNewButton() {
            var toolBEM = new BEM({
                block: "imcms-menu-new-button"
            });

            function buildNewDocButton() {
                return components.buttons.negativeButton({
                    text: texts.newDoc,
                    click: onNewDocButtonClick
                });
            }

            function onDocumentSaved(document) {
                appendNewMenuItem(document);
                documentEditorBuilder.addDocumentToList(document);
            }

            function onNewDocButtonClick(e) {
                e.preventDefault();
                docTypeSelectBuilder.build(type => {
                    docProfileSelectBuilder.build(parentDocId => {
                        pageInfoBuilder.build(null, onDocumentSaved, type, parentDocId);
                    }, {inMenu: true});
                });
            }

            return toolBEM.buildBlock("<div>", [{"button": buildNewDocButton()}]);
        }

        function fillEditorContent(menuElementsTree) {
            var $menuElementsTree = buildMenuEditorContent(menuElementsTree);

            $menuElementsContainer.append(buildMenuItemNewButton());
            $menuElementsContainer.append($menuElementsTree);

            $documentEditor = documentEditorBuilder.buildBody();
            $documentsContainer.append($documentEditor);
            documentEditorBuilder.loadDocumentEditorContent($documentEditor, {moveEnable: true});
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
            events.trigger("menu editor closed");

            $title.text("Menu Editor");
            $menuElementsContainer.add($documentsContainer).empty();
            documentEditorBuilder.clearData();
        }

        var menuWindowBuilder = new WindowBuilder({
            factory: buildMenuEditor,
            loadDataStrategy: loadMenuEditorContent,
            clearDataStrategy: clearData,
            onEscKeyPressed: "close",
            onEnterKeyPressed: saveAndClose
        });

        var $tag;

        return {
            setTag: function ($editedTag) {
                $tag = $editedTag;
                return this;
            },
            build: function (opts) {
                menuWindowBuilder.buildWindow.apply(menuWindowBuilder, arguments);
            }
        };
    }
);
