package imcode.server.user.ldap;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.ImmutableTable;
import com.google.common.collect.Table;
import com.imcode.imcms.api.P;

import java.util.Collection;
import java.util.List;
import java.util.Set;

public class MappedRoles {

    private final RolesToAdGroups rolesToAdGroups;
    private final RolesToAttributes rolesToAttributes;

    public MappedRoles(Collection<MappedRole.RoleToAttribute> rolesToAttributesColl,
                       Collection<MappedRole.RoleToAdGroup> rolesToAdGroupsColl) {

        this.rolesToAttributes = new RolesToAttributes(rolesToAttributesColl);
        this.rolesToAdGroups = new RolesToAdGroups(rolesToAdGroupsColl);
    }


    public Set<String> roles() {
        ImmutableSet.Builder<String> rolesBuilder = ImmutableSet.builder();

        rolesBuilder.addAll(rolesToAdGroups.roles());
        rolesBuilder.addAll(rolesToAttributes.roles());

        return rolesBuilder.build();
    }

    public RolesToAdGroups rolesToAdGroups() {
        return rolesToAdGroups;
    }

    public RolesToAttributes rolesToAttributes() {
        return rolesToAttributes;
    }


    public static class RolesToAttributes {
        private final Set<String> roles;
        private final Table<String, String, String> mappedRolesTable;

        private RolesToAttributes(Collection<MappedRole.RoleToAttribute> rolesToAttributesColl) {
            ImmutableTable.Builder<String, String, String> rolesTableBuilder = ImmutableTable.builder();
            ImmutableSet.Builder<String> rolesBuilder = ImmutableSet.builder();

            for (MappedRole.RoleToAttribute mr : rolesToAttributesColl) {
                rolesBuilder.add(mr.role());
                rolesTableBuilder.put(mr.attributeName(), mr.attributeValue(), mr.role());
            }

            this.roles = rolesBuilder.build();
            this.mappedRolesTable = rolesTableBuilder.build();
        }

        public String role(String attributeName, String attributeValue) {
            return mappedRolesTable.get(attributeName, attributeValue);
        }

        public Set<String> roles(List<P.P2<String, String>> attributesNameValuePairs) {
            ImmutableSet.Builder<String> rolesBuilder = ImmutableSet.builder();

            for (P.P2<String, String> nameAndValue : attributesNameValuePairs) {
                String role = role(nameAndValue._1(), nameAndValue._2());
                if (role != null) rolesBuilder.add(role);
            }

            return rolesBuilder.build();
        }

        public Set<String> roles() {
            return roles;
        }

        public Set<String> attributesNames() {
            return mappedRolesTable.rowKeySet();
        }
    }


    public static class RolesToAdGroups {
        private final Table<String, String, MappedRole.RoleToAdGroup> mappedRolesTable;

        private RolesToAdGroups(Collection<MappedRole.RoleToAdGroup> rolesToAdGroupsColl) {
            ImmutableTable.Builder<String, String, MappedRole.RoleToAdGroup> mappedRolesTableBuilder = ImmutableTable.builder();
            ImmutableSet.Builder<String> groupsBuilder = ImmutableSet.builder();
            ImmutableSet.Builder<String> rolesBuilder = ImmutableSet.builder();

            for (MappedRole.RoleToAdGroup mr : rolesToAdGroupsColl) {
                rolesBuilder.add(mr.role());
                groupsBuilder.add(mr.group());

                mappedRolesTableBuilder.put(mr.role(), mr.group(), mr);
            }

            this.mappedRolesTable = mappedRolesTableBuilder.build();
        }

        public Set<String> groups() {
            return mappedRolesTable.columnKeySet();
        }

        public Set<String> roles() {
            return mappedRolesTable.rowKeySet();
        }

        public Set<String> roles(Set<String> groups) {
            ImmutableSet.Builder<String> rolesBuilder = ImmutableSet.builder();
            ImmutableSet.Builder<String> lowerCasedGroupsBuilder = ImmutableSet.builder();

            for (String group : groups) {
                lowerCasedGroupsBuilder.add(group.toLowerCase());
            }

            Set<String> lowerCasedGroups = lowerCasedGroupsBuilder.build();

            for (MappedRole.RoleToAdGroup mappedRole : mappedRolesTable.values()) {
                if (lowerCasedGroups.contains(mappedRole.group())) rolesBuilder.add(mappedRole.role());
            }

            return rolesBuilder.build();
        }
    }
}