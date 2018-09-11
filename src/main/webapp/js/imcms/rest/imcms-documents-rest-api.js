define('imcms-documents-rest-api', ['imcms-rest-api'], function (rest) {
    var url = '/documents';
    var api = new rest.API(url);

    api.remove = function (docId) {
        return rest.ajax.call({url: url + '/' + docId, type: 'DELETE', json: false})
    };

    return api;
});
