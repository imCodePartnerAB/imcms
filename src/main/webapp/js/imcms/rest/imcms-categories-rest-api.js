define("imcms-categories-rest-api", ["imcms-rest-api"], function (rest) {

    let url = '/categories';
    let api = new rest.API(url);

    api.remove = function (category) {
        return rest.ajax.call({url: url + '/' + category.id, type: 'DELETE', json: true});
    };

    return new api;
});
