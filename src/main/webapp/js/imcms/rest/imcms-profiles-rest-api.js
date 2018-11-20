/**
 * @author Victor Pavlenko from Ubrainians for imCode
 * 21.02.18
 */
define("imcms-profiles-rest-api", ["imcms-rest-api"], function (rest) {
    let url = '/profiles';
    let api = new rest.API(url);

    api.getAllProfiles = function () {
        return rest.ajax.call({url: url, type: 'GET', json: true});
    };

    api.create = function (profile) {
        return rest.ajax.call({url: url, type: 'PUT', json: true}, profile);
    };

    api.update = function (profile) {
        return rest.ajax.call({url: url, type: 'POST', json: true}, profile);
    };

    api.deleteById = function (profile) {
        return rest.ajax.call({url: url + '/' + profile.id, type: 'DELETE', json: true});
    };

    api.getById = function (profile) {
        return rest.ajax.call({url: url + '/' + profile.id, type: 'GET', json: true});
    };

    return api;
});