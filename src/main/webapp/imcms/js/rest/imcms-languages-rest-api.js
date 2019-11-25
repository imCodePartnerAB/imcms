define("imcms-languages-rest-api", ["imcms-rest-api"], function (rest) {
    let url = "/languages";
    let api = new rest.API(url);

    api.getAvailableLangs = () => rest.ajax.call({url: `${url}/available`, type: 'GET', json: false});

    return api;
});
