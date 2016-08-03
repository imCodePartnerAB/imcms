Array.prototype.flatMap = function(lambda) {
    return Array.prototype.concat.apply([], this.map(lambda));
};

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
class Linker {
    constructor() {
        // tag 'base' should be on every page and should hold context path
        this._contextPath = $("base").attr("href");
        var linksPath = this._contextPath + "/api/links";

        $.ajax({
            url: linksPath,
            type: "GET",
            async: false,
            success: (response) => this._links = response
        });
    }

    /**
     * @returns {Array} all links
     */
    get links() {
        return this._links;
    }

    get contextPath() {
        return this._contextPath;
    }

    /**
     * Use it to get some link with context path.
     * @param {string} name - the name of url, from links.json file.
     * @param {...string} [args] - arguments for url in correct order
     * @returns {string} built link
     */
    get(name, ...args) {
        return this._contextPath + this.getRelative(name, args);
    }

    /**
     * Use it to get some link without context path.
     * @param {string} name - the name of url, from links.json file.
     * @param {...string} [args] - arguments for url in correct order
     * @returns {string} built link
     */
    getRelative(name, ...args) {
        args = args.flatMap((arg) => arg); // [[]] -> []
        args = args.filter((arg) => typeof arg !== 'undefined' && arg !== null);

        var result = this._links
            .find((link) => link.name == name && link.args.length == args.length)
            .url;

        args.forEach((arg, index) => {
            result = result.replace("{" + (index + 1) + "}", arg);
        });

        return result;
    }
}

var Imcms = {};
Imcms.Linker = new Linker();
