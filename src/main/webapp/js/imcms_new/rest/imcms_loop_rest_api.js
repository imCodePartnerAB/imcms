/**
 * Created by Serhii Maksymchuk from Ubrainians for imCode
 * 29.08.17
 */
Imcms.define("imcms-loop-rest-api", ["imcms-rest-api"], function (rest) {
    var api = new rest.API("/api/loop");

    //mock data
    api.read = function () {
        return {
            done: function (callback) {
                callback({
                    docId: 1001,
                    loopId: 1,
                    entries: [{
                        no: 1,
                        enabled: true,
                        content: "Lorem ipsum... 1"
                    }, {
                        no: 2,
                        enabled: false,
                        content: "Lorem ipsum... 2"
                    }, {
                        no: 3,
                        enabled: true,
                        content: "Lorem ipsum... 3"
                    }, {
                        no: 4,
                        enabled: true,
                        content: "Lorem ipsum... 4"
                    }, {
                        no: 5,
                        enabled: false,
                        content: "Lorem ipsum... 5"
                    }, {
                        no: 6,
                        enabled: true,
                        content: "Lorem ipsum... 6"
                    }, {
                        no: 7,
                        enabled: false,
                        content: "Lorem ipsum... 7"
                    }, {
                        no: 8,
                        enabled: true,
                        content: "Lorem ipsum... 8"
                    }, {
                        no: 9,
                        enabled: true,
                        content: "Lorem ipsum... 9"
                    }]
                });
            }
        }
    };

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
