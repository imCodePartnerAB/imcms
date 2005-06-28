package imcode.util;

import com.imcode.imcms.servlet.admin.AdminDoc;
import com.imcode.imcms.mapping.CategoryMapper;
import imcode.server.Imcms;
import imcode.server.ImcmsServices;
import imcode.server.document.DocumentDomainObject;
import imcode.server.document.CategoryDomainObject;
import imcode.server.document.CategoryTypeDomainObject;
import imcode.server.user.UserDomainObject;
import org.apache.commons.collections.Transformer;
import org.apache.commons.lang.StringEscapeUtils;
import org.apache.commons.lang.UnhandledException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.*;

public class Html {

    private Html() {

    }

    /**
     * CreateHtml code, can mark up several values as selected
     *
     * @deprecated Use {@link #createOptionList(java.util.Collection, java.util.Collection, org.apache.commons.collections.Transformer)} instead.
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

    public static String createOptionList( Collection allValues, Object selectedValue,
                                           Transformer objectToStringPairTransformer ) {
        return createOptionList( allValues, Arrays.asList( new Object[]{selectedValue} ), objectToStringPairTransformer );
    }

    public static String createOptionList( Collection allValues, Collection selectedValues,
                                           Transformer objectToStringPairTransformer ) {
        StringBuffer htmlStr = new StringBuffer();

        Set selectedValuesSet = null != selectedValues ? new HashSet( selectedValues ) : new HashSet();
        for ( Iterator iterator = allValues.iterator(); iterator.hasNext(); ) {
            Object valueObject = iterator.next();
            String[] valueAndNameStringPair = (String[])objectToStringPairTransformer.transform( valueObject );
            String value = valueAndNameStringPair[0];
            String name = valueAndNameStringPair[1];
            boolean valueSelected = selectedValuesSet.contains( valueObject );
            htmlStr.append( option( value, name, valueSelected ) );
        } // end for

        return htmlStr.toString();
    }

    /**
     * CreateHtml code. Generates the following HTML code snippets. Format should be
     * one of the arguments below.
     */

    public static String createOptionList( List data, String selected ) {
        return createOptionList( data, Arrays.asList( new String[]{selected} ) );
    }

    public static String createOptionListOfCategoriesOfTypeForDocument(CategoryMapper categoryMapper,
                                                                       CategoryTypeDomainObject categoryType,
                                                                       DocumentDomainObject document, HttpServletRequest request) {
        CategoryDomainObject[] categories = categoryMapper.getAllCategoriesOfType( categoryType );
        Arrays.sort( categories );
        CategoryDomainObject[] documentSelectedCategories = document.getCategoriesOfType( categoryType );

        Transformer categoryToStringPairTransformer = new Transformer() {
            public Object transform( Object o ) {
                CategoryDomainObject category = (CategoryDomainObject)o;
                return new String[]{"" + category.getId(), category.getName()};
            }
        };
        String categoryOptionList = createOptionList( Arrays.asList( categories ), Arrays.asList( documentSelectedCategories ), categoryToStringPairTransformer );

        if ( 1 == categoryType.getMaxChoices() ) {
            categoryOptionList = "<option>- " + (new LocalizedMessage("global/None")).toLocalizedString(request) + " -</option>" + categoryOptionList;
        }
        return categoryOptionList;
    }

    public static String getLinkedStatusIconTemplate( DocumentDomainObject document, UserDomainObject user,
                                                      HttpServletRequest request ) {
        String statusIconTemplate = getStatusIconTemplate( document, user );
        if ( user.canEditDocumentInformationFor( document ) ) {
            statusIconTemplate = "<a href=\"" + request.getContextPath() + "/servlet/AdminDoc?meta_id="
                                 + document.getId()
                                 + "&"
                                 + AdminDoc.PARAMETER__DISPATCH_FLAGS
                                 + "=1\" target=\"_blank\">" +
                                 statusIconTemplate +
                                 "</a>";
        }
        return statusIconTemplate;
    }

    private static final Object[][] STATUS_TEMPLATE_PAIRS = {
        {DocumentDomainObject.LifeCyclePhase.NEW, "status/new.frag"},
        {DocumentDomainObject.LifeCyclePhase.DISAPPROVED, "status/disapproved.frag"},
        {DocumentDomainObject.LifeCyclePhase.PUBLISHED, "status/published.frag"},
        {DocumentDomainObject.LifeCyclePhase.UNPUBLISHED, "status/unpublished.frag"},
        {DocumentDomainObject.LifeCyclePhase.ARCHIVED, "status/archived.frag"},
        {DocumentDomainObject.LifeCyclePhase.APPROVED, "status/approved.frag"},
    };

    public static String getStatusIconTemplate( DocumentDomainObject document, UserDomainObject user ) {
        DocumentDomainObject.LifeCyclePhase lifeCyclePhase = document.getLifeCyclePhase();
        String statusIconTemplateName = null;
        for ( int i = 0; i < STATUS_TEMPLATE_PAIRS.length; i++ ) {
            Object[] statusTemplatePair = STATUS_TEMPLATE_PAIRS[i];
            if (lifeCyclePhase.equals( statusTemplatePair[0 ])) {
                statusIconTemplateName = (String)statusTemplatePair[1] ;
                break ;
            }
        }
        return Imcms.getServices().getAdminTemplate( statusIconTemplateName, user, null );
    }

    public static String option( String elementValue, String content, boolean selected ) {

        StringBuffer option = new StringBuffer();

        option.append( "<option value=\"" + StringEscapeUtils.escapeHtml( elementValue ) + "\"" );
        if ( selected ) {
            option.append( " selected" );
        }
        option.append( ">" + StringEscapeUtils.escapeHtml( content ) + "</option>" );
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
        if ( null == document || !( user.canEdit( document ) || user.isUserAdmin() || user.canAccessAdminPages() ) ) {
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
        String label_urlparam;
        org.apache.oro.text.perl.Perl5Util perl5util = new org.apache.oro.text.perl.Perl5Util();
        label_urlparam = perl5util.substitute( "s!<.+?>!!g", html );
        return label_urlparam;
    }

    public static String createUsersOptionList( ImcmsServices imcref ) {
        UserDomainObject[] users = imcref.getImcmsAuthenticatorAndUserAndRoleMapper().getUsers( true, false );
        String usersOption = createOptionList(Arrays.asList( users ),null, new Transformer() {
            public Object transform( Object input ) {
                UserDomainObject user = (UserDomainObject)input ;
                return new String[] {""+user.getId(), user.getLastName()+", "+user.getFirstName()} ;
            }
        } ) ;
        return usersOption;
    }
}
