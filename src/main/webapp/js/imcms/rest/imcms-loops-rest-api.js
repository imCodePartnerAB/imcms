/**
 * Entry point for loop REST API
 *
 * Created by Serhii Maksymchuk from Ubrainians for imCode
 * 29.08.17
 */
define("imcms-loops-rest-api", ["imcms-rest-api"], function (rest) {
    return new rest.API("/loops");
});
