/**
 * @author Serhii Maksymchuk from Ubrainians for imCode
 * 28.08.18
 */
Imcms.initSiteSpecific = function (addEventsToSpecialAdmin) {
    require("imcms-site-specific-admin-panel").init(addEventsToSpecialAdmin);
};

module.exports = { // all stuff reassigned for code highlight/completion purposes
    initSiteSpecific: Imcms.initSiteSpecific,
    expiredSessionTimeInMillis: Imcms.expiredSessionTimeInMillis,
    userLanguage: Imcms.userLanguage,
    contextPath: Imcms.contextPath,
    imagesPath: Imcms.imagesPath,
    version: Imcms.version,
    isEditMode: Imcms.isEditMode,
    isPreviewMode: Imcms.isPreviewMode,
    isVersioningAllowed: Imcms.isVersioningAllowed,
    isAdmin: Imcms.isAdmin,
    editOptions: {
        isEditDocInfo: Imcms.editOptions.isEditDocInfo,
        isEditContent: Imcms.editOptions.isEditContent,
    },
    document: {
        id: Imcms.document.id,
        type: Imcms.document.type,
        hasNewerVersion: Imcms.document.hasNewerVersion,
        headline: Imcms.document.headline,
        alias: Imcms.document.alias,
    },
    language: {
        name: Imcms.language.name,
        nativeName: Imcms.language.nativeName,
        code: Imcms.language.code,
    },
    browserInfo: {
        isIE10: (window.navigator.userAgent.indexOf("Mozilla/5.0 (compatible; MSIE 10.0;") === 0)
    }
};
