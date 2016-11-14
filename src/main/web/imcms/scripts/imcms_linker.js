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
(function (Imcms) {
    var links = [];
    var simpleURLs = {};
    var linksCookiesKey = "links.json";
    var contextPath = $("base").attr("href"); // tag 'base' should be on every page and should hold context path

    /**
     * Gets links from server.
     */
    function getLinksFromServer() {
        var linksPath = contextPath + "/api/links"; // should be the only link in app

        $.ajax({
            url: linksPath,
            type: "GET",
            async: false,
            success: function (response) {
                links = response;
            }
        });
    }

    /**
     * Writes links json to cookies
     */
    function setLinksToCookies() {
        Cookies.set(linksCookiesKey, links, {
            expires: 1
        });
    }

    /**
     * Divides links with args (that needs builder) and simple URL
     */
    function divideLinks() {
        links.filter(function (link) {
            return !link.args.length;
        }).forEach(function (simpleLink) {
            links.remove(simpleLink);
            simpleURLs[simpleLink.name] = simpleLink.url;
        }.bind(this))
    }

    /**
     * Make all steps to load links from server
     */
    function loadLinks() {
        getLinksFromServer();
        setLinksToCookies();
        divideLinks();
    }

    /**
     * Gets links from cookies or from server if cookie is empty and then write cookie with 1 day expiration
     */
    function getLinks() {
        var linksFromCookies = Cookies.getJSON(linksCookiesKey);

        if (linksFromCookies && linksFromCookies.length) {
            links = linksFromCookies;
            divideLinks();

        } else {
            loadLinks();
        }
    }

    /**
     * Get simple URL or from link with builder
     * @returns {string} requested URL
     */
    function getURL(args) {
        if (args.length == 1) {
            return getSimpleUrl(args[0]);
        } else {
            var urlArgs = prepareArgs(args);

            if (!urlArgs.length) {
                return getSimpleUrl(args[0]);

            } else {
                return getLinkUrl(args[0], urlArgs);
            }
        }
    }

    /**
     * Returns URL by it's name from links.json file
     * @param {string} name - the name of URL
     * @returns {string} requested URL
     */
    function getSimpleUrl(name) {
        var link = simpleURLs[name];

        if (!link) {
            loadLinks();
            link = simpleURLs[name];
        }

        if (!link) {
            throw new Error("Can not found link with name '" + name + "'");
        }

        return link;
    }

    /**
     * Returns link's url by it's name and arguments.
     * @param {string} name - the name of url, from links.json file.
     * @param {string[]} [args] - arguments for url in correct order
     * @returns {string} built link's url
     */
    function getLinkUrl(name, args) {
        var link = getLink(name, args);

        if (!link) {
            loadLinks();
            link = getLink(name, args);
        }

        if (!link || !link.url) {
            throw new Error("Can not found link with name '" + name + "' and arguments [" + args + "]");
        }

        var result = link.url;

        args.forEach(function (arg, index) {
            result = result.replace("{" + (index + 1) + "}", arg);
        });

        return result;
    }

    /**
     * Takes link object
     * @param {string} name - link's name
     * @param {string[]} [args] - link's arguments
     * @returns {object} result link or undefined if not found
     */
    function getLink(name, args) {
        return links.find(function (link) {
            return (link.name == name && link.args.length == args.length);
        });
    }

    /**
     * Function get all arguments and returns only url's arguments.
     * First is name, and as we don't need it here we check all others args and return prepared array.
     * @param args - all arguments, including name
     * @returns {Array} array of only url arguments
     */
    function prepareArgs(args) {
        return Array.prototype.slice
            .call(args, 1) // 0 argument is link's name, 1.. is args to url so we start from 1
            .flatMap(function (arg) { // [[1],2] -> [1,2]
                return arg;
            })
            .filter(function (arg) {
                return (typeof arg !== 'undefined' && arg !== null);
            });
    }

    getLinks();

    Imcms.Linker = {

        /**
         * @returns {string} context path
         */
        getContextPath: function () {
            return contextPath;
        },

        /**
         * @returns {{}} URLs without arguments
         */
        getSimpleURLs: function () {
            return simpleURLs;
        },

        /**
         * @returns {Array} all links with args
         */
        getLinks: function () {
            return links;
        },

        /**
         * Use it to get some link with context path.
         * @param {string} name - the name of url, from links.json file.
         * @param {...string} [argsURL] - arguments for url in correct order
         * @returns {string} built link
         */
        get: function (name, argsURL) {
            return contextPath + getURL(arguments);
        },

        /**
         * Use it to get some link without context path.
         * @param {string} name - the name of url, from links.json file.
         * @param {...string} [argsURL] - arguments for url in correct order
         * @returns {string} built link
         */
        getRelative: function (name, argsURL) {
            return getURL(arguments);
        }
    };
})(Imcms);
