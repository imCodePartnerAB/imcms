package com.imcode.imcms.servlet.billboard;

import imcode.external.diverse.MetaInfo;
import imcode.external.diverse.VariableManager;
import imcode.server.ApplicationServer;
import imcode.server.HTMLConv;
import imcode.server.IMCServiceInterface;
import imcode.server.user.UserDomainObject;
import imcode.util.Utility;
import imcode.util.net.SMTP;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.net.ProtocolException;
import java.util.Hashtable;
import java.util.Properties;

/**
 * Html template in use:
 * BillBoard_Add.htm
 * BillBoard_Add_Reply.htm
 * <p/>
 * Html parstags in use:
 * #ADD_TYPE#
 * #CURRENT_SECTION_NAME#
 * <p/>
 * stored procedures in use:
 * B_AddNewBill
 * B_GetLastDiscussionId
 * B_GetEmail
 * B_GetSubjectStr
 * B_AddReply
 * B_GetSectionName
 * 
 * @author Rickard Larsson
 * @author Jerker Drottenmyr
 * @author REBUILD TO BillBoardAdd BY Peter Östergren
 * @version 1.2 20 Aug 2001
 */

public class BillBoardAdd extends BillBoard {

    private String HTML_TEMPLATE;
    private String SERVLET_NAME;  // The name on this servlet
    private final static String sectionId = "sectionId";
    private final static String header = "header";
    private final static String text = "text";
    private final static String email = "email";

