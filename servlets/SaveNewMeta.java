
import java.io.*;
import java.util.*;
import javax.servlet.*;
import javax.servlet.http.*;
import java.text.ParseException;

import imcode.util.*;
import imcode.server.*;
import imcode.server.user.UserDomainObject;
import imcode.util.DateHelper;
import imcode.server.document.DocumentMapper;
import imcode.server.document.DocumentDomainObject;

import org.apache.log4j.Category;
import com.imcode.imcms.api.Document;

/**
 Save new meta for a internalDocument.
 */
public class SaveNewMeta extends HttpServlet {
    private final static Category mainLog = Category.getInstance( IMCConstants.MAIN_LOG );

    /**
     init()
     */
    public void init( ServletConfig config ) throws ServletException {
        super.init( config );
    }


    /**
     doPost()
     */
    public void doPost( HttpServletRequest req, HttpServletResponse res ) throws ServletException, IOException {


        IMCServiceInterface imcref = ApplicationServer.getIMCServiceInterface();
        String start_url = imcref.getStartUrl();

        res.setContentType( "text/html" );
        Writer out = res.getWriter();

        /*
          From now on, we get the form data.
        */

        String parentMetaIdStr = req.getParameter( "parent_meta_id" );
        int parentMetaId = Integer.parseInt(parentMetaIdStr);

        String menuIndexStr = req.getParameter( "doc_menu_no" );
        int menuIndex = Integer.parseInt(menuIndexStr);

        String doc_type = req.getParameter( "doc_type" );
        String classification = req.getParameter( "classification" );

        String[] metatable = {/*  Nullable/Nullvalue */
            "shared", "0",
            "disable_search", "0",
            "archive", "0",
            "show_meta", "0",
            "permissions", "0",
            "expand", "1",
            "help_text_id", "1",
            "status_id", "1",
            "lang_prefix", null,
            "sort_position", "1",
            "menu_position", "1",
            "description", null,
            "meta_headline", null,
            "meta_text", null,
            "meta_image", null,
            "frame_name", "",
            "target", null};

        Properties metaprops = new Properties();
        // Loop through all meta-table-properties
        // Adding them to a HashMap to be used as input
        // That way i can mutilate the values before all the
        // permissions are checked.
        HashMap inputMap = new HashMap();
        for( int i = 0; i < metatable.length; i += 2 ) {
            inputMap.put( metatable[i], req.getParameter( metatable[i] ) );
        }

        // If target is set to '_other', it means the real target is in 'frame_name'.
        // In this case, set target to the value of frame_name.
        String target = (String)inputMap.get( "target" );
        String frame_name = (String)inputMap.get( "frame_name" );
        if( "_other".equals( target ) && frame_name != null && !"".equals( frame_name ) ) {
            inputMap.put( "target", frame_name );
        }
        inputMap.remove( "frame_name" );  // we only need to store frame_name in db column "target"

        Date nowDateTime = imcref.getCurrentDate();
        String activated_date = req.getParameter( "activated_date" );
        String activated_time = req.getParameter( "activated_time" );
        String activated_datetime;
        if( activated_date != null && activated_time != null ) {
            activated_datetime = activated_date + ' ' + activated_time;
            try {
                DateHelper.DATE_TIME_FORMAT_IN_DATABASE.parse( activated_datetime );
            } catch( ParseException ex ) {
                activated_datetime = null;
            }
        } else {
            activated_datetime = DateHelper.DATE_TIME_FORMAT_IN_DATABASE.format( nowDateTime );
        } // end of else

        String archived_date = req.getParameter( "archived_date" );
        String archived_time = req.getParameter( "archived_time" );
        String archived_datetime = null;
        if( archived_date != null && archived_time != null ) {
            archived_datetime = archived_date + ' ' + archived_time;
            try {
                DateHelper.DATE_TIME_FORMAT_IN_DATABASE.parse( archived_datetime );
            } catch( ParseException ex ) {
                archived_datetime = null;
            }
        }

        for( int i = 0; i < metatable.length; i += 2 ) {
            String tmp = (String)inputMap.get( metatable[i] );
            if( tmp != null ) {
                metaprops.setProperty( metatable[i], tmp );
            } else {
                metaprops.setProperty( metatable[i], metatable[i + 1] );
            }
        }


        // Check if user logged on
        imcode.server.user.UserDomainObject user;
        if( (user = Utility.getLoggedOnUserOrRedirect( req, res, start_url )) == null ) {
            return;
        }
        String usersLangPrefix = user.getLangPrefix();

        boolean userHasRights = DocumentMapper.checkUsersRights( imcref, user, ""+parentMetaId, usersLangPrefix, doc_type );

        // So... if the user may not create this particular doc-type... he's outta here!
        if( !userHasRights ) {
            String output = AdminDoc.adminDoc( parentMetaId, parentMetaId, user, req, res );
            if( output != null ) {
                out.write( output );
            }
            return;
        }



        // Lets fix the date information (date_created, modified etc)
        metaprops.setProperty( "date_modified", DateHelper.DATE_TIME_FORMAT_IN_DATABASE.format( nowDateTime ) );
        metaprops.setProperty( "date_created", DateHelper.DATE_TIME_FORMAT_IN_DATABASE.format( nowDateTime ) );
        metaprops.setProperty( "owner_id", String.valueOf( user.getUserId() ) );

        if( pressedOkButton( req ) ) {
            // Lets add a new meta to the db

            String metaIdStr = DocumentMapper.sqlInsertIntoMeta( imcref, doc_type, activated_datetime, archived_datetime, metaprops );
            int metaId = Integer.parseInt(metaIdStr) ;

            // Save the classifications to the db
            if( classification != null ) {
                DocumentMapper.sprocSaveClassification( imcref, metaId, classification );
            }

            DocumentMapper.sprocUpdateInheritPermissions( imcref, metaId, parentMetaId, Integer.parseInt(doc_type) );

            // Lets add the sortorder to the parents childlist
            DocumentMapper.addDocumentToMenu(imcref,user,parentMetaId,menuIndex,metaId);

            // Lets update the parents created_date

            String dateModifiedStr = metaprops.getProperty( "date_modified" );

            Date dateModified = DateHelper.createDateObjectFromString( dateModifiedStr );
            DocumentMapper.sqlUpdateModifiedDate( imcref, parentMetaId, dateModified );

            //lets log to mainLog the stuff done
            mainLog.info( "Document [" + metaId + "] of type [" + doc_type + "] created on [" + parentMetaId + "] by user: [" + user.getFullName() + "]" );

            //ok lets handle the the section stuff save to db and so on
            //lets start an see if we got any request to change the inherit one
            String section_id = req.getParameter( "change_section" );
            if( section_id == null ) {
                //ok it vas null so lets try and get the inherit one
                section_id = req.getParameter( "current_section_id" );
            }
            //ok if we have one lets update the db
            if( section_id != null ) {
                DocumentMapper.sprocSectionAddCrossref( imcref, metaId, Integer.parseInt(section_id) );
            }

            // Here is the stuff we have to do for each individual doctype. All general tasks
            // for all documenttypes is done now.

            int docTypeInt = Integer.parseInt(doc_type) ;

            switch (docTypeInt) {
                case DocumentDomainObject.DOCTYPE_BROWSER:
                    outputEditPageForNewBrowserRedirectDocument( metaId, parentMetaId, imcref, doc_type, usersLangPrefix, out );
                    break ;
                case DocumentDomainObject.DOCTYPE_FILE:
                    outputEditPageForNewFileDocument( usersLangPrefix, imcref, metaId, parentMetaId, out );
                    break;
                case DocumentDomainObject.DOCTYPE_URL:
                    outputEditPageForNewUrlDocument( metaId, parentMetaId, imcref, usersLangPrefix, out );
                    break;
                case DocumentDomainObject.DOCTYPE_HTML:
                    outputEditPageForNewHtmlDocument( metaId, parentMetaId, imcref, usersLangPrefix, out );
                    break;
                case DocumentDomainObject.DOCTYPE_TEXT:
                    outputNewTextDocumentInAdminMode( imcref, user, parentMetaId, metaId, req, doc_type, metaprops, res, out );
                    break;
                default:
                    redirectToExternalDocType( imcref, metaId, user, parentMetaId, res );
            }
        } else {
            String htmlStr = AdminDoc.adminDoc( parentMetaId, parentMetaId, user, req, res );
            out.write( htmlStr );
        }
    }

