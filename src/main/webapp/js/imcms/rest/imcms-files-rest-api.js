define("imcms-files-rest-api", ["imcms-rest-api"], function (rest) {

    let url = '/files';
    let api = new rest.API(url);

    api.download = file => rest.ajax.call({url: `${url}/file/${file}`, type: 'GET', json: false});

    api.upload = file => rest.ajax.call({url: `${url}/upload/`, type: 'POST', json: false}, file);

    api.create = isDirectory => rest.ajax.call({url: `${url}/`, type: 'POST', json: false}, isDirectory);

    api.copy = target => rest.ajax.call({url: `${url}/copy/`, type: 'POST', json: false}, target);

    api.replace = content => rest.ajax.call({url: `${url}/${content}`, type: 'PUT', json: false});

    api.move = target => rest.ajax.call({url: `${url}/move/`, type: 'PUT', json: false}, target);

    api.rename = name => rest.ajax.call({url: `${url}/rename/`, type: 'PUT', json: false}, name);

    api.remove = file => rest.ajax.call({url: `${url}/${file}`, type: 'DELETE', json: false});

    api.getFiles = () => rest.ajax.call({url: `${url}`, type: 'GET',});

    return api;
});