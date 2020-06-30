/**
 * Created by Serhii Maksymchuk from Ubrainians for imCode
 * And Pavlenko Victor from Ubrainians for imCode
 * 10.08.17.
 */
define("imcms-menu-editor-builder",
    [
        "imcms-bem-builder", "imcms-components-builder", "imcms-document-editor-builder", "imcms-modal-window-builder",
        "imcms-window-builder", "imcms-menus-rest-api", "imcms-page-info-builder", "jquery", "imcms-primitives-builder",
        "imcms-jquery-element-reload", "imcms-events", "imcms-i18n-texts", "imcms-document-copy-rest-api", "imcms",
        "imcms-document-type-select-window-builder", "imcms-document-profile-select-window-builder", "imcms-sort-types-rest-api"
    ],
    function (BEM, components, documentEditorBuilder, modal, WindowBuilder, menusRestApi, pageInfoBuilder, $,
              primitivesBuilder, reloadElement, events, texts, docCopyRestApi, imcms, docTypeSelectBuilder,
              docProfileSelectBuilder, typesSortRestAPI) {

        const documentBuilderTexts = texts.editors.document;
        texts = texts.editors.menu;

        let PUBLISHED_DATE_ASC = 'PUBLISHED_DATE_ASC';
        let PUBLISHED_DATE_DESC = 'PUBLISHED_DATE_DESC';
        let MODIFIED_DATE_ASC = 'MODIFIED_DATE_ASC';
        let MODIFIED_DATE_DESC = 'MODIFIED_DATE_DESC';
        let TREE_SORT = 'TREE_SORT';
        const classButtonOn = "imcms-button--switch-on";
        const classButtonOff = "imcms-button--switch-off";

        let $menuElementsContainer, $documentsContainer, $documentEditor;
        let $title = $('<a>');
        let localizeTypesSort = [
            texts.typesSort.treeSort,
            texts.typesSort.manual,
            texts.typesSort.alphabeticalAsc,
            texts.typesSort.alphabeticalDesc,
            texts.typesSort.publishedDateAsc,
            texts.typesSort.publishedDateDesc,
            texts.typesSort.modifiedDateAsc,
            texts.typesSort.modifiedDateDesc,
        ];
        let topPointMenu = 178; // top point menu for set item before item in the top position.
        // todo: maybe need use getFirstItemInMenuArea().offset().top - 4 or something like this? Same in doc-editor

        // variables for drag
        let mouseCoords = {
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

        function saveMenuElements(opts) {
            const menuItems = $menuElementsContainer.find("[data-menu-items-lvl=1]")
                .map(mapToMenuItem)
                .toArray();

            const menuDTO = {
                menuIndex: opts.menuIndex,
                docId: opts.docId,
                menuItems: menuItems,
                nested: opts.nested
            };

            menusRestApi.create(menuDTO)
                .done(item => {
                    onMenuSaved();
                    menuWindowBuilder.closeWindow();
                })
                .fail(() => modal.buildErrorWindow(texts.error.createFailed));

        }

        function saveAndClose(opts) {
            saveMenuElements(opts);
        }

        function buildHead(opts) {
            const $head = menuWindowBuilder.buildHead(`${texts.title} - ${texts.page} ${opts.docId}, ${texts.menuTitle} ${opts.menuIndex} - ${texts.teaser} : `);
            $head.find('.imcms-title').append($title);

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
            const $originItem = $(".imcms-menu-items--is-drag");
            const $originItemParent = $originItem.parent("[data-menu-items-lvl]");
            const $originDropItem = $(".imcms-menu-items--is-drop");
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
                disableHighlightingMenuDoc();
            }
        }

        function getMenuDocByObjId(objId) {
            const menuDocs = $(".imcms-menu-items-list").find(".imcms-menu-items");
            let menuDoc = null;

            menuDocs.each(function () {
                if ($(this).attr("data-document-id") === objId) {
                    menuDoc = $(this)
                }
            });

            return menuDoc;
        }

        function getFirstItemInMenuArea() {
            const menuDocs = $(".imcms-menu-items-list").find(".imcms-menu-items");
            return menuDocs.first();
        }

        function getMenuItemsParam(menuDocs) {
            const allMenuDocObjArray = {};
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
            $(".imcms-menu-items-list").find(".imcms-menu-items").css({
                "border": "none"
            });
        }

        function highlightMenuDoc(param, elem, isTree) {
            disableHighlightingMenuDoc();
            if (param && isTree) {
                elem.css({
                    'border': '1px dashed blue'
                });
            } else {
                $(".imcms-menu-items-list").find('.imcms-menu-items--is-drop').css({
                    'border': '1px dashed red'
                });
            }
        }

        function removedPreviousItemFrame() {
            const $menuTree = $(".imcms-menu-items-list"),
                $frame = $(".imcms-menu-items--frame"),
                $frameParent = $(".imcms-menu-items--is-drop").closest("[data-menu-items-lvl]");
            let frameCopies
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
            const $menuItem = menuDoc.find(".imcms-menu-item").first();

            if ($menuItem.find(".children-triangle").length === 0) {
                $menuItem.find(".imcms-controls").first().after(
                    buildChildrenTriangle().addClass("imcms-document-item__btn imcms-document-item__btn--open")
                );
            }
        }

        function changeDataDocumentLevel(menuDoc, $origin, placeStatus, typeSort) {
            let menuDocLvl = parseInt(menuDoc.attr("data-menu-items-lvl"));

            if (placeStatus && typeSort === TREE_SORT) {
                menuDocLvl++
            }
            $origin.attr("data-menu-items-lvl", menuDocLvl);
            $origin.children().each(function () {
                if ($(this).attr("data-menu-items-lvl")) {
                    changeDataDocumentLevel($origin, $(this), true, typeSort);
                }
            });
        }

        function slideUpMenuDocIfItClose(menuDoc) {
            const showHidBtn = menuDoc.find(".imcms-menu-item").first().find(".children-triangle");

            if (!showHidBtn.hasClass("imcms-document-item__btn--open")) {
                showHidBtn.trigger("click");
            }
        }

        function insertMenuCopyFrame(menuDoc, placeStatus, frameTop) {
            const $frame = $(".imcms-menu-items--frame"),
                $origin = $(".imcms-menu-items--is-drag").clone(true)
            ;

            const typeSort = document.getElementById('type-sort').value;

            removedPreviousItemFrame();

            if (menuDoc.attr("data-document-id") === $frame.attr("data-document-id")) {
                isPasted = false;
                return
            }

            $origin.removeClass("imcms-menu-items--is-drag").addClass("imcms-menu-items--is-drop");
            $origin.removeClass("imcms-document-items-list__document-items")
                .addClass("imcms-document-items-list__document-items");

            if (frameTop < topPointMenu) { // top point in first item frame menu
                menuDoc.before($origin);
                changeDataLevelTheTopDoc($origin, 0, null)
            } else {
                if (placeStatus && typeSort === TREE_SORT) {
                    slideUpMenuDocIfItClose(menuDoc);
                    menuDoc.append($origin);
                    changeDataDocumentLevel(menuDoc, $origin, placeStatus, typeSort);
                    addShowHideBtn(menuDoc);
                } else {
                    menuDoc.after($origin);
                    changeDataDocumentLevel(menuDoc, $origin, placeStatus, typeSort);
                }
            }

            isPasted = true;
        }

        function changeDataLevelTheTopDoc($origin, functionUsages, diff) { //todo re-build improve this shit!
            let menuDocLvl = parseInt($origin.attr("data-menu-items-lvl"));
            let difference;
            if (functionUsages === 0) {
                difference = menuDocLvl - 1;
                menuDocLvl = 1;
            } else {
                menuDocLvl = menuDocLvl - diff;
                difference = diff;
            }
            functionUsages++;

            $origin.attr("data-menu-items-lvl", menuDocLvl);
            $origin.children().each(function () {
                if ($(this).attr("data-menu-items-lvl")) {
                    changeDataLevelTheTopDoc($(this), functionUsages, difference);
                }
            });
        }

        function detectPasteArea($frame) {
            let allMenuDocObjArray = {};
            const itemTree = $(".imcms-menu-items-list"),
                menuDocs = itemTree.find(".imcms-menu-item"),
                frameTop = $frame.position().top
            ;

            // get all menu doc coords
            allMenuDocObjArray = getMenuItemsParam(menuDocs);

            let menuDoc = null,
                placeStatus = null
            ;
            let isTree = TREE_SORT === document.getElementById('type-sort').value;

            $.each(allMenuDocObjArray, (obj, param) => {
                if (frameTop > param.top && frameTop < ((param.bottom + param.top) / 2)) {
                    menuDoc = getMenuDocByObjId(obj);
                    placeStatus = true;
                    insertMenuCopyFrame(menuDoc, placeStatus, frameTop);
                }
                if (frameTop > ((param.bottom + param.top) / 2) && frameTop < param.bottom) {
                    menuDoc = getMenuDocByObjId(obj);
                    placeStatus = false;
                    insertMenuCopyFrame(menuDoc, placeStatus, frameTop);
                }
            });

            if (frameTop < topPointMenu) {
                menuDoc = getFirstItemInMenuArea();
                placeStatus = false;
                insertMenuCopyFrame(menuDoc, placeStatus, frameTop);
            }

            // highlightingMenuDoc
            if (placeStatus !== null) {
                highlightMenuDoc(placeStatus, menuDoc, isTree);
            } else {
                disableHighlightingMenuDoc();
            }
        }

        function moveFrame(event) {
            const $frame = $(".imcms-menu-items--frame");
            mouseCoords.newPageX = event.clientX;
            mouseCoords.newPageY = event.clientY;

            mouseCoords.deltaPageX = mouseCoords.newPageX - mouseCoords.pageX;
            mouseCoords.deltaPageY = mouseCoords.newPageY - mouseCoords.pageY;

            if (Math.abs(mouseCoords.deltaPageX) > 7 || Math.abs(mouseCoords.deltaPageY) > 7) {
                if (isMouseDown && detectTargetArea(event)) {
                    $frame.css({
                        "top": mouseCoords.newPageY,
                        "left": mouseCoords.newPageX
                    });
                    detectPasteArea($frame);
                } else {
                    disableDrag($frame);
                    disableHighlightingMenuDoc();
                }
            }


        }

        function closeSubItems(elem) {
            const btnTriangle = elem.find(".children-triangle").first();
            if (btnTriangle.hasClass("imcms-document-item__btn--open")) {
                btnTriangle.trigger("click");
            }
        }

        function dragMenuItem(event) {
            const $this = $(this);
            const $originItem = $this.closest(".imcms-menu-items"),
                originItemLvl = parseInt($originItem.attr("data-menu-items-lvl"))
            ;

            if (originItemLvl === 1 && $("[data-menu-items-lvl='1']").length === 1) {
                return;
            }

            const $frame = $originItem.clone(true),
                $frameLayout = $("<div>");

            $frameLayout.addClass("imcms-frame-layout")
                .css({
                    "display": "none",
                    "position": "absolute",
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
            $menuArea = $(".imcms-menu-items-list");
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
                "width": "40%",
                "top": event.clientY,
                "left": event.clientX
            });

            $frame.addClass("imcms-document-items-list__document-items");
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
            const $dataInput = $(this),
                parentId = $dataInput.attr("data-parent-id"),
                menuElementsTree = {
                    type: $dataInput.attr("data-type"),
                    documentId: $dataInput.attr("data-id"),
                    title: $dataInput.attr("data-title"),
                    isShownTitle: $dataInput.attr("data-is-shown-title") === "true",
                    documentStatus: $dataInput.attr("data-original-status"),
                    publishedDate: $dataInput.attr('data-publishedDate'),
                    modifiedDate: $dataInput.attr('data-modifiedDate'),
                    hasNewerVersion: $dataInput.attr('data-current-version') === '0' //simple convert in boolean. if not working version - not exist newer ver
                },
                level = ($dataInput.attr("data-parent-id") !== "")
                    ? parseInt($menuElementsContainer.find("[data-document-id=" + parentId + "]").attr("data-menu-items-lvl"))
                    : 1;
            let $menuElement
            ;

            if ($dataInput.attr('data-frame-top') < topPointMenu) {
                $menuElement = buildMenuItemTree(menuElementsTree, level, $dataInput.attr("data-type-sort"));
                $menuElementsContainer.find("[data-menu-items-lvl=1]").first().before($menuElement);
            } else {
                if ($dataInput.attr("data-parent-id") !== "") {
                    if ($dataInput.attr("data-insert-place") === "true") {
                        $menuElement = buildMenuItemTree(menuElementsTree, level + 1, $dataInput.attr("data-type-sort"));
                        $menuElementsContainer.find("[data-document-id=" + parentId + "]").append($menuElement);

                        const parent = $menuElement.parent();
                        if (parent.find(".children-triangle").length === 0) {
                            parent.find(".imcms-menu-item").first().find(".imcms-controls").first().after(
                                buildChildrenTriangle().addClass("imcms-document-item__btn imcms-document-item__btn--open")
                            );
                        }
                    } else {
                        $menuElement = buildMenuItemTree(menuElementsTree, level, $dataInput.attr("data-type-sort"));
                        $menuElementsContainer.find("[data-document-id=" + parentId + "]").after($menuElement);
                    }
                } else {
                    $menuElement = buildMenuItemTree(menuElementsTree, level, $dataInput.attr("data-type-sort"));
                    $menuElementsContainer.find(".imcms-menu-items-list").append($menuElement);
                }
            }
            $menuElement.addClass("imcms-document-items-list__document-items");
            $menuElement.addClass("imcms-doc-item-copy");
            let doc = documentEditorBuilder.getDocumentById($menuElement.attr('data-document-id'));
            documentEditorBuilder.refreshDocumentInList(doc);
        }

        function buildFooter(opts) {
            const $saveAndClose = components.buttons.saveButton({
                    text: texts.saveAndClose,
                    click: () => {
                        saveAndClose(opts)
                    }
                }),
                $dataInput = primitivesBuilder.imcmsInput({
                    "type": "hidden",
                    "id": "dataInput",
                    change: createItem
                });

            return WindowBuilder.buildFooter([$saveAndClose, $dataInput]);
        }

        function removeMenuItemFromEditor(currentMenuItem) {
            const submenuItem = currentMenuItem.parent().find(".imcms-menu-items"),
                parentMenuItem = currentMenuItem.closest(".imcms-menu-items"),
                currentMenuItemWrap = parentMenuItem.parent(),
                currentMenuItemId = parseInt(currentMenuItem.find(".imcms-document-item__info--id").text());

            let submenuDocIds = [];

            submenuItem.each(index => {
                submenuDocIds.push(parseInt(submenuItem[index].dataset.documentId));
            });

            submenuItem.remove();
            currentMenuItem.remove();
            parentMenuItem.remove();

            if (currentMenuItemWrap.children().length === 1) {
                currentMenuItemWrap.find(".imcms-document-item__btn").remove();
            }

            refreshDocuments(submenuDocIds, currentMenuItemId);
        }

        function refreshDocuments(submenuDocIds, currentMenuItemId) {
            submenuDocIds.forEach(id => {
                let document = documentEditorBuilder.getDocumentById(id);
                documentEditorBuilder.refreshDocumentInList(document);
            });

            let parentDoc = documentEditorBuilder.getDocumentById(currentMenuItemId);
            documentEditorBuilder.refreshDocumentInList(parentDoc);
        }

        function removeMenuItem() {
            const currentMenuItem = $(this).closest(".imcms-menu-item");
            const currentMenuItemName = currentMenuItem.find(".imcms-document-item__info--title").text();

            const question = texts.removeConfirmation + currentMenuItemName + "\"?";
            modal.buildModalWindow(question, answer => {
                if (!answer) {
                    return;
                }

                removeMenuItemFromEditor(currentMenuItem);

            });
        }

        function getMenuElementTree(document) {
            const menuElementTree = {
                documentId: document.id,
                link: `${imcms.contextPath}/${document.id}`,
                target: document.target,
                type: document.type,
                documentStatus: document.documentStatus,
                hasNewerVersion: document.currentVersion,
                children: [],
                publishedDate: `${document.published.date} ${document.published.time}`,
                modifiedDate: `${document.modified.date} ${document.modified.time}`,
            };

            document.commonContents.forEach(commonContent => {
                if (commonContent["language"]["code"] === imcms.language.code) {
                    menuElementTree["title"] = commonContent["headline"];
                }
                if (commonContent.enabled && commonContent.language.code === imcms.language.code) {
                    menuElementTree["isShownTitle"] = true;
                }
            });

            return menuElementTree;
        }

        function appendNewMenuItem(doc) {
            const typeSort = document.getElementById("type-sort").value;
            $menuItemsBlock.append(buildMenuItemTree(getMenuElementTree(doc), 1, typeSort));
            documentEditorBuilder.refreshDocumentInList(doc)
        }

        function refreshMenuItem(document) {
            const $oldMenuItem = $menuItemsBlock
                .find("[data-document-id=" + document.id + "]");

            if ($oldMenuItem.length === 1) {

                function changeTitle() {
                    const notShownTitleClass = "imcms-document-item__info--notShownTitle";
                    let titleValue = documentBuilderTexts.notShownInSelectedLang;
                    const $docTitle = $oldMenuItem.find(".imcms-document-item__info--title").first();
                    document.commonContents.forEach(commonContent => {
                        if (commonContent.enabled && commonContent.language.code === imcms.language.code) {
                            titleValue = commonContent.headline;
                        }
                    });
                    document.isShownTitle = !isTitleSameNotShownText(titleValue);
                    isTitleSameNotShownText(titleValue)
                        ? $docTitle.addClass(notShownTitleClass)
                        : $docTitle.removeClass(notShownTitleClass);
                    $docTitle.text(titleValue)
                    $docTitle.attr("title", titleValue)
                }

                function isTitleSameNotShownText(titleValue) {
                    return titleValue === documentBuilderTexts.notShownInSelectedLang;
                }

                function changeStatus() {
                    const $status = $oldMenuItem.find(".imcms-document-item__info--status").first();
                    const statusTexts = documentEditorBuilder.getDocumentStatusTexts(document.documentStatus, document.published);
                    $status.text(statusTexts.title);
                    $status.attr('title', statusTexts.tooltip);
                }

                function toggleClass() {
                    const menuItemClass = "imcms-menu-items__document-item";
                    const $menuItem = $oldMenuItem.find("." + menuItemClass).first();

                    $menuItem.removeClass((index, className) => (className.match(/\imcms-menu-items__document-item--\S+/g) || []).join(' '));

                    $menuItem.addClass(
                        menuItemClass + "--" + document.documentStatus.replace(/_/g, "-").toLowerCase()
                    );
                }

                changeTitle();
                changeStatus();
                toggleClass();

                documentEditorBuilder.refreshDocumentInList(document, true);
            }
        }

        //mapping need for changes (remove) controls in items
        function mapDocumentToMenuItem(document) {
            return {
                documentId: document.id,
                type: document.type
            }
        }

        function isMultiRemoveModeEnabled() {
            return $('.imcms-switch-block__button').hasClass(classButtonOn);
        }

        function buildMenuItemControls(menuElementTree, enabledMultiRemoveMode) {
            const menuItemId = menuElementTree.documentId;
            const $multiRemoveControl = buildMultiRemoveCheckBox(menuItemId);
            $multiRemoveControl.modifiers = ['multi-remove'];

            const $controlRemove = enabledMultiRemoveMode
                ? $multiRemoveControl
                : components.controls.remove(function () {
                    removeMenuItem.call(this, menuElementTree.documentId);
                });
            components.overlays.defaultTooltip($controlRemove, texts.remove);

            const $controlEdit = components.controls.edit(() => {
                pageInfoBuilder.build(menuItemId, refreshMenuItem, menuElementTree.type);
            });
            components.overlays.defaultTooltip($controlEdit, texts.edit);

            const $controlCopy = components.controls.copy(() => {
                docCopyRestApi.copy(menuItemId)
                    .done(copiedDocument => {

                        documentEditorBuilder.incrementDocumentNumber(1);

                        const $documentItemContainer = documentEditorBuilder
                            .buildDocument(copiedDocument, {moveEnable: true});

                        $documentEditor.find(".imcms-document-list__items").prepend($documentItemContainer);

                        appendNewMenuItem(copiedDocument);
                    })
                    .fail(() => modal.buildErrorWindow(texts.error.copyDocumentFailed));
            });
            components.overlays.defaultTooltip($controlCopy, texts.copy);

            return components.controls.buildControlsBlock("<div>", [$controlRemove, $controlCopy, $controlEdit]);
        }

        function showHideSubmenu() {
            const $btn = $(this);
            let level = $btn.parents(".imcms-menu-items").attr("data-menu-items-lvl")
            ;

            level = parseInt(level) + 1;
            const submenus = $btn.closest(".imcms-menu-items")
                .find(".imcms-menu-items[data-menu-items-lvl=" + level + "]");
            if (!submenus.is(":animated")) {
                submenus.each(function () {
                    $(this).slideToggle();
                    $(this).toggleClass("imcms-submenu-items--close");
                });
                $btn.toggleClass("imcms-document-item__btn--open");
            }
        }

        function buildChildrenTriangle() {
            return $("<div>", {
                "class": "children-triangle",
                click: showHideSubmenu
            });
        }

        function buildMoveControl(typeSort) {
            let $controlMove;
            if (typeSort === TREE_SORT) {
                $controlMove = components.controls.move();
            } else {
                $controlMove = components.controls.vertical_move();
            }
            $controlMove.on("mousedown", dragMenuItem);

            return components.controls.buildControlsBlock("<div>", [$controlMove]);
        }

        function buildMenuItem(menuElementTree, typeSort) {

            const $docId = components.texts.titleText('<a>', menuElementTree.documentId, {
                href: '/' + menuElementTree.documentId,
                target: '_blank',
                class: 'imcms-grid-coll-1',
            });
            $docId.modifiers = ['id'];
            components.overlays.defaultTooltip(
                $docId,
                documentEditorBuilder.getIdTooltipText(menuElementTree.documentId, menuElementTree.createdDate, menuElementTree.createdBy),
                'right'
            );

            const title = menuElementTree.title
                ? menuElementTree.title
                : documentBuilderTexts.notShownInSelectedLang;
            const $titleText = components.texts.titleText('<a>', title, {
                href: "/" + menuElementTree.documentId,
                class: 'imcms-flex--flex-1',
            });
            $titleText.modifiers = ['title'];
            !menuElementTree.title && $titleText.modifiers.push("notShownTitle");
            title && components.overlays.defaultTooltip($titleText, title, {placement: 'right'});

            const $publishedDate = components.texts.titleText('<div>', menuElementTree.publishedDate, {
                class: 'imcms-grid-coll-3',
            });
            $publishedDate.modifiers = ['date'];
            components.overlays.defaultTooltip(
                $publishedDate,
                documentEditorBuilder.getPublishedDateTooltipText(menuElementTree.publishedDate, menuElementTree.publishedBy),
            );

            const $modifiedDate = components.texts.titleText('<div>', menuElementTree.modifiedDate, {
                class: 'imcms-grid-coll-3',
            });
            components.overlays.defaultTooltip(
                $modifiedDate,
                documentEditorBuilder.getModifiedDateTooltipText(menuElementTree.modifiedDate, menuElementTree.modifiedBy),
            );

            const $star = menuElementTree.hasNewerVersion
                ? components.controls.star()
                : components.controls.star().css({'filter': 'grayscale(100%) brightness(140%)'});
            const $currentVersion = $('<div>').append($star).addClass('imcms-grid-coll-1');
            components.overlays.defaultTooltip(
                $currentVersion,
                documentEditorBuilder.getDocumentVersionTexts(menuElementTree.hasNewerVersion).tooltip
            );
            $currentVersion.modifiers = ['currentVersion'];

            const documentStatusTexts = documentEditorBuilder.getDocumentStatusTexts(menuElementTree.documentStatus, menuElementTree.publishedDate);
            const $documentStatus = components.texts.titleText("<div>", documentStatusTexts.title, {
                class: 'imcms-grid-coll-13'
            });
            $documentStatus.modifiers = ['status'];
            components.overlays.defaultTooltip($documentStatus, documentStatusTexts.tooltip);

            const elements = [$docId, $titleText];
            let childrenIcon = "";
            if (menuElementTree.children.length) {
                childrenIcon = (buildChildrenTriangle().addClass("imcms-document-item__btn imcms-document-item__btn--open"));
            }

            switch (typeSort) {
                case PUBLISHED_DATE_ASC:
                case PUBLISHED_DATE_DESC:
                    elements.push($publishedDate);
                    break;
                case MODIFIED_DATE_ASC:
                case MODIFIED_DATE_DESC:
                    elements.push($modifiedDate);
                    break;
            }

            elements.push($currentVersion, $documentStatus);

            const controls = [buildMoveControl(typeSort), buildMenuItemControls(menuElementTree, isMultiRemoveModeEnabled())];

            return new BEM({
                block: "imcms-document-item",
                elements: [
                    {"btn-icon": childrenIcon},
                    {"info": elements},
                    {"controls": controls}
                ]
            }).buildBlockStructure("<div>", {
                class: "imcms-menu-item"
            });
        }

        function buildMenuItemTree(menuElementTree, level, typeSort) {
            menuElementTree.children = menuElementTree.children || [];

            const treeBlock = new BEM({

                block: "imcms-document-items",
                elements: [{
                    "document-item": buildMenuItem(menuElementTree, typeSort),
                    modifiers: [menuElementTree.documentStatus.replace(/_/g, "-").toLowerCase()]
                }]
            }).buildBlockStructure("<div>", {
                class: "imcms-menu-items",
                "data-menu-items-lvl": level,
                "data-document-id": menuElementTree.documentId
            });

            ++level;

            const $childElements = menuElementTree.children.map(childElement => buildMenuItemTree(childElement, level, typeSort).addClass("imcms-submenu-items--close"));

            return treeBlock.append($childElements);
        }

        var $menuItemsBlock;

        function buildMenuEditorContent(menuElementsTree, typeSort) {
            function buildMenuElements(menuElements) {
                const $menuItems = menuElements.map(menuElement => buildMenuItemTree(menuElement, 1, typeSort));
                return new BEM({
                    block: "imcms-document-items-list",
                    elements: {
                        "document-items": $menuItems
                    }
                }).buildBlockStructure("<div>", {
                    class: "imcms-menu-items-list"
                });
            }

            function buildMenuTitlesRow() {
                const $idColumnHead = $("<div>", {
                    class: "imcms-grid-coll-1",
                    text: texts.id
                });
                $idColumnHead.modifiers = ["id"];

                const $titleColumnHead = $("<div>", {
                    class: "imcms-flex--flex-1",
                    text: texts.docTitle
                });

                const $publishedDateHead = $("<div>", {
                    class: "imcms-grid-coll-3",
                    text: texts.publishDate
                });
                $publishedDateHead.modifiers = ["date"];

                const $modifiedDateHead = $("<div>", {
                    class: "imcms-grid-coll-3",
                    text: texts.modifiedDate
                });
                $modifiedDateHead.modifiers = ["date"];

                const $versionColumnHead = $("<div>", {
                    class: "imcms-grid-coll-1",
                    text: texts.version
                });
                $versionColumnHead.modifiers = ["currentVersion"];

                const $statusColumnHead = $("<div>", {
                    class: "imcms-grid-coll-13",
                    text: texts.status
                });
                $statusColumnHead.modifiers = ["status"];

                const containerHeadTitle = [$idColumnHead, $titleColumnHead];

                switch (typeSort) {
                    case PUBLISHED_DATE_ASC:
                    case PUBLISHED_DATE_DESC:
                        containerHeadTitle.push($publishedDateHead);
                        break;
                    case MODIFIED_DATE_ASC:
                    case MODIFIED_DATE_DESC:
                        containerHeadTitle.push($modifiedDateHead);
                        break;
                }

                containerHeadTitle.push($versionColumnHead, $statusColumnHead);

                return new BEM({
                    block: "imcms-document-list-titles",
                    elements: {
                        "title": containerHeadTitle
                    }
                }).buildBlockStructure("<div>", {
                    class: "imcms-menu-list-titles"
                });
            }

            return new BEM({
                block: "imcms-document-list",
                elements: {
                    "titles": buildMenuTitlesRow(),
                    "items": $menuItemsBlock = buildMenuElements(menuElementsTree)
                }
            }).buildBlockStructure("<div>", {
                class: "imcms-menu-list"
            });
        }

        function buildMenuItemNewButton() {
            const toolBEM = new BEM({
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

            const $newDocButtonContainer = toolBEM.buildBlock("<div>", [{"button": buildNewDocButton()}]);
            $newDocButtonContainer.modifiers = ["grid-col-2"];

            return $newDocButtonContainer;
        }

        function changeControls() {
            const menuDocs = $(".imcms-menu-items-list").find(".imcms-menu-items");
            const enabledMultiRemove = isMultiRemoveModeEnabled();
            const controlsClass = enabledMultiRemove
                ? 'imcms-document-item__multi-remove-controls'
                : 'imcms-document-item__controls';
            menuDocs.each(function () {
                const $item = $(this).first();
                const menuItemId = $item.attr("data-document-id");
                const menuItem = mapDocumentToMenuItem(documentEditorBuilder.getDocumentById(menuItemId));
                if ($item.find(".children-triangle").length === 0) {
                    $item.find(".imcms-controls")
                        .last()
                        .replaceWith(buildMenuItemControls(menuItem, enabledMultiRemove)
                            .addClass(controlsClass));
                } else {
                    $item.find(".imcms-controls").slice(1, 2)
                        .replaceWith(buildMenuItemControls(menuItem, enabledMultiRemove)
                            .addClass(controlsClass));
                }
            });
        }

        function buildMultiRemoveCheckBox(menuItemId) {
            return components.checkboxes.imcmsCheckbox('<div>', {
                value: menuItemId,
                checked: 'checked'
            });
        }


        function buildSwitchesOffOnButtons() {

            function switchButtonAction() {
                const $switchButton = $('.imcms-switch-block__button');
                const $switchActiveInfoBlock = $('.imcms-switch-block__active-info');
                if (isMultiRemoveModeEnabled()) {
                    $switchButton.removeClass(classButtonOn).addClass(classButtonOff);
                    $switchActiveInfoBlock.text(texts.multiRemoveInfoOff);
                } else if ($switchButton.hasClass(classButtonOff)) {
                    $switchButton.removeClass(classButtonOff).addClass(classButtonOn);
                    $switchActiveInfoBlock.text(texts.multiRemoveInfoOn);
                }

                changeControls();
            }

            return new BEM({
                block: 'imcms-switch-block',
                elements: {
                    'active-info': components.texts.infoText('<div>', texts.multiRemoveInfoOff),
                    'button': components.buttons.switchOffButton({
                        click: switchButtonAction
                    }),
                    'multi-remove': components.buttons.positiveButton({
                        text: texts.multiRemove,
                        click: removeEnabledMenuItems
                    })
                }
            }).buildBlockStructure('<div>');
        }


        function removeEnabledMenuItems() {
            alert('remove!');
        }

        let mapTypesSort = new Map();

        function buildTypeSortingSelect(opts) {
            let typesSortSelect = components.selects.selectContainer('<div>', {
                id: 'type-sort',
                emptySelect: false,
                text: texts.titleTypeSort,
                onSelected: buildOnSelectedTypeSort
            });

            let requestNested = {
                nested: opts.nested
            };

            function defineListLocalizeTypesByNested(localizeTypes, nested) {
                if (nested) {
                    return localizeTypes;
                } else {
                    return localizeTypes.slice(1);
                }
            }

            typesSortRestAPI.getSortTypes(requestNested).done(types => {
                mapTypesSort.clear();
                types.map((typeOriginal, index) => {
                    mapTypesSort.set(defineListLocalizeTypesByNested(localizeTypesSort, requestNested.nested)[index], typeOriginal)
                });

                let keys = [...mapTypesSort.keys()];

                let typesSortDataMapped = keys.map(typeKey => ({
                    text: typeKey,
                    'data-value': mapTypesSort.get(typeKey)
                }));

                components.selects.addOptionsToSelect(typesSortDataMapped, typesSortSelect.getSelect(), buildOnSelectedTypeSort(opts));
            }).fail(() => modal.buildErrorWindow(texts.error.loadFailed));

            return typesSortSelect;
        }

        let prevType;

        function buildMenuItemsBySelectedType(menuData) {
            menusRestApi.getSortedItems(menuData).done(menuItems => {
                prevType = menuData.typeSort;
                $menuElementsContainer.find('.imcms-menu-list').remove();
                let $menuItemsSortedList = buildMenuEditorContent(menuItems, menuData.typeSort);
                $menuElementsContainer.append($menuItemsSortedList);
            }).fail(() => modal.buildErrorWindow(texts.error.loadFailed));
        }

        function buildOnSelectedTypeSort(opts) {
            return type => {

                const menuItems = $menuElementsContainer.find("[data-menu-items-lvl=1]")
                    .map(mapToMenuItem)
                    .toArray();

                let menuData = {
                    docId: opts.docId,
                    menuIndex: opts.menuIndex,
                    menuItems: menuItems,
                    nested: opts.nested,
                    typeSort: type
                };

                function getKeyByValue(mapObjects, prevType) {
                    for (let [key, value] of mapObjects.entries()) {
                        if (value === prevType) {
                            return key;
                        }
                    }
                }

                if (menuData.nested === true && prevType === TREE_SORT) {
                    modal.buildModalWindow(texts.confirmFlatSortMessage, confirmed => {
                        if (!confirmed) {
                            let prevKeySelected = getKeyByValue(mapTypesSort, prevType);
                            $('#type-sort').attr('value', prevType);
                            $editorHeadContainer.find('.imcms-drop-down-list__select-item-value').text(prevKeySelected);
                            return;
                        }

                        buildMenuItemsBySelectedType(menuData)
                    });
                } else {
                    buildMenuItemsBySelectedType(menuData)
                }
            };
        }

        let $editorHeadContainer;

        function buildEditorContainer(opts) {
            return $editorHeadContainer = new BEM({
                block: 'imcms-menu-editor-head',
                elements: {
                    'new-button': buildMenuItemNewButton(),
                    'switch-multi-delete': buildSwitchesOffOnButtons(),
                    'type-sort-block': buildTypeSortingSelect(opts)
                }
            }).buildBlockStructure('<div>')
        }

        function fillEditorContent(menuElementsTree, opts) {
            const typeSort = opts.nested ? TREE_SORT : 'MANUAL';
            prevType = typeSort;
            const $menuElementsTree = buildMenuEditorContent(menuElementsTree, typeSort);

            $menuElementsContainer.append(buildEditorContainer(opts));
            $menuElementsContainer.append($menuElementsTree);

            $documentEditor = documentEditorBuilder.buildBody();
            $documentsContainer.append($documentEditor);
            documentEditorBuilder.loadDocumentEditorContent($documentEditor, {moveEnable: true});
        }

        function loadMenuEditorContent(opts) {
            addHeadData(opts);
            menusRestApi.read(opts)
                .done(items => {
                    fillEditorContent(items, opts)
                })
                .fail(() => modal.buildErrorWindow(texts.error.loadFailed));
        }

        function addHeadData(opts) {
            let linkData = "/api/admin/menu?meta-id="
                + opts.docId
                + "&index=" + opts.menuIndex
                + "&nested=" + opts.nested;

            $title.text(linkData).css({
                'text-transform': 'lowercase',
                'color': '#fff2f9'
            });

            $title.attr('href', linkData)
        }

        let $menuEditorContainer;

        function buildMenuEditor(opts) {
            return $menuEditorContainer = new BEM({
                block: "imcms-menu-editor",
                elements: {
                    "head": buildHead(opts),
                    "body": buildBody(),
                    "footer": buildFooter(opts)
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
