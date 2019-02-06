/**
 * @author Dmytro Zemlianslyi from Ubrainians for imCode
 * 29.10.18
 */

define('imcms-ip-rules-rest-api', ['imcms-rest-api'], function (rest) {
    var url = '/ip-rules';
    var api = new rest.API(url);

    api.remove = rule => rest.ajax.call({url: `${url}/${rule.id}`, type: 'DELETE', json: false});

    return api;
});
