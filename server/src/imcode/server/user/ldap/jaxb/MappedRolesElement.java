package imcode.server.user.ldap.jaxb;

import javax.xml.bind.annotation.*;
import java.util.List;

public class MappedRolesElement {

    @XmlAttribute(name = "role-attribute-name", required = true)
    private String roleAttributeName;

    @XmlAttribute(name = "group-attribute-name", required = true)
    private String groupAttributeName;

    @XmlElement(name = "role")
    private List<RoleElement> rolesElements;

    public List<RoleElement> getRolesElements() {
        return rolesElements;
    }

    public String getRoleAttributeName() {
        return roleAttributeName;
    }

    public String getGroupAttributeName() {
        return groupAttributeName;
    }
}

