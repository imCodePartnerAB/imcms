/**
 * @author Serhii Maksymchuk from Ubrainians for imCode
 * 13.07.18
 */
Imcms.define('imcms-auth-providers-rest-api', ['imcms-rest-api'], function (rest) {
    return new rest.API('/auth-providers');
});
