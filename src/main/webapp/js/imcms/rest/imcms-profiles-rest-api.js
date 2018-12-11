/**
 * @author Victor Pavlenko from Ubrainians for imCode
 * 21.10.18
 */
define("imcms-profiles-rest-api", ["imcms-rest-api"], function (rest) {
    let url = '/profiles';
    let api = new rest.API(url);

    api.remove = function (profile) {
        return rest.ajax.call({url: url + '/' + profile.id, type: 'DELETE', json: true});
    };

    return api;
});