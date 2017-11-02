Imcms.define("imcms-rest-api", ["imcms", "jquery"], function (imcms, $) {

    var API_PREFIX = "/api";

    function logAjaxRequest(type, url, data) {
        console.log("%c AJAX " + type + " call: " + url + " with request: ", "color: blue;");
        console.log(data);
    }

    function logAjaxResponse(type, url, response) {
        console.log("%c AJAX " + type + " call: " + url + " response: ", "color: blue;");
        console.log(response);
    }

    function ajax(data, callback) {
        var url = imcms.contextPath + API_PREFIX + this.url;
        var type = this.type;
        logAjaxRequest(type, url, data);

        return $.ajax({
            url: url,
            type: this.type,
            contentType: 'application/x-www-form-urlencoded; charset=UTF-8',
            data: data,
            success: function (response) {
                logAjaxResponse(type, url, response);
                callback && callback(response);
            }
        });
    }

    function ajaxWithBody(data, callback) {
        var url = imcms.contextPath + API_PREFIX + this.url;
        var type = this.type;
        logAjaxRequest(type, url, data);

        return $.ajax({
            url: url,
            type: type,
            contentType: 'application/json; charset=utf-8',
            data: JSON.stringify(data),
            success: function (response) {
                logAjaxResponse(type, url, response);
                callback && callback(response);
            }
        });
    }

    function get(path) {
        return ajax.bind({url: path, type: "GET"});
    }

    function post(path) {
        return ajaxWithBody.bind({url: path, type: "POST"});
    }

    function put(path) {
        return ajaxWithBody.bind({url: path, type: "PUT"});
    }

    function remove(path) {
        return ajaxWithBody.bind({url: path, type: "DELETE"});
    }

    var API = function (url) {
        this.create = post(url);
        this.read = get(url);
        this.update = put(url);
        this.remove = remove(url);
    };

    return {

        API: API,

        create: function (path, data, callback) {
            post(path)(data, callback);
        },

        read: function (path, data, callback) {
            get(path)(data, callback);
        },

        update: function (path, data, callback) {
            put(path)(data, callback);
        },

        remove: function (path, data, callback) {
            remove(path)(data, callback);
        }

    }
});