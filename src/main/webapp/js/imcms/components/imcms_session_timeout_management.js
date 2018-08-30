define("imcms-session-timeout-management", ["imcms", "imcms-i18n-texts"], function (imcms, texts) {

    var twoMinutesInMillis = 2 * 60 * 1000;

    var sessionTimeoutId;
    var saveWarningMessageId;

    function initOrUpdateSessionTimeout() {
        clearTimeOut(saveWarningMessageId);
        clearTimeOut(sessionTimeoutId);

        saveWarningMessageId = setTimeout(function () {
            alert(texts.contentSaveWarningMessage)
        }, imcms.expiredSessionTimeInMillis - twoMinutesInMillis);

        sessionTimeoutId = setTimeout(function () {
            var redirectToLoginPage = confirm(texts.sessionExpiredMessage);

            if (redirectToLoginPage) {
                window.location.href = imcms.contextPath + "/login";
            }

        }, imcms.expiredSessionTimeInMillis);
    }

    function clearTimeOut(timeOutId) {
        if (timeOutId) {
            clearTimeout(timeOutId);
        }
    }

    return {
        initOrUpdateSessionTimeout: initOrUpdateSessionTimeout
    }
});
