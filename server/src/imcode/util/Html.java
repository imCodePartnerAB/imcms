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
import org.apache.commons.lang.StringEscapeUtils;
import org.apache.commons.lang.UnhandledException;
import org.apache.oro.text.perl.Perl5Util;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.*;

public class Html {

    private Html() {

    }

    /**
     * @deprecated
     */
    public static String createOptionList( List allValues, List selectedValues ) {
        StringBuffer htmlStr = new StringBuffer();

        Set selectedValuesSet = new HashSet( selectedValues );
        for ( int i = 0; i < allValues.size(); i += 2 ) {
            String value = allValues.get( i ).toString();
            String name = allValues.get( i + 1 ).toString();
            boolean valueSelected = selectedValuesSet.contains( value );
            htmlStr.append( option( value, name, valueSelected ) );
        } // end for

        return htmlStr.toString();
    }

    public static String createOptionList(Collection allValues, ToStringPairTransformer transformer) {
        return createOptionList(allValues,(Collection)null,transformer) ;
    }

    public static String createOptionList( Collection allValues, Object selectedValue,
                                           ToStringPairTransformer objectToStringPairTransformer ) {
        return createOptionList( allValues, new Object[]{selectedValue}, objectToStringPairTransformer );
    }

    public static String createOptionList( Collection allValues, Object[] selectedValues,
                                           ToStringPairTransformer objectToStringPairTransformer ) {
        return createOptionList(allValues, Arrays.asList(selectedValues), objectToStringPairTransformer) ;
    }

    public static String createOptionList( Collection allValues, Collection selectedValues,
                                           ToStringPairTransformer objectToStringPairTransformer ) {
        Set selectedValuesSet = null != selectedValues ? new HashSet( selectedValues ) : new HashSet();

        return createOptionList(allValues, selectedValuesSet, objectToStringPairTransformer);
    }

    public static String createOptionList(Collection allValues, Set selectedValues,
                                          ToStringPairTransformer objectToStringPairTransformer) {
        return createOptionList(allValues.iterator(), selectedValues, objectToStringPairTransformer);
    }

    private static String createOptionList(Iterator iterator, Set selectedValuesSet,
                                           ToStringPairTransformer objectToStringPairTransformer) {
        StringBuffer htmlStr = new StringBuffer();
        while ( iterator.hasNext() ) {
            Object valueObject = iterator.next();
            String[] valueAndNameStringPair = (String[])objectToStringPairTransformer.transform( valueObject );
            String value = valueAndNameStringPair[0];
            String name = valueAndNameStringPair[1];
            boolean valueSelected = selectedValuesSet.contains( valueObject );
            htmlStr.append( option( value, name, valueSelected ) );
        }

        return htmlStr.toString();
    }

    /** @deprecated */
    public static String createOptionList( List data, String selected ) {
        return createOptionList( data, Arrays.asList( new String[]{selected} ) );
    }

    public static String createOptionListOfCategoriesOfTypeForDocument(CategoryMapper categoryMapper,
                                                                       CategoryTypeDomainObject categoryType,
                                                                       DocumentDomainObject document, HttpServletRequest request) {
        CategoryDomainObject[] categories = categoryMapper.getAllCategoriesOfType( categoryType );
        Arrays.sort( categories );
        Set documentSelectedCategories = categoryMapper.getCategoriesOfType( categoryType, document.getCategoryIds() );

        ToStringPairTransformer categoryToStringPairTransformer = new ToStringPairTransformer() {
            protected String[] transformToStringPair(Object o) {
                CategoryDomainObject category = (CategoryDomainObject)o;
                return new String[]{"" + category.getId(), category.getName()};
            }
        };
        String categoryOptionList = createOptionList( Arrays.asList( categories ), documentSelectedCategories, categoryToStringPairTransformer );

        if ( 1 == categoryType.getMaxChoices() ) {
            categoryOptionList = "<option>- " + (new LocalizedMessage("global/None")).toLocalizedString(request) + " -</option>" + categoryOptionList;
        }
        return categoryOptionList;
    }

	public static String createOptionListOfCategoriesOfTypeNotSelectedForDocument ( DocumentMapper documentMapper,
                                                                        CategoryTypeDomainObject categoryType,
                                                                        DocumentDomainObject document ) {
        CategoryMapper categoryMapper = documentMapper.getCategoryMapper();
        CategoryDomainObject[] categories = categoryMapper.getAllCategoriesOfType( categoryType );
        Set documentSelectedCategories = categoryMapper.getCategoriesOfType( categoryType, document.getCategoryIds() );
		Set notSelectedCategories = new HashSet(Arrays.asList(categories)) ;
		notSelectedCategories.removeAll(documentSelectedCategories);

		return createOptionListOfCategories(new TreeSet(notSelectedCategories), categoryType);
    }

