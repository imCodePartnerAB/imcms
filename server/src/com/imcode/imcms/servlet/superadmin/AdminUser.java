package com.imcode.imcms.servlet.superadmin;

import com.imcode.imcms.servlet.admin.UserFinder;
import imcode.server.ApplicationServer;
import imcode.server.IMCServiceInterface;
import imcode.server.WebAppGlobalConstants;
import imcode.server.user.UserDomainObject;
import imcode.util.Utility;
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

    public void doGet( HttpServletRequest req, HttpServletResponse res ) throws ServletException, IOException {

        IMCServiceInterface imcref = ApplicationServer.getIMCServiceInterface();
        UserDomainObject user = Utility.getLoggedOnUser( req );

        // Lets verify that the user is an admin, otherwise throw him out.
        if ( !user.isSuperAdmin() && !user.isUserAdmin() ) {
            String header = "Error in AdminUser.";
            Properties langproperties = imcref.getLanguageProperties( user );
            String msg = langproperties.getProperty( "error/servlet/global/no_administrator" ) + "<br>";
            log.debug( header + "- user is not an administrator" );
            new AdminError( req, res, header, msg );
            return;
        }

        UserFinder userFinder = (UserFinder) UserFinder.getInstance( req );
        if ( null != userFinder.getSelectedUser() ) {
            gotoChangeUser(req, res, user, userFinder.getSelectedUser() );
        } else {
            userFinder.setUsersAddable( true );
            userFinder.setNullSelectable( false );
            userFinder.setSelectButton( UserFinder.SELECT_BUTTON__EDIT_USER );
            userFinder.setForwardReturnUrl( "AdminUser" );
            userFinder.forward( req, res );
        }
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
            req.getSession().setAttribute( "userToChange", ""+userToChange.getId() );
            res.sendRedirect( "AdminUserProps?CHANGE_USER=true" );
        }
    }

}