    private void outputNewTextDocumentInAdminMode( IMCServiceInterface imcref,
            UserDomainObject user, int parentMetaId, int metaId, HttpServletRequest req,
            String doc_type, Properties metaprops, HttpServletResponse res, Writer out ) throws IOException {
        DocumentMapper.copyTemplateData( imcref, user, ""+parentMetaId, ""+metaId );

        // Lets check if we should copy the metaheader and meta text into text1 and text2.
        // There are 2 types of texts. 1= html text. 0= plain text. By
        // default were creating html texts.
        String copyMetaHeader = req.getParameter( "copyMetaHeader" );
        String copyMetaFlag = (copyMetaHeader == null) ? "0" : (copyMetaHeader);
        if( copyMetaFlag.equals( "1" ) && doc_type.equals( "2" ) ) {
            String[] vp = {"'", "''"};

            String mHeadline = Parser.parseDoc( metaprops.getProperty( "meta_headline" ), vp );
            String mText = Parser.parseDoc( metaprops.getProperty( "meta_text" ), vp );

            DocumentMapper.sqlInsertIntoTexts( imcref, ""+metaId, mHeadline, mText );
        }

        // Lets activate the textfield
        DocumentMapper.sqlUpdateActivateTheTextField( imcref, metaId );

        // Lets build the page
        String htmlStr = AdminDoc.adminDoc( metaId, metaId, user, req, res );
        out.write( htmlStr );
        return;
    }