    public void doPost( HttpServletRequest req, HttpServletResponse res )
            throws ServletException, IOException {

        // Lets get all parameters for this servlet
        Properties params = this.getParameters( req );

        // Lets get the user object

        imcode.server.user.UserDomainObject user = Utility.getLoggedOnUser( req );
        if ( user == null ) return;

        if ( !isUserAuthorized( req, res, user ) ) {
            return;
        }


        // Lets detect which addtype we have
        String addType = "";
        addType = req.getParameter( "ADDTYPE" );

        IMCServiceInterface imcref = ApplicationServer.getIMCServiceInterface();

        int metaId = Integer.parseInt( params.getProperty( "META_ID" ) );
        if ( userHasRightToEdit( imcref, metaId, user ) ) {

            // ********* CANCEL ********
            if ( req.getParameter( "CANCEL" ) != null || req.getParameter( "CANCEL.x" ) != null ) {
                if ( req.getParameter( "DISCPREV" ) != null ) {//ok we want back to add mode with the olddata in the BILLBOARD_DISC frame
                    String addTypStr = "&ADDTYPE=" + req.getParameter( "ADDTYPE" );
                    res.sendRedirect( "BillBoardDiscView?DISCPREV=ok" + addTypStr );
                } else {
                    // Lets redirect to the servlet which holds in us.
                    res.sendRedirect( "BillBoardDiscView" );
                }

                return;
            }

            // ********* PREVIEW BILL ***********
            if ( req.getParameter( "PREVIEW" ) != null || req.getParameter( "PREVIEW.x" ) != null ) {

                //lets collect everything we might need later on, and store it in the session.

                String aSectionId = params.getProperty( "SECTION_ID" );

                HttpSession session = req.getSession( false );
                if ( session == null ) {
                    //lets get rid of the user
                    res.sendRedirect( "StartDoc" );
                    return;
                }

                //String addHeader = super.verifySqlText(params.getProperty("ADD_HEADER")) ;
                String addHeader = ( params.getProperty( "ADD_HEADER" ) ).trim();
                String addText = ( params.getProperty( "ADD_TEXT" ) ).trim();
                String addEpost = ( params.getProperty( "ADD_EPOST" ) ).trim();
                if ( addHeader.equals( "" ) || addText.equals( "" ) || addEpost.equals( "" ) ) {
                    //BillBoardError(HttpServletRequest req, HttpServletResponse res, String header, int errorCode)
                    log( "some fields was empty" );
                    new BillBoardError( req, res, 51, user );
                    return;
                }

                addText = super.verifySqlText( textMailLinkFix( addText ) );
                addHeader = super.verifySqlText( HTMLConv.toHTMLSpecial( addHeader ) );
                //addText = super.verifySqlText(HTMLConv.toHTMLSpecial(addText));
                addEpost = super.verifySqlText( HTMLConv.toHTMLSpecial( addEpost ) );

                if ( !validateEmail( addEpost ) ) {
                    log( "invalid epostadress" );
                    String header = SERVLET_NAME + " servlet. ";
                    new BillBoardError( req, res, header, 76, user.getLanguageIso639_2(), user );
                    return;
                }

                // Lets check the data size
                if ( addText.length() > 32000 ) {
                    String header = SERVLET_NAME + " servlet. ";
                    new BillBoardError( req, res, header, 75, user.getLanguageIso639_2(), user );
                    return;
                }

                // Ok, Lets store the stuff in a Hashtable, that we put in the session
                //fore later use
                if ( session != null ) {

                    Hashtable billPrevData = new Hashtable();
                    billPrevData.put( sectionId, aSectionId );
                    billPrevData.put( header, addHeader );
                    billPrevData.put( text, addText );
                    billPrevData.put( email, addEpost );
                    session.setAttribute( "billPrevData", billPrevData );
                } else {
                    //lets get rid of the user
                    res.sendRedirect( "StartDoc" );
                }

                //lets get the ADD_TYPE
                String addTypeStr = "&ADDTYPE=" + req.getParameter( "ADDTYPE" );
                // Lets redirect to the servlet which holds in us.
                res.sendRedirect( "BillBoardDiscView?PREVIEWMODE=OK" + addTypeStr );
                //log("PREVIEW is done in BillBoardAdd");
                return;

            }//end PREVIEW

            // ********* ADD DISCUSSION ********
            if ( addType.equalsIgnoreCase( "DISCUSSION" ) && ( req.getParameter( "ADD" ) != null || req.getParameter( "ADD.x" ) != null ) ) {
                //log("Nu är vi i AddDiscussion") ;
                // Lets add a new discussion to the database

                String userId = "";
                HttpSession session = req.getSession( false );
                if ( session != null ) {
                    userId = (String)session.getAttribute( "BillBoard.user_id" );//Conference.user_id
                } else {
                    //lets get rid of the user
                    res.sendRedirect( "StartDoc" );//ConfDiscView
                    return;
                }

                //the strings we need to save the bill
                String aSectionId;
                String addHeader;
                String addText;
                String addEpost;

                //här ska jag hämta ut prevdatan från sessionen och göra en stor if else sats
                Hashtable billPrevData = (Hashtable)session.getAttribute( "billPrevData" );
                if ( billPrevData != null ) {//ok we have to save a previewed one
                    session.removeAttribute( "billPrevData" );
                    aSectionId = (String)billPrevData.get( sectionId );
                    addHeader = (String)billPrevData.get( header );
                    addText = (String)billPrevData.get( text );
                    addEpost = (String)billPrevData.get( email );
                } else {//the old syntax were we dont preview the bill
                    aSectionId = params.getProperty( "SECTION_ID" );

                    //String addHeader = super.verifySqlText(params.getProperty("ADD_HEADER")) ;
                    addHeader = ( params.getProperty( "ADD_HEADER" ) ).trim();
                    addText = ( params.getProperty( "ADD_TEXT" ) ).trim();
                    addEpost = ( params.getProperty( "ADD_EPOST" ) ).trim();
                    if ( addHeader.equals( "" ) || addText.equals( "" ) || addEpost.equals( "" ) ) {
                        //BillBoardError(HttpServletRequest req, HttpServletResponse res, String header, int errorCode)
                        log( "some fields was empty" );
                        new BillBoardError( req, res, 51, user );
                        return;
                    }

                    addText = super.verifySqlText( textMailLinkFix( addText ) );
                    addHeader = super.verifySqlText( HTMLConv.toHTMLSpecial( addHeader ) );
                    //addText = super.verifySqlText(HTMLConv.toHTMLSpecial(addText));
                    addEpost = super.verifySqlText( HTMLConv.toHTMLSpecial( addEpost ) );

                    if ( !validateEmail( addEpost ) ) {
                        log( "invalid epostadress" );
                        String header = SERVLET_NAME + " servlet. ";
                        new BillBoardError( req, res, header, 76, user.getLanguageIso639_2(), user );
                        return;
                    }

                    // Lets check the data size
                    if ( addText.length() > 32000 ) {
                        String header = SERVLET_NAME + " servlet. ";
                        new BillBoardError( req, res, header, 75, user.getLanguageIso639_2(), user );
                        return;
                    }
                }

                // Ok, Lets add the discussion to DB
                String sqlQuest = "B_AddNewBill";
                imcref.sqlUpdateProcedure( sqlQuest, new String[]{aSectionId, userId, addHeader, addText, addEpost, req.getRemoteAddr()} );

                // Lets add the new discussion id to the session object
                // Ok, Lets get the last discussion in that section
                // HttpSession session = req.getSession(false) ;
                if ( session != null ) {
                    String latestDiscId = imcref.sqlProcedureStr( "B_GetLastDiscussionId", new String[]{params.getProperty( "META_ID" ), aSectionId} );
                    session.setAttribute( "BillBoard.disc_id", latestDiscId );
                }

                // Lets redirect to the servlet which holds in us.
                res.sendRedirect( "BillBoardDiscView" );
                return;
            }

            // ********* ADD REPLY ********
            // This is a workaround to fix the possibility to use gifs OR submit buttons

            if ( addType.equalsIgnoreCase( "REPLY" ) && ( req.getParameter( "ADD" ) != null || req.getParameter( "ADD.x" ) != null ) ) {

                // Lets add a new Reply to the database
                String discId = params.getProperty( "DISC_ID" );
                String userId = "";
                HttpSession session = req.getSession( false );
                if ( session != null ) {
                    userId = (String)session.getAttribute( "BillBoard.user_id" );//Conference.user_id
                }

                String addHeader;
                String addText;
                String addEpost;

                Hashtable billPrevData = (Hashtable)session.getAttribute( "billPrevData" );
                if ( billPrevData != null ) {//ok we have to save a previewed one
                    session.removeAttribute( "billPrevData" );
                    //	aSectionId = (String) billPrevData.get(sectionId );
                    addHeader = (String)billPrevData.get( header );
                    addText = (String)billPrevData.get( text );
                    addEpost = (String)billPrevData.get( email );
                } else {//the old syntax were we dont preview the bill
                    // Lets verify the textfields
                    //String addHeader = super.verifySqlText(params.getProperty("ADD_HEADER")) ;
                    //String addText = super.verifySqlText(params.getProperty("ADD_TEXT")) ;
                    addHeader = ( params.getProperty( "ADD_HEADER" ) ).trim();
                    addText = ( params.getProperty( "ADD_TEXT" ) ).trim();
                    addEpost = ( params.getProperty( "ADD_EPOST" ) ).trim();
                    if ( addHeader.equals( "" ) || addText.equals( "" ) || addEpost.equals( "" ) ) {
                        new BillBoardError( req, res, 51, user );
                        return;
                    }

                    addHeader = super.verifySqlText( HTMLConv.toHTMLSpecial( addHeader ) );

                    addText = super.verifySqlText( textMailLinkFix( addText ) );
                    addEpost = super.verifySqlText( HTMLConv.toHTMLSpecial( addEpost ) );

                    // Lets check the data size
                    if ( addText.length() > 32000 ) {
                        String header = SERVLET_NAME + " servlet. ";
                        new BillBoardError( req, res, header, 74, user.getLanguageIso639_2(), user );
                        return;
                    }
                }


                //ok now we have to send the mail to right email adr that we vill get from the db
                String toEmail = imcref.sqlProcedureStr( "B_GetEmail", new String[]{discId} );
                if ( toEmail == null ) {
                    log( "OBS! No fn email found!" );
                    return;
                }

                String sqlQuest = "B_GetSubjectStr";
                String subjectStr = imcref.sqlProcedureStr( sqlQuest, new String[]{discId, params.getProperty( "META_ID" ), params.getProperty( "SECTION_ID" )} );
                try {
                    this.sendReplieEmail( toEmail, addEpost, subjectStr, addText, addHeader );
                } catch ( ProtocolException pe ) {
                    new BillBoardError( req, res, "BillBoardAdd servlet. ", 76, user.getLanguageIso639_2(), user );
                    log( pe.getMessage() );
                    return;
                }

                imcref.sqlUpdateProcedure( "B_AddReply", new String[]{discId, userId, addHeader, addText, addEpost, req.getRemoteAddr()} );

                // Lets redirect to the servlet which holds in us.
                res.sendRedirect( "BillBoardDiscView?MAIL_SENT=OK" );//ConfDiscView
                return;
            }
        } else {
            String header = SERVLET_NAME + " servlet. ";
            new BillBoardError( req, res, header, 100, user.getLanguageIso639_2(), user );
            return;
        }

    } // DoPost

