/**
 * Builder for simple APIs
 *
 * Created by Serhii Maksymchuk from Ubrainians for imCode
 * 23.03.17
 */
(function (Imcms) {
    return Imcms.ApiFactory = {
        createAPI: function (apiLinkName) {
            var apiUrl = Imcms.Linker.get(apiLinkName),
                api = new Imcms.REST.API(apiUrl);

            return {
                read: function (callback, data) {
                    api.get((data || {}), callback);
                }
            };
        }
    }
})(Imcms);
