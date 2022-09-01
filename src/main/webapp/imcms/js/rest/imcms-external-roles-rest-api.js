/**
 * @author Serhii Maksymchuk from Ubrainians for imCode
 * 01.08.18
 */
define('imcms-external-roles-rest-api', ['imcms-rest-api'], function (rest) {
	return authenticationProviderId => {
		const api = new rest.API(`/external-identifiers/${authenticationProviderId}/roles`)

		api.getAuthenticationProviderId = () => authenticationProviderId;

		return api;
	}
});
