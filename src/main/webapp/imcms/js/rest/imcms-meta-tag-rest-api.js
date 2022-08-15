define("imcms-meta-tag-rest-api", ["imcms-rest-api"], function (rest) {
	let url = "/meta-tag";
	let api = new rest.API(url);

	api.readAll = () => rest.ajax.call({url: `${url}/all`, type: 'GET', json: false});
	api.create = (metaTagName) => rest.ajax.call({url: `${url}/${metaTagName}`, type: "POST", json: false});

	return api;
});
