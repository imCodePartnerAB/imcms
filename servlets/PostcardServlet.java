/*
 *
 * @(#)PostCardServlet.java
 */

import imcode.server.ApplicationServer;
import imcode.server.HTMLConv;
import imcode.server.IMCServiceInterface;
import imcode.server.SystemData;
import imcode.server.user.UserDomainObject;
import imcode.util.Utility;
import imcode.util.fortune.Quote;
import imcode.util.net.SMTP;
import org.apache.log4j.Category;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.*;
import java.net.ProtocolException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Vector;

/**
 * PARAMS in use, in doPost()
 */

public class PostcardServlet extends HttpServlet {

    private final static String POSTCARD_MAIL_SENT = "bekraftelse.html";
    private final static String POSTCARD_MAIL_SUBJECT = "mail_subject_postcard.html";
    private final static String POSTCARD_MAIL_ERROR = "mail_error.html";
    private final static String POSTCARD_BOTTOM = "preview_bottom.html";
    private final static String POSTCARD_MAIL_BODY = "mail_body_postcard.html";
    private final static String POSTCARD_SET = "preview.html";
    private final static String HTML_TEMPLATE = "vykort.html";//used to parse the postcard page
    private final static String QUOTE_FILE = "citat.txt";//used to parse the postcard page
    private final static String POSTCARD_FOLDER = "postcards";

    private final static DateFormat dateFormat = new SimpleDateFormat( "yyyy-MM-dd HH:mm:ss.SSS " );
    private static Category log = Category.getInstance( PostcardServlet.class.getName() );

    /**
     * Showing input document whit out error
     */

    public void doGet( HttpServletRequest req, HttpServletResponse res ) throws ServletException, IOException {
        //ok the user was satisfied, so now we just have to send the mail
        if ( req.getParameter( "action" ) != null ) {
            this.sendPostcardMail( req, res );
            return;
        }

        //ok its the first time lets load the page for that
        HttpSession session = req.getSession( true );

        //lets get the params we need later on
        String qRow = req.getParameter( "qr" );
        String metaId = req.getParameter( "meta_id" );
        String[] pCStuff = {qRow, metaId};

        session.setAttribute( "postCardStuff", pCStuff );

        //the stuf GetDoc needs to get the quoteline
        req.setAttribute( "externalClass", "QuoteLineCollector" );
        req.setAttribute( "qFile", QUOTE_FILE );
        req.setAttribute( "qLine", qRow );

        RequestDispatcher rd = req.getRequestDispatcher( "GetDoc" );
        rd.forward( req, res );

        return;
    }

