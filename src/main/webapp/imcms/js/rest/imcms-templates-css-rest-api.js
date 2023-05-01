define("imcms-templates-css-rest-api", ["imcms-rest-api"], function (rest) {

	const url = '/templates/css';
	const api = new rest.API(url);

	api.get = (templateName, version) => rest.ajax.call({
		url: `${url}/${templateName}?version=${version}`,
		type: 'GET',
		json: false
	});

	api.getRevision = (templateName, revision) => rest.ajax.call({
		url: `${url}/${templateName}/history/${revision ? revision : ''}`,
		type: 'GET',
		json: false
	});

	api.getHistory = templateName => rest.ajax.call({url: `${url}/${templateName}/history`, type: 'GET', json: false});

	api.replace = (templateName, data) => rest.ajax.call({
		url: `${url}/${templateName}`,
		type: 'PUT',
		json: false,
	}, data);

	api.publish = templateName => rest.ajax.call({url: `${url}/${templateName}/publish`, type: 'POST', json: false})

	return api;
});
