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

    private final static Logger trackLog = Logger.getLogger( ImcmsConstants.ACCESS_LOG );
    private final static Logger log = Logger.getLogger( GetDoc.class.getName() );
    private final static String NO_ACTIVE_DOCUMENT_URL = "no_active_document.html";
    private final static String NO_PAGE_URL = "no_page.html";

    private static final String HTTP_HEADER_REFERRER = "Referer";// Note, intended misspelling of "Referrer", according to the HTTP spec.
    public static final String REQUEST_PARAMETER__FILE_ID = "file_id";

    public void doPost( HttpServletRequest req, HttpServletResponse res ) throws IOException, ServletException {
        doGet( req, res );
    }

    /**
     * doGet()
     */
    public void doGet( HttpServletRequest req, HttpServletResponse res ) throws ServletException, IOException {

        ImcmsServices imcref = Imcms.getServices();

        int meta_id;

        try {
            meta_id = Integer.parseInt( req.getParameter( "meta_id" ) );
        } catch ( NumberFormatException ex ) {
            // Find the start-page
            meta_id = imcref.getSystemData().getStartDocument();
        }
        output( meta_id, req, res );
    }

    static void output( int meta_id, HttpServletRequest req, HttpServletResponse res ) throws IOException, ServletException {
        String tempstring = getDoc( meta_id, req, res );
        if ( tempstring != null ) {
            byte[] tempbytes = tempstring.getBytes( WebAppGlobalConstants.DEFAULT_ENCODING_WINDOWS_1252 );
            res.setContentLength( tempbytes.length );
            res.getOutputStream().write( tempbytes );
        }
    }

    public static String getDoc( int meta_id, HttpServletRequest req, HttpServletResponse res )
            throws IOException, ServletException {
        ImcmsServices imcref = Imcms.getServices();

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
            return getDocumentDoesNotExistPage( res, user );
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

        documentRequest = new DocumentRequest( imcref, user, document, referringDocument, req, res );
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

        if ( !user.canAccess( document ) ) {
            Utility.forwardToLogin( req, res );
            return null;
        }

        if ( !document.isPublished() && !user.canEdit( document ) ) {
            res.setStatus( HttpServletResponse.SC_FORBIDDEN );
            return imcref.getAdminTemplate( NO_ACTIVE_DOCUMENT_URL, user, null );
        }

        if ( document instanceof FormerExternalDocumentDomainObject ) {
            Utility.setDefaultHtmlContentType( res );
            redirectToExternalDocumentTypeWithAction( document, req, res, "view" );
            // Log to accesslog
            trackLog.info( documentRequest );
            return null;

        } else if ( document instanceof UrlDocumentDomainObject ) {
            String url_ref = ( (UrlDocumentDomainObject)document ).getUrl();
            res.sendRedirect( url_ref );
            // Log to accesslog
            trackLog.info( documentRequest );
            return null;
        } else if ( document instanceof BrowserDocumentDomainObject ) {

            String br_id = req.getHeader( "User-Agent" );
            if ( br_id == null ) {
                br_id = "";
            }
            String tmp = imcref.getExceptionUnhandlingDatabase().executeStringQuery( "select top 1 to_meta_id\n"
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
            Utility.setDefaultHtmlContentType( res );
            String html_str_temp = imcref.isFramesetDoc( meta_id );
            if ( html_str_temp == null ) {
                throw new RuntimeException( "Null-frameset encountered." );
            }
            String htmlStr = html_str_temp;
            // Log to accesslog
            trackLog.info( documentRequest );
            return htmlStr;
        } else if ( document instanceof FileDocumentDomainObject ) {
            String fileId = req.getParameter( REQUEST_PARAMETER__FILE_ID );
            FileDocumentDomainObject fileDocument = (FileDocumentDomainObject)document;
            FileDocumentDomainObject.FileDocumentFile file = fileDocument.getFileOrDefault( fileId );
            String filename = file.getFilename();
            String mimetype = file.getMimeType();
            InputStream fr;
            try {
                fr = new BufferedInputStream( file.getInputStreamSource().getInputStream() );
            } catch ( IOException ex ) {
                return getDocumentDoesNotExistPage( res, user ) ;
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
            Utility.setDefaultHtmlContentType( res );
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
            ParserParameters paramsToParser = new ParserParameters( documentRequest );

            paramsToParser.setTemplate( req.getParameter( "template" ) );
            paramsToParser.setParameter( req.getParameter( "param" ) );
            paramsToParser.setExternalParameter( externalparam );
            // Log to accesslog
            trackLog.info( documentRequest );
            String result = imcref.parsePage( paramsToParser );
            return result;
        }
    }

    public static String getDocumentDoesNotExistPage( HttpServletResponse res, UserDomainObject user ) {
        ImcmsServices imcref = Imcms.getServices();
        List vec = new ArrayList();
        SystemData sysData = imcref.getSystemData();
        String eMailServerMaster = sysData.getServerMasterAddress();
        vec.add( "#EMAIL_SERVER_MASTER#" );
        vec.add( eMailServerMaster );
        res.setStatus( HttpServletResponse.SC_NOT_FOUND );
        return imcref.getAdminTemplate( NO_PAGE_URL, user, vec );
    }

    public static void redirectToExternalDocumentTypeWithAction( DocumentDomainObject document,
                                                                 HttpServletRequest request, HttpServletResponse res,
                                                                 String action ) throws IOException {
        String externalDocumentTypeServlet = "";
        if ( document instanceof ConferenceDocumentDomainObject ) {
            externalDocumentTypeServlet = "ConfManager";
        } else if ( document instanceof ChatDocumentDomainObject ) {
            externalDocumentTypeServlet = "ChatManager";
        } else if ( document instanceof BillboardDocumentDomainObject ) {
            externalDocumentTypeServlet = "BillBoardManager";
        }

        String paramStr = "?meta_id=" + document.getId() + "&";
        paramStr += "cookie_id=1A&action=" + action;
        res.sendRedirect( request.getContextPath()+"/servlet/"+externalDocumentTypeServlet + paramStr );
    }
}
