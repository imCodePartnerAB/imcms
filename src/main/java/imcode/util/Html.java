package imcode.util;

import com.imcode.imcms.domain.dto.UserFormData;
import imcode.server.Imcms;
import imcode.server.ImcmsServices;
import imcode.server.document.LifeCyclePhase;
import imcode.server.user.ImcmsAuthenticatorAndUserAndRoleMapper;
import imcode.server.user.RoleDomainObject;
import imcode.server.user.RoleId;
import imcode.server.user.UserDomainObject;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.apache.commons.text.StringEscapeUtils;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public class Html {

    private static final Map<LifeCyclePhase, String> LIFE_CYCLE_PHASE_TO_TEMPLATE_PAIRS = new HashMap<>();

    static {
        LIFE_CYCLE_PHASE_TO_TEMPLATE_PAIRS.put(LifeCyclePhase.NEW, "status/new.jsp");
        LIFE_CYCLE_PHASE_TO_TEMPLATE_PAIRS.put(LifeCyclePhase.DISAPPROVED, "status/disapproved.jsp");
        LIFE_CYCLE_PHASE_TO_TEMPLATE_PAIRS.put(LifeCyclePhase.PUBLISHED, "status/published.jsp");
        LIFE_CYCLE_PHASE_TO_TEMPLATE_PAIRS.put(LifeCyclePhase.UNPUBLISHED, "status/unpublished.jsp");
        LIFE_CYCLE_PHASE_TO_TEMPLATE_PAIRS.put(LifeCyclePhase.ARCHIVED, "status/archived.jsp");
        LIFE_CYCLE_PHASE_TO_TEMPLATE_PAIRS.put(LifeCyclePhase.APPROVED, "status/approved.jsp");
    }

    private Html() {
    }

    public static <T> String createOptionList(Collection<T> allValues, Function<? super T, String[]> transformer) {
        return createOptionList(allValues, Collections.emptyList(), transformer);
    }

    public static <T> String createOptionList(Collection<T> allValues, Collection<T> selectedValues,
                                              Function<? super T, String[]> objectToStringPairTransformer) {
        StringBuilder htmlStr = new StringBuilder();

        for (T valueObject : allValues) {
            String[] valueAndNameStringPair = objectToStringPairTransformer.apply(valueObject);
            String value = valueAndNameStringPair[0];
            String name = valueAndNameStringPair[1];
            boolean valueSelected = selectedValues.contains(valueObject);
            htmlStr.append(option(value, name, valueSelected));
        }

        return htmlStr.toString();
    }

    /**
     * @deprecated
     */
    public static String createOptionList(List<String> allValues, String selected) {
        StringBuilder htmlStr = new StringBuilder();

        for (int i = 0; i < allValues.size(); i += 2) {
            String value = allValues.get(i);
            String name = allValues.get(i + 1);
            boolean valueSelected = Objects.equals(selected, value);
            htmlStr.append(option(value, name, valueSelected));
        }

        return htmlStr.toString();
    }

    public static String getStatusIconTemplatePath(LifeCyclePhase lifeCyclePhase) {
        for (Map.Entry<LifeCyclePhase, String> statusTemplatePair : LIFE_CYCLE_PHASE_TO_TEMPLATE_PAIRS.entrySet()) {
            if (lifeCyclePhase.equals(statusTemplatePair.getKey())) {
                return "/WEB-INF/templates/" + Imcms.getUser().getLanguage() + "/admin/" + statusTemplatePair.getValue();
            }
        }
        return "";
    }

    public static String option(String elementValue, String content, boolean selected) {

        StringBuilder option = new StringBuilder();

        option.append("<option value=\"").append(StringEscapeUtils.escapeHtml4(elementValue)).append("\"");
        if (selected) {
            option.append(" selected");
        }
        option.append(">").append(StringEscapeUtils.escapeHtml4(content)).append("</option>");
        return option.toString();
    }

    public static String hidden(String name, String value) {
        return "<input type=\"hidden\" name=\"" + StringEscapeUtils.escapeHtml4(name) + "\" value=\""
                + StringEscapeUtils.escapeHtml4(value)
                + "\">";
    }

    public static String radio(String name, String value, boolean selected) {
        return "<input type=\"radio\" name=\"" + StringEscapeUtils.escapeHtml4(name) + "\" value=\""
                + StringEscapeUtils.escapeHtml4(value) + "\"" + (selected ? " checked" : "") + ">";
    }

    public static String createUsersOptionList(ImcmsServices imcref) {
        UserDomainObject[] users = imcref.getImcmsAuthenticatorAndUserAndRoleMapper().getUsers(true, false);
        return createOptionList(Arrays.asList(users), user -> new String[]{"" + user.getId(), user.getLastName() + ", " + user.getFirstName()});
    }

    public static List<UserRole> getUserRoles(UserFormData userFormData) {
        if ((userFormData == null) || (userFormData.getRoleIds() == null)) return getNewUserRoles();

        final List<RoleDomainObject> roles = getAllRolesExceptUsersRole();

        final List<Integer> userRoles = IntStream.of(userFormData.getRoleIds()).boxed().collect(Collectors.toList());

        return roles.stream()
                .map(role -> new UserRole(
                        role.getId().getRoleId(),
                        role.getName(),
                        userRoles.contains(role.getId().getRoleId())
                ))
                .collect(Collectors.toList());
    }

    public static List<UserRole> getUserRoles(UserDomainObject editedUser) {
        if (editedUser == null) return getNewUserRoles();

        final List<RoleDomainObject> roles = editedUser.isUserAdminAndNotSuperAdmin()
                ? getRoles(editedUser.getUserAdminRoleIds())
                : getAllRolesExceptUsersRole();

        final List<RoleDomainObject> usersRoles = getRoles(editedUser.getRoleIds());

        return roles.stream()
                .map(role -> new UserRole(
                        role.getId().getRoleId(),
                        role.getName(),
                        usersRoles.contains(role)
                ))
                .collect(Collectors.toList());
    }

    private static List<UserRole> getNewUserRoles() {
        return getAllRolesExceptUsersRole()
                .stream()
                .map(role -> new UserRole(
                        role.getId().getRoleId(),
                        role.getName(),
                        false
                ))
                .collect(Collectors.toList());
    }

    public static List<UserRole> getUserAdministratedRoles(UserFormData userFormData) {
        final List<RoleDomainObject> allRoles = getAllRolesExceptUsersRole()
                .stream()
                .filter(role -> {
                    final RoleId roleId = role.getId();
                    return !(roleId.equals(RoleId.SUPERADMIN) || roleId.equals(RoleId.USERADMIN));
                })
                .collect(Collectors.toList());

        final int[] userAdminRoleIds = Optional.ofNullable(userFormData)
                .map(UserFormData::getUserAdminRoleIds)
                .orElse(new int[0]);

        final List<Integer> userAdminRoles = IntStream.of(userAdminRoleIds).boxed().collect(Collectors.toList());

        return allRoles.stream()
                .map(role -> new UserRole(
                        role.getId().getRoleId(),
                        role.getName(),
                        userAdminRoles.contains(role.getId().getRoleId())
                ))
                .collect(Collectors.toList());
    }

    public static List<UserRole> getUserAdministratedRoles(UserDomainObject editedUser) {
        final List<RoleDomainObject> allRoles = getAllRolesExceptUsersRole()
                .stream()
                .filter(role -> {
                    final RoleId roleId = role.getId();
                    return !(roleId.equals(RoleId.SUPERADMIN) || roleId.equals(RoleId.USERADMIN));
                })
                .collect(Collectors.toList());

        final List<RoleDomainObject> usersUserAdminRoles = (editedUser == null)
                ? Collections.emptyList()
                : getRoles(editedUser.getUserAdminRoleIds());

        return allRoles.stream()
                .map(role -> new UserRole(
                        role.getId().getRoleId(),
                        role.getName(),
                        usersUserAdminRoles.contains(role)
                ))
                .collect(Collectors.toList());
    }

    private static List<RoleDomainObject> getRoles(RoleId[] roleIds) {
        final ImcmsAuthenticatorAndUserAndRoleMapper roleMapper = Imcms.getServices()
                .getImcmsAuthenticatorAndUserAndRoleMapper();

        return Stream.of(roleIds)
                .map(roleMapper::getRole)
                .collect(Collectors.toList());
    }

    private static List<RoleDomainObject> getAllRolesExceptUsersRole() {
        final RoleDomainObject[] allRoles = Imcms.getServices()
                .getImcmsAuthenticatorAndUserAndRoleMapper()
                .getAllRolesExceptUsersRole();

        Arrays.sort(allRoles);

        return Arrays.asList(allRoles);
    }

    @Data
    @AllArgsConstructor
    public static class UserRole {
        public final int id;
        public final String name;
        public final boolean checked;
    }

}
