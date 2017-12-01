/**
 * Created by Serhii Maksymchuk from Ubrainians for imCode
 * 07.08.17.
 */
Imcms.define("imcms-page-info-builder",
    [
        "imcms-bem-builder", "imcms-components-builder", "imcms-documents-rest-api", "imcms-window-builder",
        "imcms-page-info-tabs-builder", "jquery"
    ],
    function (BEM, components, documentsRestApi, WindowBuilder, pageInfoTabs, $) {

        var panels, $title, documentDTO;

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

            var $tabsContainer = new BEM({
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
            shadowBuilder.closeWindow();
        }

        function saveAndClose() {
            pageInfoTabs.tabBuilders.forEach(function (tabBuilder) {
                documentDTO = tabBuilder.saveData(documentDTO);
            });

            closePageInfo();
            documentsRestApi.create(documentDTO);
        }

        function saveAndPublish() {
            // todo: save and publish
            closePageInfo();
        }

        function buildPageInfoFooterButtons() {
            var $saveBtn = components.buttons.positiveButton({
                text: "ok",
                click: saveAndClose
            });

            var $cancelBtn = components.buttons.negativeButton({
                text: "cancel",
                click: closePageInfo
            });

            var $saveAndPublishBtn = components.buttons.saveButton({
                text: "save and publish this version",
                click: saveAndPublish
            });

            return [$saveAndPublishBtn, $cancelBtn, $saveBtn];
        }

        function buildPageInfo(docId) {
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

        function loadPageInfoDataFromDocumentBy(docId) {
            documentsRestApi.read({docId: docId}).done(function (document) {
                documentDTO = document;
                $title.text((document.id) ? "document " + document.id : "new document");

                pageInfoTabs.tabBuilders.forEach(function (tab) {
                    tab.fillTabDataFromDocument(document);
                });
            });
        }

        function clearPageInfoData() {
            pageInfoTabs.tabBuilders.forEach(function (tab) {
                tab.clearTabData();
            });
        }

        function loadData(docId) {
            loadPageInfoDataFromDocumentBy(docId);
        }

        var shadowBuilder = new WindowBuilder({
            factory: function () {
                return $("<div>", {"class": "imcms-modal-layout"});
            }
        });

        var pageInfoWindowBuilder = new WindowBuilder({
            factory: buildPageInfo,
            loadDataStrategy: loadData,
            clearDataStrategy: clearPageInfoData
        });

        return {
            build: function (docId) {
                shadowBuilder.buildWindow();
                pageInfoWindowBuilder.buildWindow.applyAsync(arguments, pageInfoWindowBuilder);
            }
        }
    }
);
