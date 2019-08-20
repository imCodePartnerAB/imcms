define("imcms-version-data-rest-api", ["imcms-rest-api"], function (rest) {

    let url = '/version';

    return new rest.API(url);
});