    /**
     * process submit
     */
    public void doPost( HttpServletRequest req, HttpServletResponse res ) throws ServletException, IOException {
        String host = req.getHeader( "Host" );

        IMCServiceInterface imcref = ApplicationServer.getIMCServiceInterface();
        // Get the session
        HttpSession session = req.getSession( true );
        // Check if user logged on
        imcode.server.user.UserDomainObject user = (imcode.server.user.UserDomainObject) session.getAttribute("logon.isDone") ;

        SystemData sysData = imcref.getSystemData();

        File templateLib = getExternalTemplateFolder( req, user);


        String qLine = "1";
        String metaId = "";

        //lets get the line nr from session
        String[] pCStuff = (String[])session.getAttribute( "postCardStuff" );
        if ( pCStuff != null ) {
            qLine = pCStuff[0];
            metaId = pCStuff[1];
        }

        int qInt = 1;
        try {
            qInt = Integer.parseInt( qLine );
        } catch ( NumberFormatException nfe ) {
            //some thing gon wrong, but I dont care I give them the first line'
            //instead of the one they wanted
            //System.out.println("qLine wasn't a number");
            qInt = 1;
            log.debug( dateFormat.format( new Date() ) + "qLine wasn't a number", nfe );
        }

        List quoteList = imcref.getQuoteList( QUOTE_FILE );

        String qTextToSend = "";
        if ( quoteList.size() > qInt && qInt >= 0 ) {
            qTextToSend = HTMLConv.toHTMLSpecial( ( (Quote)quoteList.get( qInt ) ).getText() );
        }

        //ok now we have the quot in the string qLine
        //lets get the info we need
        String friendName = req.getParameter( "mailText0" );
        String friendEmail = req.getParameter( "mailTo" );
        String senderName = req.getParameter( "mailText1" );
        String senderMessage = req.getParameter( "mailText2" );
        String imageNr = req.getParameter( "vykort" );
        //lets get the image url from db (we need serverObj, metaId and imageId to do it)

        String imageUrl = imcref.sqlQueryStr( "select imgurl from images where meta_id = ? and name = ?", new String[]{metaId, imageNr} );
        if ( imageUrl == null ) {
            imageUrl = " ";
        } else {
            imageUrl = req.getContextPath() + "/images/" + imageUrl;
        }
        //System.out.println(imageUrl);

        //create the taggs to parse
        Vector vect = new Vector();
        vect.add( "#imageUrl#" );
        vect.add( imageUrl );
        vect.add( "#citat#" );
        vect.add( qTextToSend );
        vect.add( "#cont1#" );
        vect.add( friendName );
        vect.add( "#cont3#" );
        vect.add( senderName );
        vect.add( "#cont4#" );
        vect.add( HTMLConv.toHTML( HTMLConv.toHTMLSpecial( senderMessage ) ) );

        //ok nu ska vi parsa skiten med ett mall skrälle
        String html = imcref.parseExternalDoc( vect, HTML_TEMPLATE, user, "105" );
        //lets get the name to use on the file
        String pcFileName = (String)session.getAttribute( "pcFileName" );
        if ( pcFileName == null ) {
            //lets get the first part of the name
            GregorianCalendar cal = new GregorianCalendar();
            Date currentTime = cal.getTime();
            SimpleDateFormat formatter = new SimpleDateFormat( "yyMMdd" );
            String dateString = formatter.format( currentTime );
            //ok now lets get the second part (the counter)
            File counterFile = new File( templateLib, "postcardCounter.count" );
            PostcardCounter count;
            try {
                ObjectInputStream in = new ObjectInputStream( new FileInputStream( counterFile ) );
                count = (PostcardCounter)in.readObject();
                in.close();
            } catch ( Exception e ) {
                //there wasnt any counter so lets create one
                count = new PostcardCounter();
            }
            count.increment();
            int postcardNr = count.getNumber();
            //lets save the counterObj
            try {
                ObjectOutputStream out = new ObjectOutputStream( new FileOutputStream( counterFile ) );
                out.writeObject( count );
                out.close();
            } catch ( IOException ioe ) {
                log( "Obs! Save couter failed." );
                log( ioe.getMessage() );
                log.debug( dateFormat.format( new Date() ) + "Obs! Save couter failed.", ioe );
            }
            //lets setup the new name
            pcFileName = dateString + "_" + postcardNr + ".html";
            session.setAttribute( "pcFileName", pcFileName );
        }

        //ok lets save the bottom frame page, incase it has been removed
        String bottomString = imcref.parseExternalDoc( new Vector(), POSTCARD_BOTTOM, user, "105" );
        File imagePathFile = imcode.util.Utility.getDomainPrefPath( "image_path" );

        File postcardFolder = new File( imagePathFile.getParent(), POSTCARD_FOLDER );
        File bottomFile = new File( postcardFolder, "bottom.html" );
        FileWriter writ = new FileWriter( bottomFile );
        BufferedWriter buff = new BufferedWriter( writ );

        buff.write( bottomString, 0, bottomString.length() );
        buff.flush();
        buff.close();

        //ok lets save the postcardfile
        File postcardFile = new File( postcardFolder, pcFileName );
        BufferedWriter fileW = new BufferedWriter( new FileWriter( postcardFile ) );
        fileW.write( html, 0, html.length() );
        fileW.flush();
        fileW.close();

        Vector vm = new Vector();
        vm.add( "#postcard#" );
        vm.add( req.getContextPath() + "/postcards/" + pcFileName + "?" + Math.random() );
        vm.add( "#bottom#" );
        vm.add( req.getContextPath() + "/postcards/bottom.html" );

        String frameSetHtml = imcref.parseExternalDoc( vm, POSTCARD_SET, user, "105" );

        res.setContentType( "text/html" );
        PrintWriter out = res.getWriter();
        out.println( frameSetHtml );

        //now we can set up every thing we need to create the mail
        //sendMailWait( sender, mailTo, mailSubject , mailBody );
        String[] mailArr = new String[4];
        mailArr[0] = sysData.getWebMasterAddress();
        mailArr[1] = friendEmail;

        //ok lets parse the mailSubject line
        vect.add( "#mailSubject#" );
        vect.add( senderName );
        mailArr[2] = imcref.parseExternalDoc( vect, POSTCARD_MAIL_SUBJECT, user, "105" );

        //lets parse the mailBody
        vect.add( "#mailText0#" );
        vect.add( friendName );
        vect.add( "#mailText1#" );
        vect.add( senderName );
        vect.add( "#mailText2#" );
        vect.add( "http://" + host );
        vect.add( "#mailText3#" );
        vect.add( pcFileName );
        mailArr[3] = imcref.parseExternalDoc( vect, POSTCARD_MAIL_BODY, user, "105" );

        session.setAttribute( "postcardMail", mailArr );
        return;
    }

