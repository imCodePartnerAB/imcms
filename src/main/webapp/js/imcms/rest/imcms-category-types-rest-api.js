define("imcms-category-types-rest-api", ["imcms-rest-api"], function (rest) {

    let url = '/category-types';
    let api = new rest.API(url);

    api.remove = function (categoryType) {
        return rest.ajax.call({url: url + '/' + categoryType.id, type: 'DELETE', json: true});
    };

    api.getById = function (categoryTypeId) {
        return rest.ajax.call({url: url + '/' + categoryTypeId, type: 'GET', json: true});
    };

    return api;
});
