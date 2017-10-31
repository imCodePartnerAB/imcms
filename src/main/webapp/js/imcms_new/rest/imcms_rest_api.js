Imcms.define("imcms-rest-api", ["imcms", "jquery"], function (imcms, $) {

    var API_PREFIX = "/api";

    function logAjaxCall(type, url, data) {
        console.log("AJAX " + type + " call: " + url + " with data: ");
        console.log(data);
    }

    function ajax(data, callback) {
        var url = imcms.contextPath + API_PREFIX + this.url;
        logAjaxCall(this.type, url, data);

        return $.ajax({
            url: url,
            type: this.type,
            contentType: 'application/x-www-form-urlencoded; charset=UTF-8',
            data: data,
            success: callback
        });
    }

    function ajaxWithBody(data, callback) {
        var url = imcms.contextPath + API_PREFIX + this.url;
        logAjaxCall(this.type, url, data);

        return $.ajax({
            url: url,
            type: this.type,
            contentType: 'application/json; charset=utf-8',
            data: JSON.stringify(data),
            success: callback
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
        return ajax.bind({url: path, type: "DELETE"});
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