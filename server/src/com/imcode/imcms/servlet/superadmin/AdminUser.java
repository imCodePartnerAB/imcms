package com.imcode.imcms.servlet.superadmin;

import com.imcode.imcms.servlet.admin.UserFinder;
import com.imcode.imcms.servlet.WebComponent;
import com.imcode.imcms.flow.DispatchCommand;
import com.imcode.imcms.flow.DispatchCommand;
import imcode.server.ApplicationServer;
import imcode.server.IMCServiceInterface;
import imcode.server.WebAppGlobalConstants;
import imcode.server.user.UserDomainObject;
import imcode.util.Utility;
import imcode.util.LocalizedMessage;
import org.apache.log4j.Logger;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Properties;

public class AdminUser extends Administrator {

    private final static Logger log = Logger.getLogger( AdminUser.class.getName() );
    private String CHANGE_EXTERNAL_USER_URL = "/jsp/changeexternaluser.jsp";
    private static final LocalizedMessage BUTTON_TEXT__EDIT_USER = new LocalizedMessage( "templates/sv/AdminChangeUser.htm/2006" );
    private static final LocalizedMessage HEADLINE__EDIT_USER = new LocalizedMessage( "templates/sv/AdminChangeUser.htm/4/1" );

    public void doGet( HttpServletRequest req, HttpServletResponse res ) throws ServletException, IOException {

        IMCServiceInterface imcref = ApplicationServer.getIMCServiceInterface();
        final UserDomainObject user = Utility.getLoggedOnUser( req );

        // Lets verify that the user is an admin, otherwise throw him out.
        if ( !user.isSuperAdmin() && !user.isUserAdmin() ) {
            String header = "Error in AdminUser.";
            Properties langproperties = imcref.getLanguageProperties( user );
            String msg = langproperties.getProperty( "error/servlet/global/no_administrator" ) + "<br>";
            log.debug( header + "- user is not an administrator" );
            new AdminError( req, res, header, msg );
            return;
        }

        UserFinder userFinder = new UserFinder();
        userFinder.setUsersAddable( true );
        userFinder.setHeadline( HEADLINE__EDIT_USER ) ;
        userFinder.setSelectButtonText( BUTTON_TEXT__EDIT_USER );
        userFinder.setSelectUserCommand( new UserFinder.SelectUserCommand() {
            public void selectUser( UserDomainObject selectedUser, HttpServletRequest request,
                                    HttpServletResponse response ) throws ServletException, IOException {
                gotoChangeUser( request, response, user, selectedUser );
            }
        } );
        userFinder.setCancelCommand( new DispatchCommand() {
            public void dispatch( HttpServletRequest request, HttpServletResponse response ) throws IOException, ServletException {
                request.getRequestDispatcher( "AdminManager" ).forward( request, response );
            }
        } );
        userFinder.forward( req, res );
    }

    public void doPost( HttpServletRequest request, HttpServletResponse response ) throws ServletException, IOException {
        response.sendRedirect( "AdminManager" );
    }

    private void gotoChangeUser( HttpServletRequest req, HttpServletResponse res, UserDomainObject user,
                                 UserDomainObject userToChange ) throws IOException, ServletException {
        if ( !userToChange.isImcmsExternal() ) {
            redirectChangeUser( req, res, user, userToChange );
        } else {
            String queryString = "?"
                                 + java.net.URLEncoder.encode( WebAppGlobalConstants.USER_LOGIN_NAME_PARAMETER_NAME, "UTF-8" )
                                 + "="
                                 + java.net.URLEncoder.encode( userToChange.getLoginName(), "UTF-8" );
            RequestDispatcher rd = req.getRequestDispatcher( "/imcms/" + user.getLanguageIso639_2()
                                                             + CHANGE_EXTERNAL_USER_URL
                                                             + queryString );
            rd.forward( req, res );
        }
    }

    private void redirectChangeUser( HttpServletRequest req, HttpServletResponse res, UserDomainObject user,
                                     UserDomainObject userToChange ) throws IOException {

        if ( !user.isSuperAdmin() && !user.isUserAdmin() && !userToChange.equals( user ) ) {
            String header = "Error in AdminUser, change user.";
            Properties langproperties = ApplicationServer.getIMCServiceInterface().getLanguageProperties( user );
            String msg = langproperties.getProperty( "error/servlet/AdminUser/user_have_no_permission" ) + "<br>";
            log.debug( header + "- user have no permission to edit user values" );
            new AdminError( req, res, header, msg );
        } else {
            req.getSession().setAttribute( "userToChange", "" + userToChange.getId() );
            res.sendRedirect( "AdminUserProps?CHANGE_USER=true" );
        }
    }

}
