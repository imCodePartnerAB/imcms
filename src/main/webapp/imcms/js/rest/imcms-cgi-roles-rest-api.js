/**
 * @author Serhii Maksymchuk from Ubrainians for imCode
 * 01.08.18
 */
define('imcms-cgi-roles-rest-api', ['imcms-external-roles-rest-api'], function (externalRolesApi) {
    return new externalRolesApi('cgi');
});
