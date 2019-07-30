define("imcms-files-rest-api", ["imcms-rest-api"], function (rest) {

    const url = '/template-group';
    const api = new rest.API(url);

    return api;
});