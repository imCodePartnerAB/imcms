package imcode.util;

import com.imcode.imcms.servlet.admin.AdminDoc;
import imcode.server.Imcms;
import imcode.server.document.*;
import imcode.server.user.UserDomainObject;
import org.apache.commons.collections.Transformer;
import org.apache.commons.lang.StringEscapeUtils;
import org.apache.commons.lang.UnhandledException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.ServletException;
import java.util.*;
import java.io.IOException;

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

    public static String createOptionList( String selected, List data ) {
        return createOptionList( data, Arrays.asList( new String[]{selected} ) );
    }

    public static String createOptionListOfCategoriesOfTypeForDocument( DocumentMapper documentMapper,
                                                                        CategoryTypeDomainObject categoryType,
                                                                        DocumentDomainObject document ) {
        CategoryDomainObject[] categories = documentMapper.getAllCategoriesOfType( categoryType );
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
            categoryOptionList = "<option></option>" + categoryOptionList;
        }
        return categoryOptionList;
    }

    public static String getLinkedStatusIconTemplate( DocumentDomainObject document, UserDomainObject user,
                                                      HttpServletRequest request ) {
        String statusIconTemplate = getStatusIconTemplate( document, user );
        if ( user.canEditDocumentInformationFor( document ) ) {
            statusIconTemplate = "<a href=\""+request.getContextPath()+"/servlet/AdminDoc?meta_id=" + document.getId() + "&"
                                 + AdminDoc.PARAMETER__DISPATCH_FLAGS
                                 + "=1\" target=\"_blank\">" +
                                 statusIconTemplate +
                                 "</a>";
        }
        return statusIconTemplate;
    }

    private static final String TEMPLATE__STATUS_NEW = "status/new.frag";
    private static final String TEMPLATE__STATUS_DISAPPROVED = "status/disapproved.frag";
    private static final String TEMPLATE__STATUS_PUBLISHED = "status/published.frag";
    private static final String TEMPLATE__STATUS_UNPUBLISHED = "status/unpublished.frag";
    private static final String TEMPLATE__STATUS_ARCHIVED = "status/archived.frag";
    private static final String TEMPLATE__STATUS_APPROVED = "status/approved.frag";

    public static String getStatusIconTemplate( DocumentDomainObject document, UserDomainObject user ) {
        String statusIconTemplateName;
        if ( DocumentDomainObject.STATUS_NEW == document.getStatus() ) {
            statusIconTemplateName = TEMPLATE__STATUS_NEW;
        } else if ( DocumentDomainObject.STATUS_PUBLICATION_DISAPPROVED == document.getStatus() ) {
            statusIconTemplateName = TEMPLATE__STATUS_DISAPPROVED;
        } else if ( document.isPublishedAndNotArchived() ) {
            statusIconTemplateName = TEMPLATE__STATUS_PUBLISHED;
        } else if ( document.isNoLongerPublished() ) {
            statusIconTemplateName = TEMPLATE__STATUS_UNPUBLISHED;
        } else if ( document.isArchived() ) {
            statusIconTemplateName = TEMPLATE__STATUS_ARCHIVED;
        } else {
            statusIconTemplateName = TEMPLATE__STATUS_APPROVED;
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
            "<input type=\"radio\" name=\""+StringEscapeUtils.escapeHtml( name )+"\" value=\""
                + StringEscapeUtils.escapeHtml( value )+"\""+ (selected ? " checked" : "")+">" ;

    }

    /**
     * Returns the menubuttonrow
     */
    public static String getAdminButtons( UserDomainObject user, DocumentDomainObject document, HttpServletRequest request,
                                   HttpServletResponse response ) {
        if ( !(user.canEdit( document ) || user.isUserAdmin() || user.canAccessAdminPages()) ) {
            return "";
        }

        try {
            request.setAttribute( "document", document );
            request.setAttribute( "user", user );
            return Utility.getContents( "/imcms/"+user.getLanguageIso639_2()+"/jsp/admin/adminbuttons.jsp",request, response ) ;
        } catch ( ServletException e ) {
            throw new UnhandledException( e.getCause() );
        } catch ( IOException e ) {
            throw new UnhandledException( e );
        }
    }
}
