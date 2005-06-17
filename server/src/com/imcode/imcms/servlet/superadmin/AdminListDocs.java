package com.imcode.imcms.servlet.superadmin;

import imcode.server.Imcms;
import imcode.server.ImcmsServices;
import imcode.server.document.DocumentTypeDomainObject;
import imcode.server.user.UserDomainObject;
import imcode.util.*;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Lists document by create or modified date of choisen document types.
 * <p/>
 * Html template in use:
 * AdminListDocs.html
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
 * - GetLangPrefixFromId
 *
 * @author Jerker Drottenmyr
 * @version 1.04 11 Nov 2000
 */
public class AdminListDocs extends Administrator {

    private final static Logger log = Logger.getLogger( AdminListDocs.class.getName() );

    private static final String TEMPLATE_LISTDOC = "AdminListDocs.html";
    private static final String TEMPLATE_LISTDOC_LIST_MODIFIED = "AdminListDocs_doclList_modified.html";
    private static final String TEMPLATE_LISTDOC_LIST_CREATED = "AdminListDocs_doclList_created.html";
    private static final String TEMPLATE_LISTDOC_LIST_ELEMENT = "AdminListDocs_doclList_element.html";
    private static final String ERROR_HEADER = "AdminListDocs";

    protected void doGet( HttpServletRequest request, HttpServletResponse response )
            throws ServletException, IOException {

        // Lets verify that the user who tries to add a new user is an admin
        ImcmsServices imcref = Imcms.getServices();
        UserDomainObject user = Utility.getLoggedOnUser( request );
        if ( !user.isSuperAdmin() ) {
            return;
        }

        Map allDocumentTypeIdsAndNames = imcref.getDocumentMapper().getAllDocumentTypeIdsAndNamesInUsersLanguage( user );

        // Lets generate the html page
        StringBuffer optionList = new StringBuffer();
        for ( Iterator iterator = allDocumentTypeIdsAndNames.entrySet().iterator(); iterator.hasNext(); ) {
            Map.Entry entry = (Map.Entry)iterator.next();
            Integer documentTypeId = (Integer)entry.getKey();
            String documentTypeName = (String)entry.getValue();
            optionList.append( Html.option( "" + documentTypeId, documentTypeName, false ) );
        }

        Map vm = new HashMap();
        vm.put("DOCUMENT_TYPES", optionList.toString()) ;

        this.sendHtml( request, response, vm, TEMPLATE_LISTDOC );

    }

    protected void doPost( HttpServletRequest request, HttpServletResponse response )
            throws ServletException, IOException {

        ImcmsServices imcref = Imcms.getServices();

        String eMailServerMaster = imcref.getSystemData().getServerMasterAddress();
        // lets get ready for errors


        // Lets check if the user is an admin, otherwise throw him out.
        UserDomainObject user = Utility.getLoggedOnUser( request );
        if ( !user.isSuperAdmin() ) {
            String header = "Error in AdminListDocs. ";
            Properties langproperties = imcref.getLanguageProperties( user );
            String msg = langproperties.getProperty( "error/servlet/global/no_administrator" ) + "<br>";
            log.debug( header + "- user is not an administrator" );
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
            int[] docTypesToShow = null;

            /*
             * 0 = startDate to endDate
             * 1 = all
             * 2 = all upp to endDate
             * 3 = all down to startDate
             */
            String[] docTypeStrings = request.getParameterValues( "DOC_TYPES" );
            int[] docTypes = new int[docTypeStrings.length];
            for ( int i = 0; i < docTypeStrings.length; i++ ) {
                docTypes[i] = Integer.parseInt( docTypeStrings[i] );
            }

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
                        int testVar = docTypes[i];
                        // if all doctypes choosen then lets get all doctypes
                        if ( testVar == 0 ) {
                            docTypesToShow = DocumentTypeDomainObject.getAllDocumentTypeIds();
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
                String htmlListElement = imcref.getAdminTemplate( TEMPLATE_LISTDOC_LIST_ELEMENT, user, null );

                String[] tagData = {
                    "#META_ID#", null,
                    "#DOC_TYPE#", null,
                    "#HEADER#", null,
                    "#DATE#", null,
                };

                StringBuffer listOfDocs = new StringBuffer();
                String languagePrefix = user.getLanguageIso639_2();
                for ( int i = 0; i < docTypesToShow.length; i++ ) {
                    String[][] queryResult = imcref.getDatabase().execute2dArrayProcedure( "ListDocsByDate", new String[] {
                                                                                                                              listMod,
                                                                                                                      ""
                                                                                                                      + docTypesToShow[i],
                                                                                                                              startDate,
                                                                                                                              endDate,
                                                                                                                              languagePrefix
                                                                                                                      } );

                    for ( int j = 0; j < queryResult.length; j++ ) {
                        tagData[1] = queryResult[j][0];
                        tagData[3] = queryResult[j][1];
                        tagData[5] = queryResult[j][2];
                        tagData[7] = queryResult[j][3];
                        listOfDocs.append( Parser.parseDoc( htmlListElement, tagData ) );
                    }
                }

                String selectedDocTypesName = getSelectedDocTypeNames( imcref, docTypes, user );

                //Lets generate the html page
                Map vm = new HashMap();
                vm.put("LIST_DOCUMENT", listOfDocs.toString()) ;
                vm.put("selectedDocTypes", selectedDocTypesName) ;
                vm.put("selectedStartDate", startDate) ;
                vm.put("selectedEndDate", endDate) ;
                this.sendHtml( request, response, vm, parseTemplate );

            } else {
                sendErrorMessage( imcref, eMailServerMaster, user, ERROR_HEADER, 10, response );
            }
        }
    }

    private String getSelectedDocTypeNames( ImcmsServices imcref, int[] selectedDocTypeIds,
                                            UserDomainObject user ) {
        List selectedDocTypeNames = new ArrayList();
        Map allDocTypes = imcref.getDocumentMapper().getAllDocumentTypeIdsAndNamesInUsersLanguage( user );

        for ( int i = 0; i < selectedDocTypeIds.length; i++ ) {
            int selectedDocTypeId = selectedDocTypeIds[i];
            selectedDocTypeNames.add( allDocTypes.get( new Integer( selectedDocTypeId ) ) );
        }

        return StringUtils.join( selectedDocTypeNames.iterator(), ", " );
    }

    /**
     * check for right date form
     */
    private boolean isDateInRightFormat( String date ) {

        // Format the current time.
        SimpleDateFormat formatter = new SimpleDateFormat( DateConstants.DATE_FORMAT_STRING );

        try {
            formatter.parse( date );
        } catch ( ParseException e ) {
            return false;

        }

        return true;
    }
}
