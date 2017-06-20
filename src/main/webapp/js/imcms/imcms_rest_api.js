(function (Imcms) {
    function ajax(data, callback) {
        $.ajax({
            url: this.url,
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

    function patch(path) {
        return ajax.bind({url: path, type: "PATCH"});
    }

    function remove(path) {
        return ajax.bind({url: path, type: "DELETE"});
    }

    /**
     * Simple Imcms REST API Constructor to prevent creating similar code in many places.
     *
     * Created by Serhii Maksymchuk from Ubrainians for imCode
     * 15.11.16
     */
    return Imcms.REST = {
        /**
         * Automatically applies request and response for REST methods.
         * Works only if url is constant for each instance.
         */
        API: function (path) {
            this.post = post(path);
            this.get = get(path);
            this.put = put(path);
            this.patch = patch(path);
            this["delete"] = remove(path)
        },
        post: function (path, data, callback) {
            post(path)(data, callback);
        },
        get: function (path, data, callback) {
            get(path)(data, callback);
        },
        put: function (path, data, callback) {
            put(path)(data, callback);
        },
        patch: function (path, data, callback) {
            patch(path)(data, callback);
        },
        "delete": function (path, data, callback) {
            remove(path)(data, callback);
        }
    };
})(Imcms);
