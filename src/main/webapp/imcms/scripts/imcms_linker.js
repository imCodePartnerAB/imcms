(function (Imcms) {
    if (typeof Cookies !== "function") {
        throw new Error("Required 'js.cookie' library was not found!!1!");
    }

    var links = [],
        simpleURLs = {},
        linksCookiesKey = "links.json",
        contextPath = $("base").attr("href"); // tag 'base' should be on every page and should hold context path

    function getLinksFromServer() {
        var linksPath = contextPath + "/api/links"; // should be the only link in app

        $.ajax({
            url: linksPath,
            type: "GET",
            async: false, // exactly false because other modules should use Linker when links are loaded!
            success: function (response) {
                links = response;
            }
        });
    }

    function setLinksToCookies() {
        Cookies.set(linksCookiesKey, links, {
            expires: 1
        });
    }

    /**
     * Divides all links on links with args and simple URL
     */
    function divideLinks() {
        links.filter(function (link) {
            return !link.args.length;

        }).forEach(function (simpleLink) {
            links.remove(simpleLink);
            simpleURLs[simpleLink.name] = simpleLink.url;

        }.bind(this));
    }

    function loadLinks() {
        getLinksFromServer();
        setLinksToCookies();
        divideLinks();
    }

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
     * Get link with reloading if needs
     * @param name - link's name
     * @param args - arguments for link
     * @returns {Object|string}
     */
    function getOrLoadLink(name, args) {
        var link = getLink(name, args);

        if (!link) {
            loadLinks();
            link = getLink(name, args);
        }

        return link;
    }

    /**
     * Get link by name and build arguments (optional)
     * @returns {string} requested URL
     */
    function getURL(name, args) {
        args = prepareArgs(args);

        var link = getOrLoadLink(name, args);

        if (!link) {
            var errorMessage = "Can not found link with name '" + name + "'";
            errorMessage += (args.length) ? " and arguments [" + args + "]" : "";

            throw new Error(errorMessage);
        }

        if (args.length) {
            args.forEach(function (arg, index) {
                link = link.replace("{" + (index + 1) + "}", arg);
            });
        }

        return link;
    }

    /**
     * Returns URL by it's name from links.json file
     * @param {string} name - the name of URL
     * @returns {string} requested URL
     */
    function getSimpleUrl(name) {
        return simpleURLs[name];
    }

    /**
     * Returns link by name and args (optional)
     * @param {string} name - link's name
     * @param {string[]} [args] - link's arguments
     * @returns {object|string} result link or undefined if not found
     */
    function getLink(name, args) {
        if (!args || !args.length) {
            return getSimpleUrl(name);

        } else {
            var link = links.find(function (link) {
                return ((link.name == name) && (link.args.length == args.length));
            });

            return (!link || (typeof link.url === "undefined")) ? "" : link.url;
        }
    }

    /**
     * Function get all arguments and returns only url's arguments.
     * First is name, and as we don't need it here we check all others args and return prepared array.
     * @param args - all arguments, including name
     * @returns {Array} array of only url arguments
     */
    function prepareArgs(args) {
        if (!args.length) {
            return [];

        } else {
            return args.flatMap(function (arg) { // [[1],2] -> [1,2]
                    return arg;
                })
                .filter(function (arg) {
                    return ((typeof arg !== 'undefined') && (arg !== null));
                });
        }
    }

    getLinks();

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
    return Imcms.Linker = {

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
         * @param {...string} [urlArgs] - arguments for url in correct order
         * @returns {string} built link
         */
        get: function (name, urlArgs) {
            return contextPath + getURL(name, Array.prototype.slice.call(arguments, 1));
        },

        /**
         * Use it to get some link without context path.
         * @param {string} name - the name of url, from links.json file.
         * @param {...string} [argsURL] - arguments for url in correct order
         * @returns {string} built link
         */
        getRelative: function (name, argsURL) {
            return getURL(name, Array.prototype.slice.call(arguments, 1));
        }
    };
})(Imcms);
