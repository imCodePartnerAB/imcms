package imcode.server.user.ldap.jaxb;

import javax.xml.bind.annotation.XmlElement;
import java.util.Collections;
import java.util.List;

public class MappedRolesElement {

    @XmlElement(name = "role-to-ad-group")
    private List<RoleToAdGroupElement> rolesToAdGroupsElements;

    @XmlElement(name = "role-to-attribute")
    private List<RoleToAttributeElement> rolesToAttributesElements;

    public List<RoleToAdGroupElement> rolesToAdGroupsElements() {
        return rolesToAdGroupsElements != null
                ? rolesToAdGroupsElements
                : Collections.<RoleToAdGroupElement>emptyList();
    }

    public List<RoleToAttributeElement> rolesToAttributesElements() {
        return rolesToAttributesElements != null
                ? rolesToAttributesElements
                : Collections.<RoleToAttributeElement>emptyList();
    }
}
