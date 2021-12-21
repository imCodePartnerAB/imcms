/**
 * Created by Serhii Maksymchuk from Ubrainians for imCode
 * 17.08.17.
 */
const rest = require('imcms-rest-api');

const url = "/images/files"
let api = new rest.API(url);

api.moveImageFile = rest.ajax.bind({url: url + '/moveImageFile', type: "POST", json: true})

module.exports = api;