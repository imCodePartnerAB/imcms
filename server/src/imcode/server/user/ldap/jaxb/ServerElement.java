package imcode.server.user.ldap.jaxb;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "server")
public class ServerElement {

    @XmlElement(name = "ldap", required = false)
    private LdapElement ldapElement;

    public LdapElement getLdapElement() {
        return ldapElement;
    }
}