    /**
     * DoGet
     */

    public void doGet( HttpServletRequest req, HttpServletResponse res )
            throws ServletException, IOException {
        //log("START BillBoardAdd doGet");

        // Lets get all parameters for this servlet
        Properties params = this.getParameters( req );

        // Lets get the user object

        imcode.server.user.UserDomainObject user = Utility.getLoggedOnUser( req );
        if ( user == null ) return;

        if ( !isUserAuthorized( req, res, user ) ) {
            return;
        }

        // Lets get serverinformation

        IMCServiceInterface imcref = ApplicationServer.getIMCServiceInterface();

        int metaId = Integer.parseInt( params.getProperty( "META_ID" ) );
        if ( userHasRightToEdit( imcref, metaId, user ) ) {
            // Lets Get the session user id
            // Ok, Lets get the last discussion in that forum
            HttpSession session = req.getSession( false );

            // Lets get a VariableManager
            VariableManager vm = new VariableManager();

            // Lets get the users first and last names

            vm.addProperty( "ADD_TYPE", params.getProperty( "ADD_TYPE" ) );

            // Lets add the current forum name
            String currSection = imcref.sqlProcedureStr( "B_GetSectionName", new String[]{params.getProperty( "SECTION_ID" )} );
            vm.addProperty( "CURRENT_SECTION_NAME", currSection );
		
            // Lets get the addtype and add it to the page
            if ( params.getProperty( "ADD_TYPE" ).equalsIgnoreCase( "REPLY" ) ) {
                HTML_TEMPLATE = "BillBoard_Add_Reply.htm";
            } else {

                HTML_TEMPLATE = "BillBoard_Add.htm";
            }

            //ok here we have to see if the input fields shall be empty or not.
            Hashtable billPrevData = (Hashtable)session.getAttribute( "billPrevData" );
            if ( billPrevData == null ) {
                vm.addProperty( header, "" );
                vm.addProperty( text, "" );
                vm.addProperty( email, "" );

            } else { //ok we have them, so lets put use them

                session.removeAttribute( "billPrevData" );
                vm.addProperty( header, (String)billPrevData.get( header ) );
                vm.addProperty( text, (String)billPrevData.get( text ) );
                vm.addProperty( email, (String)billPrevData.get( email ) );
            }

            this.sendHtml( req, res, vm, HTML_TEMPLATE );
            return;
        } else {
            String header = SERVLET_NAME + " servlet. ";
            new BillBoardError( req, res, header, 100, user.getLanguageIso639_2(), user );
            return;
        }

    } //DoGet

