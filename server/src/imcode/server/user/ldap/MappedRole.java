package imcode.server.user.ldap;

public abstract class MappedRole {

    private final String roleName;

    private MappedRole(String roleName) {
        this.roleName = roleName;
    }

    public String roleName() {
        return roleName;
    }

    public static final class MappedToAttribute extends MappedRole {
        protected final String attributeName;
        protected final String attributeValue;

        public MappedToAttribute(String roleName, String attributeName, String attributeValue) {
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

    public static final class MappedToAdGroup extends MappedRole {
        private final String groupDn;

        public MappedToAdGroup(String roleName, String groupDn) {
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