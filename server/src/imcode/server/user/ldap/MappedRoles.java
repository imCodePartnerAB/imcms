package imcode.server.user.ldap;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.ImmutableTable;
import com.google.common.collect.Table;
import com.imcode.imcms.api.P;

import java.util.List;
import java.util.Set;

public class MappedRoles {

    private final Set<String> rolesNames;
    private final Set<String> attributesNames;
    private final Table<String, String, String> mappedRolesNamesTable;

    public MappedRoles(List<MappedRole> mappedRolesList) {
        ImmutableTable.Builder<String, String, String> rolesNamesTableBuilder = new ImmutableTable.Builder<String, String, String>();
        ImmutableSet.Builder<String> attributesNamesBuilder = new ImmutableSet.Builder<String>();
        ImmutableSet.Builder<String> rolesNamesBuilder = new ImmutableSet.Builder<String>();

        for (MappedRole mr: mappedRolesList) {
            String name = mr.getRoleName();

            rolesNamesBuilder.add(name);
            attributesNamesBuilder.add(mr.getAttributeName());

            rolesNamesTableBuilder.put(
                    mr.getAttributeName(),
                    mr.getAttributeValue(),
                    name);
        }

        this.mappedRolesNamesTable = rolesNamesTableBuilder.build();
        this.attributesNames = attributesNamesBuilder.build();
        this.rolesNames = rolesNamesBuilder.build();
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


    public Set<String> getAttributesNames() {
        return attributesNames;
    }

    public Set<String> getRolesNames() {
        return rolesNames;
    }
}
