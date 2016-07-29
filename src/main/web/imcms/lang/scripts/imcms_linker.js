/**
 * Created by Serhii from Ubrainians for Imcode
 * on 28.07.16.
 */
var Linker = function (contextPath) {
    this._contextPath = contextPath;
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
    _links: {},
    _contextPath: "",

    getLinks: function () {
        return this._links;
    }
};
