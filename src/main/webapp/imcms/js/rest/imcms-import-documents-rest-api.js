define("imcms-import-documents-rest-api", ["imcms-rest-api"], function (rest) {

	const url = '/documents/import';
	const api = new rest.API(url);

	api.getUploadProgress = () => rest.ajax.call({
		url: `${url}/upload/progress`,
		type: 'GET',
		json: true
	});

	api.upload = formData => rest.ajax.call({
		url: `${url}/upload`,
		type: 'POST',
		contentType: false,
		processData: false
	}, formData);

	api.getImportProgress = () => rest.ajax.call({
		url: `${url}/progress`,
		type: 'GET',
		json: true
	});

	api.importDocuments = (params) => rest.ajax.call({
		url: `${url}`,
		type: 'POST',
		json: true
	}, params);

	api.removeAliases = (params) => rest.ajax.call({
		url: `${url}/aliases/remove`,
		type: 'POST',
		json: true
	},params);

	api.replaceAliases = (params) => rest.ajax.call({
		url: `${url}/aliases/replace`,
		type: 'POST',
		json: true
	},params);

	return api;
});
