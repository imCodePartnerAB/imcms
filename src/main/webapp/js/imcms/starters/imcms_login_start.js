/**
 * @author Serhii Maksymchuk from Ubrainians for imCode
 * 13.07.18
 */
Imcms.require(['imcms', 'jquery', 'imcms-authentication', 'imcms-components-builder'], function (imcms, $, auth, components) {

    function getOnLoginProviderButtonClick(providerId) {
        return function () {
            var $thisLoginProvider = $('#' + providerId);
            if ($thisLoginProvider.hasClass('login-provider--active')) return;

            $('.login-provider--active').removeClass('login-provider--active').slideUp();
            $thisLoginProvider.addClass('login-provider--active').slideDown();
        }
    }

    $('#default-login-button').click(getOnLoginProviderButtonClick('default-login-provider'));

    var $loginPageButtonsContainer = $('#login-page-buttons');
    var $loginProvidersContainer = $('#login-providers');

    function buildLoginPageButton(authProvider) {
        return components.buttons.neutralButton({
            'class': 'imcms-info-body__button auth-provider-button',
            text: authProvider.providerName,
            click: getOnLoginProviderButtonClick(authProvider.providerId)
        });
    }

    function buildLoginProviderArea(authProvider) {
        return $('<div>', {
            'class': 'login-provider',
            id: authProvider.providerId,
            style: 'display: none;'
        });
    }

    function buildAuthProviderGUI(authProvider) {
        $loginPageButtonsContainer.append(buildLoginPageButton(authProvider));
        $loginProvidersContainer.append(buildLoginProviderArea(authProvider));
    }

    auth.getAuthProviders().done(function (authProviders) {
        if (!authProviders.length) {
            return;
        }

        authProviders.forEach(buildAuthProviderGUI);
    });

});
