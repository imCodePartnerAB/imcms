/**
 * Created by Serhii from Ubrainians for Imcode
 * on 28.07.16.
 *
 * Service for correct and convenient working with links.
 * Provides context path from <base> tag.
 * Works with links from links.json file.
 *
 * @author Serhii
 */
var Linker = function () {
    this._contextPath = $("base").attr("href"); // tag 'base' should be on every page and should hold context path
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
     * Use it to get some link with context path.
     * @param {string} name - the name of url, from links.json file.
     * @param {...string} [args] - arguments for url in correct order
     * @returns {string} built link
     */
    get: function (name, args) {
        var urlArgs = this._prepareArgs(arguments);
        return this._contextPath + this._getLinkUrl(name, urlArgs);
    },

    /**
     * Use it to get some link without context path.
     * @param {string} name - the name of url, from links.json file.
     * @param {...string} [args] - arguments for url in correct order
     * @returns {string} built link
     */
    getRelative: function (name, args) {
        var urlArgs = this._prepareArgs(arguments);
        return this._getLinkUrl(name, urlArgs);
    },

    /**
     * Returns link's url by it's name and arguments.
     * @param {string} name - the name of url, from links.json file.
     * @param {string[]} [args] - arguments for url in correct order
     * @returns {string} built link's url
     */
    _getLinkUrl: function (name, args) {
        var result = this._links
            .find(function (link) {
                return (link.name == name && link.args.length == args.length);
            })
            .url;

        args.forEach(function (arg, index) {
            result = result.replace("{" + (index + 1) + "}", arg);
        });

        return result;
    },

    /**
     * Function get all arguments and returns only url's arguments.
     * First is name, and as we don't need it here we check all others args and return prepared array.
     * @param args
     * @returns {*}
     * @private
     */
    _prepareArgs: function (args) {
        return Array.prototype.slice
            .call(arguments, 1) // 0 argument is link's name, 1.. is args to url so we start from 1
            .flatMap(function (arg) { // [[]] -> []
                return arg;
            })
            .filter(function (arg) {
                return (typeof arg !== 'undefined' && arg !== null);
            });
    }
};

Array.prototype.flatMap = function (lambda) {
    return Array.prototype.concat.apply([], this.map(lambda));
};

var Imcms = {};
Imcms.Linker = new Linker();
