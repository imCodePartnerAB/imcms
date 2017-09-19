Imcms.define("imcms-category-types-rest-api", ["imcms-rest-api"], function (rest) {


    var api = new rest.API("/category-types");

    //mock data
    api.read = function () {
        return {
            done: function (callback) {
                callback([
                    {
                        id: 0,
                        name: "Single category type 1",
                        multi_select: false,
                        categories: [{
                            id: 0,
                            name: "Category test 1"
                        }, {
                            id: 1,
                            name: "Category test 2"
                        }]
                    }, {
                        id: 1,
                        name: "Single category type 2",
                        multi_select: false,
                        categories: [{
                            id: 3,
                            name: "Category test 3"
                        }, {
                            id: 4,
                            name: "Category test 4"
                        }]
                    }, {
                        id: 2,
                        name: "Multi category type 3",
                        multi_select: true,
                        categories: [{
                            id: 5,
                            name: "Category test 5"
                        }, {
                            id: 6,
                            name: "Category test 7"
                        }, {
                            id: 7,
                            name: "Category test 8"
                        }, {
                            id: 8,
                            name: "Category test 9"
                        }]
                    }
                ]);
            }
        }
    };

    return api;

});
