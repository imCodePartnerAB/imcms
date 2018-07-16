/**
 * @author Serhii Maksymchuk from Ubrainians for imCode
 * 13.07.18
 */
Imcms.require(
    ['imcms', 'jquery', 'imcms-authentication', 'imcms-components-builder', 'imcms-bem-builder'],
    function (imcms, $, auth, components, BEM) {

        var nextUrl = $('input[name=next_url]').val();

        function getIdentifierLink(authProvider) {
            var tail = (nextUrl ? '?next_url=' + nextUrl : '');

            return imcms.contextPath + '/api/external-identifiers/' + authProvider.providerId + tail;
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
    }
);
