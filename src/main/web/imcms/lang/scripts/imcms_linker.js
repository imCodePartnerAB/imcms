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
    this._getLinks();
};

Linker.prototype = {
    _links: [],
    _simpleURLs: {},
    _contextPath: "",
    _linksCookiesKey: "links.json",

    /**
     * Gets links from server.
     * @private
     */
    _getLinksFromServer: function () {
        var linksPath = this._contextPath + "/api/links"; // should be the only link in app

        $.ajax({
            url: linksPath,
            type: "GET",
            async: false,
            success: function (response) {
                this._links = response;
            }.bind(this)
        });
    },

    /**
     * Writes links json to cookies
     * @private
     */
    _setLinksToCookies: function () {
        Cookies.set(this._linksCookiesKey, this._links, {
            expires: 1
        });
    },

    /**
     * Divides links with args (that needs builder) and simple URL
     * @private
     */
    _divideLinks: function () {
        this._links.filter(function (link) {
            return !link.args.length;
        }).forEach(function (simpleLink) {
            this._links.remove(simpleLink);
            this._simpleURLs[simpleLink.name] = simpleLink.url;
        }.bind(this))
    },

    /**
     * Make all steps to load links from server
     * @private
     */
    _loadLinks: function () {
        this._getLinksFromServer();
        this._setLinksToCookies();
        this._divideLinks();
    },

    /**
     * Gets links from cookies or from server if cookie is empty and then write cookie with 1 day expiration
     * @private
     */
    _getLinks: function () {
        var linksFromCookies = Cookies.getJSON(this._linksCookiesKey);

        if (linksFromCookies && linksFromCookies.length) {
            this._links = linksFromCookies;
            this._divideLinks();

        } else {
            this._loadLinks();
        }
    },

    /**
     * @returns {string} context path
     */
    getContextPath: function () {
        return this._contextPath;
    },

    /**
     * @returns {Linker._simpleURLs|{}} URLs without arguments
     */
    getSimpleURLs: function () {
        return this._simpleURLs;
    },

    /**
     * @returns {Array} all links with args
     */
    getLinks: function () {
        return this._links;
    },

    /**
     * Use it to get some link with context path.
     * @param {string} name - the name of url, from links.json file.
     * @param {...string} [argsURL] - arguments for url in correct order
     * @returns {string} built link
     */
    get: function (name, argsURL) {
        return this._contextPath + this._getURL(arguments);
    },

    /**
     * Use it to get some link without context path.
     * @param {string} name - the name of url, from links.json file.
     * @param {...string} [argsURL] - arguments for url in correct order
     * @returns {string} built link
     */
    getRelative: function (name, argsURL) {
        return this._getURL(arguments);
    },

    /**
     * Get simple URL or from link with builder
     * @returns {string} requested URL
     * @private
     */
    _getURL: function (args) {
        if (args.length == 1) {
            return this._getSimpleUrl(args[0]);
        } else {
            var urlArgs = this._prepareArgs(args);

            if (!urlArgs.length) {
                return this._getSimpleUrl(args[0]);

            } else {
                return this._getLinkUrl(args[0], urlArgs);
            }
        }
    },

    /**
     * Returns URL by it's name from links.json file
     * @param {string} name - the name of URL
     * @returns {string} requested URL
     * @private
     */
    _getSimpleUrl: function (name) {
        var link = this._simpleURLs[name];

        if (!link) {
            this._loadLinks();
            link = this._simpleURLs[name];
        }

        if (!link) {
            throw new Error("Can not found link with name '" + name + "'");
        }

        return link;
    },

    /**
     * Returns link's url by it's name and arguments.
     * @param {string} name - the name of url, from links.json file.
     * @param {string[]} [args] - arguments for url in correct order
     * @returns {string} built link's url
     * @private
     */
    _getLinkUrl: function (name, args) {
        var link = this._getLink(name, args);

        if (!link) {
            this._loadLinks();
            link = this._getLink(name, args);
        }

        if (!link || !link.url) {
            throw new Error("Can not found link with name '" + name + "' and arguments [" + args + "]");
        }

        var result = link.url;

        args.forEach(function (arg, index) {
            result = result.replace("{" + (index + 1) + "}", arg);
        });

        return result;
    },

    /**
     * Takes link object
     * @param {string} name - link's name
     * @param {string[]} [args] - link's arguments
     * @returns {object} result link or undefined if not found
     * @private
     */
    _getLink: function (name, args) {
        return this._links.find(function (link) {
            return (link.name == name && link.args.length == args.length);
        });
    },

    /**
     * Function get all arguments and returns only url's arguments.
     * First is name, and as we don't need it here we check all others args and return prepared array.
     * @param args - all arguments, including name
     * @returns {Array} array of only url arguments
     * @private
     */
    _prepareArgs: function (args) {
        return Array.prototype.slice
            .call(args, 1) // 0 argument is link's name, 1.. is args to url so we start from 1
            .flatMap(function (arg) { // [[1],2] -> [1,2]
                return arg;
            })
            .filter(function (arg) {
                return (typeof arg !== 'undefined' && arg !== null);
            });
    }
};

/**
 * Flatmap function, as in Java Stream API
 * @param {function} lambda - lambda to be used for each element
 * @returns {Array} - resulted array
 */
Array.prototype.flatMap = function (lambda) {
    return Array.prototype.concat.apply([], this.map(lambda));
};

/**
 * Extension for Array to made convenient remove
 * @param {*} value to remove from array
 */
Array.prototype.remove = function (value) {
    while (true) {
        var index = this.indexOf(value);
        if (index < 0) return;
        this.splice(index, 1);
    }
};

var Imcms = {};
Imcms.Linker = new Linker();
