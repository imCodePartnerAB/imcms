Imcms.define("imcms-roles-rest-api", ["imcms-rest-api"], function (rest) {


    var api = new rest.API("/api/roles");

    //mock data
    api.read = function () {
        return {
            done: function (callback) {
                callback([
                    {
                        id: 0,
                        name: "Superadmin"
                    },
                    {
                        id: 1,
                        name: "User"
                    },
                    {
                        id: 2,
                        name: "Useradmin"
                    },
                    {
                        id: 3,
                        name: "Test role"
                    },
                    {
                        id: 4,
                        name: "Role 4"
                    }
                ]);
            }
        }
    };

    api.remove = function (roleId) {
        return {
            done: function (callback) {
                console.log("%c Removing (not really) role with id=" + roleId, "color: blue");
                callback();
            }
        }
    };

    return api;

});
