/**
 * Created by Serhii Maksymchuk from Ubrainians for imCode
 * 07.08.17.
 */
Imcms.define("imcms-page-info-builder",
    [
        "imcms-bem-builder", "imcms-components-builder", "imcms-documents-rest-api", "imcms-window-builder",
        "imcms-page-info-tabs-builder", "jquery", "imcms-events", "imcms"
    ],
    function (BEM, components, documentsRestApi, WindowBuilder, pageInfoTabs, $, events, imcms) {

        var panels, $title, documentDTO, $saveAndPublishBtn, $tabsContainer;

        function buildPageInfoHead() {
            return new BEM({
                block: "imcms-head",
                elements: {
                    "title": $title = components.texts.titleText("<div>", "")
                }
            }).buildBlockStructure("<div>");
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

            documentsRestApi.create(documentDTO).success(function (savedDocId) {

                if (documentDTO.id === imcms.document.id) {
                    events.trigger("imcms-version-modified");

                } else {
                    documentDTO.id = savedDocId;
                }

                if (onDocumentSavedCallback) {
                    onDocumentSavedCallback(documentDTO);

                } else if (onDocumentSaved) {
                    onDocumentSaved(documentDTO);
                }
            });
        }

        function saveAndPublish() {
            saveAndClose(function () {
                events.trigger("imcms-publish-new-version-current-doc");
            });
        }

        function buildPageInfoFooterButtons() {
            var $saveBtn = components.buttons.positiveButton({
                text: "ok",
                click: saveAndClose.bindArgs(null)
            });

            var $cancelBtn = components.buttons.negativeButton({
                text: "cancel",
                click: closePageInfo
            });

            $saveAndPublishBtn = components.buttons.saveButton({
                text: "save and publish this version",
                click: saveAndPublish,
                style: "display: none;"
            });

            return [$saveAndPublishBtn, $cancelBtn, $saveBtn];
        }

        function buildPageInfo(docId, onDocumentSavedCallback, docType) {
            onDocumentSaved = onDocumentSavedCallback;
            panels = buildPageInfoPanels(docId, docType);

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

        function loadPageInfoDataFromDocumentBy(docId, docType) {

            if ((docId === imcms.document.id) && imcms.document.hasNewerVersion) {
                $saveAndPublishBtn.css("display", "block");
            }

            var requestData = {docId: docId};

            if (docType) {
                requestData.type = docType;
            }

            documentsRestApi.read(requestData).done(function (document) {
                documentDTO = document;
                $title.text((document.id) ? "document " + document.id : "new document");

                pageInfoTabs.tabBuilders.forEach(function (tab) {
                    if (tab.isDocumentTypeSupported(docType)) {
                        tab.fillTabDataFromDocument(document);
                        tab.showTab();

                    } else {
                        tab.hideTab();
                    }
                });
            });
        }

        function clearPageInfoData() {
            $saveAndPublishBtn.css("display", "none");

            pageInfoTabs.tabBuilders.forEach(function (tab) {
                tab.clearTabData();
            });
        }

        function loadData(docId, onDocumentSavedCallback, docType) {
            onDocumentSaved = onDocumentSavedCallback;
            $tabsContainer.find("[data-window-id=0]").click();
            loadPageInfoDataFromDocumentBy(docId, docType);
        }

        var pageInfoWindowBuilder = new WindowBuilder({
            factory: buildPageInfo,
            loadDataStrategy: loadData,
            clearDataStrategy: clearPageInfoData
        });

        var onDocumentSaved;

        return {
            build: function (docId, onDocumentSavedCallback, docType) {
                onDocumentSaved = onDocumentSavedCallback;
                pageInfoWindowBuilder.buildWindowWithShadow.applyAsync(arguments, pageInfoWindowBuilder);
            }
        }
    }
);
