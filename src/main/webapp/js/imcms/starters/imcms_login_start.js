/**
 * @author Serhii Maksymchuk from Ubrainians for imCode
 * 13.07.18
 */
Imcms.require(['imcms', 'jquery', 'imcms-authentication'], function (imcms, $, auth) {

    function buildAuthProviderGUI(authProvider) {
        console.log(authProvider);
    }

    auth.getAuthProviders().done(function (authProviders) {
        if (authProviders.length === 1) {
            return;
        }

        authProviders.forEach(buildAuthProviderGUI);
    });

});
