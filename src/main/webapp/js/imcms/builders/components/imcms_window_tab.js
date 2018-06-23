/**
 * @author Serhii Maksymchuk from Ubrainians for imCode
 * 23.06.18
 */
Imcms.define('imcms-window-tab', ['imcms-page-info-tab-form-builder'], function (tabFormBuilder) {

    var WindowTab = function (name, supportedDocumentType) {
        this.name = name;
        this.supportedDocumentType = supportedDocumentType;
    };

    WindowTab.prototype = {
        tabFormBuilder: tabFormBuilder,
        isDocumentTypeSupported: function (docType) {
            return docType === this.supportedDocumentType;
        },
        showTab: function () {
            tabFormBuilder.showTab(this.tabIndex);
        },
        hideTab: function () {
            tabFormBuilder.hideTab(this.tabIndex);
        },
        buildTab: function (index) {
            this.tabIndex = index;
            // override, return built $-object
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

    return WindowTab;
});
