/**
 * Created by Serhii Maksymchuk from Ubrainians for imCode
 * 17.08.17.
 */
Imcms.define("imcms-image-files-rest-api", ["imcms-rest-api", "imcms", "jquery"], function (rest, imcms, $) {

    var apiPath = "/images/files";
    var api = new rest.API(apiPath);

    var API_PREFIX = "/api";

    function logAjaxRequest(type, url, data) {
        console.log("%c AJAX " + type + " call: " + url + " with request: ", "color: blue;");
        console.log(data);
    }

    function logAjaxResponse(type, url, response) {
        console.log("%c AJAX " + type + " call: " + url + " response: ", "color: blue;");
        console.log(response);
    }

    api.create = function ajaxFilesPostCall(data, callback) {
        var url = imcms.contextPath + API_PREFIX + apiPath;
        var type = "POST";
        logAjaxRequest(type, url, data);

        return $.ajax({
            url: url,
            type: type,
            contentType: false,
            processData: false,
            data: data,
            success: function (response) {
                logAjaxResponse(type, url, response);
                callback && callback(response);
            }
        });
    };

    api.update = function (data) {
        return {
            done: function (callback) {
                console.log("%c Updating file: ", "color: blue;");
                console.log(data);
                callback({
                    status: "OK",
                    code: 200
                });
            }
        }
    };

    return api;
});
