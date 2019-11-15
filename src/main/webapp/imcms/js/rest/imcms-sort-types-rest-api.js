define("imcms-sort-types-rest-api", ["imcms-rest-api"], function (rest) {
    let url = '/types-sort';
    let api = new rest.API(url);

    api.getSortTypes = nested => rest.ajax.call({url: `${url}`, type: 'GET', json: false}, nested);

    return api;
});