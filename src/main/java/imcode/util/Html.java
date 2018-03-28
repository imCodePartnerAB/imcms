package imcode.util;

import imcode.server.Imcms;
import imcode.server.ImcmsServices;
import imcode.server.document.DocumentDomainObject;
import imcode.server.document.LifeCyclePhase;
import imcode.server.user.UserDomainObject;
import org.apache.commons.lang.UnhandledException;
import org.apache.commons.text.StringEscapeUtils;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.*;
import java.util.function.Function;

public class Html {

    private static final Map<LifeCyclePhase, String> LIFE_CYCLE_PHASE_TO_TEMPLATE_PAIRS = new HashMap<LifeCyclePhase, String>() {
        private static final long serialVersionUID = -5142853270637150747L;

        {
            put(LifeCyclePhase.NEW, "status/new.jsp");
            put(LifeCyclePhase.DISAPPROVED, "status/disapproved.jsp");
            put(LifeCyclePhase.PUBLISHED, "status/published.jsp");
            put(LifeCyclePhase.UNPUBLISHED, "status/unpublished.jsp");
            put(LifeCyclePhase.ARCHIVED, "status/archived.jsp");
            put(LifeCyclePhase.APPROVED, "status/approved.jsp");
        }
    };

    private Html() {
    }

    public static <T> String createOptionList(Collection<T> allValues, Function<? super T, String[]> transformer) {
        return createOptionList(allValues, Collections.emptyList(), transformer);
    }

    public static <T> String createOptionList(Collection<T> allValues, Collection<T> selectedValues,
                                              Function<? super T, String[]> objectToStringPairTransformer) {
        StringBuffer htmlStr = new StringBuffer();

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
        StringBuffer htmlStr = new StringBuffer();

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
                return "/WEB-INF/templates/" + Imcms.getUser().getLanguageIso639_2() + "/admin/" + statusTemplatePair.getValue();
            }
        }
        return "";
    }

    public static String option(String elementValue, String content, boolean selected) {

        StringBuffer option = new StringBuffer();

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
        return
                "<input type=\"radio\" name=\"" + StringEscapeUtils.escapeHtml4(name) + "\" value=\""
                        + StringEscapeUtils.escapeHtml4(value) + "\"" + (selected ? " checked" : "") + ">";

    }

    /**
     * Returns the menubuttonrow
     */
    public static String getAdminButtons(UserDomainObject user, DocumentDomainObject document, HttpServletRequest request,
                                         HttpServletResponse response) {
        if (null == document || !(user.canEdit(document) || user.isUserAdminAndCanEditAtLeastOneRole() || user.canAccessAdminPages())) {
            return "";
        }

        try {
            request.setAttribute("document", document);
            request.setAttribute("user", user);
            return Utility.getContents("/imcms/" + user.getLanguageIso639_2() + "/jsp/admin/admin_panel.jsp", request, response);
        } catch (ServletException | IOException e) {
            throw new UnhandledException(e);
        }
    }

    public static String createUsersOptionList(ImcmsServices imcref) {
        UserDomainObject[] users = imcref.getImcmsAuthenticatorAndUserAndRoleMapper().getUsers(true, false);
        return createOptionList(Arrays.asList(users), user -> new String[]{"" + user.getId(), user.getLastName() + ", " + user.getFirstName()});
    }

}
