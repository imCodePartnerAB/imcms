package com.imcode.imcms.servlet;

import imcode.server.*;
import imcode.server.document.*;
import imcode.server.parser.ParserParameters;
import imcode.server.user.UserDomainObject;
import imcode.util.Utility;
import org.apache.log4j.Logger;
import org.apache.oro.text.perl.Perl5Util;

import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.*;
import java.io.*;
import java.util.*;

public class GetDoc extends HttpServlet {

    private final static Logger trackLog = Logger.getLogger( IMCConstants.ACCESS_LOG );
    private final static Logger log = Logger.getLogger( GetDoc.class.getName() );
    private final static String NO_ACTIVE_DOCUMENT_URL = "no_active_document.html";
    private final static String NO_PAGE_URL = "no_page.html";
    private final static String NO_PERMISSION_URL = "no_permission.jsp";

    private static final String HTTP_HEADER_REFERRER = "Referer";// Note, intended misspelling of "Referrer", according to the HTTP spec.

    public void doPost( HttpServletRequest req, HttpServletResponse res ) throws IOException, ServletException {
        doGet( req, res );
    }

    /**
     * doGet()
     */
    public void doGet( HttpServletRequest req, HttpServletResponse res ) throws ServletException, IOException {

        IMCServiceInterface imcref = ApplicationServer.getIMCServiceInterface();

        int meta_id;
        Utility.setDefaultHtmlContentType( res );

        try {
            meta_id = Integer.parseInt( req.getParameter( "meta_id" ) );
        } catch ( NumberFormatException ex ) {
            // Find the start-page
            meta_id = imcref.getSystemData().getStartDocument();
        }
        String tempstring = getDoc( meta_id, req, res );
        if ( tempstring != null ) {
            byte[] tempbytes = tempstring.getBytes( WebAppGlobalConstants.DEFAULT_ENCODING_WINDOWS_1252 );
            res.setContentLength( tempbytes.length );
            res.getOutputStream().write( tempbytes );
        }
    }

