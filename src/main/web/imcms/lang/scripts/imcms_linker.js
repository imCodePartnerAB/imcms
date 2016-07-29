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
            this._links = response.links;
        }.bind(this)
    });
};

Linker.prototype = {
    _links: [],
    _contextPath: "",

    getLinks: function () {
        return this._links;
    }
};
