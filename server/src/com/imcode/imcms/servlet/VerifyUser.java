package com.imcode.imcms.servlet;

import imcode.server.Imcms;
import imcode.server.ImcmsServices;
import imcode.server.user.UserDomainObject;
import imcode.util.Utility;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

/**
 * Verify a user.
 */
public class VerifyUser extends HttpServlet {

    /**
     * doGet()
     */

    public void doGet( HttpServletRequest req, HttpServletResponse res ) throws ServletException, IOException {
        doPost( req, res );
        return;
    }
    /** end of doGet() */


    /**
     * doPost()
     */
    public void doPost( HttpServletRequest req, HttpServletResponse res ) throws ServletException, IOException {
        Utility.setDefaultHtmlContentType( res );

        String name = req.getParameter( "name" );
        String passwd = req.getParameter( "passwd" );
        String accessDeniedUrl = req.getContextPath()+"/imcms/"+Utility.getLoggedOnUser( req ).getLanguageIso639_2()+"/login/access_denied.jsp" ;
        String nexturl;

        // Check the name and password for validity
        ImcmsServices imcref = Imcms.getServices();
        UserDomainObject user = imcref.verifyUser( name, passwd );

        // Get session
        HttpSession session = req.getSession( true );

        // if we don't have got any user from DefaultImcmsServices lets check out next url for redirect
        if ( user == null ) {

            nexturl = accessDeniedUrl;

            // lets set session next_meta if we have got any from request, we will use it later when
            // login is successfull
            if ( req.getParameter( "next_meta" ) != null ) {
                session.setAttribute( "next_meta", req.getParameter( "next_meta" ) );
            }

            // or lets set session next_url if we have got any from request, we will use it later when
            // login is successfull
            else if ( req.getParameter( "next_url" ) != null ) {
                session.setAttribute( "next_url", req.getParameter( "next_url" ) );
            }

            // lets get different access_denied url instead of the default url
            if ( req.getParameter( "access_denied_url" ) != null ) {
                nexturl = req.getParameter( "access_denied_url" );
            }
            res.sendRedirect( nexturl );
            return;

        } else { // we have a valid user

            // Valid login.  Make a note in the session object.
            session.setAttribute( "logon.isDone", user );  // just a marker object

            String value = req.getHeader( "User-Agent" );
            session.setAttribute( "browser_id", value );

            user.setLoginType( "verify" );

            // Lets now find out nexturl to redirect the user
            nexturl = "StartDoc";  // default value

            // if user have pushed button "Ändra" from login page
            if ( req.getParameter( "Ändra" ) != null ) {

                // don't allow "user" "user" ( User Extern ) id=2 to be changed
                if(user.getId()== 2 || user.getLoginName().equals("user")){
                    res.sendRedirect( accessDeniedUrl );
                    return;
                }
                //if next_url was passed
                if ( req.getParameter( "next_url" ) != null ) {
                    nexturl = req.getParameter( "next_url" );
                }
                //or if next_meta was passed
                else if ( req.getParameter( "next_meta" ) != null ) {
                    nexturl = "GetDoc?meta_id=" + req.getParameter( "next_meta" );
                }

                session.setAttribute( "userToChange", "" + user.getId() );
                session.setAttribute( "next_url", nexturl );

                res.sendRedirect( "AdminUserProps?CHANGE_USER=true" );
                return;

            } else {

                // lets check if we have got a next_meta by session
                if ( session.getAttribute( "next_meta" ) != null ) {
                    nexturl = "GetDoc?meta_id=" + session.getAttribute( "next_meta" );
                    session.removeAttribute( "next_meta" );
                }

                // or if we have got next_url by session
                else if ( session.getAttribute( "next_url" ) != null ) {
                    nexturl = (String)session.getAttribute( "next_url" );
                    session.removeAttribute( "next_url" );
                }
                // or if we have got next_url from request object
                else if ( req.getParameter( "next_url" ) != null ) {
                    nexturl = req.getParameter( "next_url" );
                }
                //or if we have got next_meta from request object
                else if ( req.getParameter( "next_meta" ) != null ) {
                    nexturl = "GetDoc?meta_id=" + req.getParameter( "next_meta" );
                }

                // or try redirecting the client to the page he first tried to access
                else if ( session.getAttribute( "login.target" ) != null ) {
                    nexturl = (String)session.getAttribute( "login.target" );
                    session.removeAttribute( "login.target" );


                    // Couldn't redirect to the target.  Redirect to the site's home page.
                } else {
                    nexturl = "StartDoc";
                }

                res.sendRedirect( nexturl );
                return;

            } // end else
        } // end else

    } // end doPost()
} // end class

