/*
 *
 * @(#)Html.java
 *
 *
 * Copyright (c)
 *
 */

package imcode.external.diverse;

import org.apache.commons.collections.Transformer;

import java.util.*;

import imcode.server.document.CategoryTypeDomainObject;
import imcode.server.document.DocumentDomainObject;
import imcode.server.document.CategoryDomainObject;
import imcode.server.document.DocumentMapper;
import imcode.server.user.ImcmsAuthenticatorAndUserMapper;
import imcode.server.user.UserDomainObject;

/**
 * Takes care of creating Html code.
 * 
 * @version 1.1 23 Oct 2000
 */

public class Html {

    private Html() {

    }

    /**
     * CreateHtml code, can mark up several values as selected
     * @deprecated Use {@link #createOptionList(java.util.List, java.util.List, org.apache.commons.collections.Transformer)} instead.
     */
    public static String createOptionList( List allValues, List selectedValues ) {
        StringBuffer htmlStr = new StringBuffer();

        Set selectedValuesSet = new HashSet( selectedValues );
        for ( int i = 0; i < allValues.size(); i += 2 ) {
            String value = allValues.get(i).toString() ;
            String name = allValues.get(i+1).toString() ;
            boolean valueSelected = selectedValuesSet.contains( value ) ;
            htmlStr.append(createOption( value, name, valueSelected)) ;
        } // end for

        return htmlStr.toString();
    }

    public static String createOptionList( List allValues, Object selectedValue,
                                           Transformer objectToStringPairTransformer ) {
        return createOptionList( allValues, Arrays.asList(new Object[] {selectedValue}), objectToStringPairTransformer) ;
    }

    public static String createOptionList( List allValues, List selectedValues,
                                           Transformer objectToStringPairTransformer ) {
        StringBuffer htmlStr = new StringBuffer();

        Set selectedValuesSet = new HashSet( selectedValues );
        for ( int i = 0; i < allValues.size(); i++ ) {
            final Object valueObject = allValues.get(i);
            String[] valueAndNameStringPair = (String[])objectToStringPairTransformer.transform(valueObject) ;
            String value = valueAndNameStringPair[0] ;
            String name = valueAndNameStringPair[1] ;
            boolean valueSelected = selectedValuesSet.contains( valueObject ) ;
            htmlStr.append(createOption( value, name, valueSelected)) ;
        } // end for

        return htmlStr.toString();
    }

    /**
     * CreateHtml code. Generates the following HTML code snippets. Format should be
     * one of the arguments below.
     */

    public static String createOptionList( String selected, List data ) {
        return createOptionList( data, Arrays.asList(new String[] {selected}) ) ;
    }

    /**
     * creats option.
     * 
     * @param elementValue - option value
     * @param elementValue - option string
     * @param selected     - true or false
     */
    public static String createOption( String elementValue, String elementName, boolean selected ) {

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
        Arrays.sort(categories) ;
        CategoryDomainObject[] documentSelectedCategories = document.getCategoriesOfType( categoryType );

        Transformer categoryToStringPairTransformer = new Transformer() {
            public Object transform( Object o ) {
                CategoryDomainObject category = (CategoryDomainObject)o ;
                return new String[] {""+category.getId(), category.getName()} ;
            }
        } ;
        String categoryOptionList = createOptionList( Arrays.asList(categories), Arrays.asList(documentSelectedCategories), categoryToStringPairTransformer) ;

        if ( 1 == categoryType.getMaxChoices() ) {
            categoryOptionList = "<option></option>" + categoryOptionList ;
        }
        return categoryOptionList;
    }

}
