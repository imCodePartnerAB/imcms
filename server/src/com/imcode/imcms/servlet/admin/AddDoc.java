package com.imcode.imcms.servlet.admin;

import imcode.external.diverse.Html;
import imcode.server.ApplicationServer;
import imcode.server.IMCServiceInterface;
import imcode.server.user.UserDomainObject;
import imcode.util.DateConstants;
import imcode.util.Utility;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.Vector;

/**
 Adds a new document to a menu.
 Shows an empty metadata page, which calls SaveNewMeta
 */
public class AddDoc extends HttpServlet {

    public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        DocumentComposer.NewDocumentParentInformation newDocumentParentInformation = new DocumentComposer.NewDocumentParentInformation( request );

        if (newDocumentParentInformation.documentTypeId == 0) {
            createExistingDocPage( newDocumentParentInformation.parentId, newDocumentParentInformation.parentMenuIndex, request, response);
        } else {
            DocumentComposer.addObjectToSessionAndSetSessionAttributeNameInRequest( "newDocumentParentInformation", newDocumentParentInformation,request, DocumentComposer.REQUEST_ATTR_OR_PARAM__NEW_DOCUMENT_PARENT_INFORMATION_SESSION_ATTRIBUTE_NAME);
            request.getRequestDispatcher( "DocumentComposer" ).forward( request, response );
        }
    }

    private void createExistingDocPage( int meta_id, int doc_menu_no,
                                        HttpServletRequest request, HttpServletResponse response ) throws IOException {
        response.setContentType( "text/html" );

        Vector vec = new Vector();
        vec.add("#meta_id#");
        vec.add(""+meta_id);
        vec.add("#doc_menu_no#");
        vec.add(""+doc_menu_no);

        // Lets get todays date
        SimpleDateFormat formatter = new SimpleDateFormat(DateConstants.DATE_FORMAT_STRING);
        Date toDay = new Date();
        vec.add( "#start_date#" );
        vec.add( "" );
        vec.add( "#end_date#" );
        vec.add( formatter.format( toDay ) );

        vec.add("#searchstring#");
        vec.add("");

        vec.add("#searchResults#");
        vec.add("");

        IMCServiceInterface imcref = ApplicationServer.getIMCServiceInterface() ;

        UserDomainObject user = Utility.getLoggedOnUser( request ) ;
        // Lets fix the sortby list, first get the displaytexts from the database
        String[] sortOrder = imcref.sqlProcedure( "SortOrder_GetExistingDocs", new String[] { user.getLanguageIso639_2() } );
        String sortOrderStr = Html.createOptionList("", Arrays.asList(sortOrder));
        vec.add("#sortBy#");
        vec.add(sortOrderStr);

        // Lets set all the the documenttypes as selected in the html file
        String[][] allDocTypesArray = imcref.getAllDocumentTypes(user.getLanguageIso639_2());
        for (int i = 0; i < allDocTypesArray.length; ++i) {
            vec.add("#checked_" + allDocTypesArray[i][0] + "#");
            vec.add("checked");
        }

        // Lets set the create/ change types as selected in the html file
        String[] allPossibleIncludeDocsValues = {"created", "changed"};
        for (int i = 0; i < allPossibleIncludeDocsValues.length; i++) {
            vec.add("#include_check_" + allPossibleIncludeDocsValues[i] + "#");
            vec.add("checked");
        }

        // Lets set the and / or search preposition
        String[] allPossibleSearchPreps = {"and", "or"};
        for (int i = 0; i < allPossibleSearchPreps.length; i++) {
            vec.add("#search_prep_check_" + allPossibleSearchPreps[i] + "#");
            if (i == 0) {
                vec.add("checked");
            } else {
                vec.add("");
            }
        }
        // Lets parse the html page which consists of the add an existing doc
        response.getWriter().write(imcref.getAdminTemplate("existing_doc.html", user, vec ));
        return;
    }

}