    private void outputEditPageForNewBrowserRedirectDocument( int metaId, int parentMetaId,
            IMCServiceInterface imcref, String doc_type, String usersLangPrefix, Writer out ) throws IOException {
        String htmlStr;
        String sqlStr = "insert into browser_docs (metaId, to_meta_id, browser_id) values (" + metaId + "," + parentMetaId + ",0)";
        imcref.sqlUpdateQuery( sqlStr );
        Vector vec = new Vector();
        sqlStr = "select name,browsers.browser_id,to_meta_id from browser_docs join browsers on browsers.browser_id = browser_docs.browser_id where metaId = " + metaId + " order by value desc,name asc";
        Hashtable hash = imcref.sqlQueryHash( sqlStr );
        String[] b_id = (String[])hash.get( "browser_id" );
        String[] nm = (String[])hash.get( "name" );
        String[] to = (String[])hash.get( "to_meta_id" );
        String bs = "";
        if( b_id != null ) {
            bs += "<table width=\"50%\" border=\"0\">";
            for( int i = 0; i < b_id.length; i++ ) {
                String[] temparr = {" ", "&nbsp;"};
                bs += "<tr><td>" + Parser.parseDoc( nm[i], temparr ) + ":</td><td><input type=\"text\" name=\"bid" + b_id[i] + "\" value=\"" + (to[i].equals( "0" ) ? "" : to[i]) + "\"></td></tr>";
            }
            bs += "</table>";
        }
        vec.add( "#browsers#" );
        vec.add( bs );
        sqlStr = "select browser_id,name from browsers where browser_id not in (select browsers.browser_id from browser_docs join browsers on browsers.browser_id = browser_docs.browser_id where metaId = " + metaId + " ) order by value desc,name asc";
        hash = imcref.sqlQueryHash( sqlStr );
        b_id = (String[])hash.get( "browser_id" );
        nm = (String[])hash.get( "name" );
        String nb = "";
        if( b_id != null ) {
            for( int i = 0; i < b_id.length; i++ ) {
                nb += "<option value=\"" + b_id[i] + "\">" + nm[i] + "</option>";
            }
        }
        vec.add( "#new_browsers#" );
        vec.add( nb );
        vec.add( "#new_meta_id#" );
        vec.add( String.valueOf( metaId ) );
        vec.add( "#getDocType#" );
        vec.add( "<INPUT TYPE=\"hidden\" NAME=\"doc_type\" VALUE=\"" + doc_type + "\">" );
        vec.add( "#DocMenuNo#" );
        vec.add( "" );
        vec.add( "#getMetaId#" );
        vec.add( String.valueOf( parentMetaId ) );
        htmlStr = imcref.parseDoc( vec, "new_browser_doc.html", usersLangPrefix );
        out.write(htmlStr) ;
        return ;
    }

