define("imcms-link-validator-rest-api", ["imcms-rest-api"], function (rest) {
    let url = '/links';
    let api = new rest.API(url);

    api.search = function (queryObj) {
        return rest.ajax.call({url: url, type: 'GET', json: true}, queryObj); // not sure
    };

    return api;
});