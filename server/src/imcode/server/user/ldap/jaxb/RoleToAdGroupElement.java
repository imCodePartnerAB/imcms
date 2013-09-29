package imcode.server.user.ldap.jaxb;

import org.apache.commons.lang.StringUtils;

import javax.xml.bind.annotation.XmlAttribute;

/**
 * Represents mapping of a Role to an Active Directory Group
 */
public class RoleToAdGroupElement {

    @XmlAttribute(name = "role", required = true)
    private String role;

    @XmlAttribute(name = "group", required = false)
    private String group;

    public String role() {
        return role.trim();
    }

    public String group() {
        return StringUtils.defaultIfBlank(StringUtils.trimToNull(group), role()).toLowerCase();
    }
}
