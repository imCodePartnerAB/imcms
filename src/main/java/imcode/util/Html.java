package imcode.util;

import imcode.server.Imcms;
import imcode.server.ImcmsServices;
import imcode.server.document.LifeCyclePhase;
import imcode.server.user.UserDomainObject;
import org.apache.commons.text.StringEscapeUtils;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;

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

}
