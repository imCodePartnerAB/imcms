package imcode.util;

import imcode.server.document.CategoryDomainObject;
import imcode.server.document.CategoryTypeDomainObject;
import imcode.server.document.DocumentDomainObject;
import imcode.server.document.DocumentMapper;
import imcode.server.document.textdocument.ImageDomainObject;
import imcode.server.user.UserDomainObject;
import imcode.server.ApplicationServer;
import org.apache.commons.collections.Transformer;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.StringEscapeUtils;

import java.util.*;

import com.imcode.imcms.servlet.admin.AdminDoc;

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

        option.append( "<option value=\"" + elementValue + "\"" );
        if ( selected ) {
            option.append( " selected" );
        }
        option.append( ">" + elementName + "</option>" );
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

    public static String getImageTag( ImageDomainObject image ) {
        StringBuffer imageTagBuffer = new StringBuffer( 96 );
        if ( !"".equals( image.getUrl() ) ) {

            if ( StringUtils.isNotBlank( image.getLinkUrl() ) ) {
                imageTagBuffer.append( "<a href=\"" ).append( StringEscapeUtils.escapeHtml( image.getLinkUrl() ) ).append( "\"" );
                if ( !"".equals( image.getTarget() ) ) {
                    imageTagBuffer.append( " target=\"" ).append( StringEscapeUtils.escapeHtml( image.getTarget() ) ).append( "\"" );
                }
                imageTagBuffer.append( '>' );
            }

            String imageUrl = ApplicationServer.getIMCServiceInterface().getConfig().getImageUrl() + image.getUrl();

            imageTagBuffer.append( "<img src=\"" + StringEscapeUtils.escapeHtml( imageUrl ) + "\"" ); // FIXME: Get imageurl from webserver somehow. The user-object, perhaps?
            if ( 0 != image.getWidth() ) {
                imageTagBuffer.append( " width=\"" + image.getWidth() + "\"" );
            }
            if ( 0 != image.getHeight() ) {
                imageTagBuffer.append( " height=\"" + image.getHeight() + "\"" );
            }
            imageTagBuffer.append( " border=\"" + image.getBorder() + "\"" );

            if ( 0 != image.getVerticalSpace() ) {
                imageTagBuffer.append( " vspace=\"" + image.getVerticalSpace() + "\"" );
            }
            if ( 0 != image.getHorizontalSpace() ) {
                imageTagBuffer.append( " hspace=\"" + image.getHorizontalSpace() + "\"" );
            }
            if ( StringUtils.isNotBlank( image.getName() ) ) {
                imageTagBuffer.append( " name=\"" + StringEscapeUtils.escapeHtml( image.getName() ) + "\"" );
            }
            if ( StringUtils.isNotBlank( image.getAlternateText() ) ) {
                imageTagBuffer.append( " alt=\"" + StringEscapeUtils.escapeHtml( image.getAlternateText() ) + "\"" );
            }
            if ( StringUtils.isNotBlank( image.getLowResolutionUrl() ) ) {
                imageTagBuffer.append( " lowsrc=\"" + StringEscapeUtils.escapeHtml( image.getLowResolutionUrl() ) + "\"" );
            }
            if ( StringUtils.isNotBlank( image.getAlign() ) && !"none".equals( image.getAlign() ) ) {
                imageTagBuffer.append( " align=\"" + StringEscapeUtils.escapeHtml( image.getAlign() ) + "\"" );
            }
            imageTagBuffer.append( ">" );
            if ( StringUtils.isNotBlank( image.getLinkUrl() ) ) {
                imageTagBuffer.append( "</a>" );
            }
        }
        return imageTagBuffer.toString();
    }

    public static String hidden( String name, String value ) {
        return "<input type=\"hidden\" name=\""+StringEscapeUtils.escapeHtml( name )+"\" value=\""+StringEscapeUtils.escapeHtml( value )+"\">";
    }
}
