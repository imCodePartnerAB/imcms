/**
 * Created by Serhii Maksymchuk from Ubrainians for imCode
 * 05.09.17
 */
Imcms.define("imcms-menu-rest-api", ["imcms-rest-api"], function (rest) {
    var api = new rest.API("/api/menu");

    //mock data
    api.read = function () {
        return {
            done: function (callback) {
                callback([{
                    id: 1001,
                    title: "Start page",
                    children: [{
                        id: 1002,
                        title: "Second page"
                    }, {
                        id: 1003,
                        title: "Another page 1"
                    }, {
                        id: 1004,
                        title: "Another page 2"
                    }, {
                        id: 1005,
                        title: "Another page 3"
                    }]
                }, {
                    id: 1006,
                    title: "Main page",
                    children: [{
                        id: 1012,
                        title: "Third page"
                    }, {
                        id: 1013,
                        title: "Inner page 1"
                    }, {
                        id: 1014,
                        title: "Inner page 2"
                    }, {
                        id: 1015,
                        title: "Inner page 3",
                        children: [{
                            id: 1021,
                            title: "Some page"
                        }, {
                            id: 1022,
                            title: "One more page 1"
                        }, {
                            id: 1023,
                            title: "One more page 2"
                        }, {
                            id: 1124,
                            title: "One more page 3"
                        }]
                    }]
                }, {
                    id: 1007,
                    title: "Childless page"
                }, {
                    id: 1008,
                    title: "Last page",
                    children: [{
                        id: 1009,
                        title: "Some page"
                    }, {
                        id: 1010,
                        title: "One more page 1"
                    }, {
                        id: 1011,
                        title: "One more page 2"
                    }, {
                        id: 1111,
                        title: "One more page 3"
                    }]
                }]);
            }
        }
    };

    api.remove = function (data) {
        return {
            done: function (callback) {
                console.log("%c Removing (not really) menu item:", "color: blue");
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
                console.log("%c Creating (not really) new menu item:", "color: blue");
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