	public static String createOptionListOfCategories(Collection categories, CategoryTypeDomainObject categoryType){
        ToStringPairTransformer categoryToStringPairTransformer = new ToStringPairTransformer() {
            protected String[] transformToStringPair(Object object) {
                CategoryDomainObject category = (CategoryDomainObject)object;
                return new String[]{"" + category.getId(), category.getName()};
            }
        };
		String categoryOptionList = createOptionList( categories, Arrays.asList( new String[]{}  ), categoryToStringPairTransformer );

		if ( 1 == categoryType.getMaxChoices() ) {
			categoryOptionList = "<option></option>" + categoryOptionList;
		}
		return categoryOptionList;
	}

	public static String getLinkedStatusIconTemplate( DocumentDomainObject document, UserDomainObject user,
                                                      HttpServletRequest request ) {
        String statusIconTemplate = getStatusIconTemplate( document, user );
        if ( user.canEditDocumentInformationFor( document ) ) {
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

    private static final Object[][] STATUS_TEMPLATE_PAIRS = {
        { LifeCyclePhase.NEW, "status/new.frag"},
        { LifeCyclePhase.DISAPPROVED, "status/disapproved.frag"},
        { LifeCyclePhase.PUBLISHED, "status/published.frag"},
        { LifeCyclePhase.UNPUBLISHED, "status/unpublished.frag"},
        { LifeCyclePhase.ARCHIVED, "status/archived.frag"},
        { LifeCyclePhase.APPROVED, "status/approved.frag"},
    };

    public static String getStatusIconTemplate( DocumentDomainObject document, UserDomainObject user ) {
        LifeCyclePhase lifeCyclePhase = document.getLifeCyclePhase();
        String statusIconTemplateName = null;
        for ( Object[] statusTemplatePair : STATUS_TEMPLATE_PAIRS ) {
            if ( lifeCyclePhase.equals(statusTemplatePair[0]) ) {
                statusIconTemplateName = (String) statusTemplatePair[1];
                break;
            }
        }
        return Imcms.getServices().getAdminTemplate( statusIconTemplateName, user, null );
    }

    public static String option( String elementValue, String content, boolean selected ) {

        StringBuffer option = new StringBuffer();

        option.append("<option value=\"").append(StringEscapeUtils.escapeHtml(elementValue)).append("\"");
        if ( selected ) {
            option.append( " selected" );
        }
        option.append(">").append(StringEscapeUtils.escapeHtml(content)).append("</option>");
        return option.toString();
    }

    public static String hidden( String name, String value ) {
        return "<input type=\"hidden\" name=\"" + StringEscapeUtils.escapeHtml( name ) + "\" value=\""
               + StringEscapeUtils.escapeHtml( value )
               + "\">";
    }

    public static String radio( String name, String value, boolean selected ) {
        return
                "<input type=\"radio\" name=\"" + StringEscapeUtils.escapeHtml( name ) + "\" value=\""
                + StringEscapeUtils.escapeHtml( value ) + "\"" + ( selected ? " checked" : "" ) + ">";

    }

    /**
     * Returns the menubuttonrow
     */
    public static String getAdminButtons( UserDomainObject user, DocumentDomainObject document, HttpServletRequest request,
                                          HttpServletResponse response ) {
        if ( null == document || !( user.canEdit( document ) || user.isUserAdminAndCanEditAtLeastOneRole() || user.canAccessAdminPages() ) ) {
            return "";
        }

        try {
            request.setAttribute( "document", document );
            request.setAttribute( "user", user );
            return Utility.getContents( "/imcms/" + user.getLanguageIso639_2() + "/jsp/admin/adminbuttons.jsp", request, response );
        } catch ( ServletException e ) {
            throw new UnhandledException( e );
        } catch ( IOException e ) {
            throw new UnhandledException( e );
        }
    }

    public static String removeTags( String html ) {
        Perl5Util perl5util = new Perl5Util();
        return perl5util.substitute("s!<.+?>!!g", html);
    }

    public static String createUsersOptionList( ImcmsServices imcref ) {
        UserDomainObject[] users = imcref.getImcmsAuthenticatorAndUserAndRoleMapper().getUsers( true, false );
        return createOptionList(Arrays.asList( users ), new ToStringPairTransformer() {
            public String[] transformToStringPair( Object input ) {
                UserDomainObject user = (UserDomainObject)input ;
                return new String[] {""+user.getId(), user.getLastName()+", "+user.getFirstName()} ;
            }
        } );
    }

}
