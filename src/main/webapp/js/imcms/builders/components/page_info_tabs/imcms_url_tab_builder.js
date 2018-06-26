/**
 * Page Info tab is special for URL document type.
 *
 * @author Serhii Maksymchuk from Ubrainians for imCode
 * 26.01.18
 */
Imcms.define("imcms-url-tab-builder",
    [
        "imcms-components-builder", "imcms-document-types", "imcms-i18n-texts", "imcms-page-info-tab"
    ],
    function (components, docTypes, texts, PageInfoTab) {

        texts = texts.pageInfo.url;

        var tabData = {}, $urlInputContainer;

        var UrlTab = function (name, docType) {
            PageInfoTab.apply(this, arguments);
        };

        UrlTab.prototype = Object.create(PageInfoTab.prototype);

        UrlTab.prototype.tabElementsFactory = function () {
            return [$urlInputContainer = components.texts.textField("<div>", {
                name: "url",
                text: texts.title
            })];
        };
        UrlTab.prototype.fillTabDataFromDocument = function (document) {
            /** @namespace document.documentURL */
            $urlInputContainer.setValue(document.documentURL.url);
        };
        UrlTab.prototype.saveData = function (document) {
            if (!this.isDocumentTypeSupported(document.type)) {
                return document;
            }

            document.documentURL.url = $urlInputContainer.getValue();
            return document;
        };
        UrlTab.prototype.clearTabData = function () {
            $urlInputContainer.setValue('');
            tabData = {};
        };

        return new UrlTab(texts.name, docTypes.URL);
    }
);
