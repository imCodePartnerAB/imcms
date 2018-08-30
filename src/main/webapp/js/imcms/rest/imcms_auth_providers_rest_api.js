/**
 * @author Serhii Maksymchuk from Ubrainians for imCode
 * 13.07.18
 */
define('imcms-auth-providers-rest-api', ['imcms-rest-api'], function (rest) {
    return new rest.API('/auth-providers');
});
