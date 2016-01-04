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
import org.apache.commons.lang3.StringEscapeUtils;
import org.apache.oro.text.perl.Perl5Util;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.*;

public class Html {

	private static final Object[][] STATUS_TEMPLATE_PAIRS = {
			{LifeCyclePhase.NEW, "status/new.frag"},
			{LifeCyclePhase.DISAPPROVED, "status/disapproved.frag"},
			{LifeCyclePhase.PUBLISHED, "status/published.frag"},
			{LifeCyclePhase.UNPUBLISHED, "status/unpublished.frag"},
			{LifeCyclePhase.ARCHIVED, "status/archived.frag"},
			{LifeCyclePhase.APPROVED, "status/approved.frag"},
	};

	private Html() {
	}

	//todo: remove these many overloaded methods

	/**
	 * @deprecated
	 */
	public static String createOptionList(List allValues, List<String> selectedValues) {
		StringBuffer htmlStr = new StringBuffer();

		Set<String> selectedValuesSet = new HashSet<>(selectedValues);
		for (int i = 0; i < allValues.size(); i += 2) {
			String value = allValues.get(i).toString();
			String name = allValues.get(i + 1).toString();
			boolean valueSelected = selectedValuesSet.contains(value);
			htmlStr.append(option(value, name, valueSelected));
		}

		return htmlStr.toString();
	}

	public static <E> String createOptionList(Collection<E> allValues, ToStringPairTransformer transformer) {
		return createOptionList(allValues, (List<E>) null, transformer);
	}

	public static <E> String createOptionList(Collection<E> allValues, Object selectedValue,
											  ToStringPairTransformer objectToStringPairTransformer) {
		return createOptionList(allValues, new Object[]{selectedValue}, objectToStringPairTransformer);
	}

	public static <E> String createOptionList(Collection<E> allValues, Object[] selectedValue,
											  ToStringPairTransformer objectToStringPairTransformer) {
		return createOptionList(allValues, Arrays.asList(selectedValue), objectToStringPairTransformer);
	}

	public static <E> String createOptionList(Collection<E> allValues, Collection<E> selectedValues,
											  ToStringPairTransformer objectToStringPairTransformer) {
		Set<E> selectedValuesSet = (null == selectedValues) ? new HashSet<>() : new HashSet<>(selectedValues);

		return createOptionList(allValues, selectedValuesSet, objectToStringPairTransformer);
	}

	private static <E> String createOptionList(Collection<E> allValues, Set<E> selectedValues,
											   ToStringPairTransformer objectToStringPairTransformer) {
		StringBuffer htmlStr = new StringBuffer();
		for (E valueObject : allValues) {
			String[] valueAndNameStringPair = (String[]) objectToStringPairTransformer.transform(valueObject);
			String value = valueAndNameStringPair[0];
			String name = valueAndNameStringPair[1];
			boolean valueSelected = selectedValues.contains(valueObject);
			htmlStr.append(option(value, name, valueSelected));
		}
		return htmlStr.toString();
	}

	/**
	 * Deprecated. Use one of another createOptionList() methods
	 */
	@Deprecated
	private static <T> String createOptionList(Iterator<T> iterator, Set<T> selectedValuesSet,
											   ToStringPairTransformer objectToStringPairTransformer) {
		StringBuffer htmlStr = new StringBuffer();
		while (iterator.hasNext()) {
			T valueObject = iterator.next();
			String[] valueAndNameStringPair = (String[]) objectToStringPairTransformer.transform(valueObject);
			String value = valueAndNameStringPair[0];
			String name = valueAndNameStringPair[1];
			boolean valueSelected = selectedValuesSet.contains(valueObject);
			htmlStr.append(option(value, name, valueSelected));
		}

		return htmlStr.toString();
	}

	/**
	 * @deprecated
	 */
	public static String createOptionList(List data, String selected) {
		return createOptionList(data, Arrays.asList(new String[]{selected}));
	}

	public static String createOptionListOfCategoriesOfTypeForDocument(CategoryMapper categoryMapper,
																	   CategoryTypeDomainObject categoryType,
																	   DocumentDomainObject document, HttpServletRequest request) {
		CategoryDomainObject[] categories = categoryMapper.getAllCategoriesOfType(categoryType);
		Arrays.sort(categories);
		Set<CategoryDomainObject> documentSelectedCategories = categoryMapper.getCategoriesOfType(categoryType, document.getCategoryIds());

		ToStringPairTransformer categoryToStringPairTransformer = new ToStringPairTransformer() {
			protected String[] transformToStringPair(Object o) {
				CategoryDomainObject category = (CategoryDomainObject) o;
				return new String[]{"" + category.getId(), category.getName()};
			}
		};
		String categoryOptionList = createOptionList(Arrays.asList(categories).iterator(), documentSelectedCategories, categoryToStringPairTransformer);

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

	public static <T> String createOptionListOfCategories(Collection<T> categories, CategoryTypeDomainObject categoryType) {
		ToStringPairTransformer categoryToStringPairTransformer = new ToStringPairTransformer() {
			protected String[] transformToStringPair(Object object) {
				CategoryDomainObject category = (CategoryDomainObject) object;
				return new String[]{"" + category.getId(), category.getName()};
			}
		};
		String categoryOptionList = createOptionList(categories, Collections.emptyList(), categoryToStringPairTransformer);

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
		return "<input type=\"radio\" name=\"" + StringEscapeUtils.escapeHtml4(name) + "\" value=\""
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
		return new Perl5Util().substitute("s!<.+?>!!g", html);
	}

	public static String createUsersOptionList(ImcmsServices imcref) {
		UserDomainObject[] users = imcref.getImcmsAuthenticatorAndUserAndRoleMapper().getUsers(true, false);
		return createOptionList(Arrays.asList(users), new ToStringPairTransformer() {
			public String[] transformToStringPair(Object input) {
				UserDomainObject user = (UserDomainObject) input;
				return new String[]{"" + user.getId(), user.getLastName() + ", " + user.getFirstName()};
			}
		});
	}
}
