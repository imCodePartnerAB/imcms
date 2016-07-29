/**
 * Created by Serhii from Ubrainians for Imcode
 * on 28.07.16.
 */
var Linker = function (contextPath) {
    this._contextPath = contextPath;
    $.ajax({
        url: this._contextPath + this._theOnlyStringLinkInApp,
        type: "GET",
        success: function (response) {
            this._links = response;
        }.bind(this)
    }).bind(this);
};

Linker.prototype = {
    _theOnlyStringLinkInApp: "/api/links.json",
    _links: {},
    _contextPath: "",

    getLinks: function () {
        return this._links;
    }
};
