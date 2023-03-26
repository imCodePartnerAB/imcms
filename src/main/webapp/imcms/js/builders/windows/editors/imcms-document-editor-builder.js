/**
 * Created by Serhii Maksymchuk from Ubrainians for imCode
 * 14.08.17.
 */
define('imcms-document-editor-builder',
    [
        'imcms-bem-builder', 'imcms-page-info-builder', 'imcms-components-builder', 'imcms-primitives-builder',
        'imcms-documents-rest-api', 'imcms-document-basket-rest-api', 'imcms-documents-search-rest-api', 'imcms-users-rest-api',
        'imcms-categories-rest-api', 'imcms-window-builder', 'jquery', 'imcms', 'imcms-modal-window-builder',
        'imcms-document-type-select-window-builder', 'imcms-i18n-texts', 'imcms-events',
        'imcms-document-profile-select-window-builder', 'imcms-document-copy-rest-api',
        'imcms-document-status', 'imcms-modal-window-builder'
    ],
    function (BEM, pageInfoBuilder, components, primitives, docRestApi, docBasketRestApi, docSearchRestApi, usersRestApi,
              categoriesRestApi, WindowBuilder, $, imcms, imcmsModalWindowBuilder, docTypeSelectBuilder, texts, events,
              docProfileSelectBuilder, docCopyRestApi, docStatus, modal) {

        const textNone = texts.none;
        texts = texts.editors.document;

        const classButtonOn = 'imcms-button--switch-on';
        const classButtonOff = 'imcms-button--switch-off';
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

        let $documentsContainer, $editorBody, $documentsList, $actionSelect;

        let currentDocumentNumber = 0;
        const defaultPageSize = 100;

        const term = 'term';
        const userId = 'userId';
        const categoriesId = 'categoriesId';
        const sortProperty = 'page.property';
        const sortDirection = 'page.direction';
        const roleIdProperty = 'roleId';

        const pageSkip = 'page.skip';

        const defaultSortPropertyValue = 'modified_datetime'
        const asc = 'ASC';
        const desc = 'DESC';

        const defaultSortingAttribute = 'default-sorting';

        const searchQueryObj = {
            'term': '',
            'userId': null,
            'categoriesId': {},
            'page.skip': currentDocumentNumber,
            'page.size': defaultPageSize,
            'roleId': null
        };

        let sendSearchDocRequest = true;
        let errorMsg;

        const sortAscClassName = 'imcms-control--sort-asc';
        const sortDescClassName = 'imcms-control--sort-desc';
        const sortAscClass = '.' + sortAscClassName;
        const sortDescClass = '.' + sortDescClassName;

        const menuItemsListSelector = '.imcms-menu-items-list';
        const menuItemsSelector = '.imcms-menu-items';
	    const menuDocItemCopy = '.imcms-doc-item-copy';

        function get$menuItemsList() {
            return $(menuItemsListSelector);
        }

        function get$menuItems() {
            return $(menuItemsSelector);
        }

        function buildErrorBlock() {
            errorMsg = components.texts.errorText('<div>', texts.error.searchFailed, {style: 'display: none;'});
            const errorMsgContainer = $('<div>');
            errorMsgContainer.append(errorMsg);
            return errorMsgContainer;
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
                    pushDocumentsInArray(documentList);
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

        function updateDocumentInList(document) {
            let index = docs.findIndex((doc) => doc.documentId === document.id || doc.id === document.id);
            if(index) docs[index] = document;

            refreshDocumentInList(document, true);
        }

        let $textField;

        function buildBodyHeadTools(opts) {
            opts = opts ? opts : {};

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
                    text: '+',
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
                }).buildBlockStructure('<div>');
            }

            function buildSearchDocField() {

                $textField = components.texts.textField('<div>', {
                    id: 'searchText',
                    name: 'search',
                    placeholder: texts.freeTextPlaceholder,
                    text: texts.freeText
                });

                $textField.$input.on('input', function () {
                    const textFieldValue = $(this).val().toLowerCase().trim().replace(/:/g, '\\:');
                    //todo: maybe add support all special symbols in the future.. ?
                    if (searchQueryObj[term] !== textFieldValue) {
                        appendDocuments(term, textFieldValue, true, true);
                    }
                });

                return new BEM({
                    block: 'imcms-input-search',
                    elements: {
                        'text-box': $textField,
                        'button': components.buttons.searchButton()
                    }
                }).buildBlockStructure('<div>');
            }

            function buildUsersFilterSelect() {
                const onSelected = value => {
                    if (searchQueryObj[userId] !== value) {
                        appendDocuments(userId, value, true, true);
                    }
                };

                const $usersFilterSelectContainer = components.selects.selectContainer('<div>', {
                    id: 'users-filter',
                    name: 'users-filter',
                    text: texts.owner,
                    emptySelect: true,
                    onSelected: onSelected
                });

                usersRestApi.getAllAdmins()
                    .done(users => {
                        const usersDataMapped = users.map(user => ({
                            text: user.login,
                            'data-value': user.id
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

                const $categoriesFilterSelectContainer = components.selects.selectContainer('<div>', {
                    id: 'categories-filter',
                    name: 'categories-filter',
                    text: texts.category,
                    emptySelect: true,
                    onSelected: onSelected
                });

                categoriesRestApi.read(null)
                    .done(categories => {
                        const categoriesDataMapped = categories.map(category => ({
                            text: category.name,
                            'data-value': category.id
                        }));

                        components.selects.addOptionsToSelect(
                            categoriesDataMapped, $categoriesFilterSelectContainer.getSelect(), onSelected);
                    })
                    .fail(() => modal.buildErrorWindow(texts.error.categoriesLoadFailed));

                return $categoriesFilterSelectContainer;
            }

            const toolBEM = new BEM({
                block: 'imcms-document-editor-head-tool',
                elements: {
                    'search': 'imcms-input-search',
                    'button': 'imcms-button'
                }
            });

            const $newDocButtonContainer = toolBEM.buildBlock('<div>', [{'button': buildNewDocButton()}]);
            $newDocButtonContainer.modifiers = ['grid-col-1'];

            const $searchContainer = toolBEM.buildBlock('<div>', [{'search': buildSearchDocField()}]);
            $searchContainer.modifiers = ['grid-col-3'];

            const $usersFilter = toolBEM.buildBlock('<div>', [{'select': buildUsersFilterSelect()}]);
            $usersFilter.modifiers = ['grid-col-4'];

            const $categoriesFilter = toolBEM.buildBlock('<div>', [{'select': buildCategoriesFilterSelect()}]);
            $categoriesFilter.modifiers = ['grid-col-4'];

            const $loadingAnimation = toolBEM.buildBlock('<div>', [{'load': buildLoadCopyAnimation()}]);
            $loadingAnimation.modifiers = ['grid-col-1'];

            const $multiRemoveDocs = toolBEM.buildBlock('<div>', [{'remove': buildSwitchesOffOnButtons()}]);
            $multiRemoveDocs.modifiers = ['grid-col-1'];

            return new BEM({
                block: 'imcms-document-editor-head-tools',
                elements: {
                    'tool': [
                        (opts.inMenu && !imcms.accessToDocumentEditor) ||$newDocButtonContainer,
                        $searchContainer,
                        $usersFilter,
                        $categoriesFilter,
                        $loadingAnimation,
                        opts.inMenu || $multiRemoveDocs
                    ]
                }
            }).buildBlockStructure('<div>');
        }

        function buildBodyHead(opts) {
            return new BEM({
                block: 'imcms-document-editor-head',
                elements: {
                    'tools': buildBodyHeadTools(opts),
                    'error-search': buildErrorBlock()
                }
            }).buildBlockStructure('<div>');
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
            const $defaultSortingHeader = $('.imcms-document-editor-body .imcms-document-list-titles__title--modified-date');
            highlightSorting($defaultSortingHeader);
        }

        function highlightSorting($sortingHeader) {
            if (isActiveHeader($sortingHeader)) {
                const $sortingIcon = $sortingHeader.find('.imcms-document-list-title-row__icon');
                toggleSortingIcon($sortingIcon)
            }

            $('.imcms-document-list-titles__title--active').removeClass('imcms-document-list-titles__title--active');
            $sortingHeader.addClass('imcms-document-list-titles__title--active');
        }

        function toggleSortingIcon($sortingIcon) {
            $sortingIcon.hasClass(sortDescClassName)
                ? $sortingIcon.removeClass(sortDescClassName).addClass(sortAscClassName)
                : $sortingIcon.removeClass(sortAscClassName).addClass(sortDescClassName);
        }

        function setDefaultSortingIcons() {
            $('.imcms-document-list-titles__title ' + sortDescClass)
                .removeClass(sortDescClassName)
                .addClass(sortAscClassName);

            $(`.imcms-document-list-titles__title[${defaultSortingAttribute}] ${sortAscClass}`)
                .removeClass(sortAscClassName)
                .addClass(sortDescClassName);
        }

        function discardPreviousSortingIcon() {
            $('.imcms-document-list__titles').find(sortDescClass)
                .removeClass(sortDescClassName)
                .addClass(sortAscClassName);
        }

        function isActiveHeader($sortingHeader) {
            return $sortingHeader.hasClass('imcms-document-list-titles__title--active');
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

        function getDocumentIdsWithActiveCheckbox(){
            const $documents = $('.imcms-document-items-list').find('.imcms-document-items');
            const docIds = [];

            $documents.each(function () {
                const $doc = $(this).first();

                if (isActiveCheckBox($doc)) {
                    docIds.push(parseInt($doc.attr('data-doc-id')));
                }
            });

            function isActiveCheckBox($doc) {
                return $doc.find('.'+multiRemoveControlClass).find(":input").get(0).checked;
            }

            return docIds;
        }

        function buildDocumentListTitlesRow(opts) {
            const $idColumnHead = buildTitleRow({
                text: texts.sort.id,
                bySorting: 'meta_id',
                elementClass: 'imcms-grid-col-18',
                modifiers: ['id'],
            });

            const $titleColumnHead = buildTitleRow({
                text: texts.sort.title,
                bySorting: 'meta_headline_l_' + imcms.language.code,
                elementClass: 'imcms-flex--flex-3',
                modifiers: ['title']
            });

            const $aliasColumnHead = buildTitleRow({
                text: texts.sort.alias,
	            bySorting: 'meta_alias_l_' + imcms.language.code,
                elementClass: 'imcms-flex--flex-2',
            });

            const $modifiedColumnHead = buildTitleRow({
                text: texts.sort.modified,
                bySorting: defaultSortPropertyValue,
                elementClass: 'imcms-grid-col-17',
                modifiers: ['modified-date'],
            });

            const $publishedColumnHead = buildTitleRow({
                text: texts.sort.published,
                bySorting: 'publication_start_datetime',
                elementClass: 'imcms-grid-col-17',
                modifiers: ['published-date'],
            });

            const $versionColumnHead = buildTitleRow({
                text: texts.sort.version,
                bySorting: 'version_no',
                elementClass: 'imcms-grid-col-18',
                modifiers: ['currentVersion'],
            });

            const $typeColumnHead = buildTitleRow({
                text: texts.sort.type,
                elementClass: 'imcms-grid-col-18',
                modifiers: ['type'],
            });

            const $statusColumnHead = buildTitleRow({
                text: texts.sort.status,
                elementClass: 'imcms-grid-col-1',
                modifiers: ['status'],
            });

            const elements = {
                title: [
                    $idColumnHead,
                    $titleColumnHead,
                    $aliasColumnHead,
                    $modifiedColumnHead,
                    $publishedColumnHead,
                    $versionColumnHead,
                    $typeColumnHead,
                    $statusColumnHead,
                ],
            };
            if (!opts.inMenu) {
                elements.actions = buildActionSelect();
            }

            return new BEM({
                block: 'imcms-document-list-titles',
                elements,
            }).buildBlockStructure('<div>');
        }

        function buildTitleRow({text, bySorting, modifiers, elementClass}) {
            const $emptyIcon = $('<div>');

            const $sortIcon = bySorting
                ? bySorting === defaultSortPropertyValue ? components.controls.sortDesc() : components.controls.sortAsc()
                : $emptyIcon;

            const $titleRow = $('<div>', {
                text: text,
            });

            const $titleRowBem = new BEM({
                block: 'imcms-document-list-title-row',
                elements: {
                    'title': $titleRow,
                    'icon': $sortIcon,
                }
            }).buildBlockStructure('<div>', {
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

        function buildActionSelect() {
            const deleteId = 'delete';
            const putId = 'put';

            const onSelected = value => {
                let docIdsWithActiveCheckbox = getDocumentIdsWithActiveCheckbox();

                switch (value) {
                    case deleteId:
                        removeDocuments(docIdsWithActiveCheckbox);
                        break;
                    case putId:
                        putToWasteBasket(docIdsWithActiveCheckbox);

                        docIdsWithActiveCheckbox.forEach(docId => {
                            $documentsList.find('[data-doc-id=' + docId + ']')
                                .find('.' + multiRemoveControlClass).find(":input").get(0).click()
                        });
                        break;
                }

                $actionSelect.find(".imcms-drop-down-list__select-item-value").text(texts.controls.actions);
            };

            $actionSelect = components.selects.selectContainer('<div>', {
                emptySelect: false,
                onSelected: onSelected
            }, [
                {
                    text: texts.controls.removeAction,
                    'data-value': deleteId
                },
                {
                    text: texts.controls.putToBasketAction,
                    'data-value': putId
                }]);

            $actionSelect.css("display", "none")

            $actionSelect.find(".imcms-drop-down-list__select-item-value").text(texts.controls.actions);

            return $('<div>').append($actionSelect);
        }

        function createFrame(event) {
            const $this = $(this),
                original = $this.closest('.imcms-document-items'),
                $frame = original.clone(),
                $frameLayout = $('<div>'),
                frameItem = $frame.find('.imcms-document-item'),
	            $menuEditor = $(".imcms-menu-editor")
            ;

            $menuArea = get$menuItemsList();
            $frameLayout.addClass('imcms-frame-layout')
                .css({
                    'display': 'none',
                    'position': 'fixed',
                    'top': 0,
                    'left': 0,
                    'width': '100%',
                    'height': '100%',
                    'background': 'transparent',
                    // 'opacity': 0,
                    'z-index': 10101
                });
            $frameLayout.appendTo($menuEditor);

            original.addClass('imcms-document-items--is-drag');

            isMouseDown = true;
            mouseCoords = {
                pageX: event.clientX,
                pageY: event.clientY,
                top: $this.closest('.imcms-document-items').position().top,
                left: $this.closest('.imcms-document-items').position().left
            };
            menuAreaProp = {
                top: $menuArea.position().top,
                left: $menuArea.position().left,
                right: menuAreaProp.left + $menuArea.outerWidth(),
                bottom: menuAreaProp.top + $menuArea.outerHeight()
            };

	        frameItem.attr({
		        'data-id': frameItem.find('.imcms-document-item__info--id').text(),
		        'data-title': frameItem.find('.imcms-document-item__info--title').text(),
		        'data-is-shown-title': !frameItem.find('.imcms-document-item__info--notShownTitle').length,
		        'data-publishedDate': frameItem.find('.imcms-document-item__info--publishedDate').text(),
		        'data-modifiedDate': frameItem.find('.imcms-document-item__info--modifiedDate').text(),
		        'data-type': frameItem.find('.imcms-document-item__info--type').text(),
		        'data-status': frameItem.find('.imcms-document-item__info--status').text(),
		        'data-original-status': frameItem.find('.imcms-document-item__info--originalStatus').text(),
		        'data-current-version': frameItem.find('.imcms-document-item__info--currentVersion').children().attr('value'),
		        'data-createdBy': frameItem.find('.imcms-document-item__info--createdBy').text(),
		        'data-createdDate': frameItem.find('.imcms-document-item__info--createdDate').text(),
                'data-linkableForUnauthorizedUsers': frameItem.find('.imcms-document-item__info--linkableForUnauthorizedUsers').text(),
                'data-linkableByOtherUsers': frameItem.find('.imcms-document-item__info--linkableByOtherUsers').text(),
	        });

            $frame.addClass('imcms-document-items--frame');
            $frame.css({
                'top': mouseCoords.top,
                'left': mouseCoords.left
            });

            toggleUserSelect(true);
            $frame.appendTo($menuEditor);
        }

        $(document).on('mousemove', event => {
            if (!isMouseDown) {
                const $dragDoc = $('.imcms-document-items--is-drag');
                $dragDoc.removeClass('imcms-document-items--is-drag');
                return;
            }
            moveFrame(event);
        });

        function refreshDocumentInList(document, savedFlag) {
            if ($documentsList === undefined || null === document) return;
            const $oldDocumentElement = $documentsList.find('[data-doc-id=' + document.id + ']');

            if ($oldDocumentElement.length === 1) {
                const $newDocumentElement = buildDocument(document, currentEditorOptions, savedFlag);
                $oldDocumentElement.replaceWith($newDocumentElement);
            }
        }

        function refreshDocumentStatus(docId, docStatus, statusColor, backgroundColor){
            const $documentElement = $documentsList.find('[data-doc-id=' + docId + ']');

            const $documentStatus = $documentElement.find(".imcms-document-item__info--status");
            $documentStatus.text(docStatus.title);
            if(statusColor) $documentStatus.css("color", statusColor);

            if(backgroundColor) $documentElement.find(".imcms-document-item").css("background-color", backgroundColor);

            components.overlays.changeTooltipText($documentStatus, docStatus.tooltip);

            $documentElement.find(".imcms-document-item__info--originalStatus").text(docStatus);
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
                $doc.find('.imcms-controls')
                    .replaceWith(buildDocItemControls(document, opts, isEnabledMultiRemove));
            });
        }

        function buildSwitchesOffOnButtons() {

            function switchButtonAction() {
                const $switchButton = $('.imcms-remove-switch-block__button');

                if (isMultiRemoveModeEnabled()) {
                    $switchButton.removeClass(classButtonOn).addClass(classButtonOff);
                    $actionSelect.css('display', 'none');
                } else if ($switchButton.hasClass(classButtonOff)) {
                    $switchButton.removeClass(classButtonOff).addClass(classButtonOn);
                    $actionSelect.css('display', 'block');
                }

                changeControlsByMultiRemove();
            }

            return new BEM({
                block: 'imcms-remove-switch-block',
                elements: {
                    'active-info': components.texts.infoText('<div>', texts.controls.multiRemoveInfo),
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
            const newVal = $this.is(':checked');
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

            if (isEnableMultiRemove && !opts.inMenu) controls.push($multiRemoveBoxControl);

            if (opts.copyEnable) {
                function onConfirm() {
                    const $animationBlock = $('.imcms-document-editor-head-tool__load');
	                $animationBlock.css({
		                'visibility': 'visible',
					});
                    docCopyRestApi.copy(document.id)
                        .done(copiedDocument => {
                            addDocumentToList(copiedDocument);
                            $animationBlock.css('visibility', 'hidden');
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
                ? components.controls.buildControlsBlock('<div>', controls, {
                    'class': multiRemoveControlClass
                })
                : components.controls.buildControlsBlock('<div>', controls, {
                    'class': 'imcms-document-item__controls'
                });
        }

        function moveFrame(event) {
            const $frame = $('.imcms-document-items--frame');
            mouseCoords.newPageX = event.clientX;
            mouseCoords.newPageY = event.clientY;

            if (isMouseDown) {
                $frame.css({
                    'top': (mouseCoords.newPageY - mouseCoords.pageY) + mouseCoords.top,
                    'left': (mouseCoords.newPageX - mouseCoords.pageX) + mouseCoords.left
                });

	            if (detectTargetArea(event)) {
		            getDocumentParent();
	            } else {
		            $(menuDocItemCopy).remove();
	            }

            }
        }

        function checkByDocIdInMenuEditor(documentId) {
            let status = false;
            get$menuItems().each(function () {
                if (parseInt($(this).attr('data-document-id')) === documentId) {
                    status = true;
                }
            });

            return status;
        }

        function detectTargetArea(event) {
            return (event.clientY > menuAreaProp.top) &&
	            (event.clientY < menuAreaProp.bottom + menuAreaProp.top) &&
	            (event.clientX > menuAreaProp.left) &&
	            (event.clientX < menuAreaProp.right);
        }

        function getMenuDocByObjId(obj) {
            const menuDocs = get$menuItemsList().find(menuItemsSelector);
            let menuDoc = null;

            menuDocs.each(function () {
                if ($(this).attr('data-document-id') === obj) {
                    menuDoc = $(this)
                }
            });

            return menuDoc;
        }

        function removedPreviousItemFrame() {
            const $menuTree = get$menuItemsList(),
                $menuItemFrame = $('.imcms-document-items--frame').find('.imcms-document-item'),
                $frameParent = $menuTree.find('[data-document-id=' + $menuItemFrame.attr('data-id') + ']')
                    .parent('[data-menu-items-lvl]')
            ;

            if ($frameParent.find('[data-menu-items-lvl]').length === 1) {
                $frameParent.find('.children-triangle').remove();
            }
            $menuTree.find('[data-document-id=' + $menuItemFrame.attr('data-id') + ']').remove();
        }

        function slideUpMenuDocIfItClose(menuDoc) {
            const showHidBtn = menuDoc.find('.imcms-menu-item').first().find('.children-triangle');

            if (!showHidBtn.hasClass('imcms-document-item__btn--open')) {
                showHidBtn.trigger('click');
            }
        }

        function createMenuItemFrame(menuDoc, placeStatus, frameTop) {
            const insertedParent = {
		            parent: frameTop < topPointMenu || (frameTop > topPointMenu && frameTop < menuAreaProp.bottom+ menuAreaProp.top) ? null : menuDoc,
                    status: placeStatus,
                    frameTopPos: frameTop
                },
                $menuItemFrame = $('.imcms-document-items--frame').find('.imcms-document-item')
            ;

            removedPreviousItemFrame();

            if (menuDoc.find('[data-document-id=' + $menuItemFrame.attr('data-id') + ']').length !== 0) {
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

            // false -> under parent; true -> in parent; null -> under all
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

			if (frameTop > topPointMenu && frameTop < menuAreaProp.bottom + menuAreaProp.top) {
				menuDoc = getFirstItemInMenuArea();
				placeStatus = false;
				createMenuItemFrame(menuDoc, placeStatus, frameTop)
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
                menuDocs = itemTree.find('.imcms-menu-item'),
                $frame = $('.imcms-document-items--frame'),
                frameTop = $frame.offset().top
            ;
            if (menuDocs.length === 0) {
                return checkFramePositioning(allMenuDocObjArray, frameTop)
            }

            // get all menu doc coords
            menuDocs.each(function () {
                allMenuDocObjArray[$(this).closest(menuItemsSelector).attr('data-document-id')] = {
                    top: $(this).offset().top,
                    bottom: $(this).offset().top + $(this).outerHeight()
                };
            });
            return checkFramePositioning(allMenuDocObjArray, frameTop);
        }

        function setDataInputParams(insertedParent, frameItem) {
            const dataInput = $('#dataInput');
            const typeSort = document.getElementById('type-sort').value;

            if (typeSort !== TREE_SORT && insertedParent.parent !== null) {
	            dataInput.attr({
		            'data-parent-id': insertedParent.parent.attr('data-document-id'),
		            'data-insert-place': ''
	            });
            } else {
                if (insertedParent.parent !== null) {
                    dataInput.attr({
	                    'data-parent-id': insertedParent.parent.attr('data-document-id'),
	                    'data-insert-place': insertedParent.status
                    });
                } else {
	                dataInput.attr({
		                'data-parent-id': '',
		                'data-insert-place': ''
	                });
                }
            }
	        dataInput.attr({
		        'data-id': frameItem.attr('data-id'),
		        'data-type-sort': typeSort,
		        'data-type': frameItem.attr('data-type'),
		        'data-status': frameItem.attr('data-status'),
		        'data-original-status': frameItem.attr('data-original-status'),
		        'data-createdBy': frameItem.attr('data-createdBy'),
		        'data-createdDate': frameItem.attr("data-createdDate"),
		        'data-publishedDate': frameItem.attr('data-publishedDate'),
		        'data-modifiedDate': frameItem.attr('data-modifiedDate'),
		        'data-is-shown-title': frameItem.attr('data-is-shown-title'),
		        'data-frame-top': insertedParent.frameTopPos,
		        'data-current-version': frameItem.attr('data-current-version'),
                'data-linkableForUnauthorizedUsers': frameItem.attr('data-linkableForUnauthorizedUsers'),
                'data-linkableByOtherUsers': frameItem.attr('data-linkableByOtherUsers')
	        });
            dataInput.attr('data-title', frameItem.attr('data-title')).trigger('change');

	        $(menuDocItemCopy).css({
		        'border': '1px dashed red'
	        });
        }

        function toggleUserSelect(flag) {
            if (flag) {
                $('.imcms-frame-layout').css({'display': 'block'});
            } else {
                $('.imcms-frame-layout').remove();
            }
        }

        $(document).on('mouseup', event => {
            if (!isMouseDown) {
                return;
            }
            const $frame = $('.imcms-document-items--frame'),
                frameItem = $frame.find('.imcms-document-item');
            let insertedParent = null
            ;

            if ($frame.length === 0) {
                return;
            }
	        const $menuItemsList = get$menuItemsList();
            if (detectTargetArea(event)) {
                const $menuItemsList = get$menuItemsList();

                if ($menuItemsList.find('[data-document-id=' + frameItem.attr('data-id') + ']').length === 0) {
                    insertedParent = getDocumentParent();
                    setDataInputParams(insertedParent, frameItem);
                }

                $menuItemsList.find(menuDocItemCopy).removeClass('imcms-doc-item-copy');
            }

            toggleUserSelect(false);

	        $menuItemsList.find(menuDocItemCopy).remove();
            let document = getDocumentById(frameItem.attr('data-id'));
			refreshDocumentInList(document, isDocSaved(document));
            $frame.remove();
            isMouseDown = false;
            const $sortOrder = $('.imcms-document-list-titles__title--sort-order');
            $sortOrder.click();
        });

        function isDocSaved(document){
            return document.commonContents !== undefined && document.currentVersion.id !== undefined;
        }

        function getDocumentVersionTexts(hasNewerVersion) {
            return hasNewerVersion
                ? {tooltip: texts.version.tooltip.hasNewerVersion}
                : {tooltip: texts.version.tooltip.noWorkingVersion}
        }

        function getIdTooltipText(id, created, createdBy) {
			if (createdBy)
                return `${id}. ${texts.id.tooltip.createdOn} ${created} ${texts.by} ${createdBy}`
            else
				return `${id}. ${texts.id.tooltip.createdOn} ${created.date} ${texts.by} ${created.by}`
        }

        function getModifiedDateTooltipText(date, user) {
            return `${texts.modified.tooltip.lastChangedOn} ${date} ${texts.by} ${user}`
        }

        function getPublishedDateTooltipText(date, user) {
            return `${texts.published.tooltip.publishedOn} ${date} ${texts.by} ${user}`
        }

        /** @namespace document.documentStatus */
        function buildDocItem(document, opts, savedFlag) {

            const $docItemId = components.texts.titleText('<a>', document.id, {
                href: '/' + document.id,
                class: 'imcms-grid-col-18',
            });
            $docItemId.modifiers = ['id'];
            components.overlays.defaultTooltip(
                $docItemId,
                getIdTooltipText(document.id, document.created, document.createdBy),
                {placement: 'right'}
            );

            let title;
            if (savedFlag) {
                const content = document.commonContents.filter(content => content.enabled)
                    .filter(enableContent => enableContent.language.code === imcms.language.code)
                    .shift();

                title = content ? content.headline : texts.notShownInSelectedLang;

            } else {
                title = document.isShownTitle ? document.title : texts.notShownInSelectedLang;
            }
            const $docItemTitle = components.texts.titleText('<a>', title, {
                href: '/' + document.id,
                class: 'imcms-flex--flex-3',
            });
            $docItemTitle.modifiers = ['title'];
            (!document.isShownTitle && undefined !== document.isShownTitle) && $docItemTitle.modifiers.push('notShownTitle');
            title && components.overlays.defaultTooltip($docItemTitle, title);

            const $docItemAlias = components.texts.titleText('<div>', document.alias && ('/' + document.alias), {
                class: 'imcms-flex--flex-2',
            });
            $docItemAlias.modifiers = ['alias'];
            document.alias && components.overlays.defaultTooltip($docItemAlias, '/' + document.alias);

            let docModifiedDate;
            let docModifiedBy;
            if (savedFlag) {
                docModifiedDate = (document.modified.date && document.modified.time)
                    ? `${document.modified.date} ${document.modified.time}`
                    : '';
                docModifiedBy = document.modified.by;
            } else {
                docModifiedDate = document.modified;
                docModifiedBy = document.modifiedBy;
            }
            const $docItemModified = components.texts.titleText('<div>', docModifiedDate, {
                class: 'imcms-grid-col-17',
            });
            $docItemModified.modifiers = ['date', 'modifiedDate'];
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
                    : '';
                docPublishedBy = document.published.by;
            } else {
                docPublishedDate = document.published;
                docPublishedBy = document.publishedBy;
            }
            const $docItemPublished = components.texts.titleText('<div>', docPublishedDate, {
                class: 'imcms-grid-col-17',
            });
            $docItemPublished.modifiers = ['date', 'publishedDate'];
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

            const $docItemType = components.texts.titleText('<div>', document.type, {
                class: 'imcms-grid-col-18',
            });
            $docItemType.modifiers = ['type'];

            const docStatusTexts = docStatus.getDocumentStatusTexts(document.documentStatus, docPublishedDate);
            const $docStatus = components.texts.titleText('<div>', docStatusTexts.title, {
                class: 'imcms-grid-col-1',
            });
            $docStatus.modifiers = ['status'];
            components.overlays.defaultTooltip($docStatus, docStatusTexts.tooltip, {placement: 'left'});

            const $originalDocStatus = components.texts.titleText('<div>', document.documentStatus);
            $originalDocStatus.modifiers = ['originalStatus'];
            $originalDocStatus.css({'display': 'none'});

			//required in order to correct build menu items when drag
	        const $docItemCreatedBy = $("<div hidden>").text(document.createdBy);
	        $docItemCreatedBy.modifiers=['createdBy'];
	        const $docItemCreatedDate = $("<div hidden>").text(document.created);
	        $docItemCreatedDate.modifiers=['createdDate'];

            const $docLinkableForUnauthorizedUsers = $("<div hidden>").text(document.linkableForUnauthorizedUsers);
            $docLinkableForUnauthorizedUsers.modifiers=['linkableForUnauthorizedUsers'];
            const $docLinkableByOtherUsers = $("<div hidden>").text(document.linkableByOtherUsers);
            $docLinkableByOtherUsers.modifiers=['linkableByOtherUsers'];

            const elements = [
                {
                    'info': [
                        $docItemId,
                        $docItemTitle,
                        $docItemAlias,
                        $docItemModified,
                        $docItemPublished,
                        $currentVersion,
                        $docItemType,
                        $docStatus,
                        $originalDocStatus,
	                    $docItemCreatedBy,
	                    $docItemCreatedDate,
                        $docLinkableForUnauthorizedUsers,
                        $docLinkableByOtherUsers
                    ]
                },
                {'controls': buildDocItemControls(document, opts, isMultiRemoveModeEnabled())}
            ];

            const $moveControl = components.controls.move();
            const $unMoveArrow = components.controls.left().css({'cursor': 'not-allowed'});

            if (opts && opts.moveEnable) {
                $moveControl.on('mousedown', createFrame);
                let isExistDocInMenu = checkByDocIdInMenuEditor(document.id);
                let $controlsBlock = components.controls.buildControlsBlock('<div>',
                    (isExistDocInMenu) ? [$unMoveArrow] : [$moveControl]);
                elements.unshift({
                    controls: (isExistDocInMenu)
                        ? $controlsBlock.css({'display': 'block'})
                        : $controlsBlock
                });
            }

            return new BEM({
                block: 'imcms-document-item',
                elements: elements
            }).buildBlockStructure('<div>');
        }

        function buildDocumentItemContainer(document, opts, isUsed, savedFlag) {
            return new BEM({
                block: 'imcms-document-items',
                elements: [{
                    'document-item': buildDocItem(document, opts, savedFlag),
                    modifiers: [document.documentStatus.replace(/_/g, '-').toLowerCase()]
                }]
            }).buildBlockStructure('<div>', {
                'data-doc-id': document.id,
                style: (isUsed) ? 'background-color: #b6b6b6; opacity: 0.4;' : ''
            });
        }

        const documentsListBEM = new BEM({
            block: 'imcms-document-items-list',
            elements: {
                'document-items': ''
            }
        });

        function buildDocument(document, opts, savedFlag) {
            const $documentItem = buildDocumentItemContainer(document, opts, checkByDocIdInMenuEditor(document.id), savedFlag);
            return documentsListBEM.makeBlockElement('document-items', $documentItem);
        }

        function buildDocumentList(documentList, savedFlag, opts) {
            const $blockElements = documentList.map(document => buildDocumentItemContainer(document, opts, checkByDocIdInMenuEditor(document.id), savedFlag));

            return new BEM({
                block: 'imcms-document-items-list',
                elements: {
                    'document-items': $blockElements
                }
            }).buildBlockStructure('<div>');
        }

        function buildEditorBody(documentList, opts) {
            currentEditorOptions = opts;
            $documentsList = buildDocumentList(documentList, false, opts);

            $documentsList.scroll(function () {
                const $this = $(this);

                const innerHeight = $this.innerHeight();
                const scrollHeight = this.scrollHeight;

                if (sendSearchDocRequest
                    && innerHeight !== scrollHeight
                    && (($this.scrollTop() + innerHeight) >= scrollHeight)) {
                    appendDocuments(pageSkip, currentDocumentNumber, false, false);
                }
                errorMsg.slideUp();
            });

            return new BEM({
                block: 'imcms-document-list',
                elements: {
                    'titles': buildDocumentListTitlesRow(opts),
                    'items': $documentsList
                }
            }).buildBlockStructure('<div>');
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

        function buildBody(opts) {
            return new BEM({
                block: 'imcms-document-editor-body',
                elements: {
                    'body-head': buildBodyHead(opts)
                }
            }).buildBlockStructure('<div>');
        }

        function loadDocumentEditorContent($documentsContainer, opts) {
            docSearchRestApi.read(searchQueryObj)
                .done(documentList => {
                    pushDocumentsInArray(documentList);
                    const newDocsList = documentList.slice(0, 100);
                    incrementDocumentNumber(newDocsList.length);
                    $editorBody = buildEditorBody(newDocsList, opts);
                    $documentsContainer.append($editorBody);
                    highlightDefaultSorting();
                    setDefaultSortProperties();
                })
                .fail(() => {
                    errorMsg.slideDown();
                });
        }

        let docs = [];

        function pushDocumentsInArray(documentList) {
            const allDocIds = docs.map(doc => doc.documentId || doc.id);
            documentList.forEach(document => {
                if (!allDocIds.includes(document.documentId || document.id)) {
                    docs.push(document);
                }
            });
            return docs;
        }

        function initMenuDocuments(ids){
            const allDocIds = docs.map(doc => doc.documentId || doc.id);

            ids.forEach(id => {
                if(!allDocIds.includes(document.documentId || document.id)){
                    docSearchRestApi.searchById(id).done(doc => docs.push(doc));
                }
            });
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
                block: 'imcms-document-editor',
                elements: {
                    'head': buildHead(),
                    'body': $documentsContainer = buildBody(),
                    'footer': buildFooter()
                }
            }).buildBlockStructure('<div>', {'class': 'imcms-editor-window'});
        }

        function removeDocument(document) {
            const question = 'Do you want to remove document ' + document.id + '?';

            imcmsModalWindowBuilder.buildModalWindow(question, function (answer) {
                if (!answer) {
                    return;
                }

                docRestApi.remove(document)
                    .done(function () {
                        $(this).parent().parent().remove();
                    })
                    .fail((response) => {
                        let errorText;
                        if(response.responseText){
                            let ids = response.responseText.replace("[", "").replace("]", "");
                            errorText = texts.error.removeProtectedDocumentFailed + ": " + ids;
                        }else{
                            errorText = texts.error.removeDocumentFailed;
                        }
                        modal.buildErrorWindow(errorText);
                    });
            });
        }

        function removeDocumentsFromEditor(documentIds) {
            documentIds.forEach(id => {
                $documentsList.find('[data-doc-id=' + id + ']').remove();
            })
        }

        function removeDocuments(documentIds) {
            const question1 = texts.controls.question;
            const question2 = texts.controls.question2;

            imcmsModalWindowBuilder.buildModalWindow(question1, function (answer) {
                if (!answer) {
                    return;
                }

                imcmsModalWindowBuilder.buildModalWindow(question2, function (answer) {
                    if (!answer) {
                        return;
                    }

                    docRestApi.removeByIds(documentIds).done(() => {
                        removeDocumentsFromEditor(documentIds);
                        alert(texts.deleteInfo)
                    }).fail((response) => {
                        let errorText;
                        if(response.responseText){
                            let ids = response.responseText.replace("[", "").replace("]", "");
                            errorText = texts.error.removeProtectedDocumentFailed + ": " + ids;
                        }else{
                            errorText = texts.error.removeDocumentFailed;
                        }
                        modal.buildErrorWindow(errorText);
                    })
                });
            });
        }

        function putToWasteBasket(docIds){
            docBasketRestApi.createByIds(docIds).done(() => {
                docIds.forEach(docId => {
                    refreshDocumentStatus(docId, docStatus.getDocumentStatusTexts('WASTE_BASKET'), "red", "transparent");
                });
            }).fail(() => {
                modal.buildErrorWindow(texts.error.putToWasteBasketFailed);
            });
        }

        var currentEditorOptions;

        function loadData() {
            loadDocumentEditorContent($documentsContainer, {
                editEnable: true,
                copyEnable: true,
                removeEnable: false, // todo: maybe should be replaced with archivationEnable in future
                inMenu: false
            });
        }

        function clearData() {
            events.trigger('document-editor-closed');
            $('.imcms-info-page').css({'display': 'block'});

            // setting default values
            searchQueryObj[pageSkip] = currentDocumentNumber = 0;
            searchQueryObj[term] = '';
            searchQueryObj[userId] = null;
            searchQueryObj[categoriesId] = {};
            searchQueryObj[roleIdProperty] = null;

            sendSearchDocRequest = true;

            delete searchQueryObj[sortProperty];
            delete searchQueryObj[sortDirection];

            // clean up
            $textField.$input.val('');

            $editorBody.remove();
        }

        function incrementDocumentNumber(delta) {
            currentDocumentNumber += delta;
        }

        var documentWindowBuilder = new WindowBuilder({
            factory: buildDocumentEditor,
            loadDataStrategy: loadData,
            clearDataStrategy: clearData,
            onEscKeyPressed: 'close'
        });

        return {
            buildBody,
            loadDocumentEditorContent,
            clearData,
            buildDocument,
            incrementDocumentNumber,
            initMenuDocuments,
            addDocumentToList,
            updateDocumentInList,
            getDocumentVersionTexts,
            getIdTooltipText,
            getModifiedDateTooltipText,
            getPublishedDateTooltipText,
            refreshDocumentInList,
            getDocumentById,
            build: function (roleId) {
                searchQueryObj[roleIdProperty] = roleId;
                documentWindowBuilder.buildWindow.apply(documentWindowBuilder, arguments);
            }
        };
    }
);
