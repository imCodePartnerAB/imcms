package com.imcode.imcms.servlet.conference;

import imcode.external.diverse.MetaInfo;
import imcode.server.user.UserDomainObject;
import imcode.util.Utility;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.Properties;

import com.imcode.imcms.servlet.conference.Conference;
import com.imcode.imcms.servlet.conference.ConfError;

public class ConfManager extends Conference {

    /**
     * The GET method creates the html page when this side has been
     * redirected from somewhere else.
     */

    public void doGet( HttpServletRequest req, HttpServletResponse res ) throws ServletException, IOException {

        MetaInfo.Parameters params = MetaInfo.getParameters( req );

        UserDomainObject user = Utility.getLoggedOnUser( req );
        int testMetaId = params.getMetaId();
        if ( !isUserAuthorized( req, res, testMetaId, user ) ) {
            return;
        }

        String action = req.getParameter( "action" );
        //log("ConfManager is in action...") ;
        if ( action == null ) {
            String header = "ConfManager servlet. ";
            ConfError err = new ConfError( req, res, header, 3 );
            log( header + err.getErrorMsg() );
            return;
        }

        // ********* NEW ********
        if ( action.equalsIgnoreCase( "NEW" ) ) {
            log( "Lets add a conference" );
            HttpSession session = req.getSession( false );
            if ( session != null ) {
                // log("Ok nu sätter vi metavärdena");
                setSessionAttributes( session, params );
            }

            String url = "ConfCreator?action=NEW";
            //log("Redirect till:" + url) ;
            res.sendRedirect( url );
            return;
        }

        // ********* VIEW ********
        if ( action.equalsIgnoreCase( "VIEW" ) ) {

            // Lets get userparameters
            String userId = "" + user.getId();

            // Lets detect which type of user we got
            String loginType = user.getLoginType();

            // We got 3 usertypes: 0= specialusers, 1=normal, 2=confernce
            // We got 3 logintypes: "Extern"=web users, "ip_access"= people from a certain ip nbr
            // and "verify" = people who has logged into the system

            if ( !loginType.equalsIgnoreCase( "VERIFY" ) ) {
                // Lets store  the standard metavalues in his session object
                HttpSession session = req.getSession( false );
                if ( session != null ) {
                    // log("Ok nu sätter vi metavärdena");
                    setSessionAttributes( session, params );
                    session.setAttribute( "Conference.viewedDiscList", new Properties() );
                    log( "OK, nu sätter vi viewedDiscList" );
                }

                String loginPage = "ConfLogin?login_type=login";
                //log("Redirect till:" + loginPage) ;
                res.sendRedirect( loginPage );
                return;
            }

            log( "Ok, användaren har loggat in, förbered honom för konferensen" );
            //  Lets update the users sessionobject with a with a ok login to the conference
            //	Send him to the manager with the ability to get in
            if ( !super.prepareUserForConf( req, res, params, userId ) ) {
                log( "Error in prepareUserFor Conf" );
            }

            return;
        } // End of View

        // ********* CHANGE ********
        if ( action.equalsIgnoreCase( "CHANGE" ) ) {
            String url = "ChangeExternalDoc2?" + MetaInfo.passMeta( params ) + "&metadata=meta";
            res.sendRedirect( url );
            return;
        } // End if

    } // End doGet

    /**
     * Log function, will work for both servletexec and Apache
     */

    public void log( String str ) {
        super.log( str );
        System.out.println( "ConfManager: " + str );
    }

} // End of class
