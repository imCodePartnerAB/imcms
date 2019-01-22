define("imcms-categories-rest-api", ["imcms-rest-api"], function (rest) {

    let url = '/categories';
    let api = new rest.API(url);

    api.remove = function (category) {
        return rest.ajax.call({url: url + '/' + category.id, type: 'DELETE', json: true});
    };

    api.getById = function (categoryId) {
        return rest.ajax.call({url: url + '/' + categoryId, type: 'GET', json: true});
    };

    api.getCategoriesByCategoryTypeId = function (categoryTypeId) {
        return rest.ajax.call({url: url + '/category-type/' + categoryTypeId, type: 'GET', json: true});
    };

    return api;
});
