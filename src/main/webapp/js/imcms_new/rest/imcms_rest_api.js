Imcms.define("imcms-rest-api", ["imcms", "jquery"], function (imcms, $) {

    var API_PREFIX = "/api";

    function logAjaxRequest(type, url, data) {
        console.time(url);
        console.log("%c AJAX " + type + " call: " + url + (data ? " with request: " : ""), "color: blue;");
        data && console.log(data);
    }

    function logAjaxResponse(type, url, response) {
        console.timeEnd(url);
        console.log("%c AJAX " + type + " call done: " + url + (response ? " response: " : ""), "color: green;");
        response && console.log(response);
    }

    function ajax(data, callback) {
        var url = imcms.contextPath + API_PREFIX + this.url;
        var type = this.type;
        var contentType = (this.contentType === undefined) // exactly
            ? ('application/' + (this.json ? 'json' : 'x-www-form-urlencoded') + '; charset=UTF-8')
            : this.contentType;

        logAjaxRequest(type, url, data);

        return $.ajax({
            url: url,
            type: type,
            contentType: contentType,
            processData: this.processData,
            data: this.json ? JSON.stringify(data) : data,

            success: function (response) {
                logAjaxResponse(type, url, response);
                callback && callback(response);
            },

            error: function (response) {
                console.error(response);
            }
        });
    }

    function get(path) {
        return ajax.bind({url: path, type: "GET", json: false});
    }

    function post(path) {
        return ajax.bind({url: path, type: "POST", json: true});
    }

    function postFiles(path) {
        return ajax.bind({url: path, type: "POST", json: false, contentType: false, processData: false});
    }

    function patch(path) {
        return ajax.bind({url: path, type: "PATCH", json: true});
    }

    function remove(path) {
        return ajax.bind({url: path, type: "DELETE", json: true});
    }

    var API = function (url) {
        this.create = post(url);
        this.read = get(url);
        this.update = patch(url);
        this.remove = remove(url);
        this.postFiles = postFiles(url);
    };

    return {
        ajax: ajax,
        API: API
    }
});