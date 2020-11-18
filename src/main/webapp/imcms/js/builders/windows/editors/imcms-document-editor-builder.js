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

        const classButtonOn = "imcms-button--switch-on";
        const classButtonOff = "imcms-button--switch-off";
        const rightPaddingNoneClassName = 'imcms-flex--pr-0';
        const multiRemoveControlClass = 'imcms-document-item__multi-remove-controls';
        let TREE_SORT = 'TREE_SORT';
        let WORKING_VERSION = 0;
        let topPointMenu = 178; // top point menu for set item before item in the top position. Improve it if you can

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

        const defaultSortingAttribute = 'default-sorting';

        const searchQueryObj = {
            "term": "",
            "userId": null,
            "categoriesId": {},
            "page.skip": currentDocumentNumber
        };

        let sendSearchDocRequest = true;
        let errorMsg;

        const sortAscClassName = "imcms-control--sort-asc";
        const sortDescClassName = "imcms-control--sort-desc";
        const sortAscClass = "." + sortAscClassName;
        const sortDescClass = "." + sortDescClassName;

        const menuItemsListSelector = '.imcms-menu-items-list';
        const menuItemsSelector = '.imcms-menu-items';

        function get$menuItemsList() {
            return $(menuItemsListSelector);
        }

        function get$menuItems() {
            return $(menuItemsSelector);
        }

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
                setDefaultSortingIcons();
            }

            docSearchRestApi.read(searchQueryObj)
                .done(documentList => {
                    if (!documentList || (documentList.length === 0)) {
                        sendSearchDocRequest = false;
                        errorMsg.slideDown();
                        return;
                    } else {
                        errorMsg.slideUp();
                    }
                    incrementDocumentNumber(documentList.length);
                    $documentsList.empty();
                    documentList.forEach(document => {
                        $documentsList.append(buildDocument(document, currentEditorOptions, false));
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
            const $document = buildDocument(document, currentEditorOptions, true);
            $documentsList.prepend($document);
            docs.push(document);
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

            function buildLoadCopyAnimation() {
                return new BEM({
                    block: 'animation-copying',
                    elements: {
                        'text': $('<div>').text(texts.controls.copy.action).addClass('imcms-label'),
                        'load': $('<div>').addClass('loading-animation')
                    }
                }).buildBlockStructure("<div>");
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

            const $loadingAnimation = toolBEM.buildBlock('<div>', [{'load': buildLoadCopyAnimation()}]);
            $loadingAnimation.modifiers = ['grid-col-1'];

            const $multiRemoveDocs = toolBEM.buildBlock('<div>', [{'remove': buildSwitchesOffOnButtons()}])
            $multiRemoveDocs.modifiers = ['grid-col-19'];

            return new BEM({
                block: "imcms-document-editor-head-tools",
                elements: {
                    "tool": [
                        $newDocButtonContainer,
                        $searchContainer,
                        $usersFilter,
                        $categoriesFilter,
                        $loadingAnimation,
                        $multiRemoveDocs
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
            const $defaultSortingHeader = $(".imcms-document-editor-body .imcms-document-list-titles__title").first();
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
            $sortingIcon.hasClass(sortDescClassName)
                ? $sortingIcon.removeClass(sortDescClassName).addClass(sortAscClassName)
                : $sortingIcon.removeClass(sortAscClassName).addClass(sortDescClassName);
        }

        function setDefaultSortingIcons() {
            $(".imcms-document-list-titles__title " + sortDescClass)
                .removeClass(sortDescClassName)
                .addClass(sortAscClassName);

            $(`.imcms-document-list-titles__title[${defaultSortingAttribute}] ${sortAscClass}`)
                .removeClass(sortAscClassName)
                .addClass(sortDescClassName);
        }

        function discardPreviousSortingIcon() {
            $(".imcms-document-list__titles").find(sortDescClass)
                .removeClass(sortDescClassName)
                .addClass(sortAscClassName);
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
                setDefaultSortingIcons();
            }
        }

        function onClickSorting(bySorting, $sortingHeader) {
            if (isAlreadySortedBy(bySorting)) {
                processSameSorting(bySorting, $sortingHeader);

            } else {
                setSortBy(bySorting);
                discardPreviousSortingIcon();
                highlightSorting($sortingHeader);
            }

            appendDocuments(sortProperty, searchQueryObj[sortProperty], true, false);
        }

        function removeEnabledMenuItems() {
            const $documents = $('.imcms-document-items-list').find('.imcms-document-items');
            const docIds = [];

            $documents.each(function () {
                const $doc = $(this).first();

                if (isActiveCheckBoxMultiRemoveDocuments($doc)) {
                    docIds.push(parseInt($doc.attr('data-doc-id')));
                }
            });

            removeDocuments(docIds);

            function isActiveCheckBoxMultiRemoveDocuments($doc) {
                return $doc.find('.imcms-document-item__multi-remove-controls').children()[0].firstChild.checked;
            }
        }

        function buildDocumentListTitlesRow() {
            const $idColumnHead = buildTitleRow({
                text: texts.sort.id,
                bySorting: defaultSortPropertyValue,
                elementClass: "imcms-grid-col-18",
                modifiers: ["id"],
            });

            const $titleColumnHead = buildTitleRow({
                text: texts.sort.title,
                bySorting: "meta_headline_" + imcms.language.code,
                elementClass: "imcms-flex--flex-3",
            });

            const $aliasColumnHead = buildTitleRow({
                text: texts.sort.alias,
                bySorting: "alias",
                elementClass: "imcms-flex--flex-2",
            });

            const $modifiedColumnHead = buildTitleRow({
                text: texts.sort.modified,
                bySorting: "modified_datetime",
                elementClass: "imcms-grid-col-17",
                modifiers: ['date'],
            });

            const $publishedColumnHead = buildTitleRow({
                text: texts.sort.published,
                bySorting: "publication_start_datetime",
                elementClass: "imcms-grid-col-17",
                modifiers: ['date'],
            });

            const $versionColumnHead = buildTitleRow({
                text: texts.sort.version,
                bySorting: 'version_no',
                elementClass: 'imcms-grid-col-18',
                modifiers: ['currentVersion'],
            });

            const $typeColumnHead = buildTitleRow({
                text: texts.sort.type,
                elementClass: "imcms-grid-col-18",
                modifiers: ['type'],
            });

            const $statusColumnHead = buildTitleRow({
                text: texts.sort.status,
                elementClass: 'imcms-grid-col-1',
                modifiers: ['status'],
            });

            $removeButton = components.buttons.positiveButton({
                text: texts.controls.removeButton,
                click: removeEnabledMenuItems,
                style: 'display:none;'
            });

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
                        $statusColumnHead,
                        $removeButton
                    ]
                }
            }).buildBlockStructure("<div>");
        }

        function buildTitleRow({text, bySorting, modifiers, elementClass}) {
            const $emptyIcon = $('<div>');

            const $sortIcon = bySorting
                ? bySorting === defaultSortPropertyValue ? components.controls.sortDesc() : components.controls.sortAsc()
                : $emptyIcon;

            const $titleRow = $("<div>", {
                text: text,
            });

            const $titleRowBem = new BEM({
                block: "imcms-document-list-title-row",
                elements: {
                    "title": $titleRow,
                    "icon": $sortIcon,
                }
            }).buildBlockStructure("<div>", {
                class: elementClass,
                click: function () {
                    if (bySorting) {
                        onClickSorting(bySorting, $(this))
                    }
                },
            });
            $titleRowBem.modifiers = modifiers;

            if (bySorting === defaultSortPropertyValue) {
                $titleRowBem.attr(defaultSortingAttribute, '');
            }

            return $titleRowBem;
        }

        function createFrame(event) {
            const $this = $(this),
                original = $this.closest(".imcms-document-items"),
                $frame = original.clone(),
                $frameLayout = $("<div>"),
                frameItem = $frame.find(".imcms-document-item")
            ;

            $menuArea = get$menuItemsList();
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

            frameItem.attr("data-id", frameItem.find(".imcms-document-item__info--id").text());
            frameItem.attr("data-title", frameItem.find(".imcms-document-item__info--title").text());
            frameItem.attr("data-is-shown-title", !frameItem.find(".imcms-document-item__info--notShownTitle").length);
            frameItem.attr("data-publishedDate", frameItem.find(".imcms-document-item__info--publishedDate").text());
            frameItem.attr("data-modifiedDate", frameItem.find(".imcms-document-item__info--modifiedDate").text());
            frameItem.attr("data-type", frameItem.find(".imcms-document-item__info--type").text());
            frameItem.attr("data-status", frameItem.find(".imcms-document-item__info--status").text());
            frameItem.attr("data-original-status", frameItem.find(".imcms-document-item__info--originalStatus").text());
            frameItem.attr("data-current-version", frameItem.find(".imcms-document-item__info--currentVersion").children().attr('value'));

            $frame.addClass("imcms-document-items--frame");
            $frame.css({
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

        function refreshDocumentInList(document, savedFlag) {
            if ($documentsList === undefined || null === document) return;
            const $oldDocumentElement = $documentsList.find("[data-doc-id=" + document.id + "]");

            if ($oldDocumentElement.length === 1) {
                const $newDocumentElement = buildDocument(document, currentEditorOptions, savedFlag);
                $oldDocumentElement.replaceWith($newDocumentElement);
            }
        }


        function isMultiRemoveModeEnabled() {
            return $('.imcms-remove-switch-block__button').hasClass(classButtonOn);
        }

        function changeControlsByMultiRemove() {
            const $documents = $('.imcms-document-items-list').find('.imcms-document-items');
            const isEnabledMultiRemove = isMultiRemoveModeEnabled();

            const opts = { // i am not sure about this hard code ..
                copyEnable: true,
                editEnable: true
            }

            $documents.each(function () {
                const $doc = $(this).first();
                const documentId = $doc.attr('data-doc-id');
                const document = getDocumentById(documentId);
                $doc.find(".imcms-controls")
                    .replaceWith(buildDocItemControls(document, opts, isEnabledMultiRemove));
            });
        }

        let $removeButton;

        function buildSwitchesOffOnButtons() {

            function switchButtonAction() {
                const $switchButton = $('.imcms-remove-switch-block__button');
                const $switchActiveInfoBlock = $('.imcms-remove-switch-block__active-info');

                if (isMultiRemoveModeEnabled()) {
                    $switchButton.removeClass(classButtonOn).addClass(classButtonOff);
                    $switchActiveInfoBlock.text(texts.controls.multiRemoveInfoOff);
                    $removeButton.css('display', 'none');
                    // $menuTitlesBlock.removeClass(rightPaddingNoneClassName);
                } else if ($switchButton.hasClass(classButtonOff)) {
                    $switchButton.removeClass(classButtonOff).addClass(classButtonOn);
                    $switchActiveInfoBlock.text(texts.controls.multiRemoveInfoOn);
                    $removeButton.css('display', 'block');
                    // $menuTitlesBlock.addClass(rightPaddingNoneClassName);
                }

                changeControlsByMultiRemove();
            }

            return new BEM({
                block: 'imcms-remove-switch-block',
                elements: {
                    'active-info': components.texts.infoText('<div>', texts.controls.multiRemoveInfoOff),
                    'button': components.buttons.switchOffButton({
                        click: switchButtonAction
                    }),
                }
            }).buildBlockStructure('<div>');
        }

        function buildRemoveCheckBoxes() {
            return components.checkboxes.imcmsCheckbox('<div>', {
                change: changeValueCheckBox
            });
        }


        function changeValueCheckBox() {
            const $this = $(this);
            const newVal = $this.is(":checked");
            $this.val(newVal);
        }

        function buildDocItemControls(document, opts, isEnableMultiRemove) {
            const controls = [];
            opts = opts || {};
            const $multiRemoveBoxControl = buildRemoveCheckBoxes();
            $multiRemoveBoxControl.modifiers = ['multi-remove'];

            if (opts.removeEnable) {
                const $controlRemove = components.controls.remove(function () {
                    removeDocument.call(this, document);
                });
                controls.push($controlRemove);
            }

            if (isEnableMultiRemove) controls.push($multiRemoveBoxControl);

            if (opts.copyEnable) {
                function onConfirm() {
                    const $animationBlock = $('.imcms-document-editor-head-tool__load');
                    $animationBlock.css('display', 'inline-table');
                    docCopyRestApi.copy(document.id)
                        .done(copiedDocument => {
                            addDocumentToList(copiedDocument);
                            $animationBlock.css('display', 'none');
                        })
                        .fail(() => modal.buildErrorWindow(texts.error.copyDocumentFailed));
                }

                const $controlCopy = components.controls.copy(() => {
                    modal.buildConfirmWindow(
                        texts.controls.copy.confirmMessage + document.id + '?',
                        onConfirm
                    );
                });
                components.overlays.defaultTooltip($controlCopy, texts.controls.copy.title, {placement: 'left'});
                controls.push($controlCopy);
            }

            if (opts.editEnable) {
                const $controlEdit = components.controls.edit(() => {
                    pageInfoBuilder.build(document.id, refreshDocumentInList, document.type);
                });
                components.overlays.defaultTooltip($controlEdit, texts.controls.edit.title, {placement: 'left'});
                controls.push($controlEdit);
            }

            return isEnableMultiRemove
                ? components.controls.buildControlsBlock("<div>", controls, {
                    'class': multiRemoveControlClass
                })
                : components.controls.buildControlsBlock("<div>", controls, {
                    'class': 'imcms-document-item__controls'
                });
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
            get$menuItems().each(function () {
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
            const menuDocs = get$menuItemsList().find(menuItemsSelector);
            let menuDoc = null;

            menuDocs.each(function () {
                if ($(this).attr("data-document-id") === obj) {
                    menuDoc = $(this)
                }
            });

            return menuDoc;
        }

        function disableHighlightingMenuDoc() {
            get$menuItemsList().find(menuItemsSelector).css({
                "border": "none"
            });
        }

        function removedPreviousItemFrame() {
            const $menuTree = get$menuItemsList(),
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

            if (!showHidBtn.hasClass("imcms-document-item__btn--open")) {
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
            function highlightMenuDoc() {
                disableHighlightingMenuDoc();
                get$menuItemsList().find(".imcms-doc-item-copy").css({
                    'border': '1px dashed red'
                });
            }

            $.each(allMenuDocObjArray, (obj, param) => {
                if (frameTop > param.top && frameTop < ((param.bottom + param.top) / 2)) {
                    menuDoc = getMenuDocByObjId(obj);
                    placeStatus = false;
                    createMenuItemFrame(menuDoc, placeStatus, frameTop);
                }
                if (frameTop > ((param.bottom + param.top) / 2) && frameTop < param.bottom) {
                    menuDoc = getMenuDocByObjId(obj);
                    placeStatus = true;
                    createMenuItemFrame(menuDoc, placeStatus, frameTop);
                }
            });

            if (frameTop < topPointMenu) {
                menuDoc = getFirstItemInMenuArea();
                placeStatus = false;
                createMenuItemFrame(menuDoc, placeStatus, frameTop);
            }

            // highlightingMenuDoc
            if (placeStatus !== null) {
                highlightMenuDoc();
            } else {
                disableHighlightingMenuDoc();
            }

            return {
                parent: menuDoc,
                status: placeStatus,
                frameTopPos: frameTop
            }
        }

        function getFirstItemInMenuArea() {
            const menuDocs = get$menuItemsList().find(menuItemsSelector);
            return menuDocs.first();
        }

        function getDocumentParent() {
            const allMenuDocObjArray = {},
                itemTree = get$menuItemsList(),
                menuDocs = itemTree.find(".imcms-menu-item"),
                $frame = $(".imcms-document-items--frame"),
                frameTop = $frame.offset().top
            ;
            if (menuDocs.length === 0) {
                return checkFramePositioning(allMenuDocObjArray, frameTop)
            }

            // get all menu doc coords
            menuDocs.each(function () {
                allMenuDocObjArray[$(this).closest(menuItemsSelector).attr("data-document-id")] = {
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
            } else {
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
            dataInput.attr("data-is-shown-title", frameItem.attr("data-is-shown-title"));
            dataInput.attr("data-frame-top", insertedParent.frameTopPos);
            dataInput.attr('data-current-version', frameItem.attr('data-current-version'))
            dataInput.attr("data-title", frameItem.attr("data-title")).trigger("change");
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
                const $menuItemsList = get$menuItemsList();

                if ($menuItemsList.find("[data-document-id=" + frameItem.attr("data-id") + "]").length === 0) {
                    insertedParent = getDocumentParent();
                    setDataInputParams(insertedParent, frameItem);
                }

                $menuArea.css({
                    "border-color": "transparent"
                });
                $menuItemsList.find(".imcms-doc-item-copy").removeClass("imcms-doc-item-copy");
                disableHighlightingMenuDoc();
            }

            toggleUserSelect(false);

            $frame.remove();
            isMouseDown = false;
            const $sortOrder = $('.imcms-document-list-titles__title--sort-order');
            $sortOrder.click();
        });

        function getDocumentStatusTexts(documentStatus, publishedDate) {
            return {
                PUBLISHED: {
                    title: texts.status.title.published,
                    tooltip: texts.status.tooltip.published + ' ' + publishedDate,
                },
                PUBLISHED_WAITING: {
                    title: texts.status.title.publishedWaiting,
                    tooltip: texts.status.tooltip.publishedWaiting,
                },
                IN_PROCESS: {
                    title: texts.status.title.inProcess,
                    tooltip: texts.status.tooltip.inProcess,
                },
                DISAPPROVED: {
                    title: texts.status.title.disapproved,
                    tooltip: texts.status.tooltip.disapproved,
                },
                ARCHIVED: {
                    title: texts.status.archived,
                    tooltip: texts.status.archived,
                },
                PASSED: {
                    title: texts.status.passed,
                    tooltip: texts.status.passed,
                },
            }[documentStatus];
        }

        function getDocumentVersionTexts(hasNewerVersion) {
            return hasNewerVersion
                ? {tooltip: texts.version.tooltip.hasNewerVersion}
                : {tooltip: texts.version.tooltip.noWorkingVersion}
        }

        function getIdTooltipText(id, date, user) {
            return `${id}. ${texts.id.tooltip.createdOn} ${date} ${texts.by} ${user}`
        }

        function getModifiedDateTooltipText(date, user) {
            return `${texts.modified.tooltip.lastChangedOn} ${date} ${texts.by} ${user}`
        }

        function getPublishedDateTooltipText(date, user) {
            return `${texts.published.tooltip.publishedOn} ${date} ${texts.by} ${user}`
        }

        /** @namespace document.documentStatus */
        function buildDocItem(document, opts, savedFlag) {

            const $docItemId = components.texts.titleText("<a>", document.id, {
                href: "/" + document.id,
                class: "imcms-grid-col-18",
            });
            $docItemId.modifiers = ["id"];
            components.overlays.defaultTooltip(
                $docItemId,
                getIdTooltipText(document.id, document.created, document.createdBy),
                {placement: 'right'}
            );

            let title;
            if (savedFlag) {
                const content = document.commonContents.filter(content => content.enabled)
                    .filter(enableContent => enableContent.language.code === Imcms.language.code)
                    .shift();

                title = content ? content.headline : texts.notShownInSelectedLang;

            } else {
                title = document.isShownTitle ? document.title : texts.notShownInSelectedLang;
            }
            const $docItemTitle = components.texts.titleText("<a>", title, {
                href: "/" + document.id,
                class: "imcms-flex--flex-3",
            });
            $docItemTitle.modifiers = ["title"];
            (!document.isShownTitle && undefined !== document.isShownTitle) && $docItemTitle.modifiers.push("notShownTitle");
            title && components.overlays.defaultTooltip($docItemTitle, title);

            const $docItemAlias = components.texts.titleText("<div>", document.alias && ("/" + document.alias), {
                class: "imcms-flex--flex-2",
            });
            $docItemAlias.modifiers = ["alias"];
            document.alias && components.overlays.defaultTooltip($docItemAlias, "/" + document.alias);

            let docModifiedDate;
            let docModifiedBy;
            if (savedFlag) {
                docModifiedDate = (document.modified.date && document.modified.time)
                    ? `${document.modified.date} ${document.modified.time}`
                    : "";
                docModifiedBy = document.modified.by;
            } else {
                docModifiedDate = document.modified;
                docModifiedBy = document.modifiedBy;
            }
            const $docItemModified = components.texts.titleText("<div>", docModifiedDate, {
                class: "imcms-grid-col-17",
            });
            $docItemModified.modifiers = ["date", "modifiedDate"];
            if (docModifiedDate) {
                components.overlays.defaultTooltip(
                    $docItemModified,
                    getModifiedDateTooltipText(docModifiedDate, docModifiedBy)
                );
            }

            let docPublishedDate;
            let docPublishedBy;
            if (savedFlag) {
                docPublishedDate = (document.published.date && document.published.time)
                    ? `${document.published.date} ${document.published.time}`
                    : "";
                docPublishedBy = document.published.by;
            } else {
                docPublishedDate = document.published;
                docPublishedBy = document.publishedBy;
            }
            const $docItemPublished = components.texts.titleText("<div>", docPublishedDate, {
                class: "imcms-grid-col-17",
            });
            $docItemPublished.modifiers = ["date", "publishedDate"];
            if (docPublishedDate) {
                components.overlays.defaultTooltip(
                    $docItemPublished,
                    getPublishedDateTooltipText(docPublishedDate, docPublishedBy)
                );
            }

            const currentVersionDoc = savedFlag ? document.currentVersion.id : document.currentVersion;
            const $star = components.controls.star().attr('value', currentVersionDoc);
            const $starByVersion = currentVersionDoc === WORKING_VERSION
                ? $star
                : $star.css({'filter': 'grayscale(100%) brightness(140%)'});

            const $currentVersion = $('<div>').append($starByVersion).addClass('imcms-grid-col-18');
            $currentVersion.modifiers = ['currentVersion'];
            components.overlays.defaultTooltip(
                $currentVersion,
                getDocumentVersionTexts(currentVersionDoc === WORKING_VERSION).tooltip
            );

            const $docItemType = components.texts.titleText("<div>", document.type, {
                class: "imcms-grid-col-18",
            });
            $docItemType.modifiers = ["type"];

            const docStatusTexts = getDocumentStatusTexts(document.documentStatus, docPublishedDate);
            const $docStatus = components.texts.titleText("<div>", docStatusTexts.title, {
                class: "imcms-grid-col-1",
            });
            $docStatus.modifiers = ["status"];
            components.overlays.defaultTooltip($docStatus, docStatusTexts.tooltip, {placement: 'left'});

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

        function buildDocumentItemContainer(document, opts, isUsed, savedFlag) {
            return new BEM({
                block: "imcms-document-items",
                elements: [{
                    "document-item": buildDocItem(document, opts, savedFlag),
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

        function buildDocument(document, opts, savedFlag) {
            const $documentItem = buildDocumentItemContainer(document, opts, checkByDocIdInMenuEditor(document.id), savedFlag);
            return documentsListBEM.makeBlockElement("document-items", $documentItem);
        }

        function buildDocumentList(documentList, savedFlag) {
            const $blockElements = documentList.map(document => buildDocumentItemContainer(document, currentEditorOptions, checkByDocIdInMenuEditor(document.id), savedFlag));

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
                'color': '#fff2f9'
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
                    pushDocumentsInArray(documentList);
                    const newDocsList = documentList.slice(0, 100);
                    incrementDocumentNumber(newDocsList.length);
                    $editorBody = buildEditorBody(newDocsList, opts);
                    $documentsContainer.append($editorBody);
                    highlightDefaultSorting();
                })
                .fail(() => {
                    errorMsg.slideDown();
                });
        }

        let docs = [];

        function pushDocumentsInArray(documentList) {
            const allDocIds = docs.map(doc => doc.documentId);
            documentList.forEach(document => {
                if (!allDocIds.includes(document.documentId)) {
                    docs.push(document);
                }
            });
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

        function removeDocumentsFromEditor(documentIds) {
            documentIds.forEach(id => {
                $documentsList.find("[data-doc-id=" + id + "]").remove();
            })
        }

        function removeDocuments(documentIds) {
            const question = texts.controls.question;

            imcmsModalWindowBuilder.buildModalWindow(question, function (answer) {
                if (!answer) {
                    return;
                }

                docRestApi.removeByIds(documentIds).done(() => {
                    removeDocumentsFromEditor(documentIds);
                    alert(texts.deleteInfo)
                }).fail(() => modal.buildErrorWindow(texts.error.removeDocumentFailed))

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
            buildBody,
            loadDocumentEditorContent,
            clearData,
            buildDocument,
            incrementDocumentNumber,
            addDocumentToList,
            getDocumentStatusTexts,
            getDocumentVersionTexts,
            getIdTooltipText,
            getModifiedDateTooltipText,
            getPublishedDateTooltipText,
            refreshDocumentInList,
            getDocumentById,
            build: function () {
                documentWindowBuilder.buildWindow.apply(documentWindowBuilder, arguments);
            }
        };
    }
);
