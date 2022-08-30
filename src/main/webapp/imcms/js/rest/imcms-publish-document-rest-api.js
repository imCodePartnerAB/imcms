define("imcms-publish-document-rest-api", ["imcms-rest-api"], function (rest) {
    let url = '/publish-document';
    let api = new rest.API(url);

    api.publish = id => rest.ajax.call({url: `${url}/${id}`, type: 'POST', json: false});

    return api;
});