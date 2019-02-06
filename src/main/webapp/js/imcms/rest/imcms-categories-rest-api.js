define("imcms-categories-rest-api", ["imcms-rest-api"], function (rest) {

    let url = '/categories';
    let api = new rest.API(url);

    api.remove = category => rest.ajax.call({url: `${url}/${category.id}`, type: 'DELETE', json: true});

    api.getById = categoryId => rest.ajax.call({url: `${url}/${categoryId}`, type: 'GET', json: true});

    api.getCategoriesByCategoryTypeId = categoryTypeId => rest.ajax.call({
        url: `${url}/category-type/${categoryTypeId}`,
        type: 'GET',
        json: true
    });

    return api;
});
