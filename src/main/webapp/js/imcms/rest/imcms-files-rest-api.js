define("imcms-files-rest-api", ["imcms-rest-api"], function (rest) {

    let url = '/files';
    let api = new rest.API(url);

    api.download = file => rest.ajax.call({url: `${url}/file/${file}`, type: 'GET', json: false});

    api.get = file => rest.ajax.call({url: `${url}/${file}`, type: 'GET', json: false});

    api.upload = file => rest.ajax.call({url: `${url}/upload/`, type: 'POST', json: false}, file);

    api.copy = pathParam => rest.ajax.call({url: `${url}/copy/`, type: 'POST', json: false}, pathParam);

    api.change = content => rest.ajax.call({url: `${url}/${content}`, type: 'PUT', json: false});

    api.move = target => rest.ajax.call({url: `${url}/move/`, type: 'PUT', json: false}, target);

    api.rename = path => rest.ajax.call({url: `${url}/rename/`, type: 'PUT', json: true}, path);

    api.delete = filePath => rest.ajax.call({url: `${url}/${filePath}`, type: 'DELETE', json: false});

    return api;
});