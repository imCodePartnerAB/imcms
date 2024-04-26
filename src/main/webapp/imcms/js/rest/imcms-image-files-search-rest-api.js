define("imcms-image-files-search-rest-api", ["imcms-rest-api"], function (rest) {
	const url = '/images/files/search';
	const api = new rest.API(url);

	return api;
});
