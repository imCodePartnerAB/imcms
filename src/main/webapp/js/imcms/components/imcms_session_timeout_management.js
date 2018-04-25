Imcms.define("imcms-session-timeout-management", ["imcms", "imcms-i18n-texts"], function (imcms, texts) {

    var sessionTimeoutId;

    function initOrUpdateSessionTimeout() {
        if (sessionTimeoutId) {
            clearTimeout(sessionTimeoutId);
        }

        sessionTimeoutId = setTimeout(function () {
            alert(texts.sessionExpiredMessage);
            window.location.href = imcms.contextPath + "/login";
        }, imcms.expiredSessionTimeInMillis);
    }

    return {
        initOrUpdateSessionTimeout: initOrUpdateSessionTimeout
    }
});