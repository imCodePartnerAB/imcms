package com.imcode.imcms.servlet;

import imcode.server.DocumentRequest;
import imcode.server.Imcms;
import imcode.server.ImcmsConstants;
import imcode.server.ImcmsServices;
import imcode.server.Revisits;
import imcode.server.document.DocumentDomainObject;
import imcode.server.document.DocumentTypeDomainObject;
import imcode.server.document.FileDocumentDomainObject;
import imcode.server.document.HtmlDocumentDomainObject;
import imcode.server.document.UrlDocumentDomainObject;
import imcode.server.parser.ParserParameters;
import imcode.server.user.UserDomainObject;
import imcode.util.Utility;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.SocketException;
import java.util.Date;
import java.util.HashMap;
import java.util.Stack;

import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.lang.time.StopWatch;
import org.apache.log4j.Logger;
import org.apache.log4j.NDC;
import org.apache.oro.text.perl.Perl5Util;

import com.imcode.imcms.mapping.DocumentMapper;

public class GetDoc extends HttpServlet {

    private final static Logger TRACK_LOG = Logger.getLogger(ImcmsConstants.ACCESS_LOG);
    private final static Logger LOG = Logger.getLogger(GetDoc.class.getName());
    private final static String NO_ACTIVE_DOCUMENT_URL = "no_active_document.html";

    private static final String HTTP_HEADER_REFERRER = "Referer";// Note, intended misspelling of "Referrer", according to the HTTP spec.
    public static final String REQUEST_PARAMETER__FILE_ID = "file_id";

    public void doPost(HttpServletRequest req, HttpServletResponse res) throws IOException, ServletException {
        doGet(req, res);
    }

