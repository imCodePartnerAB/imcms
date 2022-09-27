/**
 * Created by Serhii Maksymchuk from Ubrainians for imCode
 * 07.08.17.
 */
define("imcms-page-info-builder",
    [
        "imcms-bem-builder", "imcms-components-builder", "imcms-window-builder",
        "imcms-page-info-tabs-builder", "jquery", "imcms-events", "imcms",
        "imcms-documents-rest-api", "imcms-file-doc-files-rest-api", "imcms-roles-rest-api", "imcms-publish-document-rest-api",
        "imcms-modal-window-builder", "imcms-i18n-texts", 'imcms-appearance-tab-builder', 'imcms-document-types', 'imcms-document-permission-types',
	    'imcms-formatters'
    ],
    (BEM, components, WindowBuilder, pageInfoTabs, $, events, imcms,
     documentsRestApi, docFilesAjaxApi, rolesRestApi, publishDocumentRestApi,
     modal, texts, appearanceTab, docTypes, docPermissionTypes, formatters) => {

        texts = texts.pageInfo;

        const TAB_INDEX_ATTRIBUTE = 'data-window-id';

        let windowPageInfoTabsBuilder;

        let panels$, documentDTO, $saveAndPublishBtn, $saveBtn, $nextBtn, $loadingAnimation;
        let $title = $('<a>');

        function buildPageInfoHead() {
            const $head = pageInfoWindowBuilder.buildHeadWithResizing(texts.document + ' - ', closePageInfo);
            $head.find(".imcms-head__title").append($title);

            return $head;
        }

        function buildPageInfoPanels(docId) {
            return windowPageInfoTabsBuilder.tabBuilders.map((tabBuilder, index) => tabBuilder.buildTab(index, docId));
        }

        function closePageInfo() {
            modal.buildConfirmWindowWithDontShowAgain(
                texts.confirmMessageOnCancel,
                () => pageInfoWindowBuilder.closeWindow(),
                "page-info-close"
            );
        }

        function saveAndClose(onDocumentSavedCallback) {
            windowPageInfoTabsBuilder.tabBuilders.forEach((tabBuilder) => documentDTO = tabBuilder.saveData(documentDTO));

            //Clear modified info before send to API
            documentDTO.modified = {id: ""};
	        const commonContents = documentDTO.commonContents;

            $loadingAnimation.show();
            saveDoc(documentDTO)
                .done((savedDoc) => {

                    if (documentDTO.newFiles) {
                        // files saved separately because of different content types and in file-doc case
                        documentDTO.newFiles.append("docId", savedDoc.id);
	                    docFilesAjaxApi.postFiles(documentDTO.newFiles);
                    }

	                if (documentDTO.id === imcms.document.id) {
		                events.trigger("imcms-version-modified");

	                } else {
		                documentDTO.id = savedDoc.id;
	                }

	                let duplicateLanguageAliases = [];
	                commonContents.forEach((commonContent, index) => {
		                const prevAlias = commonContent.alias;
		                const newAlias = savedDoc.commonContents[index].alias;
		                if (prevAlias !== newAlias && prevAlias !== "") {
			                duplicateLanguageAliases.push(savedDoc.commonContents[index].language.name);
		                }
	                })

	                if (duplicateLanguageAliases.length) {
		                documentDTO = savedDoc;
		                $loadingAnimation.hide();
		                modal.buildErrorWindow(texts.error.duplicateAlias.replace('%s', formatters.arrayOfStringsToFormattedString(duplicateLanguageAliases)));
	                } else {
		                $loadingAnimation.hide();
		                pageInfoWindowBuilder.closeWindow();
		                if (onDocumentSavedCallback) {
			                onDocumentSavedCallback(savedDoc);
		                }
					}
                })
	            .fail(() => {
		            modal.buildErrorWindow(texts.error.createDocumentFailed)
		            $loadingAnimation.hide();
	            });
        }

	    function saveDoc(document) {
		    return document.id ? documentsRestApi.replace(document) : documentsRestApi.create(document);
	    }

        function saveAndPublishAndReload() {
            saveAndClose(() => events.trigger("imcms-publish-new-version-current-doc"));
        }

        function saveAndPublishAndClose() {
            saveAndClose(() => {
                publishDocumentRestApi.publish(documentDTO.id)
                    .always(() => {
                        events.trigger("imcms-alert-publish-new-version");

                        let requestData = {docId: documentDTO.id};
                        documentsRestApi.read(requestData).done((document) => {
                            onDocumentSaved(document, true);
                        }).fail(() => modal.buildErrorWindow(texts.error.loadDocumentFailed))
                    })
            });
        }

        function confirmSaving() {
            const onDocumentSavedCallback = onDocumentSaved != null ?
                (doc) => onDocumentSaved(doc, true) : null;

            modal.buildConfirmWindow(texts.confirmMessage, () => saveAndClose(onDocumentSavedCallback));
        }

        function confirmSavingAndPublishing(saveAndPublish){
            modal.buildConfirmWindow(texts.confirmMessageOnSaveAndPublish, saveAndPublish);
        }

        function validateDoc() {
            return {
                isValid: appearanceTab.isValid(), // only this tab for now...
                message: texts.oneLanguageShouldBeEnabled
            };
        }

        function ifValidDocInfo(callMeIfValid) {
            return () => {
                const validationResult = validateDoc();
                (validationResult.isValid) ? callMeIfValid.call() : alert(validationResult.message);
            };
        }

        function moveToNextTabOrEnableOthersAndReplaceButton($buttonsForReplacing) {
            return function () {
                const currentTabIndex = parseInt(windowPageInfoTabsBuilder.getActiveTab().attr(TAB_INDEX_ATTRIBUTE));
                const $nextTab = getNextTab(currentTabIndex);
                const isEnabledNextTab = windowPageInfoTabsBuilder.isEnabledTabByIndex($nextTab.attr(TAB_INDEX_ATTRIBUTE));

                if (isEnabledNextTab) {
                    $nextTab.click();
                } else {
                    setEnabledExcessTabs(true);
                    $(this).hide();
                    $buttonsForReplacing.forEach($button => $button.show());
                }
            }
        }

        function getNextTab(currentTabIndex) {
            const nextTabIndex = currentTabIndex + 1;
            const $nextTab = windowPageInfoTabsBuilder.getTabByIndex(nextTabIndex);
            return $nextTab.css('display') === 'none' ? getNextTab(nextTabIndex) : $nextTab;
        }

        function buildPageInfoFooterButtons() {
            $saveBtn = components.buttons.positiveButton({
                text: texts.buttons.ok,
                click: ifValidDocInfo(confirmSaving),
                style: 'display: none',
            });

            const saveAndPublish = documentDTO.type === docTypes.TEXT ?
                ifValidDocInfo(saveAndPublishAndReload) : ifValidDocInfo(saveAndPublishAndClose);

            $saveAndPublishBtn = components.buttons.saveButton({
                text: texts.buttons.saveAndPublish,
                click: () => confirmSavingAndPublishing(saveAndPublish),
                style: "display: none;"
            });

            $nextBtn = components.buttons.positiveButton({
                text: texts.buttons.next + ' \u2b95',
                click: moveToNextTabOrEnableOthersAndReplaceButton([$saveBtn, $saveAndPublishBtn]),
                style: 'display: none',
            });

            const $cancelBtn = components.buttons.negativeButton({
                text: texts.buttons.cancel,
                click: closePageInfo
            });

            $loadingAnimation = $('<div>').addClass('pageInfo loading-animation').css("display", "none");

            const buttons = [$cancelBtn, $saveBtn, $nextBtn, $loadingAnimation];

            if (imcms.isSuperAdmin && imcms.isVersioningAllowed) {
                buttons.unshift($saveAndPublishBtn);
            }

            return buttons;
        }

        function buildPageInfo(docId, onDocumentSavedCallback) {
            onDocumentSaved = onDocumentSavedCallback;
            panels$ = buildPageInfoPanels(docId);

            return new BEM({
                block: "imcms-pop-up-modal",
                elements: {
                    "head": buildPageInfoHead(),
                    "left-side": windowPageInfoTabsBuilder.buildWindowTabs(panels$),
                    "right-side": $("<div>", {"class": "imcms-right-side"}).append(panels$),
                    "footer": $("<div>", {"class": "imcms-footer"}).append(buildPageInfoFooterButtons())
                }
            }).buildBlockStructure("<div>", {"data-menu": "pageInfo"});
        }

        function loadPageInfoDataFromDocumentBy(docId, docType, parentDocId) {

            if ((docId === imcms.document.id && imcms.document.hasNewerVersion) ||
                (documentDTO.id && documentDTO.type !== docTypes.TEXT)) {

                $saveAndPublishBtn.css("display", "block");
            }

            const linkData = '/api/admin/page-info?meta-id=' + documentDTO.id;

            const newDocText = documentDTO.type === docTypes.TEXT
                ? texts.newDocument.text
                : documentDTO.type === docTypes.URL
                    ? texts.newDocument.url
                    : texts.newDocument.file;

            $title.text((documentDTO.id)
                ? linkData
                : newDocText).css({'text-transform': 'initial', 'color': '#fff2f9'});

            $title.attr('href', linkData);

            windowPageInfoTabsBuilder.tabBuilders.forEach((tab) => {
                if (tab.isDocumentTypeSupported(documentDTO.type)) {
                    tab.fillTabDataFromDocument(documentDTO);
                    tab.showTab();

                } else {
                    tab.hideTab();
                }
            });

            if (!documentDTO.id && documentDTO.type !== docTypes.TEXT) {
                setEnabledExcessTabs(false);
            }

            documentDTO.id || documentDTO.type === docTypes.TEXT
                ? $saveBtn.show()
                : $nextBtn.show();
        }

        function setEnabledExcessTabs(isEnabled) {
            const tabs = windowPageInfoTabsBuilder.tabBuilders;

            tabs.slice(3, tabs.length).forEach(tab => {
                tab.setEnabled(isEnabled);
            });
        }

        function clearPageInfoData() {
            events.trigger("page info closed");
            $saveAndPublishBtn.css("display", "none");

            windowPageInfoTabsBuilder.tabBuilders.forEach((tab) => {
                tab.clearTabData();
            });
        }

        function loadData(docId, onDocumentSavedCallback, docType, parentDocId) {
            onDocumentSaved = onDocumentSavedCallback;
            selectFirstTab(docType);
            loadPageInfoDataFromDocumentBy(docId, docType, parentDocId);
        }

        function selectFirstTab(docType) {
            const firstTab = windowPageInfoTabsBuilder.tabBuilders.find(tab => tab.isDocumentTypeSupported(docType));
            const indexOfFirstTab = firstTab ? firstTab.tabIndex : 0;
            if (indexOfFirstTab === 0) {
                windowPageInfoTabsBuilder.setActiveTab(indexOfFirstTab, false);
            }
            windowPageInfoTabsBuilder.setActiveTab(indexOfFirstTab, true);
        }

        const pageInfoWindowBuilder = new WindowBuilder({
            factory: buildPageInfo,
            loadDataStrategy: loadData,
            clearDataStrategy: clearPageInfoData,
            onEscKeyPressed: closePageInfo,
            onEnterKeyPressed: confirmSaving
        });

        let onDocumentSaved;

        return {
            build: function (docId, onDocumentSavedCallback, docType, parentDocId) {
                onDocumentSaved = onDocumentSavedCallback;

                const requestData = {
                    docId: docId,
	                parentDocIdentity: parentDocId
                };
                if (docType) requestData.type = docType;

                documentsRestApi.read(requestData).done((document) => {
                    documentDTO = document;

                    //check document edit or create new one
                    if(parentDocId){
                        windowPageInfoTabsBuilder = document.type === docTypes.TEXT ? pageInfoTabs.textWindowTabsBuilder : pageInfoTabs.urlOrFileWindowTabsBuilder;
                        pageInfoWindowBuilder.buildWindowWithShadow.apply(pageInfoWindowBuilder, arguments);
                    }else{
                        rolesRestApi.currentUserRoleIds().done((roleIds) => {
                            const docPermissions = roleIds.map(roleId => documentDTO.roleIdToPermission[roleId]);

                            //check which tabs to display
                            if (imcms.isSuperAdmin || docPermissions.includes(docPermissionTypes.EDIT)) {
                                //url and file document types have not the "All Data" tab
                                windowPageInfoTabsBuilder = document.type === docTypes.TEXT ? pageInfoTabs.textWindowTabsBuilder : pageInfoTabs.urlOrFileWindowTabsBuilder;
                            } else {
                                windowPageInfoTabsBuilder = pageInfoTabs.limitedWindowTabsBuilder;

                                //check Page Info is available
                                const restrictedPermissionTypes = [docPermissionTypes.RESTRICTED_1, docPermissionTypes.RESTRICTED_2];
                                let haveAccess = false;
                                for (let i = 0; i < restrictedPermissionTypes.length; i++) {
                                    if (docPermissions.includes(restrictedPermissionTypes[i])) {
                                        haveAccess = documentDTO.restrictedPermissions[i].editDocInfo
                                        if (haveAccess) break;
                                    }
                                }
                                if (!haveAccess){
                                    modal.buildWarningWindow(texts.error.noAccess);
                                    return;
                                }
                            }

                            pageInfoWindowBuilder.buildWindowWithShadow.apply(pageInfoWindowBuilder, arguments);
                        }).fail(() => modal.buildErrorWindow(texts.error.loadRolesFailed))
                    }

                }).fail(() => modal.buildErrorWindow(texts.error.loadDocumentFailed))
            }
        };
    }
);
