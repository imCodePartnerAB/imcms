package imcode.server.user.ldap;

import com.google.common.collect.*;
import com.imcode.imcms.api.P;

import java.util.*;

public class MappedRoles {

    public static class MappedToAttributes {
        private final Set<String> rolesNames;
        private final Table<String, String, String> mappedRolesNamesTable;

        private MappedToAttributes(Collection<MappedRole.MappedToAttribute> mappedRoles) {
            ImmutableTable.Builder<String, String, String> rolesNamesTableBuilder = new ImmutableTable.Builder<String, String, String>();
            ImmutableSet.Builder<String> rolesNamesBuilder = new ImmutableSet.Builder<String>();

            for (MappedRole.MappedToAttribute mr: mappedRoles) {
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
            ImmutableSet.Builder<String> rolesNamesBuilder = new ImmutableSet.Builder<String>();

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


    public static class ToAdGroups {
        private final Table<String, String, MappedRole.MappedToAdGroup> mappedRolesTable;

        private ToAdGroups(Collection<MappedRole.MappedToAdGroup> mappedRoles) {
            ImmutableTable.Builder<String, String, MappedRole.MappedToAdGroup> mappedRolesTableBuilder =
                    new ImmutableTable.Builder<String, String, MappedRole.MappedToAdGroup>();
            ImmutableSet.Builder<String> groupsDnsBuilder = new ImmutableSet.Builder<String>();
            ImmutableSet.Builder<String> rolesNamesBuilder = new ImmutableSet.Builder<String>();

            for (MappedRole.MappedToAdGroup mr: mappedRoles) {
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
            ImmutableSet.Builder<String> rolesNamesBuilder = new ImmutableSet.Builder<String>();
            ImmutableSet.Builder<String> lowerCasedGroupDnsBuilder = new ImmutableSet.Builder<String>();

            for (String groupDn: groupsDns) {
                lowerCasedGroupDnsBuilder.add(groupDn.toLowerCase());
            }

            Set<String> lowerCasedGroupDns = lowerCasedGroupDnsBuilder.build();

            for (MappedRole.MappedToAdGroup mappedRole: mappedRolesTable.values()) {
                if (lowerCasedGroupDns.contains(mappedRole.groupDn())) rolesNamesBuilder.add(mappedRole.roleName());
            }

            return rolesNamesBuilder.build();
        }
    }


    private final ToAdGroups toAdGroups;
    private final MappedToAttributes toAttributes;

    public MappedRoles(Collection<MappedRole.MappedToAttribute> rolesMappedToAttributes,
                       Collection<MappedRole.MappedToAdGroup> rolesMappedToAdGroups) {

        this.toAttributes = new MappedToAttributes(rolesMappedToAttributes);
        this.toAdGroups = new ToAdGroups(rolesMappedToAdGroups);
    }


    public Set<String> rolesNames() {
        ImmutableSet.Builder<String> rolesNamesBuilder = new ImmutableSet.Builder<String>();

        rolesNamesBuilder.addAll(toAdGroups.rolesNames());
        rolesNamesBuilder.addAll(toAttributes.rolesNames());

        return rolesNamesBuilder.build();
    }

    public ToAdGroups mappedToAdGroups() {
        return toAdGroups;
    }

    public MappedToAttributes mappedToAttributes() {
        return toAttributes;
    }
}