/**
 * Created by Serhii Maksymchuk from Ubrainians for imCode
 * 29.08.17
 */
Imcms.define("imcms-loop-rest-api", ["imcms-rest-api"], function (rest) {
    var api = new rest.API("/loop");

    api.update = function (data) {
        return {
            done: function (callback) {
                console.log("%c Updating (not really) loop entries:", "color: blue");
                console.log(data);

                callback.call();
            }
        }
    };

    return api;
});
