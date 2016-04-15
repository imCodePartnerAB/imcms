package imcode.server.user.saml2.store;

import java.util.Date;
import java.util.Map;

public class SAMLSessionInfo {
	private String nameId;
	private Map<String, String> attributes;
	private Date validTo;
	private String sessionIndex;

	public SAMLSessionInfo(String nameId, Map<String, String> attributes, Date validTo, String sessionIndex) {
		this.nameId = nameId;
		this.attributes = attributes;
		this.validTo = validTo;
		this.sessionIndex = sessionIndex;
	}

	public Date getValidTo() {
		return this.validTo;
	}

	public String getNameId() {
		return this.nameId;
	}

	public Map<String, String> getAttributes() {
		return this.attributes;
	}

	public String getSessionIndex() {
		return this.sessionIndex;
	}
}