    /**
     * Collects all the parameters used by this servlet
     */

    private Properties getParameters( HttpServletRequest req ) {

        // Lets get the standard SESSION metainformation
        Properties params = MetaInfo.createPropertiesFromMetaInfoParameters( super.getBillBoardSessionParameters( req ) );

        // Lets get the EXTENDED SESSION PARAMETERS
        super.addExtSessionParametersToProperties( req, params );

        // Lets get our REQUESTPARAMETERS
        String addType = ( req.getParameter( "ADDTYPE" ) == null ) ? " " : ( req.getParameter( "ADDTYPE" ) );
        String addHeader = ( req.getParameter( "ADDHEADER" ) == null ) ? " " : ( req.getParameter( "ADDHEADER" ) );
        String addText = ( req.getParameter( "ADDTEXT" ) == null ) ? " " : ( req.getParameter( "ADDTEXT" ) );
        String addEpost = ( req.getParameter( "ADDEPOST" ) == null ) ? " " : ( req.getParameter( "ADDEPOST" ) );

        // Alright, these parameters are userdefined text, and if the user hasnt filled something in them
        // then the checkparamters will warn for this. The thing is that we dont care if the
        // user passes a text or not, so lets look if the variable is empty, and if it is
        // just put " " in it!
        //Not yet anyway but soon we vill care!!!
        //log("ADD_EPOST:"+ addEpost +" ADD_HEADER:"+ addHeader +" ADD_TEXT:"+ addText+" ADD_TYPE:"+ addType);
        if ( addText.equals( "" ) ) addText = " ";
        if ( addHeader.equals( "" ) ) addHeader = " ";
        if ( addEpost.equals( "" ) ) addEpost = " ";
        if ( addType.equals( "" ) ) addType = " ";

        params.setProperty( "ADD_EPOST", addEpost );
        params.setProperty( "ADD_HEADER", addHeader );
        params.setProperty( "ADD_TEXT", addText );
        params.setProperty( "ADD_TYPE", addType );

        return params;
    }

