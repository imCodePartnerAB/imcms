const imcms = require('imcms');
const texts = require('imcms-i18n-texts');

const twoMinutesInMillis = 2 * 60 * 1000;
const justBeforeSessionExpires = imcms.expiredSessionTimeInMillis - twoMinutesInMillis;

let sessionTimeoutId;
let saveWarningMessageId;

function onSessionTimeOut() {
    const redirectToLoginPage = confirm(texts.sessionExpiredMessage);

    if (redirectToLoginPage) window.location.href = imcms.contextPath + "/login";
}

function initOrUpdateSessionTimeout() {
    clearTimeOut(saveWarningMessageId);
    clearTimeOut(sessionTimeoutId);

    saveWarningMessageId = setTimeout(() => alert(texts.contentSaveWarningMessage), justBeforeSessionExpires);
    sessionTimeoutId = setTimeout(onSessionTimeOut, imcms.expiredSessionTimeInMillis);
}

function clearTimeOut(timeOutId) {
    if (timeOutId) clearTimeout(timeOutId);
}

module.exports = {
    initOrUpdateSessionTimeout: initOrUpdateSessionTimeout
};
