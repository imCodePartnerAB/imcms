/**
 * Entry point for images REST API
 *
 * Created by Serhii Maksymchuk from Ubrainians for imCode
 * 05.09.17
 */
define('imcms-images-rest-api', ['imcms-rest-api'], function (rest) {
	let url = '/images';
	let api = new rest.API(url);

	api.getLoopImages = (requestDTO) => rest.ajax.call({url: `${url}/loop`, type: 'GET', json: false}, requestDTO)

	return api;
})