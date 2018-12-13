define('imcms-link-validator-rest-api', ["imcms-rest-api"], function (rest) {
    let url = '/links';
    let api = new rest.API(url);

    api.validate = function (filterObj) {
        return rest.ajax.call({url: url, type: 'GET', json: true}, filterObj);
    };

    return api;
});