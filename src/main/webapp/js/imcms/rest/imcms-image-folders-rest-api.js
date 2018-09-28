/**
 * Created by Serhii Maksymchuk from Ubrainians for imCode
 * 01.11.17
 */
define("imcms-image-folders-rest-api", ["imcms-rest-api"], function (rest) {
    const url = '/images/folders';
    let api = new rest.API(url);

    api.canDelete = rest.ajax.bind({url: url + '/can-delete', type: "POST", json: true});

    api.check = rest.ajax.bind({url: url + '/check', type: "GET", json: false});

    return api;
});
