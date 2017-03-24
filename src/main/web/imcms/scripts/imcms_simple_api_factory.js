/**
 * Builder for siple APIs
 *
 * Created by Serhii Maksymchuk from Ubrainians for imCode
 * 23.03.17
 */
(function (Imcms) {
    return Imcms.ApiFactory = {
        createAPI: function (apiLinkName) {
            var apiUrl = Imcms.Linker.get(apiLinkName),
                api = {},
                Module = function () {
                    api = new Imcms.REST.API(apiUrl);
                };

            Module.prototype = {
                read: function (callback, data) {
                    api.get((data || {}), callback);
                }
            };

            return new Module();
        }
    }
})(Imcms);