    private void outputEditPageForNewFileDocument( String usersLangPrefix, IMCServiceInterface imcref,
            int metaId, int parentMetaId, Writer out ) throws IOException {
        String htmlStr;
        String sqlStr = "select mime,mime_name from mime_types where lang_prefix = '" + usersLangPrefix + "' and mime != 'other'";
        String temp[] = imcref.sqlQuery( sqlStr );
        Vector vec = new Vector();
        String temps = null;
        for( int i = 0; i < temp.length; i += 2 ) {
            temps += "<option value=\"" + temp[i] + "\">" + temp[i + 1] + "</option>";
        }
        vec.add( "#mime#" );
        vec.add( temps );
        vec.add( "#new_meta_id#" );
        vec.add( String.valueOf( metaId ) );
        vec.add( "#getMetaId#" );
        vec.add( String.valueOf( parentMetaId ) );
        htmlStr = imcref.parseDoc( vec, "new_fileupload.html", usersLangPrefix );
        out.write( htmlStr );
        return;
    }

    private void outputEditPageForNewUrlDocument( int metaId, int parentMetaId, IMCServiceInterface imcref,
            String usersLangPrefix, Writer out ) throws IOException {
        String htmlStr;
        Vector vec = new Vector();
        vec.add( "#new_meta_id#" );
        vec.add( String.valueOf( metaId ) );
        vec.add( "#getMetaId#" );
        vec.add( String.valueOf( parentMetaId ) );
        htmlStr = imcref.parseDoc( vec, "new_url_doc.html", usersLangPrefix );
        out.write( htmlStr );
        return;
    }

    private void outputEditPageForNewHtmlDocument( int metaId, int parentMetaId,
            IMCServiceInterface imcref, String usersLangPrefix, Writer out ) throws IOException {
        String htmlStr;
        Vector vec = new Vector();
        vec.add( "#new_meta_id#" );
        vec.add( String.valueOf( metaId ) );
        vec.add( "#getMetaId#" );
        vec.add( String.valueOf( parentMetaId ) );
        htmlStr = imcref.parseDoc( vec, "new_frameset.html", usersLangPrefix );
        out.write( htmlStr );
        return;
    }

    private void redirectToExternalDocType( IMCServiceInterface imcref, int metaId,
            UserDomainObject user, int parentMetaId, HttpServletResponse res ) throws IOException {
        // check if external doc
        ExternalDocType ex_doc;
        ex_doc = imcref.isExternalDoc( metaId, user );
        String paramStr = "?metaId=" + metaId + "&";
        paramStr += "parent_meta_id=" + parentMetaId + "&";
        paramStr += "cookie_id=" + "1A" + "&action=new";
        res.sendRedirect( ex_doc.getCallServlet() + paramStr );
    }

    private boolean pressedOkButton( HttpServletRequest req ) {
        return req.getParameter( "ok" ) != null;
    }

}