    /**
     * Sends a replie mail
     * <p/>
     * ##########//this.sendReplieEmail(req,res,toEmail,addEpost,addHeader,addText);
     * <p/>
     * ##########//this.sendReplieEmail(req,res,toEmail,addEpost,addHeader,addText);
     * <p/>
     * ##########//this.sendReplieEmail(req,res,toEmail,addEpost,addHeader,addText);
     * <p/>
     * ##########//this.sendReplieEmail(req,res,toEmail,addEpost,addHeader,addText);
     */
    //##########//this.sendReplieEmail(req,res,toEmail,addEpost,addHeader,addText);
    private void sendReplieEmail(
            String toEmail,
            String fromEmail,
            String header,
            String text,
            String replyHeader )
            throws IOException {

        /* mailserver info */
        String mailserver = Utility.getDomainPref( "smtp_server" );
        String stringMailPort = Utility.getDomainPref( "smtp_port" );
        String stringMailtimeout = Utility.getDomainPref( "smtp_timeout" );

        // Handling of default-values is another area where java can't hold a candle to perl.
        int mailport = 25;
        try {
            mailport = Integer.parseInt( stringMailPort );
        } catch ( NumberFormatException ignored ) {
            // Do nothing, let mailport stay at default.
        }

        int mailtimeout = 10000;
        try {
            mailtimeout = Integer.parseInt( stringMailtimeout );
        } catch ( NumberFormatException ignored ) {
            // Do nothing, let mailtimeout stay at default.
        }

        SMTP smtp = new SMTP( mailserver, mailport, mailtimeout );

        smtp.sendMailWait( fromEmail, toEmail, header, replyHeader + "\n" + text );
    }

    /* checks if email address is valid (abc@x)*/
    private boolean validateEmail( String eMail ) {
        int stringLength = eMail.length();
        int indexAt = eMail.indexOf( "@" );

        if ( indexAt > 0 && indexAt < ( stringLength - 1 ) ) {
            return true;
        } else {
            return false;
        }
    }

