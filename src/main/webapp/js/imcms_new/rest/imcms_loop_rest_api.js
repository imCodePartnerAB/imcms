/**
 * Created by Serhii Maksymchuk from Ubrainians for imCode
 * 29.08.17
 */
Imcms.define("imcms-loop-rest-api", ["imcms-rest-api"], function (rest) {
    var api = new rest.API("/loop");

    api.remove = function (data) {
        return {
            done: function (callback) {
                console.log("%c Removing (not really) loop entry:", "color: blue");
                console.log(data);

                callback({
                    code: 200,
                    status: "OK"
                });
            }
        }
    };

    api.create = function (data) {
        return {
            done: function (callback) {
                console.log("%c Creating (not really) new loop entry:", "color: blue");
                console.log(data);

                callback({
                    code: 200,
                    status: "OK"
                });
            }
        }
    };

    return api;
});