    private File getExternalTemplateFolder(HttpServletRequest req, UserDomainObject user) throws IOException {
        IMCServiceInterface imcref = ApplicationServer.getIMCServiceInterface();

        
        // Since our templates are located into the 105 folder, we'll have to hang on 105
        String langPrefix = user != null ? user.getLangPrefix() : imcref.getDefaultLanguageAsIso639_2() ;
        File templateLib = new File( imcref.getTemplateHome(), langPrefix );
        templateLib = new File( templateLib, "105" );

        return templateLib;
    }

    /**
     * Log function. Logs the message to the log file and console
     */
    public void log( String msg ) {
        super.log( msg );
    }

    /**
     * The method to handles the mail-stuff needed
     */
    private void sendPostcardMail( HttpServletRequest req, HttpServletResponse res ) throws IOException {
        /* mailserver info */
        IMCServiceInterface imcref = ApplicationServer.getIMCServiceInterface();
        HttpSession session = req.getSession( false );
        if ( session == null )
            res.sendRedirect( req.getContextPath() + "/servlet/StartDoc" );

        // Check if user logged on
        imcode.server.user.UserDomainObject user = (imcode.server.user.UserDomainObject) session.getAttribute("logon.isDone") ;

        String mailserver = Utility.getDomainPref( "smtp_server" );
        String stringMailPort = Utility.getDomainPref( "smtp_port" );
        String stringMailtimeout = Utility.getDomainPref( "smtp_timeout" );

        res.setContentType( "text/html" );
        PrintWriter out = res.getWriter();


        String[] mailNfo = (String[])session.getAttribute( "postcardMail" );

        session.removeAttribute( "postcardMail" );
        session.removeAttribute( "pcFileName" );
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
        // send mail
        try {
            SMTP smtp = new SMTP( mailserver, mailport, mailtimeout );
            smtp.sendMailWait( mailNfo[0], mailNfo[1], mailNfo[2], mailNfo[3] );

        } catch ( ProtocolException ex ) {
            out.println( imcref.parseExternalDoc( new Vector(), POSTCARD_MAIL_ERROR, user, "105" ) );
            log( "Protocol error while sending mail. " + ex.getMessage() );
            return;
        } catch ( IOException ex ) {
            out.println( imcref.parseExternalDoc( new Vector(), POSTCARD_MAIL_ERROR, user, "105" ) );
            log( "The mailservlet probably timed out. " + ex.getMessage() );
            return;
        }

        out.println( imcref.parseExternalDoc( new Vector(), POSTCARD_MAIL_SENT, user, "105" ) );
        return;
    }

    /**
     * A nice litle counter class to handle the numbering of postcards
     */
    class PostcardCounter implements Serializable {

        private int _count = 0;

        private PostcardCounter() {
        }

        private int getNumber() {
            return _count;
        }

        private void increment() {
            _count++;
        }
    }

}
