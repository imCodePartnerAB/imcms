/**
 * Created by Serhii Maksymchuk from Ubrainians for imCode
 * 07.08.17.
 */
define("imcms-page-info-builder",
    [
        "imcms-bem-builder", "imcms-components-builder", "imcms-documents-rest-api", "imcms-window-builder",
        "imcms-page-info-tabs-builder", "jquery", "imcms-events", "imcms", "imcms-file-doc-files-rest-api",
        "imcms-modal-window-builder", "imcms-i18n-texts", 'imcms-appearance-tab-builder', 'imcms-document-types'
    ],
    (BEM, components, documentsRestApi, WindowBuilder, pageInfoTabs, $, events, imcms, docFilesAjaxApi,
     modal, texts, appearanceTab, docTypes) => {

        texts = texts.pageInfo;

        const TAB_INDEX_ATTRIBUTE = 'data-window-id';

        let panels$, documentDTO, $saveAndPublishBtn, $saveBtn, $nextBtn;
        let $title = $('<a>');

        function buildPageInfoHead() {
            const $head = pageInfoWindowBuilder.buildHeadWithResizing(texts.document + ' - ', closePageInfo);
            $head.find(".imcms-head__title").append($title);

            return $head;
        }

        function buildPageInfoPanels(docId) {
            return pageInfoTabs.tabBuilders.map((tabBuilder, index) => tabBuilder.buildTab(index, docId));
        }

        function closePageInfo() {
            modal.buildConfirmWindowWithDontShowAgain(
                texts.confirmMessageOnCancel,
                () => pageInfoWindowBuilder.closeWindow(),
                "page-info-close"
            );
        }

        function saveAndClose(onDocumentSavedCallback) {
            pageInfoTabs.tabBuilders.forEach((tabBuilder) => documentDTO = tabBuilder.saveData(documentDTO));

            pageInfoWindowBuilder.closeWindow();

            //Clear modified info before send to API
            documentDTO.modified = {id: ""};

            documentsRestApi.create(documentDTO)
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

                    if (onDocumentSavedCallback) {
                        onDocumentSavedCallback(savedDoc);
                    }

                    if (onDocumentSaved) {
                        onDocumentSaved(savedDoc, true);
                    }
                })
                .fail(() => modal.buildErrorWindow(texts.error.createDocumentFailed));
        }

        function saveAndPublish() {
            saveAndClose(() => events.trigger("imcms-publish-new-version-current-doc"));
        }

        function confirmSaving() {
            modal.buildConfirmWindow(texts.confirmMessage, saveAndClose);
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

        function moveToNextTabOrEnableOthersAndReplaceButton($buttonForReplacing) {
            return function () {
                const currentTabIndex = parseInt(pageInfoTabs.getActiveTab().attr(TAB_INDEX_ATTRIBUTE));
                const $nextTab = getNextTab(currentTabIndex);
                const isEnabledNextTab = pageInfoTabs.isEnabledTabByIndex($nextTab.attr(TAB_INDEX_ATTRIBUTE));

                if (isEnabledNextTab) {
                    $nextTab.click();
                } else {
                    setEnabledExcessTabs(true);
                    $(this).hide();
                    $buttonForReplacing.show()
                }
            }
        }

        function getNextTab(currentTabIndex) {
            const nextTabIndex = currentTabIndex + 1;
            const $nextTab = pageInfoTabs.getTabByIndex(nextTabIndex);
            return $nextTab.css('display') === 'none' ? getNextTab(nextTabIndex) : $nextTab;
        }

        function buildPageInfoFooterButtons() {
            $saveBtn = components.buttons.positiveButton({
                text: texts.buttons.ok,
                click: ifValidDocInfo(confirmSaving),
                style: 'display: none',
            });

            $nextBtn = components.buttons.positiveButton({
                text: texts.buttons.next + ' \u2b95',
                click: moveToNextTabOrEnableOthersAndReplaceButton($saveBtn),
                style: 'display: none',
            });

            const $cancelBtn = components.buttons.negativeButton({
                text: texts.buttons.cancel,
                click: closePageInfo
            });

            $saveAndPublishBtn = components.buttons.saveButton({
                text: texts.buttons.saveAndPublish,
                click: ifValidDocInfo(saveAndPublish),
                style: "display: none;"
            });

            const buttons = [$cancelBtn, $saveBtn, $nextBtn];

            if (imcms.isAdmin && imcms.isVersioningAllowed) {
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
                    "left-side": pageInfoTabs.buildWindowTabs(panels$),
                    "right-side": $("<div>", {"class": "imcms-right-side"}).append(panels$),
                    "footer": $("<div>", {"class": "imcms-footer"}).append(buildPageInfoFooterButtons())
                }
            }).buildBlockStructure("<div>", {"data-menu": "pageInfo"});
        }

        function loadPageInfoDataFromDocumentBy(docId, docType, parentDocId) {

            if ((docId === imcms.document.id) && imcms.document.hasNewerVersion) {
                $saveAndPublishBtn.css("display", "block");
            }

            const requestData = {
                docId: docId,
                parentDocId: parentDocId
            };

            if (docType) {
                requestData.type = docType;
            }

            documentsRestApi.read(requestData)
                .done((document) => {
                    documentDTO = document;
                    const linkData = '/api/admin/page-info?meta-id=' + document.id;

                    const newDocText = document.type === docTypes.TEXT
                        ? texts.newDocument.text
                        : document.type === docTypes.URL
                            ? texts.newDocument.url
                            : texts.newDocument.file;

                    $title.text((document.id)
                        ? linkData
                        : newDocText).css({'text-transform': 'initial', 'color': '#fff2f9'});

                    $title.attr('href', linkData);

                    pageInfoTabs.tabBuilders.forEach((tab) => {
                        if (tab.isDocumentTypeSupported(document.type)) {
                            tab.fillTabDataFromDocument(document);
                            tab.showTab();

                        } else {
                            tab.hideTab();
                        }
                    });

                    if (!document.id && document.type !== docTypes.TEXT) {
                        setEnabledExcessTabs(false);
                    }

                    document.id || document.type === docTypes.TEXT
                        ? $saveBtn.show()
                        : $nextBtn.show();
                })
                .fail(() => modal.buildErrorWindow(texts.error.loadDocumentFailed));
        }

        function setEnabledExcessTabs(isEnabled) {
            const tabs = pageInfoTabs.tabBuilders;

            tabs.slice(3, tabs.length).forEach(tab => {
                tab.setEnabled(isEnabled);
            });
        }

        function clearPageInfoData() {
            events.trigger("page info closed");
            $saveAndPublishBtn.css("display", "none");

            pageInfoTabs.tabBuilders.forEach((tab) => {
                tab.clearTabData();
            });
        }

        function loadData(docId, onDocumentSavedCallback, docType, parentDocId) {
            onDocumentSaved = onDocumentSavedCallback;
            selectFirstTab(docType);
            loadPageInfoDataFromDocumentBy(docId, docType, parentDocId);
        }

        function selectFirstTab(docType) {
            const firstTab = pageInfoTabs.tabBuilders.find(tab => tab.isDocumentTypeSupported(docType));
            const indexOfFirstTab = firstTab ? firstTab.tabIndex : 0;
            if (indexOfFirstTab === 0) {
                pageInfoTabs.setActiveTab(indexOfFirstTab, false);
            }
            pageInfoTabs.setActiveTab(indexOfFirstTab, true);
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
                pageInfoWindowBuilder.buildWindowWithShadow.apply(pageInfoWindowBuilder, arguments);
            }
        };
    }
);
