package com.imcode.imcms.servlet.chat;

import imcode.external.chat.ChatBase;
import imcode.external.chat.ChatError;
import imcode.server.Imcms;
import imcode.server.ImcmsServices;
import imcode.server.document.DocumentMapper;
import imcode.server.document.DocumentDomainObject;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Properties;
import java.util.Vector;

public class ChatHelp extends ChatBase {

    private final static String USER_TEMPLATE = "conf_help_user.htm";
    private final static String ADMIN_TEMPLATE = "conf_help_admin.htm";
    private final static String ADMIN_TEMPLATE2 = "conf_help_admin2.htm";

    public void doPost(HttpServletRequest req, HttpServletResponse res)
            throws ServletException, IOException {

	// Lets get all parameters for this servlet
	Properties params = this.getParameters(req) ;

	// Lets get the user object
	imcode.server.user.UserDomainObject user = super.getUserObj(req ) ;
	if(user == null) return ;

	if ( isUserAuthorized( req, res, user ) ) {

		String file = "";

		// Lets create the path to our html page
		if ( params.getProperty("HELP_MODE").equalsIgnoreCase("USER") ) {
			file = USER_TEMPLATE	;
		} else if ( params.getProperty("HELP_MODE").equalsIgnoreCase("ADMIN") ) {
			//lets see if user has adminrights
			int metaId = getMetaId( req );
            ImcmsServices imcref = Imcms.getServices() ;
            DocumentMapper documentMapper = imcref.getDocumentMapper();
            DocumentDomainObject document = documentMapper.getDocument(metaId);
            if ( user.canEdit( document ) ) {
				file = ADMIN_TEMPLATE ;
				if( params.getProperty("HELP_AREA").equalsIgnoreCase("TEMPLATESPEC") ) {
					file = ADMIN_TEMPLATE2;
				    }
                } else {
                    String header = "ConfHelp servlet. ";
                    new ChatError( req, res, header, 6 );
                    return;
			    }
		    }

		//if( params.getProperty("HELP_SPEC").equalsIgnoreCase("SPEC") ) file = ADMIN_TEMPLATE2 ;
		//			this.sendHtml(req,res,vm, file) ;
		this.sendHtml(req,res,new Vector(), file, null) ;
		return ;

        } else {
		return;
	    }

    } //DoPost

    /**
     Collects all the parameters used by this servlet
     **/

    private Properties getParameters( HttpServletRequest req ) {

        Properties params = super.getSessionParameters( req );

        // Lets get the EXTENDED SESSION PARAMETERS
        super.getExtSessionParameters( req, params );

        // Lets get our REQUESTPARAMETERS
        String helpInfo = ( req.getParameter( "helparea" ) == null ) ? "" : ( req.getParameter( "helparea" ) );
        String helpMode = ( req.getParameter( "helpmode" ) == null ) ? "" : ( req.getParameter( "helpmode" ) );
        //String helpSpec = (req.getParameter("helpspec")==null) ? "-1" : (req.getParameter("helpspec")) ;

        //params.setProperty("HELP_SPEC", helpSpec) ;
        params.setProperty( "HELP_AREA", helpInfo );
        params.setProperty( "HELP_MODE", helpMode );
        //log(req.getParameter("helpmode"));
        return params;
    }

    /**
     Service method. Sends the user to the post method
     **/

    public void service( HttpServletRequest req, HttpServletResponse res )
            throws ServletException, IOException {

        String action = req.getMethod();
        // log("Action:" + action) ;
        if ( action.equals( "POST" ) )
            this.doPost( req, res );
        else
            this.doPost( req, res );
    }

    /**
     Log function, will work for both servletexec and Apache
     **/

    public void log( String str ) {
        super.log( "ChatHelp: " + str );
    }

} // End of class
