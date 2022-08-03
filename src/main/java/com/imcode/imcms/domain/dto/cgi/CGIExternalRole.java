package com.imcode.imcms.domain.dto.cgi;

import com.imcode.imcms.domain.component.cgi.CGIAuthenticationProvider;
import com.imcode.imcms.domain.dto.ExternalRole;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.UUID;

@Data
@EqualsAndHashCode(callSuper = true)
public class CGIExternalRole extends ExternalRole {

	private String displayName;

	{
		providerId = CGIAuthenticationProvider.EXTERNAL_USER_AND_ROLE_CGI;
	}

	public CGIExternalRole(String displayName) {
		setId(UUID.randomUUID().toString());
		this.displayName = displayName;
	}
}
