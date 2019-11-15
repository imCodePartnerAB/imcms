/**
 * Created by Serhii Maksymchuk from Ubrainians for imCode
 * 08.11.17
 */
define("imcms-texts-rest-api", ["imcms-rest-api"], function (rest) {
    const apiPath = "/texts";
    const api = new rest.API(apiPath);

    // custom non-json POST call
    api.create = rest.ajax.bind({url: apiPath, type: "POST", json: false});

    return api;
});