    /*
     *Takes a string and takes out substrings marked #MAIL# to #/MAIL# or #LINK# to #/LINK#
     *and convert everything else to html-safe code, Fore the substrings it creates
     *proper html-code
     */
    private String textMailLinkFix( String text ) {
        //super.verifySqlText(HTMLConv.toHTMLSpecial(addText));
        boolean done = false;

        StringBuffer sendStr = new StringBuffer();
        String hackStr = text;

        while ( !done ) {
            boolean linkB = false;
            boolean mailB = false;
            int lStart = hackStr.indexOf( "<L>" );
            //log("lStart = "+lStart);
            int lEnd = hackStr.indexOf( "</L>" );
            //log("lEnd = "+lEnd);
            int mStart = hackStr.indexOf( "<M>" );
            //log("mStart = "+mStart);
            int mEnd = hackStr.indexOf( "</M>" );
            //log("mEnd ="+ mEnd);
            //ok lets do some if cases to figure out what to do
            if ( lStart != -1 && lEnd != -1 ) {
                linkB = true;
            }
            if ( mStart != -1 && mEnd != -1 ) {
                mailB = true;
            }
            if ( linkB && mailB )//ok we have at least one of each
            {
                if ( lStart < mStart )//lets do link stuff //substring(int beginIndex, int endIndex)
                {
                    //ok lets convert everything before the first #LINK# mark
                    if ( lStart > 0 )
                        sendStr.append( HTMLConv.toHTMLSpecial( hackStr.substring( 0, lStart ) ) );

                    //ok lets append the link string
                    sendStr.append( doLinkStuff( hackStr.substring( lStart + 3, lEnd ) ) );

                    hackStr = hackStr.substring( lEnd + 4 );

                } else //ok lets do mailstuff
                {
                    //ok lets convert everything before the first #MAIL# mark
                    if ( mStart > 0 )
                        sendStr.append( HTMLConv.toHTMLSpecial( hackStr.substring( 0, mStart ) ) );

                    //ok lets append the mail string
                    sendStr.append( doMailStuff( hackStr.substring( mStart + 3, mEnd ) ) );

                    hackStr = hackStr.substring( mEnd + 4 );
                }
            } else if ( linkB )//ok lets do some linkstuff
            {
                //ok lets convert everything before the first #LINK# mark
                if ( lStart > 0 )
                    sendStr.append( HTMLConv.toHTMLSpecial( hackStr.substring( 0, lStart ) ) );

                //ok lets append the link string
                sendStr.append( doLinkStuff( hackStr.substring( lStart + 3, lEnd ) ) );

                hackStr = hackStr.substring( lEnd + 4 );
            } else if ( mailB )	//lets do some mailstuff
            {	//ok lets convert everything before the first #MAIL# mark
                if ( mStart > 0 )
                    sendStr.append( HTMLConv.toHTMLSpecial( hackStr.substring( 0, mStart ) ) );

                //ok lets append the mail string
                sendStr.append( doMailStuff( hackStr.substring( mStart + 3, mEnd ) ) );

                hackStr = hackStr.substring( mEnd + 4 );

            } else//ok we are done doing stuff
            {
                sendStr.append( HTMLConv.toHTMLSpecial( hackStr ) );
                done = true;
            }

        }
        return sendStr.toString();
    }

    /*
     *Takes a string and makes an html-link-tag
     */
    private String doLinkStuff( String str ) {
        //<A HRef=' + AddStr + DaLink + ' Target=_new>' + DisplayURL + '</A>' + '&nbsp;
        String daLink = "";
        int i = str.indexOf( "HTTP://" );
        if ( i < 0 ) daLink = "HTTP://";

        return "<A HRef=\"" + daLink + str + "\" Target=\"_new\">" + str + "</A>";
    }

    /*
     *Takes a string and makes an html-mailto-tag
     */
    private String doMailStuff( String str ) {
        //<A HRef="mailto:' + MailLink + '"' + 'Target=_new>' +' ' + MailLink + '</A>' + '&nbsp
        return "<A HRef=\"mailto:" + str + "\" Target=_new> " + str + "</A>";
    }

    /**
     * Init
     */

    public void init( ServletConfig config )
            throws ServletException {
        super.init( config );
        HTML_TEMPLATE = "BillBoard_Add.htm";
        SERVLET_NAME = "BillBoardAdd";
    }

    /**
     * Log function, will work for both servletexec and Apache
     */

    public void log( String str ) {
        super.log( "BillBoardAdd " + str );
        //System.out.println(SERVLET_NAME + " " + str ) ;
    }

} // End of class
