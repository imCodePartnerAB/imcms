package imcode.server.user.ldap.jaxb;

import javax.xml.bind.annotation.XmlAttribute;

public class RoleElement {

    @XmlAttribute(required = true)
    private String name;

    @XmlAttribute(name = "attribute-name")
    private String attributeName;

    @XmlAttribute(name = "attribute-value", required = true)
    private String attributeValue;

    public String getAttributeValue() {
        return attributeValue;
    }

    public String getAttributeName() {
        return attributeName;
    }

    public String getName() {
        return name;
    }
}
