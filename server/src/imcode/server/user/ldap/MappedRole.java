package imcode.server.user.ldap;

public class MappedRole {

    private final String roleName;
    private final String attributeName;
    private final String attributeValue;

    public MappedRole(String roleName, String attributeName, String attributeValue) {
        this.roleName = roleName;
        this.attributeName = attributeName;
        this.attributeValue = attributeValue;
    }

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
