package com.imcode.imcms.servlet.conference;

import imcode.external.diverse.VariableManager;
import imcode.server.Imcms;
import imcode.server.user.UserDomainObject;
import imcode.util.Html;
import imcode.util.Utility;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.Vector;

public class ConfForum extends Conference {

    private final static String ADMIN_LINK_TEMPLATE = "conf_forum_admin_link.htm";

    private final static String HTML_TEMPLATE = "conf_forum.htm";
    private final static String HTML_TEMPLATE_EXT = "conf_forum_ext.htm";

    public void doPost( HttpServletRequest req, HttpServletResponse res ) throws ServletException, IOException {

        String htmlFile = HTML_TEMPLATE;
        if ( req.getParameter( "advancedView" ) != null ) htmlFile = HTML_TEMPLATE_EXT;

        UserDomainObject user = Utility.getLoggedOnUser( req );
        if ( !isUserAuthorized( req, res, user ) ) {
            return;
        }

        HttpSession session = req.getSession( false );
        String aMetaId = (String)session.getAttribute( "Conference.meta_id" );

        // Lets get the information from DB
        String[] sqlAnswer = Imcms.getServices().getExceptionUnhandlingDatabase().executeArrayProcedure( "A_GetAllForum", new String[] {aMetaId} );
        Vector forumV = super.convert2Vector( sqlAnswer );

        // Lets fill the select box
        String forumList = Html.createOptionList( forumV, "" );

        // Lets build the Responsepage
        VariableManager vm = new VariableManager();
        vm.addProperty( "FORUM_LIST", forumList );
        vm.addProperty( "ADMIN_LINK_HTML", ADMIN_LINK_TEMPLATE );

        this.sendHtml( req, res, vm, htmlFile );

    }

    public void doGet( HttpServletRequest req, HttpServletResponse res ) throws ServletException, IOException {
        this.doPost( req, res );
    }

} // End of class
