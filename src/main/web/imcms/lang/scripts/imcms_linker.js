/**
 * Created by Serhii from Ubrainians for Imcode
 * on 28.07.16.
 *
 * Service for correct and convenient working with links
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
     * Use it to get some link.
     * @param {string} name - the name of url, from links.json file.
     * @param {...string} arg - arguments for url in correct order
     * @returns {string} built link
     */
    get(name, arg) {
        var args = Array.prototype.slice
            .call(arguments, 1) // 0 argument is link's name, 1.. is args to url so we start from 1
            .filter((arg) => typeof arg !== 'undefined' && arg !== null);

        var result = this._links
            .find((link) => link.name == name && link.args.length == args.length)
            .url;

        args.forEach((arg, index) => {
            result = result.replace("{" + (index + 1) + "}", arg);
        });

        return this._contextPath + result;
    }
}

var Imcms = {};
Imcms.Linker = new Linker();
