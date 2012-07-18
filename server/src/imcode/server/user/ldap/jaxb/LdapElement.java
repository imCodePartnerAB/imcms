package imcode.server.user.ldap.jaxb;

import javax.xml.bind.annotation.XmlElement;

public class LdapElement {

    @XmlElement(name = "mapped-roles", required = true)
    private MappedRolesElement mappedRolesElement;

    public MappedRolesElement mappedRolesElement() {
        return mappedRolesElement;
    }
}