    /** doGet() */
    public void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        String documentId = req.getParameter("meta_id");
        viewDoc(documentId, req, res);
    }

    /**
     * Renders document.
     */
    public static void viewDoc(String documentId, HttpServletRequest req,
                         HttpServletResponse res) throws IOException, ServletException {
        ImcmsServices imcref = Imcms.getServices();
        DocumentMapper documentMapper = imcref.getDocumentMapper();
        UserDomainObject user = Utility.getLoggedOnUser( req );
        boolean publishedVersionRequest = req.getParameter("p") != null;
        boolean workingVersionRequest = req.getParameter("w") != null;
                
        DocumentDomainObject document = publishedVersionRequest
        	? documentMapper.getPublishedDocumentForShowing(documentId, user)
        	: workingVersionRequest 
        		? documentMapper.getWorkingDocumentForShowing(documentId, user)
        		: documentMapper.getDocumentForShowing(documentId, user);
        
        if (null == document) {
            res.sendError(HttpServletResponse.SC_NOT_FOUND);
            return;
        }

        viewDoc(document, req, res);
    }

    public static void viewDoc(DocumentDomainObject document, HttpServletRequest req, HttpServletResponse res) throws IOException, ServletException {
        NDC.push("" + document.getId());
        try {
            StopWatch stopWatch = new StopWatch();
            stopWatch.start();

            privateGetDoc(document, res, req);
            stopWatch.stop();
            long renderTime = stopWatch.getTime();
            LOG.trace("Rendering document " + document.getId() + " took " + renderTime + "ms.");
        } finally {
            NDC.pop();
        }
    }

    private static void privateGetDoc(DocumentDomainObject document, HttpServletResponse res,
                                      HttpServletRequest req) throws IOException, ServletException {
        ImcmsServices imcref = Imcms.getServices();

        HttpSession session = req.getSession(true);
        UserDomainObject user = Utility.getLoggedOnUser( req );
        DocumentMapper documentMapper = imcref.getDocumentMapper();

        if ( null == document ) {
            res.sendError(HttpServletResponse.SC_NOT_FOUND);
            return ;
        }                                

        Stack history = (Stack) req.getSession().getAttribute("history");
        if ( history == null ) {
            history = new Stack();
            req.getSession().setAttribute("history", history);
        }

        Integer meta_int = new Integer(document.getId());
        if ( isTextDocument(document) && ( history.empty() || !history.peek().equals(meta_int))) {
            history.push(meta_int);
        }

        String referrer = req.getHeader(HTTP_HEADER_REFERRER);
        DocumentDomainObject referringDocument = null;
        Perl5Util perlrx = new Perl5Util();
        if ( null != referrer && perlrx.match("/meta_id=(\\d+)/", referrer) ) {
            int referring_meta_id = Integer.parseInt(perlrx.group(1));
            referringDocument = documentMapper.getPublishedDocument(referring_meta_id);
        }

        DocumentRequest documentRequest = new DocumentRequest(imcref, user, document, referringDocument, req, res);
        documentRequest.setEmphasize(req.getParameterValues("emp"));

        Cookie[] cookies = req.getCookies();
        HashMap cookieHash = new HashMap();

        for ( int i = 0; cookies != null && i < cookies.length; ++i ) {
            Cookie currentCookie = cookies[i];
            cookieHash.put(currentCookie.getName(), currentCookie.getValue());
        }

        Revisits revisits = new Revisits();

        if ( cookieHash.get("imVisits") == null ) {
            Date now = new Date();
            long lNow = now.getTime();
            String sNow = "" + lNow;
            Cookie resCookie = new Cookie("imVisits", session.getId() + sNow);
            resCookie.setMaxAge(31500000);
            resCookie.setPath("/");
            res.addCookie(resCookie);
            revisits.setRevisitsId(session.getId());
            revisits.setRevisitsDate(sNow);
        } else {
            revisits.setRevisitsId(cookieHash.get("imVisits").toString());
        }
        documentRequest.setRevisits(revisits);

        if ( !user.canAccess(document) ) {
            Utility.forwardToLogin(req, res);
            return;
        }

        if ( !document.isPublished() && !user.canEdit(document) ) {
            res.setStatus(HttpServletResponse.SC_FORBIDDEN);
            Utility.setDefaultHtmlContentType(res);
            imcref.getAdminTemplate(NO_ACTIVE_DOCUMENT_URL, user, null);
            return;
        }
        
        if ( document instanceof UrlDocumentDomainObject ) {
            String url_ref = ( (UrlDocumentDomainObject) document ).getUrl();
            res.sendRedirect(url_ref);
            // Log to accesslog
            TRACK_LOG.info(documentRequest);
            return ;
        } else if ( document instanceof HtmlDocumentDomainObject ) {
            Utility.setDefaultHtmlContentType(res);
            String htmlDocumentData = ((HtmlDocumentDomainObject)document).getHtml();
            TRACK_LOG.info(documentRequest);
            res.getWriter().write(htmlDocumentData);
        } else if ( document instanceof FileDocumentDomainObject ) {
            String fileId = req.getParameter(REQUEST_PARAMETER__FILE_ID);
            FileDocumentDomainObject fileDocument = (FileDocumentDomainObject) document;
            FileDocumentDomainObject.FileDocumentFile file = fileDocument.getFileOrDefault(fileId);
            String filename = file.getFilename();
            String mimetype = file.getMimeType();
            InputStream fr;
            try {
                fr = new BufferedInputStream(file.getInputStreamSource().getInputStream());
            } catch ( IOException ex ) {
                res.sendError(HttpServletResponse.SC_NOT_FOUND);
                return ;
            }
            int len = fr.available();
            ServletOutputStream out = res.getOutputStream();
            res.setContentLength(len);
            res.setContentType(mimetype);
            String content_disposition = ( null != req.getParameter("download") ? "attachment" : "inline" )
                                         + "; filename=\""
                                         + filename
                                         + "\"";
            res.setHeader("Content-Disposition", content_disposition);
            try {
                int bytes_read;
                byte[] buffer = new byte[32768];
                while ( -1 != ( bytes_read = fr.read(buffer) ) ) {
                    out.write(buffer, 0, bytes_read);
                }
            } catch ( SocketException ex ) {
                LOG.debug("Exception occured", ex);
            }
            fr.close();
            out.flush();
            out.close();
            // Log to accesslog
            TRACK_LOG.info(documentRequest);
        } else {
            Utility.setDefaultHtmlContentType(res);
            user.setTemplateGroup(null);
            ParserParameters paramsToParser = new ParserParameters(documentRequest);

            paramsToParser.setTemplate(req.getParameter("template"));
            paramsToParser.setParameter(req.getParameter("param"));
            // Log to accesslog
            TRACK_LOG.info(documentRequest);
            imcref.parsePage(paramsToParser, res.getWriter());
        }
    }

    private static boolean isTextDocument(DocumentDomainObject document) {
        return DocumentTypeDomainObject.TEXT == document.getDocumentType();
    }

}
