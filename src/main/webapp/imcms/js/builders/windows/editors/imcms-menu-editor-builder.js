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
        "imcms-document-type-select-window-builder", "imcms-document-profile-select-window-builder", 'imcms-document-status'
    ],
    function (BEM, components, documentEditorBuilder, modal, WindowBuilder, menusRestApi, pageInfoBuilder, $,
              primitivesBuilder, reloadElement, events, texts, docCopyRestApi, imcms, docTypeSelectBuilder,
              docProfileSelectBuilder, docStatus) {

        const documentBuilderTexts = texts.editors.document;
        texts = texts.editors.menu;

        const WORKING_VERSION = 0;

        const PUBLISHED_DATE_ASC = 'PUBLISHED_DATE_ASC';
        const PUBLISHED_DATE_DESC = 'PUBLISHED_DATE_DESC';
        const MODIFIED_DATE_ASC = 'MODIFIED_DATE_ASC';
        const MODIFIED_DATE_DESC = 'MODIFIED_DATE_DESC';
        const TREE_SORT = 'TREE_SORT';
        const MANUAL = 'MANUAL';
        const ALPHABETICAL_ASC = 'ALPHABETICAL_ASC';
        const ALPHABETICAL_DESC = 'ALPHABETICAL_DESC';
        const classButtonOn = "imcms-button--switch-on";
        const classButtonOff = "imcms-button--switch-off";
        const multiRemoveControlClass = 'imcms-document-item__multi-remove-controls';

        const menuItemsListSelector = '.imcms-menu-items-list';

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

        const originalTypes = [
            TREE_SORT,
            MANUAL,
            ALPHABETICAL_ASC,
            ALPHABETICAL_DESC,
            PUBLISHED_DATE_ASC,
            PUBLISHED_DATE_DESC,
            MODIFIED_DATE_ASC,
            MODIFIED_DATE_DESC
        ];
        const topPointMenu = 170; // top point menu for set item before item in the top position.
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

        function get$menuItemsList() {
            return $(menuItemsListSelector);
        }

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
	                createdBy: document.createdBy,
	                createdDate: document.created,
                    modifiedDate: document.modified,
                    publishedDate: document.published,
                    hasNewerVersion: document.currentVersion.id === WORKING_VERSION,
                    sortOrder: menuItem.sortOrder,
                    documentStatus: document.documentStatus,
                    linkableForUnauthorizedUsers: document.linkableForUnauthorizedUsers,
                    linkableByOtherUsers: document.linkableByOtherUsers,
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

            } else {
                menuElement = {
                    documentId: docId,
                    type: document.type,
	                createdBy: document.createdBy,
	                createdDate: document.created,
                    modifiedDate: document.modified,
                    publishedDate: document.published,
                    title: document.title,
                    hasNewerVersion: document.currentVersion === WORKING_VERSION,
                    sortOrder: menuItem.sortOrder,
                    documentStatus: document.documentStatus,
                    isShownTitle: document.isShownTitle,
                    linkableForUnauthorizedUsers: document.linkableForUnauthorizedUsers,
                    linkableByOtherUsers: document.linkableByOtherUsers,
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
                typeSort: document.getElementById('type-sort').value.trim()
            };

            menusRestApi.create(menuDTO)
                .done(() => {
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
            //save scroll position
            const menuItemsListScrollPosition = get$menuItemsList().scrollTop();

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
	            reorderMenuListBySortNumber(getAllMenuItems());
                get$menuItemsList().scrollTop(menuItemsListScrollPosition);
            } else {
                $originItem.removeClass("imcms-menu-items--is-drag");
            }
        }

        function detectTargetArea(event) {
	        return (event.clientY > menuAreaProp.top) &&
                (event.clientY < menuAreaProp.bottom) &&
                (event.clientX > menuAreaProp.left) &&
                (event.clientX < menuAreaProp.right);
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
            const menuDocs = get$menuItemsList().find(".imcms-menu-items");
            let menuDoc = null;

            menuDocs.each(function () {
                if ($(this).attr("data-document-id") === objId) {
                    menuDoc = $(this)
                }
            });

            return menuDoc;
        }

        function getFirstItemInMenuArea() {
            const menuDocs = get$menuItemsList().find(".imcms-menu-items");
            return menuDocs.first();
        }

        function getLastItemInMenuArea() {
            const menuDocs = get$menuItemsList().find(".imcms-menu-items");
            return menuDocs.last();
        }

        function getLastParentItemInMenuArea() {
            const menuDocs = get$menuItemsList().find(".imcms-menu-items[data-menu-items-lvl='1']");
            return menuDocs.last();
        }

        function getMenuItemsParam(menuDocs) {
            const allMenuDocObjArray = {};
            menuDocs.each(function () {
                if (!$(this).closest(".imcms-menu-items").hasClass("imcms-menu-items--is-drag")) {
                    allMenuDocObjArray[$(this).closest(".imcms-menu-items").attr("data-document-id")] = {
                        top: this.getBoundingClientRect().top,
                        bottom: this.getBoundingClientRect().bottom
                    };
                }
            });
            return allMenuDocObjArray;
        }

        function disableHighlightingMenuDoc() {
            get$menuItemsList().find(".imcms-menu-items").css({
                "border": "none"
            });
        }

        function highlightMenuDoc() {
            get$menuItemsList().find('.imcms-menu-items--is-drop').css({
                'border': '1px dashed red'
            });
        }

        function removedPreviousItemFrame() {
            const $menuTree = get$menuItemsList(),
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


            if (menuDoc.attr("data-document-id") === $frame.attr("data-document-id")) {
                isPasted = false;
                return;
            }
            removedPreviousItemFrame();

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

	    function sortByDragging(container, enable) {
		    const $container = $(container).children().toArray().filter(entry => !entry.classList.contains("imcms-menu-item"));
		    $container.forEach(entry => {
			    const $entry = $(entry);
			    $entry.off('dragstart dragend drag').attr("draggable", false)
			    if (enable) {
				    $entry.on({
					    'dragstart': function () {
						    isMouseDown = true;
						    closeSubItems($(this))
					    },
					    'dragend': function (e) {
						    isMouseDown = false;
							disableHighlightingMenuDoc()
						    slideUpMenuDocIfItClose($(e.target))
						    sortByDragging(container, false);
							reorderMenuListBySortNumber(getAllMenuItems())
					    },
					    'drag': handleDragWhenSort
				    }).attr("draggable", true)
			    }
		    })
	    }

	    function handleDragWhenSort(e) {
		    e.preventDefault();
		    const selectedItem = e.target,
			    list = selectedItem.parentNode,
			    x = e.clientX,
			    y = e.clientY;

			//need to disable drag when tag <a> dragging
		    if (isMenuElementAllowedToDragSort(selectedItem)) {
			    const elementFromPoint = document.elementFromPoint(x, y);
			    let swapItem = elementFromPoint === null ? selectedItem : $(elementFromPoint).parents()[1];
			    selectedItem.classList.add("imcms-menu-items--is-drop");

			    if (swapItem && list === swapItem.parentNode) {
				    swapItem = swapItem !== selectedItem.nextSibling ? swapItem : swapItem.nextSibling;

				    const $swapItemSortOrder = $(swapItem).find('.imcms-document-item__sort-order').children().first();
				    const $selectedItemSortOrder = $(selectedItem).find('.imcms-document-item__sort-order').children().first();

				    $selectedItemSortOrder.val($swapItemSortOrder.val());

				    list.insertBefore(selectedItem, swapItem);
				    highlightMenuDoc();
			    }
		    }
		    selectedItem.classList.remove("imcms-menu-items--is-drop");
	    }

		function isMenuElementAllowedToDragSort(element) {
			return element.nodeName !== "A" && !element.classList.contains("imcms-control") &&
				!element.classList.contains("children-triangle");
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
            const itemTree = get$menuItemsList(),
                menuDocs = itemTree.find(".imcms-menu-item"),
                frameTop = $frame.position().top
            ;

            // get all menu doc coords
            const allMenuDocObjArray = getMenuItemsParam(menuDocs);

            let menuDoc = null,
                placeStatus = null
            ;

            $.each(allMenuDocObjArray, (obj, param) => {
	            if (frameTop > param.top && frameTop < ((param.bottom + param.top) / 2)) {
		            menuDoc = getMenuDocByObjId(obj);
		            placeStatus = true;
		            insertMenuCopyFrame(menuDoc, placeStatus, frameTop);
	            } else if (frameTop > ((param.bottom + param.top) / 2) && frameTop < param.bottom) {
		            menuDoc = getMenuDocByObjId(obj);
		            placeStatus = false;
		            insertMenuCopyFrame(menuDoc, placeStatus, frameTop);
	            }
            });

            // highlightingMenuDoc
            if (placeStatus !== null) {
                highlightMenuDoc();
            }
        }

        function moveFrame(event) {
            const $frame = $(".imcms-menu-items--frame");
            const $menuEditor = $(".imcms-menu-editor");
            mouseCoords.newPageX = event.clientX;
            mouseCoords.newPageY = event.clientY;

            mouseCoords.deltaPageX = mouseCoords.newPageX - mouseCoords.pageX;
            mouseCoords.deltaPageY = mouseCoords.newPageY - mouseCoords.pageY;

            if (isMouseDown) {
                $frame.css({
                    'top': isStandaloneEditor() ? mouseCoords.newPageY - parseInt($menuEditor.css("top"), 10) : mouseCoords.newPageY,
                    'left':  mouseCoords.newPageX
                });

                if (Math.abs(mouseCoords.deltaPageX) > 7 || Math.abs(mouseCoords.deltaPageY) > 7) {
                    const frameTop = $frame.position().top;
                    const $menuEditorBody = $('.imcms-menu-editor__body')

                    if (detectTargetArea(event)) {
                        detectPasteArea($frame);
                    } else if (frameTop < get$menuItemsList().offset().top) {
                        get$menuItemsList().scrollTop(get$menuItemsList().scrollTop() - 20);

                        const $origin = $(".imcms-menu-items--is-drag").clone(true)
                        removedPreviousItemFrame();

                        $origin.removeClass("imcms-menu-items--is-drag").addClass("imcms-menu-items--is-drop");
                        $origin.removeClass("imcms-document-items-list__document-items")
                            .addClass("imcms-document-items-list__document-items");

                        const menuDoc = getFirstItemInMenuArea();
                        $origin.attr("data-menu-items-lvl", 1);
                        $origin.insertBefore(menuDoc);
                        highlightMenuDoc();
                    } else if (frameTop > $menuEditorBody.outerHeight(true)) {
                        get$menuItemsList().scrollTop(get$menuItemsList().scrollTop() + 20);
                        const $origin = $(".imcms-menu-items--is-drag").clone(true)
                        removedPreviousItemFrame();

                        $origin.removeClass("imcms-menu-items--is-drag").addClass("imcms-menu-items--is-drop");
                        $origin.removeClass("imcms-document-items-list__document-items")
                            .addClass("imcms-document-items-list__document-items");

                        const menuDoc = getLastParentItemInMenuArea();
                        $origin.attr("data-menu-items-lvl", 1);
                        $origin.insertAfter(menuDoc);
                        highlightMenuDoc();
                    }
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
                originItemLvl = parseInt($originItem.attr("data-menu-items-lvl")),
	            $menuEditorArea = $(".imcms-menu-editor")
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
            $frameLayout.appendTo($menuEditorArea);

            $originItem.addClass("imcms-menu-items--is-drag");

            closeSubItems($originItem);

            mouseCoords = {
                pageX: event.clientX,
                pageY: event.clientY,
                top: $originItem.position().top,
                left: $originItem.position().left
            };
            $menuArea = get$menuItemsList();
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
                "top": isStandaloneEditor() ? event.clientY - parseInt($menuEditorArea.css("top"), 10) : event.clientY,
                "left": event.clientX
            });

            $frame.addClass("imcms-document-items-list__document-items");
            $frame.addClass("imcms-menu-items--frame");

            $frame.appendTo($menuEditorArea);

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
	                createdBy: $dataInput.attr("data-createdBy"),
	                createdDate: $dataInput.attr("data-createdDate"),
                    publishedDate: $dataInput.attr('data-publishedDate'),
                    modifiedDate: $dataInput.attr('data-modifiedDate'),
                    hasNewerVersion: $dataInput.attr('data-current-version') === '0', //simple convert in boolean. if not working version - not exist newer ver
                    linkableForUnauthorizedUsers: $dataInput.attr('data-linkableForUnauthorizedUsers') === "true",
                    linkableByOtherUsers: $dataInput.attr('data-linkableByOtherUsers') === "true"
                },
                level = ($dataInput.attr("data-parent-id") !== "")
                    ? parseInt($menuElementsContainer.find("[data-document-id=" + parentId + "]").attr("data-menu-items-lvl"))
                    : 1;
            let $menuElement
            ;

            $menuElement = buildMenuItemTree(menuElementsTree, {
                level,
                sortType: $dataInput.attr("data-type-sort")
            });
            $menuElementsContainer.find(".imcms-menu-items-list").append($menuElement);
            $menuElement.addClass("imcms-document-items-list__document-items");
            $menuElement.addClass("imcms-doc-item-copy");
            let doc = documentEditorBuilder.getDocumentById($menuElement.attr('data-document-id'));
            documentEditorBuilder.refreshDocumentInList(doc, isDocumentItem(doc));
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
	            createdBy: document.createdBy,
	            createdDate: `${document.created.date} ${document.created.time}`,
                publishedDate: `${document.published.date} ${document.published.time}`,
                modifiedDate: `${document.modified.date} ${document.modified.time}`,
                linkableForUnauthorizedUsers: document.linkableForUnauthorizedUsers,
                linkableByOtherUsers: document.linkableByOtherUsers
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

            getFirstItemInMenuArea().before(buildMenuItemTree(getMenuElementTree(doc), {level: 1, sortType}));

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
                        if (commonContent.enabled && commonContent.language.code === imcms.language.code && commonContent.headline) {
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
                    const statusTexts = docStatus.getDocumentStatusTexts(document.documentStatus, `${document.published.date} ${document.published.time}`);
                    $status.text(statusTexts.title);
                    components.overlays.changeTooltipText($status, statusTexts.tooltip);
                }

                function changeStar() {
                    const $star = $oldMenuItem.find(".imcms-control--star").first();

                    if(document.currentVersion.id === WORKING_VERSION){
                        $star.css('filter', '');
                    }else{
                        $star.css('filter', 'grayscale(100%) brightness(140%)');
                    }
                }

                function toggleClass() {
                    const menuItemClass = "imcms-document-items__document-item";
                    const $menuItem = $oldMenuItem.find("." + menuItemClass).first();

                    $menuItem.removeClass((index, className) => (className.match(/imcms-document-items__document-item--\S+/g) || []).join(' '));

                    $menuItem.addClass(
                        menuItemClass + "--" + document.documentStatus.replace(/_/g, "-").toLowerCase()
                    );
                }

                changeTitle();
                changeStatus();
                changeStar();
                toggleClass();

                documentEditorBuilder.updateDocumentInList(document);
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

            const controls = [];
            controls.push($controlRemove);
            if(imcms.accessToDocumentEditor || imcms.isSuperAdmin) controls.push($controlCopy);
            controls.push($controlEdit);

            return enabledMultiRemoveMode
                ? components.controls.buildControlsBlock("<div>", controls, {
                    'class': multiRemoveControlClass
                })
                : components.controls.buildControlsBlock("<div>", controls);
        }

        function showHideSubmenu() {
            const $btn = $(this);
	        const time = isMouseDown ? 0 : 400;
            let level = $btn.parents(".imcms-menu-items").attr("data-menu-items-lvl")
            ;

            level = parseInt(level) + 1;
            const submenus = $btn.closest(".imcms-menu-items")
                .find(".imcms-menu-items[data-menu-items-lvl=" + level + "]");
            if (!submenus.is(":animated")) {
                submenus.each(function () {
                    $(this).slideToggle(time);
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
            $numberingTypeSortFlag.isChecked() ? $moveControl.css("display", "none") : $moveControl.css("display", "block");

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

							if (menuItem1.sortOrder.length===menuItem2.sortOrder) {
								return parsedValItem1[parsedValItem1.length - 1] - parsedValItem2[parsedValItem2.length - 1];
							}
						})
                    }
                    return item;
                });
        }

        function reorderMenuListBySortNumber(menuItems, menuItemId, highlight) {
            const currentTypeSort = document.getElementById('type-sort').value.trim();
            getDeepSortedItemsBySortNumber(menuItems);

            const mappedMenuItems = menuItems.map(menuItem => mapToMenuItemWithAllFields(menuItem));

            $menuElementsContainer.find('.imcms-menu-list').remove();
            const $menuItemsSortedList = buildMenuEditorContent(mappedMenuItems, currentTypeSort);
            $menuElementsContainer.append($menuItemsSortedList);

			if (highlight) {
				const $changedItem = getMenuDocByObjId(menuItemId);
				if ($changedItem) {
					$changedItem.addClass('imcms-border-light');

					setTimeout(function () {
						$changedItem.removeClass('imcms-border-light');
					}, 5000);
				}
			}
        }

        function findPlaceMenuElement(menuItems, index) {
            const indexArray = parseIndex(index);
            return indexArray.reduce((acc, value, i) => {
                if (indexArray.length - 1 === i) {
                    return acc;
                }
                if (acc[value]) {
                    return acc[value].children;
                }
                throw new Error(texts.error.invalidSortNumber);
            }, menuItems);
        }

        function parseIndex(index) {
            return index.split('.').map(n => Math.max(parseInt(n) - 1, 0));
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
            const $numberingSortBox = components.texts.textBox('<div>', {
                class: 'imcms-flex--m-auto',
                value: menuElement.sortOrder,
                id: menuElement.sortOrder,
                name: menuElement.documentId
            });

	        $numberingSortBox.$input.blur(onInputBlur).keypress(onInputEnterPressed);

	        (sortType === MANUAL || sortType === TREE_SORT) ? $numberingTypeSortFlag.show() : $numberingTypeSortFlag.hide();
            $numberingTypeSortFlag.isChecked() ? $numberingSortBox.show() : $numberingSortBox.hide();

            const $docId = components.texts.titleText('<a>', menuElement.documentId, {
                href: '/' + menuElement.documentId,
                target: '_blank',
	            style: 'font-weight:normal',
            });
            $docId.modifiers = ['id'];
            components.overlays.defaultTooltip(
                $docId,
                documentEditorBuilder.getIdTooltipText(menuElement.documentId, menuElement.createdDate, menuElement.createdBy),
                {placement: 'right'},
            );

            const title = menuElement.title
                ? menuElement.title
                : documentBuilderTexts.notShownInSelectedLang;
            const $titleText = components.texts.titleText('<a>', title, {
                href: "/" + menuElement.documentId,
	            style: 'font-weight:normal',
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

            const documentStatusTexts = docStatus.getDocumentStatusTexts(menuElement.documentStatus, menuElement.publishedDate);
            const $documentStatus = components.texts.titleText("<div>", documentStatusTexts.title, {
                class: 'imcms-grid-col-13'
            });
            $documentStatus.modifiers = ['status'];
            components.overlays.defaultTooltip($documentStatus, documentStatusTexts.tooltip);

	        prepareLinkToBeDraggable($docId);
	        prepareLinkToBeDraggable($titleText);

	        const $draggableDocId = $('<div>').append($docId).addClass('imcms-grid-col-1');
	        const $draggableTitleText = $('<div>').append($titleText).addClass("imcms-flex--flex-1");

	        const elements = [$draggableDocId, $draggableTitleText];
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
            }).on({
	            "mousedown": function (e) {
		            if (!$numberingTypeSortFlag.isChecked() && sortType === TREE_SORT && isMenuElementAllowedToDragSort(e.target)){
			            const container = $(this).parents()[1];
			            sortByDragging(container, true);
		            }
	            },
	            "mouseover": function () {
		            $(this).css("cursor", (!$numberingTypeSortFlag.isChecked() && sortType === TREE_SORT) ? "pointer" : "default");
	            }
            });
        }

	    function prepareLinkToBeDraggable($linkElement) {
		    $linkElement.on({
			    "mousedown": function (downEvent) {
				    const $this = $(this);
				    const container = $this.parents()[3];
				    sortByDragging(container, true);

				    $this.on("mousemove", function (moveEvent) {
					    $this.on("dragstart", function (dragEvent) {
						    isMouseDown = true;
						    closeSubItems($($this.parents()[2]))
					    })
				    })

				    $this.on("drag", function (dragEvent) {
					    dragEvent.target = $this.parents()[2];
					    handleDragWhenSort(dragEvent)
				    })
			    }
		    })
	    }

	    function onInputBlur(events) {
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

		    const typeSort = document.getElementById('type-sort').value;
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

		    if (typeSort && typeSort !== TREE_SORT && currentValue.includes('.')) {
			    alert(`${typeSort} sort doesn't support nesting!!`)
			    $(events.target).val(currentIndex)
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
            placementElement.splice(parsedValue.pop(), 0, splicedElem);

		    reorderMenuListBySortNumber(items, documentId, true);
	    }

	    function onInputEnterPressed(e) {
		    if (e.which === 13)
			    onInputBlur(e)
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
					class:'imcms-menu-list-titles__title--sort-order',
                    text: texts.order,
                    click: clickReOrderMenuItemsBySortNumbers,
                });
                $sortOrderColumnHead.modifiers = ["sort-order"];
                $numberingTypeSortFlag.isChecked() ? $sortOrderColumnHead.show() : $sortOrderColumnHead.hide();
                $removeButton = components.buttons.errorButton({
	                class:'imcms-menu-list-titles__title--remove',
                    text: texts.multiRemove,
                    click: removeEnabledMenuItems
                });

                isMultiRemoveModeEnabled() ? $removeButton.css('visibility', '') : $removeButton.css('visibility', 'hidden');

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
            const menuDocs = get$menuItemsList().find(".imcms-menu-items");
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

                if (isMultiRemoveModeEnabled()) {
                    $switchButton.removeClass(classButtonOn).addClass(classButtonOff);
                    $removeButton.css('visibility', 'hidden');
                } else if ($switchButton.hasClass(classButtonOff)) {
                    $switchButton.removeClass(classButtonOff).addClass(classButtonOn);
                    $removeButton.css('visibility', '');
                }

                changeControlsByMultiRemove();
            }

            return new BEM({
                block: 'imcms-switch-block',
                elements: {
                    'active-info': components.texts.infoText('<div>', texts.multiRemoveInfo),
                    'button': components.buttons.switchOffButton({
                        click: switchButtonAction
                    }),
                }
            }).buildBlockStructure('<div>');
        }


        function removeEnabledMenuItems() {
            const menuDocs = get$menuItemsList().find(".imcms-menu-items");

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
            const menuDocs = get$menuItemsList().find(".imcms-menu-items");

            const toggleCommand = isChecked
                ? $element => $element.show()
                : $element => $element.hide();

            menuDocs.each(function () {
                const $item = $(this).find('.imcms-document-item__sort-order');
                toggleCommand($item);
            });
        }

        function toggleMenuTitlesIndent(isChecked) {
            isChecked ? $sortOrderColumnHead.show() : $sortOrderColumnHead.hide();
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
            const lastSavedTypeSort = opts.typeSort;

            $typesSortSelect = components.selects.selectContainer('<div>', {
                id: 'type-sort',
                emptySelect: false,
                onSelected: buildOnSelectedTypeSort
            });

            $numberingTypeSortFlag = components.checkboxes.imcmsCheckbox('<div>', {
                text: texts.sortNumberTitle,
                checked: 'checked',
                change: onChangeNumberingSortFlag,
            });

            mapTypesSort.clear();
            originalTypes.map((typeOriginal, index) => {
                mapTypesSort.set(localizeTypesSort[index], typeOriginal)
            });

            let keys = [...mapTypesSort.keys()];

            let typesSortDataMapped = keys.map(typeKey => ({
                text: typeKey,
                'data-value': mapTypesSort.get(typeKey),
                'class': lastSavedTypeSort === mapTypesSort.get(typeKey) && 'imcms-last-saved-mode'
            }));

            components.selects.addOptionsToSelect(typesSortDataMapped, $typesSortSelect.getSelect(), buildOnSelectedTypeSort(opts));

            return new BEM({
                block: 'imcms-menu-sort',
                elements: {
                    'type-sort': $typesSortSelect,
                    'type-sort-numbering': $numberingTypeSortFlag
                }
            }).buildBlockStructure('<div>', {
            });
        }

        let prevType;

        function buildMenuItemsBySelectedType(menuData) {
            menusRestApi.getSortedItems(menuData).done(menuItems => {
                prevType = menuData.typeSort;
                rebuildAllMenuItemsInMenuContainer(menuItems, menuData.typeSort);
            }).fail(() => modal.buildErrorWindow(texts.error.loadFailed));
        }

        function rebuildAllMenuItemsInMenuContainer(menuItems, typeSort) {
            $menuElementsContainer.find('.imcms-menu-list').remove();
            let $menuItemsSortedList = buildMenuEditorContent(menuItems, typeSort);
            $menuElementsContainer.append($menuItemsSortedList);
        }

        function addHighlightToSelectItemByLastSavedType(currentSelectedVal, lastSavedType) {
            const $currentSelectedVal = $('.imcms-menu-sort__type-sort .imcms-drop-down-list__select-item-value');
            if (currentSelectedVal === lastSavedType && !$currentSelectedVal.hasClass('imcms-last-saved-mode')) {
                $currentSelectedVal.addClass('imcms-last-saved-mode');
            } else {
                $currentSelectedVal.removeClass('imcms-last-saved-mode');
            }

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
                    typeSort: type
                };

                function getKeyByValue(mapObjects, prevType) {
                    for (let [key, value] of mapObjects.entries()) {
                        if (value === prevType) {
                            return key;
                        }
                    }
                }

                addHighlightToSelectItemByLastSavedType(type, opts.typeSort);

                if (prevType === TREE_SORT && type !== TREE_SORT) {
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
	            if (isMultiRemoveModeEnabled())
		            $('.imcms-switch-block__button').click();
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
            const typeSort = opts.typeSort;
            prevType = typeSort;
            $menuElementsContainer.append(buildEditorContainer(opts));

            addHighlightToSelectItemByLastSavedType($('#type-sort').val(), typeSort);

            const $menuElementsTree = buildMenuEditorContent(menuElementsTree, typeSort);
            $menuElementsContainer.append($menuElementsTree);

            documentEditorBuilder.initMenuEditor(menuElementsTree.map(doc => doc.documentId));

            $documentEditor = documentEditorBuilder.buildBody(opts);
            $documentsContainer.append($documentEditor);
            documentEditorBuilder.loadDocumentEditorContent($documentEditor, {...opts, moveEnable: true});
        }

        function loadMenuEditorContent(opts) {
            addHeadData(opts);
            menusRestApi.read(opts)
                .done(menu => {
                    opts.inMenu = true;
                    opts.typeSort = menu.typeSort
                    fillEditorContent(menu.menuItems, opts);
                })
                .fail(() => modal.buildErrorWindow(texts.error.loadFailed));
        }

        function addHeadData(opts) {
            let linkData = "/api/admin/menu?meta-id="
                + opts.docId
                + "&index=" + opts.menuIndex;

            $title.text(linkData).css({
                'text-transform': 'lowercase',
                'color': '#fff2f9',
	            'overflow':'hidden',
	            'white-space':'nowrap',
	            'text-overflow':'ellipsis'
            });

            $title.attr('href', linkData)
        }

        function isStandaloneEditor(){
            return $(".standalone-editor-body").length !== 0;
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