    public static String getDoc(int meta_id, HttpServletRequest req, HttpServletResponse res)
            throws IOException, ServletException {
        IMCServiceInterface imcref = ApplicationServer.getIMCServiceInterface();

        Vector vec = new Vector();
        SystemData sysData = imcref.getSystemData();
        String eMailServerMaster = sysData.getServerMasterAddress();
        vec.add( "#EMAIL_SERVER_MASTER#" );
        vec.add( eMailServerMaster );

        HttpSession session = req.getSession( true );

        UserDomainObject user = Utility.getLoggedOnUser( req );
        Stack history = (Stack)user.get( "history" );
        if ( history == null ) {
            history = new Stack();
            user.put( "history", history );
        }

        Integer meta_int = new Integer( meta_id );
        if ( history.empty() || !history.peek().equals( meta_int ) ) {
            history.push( meta_int );
        }

        DocumentMapper documentMapper = imcref.getDocumentMapper();
        DocumentDomainObject document = documentMapper.getDocument( meta_id );
        if ( null == document ) {
            res.setStatus( HttpServletResponse.SC_NOT_FOUND );
            return imcref.getAdminTemplate( NO_PAGE_URL, user, vec );
        }

        DocumentRequest documentRequest;
        Revisits revisits;
        String referrer = req.getHeader( HTTP_HEADER_REFERRER );
        DocumentDomainObject referringDocument = null;
        Perl5Util perlrx = new Perl5Util();
        if ( null != referrer && perlrx.match( "/meta_id=(\\d+)/", referrer ) ) {
            int referring_meta_id = Integer.parseInt( perlrx.group( 1 ) );
            referringDocument = documentMapper.getDocument( referring_meta_id );
        }

        documentRequest = new DocumentRequest( imcref, user, document, referringDocument, req );
        documentRequest.setEmphasize( req.getParameterValues( "emp" ) );

        Cookie[] cookies = req.getCookies();
        Hashtable cookieHash = new Hashtable();

        for ( int i = 0; cookies != null && i < cookies.length; ++i ) {
            Cookie currentCookie = cookies[i];
            cookieHash.put( currentCookie.getName(), currentCookie.getValue() );
        }

        revisits = new Revisits();

        if ( cookieHash.get( "imVisits" ) == null ) {
            Date now = new Date();
            long lNow = now.getTime();
            String sNow = "" + lNow;
            Cookie resCookie = new Cookie( "imVisits", session.getId() + sNow );
            resCookie.setMaxAge( 31500000 );
            resCookie.setPath( "/" );
            res.addCookie( resCookie );
            revisits.setRevisitsId( session.getId() );
            revisits.setRevisitsDate( sNow );
        } else {
            revisits.setRevisitsId( cookieHash.get( "imVisits" ).toString() );
        }
        documentRequest.setRevisits( revisits );

        // FIXME: One of the places that need fixing. Number one, we should put the no-permission-page
        // among the templates for the default-language. Number two, we should use just one function for
        // checking permissions. Number three, since the user obviously has logged in, give him the page in his own language!

        if ( !documentMapper.userHasAtLeastDocumentReadPermission( user, document ) ) {
            session.setAttribute( "login.target",
                                  req.getRequestURL().append( "?" ).append( req.getQueryString() ).toString() );
            String redirect = "/imcms/" + user.getLanguageIso639_2() + "/login/" + NO_PERMISSION_URL;
            res.setStatus( HttpServletResponse.SC_FORBIDDEN );
            req.getRequestDispatcher( redirect ).forward( req,res );
            return null;
        }

        if ( !document.isPublished() && !documentMapper.userHasMoreThanReadPermissionOnDocument( user, document ) ) {
            res.setStatus( HttpServletResponse.SC_FORBIDDEN );
            return imcref.getAdminTemplate( NO_ACTIVE_DOCUMENT_URL, user, null );
        }

        if ( document instanceof FormerExternalDocument ) {
            redirectToExternalDocumentTypeWithAction( document, res, "view" );
            // Log to accesslog
            trackLog.info( documentRequest );
            return null;

        } else if ( document instanceof UrlDocumentDomainObject ) {
            String url_ref = imcref.isUrlDoc( meta_id );
            Perl5Util regexp = new Perl5Util();
            if ( !regexp.match( "m!^\\w+:|^[/.]!", url_ref ) ) {
                url_ref = "http://" + url_ref;
            }
            res.sendRedirect( url_ref );
            // Log to accesslog
            trackLog.info( documentRequest );
            return null;
        } else if ( document instanceof BrowserDocumentDomainObject ) {

            String br_id = req.getHeader( "User-Agent" );
            if ( br_id == null ) {
                br_id = "";
            }
            String tmp = imcref.sqlQueryStr( "select top 1 to_meta_id\n"
                                             + "from browser_docs\n"
                                             + "join browsers on browsers.browser_id = browser_docs.browser_id\n"
                                             + "where meta_id = ? and ? like user_agent order by value desc",
                                             new String[]{"" + meta_id, br_id} );
            if ( tmp != null && ( !"".equals( tmp ) ) ) {
                meta_id = Integer.parseInt( tmp );
            } else {
                Map browserDocumentIdMap = ( (BrowserDocumentDomainObject)document ).getBrowserDocumentIdMap();
                meta_id = ( (Integer)browserDocumentIdMap.get( BrowserDocumentDomainObject.Browser.DEFAULT ) ).intValue();
            }

            res.sendRedirect( "GetDoc?meta_id=" + meta_id );
            // Log to accesslog
            trackLog.info( documentRequest );
            return null;
        } else if ( document instanceof HtmlDocumentDomainObject ) {
            String html_str_temp = imcref.isFramesetDoc( meta_id );
            if ( html_str_temp == null ) {
                throw new RuntimeException( "Null-frameset encountered." );
            }
            String htmlStr = html_str_temp;
            // Log to accesslog
            trackLog.info( documentRequest );
            return htmlStr;
        } else if ( document instanceof FileDocumentDomainObject ) {
            FileDocumentDomainObject fileDocument = (FileDocumentDomainObject)document;
            String filename = fileDocument.getFilename();
            String mimetype = fileDocument.getMimeType();
            InputStream fr;
            try {
                fr = new BufferedInputStream( ( (FileDocumentDomainObject)document ).getInputStreamSource().getInputStream() );
            } catch ( IOException ex ) {
                return imcref.getAdminTemplate( NO_PAGE_URL, user, vec );
            }
            int len = fr.available();
            ServletOutputStream out = res.getOutputStream();
            res.setContentLength( len );
            res.setContentType( mimetype );
            String content_disposition = ( null != req.getParameter( "download" ) ? "attachment" : "inline" )
                                         + "; filename=\""
                                         + filename
                                         + "\"";
            res.setHeader( "Content-Disposition", content_disposition );
            try {
                int bytes_read;
                byte[] buffer = new byte[32768];
                while ( -1 != ( bytes_read = fr.read( buffer ) ) ) {
                    out.write( buffer, 0, bytes_read );
                }
            } catch ( java.net.SocketException ex ) {
                log.debug( "Exception occured", ex );
            }
            fr.close();
            out.flush();
            out.close();
            // Log to accesslog
            trackLog.info( documentRequest );
            return null;
        } else {
            String externalparam = null;
            if ( req.getParameter( "externalClass" ) != null || req.getAttribute( "externalClass" ) != null ) {
                String className;
                if ( req.getParameter( "externalClass" ) != null ) {
                    className = req.getParameter( "externalClass" );
                } else {
                    className = (String)req.getAttribute( "externalClass" );
                }
                try {
                    Class cl = Class.forName( className );
                    imcode.external.GetDocControllerInterface obj = (imcode.external.GetDocControllerInterface)cl.newInstance();
                    externalparam = obj.createString( req );
                } catch ( Exception e ) {
                    StringWriter sw = new StringWriter();
                    e.printStackTrace( new PrintWriter( sw ) );
                    externalparam = "<!-- Exception: " + sw.toString() + " -->";
                }
            }
            user.setTemplateGroup( null );
            ParserParameters paramsToParser = new ParserParameters();

            paramsToParser.setTemplate( req.getParameter( "template" ) );
            paramsToParser.setParameter( req.getParameter( "param" ) );
            paramsToParser.setExternalParameter( externalparam );
            paramsToParser.setDocumentRequest( documentRequest );
            String result = imcref.parsePage( paramsToParser );
            // Log to accesslog
            trackLog.info( documentRequest );
            return result;
        }
    }

    public static void redirectToExternalDocumentTypeWithAction( DocumentDomainObject document,
                                                                 HttpServletResponse res,
                                                                 String action ) throws IOException {
        String externalDocumentTypeServlet = "";
        if ( document instanceof ConferenceDocumentDomainObject ) {
            externalDocumentTypeServlet = "ConfManager";
        } else if ( document instanceof ChatDocumentDomainObject ) {
            externalDocumentTypeServlet = "ChatManager";
        } else if ( document instanceof BillboardDocumentDomainObject) {
            externalDocumentTypeServlet = "BillBoardManager";
        }

        String paramStr = "?meta_id=" + document.getId() + "&";
        paramStr += "cookie_id=1A&action=" + action;
        res.sendRedirect( externalDocumentTypeServlet + paramStr );
        }
}
