/*
 *
 * @(#)AdminListDocs.java
 *
 *
 * 2000-10-20
 *
 * Copyright (c)
 *
 */

import imcode.external.diverse.Html;
import imcode.external.diverse.VariableManager;
import imcode.server.IMCServiceInterface;
import imcode.server.ApplicationServer;
import imcode.util.Parser;
import imcode.util.Utility;
import imcode.util.DateHelper;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.List;
import java.util.ArrayList;

import org.apache.commons.lang.StringUtils;

/**
 * Lists document by create or modified date of choisen document types.
 * <p/>
 * Html template in use:
 * AdminListDocs.html
 * AdminListDocs_doclList.html
 * Error.html
 * <p/>
 * Html parstags in use:
 * #DOCUMENT_TYPES#
 * #LIST_DOCUMENT#
 * #META_ID#
 * #HEADER#
 * #DOC_TYPE#
 * #DATE#
 * <p/>
 * stored procedures in use:
 * - ListDocsByDate
 * - ListDocsGetInternalDocTypes
 * - ListDocsGetInternalDocTypesValue
 * - GetLangPrefixFromId
 *
 * @author Jerker Drottenmyr
 * @version 1.04 11 Nov 2000
 */
public class AdminListDocs extends Administrator {

    private static final String TEMPLATE_LISTDOC = "AdminListDocs.html";
    private static final String TEMPLATE_LISTDOC_LIST_MODIFIED = "AdminListDocs_doclList_modified.html";
    private static final String TEMPLATE_LISTDOC_LIST_CREATED = "AdminListDocs_doclList_created.html";
    private static final String TEMPLATE_LISTDOC_LIST_ELEMENT = "AdminListDocs_doclList_element.html";
    private static final String ERROR_HEADER = "AdminListDocs";

    protected void doGet( HttpServletRequest request, HttpServletResponse response )
            throws ServletException, IOException {

        IMCServiceInterface imcref = ApplicationServer.getIMCServiceInterface();

        // Lets validate the session
        if ( !checkSession( request, response ) ) {
            return;
        }

        // Lets get an user object
        imcode.server.user.UserDomainObject user = getUserObj( request, response );

        if ( user == null ) {
            return;
        }

        // Lets verify that the user who tries to add a new user is an admin
        if ( imcref.checkAdminRights( user ) == false ) {
            return;
        }

        String languagePrefix = user.getLangPrefix();

        // Lets get all doctypes from DB
        String[][] queryResult = imcref.sqlProcedureMulti( "ListDocsGetInternalDocTypes", new String[]{languagePrefix} );

        // Lets generate the html page
        String optionList = Html.createListOfOptions( queryResult );

        VariableManager vm = new VariableManager();
        vm.addProperty( "DOCUMENT_TYPES", optionList );

        this.sendHtml( request, response, vm, TEMPLATE_LISTDOC );
        return;

    }

