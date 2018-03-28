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

        var panels, $title, documentDTO, $saveAndPublishBtn, $tabsContainer;

        function buildPageInfoHead() {
            var $head = pageInfoWindowBuilder.buildHead();
            $title = $head.find(".imcms-head__title");

            return $head;
        }

        function showPanel(index) {
            panels.forEach(function ($panel, number) {
                $panel.css({"display": (index === number) ? "block" : "none"});
            });
        }

        function buildPageInfoTabs() {
            function getOnTabClick(index) {
                return function () {
                    $tabsContainer.find(".imcms-title--active").removeClass("imcms-title--active");
                    $(this).addClass("imcms-title--active");
                    showPanel(index);
                }
            }

            var $tabs = pageInfoTabs.tabBuilders.map(function (tabBuilder, index) {
                return {
                    tag: "<div>",
                    "class": "imcms-title",
                    attributes: {
                        "data-window-id": index,
                        text: tabBuilder.name,
                        click: getOnTabClick(index)
                    },
                    modifiers: (index === 0 ? ["active"] : [])
                };
            });

            $tabsContainer = new BEM({
                block: "imcms-tabs",
                elements: {
                    "tab": $tabs
                }
            }).buildBlockStructure("<div>");

            return new BEM({
                block: "imcms-left-side",
                elements: {
                    "tabs": $tabsContainer
                }
            }).buildBlockStructure("<div>");
        }

        function buildPageInfoPanels(docId) {
            return pageInfoTabs.tabBuilders.map(function (tabBuilder, index) {
                return tabBuilder.buildTab(index, docId).css("display", (index === 0 ? "block" : "none"));
            });
        }

        function closePageInfo() {
            pageInfoWindowBuilder.closeWindow();
        }

        function saveAndClose(onDocumentSavedCallback) {
            pageInfoTabs.tabBuilders.forEach(function (tabBuilder) {
                documentDTO = tabBuilder.saveData(documentDTO);
            });

            closePageInfo();

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
            modalWindowBuilder.buildModalWindow(texts.confirmMessage, function (isUserConfirmedSaving) {
                isUserConfirmedSaving ? saveAndClose() : "do nothing =)";
            });
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
            panels = buildPageInfoPanels(docId);

            return new BEM({
                block: "imcms-pop-up-modal",
                elements: {
                    "head": buildPageInfoHead(),
                    "left-side": buildPageInfoTabs(),
                    "right-side": $("<div>", {"class": "imcms-right-side"}).append(panels),
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
            $tabsContainer.find("[data-window-id=0]").click();
            loadPageInfoDataFromDocumentBy(docId, docType, parentDocId);
        }

        var pageInfoWindowBuilder = new WindowBuilder({
            factory: buildPageInfo,
            loadDataStrategy: loadData,
            clearDataStrategy: clearPageInfoData
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
