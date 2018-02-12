/** @namespace document.documentURL */
/**
 * Page Info tab is special for URL document type.
 *
 * @author Serhii Maksymchuk from Ubrainians for imCode
 * 26.01.18
 */
Imcms.define("imcms-url-tab-builder",
    [
        "imcms-components-builder", "imcms-document-types", "imcms-page-info-tab-form-builder", "imcms-i18n-texts"
    ],
    function (components, docTypes, tabContentBuilder, texts) {

        texts = texts.pageInfo.url;

        var tabData = {}, $urlInputContainer;

        return {
            name: texts.name,
            tabIndex: null,
            isDocumentTypeSupported: function (docType) {
                return docType === docTypes.URL;
            },
            showTab: function () {
                tabContentBuilder.showTab(this.tabIndex);
            },
            hideTab: function () {
                tabContentBuilder.hideTab(this.tabIndex);
            },
            buildTab: function (index) {
                this.tabIndex = index;

                $urlInputContainer = components.texts.textField("<div>", {
                    name: "url",
                    text: texts.title
                });

                return tabContentBuilder.buildFormBlock([$urlInputContainer], index);
            },
            fillTabDataFromDocument: function (document) {
                $urlInputContainer.setValue(document.documentURL.url);
            },
            saveData: function (document) {
                if (!this.isDocumentTypeSupported(document.type)) {
                    return document;
                }

                document.documentURL.url = $urlInputContainer.getValue();
                return document;
            },
            clearTabData: function () {
                $urlInputContainer.setValue('');
                tabData = {};
            }
        };
    }
);
