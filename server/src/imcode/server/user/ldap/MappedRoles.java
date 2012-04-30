package imcode.server.user.ldap;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.ImmutableTable;
import com.google.common.collect.Table;
import com.imcode.imcms.api.P;

import java.util.List;
import java.util.Set;

public class MappedRoles {

    private final List<MappedRole> mappedRolesList;
    private final Set<String> attributesNames;
    private final Table<String, String, String> mappedRolesTable;

    public MappedRoles(List<MappedRole> mappedRolesList) {
        ImmutableTable.Builder<String, String, String> mappedRolesTableBuilder = new ImmutableTable.Builder<String, String, String>();
        ImmutableSet.Builder<String> attributesNamesBuilder = new ImmutableSet.Builder<String>();

        for (MappedRole mappedRole: mappedRolesList) {
            mappedRolesTableBuilder.put(
                    mappedRole.getAttributeName().toLowerCase(),
                    mappedRole.getAttributeValue().toLowerCase(),
                    mappedRole.getRoleName().toLowerCase());

            attributesNamesBuilder.add(mappedRole.getAttributeName());
        }

        this.mappedRolesTable = mappedRolesTableBuilder.build();
        this.attributesNames = attributesNamesBuilder.build();
        this.mappedRolesList = mappedRolesList;
    }


    public String getRoleName(String attributeName, String attributeValue) {
        return mappedRolesTable.get(attributeName.toLowerCase(), attributeValue.toLowerCase());
    }

    public Set<String> getRolesNames(List<P.P2<String, String>> keys) {
        ImmutableSet.Builder<String> rolesNamesBuilder = new ImmutableSet.Builder<String>();

        for (P.P2<String, String> key: keys) {
            String name = getRoleName(key._1(), key._2());
            if (name != null) rolesNamesBuilder.add(name);
        }

        return rolesNamesBuilder.build();
    }


    public Set<String> getAttributesNames() {
        return attributesNames;
    }
}
