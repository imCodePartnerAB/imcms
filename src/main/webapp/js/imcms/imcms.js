/**
 * @author Serhii Maksymchuk from Ubrainians for imCode
 * 28.08.18
 */
define('imcms', [], function () {
    Imcms.initSiteSpecific = function (addEventsToSpecialAdmin) {
        var siteSpecific = require("imcms-site-specific-admin-panel");
        siteSpecific.init(addEventsToSpecialAdmin);
    };

    return Imcms;
});
