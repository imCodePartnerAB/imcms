/**
 * Created by Serhii Maksymchuk from Ubrainians for imCode
 * 17.08.17.
 */
Imcms.define("imcms-image-files-rest-api", ["imcms-rest-api"], function (rest) {

    var apiPath = "/images/files";
    var api = new rest.API(apiPath);

    // custom non-json POST call for files
    api.create = rest.ajax.bind({url: apiPath, type: "POST", json: false, contentType: false, processData: false});

    return api;
});
