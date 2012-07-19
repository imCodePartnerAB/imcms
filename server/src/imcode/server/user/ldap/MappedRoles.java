package imcode.server.user.ldap;

import com.google.common.collect.*;
import com.imcode.imcms.api.P;

import java.util.*;

public class MappedRoles {

    private final RolesToAdGroups rolesToAdGroups;
    private final RolesToAttributes rolesToAttributes;

    public MappedRoles(Collection<MappedRole.RoleToAttribute> rolesToAttributesColl,
                       Collection<MappedRole.RoleToAdGroup> rolesToAdGroupsColl) {

        this.rolesToAttributes = new RolesToAttributes(rolesToAttributesColl);
        this.rolesToAdGroups = new RolesToAdGroups(rolesToAdGroupsColl);
    }


    public Set<String> rolesNames() {
        ImmutableSet.Builder<String> rolesNamesBuilder = ImmutableSet.builder();

        rolesNamesBuilder.addAll(rolesToAdGroups.rolesNames());
        rolesNamesBuilder.addAll(rolesToAttributes.rolesNames());

        return rolesNamesBuilder.build();
    }

    public RolesToAdGroups rolesToAdGroups() {
        return rolesToAdGroups;
    }

    public RolesToAttributes rolesToAttributes() {
        return rolesToAttributes;
    }


    public static class RolesToAttributes {
        private final Set<String> rolesNames;
        private final Table<String, String, String> mappedRolesNamesTable;

        private RolesToAttributes(Collection<MappedRole.RoleToAttribute> rolesToAttributesColl) {
            ImmutableTable.Builder<String, String, String> rolesNamesTableBuilder = ImmutableTable.builder();
            ImmutableSet.Builder<String> rolesNamesBuilder = ImmutableSet.builder();

            for (MappedRole.RoleToAttribute mr: rolesToAttributesColl) {
                rolesNamesBuilder.add(mr.roleName());
                rolesNamesTableBuilder.put(mr.attributeName(), mr.attributeValue(), mr.roleName());
            }

            this.rolesNames = rolesNamesBuilder.build();
            this.mappedRolesNamesTable = rolesNamesTableBuilder.build();
        }

        public String roleName(String attributeName, String attributeValue) {
            return mappedRolesNamesTable.get(attributeName, attributeValue);
        }

        public Set<String> rolesNames(List<P.P2<String, String>> attributesNameValuePairs) {
            ImmutableSet.Builder<String> rolesNamesBuilder = ImmutableSet.builder();

            for (P.P2<String, String> nameAndValue: attributesNameValuePairs) {
                String roleName = roleName(nameAndValue._1(), nameAndValue._2());
                if (roleName != null) rolesNamesBuilder.add(roleName);
            }

            return rolesNamesBuilder.build();
        }

        public Set<String> rolesNames() {
            return rolesNames;
        }

        public Set<String> attributesNames() {
            return mappedRolesNamesTable.rowKeySet();
        }
    }


    public static class RolesToAdGroups {
        private final Table<String, String, MappedRole.RoleToAdGroup> mappedRolesTable;

        private RolesToAdGroups(Collection<MappedRole.RoleToAdGroup> rolesToAdGroupsColl) {
            ImmutableTable.Builder<String, String, MappedRole.RoleToAdGroup> mappedRolesTableBuilder = ImmutableTable.builder();
            ImmutableSet.Builder<String> groupsDnsBuilder = ImmutableSet.builder();
            ImmutableSet.Builder<String> rolesNamesBuilder = ImmutableSet.builder();

            for (MappedRole.RoleToAdGroup mr: rolesToAdGroupsColl) {
                rolesNamesBuilder.add(mr.roleName());
                groupsDnsBuilder.add(mr.groupDn());

                mappedRolesTableBuilder.put(mr.roleName(), mr.groupDn(), mr);
            }

            this.mappedRolesTable = mappedRolesTableBuilder.build();
        }

        public Set<String> groupsDns() {
            return mappedRolesTable.columnKeySet();
        }

        public Set<String> rolesNames() {
            return mappedRolesTable.rowKeySet();
        }

        public Set<String> rolesNames(Set<String> groupsDns) {
            ImmutableSet.Builder<String> rolesNamesBuilder = ImmutableSet.builder();
            ImmutableSet.Builder<String> lowerCasedGroupDnsBuilder = ImmutableSet.builder();

            for (String groupDn: groupsDns) {
                lowerCasedGroupDnsBuilder.add(groupDn.toLowerCase());
            }

            Set<String> lowerCasedGroupDns = lowerCasedGroupDnsBuilder.build();

            for (MappedRole.RoleToAdGroup mappedRole: mappedRolesTable.values()) {
                if (lowerCasedGroupDns.contains(mappedRole.groupDn())) rolesNamesBuilder.add(mappedRole.roleName());
            }

            return rolesNamesBuilder.build();
        }
    }
}