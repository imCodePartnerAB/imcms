/**
 * Created by Serhii Maksymchuk from Ubrainians for imCode
 * 01.11.17
 */
const rest = require('imcms-rest-api');

const url = '/images/folders';
let api = new rest.API(url);

api.canDelete = rest.ajax.bind({url: url + '/can-delete', type: "POST", json: true});

api.check = rest.ajax.bind({url: url + '/check', type: "GET", json: false});

module.exports = api;
