package imcode.server.user.ldap.jaxb;

import javax.xml.bind.annotation.XmlAttribute;

/**
 * Represents mapping of a Role to an Active Directory Group
 */
public class RoleToAdGroupElement {

    @XmlAttribute(name = "role-name", required = true)
    private String roleName;

    @XmlAttribute(name = "group-dn", required = true)
    private String attributeName;

    public String getAttributeName() {
        return attributeName;
    }

    public String getRoleName() {
        return roleName;
    }
}
