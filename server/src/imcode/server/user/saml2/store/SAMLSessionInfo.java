package imcode.server.user.saml2.store;

import java.util.Date;
import java.util.Map;

/**
 * Created by Shadowgun on 20.11.2014.
 */
public class SAMLSessionInfo {
    private String nameId;
    private Map<String, String> attributes;
    private Date validTo;

    public SAMLSessionInfo(String nameId, Map<String, String> attributes, Date validTo) {
        this.nameId = nameId;
        this.attributes = attributes;
        this.validTo = validTo;
    }

    public Date getValidTo() {
        return validTo;
    }

    public String getNameId() {
        return nameId;
    }

    public Map<String, String> getAttributes() {
        return attributes;
    }
    // getters should be defined below

}
