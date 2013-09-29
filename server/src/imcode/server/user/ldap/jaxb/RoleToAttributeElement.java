package imcode.server.user.ldap.jaxb;

import javax.xml.bind.annotation.XmlAttribute;

public class RoleToAttributeElement {

    @XmlAttribute(name = "role", required = true)
    private String role;

    @XmlAttribute(name = "attribute-name", required = true)
    private String attributeName;

    @XmlAttribute(name = "attribute-value", required = true)
    private String attributeValue;

    public String attributeName() {
        return attributeName.trim().toLowerCase();
    }

    public String attributeValue() {
        return attributeValue.trim().toLowerCase();
    }

    public String role() {
        return role.trim();
    }
}
