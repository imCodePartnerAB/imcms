Imcms.define("imcms-rest-api", ["imcms", "jquery", "imcms-ajax-request-data-decorator"], function (Imcms, $, decorate) {

    var API_PREFIX = "/api";

    function ajax(data, callback) {
        return $.ajax({
            url: Imcms.contextPath + API_PREFIX + this.url,
            type: this.type,
            data: decorate(data),
            contentType: "application/json",
            success: callback
        });
    }

    function post(path) {
        return ajax.bind({url: path, type: "POST"});
    }

    function get(path) {
        return ajax.bind({url: path, type: "GET"});
    }

    function put(path) {
        return ajax.bind({url: path, type: "PUT"});
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