package com.imcode.imcms.servlet.billboard;

/*
 *
 * @(#)BillBoardDiscView.java
 *
 *
 *
 * Copyright (c)
 *
 */

import imcode.server.*;
import imcode.server.user.UserDomainObject;

import java.io.*;
import java.util.*;
import javax.servlet.*;
import javax.servlet.http.*;

import imcode.external.diverse.*;
import imcode.util.Utility;

import java.rmi.*;
import java.rmi.registry.*;

import com.imcode.imcms.servlet.billboard.BillBoard;

/**
 * Html template in use:
 * BillBoard_Disc_View.htm
 * <p/>
 * Html parstags in use:
 * #BILLBOARD_DISC#
 * #BILLBOARD_REPLY#
 * <p/>
 * stored procedures in use:
 * -
 * 
 * @author Rickard Larsson REBUILD TO BillBoardDiscView BY Peter Östergren
 * @version 1.2 20 Aug 2001
 */

public class BillBoardDiscView extends BillBoard {//ConfDiscView

    private String HTML_TEMPLATE;         // the relative path from web root to where the servlets are

    public void doGet( HttpServletRequest req, HttpServletResponse res )
            throws ServletException, IOException {
        // Lets get the standard parameters and validate them
        // Properties params = super.getParameters(req) ;

        // Lets get the standard SESSION parameters and validate them
        Properties params = MetaInfo.createPropertiesFromMetaInfoParameters( super.getBillBoardSessionParameters( req ) );

        // Lets get an user object

        imcode.server.user.UserDomainObject user = Utility.getLoggedOnUser( req );
        if ( user == null ) return;

        if ( !isUserAuthorized( req, res, user ) ) {
            log( "isUserAuthorized = false so return" );
            return;
        }


        // Lets build the Responsepage
        VariableManager vm = new VariableManager();


        // Lets get all parameters in a string which we'll send to every
        // servlet in the frameset
        String paramStr = MetaInfo.passMeta( params );
        String extParam = "";
        //now we have an ugly ugly ugly part, I'm gonna rewrite it some time but untill then its ugly
        if ( req.getParameter( "MAIL_SENT" ) != null ) {
            extParam = "&MAIL_SENT=OK";
        }

        if ( req.getParameter( "ADDTYPE" ) != null ) {
            paramStr += "&ADDTYPE=" + req.getParameter( "ADDTYPE" );
        }

        if ( req.getParameter( "PREVIEWMODE" ) != null ) {
            extParam += "&PREVIEWMODE=OK";
        }
        if ( req.getParameter( "DISCPREV" ) != null ) {
            vm.addProperty( "BILLBOARD_DISC", "BillBoardAdd?ADD=ok" + paramStr );
        } else {
            vm.addProperty( "BILLBOARD_DISC", "BillBoardDisc?" + paramStr );

        }

        vm.addProperty( "BILLBOARD_REPLY", "BillBoardReply?" + paramStr + extParam );

        this.sendHtml( req, res, vm, HTML_TEMPLATE );
        return;
    }

    /**
     * Detects paths and filenames.
     */

    public void init( ServletConfig config ) throws ServletException {
        super.init( config );
        HTML_TEMPLATE = "billboard_disc_view.htm";//Conf_Disc_View.htm

    }

    /**
     * Log function, will work for both servletexec and Apache
     */

    public void log( String msg ) {
        super.log( "BillBoardDiscView: " + msg );

    }
} // End of class
