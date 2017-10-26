package imcode.util;

import com.imcode.imcms.mapping.CategoryMapper;
import com.imcode.imcms.mapping.DocumentMapper;
import com.imcode.imcms.servlet.admin.AdminDoc;
import com.imcode.imcms.util.l10n.LocalizedMessage;
import imcode.server.Imcms;
import imcode.server.ImcmsServices;
import imcode.server.document.CategoryDomainObject;
import imcode.server.document.CategoryTypeDomainObject;
import imcode.server.document.DocumentDomainObject;
import imcode.server.document.LifeCyclePhase;
import imcode.server.user.UserDomainObject;
import org.apache.commons.lang.UnhandledException;
import org.apache.commons.text.StringEscapeUtils;
import org.apache.oro.text.perl.Perl5Util;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.*;
import java.util.function.Function;

public class Html {

    private static final Object[][] STATUS_TEMPLATE_PAIRS = {
            {LifeCyclePhase.NEW, "status/new.jsp"},
            {LifeCyclePhase.DISAPPROVED, "status/disapproved.jsp"},
            {LifeCyclePhase.PUBLISHED, "status/published.jsp"},
            {LifeCyclePhase.UNPUBLISHED, "status/unpublished.jsp"},
            {LifeCyclePhase.ARCHIVED, "status/archived.jsp"},
            {LifeCyclePhase.APPROVED, "status/approved.jsp"},
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

    public static String createOptionListOfCategoriesOfTypeForDocument(CategoryMapper categoryMapper,
                                                                       CategoryTypeDomainObject categoryType,
                                                                       DocumentDomainObject document, HttpServletRequest request) {
        CategoryDomainObject[] categories = categoryMapper.getAllCategoriesOfType(categoryType);
        Arrays.sort(categories);
        Set<CategoryDomainObject> documentSelectedCategories = categoryMapper.getCategoriesOfType(categoryType, document.getCategoryIds());

        Function<CategoryDomainObject, String[]> categoryToStringPairTransformer = category -> new String[]{"" + category.getId(), category.getName()};
        String categoryOptionList = createOptionList(Arrays.asList(categories), documentSelectedCategories, categoryToStringPairTransformer);

        if (1 == categoryType.getMaxChoices()) {
            categoryOptionList = "<option>- " + (new LocalizedMessage("global/None")).toLocalizedString(request) + " -</option>" + categoryOptionList;
        }
        return categoryOptionList;
    }

    public static String createOptionListOfCategoriesOfTypeNotSelectedForDocument(DocumentMapper documentMapper,
                                                                                  CategoryTypeDomainObject categoryType,
                                                                                  DocumentDomainObject document) {
        CategoryMapper categoryMapper = documentMapper.getCategoryMapper();
        CategoryDomainObject[] categories = categoryMapper.getAllCategoriesOfType(categoryType);
        Set<CategoryDomainObject> documentSelectedCategories = categoryMapper.getCategoriesOfType(categoryType, document.getCategoryIds());
        Set<CategoryDomainObject> notSelectedCategories = new HashSet<>(Arrays.asList(categories));
        notSelectedCategories.removeAll(documentSelectedCategories);

        return createOptionListOfCategories(new TreeSet<>(notSelectedCategories), categoryType);
    }

    public static String createOptionListOfCategories(Collection<CategoryDomainObject> categories, CategoryTypeDomainObject categoryType) {
        Function<CategoryDomainObject, String[]> categoryToStringPairTransformer = category -> new String[]{"" + category.getId(), category.getName()};
        String categoryOptionList = createOptionList(categories, categoryToStringPairTransformer);

        if (1 == categoryType.getMaxChoices()) {
            categoryOptionList = "<option></option>" + categoryOptionList;
        }
        return categoryOptionList;
    }

    public static String getLinkedStatusIconTemplate(DocumentDomainObject document, UserDomainObject user,
                                                     HttpServletRequest request) {
        String statusIconTemplate = getStatusIconTemplate(document, user);
        if (user.canEditDocumentInformationFor(document)) {
            statusIconTemplate = "<a href=\"" + request.getContextPath() + "/servlet/AdminDoc?meta_id="
                    + document.getId()
                    + "&amp;"
                    + AdminDoc.PARAMETER__DISPATCH_FLAGS
                    + "=1\" target=\"_blank\">" +
                    statusIconTemplate +
                    "</a>";
        }
        return statusIconTemplate;
    }

    public static String getStatusIconTemplate(DocumentDomainObject document, UserDomainObject user) {
        LifeCyclePhase lifeCyclePhase = document.getLifeCyclePhase();
        String statusIconTemplateName = null;
        for (Object[] statusTemplatePair : STATUS_TEMPLATE_PAIRS) {
            if (lifeCyclePhase.equals(statusTemplatePair[0])) {
                statusIconTemplateName = (String) statusTemplatePair[1];
                break;
            }
        }
        return Imcms.getServices().getAdminTemplate(statusIconTemplateName, user, null);
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

    public static String removeTags(String html) {
        Perl5Util perl5util = new Perl5Util();
        return perl5util.substitute("s!<.+?>!!g", html);
    }

    public static String createUsersOptionList(ImcmsServices imcref) {
        UserDomainObject[] users = imcref.getImcmsAuthenticatorAndUserAndRoleMapper().getUsers(true, false);
        return createOptionList(Arrays.asList(users), user -> new String[]{"" + user.getId(), user.getLastName() + ", " + user.getFirstName()});
    }

}
