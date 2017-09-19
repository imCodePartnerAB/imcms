/**
 * Created by Serhii Maksymchuk from Ubrainians for imCode
 * 05.09.17
 */
Imcms.define("imcms-menu-rest-api", ["imcms-rest-api"], function (Rest) {
    return new Rest.API("/menu");
});
