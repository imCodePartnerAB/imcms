package com.imcode.imcms.servlet.conference;

/*
 *
 * @(#)ConfDiscView.java
 *
 *
 *
 * Copyright (c)
 *
*/

import java.io.*;
import java.util.*;
import javax.servlet.*;
import javax.servlet.http.*;

import imcode.external.diverse.*;
import imcode.server.user.UserDomainObject;
import imcode.util.Utility;

/**
 * Html template in use:
 * <p/>
 * <p/>
 * Html parstags in use:
 * <p/>
 * stored procedures in use:
 * -
 *
 * @author Rickard Larsson
 * @version 1.0 21 Nov 2000
 */

public class ConfDiscView extends Conference {

    private final static String HTML_TEMPLATE = "Conf_Disc_View.htm";         // the relative path from web root to where the servlets are

    public void doGet( HttpServletRequest req, HttpServletResponse res ) throws ServletException, IOException {

        // Lets get the standard parameters and validate them
        // Properties params = super.getParameters(req) ;

        // Lets get the standard SESSION parameters and validate them
        Properties params = MetaInfo.createPropertiesFromMetaInfoParameters( super.getConferenceSessionParameters( req ) );

        UserDomainObject user = Utility.getLoggedOnUser( req );
        if ( !isUserAuthorized( req, res, user ) ) {
            return;
        }

        // Lets get all parameters in a string which we'll send to every
        // servlet in the frameset
        String paramStr = MetaInfo.passMeta( params );


        // Lets build the Responsepage
        VariableManager vm = new VariableManager();
        vm.addProperty( "CONF_DISC", "ConfDisc?" + paramStr );
        vm.addProperty( "CONF_REPLY", "ConfReply?" + paramStr );

        this.sendHtml( req, res, vm, HTML_TEMPLATE );
        //	log("Nu är ConfDiscView klar") ;
        return;
    }

    /**
     * Log function, will work for both servletexec and Apache
     */

    public void log( String str ) {
        super.log( str );
        System.out.println( "ConfDiscView: " + str );
    }
} // End of class
