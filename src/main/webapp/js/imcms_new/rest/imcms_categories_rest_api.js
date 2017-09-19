Imcms.define("imcms-categories-rest-api", ["imcms-rest-api"], function (rest) {


    var api = new rest.API("/categories");

    //mock data
    api.read = function () {
        return {
            done: function (callback) {
                callback([
                    {
                        id: 0,
                        name: "Category secured"
                    },
                    {
                        id: 1,
                        name: "Category test"
                    },
                    {
                        id: 2,
                        name: "Category 1"
                    },
                    {
                        id: 3,
                        name: "Category 2"
                    },
                    {
                        id: 4,
                        name: "Category 3"
                    }
                ]);
            }
        }
    };

    return api;

});
