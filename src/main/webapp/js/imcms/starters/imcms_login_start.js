import '../../../imcms/css/imcms_admin.css';
import '../../../css/imcms-imports_files.css';
import '../../../css/imcms-login-page.css';

var imcms = require('imcms');
var $ = require('jquery');
var auth = require('imcms-authentication');
var components = require('imcms-components-builder');
var BEM = require('imcms-bem-builder');

$(function () {
    var nextUrl = $('input[name=next_url]').val();

    function getIdentifierLink(authProvider) {
        var tail = (nextUrl ? '?next_url=' + nextUrl : '');

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

        var $alternateLoginButtons = authProviders.map(authProviderToLoginButton);

        var $alternateLoginFooter = new BEM({
            block: 'imcms-info-footer',
            elements: {
                message: components.texts.infoText('<div>', 'Alternative login:'), //todo: support localization!
                button: $alternateLoginButtons
            }
        }).buildBlockStructure('<div>', {
            'class': 'imcms-login__footer'
        });

        $('.imcms-info-body__login').append($alternateLoginFooter);
    }

    auth.getAuthProviders().done(onAuthProvidersLoaded);

    $('#Username').focus();
});
