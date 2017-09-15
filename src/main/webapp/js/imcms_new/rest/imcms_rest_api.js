Imcms.define("imcms-rest-api", ["jquery"], function ($) {
    function ajax(data, callback) {
        var url = this.url;
        // path variable case handling, it should be at the end of the url
        if (typeof data === "string" || typeof data === "number") {
            url += '/' + data;
            data = {};
        }
        return $.ajax({
            url: url,
            type: this.type,
            data: data,
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