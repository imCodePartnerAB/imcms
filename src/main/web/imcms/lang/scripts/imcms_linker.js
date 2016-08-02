/**
 * Created by Serhii from Ubrainians for Imcode
 * on 28.07.16.
 *
 * Service for correct and convenient working with links
 * @constructor
 */
var Linker = function () {
    // tag 'base' should be on every page and should hold context path
    this._contextPath = $("base").attr("href");
    var linksPath = this._contextPath + "/api/links";

    $.ajax({
        url: linksPath,
        type: "GET",
        async: false,
        success: function (response) {
            this._links = response;
        }.bind(this)
    });
};

Linker.prototype = {
    _links: [],
    _contextPath: "",

    /**
     * @returns {Array} all links
     */
    getLinks: function () {
        return this._links;
    },

    /**
     * Use it to get some link.
     * @param {string} name - the name of url, from links.json file.
     * @param {...string} arg - arguments for url in correct order
     * @returns {string} built link
     */
    get: function (name, arg) {
        var args = Array.prototype.slice
            .call(arguments)
            .filter(function (e) {
                return (typeof e !== 'undefined' && e !== null);
            });

        var result = this._links
            .find(function (link) {
                return (link.name == name && link.args.length == args.length - 1);
            })
            .url;

        // 0 argument is link's name, 1.. is args to url so we start from 1
        for (var i = 1; i < args.length; i++) {
            result = result.replace("{" + i + "}", args[i]);
        }

        return this._contextPath + result;
    }
};

var Imcms = {};
Imcms.Linker = new Linker();
