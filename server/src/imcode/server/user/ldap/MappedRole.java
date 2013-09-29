package imcode.server.user.ldap;

public abstract class MappedRole {

    private final String role;

    private MappedRole(String role) {
        this.role = role;
    }

    public String role() {
        return role;
    }

    public static final class RoleToAttribute extends MappedRole {
        protected final String attributeName;
        protected final String attributeValue;

        public RoleToAttribute(String role, String attributeName, String attributeValue) {
            super(role);
            this.attributeName = attributeName;
            this.attributeValue = attributeValue;
        }

        public String attributeName() {
            return attributeName;
        }

        public String attributeValue() {
            return attributeValue;
        }

        @Override
        public String toString() {
            return String.format("MappedRole.RoleToAttribute{" +
                    "role='%s', " +
                    "attributeName='%s', " +
                    "attributeValue='%s'}", role(), attributeName(), attributeValue());
        }
    }

    public static final class RoleToAdGroup extends MappedRole {
        private final String group;

        public RoleToAdGroup(String role, String group) {
            super(role);
            this.group = group;
        }

        public String group() {
            return group;
        }

        @Override
        public String toString() {
            return String.format("MappedRole.RoleToAdGroup{role='%s', group='%s'}", role(), group());
        }
    }
}