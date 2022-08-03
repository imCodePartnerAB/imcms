package com.imcode.imcms.domain.dto.cgi;

import com.imcode.imcms.domain.component.cgi.CGIAuthenticationProvider;
import com.imcode.imcms.model.ExternalUser;
import com.imcode.imcms.model.ExternalUserDTO;
import imcode.server.ImcmsConstants;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.joda.time.DateTime;

import java.util.Map;

@Data
@AllArgsConstructor
public class CGIUserDTO implements ExternalUserDTO {
	private String nameId;
	private Map<String, String> attributes;
	private DateTime validTo;
	private String sessionIndex;

	public ExternalUser toExternalUser() {
		final ExternalUser user = new ExternalUser(CGIAuthenticationProvider.EXTERNAL_USER_AND_ROLE_CGI);

		user.setLoginName(attributes.get("Subject_SerialNumber"));
		user.setFirstName(attributes.get("Subject_GivenName"));
		user.setLastName(attributes.get("Subject_Surname"));
		user.setPassword("");
		user.setLanguageIso639_2(ImcmsConstants.ENG_CODE_ISO_639_2);
		user.setCompany(attributes.get("Subject_OrganisationName"));
		user.setSessionId(attributes.get("CertificateSerialNumber"));
		user.setCountry(attributes.get("Subject_CountryName"));

		return user;
	}
}
