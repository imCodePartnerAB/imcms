package imcode.server.user.ldap.jaxb;

import javax.xml.bind.annotation.*;
import java.util.List;

public class MappedRolesElement {

    @XmlAttribute(name = "default-attribute-name", required = true)
    private String defaultAttributeName;

    @XmlElement(name = "role")
    private List<RoleElement> rolesElements;

    public List<RoleElement> getRolesElements() {
        return rolesElements;
    }

    public String getDefaultAttributeName() {
        return defaultAttributeName;
    }
}