    protected void doPost( HttpServletRequest request, HttpServletResponse response )
            throws ServletException, IOException {

        IMCServiceInterface imcref = ApplicationServer.getIMCServiceInterface();

        String eMailServerMaster = Utility.getDomainPref( "servermaster_email" );

        // Lets validate the session
        if ( !checkSession( request, response ) ) return;

        // Lets get an user object
        imcode.server.user.UserDomainObject user = getUserObj( request, response );

        // lets get ready for errors
        String languagePrefix = user.getLangPrefix();

        if ( user == null ) {
            String header = "Error in AdminRoleBelongings.";
            String msg = "Couldnt create an user object." + "<BR>";
            this.log( header + msg );
            new AdminError( request, response, header, msg );
            return;
        }

        // Lets check if the user is an admin, otherwise throw him out.
        if ( !imcref.checkAdminRights( user ) ) {
            String header = "Error in AdminRoleBelongings.";
            String msg = "The user is not an administrator." + "<BR>";
            this.log( header + msg );
            new AdminError( request, response, header, msg );

            return;
        }

        // *************** RETURN TO ADMINMANAGER *****************
        if ( request.getParameter( "CANCEL" ) != null ) {
            response.sendRedirect( "AdminManager" );
            return;
        }

        // *************** RETURN TO ADMIN ROLES *****************
        if ( request.getParameter( "LISTDOC_LIST" ) != null ) {
            boolean noErrors = true;
            String parseTemplate = null;
            String[] docTypesToShow = null;

            /*
             * 0 = startDate to endDate
             * 1 = all
             * 2 = all upp to endDate
             * 3 = all down to startDate
             */
            String[] docTypes = request.getParameterValues( "DOC_TYPES" );

            /*
             * 0 = all date !not in use
             * 1 = create date
             * 2 = modified date
             */
            String listMod = request.getParameter( "LISTMOD" );
            String startDate = request.getParameter( "START_DATE" );
            String endDate = request.getParameter( "END_DATE" );

            /* lets see if any errors in requared fields or if some is missing */
            try {
                if ( listMod != null ) {
                    int mod = Integer.parseInt( listMod );
                    if ( !( mod == 1 || mod == 2 ) ) {
                        noErrors = false;
                    } else {
                        // lets set htmlTemplate (create or modified )
                        switch ( mod ) {
                            case ( 1 ):
                                parseTemplate = TEMPLATE_LISTDOC_LIST_CREATED;
                                break;
                            default:
                                parseTemplate = TEMPLATE_LISTDOC_LIST_MODIFIED;
                                break;
                        }
                    }
                } else {
                    noErrors = false;
                }

                if ( docTypes != null ) {
                    docTypesToShow = docTypes;
                    for ( int i = 0; i < docTypes.length; i++ ) {
                        int testVar = Integer.parseInt( docTypes[i] );
                        // if all doctypes choosen then lets get all doctypes
                        if ( testVar == 0 ) {
                            docTypesToShow = imcref.sqlProcedure( "ListDocsGetInternalDocTypesValue", new String[0] );
                        }
                    }
                } else {
                    noErrors = false;
                }
            } catch ( NumberFormatException e ) {
                noErrors = false;
            }

            if ( startDate != null ) {
                if ( startDate.length() > 0 ) {
                    if ( !isDateInRightFormat( startDate ) ) {
                        noErrors = false;
                    }
                } else {
                    startDate = "0"; // Stored Procedure expects 0 then no startDate
                }
            } else {
                noErrors = false; // no startDate field submited
            }

            if ( endDate != null ) {
                if ( endDate.length() > 0 ) {
                    if ( !isDateInRightFormat( endDate ) ) {
                        noErrors = false;
                    }
                } else {
                    endDate = "0"; // Stored Procedure expects 0 then no endDate
                }
            } else {
                noErrors = false; // no endDate field submited
            }

            // lets generate response page
            if ( noErrors ) {

                //lets get htmltemplate for tablerow
                String htmlListElement = imcref.parseDoc( null, TEMPLATE_LISTDOC_LIST_ELEMENT, user);

                String[] tagData = {
                    "#META_ID#", null,
                    "#DOC_TYPE#", null,
                    "#HEADER#", null,
                    "#DATE#", null,
                };

                StringBuffer listOfDocs = new StringBuffer();
                for ( int i = 0; i < docTypesToShow.length; i++ ) {
                    String[][] queryResult = imcref.sqlProcedureMulti( "ListDocsByDate",
                                                                       new String[]{listMod, docTypesToShow[i], startDate, endDate, languagePrefix} );

                    for ( int j = 0; j < queryResult.length; j++ ) {
                        tagData[1] = queryResult[j][0];
                        tagData[3] = queryResult[j][1];
                        tagData[5] = queryResult[j][2];
                        tagData[7] = queryResult[j][3];
                        listOfDocs.append( Parser.parseDoc( htmlListElement, tagData ) );
                    }
                }

                String selectedDocTypesName = getSelectedDocTypeNames( imcref, docTypes, languagePrefix );

                //Lets generate the html page
                VariableManager vm = new VariableManager();
                vm.addProperty( "LIST_DOCUMENT", listOfDocs.toString() );
                vm.addProperty( "selectedDocTypes", selectedDocTypesName );
                vm.addProperty( "selectedStartDate", startDate );
                vm.addProperty( "selectedEndDate", endDate );
                this.sendHtml( request, response, vm, parseTemplate );

            } else {
                sendErrorMessage( imcref, eMailServerMaster, user, ERROR_HEADER, 10, response );
            }
        }
    }

    private String getSelectedDocTypeNames( IMCServiceInterface imcref, String[] selectedDocTypes, String langPrefix ) {
        List selectedDocTypeNames = new ArrayList() ;
        String[][] allDocTypes = imcref.getDocumentTypesInList( langPrefix );

        for (int i = 0; i < allDocTypes.length; ++i) {
            if (Arrays.asList(selectedDocTypes).contains(allDocTypes[i][0])) {
                selectedDocTypeNames.add(allDocTypes[i][1]) ;
            }
        }

        return StringUtils.join( selectedDocTypeNames.iterator(), ", " );
    }

    /**
     * check for right date form
     */
    private boolean isDateInRightFormat( String date ) {

        // Format the current time.
        SimpleDateFormat formatter = new SimpleDateFormat( DateHelper.DATE_FORMAT_STRING );

        try {
            formatter.parse( date );
        } catch ( ParseException e ) {
            return false;

        }

        return true;
    }
}
