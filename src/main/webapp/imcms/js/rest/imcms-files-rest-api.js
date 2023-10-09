define("imcms-files-rest-api", ["imcms-rest-api"], function (rest) {

    let url = '/files';
    let api = new rest.API(url);

    api.download = file => rest.ajax.call({url: `${url}/file/${file}`, type: 'GET', json: false});

    api.get = file => rest.ajax.call({url: `${url}/${file}`, type: 'GET', json: false});

    api.getFile = path => rest.ajax.call({url: `${url}/get-file`, type: 'GET', json: false}, path);

    api.upload = formData => rest.ajax.call({
        url: `${url}/upload/`,
        type: "POST",
        contentType: false,
        processData: false
    }, formData);

    api.copy = pathParam => rest.ajax.call({url: `${url}/copy/`, type: 'POST', json: true}, pathParam);

    api.copyWithRename = formData => rest.ajax.call({
        url: `${url}/copy/rename/`,
        type: 'POST',
        contentType: false,
        processData: false
    }, formData);

    api.change = formData => rest.ajax.call({
        url: `${url}/`,
        type: 'PUT',
        contentType: false,
        processData: false
    }, formData);

    api.move = pathParam => rest.ajax.call({url: `${url}/move/`, type: 'PUT', json: true}, pathParam);

    api.moveWithRename = formData => rest.ajax.call({
        url: `${url}/move/rename/`,
        type: 'PUT',
        contentType: false,
        processData: false
    }, formData);

    api.rename = pathParam => rest.ajax.call({url: `${url}/rename/`, type: 'PUT', json: true}, pathParam);

    api.defaultRename = pathParam => rest.ajax.call({url: `${url}/rename/default/`, type: 'PUT', json: true}, pathParam);

    api.deleteFile = sourceFile => rest.ajax.call({url: `${url}/`, type: 'DELETE', json: true}, sourceFile);

    api.getDocuments = template => rest.ajax.call({url: `${url}/docs`, type: 'GET', json: false}, template);

    api.exists = pathParam => rest.ajax.call({url: `${url}/exists/`, type: 'GET', json: false}, pathParam);

    api.existsAll = pathsParam => rest.ajax.call({url: `${url}/exists/all/`, type: 'GET', json: false}, pathsParam);

    return api;
});
