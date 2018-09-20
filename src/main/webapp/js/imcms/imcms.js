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
        isEditDocInfo: Imcms.isEditDocInfo,
        isEditContent: Imcms.isEditContent,
    },
    document: {
        id: Imcms.id,
        type: Imcms.type,
        hasNewerVersion: Imcms.hasNewerVersion,
        headline: Imcms.headline,
        alias: Imcms.alias,
    },
    language: {
        name: Imcms.name,
        nativeName: Imcms.nativeName,
        code: Imcms.code,
    },
    browserInfo: {
        isIE10: (window.navigator.userAgent.indexOf("Mozilla/5.0 (compatible; MSIE 10.0;") === 0)
    }
};
