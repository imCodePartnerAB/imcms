define(
    'imcms-user-properties-rest-api', ["imcms-rest-api"], function (rest) {
        let url = '/user/properties';
        let api = new rest.API(url);

        api.create = properties => rest.ajax.call({url: `${url}`, type: 'POST', json: true}, properties);

        api.getPropertiesByUserId = userId => rest.ajax.call({
            url: `${url}/${userId}`,
            type: 'GET',
            json: true
        });

        api.remove = propertyId => rest.ajax.call({
            url: `${url}/${propertyId}`,
            type: 'DELETE',
            json: false
        });

        api.updateAll = properties => rest.ajax.call({url: `${url}/update`, type: 'POST', json: true}, properties);

        return api;
    }
);