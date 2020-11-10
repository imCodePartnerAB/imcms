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

        const WORKING_VERSION = 0;

        const PUBLISHED_DATE_ASC = 'PUBLISHED_DATE_ASC';
        const PUBLISHED_DATE_DESC = 'PUBLISHED_DATE_DESC';
        const MODIFIED_DATE_ASC = 'MODIFIED_DATE_ASC';
        const MODIFIED_DATE_DESC = 'MODIFIED_DATE_DESC';
        const TREE_SORT = 'TREE_SORT';
        const MANUAL = 'MANUAL';
        const classButtonOn = "imcms-button--switch-on";
        const classButtonOff = "imcms-button--switch-off";
        const multiRemoveControlClass = 'imcms-document-item__multi-remove-controls';
        const rightPaddingNoneClassName = 'imcms-flex--pr-0';

        let $menuElementsContainer, $documentsContainer, $documentEditor;
        const $title = $('<a>');
        const localizeTypesSort = [
            texts.typesSort.treeSort,
            texts.typesSort.manual,
            texts.typesSort.alphabeticalAsc,
            texts.typesSort.alphabeticalDesc,
            texts.typesSort.publishedDateAsc,
            texts.typesSort.publishedDateDesc,
            texts.typesSort.modifiedDateAsc,
            texts.typesSort.modifiedDateDesc,
        ];
        const topPointMenu = 178; // top point menu for set item before item in the top position.
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
                sortOrder: $(this).first().find('.imcms-document-item__sort-order').children().val().trim(),
                children: $(this).children("[data-menu-items-lvl]").map(mapToMenuItem).toArray()
            }
        }

        function isDocumentItem(document) {
            return document.commonContents !== undefined && document.currentVersion.id !== undefined;
        }

        function mapToMenuItemWithAllFields(menuItem) {

            const docId = menuItem.documentId;
            const document = documentEditorBuilder.getDocumentById(docId);
            let menuElement;

            if (isDocumentItem(document)) {
                menuElement = {
                    documentId: docId,
                    type: document.type,
                    modifiedDate: document.modified,
                    publishedDate: document.published,
                    hasNewerVersion: document.currentVersion.id === WORKING_VERSION,
                    sortOrder: menuItem.sortOrder,
                    documentStatus: document.documentStatus,
                    children: menuItem.children.map(item => mapToMenuItemWithAllFields(item))
                };


                document.commonContents.forEach(commonContent => {
                    if (commonContent["language"]["code"] === imcms.language.code) {
                        menuElement["title"] = commonContent["headline"];
                    }
                    if (commonContent.enabled && commonContent.language.code === imcms.language.code) {
                        menuElement["isShownTitle"] = true;
                    }
                });

            } else{
                menuElement = {
                    documentId: docId,
                    type: document.type,
                    modifiedDate: document.modified,
                    publishedDate: document.published,
                    title: document.title,
                    hasNewerVersion: document.currentVersion === WORKING_VERSION,
                    sortOrder: menuItem.sortOrder,
                    documentStatus: document.documentStatus,
                    isShownTitle: document.isShownTitle,
                    children: menuItem.children.map(item => mapToMenuItemWithAllFields(item))
                };
            }

                return menuElement;
        }

        function getAllMenuItems() {
            return $menuElementsContainer.find("[data-menu-items-lvl=1]")
                .map(mapToMenuItem)
                .toArray();
        }

        function saveMenuElements(opts) {
            const menuItems = $menuElementsContainer.find("[data-menu-items-lvl=1]")
                .map(mapToMenuItem)
                .toArray();

            const menuDTO = {
                menuIndex: opts.menuIndex,
                docId: opts.docId,
                menuItems: menuItems,
                nested: opts.nested,
                typeSort: document.getElementById('type-sort').value.trim()
            };

            menusRestApi.create(menuDTO)
                .done(menu => {
                    onMenuSaved();
                    menuWindowBuilder.closeWindow();
                })
                .fail(() => modal.buildErrorWindow(texts.error.createFailed));

        }

        function saveAndClose(opts) {
            if (document.getElementById('saveAndCloseMenuArea').classList.contains('imcms-button--disabled-click')) {
                alert(texts.error.fixInvalidPosition);
            } else {
                saveMenuElements(opts);
            }
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

        function highlightMenuDoc() {
            disableHighlightingMenuDoc();
                $(".imcms-menu-items-list").find('.imcms-menu-items--is-drop').css({
                    'border': '1px dashed red'
                });
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
                $menuItem.find(".imcms-document-item__info").first().before(
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
                    menuDoc.before($origin);
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
            const itemTree = $(".imcms-menu-items-list"),
                menuDocs = itemTree.find(".imcms-menu-item"),
                frameTop = $frame.position().top
            ;

            // get all menu doc coords
            const allMenuDocObjArray = getMenuItemsParam(menuDocs);

            let menuDoc = null,
                placeStatus = null
            ;
            let isTree = TREE_SORT === document.getElementById('type-sort').value;

            $.each(allMenuDocObjArray, (obj, param) => {
                if (frameTop > param.top && frameTop < ((param.bottom + param.top) / 2)) {
                    menuDoc = getMenuDocByObjId(obj);
                    placeStatus = false;
                    insertMenuCopyFrame(menuDoc, placeStatus, frameTop);
                }
                if (frameTop > ((param.bottom + param.top) / 2) && frameTop < param.bottom) {
                    menuDoc = getMenuDocByObjId(obj);
                    placeStatus = true;
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
                highlightMenuDoc();
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

            if (($dataInput.attr('data-frame-top') < topPointMenu
                || $dataInput.attr("data-type-sort") === TREE_SORT
                || $dataInput.attr("data-type-sort") === MANUAL)
                && getAllMenuItems().length !== 0) {
                $menuElement = buildMenuItemTree(menuElementsTree, {
                    level: 1,
                    sortType: $dataInput.attr("data-type-sort")
                });
                $menuElementsContainer.find("[data-menu-items-lvl=1]").first().before($menuElement);
            } else {
                if ($dataInput.attr("data-parent-id") !== "") {
                    if ($dataInput.attr("data-insert-place") === "true") {
                        $menuElement = buildMenuItemTree(menuElementsTree, {
                            level: level + 1,
                            sortType: $dataInput.attr("data-type-sort"),
                        });
                        $menuElementsContainer.find("[data-document-id=" + parentId + "]").append($menuElement);

                        const parent = $menuElement.parent();
                        if (parent.find(".children-triangle").length === 0) {
                            parent.find(".imcms-menu-item").first().find(".imcms-document-item__info").first().before(
                                buildChildrenTriangle().addClass("imcms-document-item__btn imcms-document-item__btn--open")
                            );
                        }
                    } else {
                        $menuElement = buildMenuItemTree(menuElementsTree, { level, sortType: $dataInput.attr("data-type-sort") });
                        $menuElementsContainer.find("[data-document-id=" + parentId + "]").before($menuElement);
                    }
                } else {
                    $menuElement = buildMenuItemTree(menuElementsTree, { level, sortType: $dataInput.attr("data-type-sort") });
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
                    id: 'saveAndCloseMenuArea',
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


        function controlPaddingClassForTitlesHEAD() {
            if (isMultiRemoveModeEnabled() && !$menuTitlesBlock.hasClass(rightPaddingNoneClassName) ) {
                $menuTitlesBlock.addClass(rightPaddingNoneClassName);
            } else {
                $menuTitlesBlock.removeClass(rightPaddingNoneClassName);
            }
        }

        function removeMenuItemFromEditor(currentMenuItem, activeMultiRemove) {
            const submenuItem = activeMultiRemove
                ? currentMenuItem.find(".imcms-menu-items")
                : currentMenuItem.parent().find(".imcms-menu-items"),

                parentMenuItem = currentMenuItem.closest(".imcms-menu-items"),
                currentMenuItemWrap = parentMenuItem.parent(),
                currentMenuItemId = activeMultiRemove
                    ? parseInt(currentMenuItem.data('document-id'))
                    : parseInt(currentMenuItem.find(".imcms-document-item__info--id").text());

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
                documentEditorBuilder.refreshDocumentInList(document, isDocumentItem(document));
            });

            let parentDoc = documentEditorBuilder.getDocumentById(currentMenuItemId);
            documentEditorBuilder.refreshDocumentInList(parentDoc, isDocumentItem(parentDoc));
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
                reorderMenuListBySortNumber(getAllMenuItems());
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
            const sortType = document.getElementById("type-sort").value;

            getFirstItemInMenuArea().before(buildMenuItemTree(getMenuElementTree(doc), { level: 1, sortType }));

            documentEditorBuilder.addDocumentToList(doc);
            reorderMenuListBySortNumber(getAllMenuItems());
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

                    $menuItem.removeClass((index, className) => (className.match(/imcms-menu-items__document-item--\S+/g) || []).join(' '));

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
            const $multiRemoveBoxControl = buildMultiRemoveCheckBox();
            $multiRemoveBoxControl.modifiers = ['multi-remove'];

            const $controlRemove = enabledMultiRemoveMode
                ? $multiRemoveBoxControl
                : components.controls.remove(function () {
                    removeMenuItem.call(this, menuElementTree.documentId);
                });
            components.overlays.defaultTooltip($controlRemove, texts.remove);

            const $controlEdit = components.controls.edit(() => {
                pageInfoBuilder.build(menuItemId, refreshMenuItem, menuElementTree.type);
            });
            components.overlays.defaultTooltip($controlEdit, texts.edit);

            const $controlCopy = components.controls.copy(() => {
                const $animationBlock = $('.imcms-document-editor-head-tool__load');
                $animationBlock.css('display', 'inline-table');

                docCopyRestApi.copy(menuItemId)
                    .done(copiedDocument => {

                        // const $documentItemContainer = documentEditorBuilder
                        //     .buildDocument(copiedDocument, {moveEnable: true}, true);

                        // $documentEditor.find(".imcms-document-list__items").prepend($documentItemContainer);

                        appendNewMenuItem(copiedDocument);
                        $animationBlock.css('display', 'none');
                    })
                    .fail(() => modal.buildErrorWindow(texts.error.copyDocumentFailed));
            });
            components.overlays.defaultTooltip($controlCopy, texts.copy);

            return enabledMultiRemoveMode
                ? components.controls.buildControlsBlock("<div>", [$controlRemove, $controlCopy, $controlEdit], {
                    'class': multiRemoveControlClass
                })
                : components.controls.buildControlsBlock("<div>", [$controlRemove, $controlCopy, $controlEdit]);
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
            let $moveControl;
            if (typeSort === TREE_SORT) {
                $moveControl = components.controls.move();
            } else {
                $moveControl = components.controls.vertical_move();
            }
            $moveControl.on("mousedown", dragMenuItem);
            $numberingTypeSortFlag.isChecked() ? $moveControl.hide() : $moveControl.show();

            return components.controls.buildControlsBlock("<div>", [$moveControl]);
        }

        function clickReOrderMenuItemsBySortNumbers() {
            reorderMenuListBySortNumber(getAllMenuItems());
        }

        function getDeepSortedItemsBySortNumber(menuItems) {
            return menuItems
                .map((item, index, arr) => {
                    if (item.children.length) {
                        getDeepSortedItemsBySortNumber(item.children);
                    } else {
                        arr.sort(function (menuItem1, menuItem2) {
                            const parsedValItem1 = parseValue(menuItem1.sortOrder);
                            const parsedValItem2 = parseValue(menuItem2.sortOrder);

                            return parsedValItem1[parsedValItem1.length - 1] - parsedValItem2[parsedValItem2.length - 1];
                        })
                    }
                    return item;
                });
        }

        function reorderMenuListBySortNumber(menuItems, isOldValMoreCurrent, menuItemId) {
            const currentTypeSort = document.getElementById('type-sort').value.trim();
            getDeepSortedItemsBySortNumber(menuItems);

            swapSameItemSortNumber(menuItems, isOldValMoreCurrent);

            const mappedMenuItems = menuItems.map(menuItem => mapToMenuItemWithAllFields(menuItem));

            $menuElementsContainer.find('.imcms-menu-list').remove();
            const $menuItemsSortedList = buildMenuEditorContent(mappedMenuItems, currentTypeSort);
            controlPaddingClassForTitlesHEAD();
            $menuElementsContainer.append($menuItemsSortedList);

            const $changedItem = getMenuDocByObjId(menuItemId);
            if ($changedItem) {
                $changedItem.addClass('imcms-border-light');

                setTimeout(function () {
                    $changedItem.removeClass('imcms-border-light');
                }, 5000);
            }


        }

        function findPlaceMenuElement(menuItems, index) {
            const indexArray = parseIndex(index);
            const placementElement = indexArray.reduce((acc, value, i) => {
                if (indexArray.length - 1 === i) {
                    return acc;
                }
                if (acc[value]) {
                    return acc[value].children;
                }
                throw new Error(texts.error.invalidSortNumber);
            }, menuItems);

            return placementElement;
        }

        function parseIndex(index) {
            return index.split('.').map(n => parseInt(n) - 1);
        }

        function parseValue(value) {
            return value.split('.').map(n => parseInt(n));
        }

        function isCheckOldValueMoreThanCurrentValue(oldVal, currentVal) {
            const arrayOldVal = oldVal.split('.').map(n => parseInt(n));
            const arrayCurrentVal = currentVal.split('.').map(n => parseInt(n));

            return arrayOldVal.some((oldVal, index) => oldVal > arrayCurrentVal[index])
        }

        function buildMenuItem(menuElement, {sortType}) {

            function removeAndAddClassForInCorrectData($input, isAdd) {
                if ($input.hasClass('imcms-menu-incorrect-data-light')) {
                    $input.removeClass('imcms-menu-incorrect-data-light');
                    document.getElementById('saveAndCloseMenuArea').classList.remove('imcms-button--disabled-click');
                }
                if (isAdd) {
                    $input.addClass('imcms-menu-incorrect-data-light');
                    alert(texts.error.invalidPosition);
                    document.getElementById('saveAndCloseMenuArea').classList.add('imcms-button--disabled-click')
                }
            }

            const $numberingSortBox = components.texts.textBox('<div>', {
                class: 'imcms-flex--m-auto',
                value: menuElement.sortOrder,
                id: menuElement.sortOrder,
                name: menuElement.documentId
            });

            $numberingSortBox.$input.blur(events => {
                const currentValue = $(events.target).val();
                const currentIndex = $(events.target).attr('id');
                const documentId = $(events.target).attr('name');
                const parsedIndex = parseIndex(currentIndex);
                const parsedValue = parseIndex(currentValue);
                const items = getAllMenuItems();

                removeAndAddClassForInCorrectData($(events.target));
                if (currentValue === currentIndex) return;
                let placementElement;
                try {
                    placementElement = findPlaceMenuElement(items, currentValue);
                } catch (e) {
                    console.error(e);
                    removeAndAddClassForInCorrectData($(events.target), true);
                    return;
                }

                if (parsedIndex.every((value, index) => value === parsedValue[index])) {
                    removeAndAddClassForInCorrectData($(events.target), true);
                    return;
                }

                const foundElement = parsedIndex.reduce((acc, value, i) => {
                    if (parsedIndex.length - 1 === i) {
                        return Array.isArray(acc) ? acc : acc.children;
                    } else {
                        return Array.isArray(acc) ? acc[value] : acc.children[value];
                    }
                }, items);
                const splicedElem = foundElement.splice(parsedIndex[parsedIndex.length - 1], 1)[0];
                placementElement.push(splicedElem);

                reorderMenuListBySortNumber(items, isCheckOldValueMoreThanCurrentValue(currentIndex, currentValue), documentId);
            });

            sortType === MANUAL ? $numberingTypeSortFlag.show() : $numberingTypeSortFlag.hide();
            $numberingTypeSortFlag.isChecked() ? $numberingSortBox.show() : $numberingSortBox.hide();

            const $docId = components.texts.titleText('<a>', menuElement.documentId, {
                href: '/' + menuElement.documentId,
                target: '_blank',
                class: 'imcms-grid-col-1',
            });
            $docId.modifiers = ['id'];
            components.overlays.defaultTooltip(
                $docId,
                documentEditorBuilder.getIdTooltipText(menuElement.documentId, menuElement.createdDate, menuElement.createdBy),
                { placement: 'right' },
            );

            const title = menuElement.title
                ? menuElement.title
                : documentBuilderTexts.notShownInSelectedLang;
            const $titleText = components.texts.titleText('<a>', title, {
                href: "/" + menuElement.documentId,
                class: 'imcms-flex--flex-1',
            });
            $titleText.modifiers = ['title'];
            !menuElement.title && $titleText.modifiers.push("notShownTitle");
            title && components.overlays.defaultTooltip($titleText, title, {placement: 'right'});

            const $publishedDate = components.texts.titleText('<div>', menuElement.publishedDate, {
                class: 'imcms-grid-col-3',
            });
            $publishedDate.modifiers = ['date'];
            components.overlays.defaultTooltip(
                $publishedDate,
                documentEditorBuilder.getPublishedDateTooltipText(menuElement.publishedDate, menuElement.publishedBy),
            );

            const $modifiedDate = components.texts.titleText('<div>', menuElement.modifiedDate, {
                class: 'imcms-grid-col-3',
            });
            components.overlays.defaultTooltip(
                $modifiedDate,
                documentEditorBuilder.getModifiedDateTooltipText(menuElement.modifiedDate, menuElement.modifiedBy),
            );

            const $star = menuElement.hasNewerVersion
                ? components.controls.star()
                : components.controls.star().css({'filter': 'grayscale(100%) brightness(140%)'});
            const $currentVersion = $('<div>').append($star).addClass('imcms-grid-col-1');
            components.overlays.defaultTooltip(
                $currentVersion,
                documentEditorBuilder.getDocumentVersionTexts(menuElement.hasNewerVersion).tooltip
            );
            $currentVersion.modifiers = ['currentVersion'];

            const documentStatusTexts = documentEditorBuilder.getDocumentStatusTexts(menuElement.documentStatus, menuElement.publishedDate);
            const $documentStatus = components.texts.titleText("<div>", documentStatusTexts.title, {
                class: 'imcms-grid-col-13'
            });
            $documentStatus.modifiers = ['status'];
            components.overlays.defaultTooltip($documentStatus, documentStatusTexts.tooltip);

            const elements = [$docId, $titleText];
            let childrenIcon = "";
            if (menuElement.children.length) {
                childrenIcon = (buildChildrenTriangle().addClass("imcms-document-item__btn imcms-document-item__btn--open"));
            }

            switch (sortType) {
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

            const controls = [buildMoveControl(sortType), buildMenuItemControls(menuElement, isMultiRemoveModeEnabled())];

            return new BEM({
                block: "imcms-document-item",
                elements: [
                    {'sort-order': [$numberingSortBox]},
                    {"btn-icon": childrenIcon},
                    {"info": elements},
                    {"controls": controls}
                ]
            }).buildBlockStructure("<div>", {
                class: "imcms-menu-item"
            });
        }

        function setSortNumbersInMenuItems(menuElements, treeKey) {
            for (let i = 0; i < menuElements.length; i++) {
                const currentMenuItem = menuElements[i];
                const hasChildren = currentMenuItem.children.length;
                let dataTreeKey = treeKey ? treeKey + "." + (i + 1) : (i + 1) + '';
                const isEmptySortNumber = currentMenuItem.sortOrder;

                currentMenuItem.sortOrder = dataTreeKey;

                if (hasChildren) {
                    dataTreeKey = isEmptySortNumber ? dataTreeKey : currentMenuItem.sortOrder;
                    setSortNumbersInMenuItems(menuElements[i].children, dataTreeKey);
                }
            }
        }

        function swapSameItemSortNumber(menuItems, oldValMoreCurrent) {
            let duplicate;
            menuItems.forEach((item, index, array) => {
                if (item.children.length) {
                    swapSameItemSortNumber(item.children, oldValMoreCurrent);
                }
                duplicate = array.map(item => item.sortOrder).indexOf(item.sortOrder) !== index ? item : duplicate
            });

            const foundSameItemIndex = menuItems.findIndex(item => item === duplicate);

            if (foundSameItemIndex !== -1) {
                if (oldValMoreCurrent) {
                    const sameItem = menuItems[foundSameItemIndex];
                    menuItems[foundSameItemIndex] = menuItems[foundSameItemIndex - 1];
                    menuItems[foundSameItemIndex - 1] = sameItem;
                }
            }
        }

        function getFlattenMenuItems(menuElements, flatMenuItems) {
            flatMenuItems = (flatMenuItems.length) ? flatMenuItems : [];
            for (let i = 0; i < menuElements.length; i++) {
                const currentMenuItem = menuElements[i];
                const hasChildren = currentMenuItem.children.length;
                flatMenuItems.push(currentMenuItem);
                if (hasChildren) {
                    getFlattenMenuItems(currentMenuItem.children, flatMenuItems)
                }
            }
            return flatMenuItems;
        }

        function buildMenuItemTree(menuElement, {level, sortType}) {
            menuElement.children = menuElement.children || [];

            const treeBlock = new BEM({
                block: "imcms-document-items",
                elements: [{
                    "document-item": buildMenuItem(menuElement, {sortType}),
                    modifiers: [menuElement.documentStatus.replace(/_/g, "-").toLowerCase()]
                }]
            }).buildBlockStructure("<div>", {
                class: "imcms-menu-items",
                "data-menu-items-lvl": level,
                "data-document-id": menuElement.documentId
            });

            ++level;

            const $childElements = menuElement.children.map(childElement => {
                const $itemTree = buildMenuItemTree(childElement, {level, sortType});
                $itemTree.addClass("imcms-submenu-items--close");
                return $itemTree;
            });

            return treeBlock.append($childElements);
        }

        let $menuTitlesBlock;
        let $menuItemsBlock;
        let $sortOrderColumnHead;

        let $removeButton;

        function buildMenuEditorContent(menuElementsTree, typeSort) {
            function buildMenuElements(menuElements) {
                setSortNumbersInMenuItems(menuElements, null);
                const $menuItems = menuElements.map(menuElement => {
                    return buildMenuItemTree(menuElement, {level: 1, sortType: typeSort});
                });
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
                $sortOrderColumnHead = $("<div>", {
                    class: "imcms-grid-col-1",
                    text: texts.order,
                    click: clickReOrderMenuItemsBySortNumbers,
                });
                $sortOrderColumnHead.modifiers = ["sort-order"];
                $numberingTypeSortFlag.isChecked() ? $sortOrderColumnHead.show() : $sortOrderColumnHead.hide();
                $removeButton = components.buttons.positiveButton({
                    text: texts.multiRemove,
                    click: removeEnabledMenuItems
                });

                isMultiRemoveModeEnabled() ? $removeButton.css('display', 'block') : $removeButton.css('display', 'none');

                const $idColumnHead = $("<div>", {
                    class: "imcms-grid-col-1",
                    text: texts.id
                });
                $idColumnHead.modifiers = ["id"];

                const $titleColumnHead = $("<div>", {
                    class: "imcms-flex--flex-1",
                    text: texts.docTitle
                });

                const $publishedDateHead = $("<div>", {
                    class: "imcms-grid-col-3",
                    text: texts.publishDate
                });
                $publishedDateHead.modifiers = ["date"];

                const $modifiedDateHead = $("<div>", {
                    class: "imcms-grid-col-3",
                    text: texts.modifiedDate
                });
                $modifiedDateHead.modifiers = ["date"];

                const $versionColumnHead = $("<div>", {
                    class: "imcms-grid-col-1",
                    text: texts.version
                });
                $versionColumnHead.modifiers = ["currentVersion"];

                const $statusColumnHead = $("<div>", {
                    class: "imcms-grid-col-13",
                    text: texts.status
                });
                $statusColumnHead.modifiers = ["status"];

                const containerHeadTitle = [$sortOrderColumnHead, $idColumnHead, $titleColumnHead];

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
                        "title": containerHeadTitle,
                        "multi-remove": $removeButton,
                    }
                }).buildBlockStructure("<div>", {
                    class: "imcms-menu-list-titles"
                });
            }

            return new BEM({
                block: "imcms-document-list",
                elements: {
                    "titles": $menuTitlesBlock = buildMenuTitlesRow(),
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
                    text: '+',
                    click: onNewDocButtonClick
                });
            }

            function onDocumentSaved(document) {
                appendNewMenuItem(document);
            }

            function onNewDocButtonClick(e) {
                e.preventDefault();
                docTypeSelectBuilder.build(type => {
                    docProfileSelectBuilder.build(parentDocId => {
                        pageInfoBuilder.build(null, onDocumentSaved, type, parentDocId);
                    }, {inMenu: true});
                });
            }

            return toolBEM.buildBlock("<div>", [{"button": buildNewDocButton()}], {
                class: 'imcms-flex--flex-1'
            });
        }

        function changeControlsByMultiRemove() {
            const menuDocs = $(".imcms-menu-items-list").find(".imcms-menu-items");
            const enabledMultiRemove = isMultiRemoveModeEnabled();
            const controlsClass = enabledMultiRemove
                ? 'imcms-document-item__multi-remove-controls'
                : 'imcms-document-item__controls';
            menuDocs.each(function () {
                const $item = $(this).first();
                const menuItemId = $item.attr("data-document-id");
                const menuItem = mapDocumentToMenuItem(documentEditorBuilder.getDocumentById(menuItemId));
                if (itemHasChildrenTriangle($item)) {
                    $item.find(".imcms-controls").slice(1, 2)
                        .replaceWith(buildMenuItemControls(menuItem, enabledMultiRemove)
                            .addClass(controlsClass));
                } else {
                    $item.find(".imcms-controls")
                        .last()
                        .replaceWith(buildMenuItemControls(menuItem, enabledMultiRemove)
                            .addClass(controlsClass));
                }
            });
        }

        function itemHasChildrenTriangle($menuItem) {
            return $menuItem.find(".children-triangle").length !== 0;
        }

        function buildMultiRemoveCheckBox() {
            return components.checkboxes.imcmsCheckbox('<div>', {
                value: true,
                checked: 'checked',
                change: changeValueCheckBox
            });
        }

        function changeValueCheckBox() {
            const $this = $(this);
            const newVal = $this.is(":checked");
            $this.val(newVal);
        }


        function buildSwitchesOffOnButtons() {

            function switchButtonAction() {
                const $switchButton = $('.imcms-switch-block__button');
                const $switchActiveInfoBlock = $('.imcms-switch-block__active-info');

                if (isMultiRemoveModeEnabled()) {
                    $switchButton.removeClass(classButtonOn).addClass(classButtonOff);
                    $switchActiveInfoBlock.text(texts.multiRemoveInfoOff);
                    $removeButton.css('display', 'none');
                    $menuTitlesBlock.removeClass(rightPaddingNoneClassName);
                } else if ($switchButton.hasClass(classButtonOff)) {
                    $switchButton.removeClass(classButtonOff).addClass(classButtonOn);
                    $switchActiveInfoBlock.text(texts.multiRemoveInfoOn);
                    $removeButton.css('display', 'block');
                    $menuTitlesBlock.addClass(rightPaddingNoneClassName);
                }

                changeControlsByMultiRemove();
            }

            return new BEM({
                block: 'imcms-switch-block',
                elements: {
                    'active-info': components.texts.infoText('<div>', texts.multiRemoveInfoOff),
                    'button': components.buttons.switchOffButton({
                        click: switchButtonAction
                    }),
                }
            }).buildBlockStructure('<div>');
        }


        function removeEnabledMenuItems() {
            const menuDocs = $(".imcms-menu-items-list").find(".imcms-menu-items");

            menuDocs.each(function () {
                const $item = $(this).first();

                if (isActiveCheckBoxMultiRemoveInMenuItem($item, itemHasChildrenTriangle($item))) {
                    removeMenuItemFromEditor($item, true);
                }
            });

            function isActiveCheckBoxMultiRemoveInMenuItem($item, isHasChildren) {
                let isCheckedItem;

                if (isHasChildren) {
                    isCheckedItem = $item.find(".imcms-controls")
                        .slice(1, 2)
                        .find('.imcms-controls__control--multi-remove')[0].firstChild.checked;
                } else {
                    isCheckedItem = $item.find(".imcms-controls")
                        .last()
                        .find('.imcms-controls__control--multi-remove')[0].firstChild.checked;
                }

                return isCheckedItem;
            }

            reorderMenuListBySortNumber(getAllMenuItems());
        }

        let mapTypesSort = new Map();

        function toggleNumberingSortFields(isChecked) {
            const menuDocs = $(".imcms-menu-items-list").find(".imcms-menu-items");

            const toggleCommand = isChecked
                ? $element => $element.show()
                : $element => $element.hide();

            menuDocs.each(function() {
                const $item = $(this).find('.imcms-document-item__sort-order');
                toggleCommand($item);
            });
        }

        function toggleMenuTitlesIndent(isChecked) {
            isChecked ? $sortOrderColumnHead.show() :$sortOrderColumnHead.hide();
        }

        function toggleDragAndDrop(isChecked) {
            const $menuItem = $('.imcms-menu-item');
            const moveControls = [
                $menuItem.find('.imcms-control--move'),
                $menuItem.find('.imcms-control--vertical_move'),
            ];
            const command = isChecked
                ? $el => $el.hide()
                : $el => $el.show();
            moveControls.forEach(command);
        }

        let $numberingTypeSortFlag;

        function onChangeNumberingSortFlag() {
            const isChecked = $numberingTypeSortFlag.isChecked();
            toggleNumberingSortFields(isChecked);
            toggleMenuTitlesIndent(isChecked);
            toggleDragAndDrop(isChecked);
        }

        let $typesSortSelect;

        function buildTypeSortingSelect(opts) {
            $typesSortSelect = components.selects.selectContainer('<div>', {
                id: 'type-sort',
                class: 'imcms-flex--w-40',
                emptySelect: false,
                onSelected: buildOnSelectedTypeSort
            });

            $numberingTypeSortFlag = components.checkboxes.imcmsCheckbox('<div>', {
                text: texts.sortNumberTitle,
                checked: 'checked',
                change: onChangeNumberingSortFlag,
            });

            const requestNested = {
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

                components.selects.addOptionsToSelect(typesSortDataMapped, $typesSortSelect.getSelect(), buildOnSelectedTypeSort(opts));
            }).fail(() => modal.buildErrorWindow(texts.error.loadFailed));

            return new BEM({
                block: 'imcms-menu-sort',
                elements: {
                    'type-sort': $typesSortSelect,
                    'type-sort-numbering': $numberingTypeSortFlag
                }
            }).buildBlockStructure('<div>', {
                class: 'imcms-flex--flex-1',
            });
        }

        let prevType;

        function buildMenuItemsBySelectedType(menuData) {
            menusRestApi.getSortedItems(menuData).done(menuItems => {
                prevType = menuData.typeSort;
                controlPaddingClassForTitlesHEAD();
                rebuildAllMenuItemsInMenuContainer(menuItems, menuData.typeSort);
            }).fail(() => modal.buildErrorWindow(texts.error.loadFailed));
        }

        function rebuildAllMenuItemsInMenuContainer(menuItems, typeSort) {
            $menuElementsContainer.find('.imcms-menu-list').remove();
            let $menuItemsSortedList = buildMenuEditorContent(menuItems, typeSort);
            $menuElementsContainer.append($menuItemsSortedList);
        }

        function buildOnSelectedTypeSort(opts) {
            return type => {

                const menuItems = $menuElementsContainer.find("[data-menu-items-lvl=1]")
                    .map(mapToMenuItem)
                    .toArray();

                const menuData = {
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

                if (menuData.nested === true && prevType === TREE_SORT && type !== TREE_SORT) {
                    modal.buildModalWindow(texts.confirmFlatSortMessage, confirmed => {
                        if (!confirmed) {
                            let prevKeySelected = getKeyByValue(mapTypesSort, prevType);
                            $('#type-sort').attr('value', prevType);
                            $editorHeadContainer.find('.imcms-drop-down-list__select-item-value').text(prevKeySelected);
                            return;
                        }

                        buildMenuItemsBySelectedType(menuData);
                        toggleSortingCheckboxViewAndTurnOffIfNeededBySortType(type);
                    });
                } else {
                    buildMenuItemsBySelectedType(menuData);
                    toggleSortingCheckboxViewAndTurnOffIfNeededBySortType(type);
                }
            };
        }

        function toggleSortingCheckboxViewAndTurnOffIfNeededBySortType(sortType) {
            if (sortType === TREE_SORT || sortType === MANUAL) {
                $numberingTypeSortFlag.show()
                !$numberingTypeSortFlag.isChecked() && $numberingTypeSortFlag.setChecked(true);
            } else {
                $numberingTypeSortFlag.hide();
                $numberingTypeSortFlag.isChecked() && $numberingTypeSortFlag.setChecked(false);
            }
        }

        function buildHeadFirstLine() {
            return new BEM({
                block: 'imcms-menu-head-row',
                elements: {
                    'new-button': buildMenuItemNewButton(),
                }
            }).buildBlockStructure('<div>');
        }

        function buildHeadSecondLine(opts) {
            return new BEM({
                block: 'imcms-menu-head-row',
                elements: {
                    'type-sort-block': buildTypeSortingSelect(opts),
                    'switch-multi-delete': buildSwitchesOffOnButtons(),
                }
            }).buildBlockStructure('<div>');
        }

        let $editorHeadContainer;

        function buildEditorContainer(opts) {
            return $editorHeadContainer = new BEM({
                block: 'imcms-menu-editor-head',
                elements: {
                    'first-line': buildHeadFirstLine(),
                    'second-line': buildHeadSecondLine(opts),
                }
            }).buildBlockStructure('<div>');
        }

        function fillEditorContent(menuElementsTree, opts) {
            const typeSort = opts.nested ? TREE_SORT : MANUAL;
            prevType = typeSort;
            $menuElementsContainer.append(buildEditorContainer(opts));

            const $menuElementsTree = buildMenuEditorContent(menuElementsTree, typeSort);
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

        const menuWindowBuilder = new WindowBuilder({
            factory: buildMenuEditor,
            loadDataStrategy: loadMenuEditorContent,
            clearDataStrategy: clearData,
            onEscKeyPressed: "close",
            onEnterKeyPressed: saveAndClose
        });

        let $tag;

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
