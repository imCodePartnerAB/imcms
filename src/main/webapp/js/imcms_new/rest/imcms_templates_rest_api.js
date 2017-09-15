Imcms.define("imcms-templates-rest-api", ["imcms-rest-api"], function (rest) {


    var api = new rest.API("/api/templates");

    //mock data
    api.read = function () {
        return {
            done: function (callback) {
                callback([
                    {
                        id: 0,
                        name: "demo"
                    },
                    {
                        id: 1,
                        name: "test"
                    },
                    {
                        id: 2,
                        name: "main_page"
                    },
                    {
                        id: 3,
                        name: "start_page"
                    },
                    {
                        id: 4,
                        name: "startsida"
                    }
                ]);
            }
        }
    };

    return api;

});
