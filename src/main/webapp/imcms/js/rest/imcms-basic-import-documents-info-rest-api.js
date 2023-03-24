define("imcms-basic-import-documents-info-rest-api", ["imcms-rest-api"], function (rest) {

	const url = '/documents/import/info';
	const api = new rest.API(url);

	api.update = basicImportDocument => rest.ajax.call({
		url: `${url}`,
		type: 'POST',
		json: true
	}, basicImportDocument);

	api.getAll = (pageable, startId, endId, filters) => {
		const params = new URLSearchParams();
		if (pageable) {
			pageable.page ? params.append("page", pageable.page) : '';
			pageable.size ? params.append("size", pageable.size) : '';
		}
		startId ? params.append(`startId`, startId) : '';
		endId ? params.append(`endId`, endId) : '';
		filters.forEach(filter => {
			if (filter === "excludeImported")
				params.append(`excludeImported`, true);
			if (filter === "excludeSkip")
				params.append(`excludeSkip`, true);
		})

		return rest.ajax.call({
			url: `${url}/all?${params.toString()}`,
			type: 'GET',
			contentType: false,
			processData: false
		})
	}

	return api;
});
