/**
 * @author Serhii Maksymchuk from Ubrainians for imCode
 * 23.06.18
 */
Imcms.define('imcms-page-info-tab', ['imcms-page-info-tab-form-builder'], function (tabFormBuilder) {

    var PageInfoTab = function (name, supportedDocumentType) {
        this.name = name;
        this.supportedDocumentType = supportedDocumentType;
    };

    PageInfoTab.prototype = {
        isDocumentTypeSupported: function (docType) {
            return docType === this.supportedDocumentType;
        },
        showTab: function () {
            tabFormBuilder.showTab(this.tabIndex);
        },
        hideTab: function () {
            tabFormBuilder.hideTab(this.tabIndex);
        },
        /**
         * @returns {Array} array of tab $elements
         */
        tabElementsFactory: function () {
            // override, return array of tab $elements
        },
        buildTab: function (index) {
            this.tabIndex = index;
            var tabElements$ = this.tabElementsFactory.apply(this, arguments);
            return tabFormBuilder.buildFormBlock(tabElements$, index);
        },
        fillTabDataFromDocument: function (document) {
            // override
        },
        saveData: function (documentDTO) {
            return documentDTO; // no additional actions by default, override if necessary
        },
        clearTabData: function () {
            // override
        }
    };

    return PageInfoTab;
});
