/**
 * Created by Serhii from Ubrainians for Imcode
 * on 28.07.16.
 *
 * Service for correct and convenient working with links
 * @param contextPath - context path of web app, set only if present, else ignore it.
 * @constructor
 */
var Linker = function (contextPath) {
    if (contextPath) {
        this._contextPath = contextPath;
    }
    var _theOnlyStringLinkInApp = "/api/links.json";

    $.ajax({
        url: this._contextPath + _theOnlyStringLinkInApp,
        type: "GET",
        success: function (response) {
            this._links = response;
        }.bind(this)
    });
};

Linker.prototype = {
    _links: [],
    _contextPath: "",

    getLinks: function () {
        return this._links;
    },
    
    get: function () {
        this._links.forEach(function (link) {

        });
        // arg[0] is name of link
        // arg[>1] is args for link
    }
};
