Imcms.define("imcms-users-rest-api", ["imcms-rest-api"], function (rest) {


    var api = new rest.API("/api/users");

    //mock data
    api.read = function () {
        return {
            done: function (callback) {
                callback([
                    {
                        id: 0,
                        username: "admin"
                    },
                    {
                        id: 1,
                        username: "test"
                    },
                    {
                        id: 2,
                        username: "user"
                    },
                    {
                        id: 3,
                        username: "Clark_Kent"
                    },
                    {
                        id: 4,
                        username: "Dark-Knight"
                    },
                    {
                        id: 5,
                        username: "Bruce_Wayne"
                    }
                ]);
            }
        }
    };

    return api;

});
