package imcode.server.user.ldap;

public abstract class MappedRole {

    private final String roleName;

    private MappedRole(String roleName) {
        this.roleName = roleName;
    }

    public String roleName() {
        return roleName;
    }

    public static final class ToAttribute extends MappedRole {
        protected final String attributeName;
        protected final String attributeValue;

        public ToAttribute(String roleName, String attributeName, String attributeValue) {
            super(roleName);
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
            return String.format("MappedRole.ToAttribute{" +
                    "roleName='%s', " +
                    "attributeName='%s', " +
                    "attributeValue='%s'}", roleName(), attributeName(), attributeValue());
        }
    }

    public static final class ToAdGroup extends MappedRole {
        private final String groupDn;

        public ToAdGroup(String roleName, String groupDn) {
            super(roleName);
            this.groupDn = groupDn;
        }

        public String groupDn() {
            return groupDn;
        }

        @Override
        public String toString() {
            return String.format("MappedRole.ToAdGroup{'roleName='%s', groupDn='%s'}", roleName(), groupDn());
        }
    }
}