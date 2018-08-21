/**
 * Created by Serhii Maksymchuk from Ubrainians for imCode
 * 07.08.17.
 */
Imcms.define("imcms-page-info-builder",
    [
        "imcms-bem-builder", "imcms-components-builder", "imcms-documents-rest-api", "imcms-window-builder",
        "imcms-page-info-tabs-builder", "jquery", "imcms-events", "imcms", "imcms-file-doc-files-rest-api",
        "imcms-modal-window-builder", "imcms-i18n-texts"
    ],
    function (BEM, components, documentsRestApi, WindowBuilder, pageInfoTabs, $, events, imcms, docFilesAjaxApi,
              modalWindowBuilder, texts) {

        texts = texts.pageInfo;

        var panels$, $title, documentDTO, $saveAndPublishBtn;

        function buildPageInfoHead() {
            var $head = pageInfoWindowBuilder.buildHead("", closePageInfo);
            $title = $head.find(".imcms-head__title");

            return $head;
        }

        function buildPageInfoPanels(docId) {
            return pageInfoTabs.tabBuilders.map(function (tabBuilder, index) {
                var $tab = tabBuilder.buildTab(index, docId);

                (index === 0) ? $tab.slideDown() : $tab.slideUp();

                return $tab;
            });
        }

        function closePageInfo() {
            modalWindowBuilder.buildConfirmWindowWithDontShowAgain(texts.confirmMessageOnCancel, function () {
                pageInfoWindowBuilder.closeWindow()
            }, "page-info-close");
        }

        function saveAndClose(onDocumentSavedCallback) {
            pageInfoTabs.tabBuilders.forEach(function (tabBuilder) {
                documentDTO = tabBuilder.saveData(documentDTO);
            });

            pageInfoWindowBuilder.closeWindow();

            documentsRestApi.create(documentDTO).success(function (savedDoc) {

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
                    onDocumentSaved(savedDoc);
                }
            });
        }

        function saveAndPublish() {
            saveAndClose(function () {
                events.trigger("imcms-publish-new-version-current-doc");
            });
        }

        function confirmSaving() {
            modalWindowBuilder.buildConfirmWindow(texts.confirmMessage, saveAndClose);
        }

        function buildPageInfoFooterButtons() {
            var $saveBtn = components.buttons.positiveButton({
                text: texts.buttons.ok,
                click: confirmSaving
            });

            var $cancelBtn = components.buttons.negativeButton({
                text: texts.buttons.cancel,
                click: closePageInfo
            });

            $saveAndPublishBtn = components.buttons.saveButton({
                text: texts.buttons.saveAndPublish,
                click: saveAndPublish,
                style: "display: none;"
            });

            var buttons = [$cancelBtn, $saveBtn];

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

            var requestData = {
                docId: docId,
                parentDocId: parentDocId
            };

            if (docType) {
                requestData.type = docType;
            }

            documentsRestApi.read(requestData).done(function (document) {
                documentDTO = document;
                $title.text((document.id) ? (texts.document + " " + document.id) : texts.newDocument);

                pageInfoTabs.tabBuilders.forEach(function (tab) {
                    if (tab.isDocumentTypeSupported(document.type)) {
                        tab.fillTabDataFromDocument(document);
                        tab.showTab();

                    } else {
                        tab.hideTab();
                    }
                });
            });
        }

        function clearPageInfoData() {
            events.trigger("page info closed");
            $saveAndPublishBtn.css("display", "none");

            pageInfoTabs.tabBuilders.forEach(function (tab) {
                tab.clearTabData();
            });
        }

        function loadData(docId, onDocumentSavedCallback, docType, parentDocId) {
            onDocumentSaved = onDocumentSavedCallback;
            pageInfoTabs.$tabsContainer.find("[data-window-id=0]").click();
            loadPageInfoDataFromDocumentBy(docId, docType, parentDocId);
        }

        var pageInfoWindowBuilder = new WindowBuilder({
            factory: buildPageInfo,
            loadDataStrategy: loadData,
            clearDataStrategy: clearPageInfoData,
            onEscKeyPressed: closePageInfo,
            onEnterKeyPressed: confirmSaving
        });

        var onDocumentSaved;

        return {
            build: function (docId, onDocumentSavedCallback, docType, parentDocId) {
                onDocumentSaved = onDocumentSavedCallback;
                pageInfoWindowBuilder.buildWindowWithShadow.applyAsync(arguments, pageInfoWindowBuilder);
            }
        }
    }
);
