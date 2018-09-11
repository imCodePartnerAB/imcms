/**
 * Entry point for images REST API
 *
 * Created by Serhii Maksymchuk from Ubrainians for imCode
 * 05.09.17
 */
define("imcms-images-rest-api", ["imcms-rest-api"], function (rest) {
    return new rest.API("/images");
});
