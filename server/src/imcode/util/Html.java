package imcode.util;

import com.imcode.imcms.servlet.admin.AdminDoc;
import com.imcode.imcms.servlet.admin.ChangeImage;
import imcode.server.ApplicationServer;
import imcode.server.document.CategoryDomainObject;
import imcode.server.document.CategoryTypeDomainObject;
import imcode.server.document.DocumentDomainObject;
import imcode.server.document.DocumentMapper;
import imcode.server.document.textdocument.ImageDomainObject;
import imcode.server.user.UserDomainObject;
import org.apache.commons.collections.Transformer;
import org.apache.commons.lang.StringEscapeUtils;
import org.apache.commons.lang.StringUtils;

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

    public static String createOptionList( String selected, List data ) {
        return createOptionList( data, Arrays.asList( new String[]{selected} ) );
    }

    /**
     * creats option.
     *
     * @param elementValue - option value
     * @param elementValue - option string
     * @param selected     - true or false
     */
    public static String option( String elementValue, String elementName, boolean selected ) {

        StringBuffer option = new StringBuffer();

        option.append( "<option value=\"" + StringEscapeUtils.escapeHtml( elementValue ) + "\"" );
        if ( selected ) {
            option.append( " selected" );
        }
        option.append( ">" + StringEscapeUtils.escapeHtml( elementName ) + "</option>" );
        return option.toString();
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

    public static String getLinkedStatusIconTemplate( DocumentDomainObject document, UserDomainObject user ) {
        DocumentMapper documentMapper = ApplicationServer.getIMCServiceInterface().getDocumentMapper();
        String statusIconTemplate = documentMapper.getStatusIconTemplate( document, user );
        if ( documentMapper.userHasMoreThanReadPermissionOnDocument( user, document ) ) {
            statusIconTemplate = "<a href=\"AdminDoc?meta_id=" + document.getId() + "&"
                                 + AdminDoc.PARAMETER__DISPATCH_FLAGS
                                 + "=1\" target=\"_blank\">" +
                                 statusIconTemplate +
                                 "</a>";
        }
        return statusIconTemplate;
    }

    public static String hidden( String name, String value ) {
        return "<input type=\"hidden\" name=\"" + StringEscapeUtils.escapeHtml( name ) + "\" value=\""
               + StringEscapeUtils.escapeHtml( value )
               + "\">";
    }
}
