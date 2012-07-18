package imcode.server.user.ldap.jaxb;

import javax.xml.bind.annotation.XmlAttribute;

public class RoleToAttributeElement {
    @XmlAttribute(name = "role-name", required = true)
    private String roleName;

    @XmlAttribute(name = "attribute-name", required = true)
    private String attributeName;

    @XmlAttribute(name = "attribute-value", required = true)
    private String attributeValue;

    public String getAttributeName() {
        return attributeName;
    }

    public String getAttributeValue() {
        return attributeValue;
    }

    public String getRoleName() {
        return roleName;
    }
}
