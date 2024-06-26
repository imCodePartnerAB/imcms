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
	    'imcms-formatters', 'lodash'
    ],
    (BEM, components, WindowBuilder, pageInfoTabs, $, events, imcms,
     documentsRestApi, docFilesAjaxApi, rolesRestApi, publishDocumentRestApi,
     modal, texts, appearanceTab, docTypes, docPermissionTypes, formatters, lodash) => {

        texts = texts.pageInfo;

        const TAB_INDEX_ATTRIBUTE = 'data-window-id';
        const WORKING_VERSION = 0;

        let windowPageInfoTabsBuilder;
        let isStandalone = false;

        let panels$, documentDTO, $usageDetails, $saveAndPublishBtn, $saveBtn, $nextBtn, $loadingAnimation;
        let $title = $('<a>');

        let versionNo;

        function buildPageInfoHead() {
            const $head = pageInfoWindowBuilder.buildHeadWithResizing(texts.document + ' - ', closePageInfo);
            $head.find(".imcms-head__title").append($title);

            return $head;
        }

        function buildPageInfoPanels(docId) {
            return windowPageInfoTabsBuilder.getAllTabBuilders().map((tabBuilder, index) => tabBuilder.buildTab(index, docId));
        }

        function openDocumentationWindow(){
            const currentTabIndex = parseInt(windowPageInfoTabsBuilder.getActiveTab().attr(TAB_INDEX_ATTRIBUTE));
            const tabDocLink = windowPageInfoTabsBuilder.getAllTabBuilders()[currentTabIndex].getDocLink();
            const docLink = tabDocLink ? imcms.documentationLink + tabDocLink : imcms.documentationLink;
            window.open(docLink)
        }

        function closePageInfo() {
            modal.buildConfirmWindowWithDontShowAgain(
                texts.confirmMessageOnCancel,
                () => pageInfoWindowBuilder.closeWindow(),
                "page-info-close"
            );
        }

        function saveAndClose(publishDocumentCallback) {
            windowPageInfoTabsBuilder.getAllTabBuilders().forEach((tabBuilder) => documentDTO = tabBuilder.saveData(documentDTO));

            $loadingAnimation.show();
            saveDoc(documentDTO)
                .done((savedDoc) => {
                    $loadingAnimation.hide();

                    if (documentDTO.newFiles) {
                        // files saved separately because of different content types and in file-doc case
                        documentDTO.newFiles.append("docId", savedDoc.id);
	                    docFilesAjaxApi.postFiles(documentDTO.newFiles);
                    }

	                if (documentDTO.id === imcms.document.id) {
                        if (savedDoc.currentVersion.id === WORKING_VERSION) events.trigger("imcms-version-modified");

	                } else {
		                documentDTO.id = savedDoc.id;
	                }

                    const duplicateLanguageAliases = findDuplicateAliases(documentDTO, savedDoc);
                    if (duplicateLanguageAliases.length) {
                        documentDTO = savedDoc;
                        modal.buildErrorWindow(texts.error.duplicateAlias.replace('%s', formatters.arrayOfStringsToFormattedString(duplicateLanguageAliases)));
                    } else {
                        publishDocumentCallback ? publishDocumentCallback(savedDoc) : performOnDocumentSavedCallback();
                        pageInfoWindowBuilder.closeWindow();
                    }
                })
	            .fail(() => {
		            modal.buildErrorWindow(texts.error.createDocumentFailed)
		            $loadingAnimation.hide();
	            });
        }

        function findDuplicateAliases(documentDTO, savedDoc) {
            return documentDTO.commonContents.filter((commonContent, index) => {
                const prevAlias = commonContent.alias;
                const newAlias = savedDoc.commonContents[index].alias;

                return prevAlias !== newAlias && prevAlias !== "";
            }).map((commonContent, index) => savedDoc.commonContents[index].language.name);
        }
        
        function performOnDocumentSavedCallback() {
            if (onDocumentSaved) {
                let requestData = {docId: documentDTO.id};
                documentsRestApi.read(requestData).done((document) => {
                    onDocumentSaved(document, true);
                }).fail(() => modal.buildErrorWindow(texts.error.loadDocumentFailed))
            }
        }

	    function saveDoc(document) {
		    return document.id ? documentsRestApi.replace(document) : documentsRestApi.create(document);
	    }

        function publishAndReload() {
            events.trigger("imcms-publish-new-version-current-doc");
        }

        function publish() {
            publishDocumentRestApi.publish(documentDTO.id)
                .always(() => {
                    events.trigger("imcms-alert-publish-new-version");
                    performOnDocumentSavedCallback();
                })
        }

        function confirmSaving() {
            modal.buildConfirmWindow(texts.confirmMessage, saveAndClose);
        }

        function confirmSavingAndPublishing(){
            const publishFunc = onDocumentSaved ? publish : publishAndReload;

            modal.buildConfirmWindow(texts.confirmMessageOnSaveAndPublish, () => saveAndClose(publishFunc));
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
                text: texts.buttons.save,
                click: ifValidDocInfo(confirmSaving),
                style: 'display: none',
            });

            $saveAndPublishBtn = components.buttons.saveButton({
                id: 'save-and-publish-btn',
                text: texts.buttons.saveAndPublish,
                click: ifValidDocInfo(confirmSavingAndPublishing),
                style: 'display: none;'
            });

            $nextBtn = components.buttons.positiveButton({
                text: texts.buttons.next + ' \u2b95',
                click: moveToNextTabOrEnableOthersAndReplaceButton([$saveBtn, $saveAndPublishBtn]),
                style: 'display: none',
            });

            $usageDetails = components.link.buildLinkButton({
                title: texts.documentation,
                onClick: openDocumentationWindow
            });

            const $cancelBtn = components.buttons.negativeButton({
                text: texts.buttons.cancel,
                click: closePageInfo
            });

            $loadingAnimation = $('<div>').addClass('pageInfo loading-animation').css("display", "none");

            const buttons = [$cancelBtn, $saveBtn, $nextBtn, $loadingAnimation, $usageDetails];

            if (imcms.isVersioningAllowed && imcms.accessToPublishCurrentDoc) {
                buttons.unshift($saveAndPublishBtn);
            }

            //don't show buttons when we check a previous version of a document
            if(versionNo) return [];

            return buttons;
        }

        function documentHasNewerVersion(doc) {
            const versionId = doc ? doc.currentVersion.id : documentDTO.currentVersion.id;

            return versionId === WORKING_VERSION || (imcms.document.id === documentDTO.id && imcms.document.hasNewerVersion);
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

        function toggleButtonVisibility() {
            const isAnyVersionFieldChanged= isAnyVersionedFieldChanged();

            isAnyVersionFieldChanged ? $saveAndPublishBtn.slideDown() : $saveAndPublishBtn.slideUp();
        }

        function isAnyVersionedFieldChanged (){
            const versionedFields = lodash.flatten(windowPageInfoTabsBuilder.getAllTabBuilders().filter(tab => tab.getVersionedFields()).map(tab => tab.getVersionedFields()));

            for (let i = 0; i < versionedFields.length; i++) {
                const $field = versionedFields[i];

                if ($field.isChanged && $field.isChanged()) {
                    return true;
                }
            }

            return false;
        }

        function loadPageInfoDataFromDocumentBy(docId, docType, parentDocId) {
            const linkData = imcms.contextPath + '/api/admin/page-info?meta-id=' + documentDTO.id;

            const newDocText = documentDTO.type === docTypes.TEXT
                ? texts.newDocument.text
                : documentDTO.type === docTypes.URL
                    ? texts.newDocument.url
                    : texts.newDocument.file;

            $title.text((documentDTO.id)
                ? linkData
                : newDocText).css({'text-transform': 'initial', 'color': '#fff2f9'});

            $title.attr('href', linkData);

            windowPageInfoTabsBuilder.getAllTabBuilders().forEach((tab) => {
                if (tab.isDocumentTypeSupported(documentDTO.type)) {
                    tab.fillTabDataFromDocument(documentDTO);
                } else {
                    tab.hideTab();
                }

                if (tab.getVersionedFields()) {
                    tab.getVersionedFields().forEach(versionedField=> {
                        versionedField.on("input", toggleButtonVisibility)
                    })
                }
            });

            if (!documentDTO.id && documentDTO.type !== docTypes.TEXT) {
                setEnabledExcessTabs(false);
            }

            if (documentDTO.id || documentDTO.type === docTypes.TEXT){
                $saveBtn.show();
                if (documentHasNewerVersion()) $saveAndPublishBtn.show();
            }else {
                $nextBtn.show();
            }
        }

        function setEnabledExcessTabs(isEnabled) {
            const tabs = windowPageInfoTabsBuilder.getAllTabBuilders();

            tabs.slice(3, tabs.length).forEach(tab => {
                tab.setEnabled(isEnabled);
            });
            windowPageInfoTabsBuilder.setEnabledAdvancedButton(isEnabled);
        }

        function clearPageInfoData() {
            events.trigger("page info closed");
            $saveAndPublishBtn.css("display", "none");

            windowPageInfoTabsBuilder.getAllTabBuilders().forEach((tab) => {
                tab.clearTabData();
            });
        }

        function loadData(docId, onDocumentSavedCallback, docType, parentDocId) {
            onDocumentSaved = onDocumentSavedCallback;
            selectFirstTab(docType);
            loadPageInfoDataFromDocumentBy(docId, docType, parentDocId);
        }

        function selectFirstTab(docType) {
            const firstTab = windowPageInfoTabsBuilder.getAllTabBuilders().find(tab => tab.isDocumentTypeSupported(docType));
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
            standalone: function () {
                isStandalone = true;
                return this;
            },
            build: function (docId, onDocumentSavedCallback, docType, parentDocId) {
                onDocumentSaved = onDocumentSavedCallback;

                versionNo = new URLSearchParams(window.location.search).get("version-no");
                const requestData = {
                    docId: docId,
                    versionNo: versionNo,
	                parentDocIdentity: parentDocId,
                    type: docType
                };

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
