/**
 * @author Serhii Maksymchuk from Ubrainians for imCode
 * 23.06.18
 */
define('imcms-page-info-tab',
    [
        'imcms-window-tab-builder', 'imcms-i18n-texts'
    ], function (TabWindowBuilder, texts) {

    const PageInfoTab = function (name, supportedDocumentType) {
        TabWindowBuilder.call(this, name);
        this.supportedDocumentType = supportedDocumentType;
    };

    PageInfoTab.prototype = Object.create(TabWindowBuilder.prototype);

    PageInfoTab.prototype.isDocumentTypeSupported = function (docType) {
        return docType === this.supportedDocumentType; // or override if any doc type supported
    };
    PageInfoTab.prototype.fillTabDataFromDocument = function (document) {
        // override
    };
    PageInfoTab.prototype.saveData = function (documentDTO) {
        return documentDTO; // no additional actions by default, override if necessary
    };
    PageInfoTab.prototype.clearTabData = () => {
        // override
    };
    PageInfoTab.prototype.isValid = () => true;

    PageInfoTab.prototype.getDocLink = () => texts.pageInfo.documentationLink;

    return PageInfoTab;
});
