/**
 * @author Serhii Maksymchuk from Ubrainians for imCode
 * 13.07.18
 */
Imcms.define('imcms-authentication', ['imcms-auth-providers-rest-api'], function (authProvidersAPI) {

    return {
        getAuthProviders: function () {
            return authProvidersAPI.read();
        }
    };
});
