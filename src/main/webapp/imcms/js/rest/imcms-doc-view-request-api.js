define('imcms-doc-view-request-api', ['imcms-rest-api', "imcms"], function (rest, imcms) {

    return {
        simulationDocRequest: docId => rest.ajax.call({
            url: `/${docId}`,
            type: 'GET',
            json: false,
            contentType: 'text/html;charset=UTF-8',
            withoutPrefix: true
        })
    };
});