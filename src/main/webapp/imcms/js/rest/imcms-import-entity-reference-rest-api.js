define("imcms-import-entity-reference-rest-api", ["imcms-rest-api"], function (rest) {

	const url = '/documents/import/references/';
	const api = new rest.API(url);

	api.getAllReferences = type => rest.ajax.call({
		url: `${url}?type=${type}`,
		type: "GET",
		json: false
	})

	return api;
});

