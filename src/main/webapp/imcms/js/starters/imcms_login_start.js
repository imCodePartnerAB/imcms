import '../../css/imcms_admin.css';
import '../../css/imcms-imports_files.css';
import '../../css/imcms-login-page.css';

const imcms = require('imcms');
const $ = require('jquery');
const auth = require('imcms-authentication');
const components = require('imcms-components-builder');
const BEM = require('imcms-bem-builder');
const modal = require("imcms-modal-window-builder");
const cookies = require('imcms-cookies');
let texts = require("imcms-i18n-texts");

$(function () {

    texts = texts.login;

    const nextUrl = $('input[name=next_url]').val();

    function getIdentifierLink(authProvider) {
        const tail = (nextUrl ? '?next_url=' + nextUrl : '');

        return imcms.contextPath + '/api/external-identifiers/login/' + authProvider.providerId + tail;
    }

    function authProviderToLoginButton(authProvider) {
        return new BEM({
            block: 'auth-provider-button',
            elements: {
                icon: $('<img>', {
                    src: imcms.contextPath + authProvider.iconPath
                })
            }
        }).buildBlockStructure('<a>', {
            href: getIdentifierLink(authProvider),
            title: authProvider.providerName
        });
    }

    function onAuthProvidersLoaded(authProviders) {
        if (!authProviders.length) {
            return;
        }

        const $alternateLoginButtons = authProviders.map(authProviderToLoginButton);

        const $alternateLoginFooter = new BEM({
            block: 'imcms-info-footer',
            elements: {
                message: components.texts.infoText('<div>', texts.alternativeLogin),
                button: $alternateLoginButtons
            }
        }).buildBlockStructure('<div>', {
            'class': 'imcms-login__footer'
        });

        $('.imcms-info-body__login').append($alternateLoginFooter);
    }

    auth.getAuthProviders()
        .done(onAuthProvidersLoaded)
        .fail(() => modal.buildErrorWindow(texts.error.loadProvidersFailed));

    $('#username').focus();

	displayRemainingTime();
});

function displayRemainingTime() {
	const $errorMsgElement = $('.imcms-login__error-msg');
	const errorMsg = $errorMsgElement.html();
	const remainingTimeMilliSeconds = $errorMsgElement.attr('data-remaining-time');

	if (remainingTimeMilliSeconds) {
		const date = new Date(Number.parseInt(remainingTimeMilliSeconds));
		$errorMsgElement.html(errorMsg + getRemainingTime(date));

		const timer = setInterval(function () {
			if (date.getMinutes() === 0 && date.getSeconds() === 0) {
				clearInterval(timer);
				$('#imcms-login-errors').slideUp();
			} else {
				$errorMsgElement.html(errorMsg + getRemainingTime(date));
			}
		}, 1000)
	}
}

function getRemainingTime(date) {
	const userCookieLanguage = cookies.getCookie('userLanguage');

	date.setSeconds(date.getSeconds() - 1);
	return date.toLocaleTimeString(userCookieLanguage, {
		minute: "2-digit",
		second: "2-digit"
	});
}