package com.imcode.imcms.servlet;

import com.imcode.imcms.mapping.DocumentMapper;
import com.imcode.db.commands.SqlQueryCommand;
import imcode.server.*;
import imcode.server.document.*;
import imcode.server.kerberos.KerberosLoginResult;
import imcode.server.kerberos.KerberosLoginStatus;
import imcode.server.parser.ParserParameters;
import imcode.server.user.UserDomainObject;
import imcode.util.Utility;
import org.apache.commons.io.FilenameUtils;
import org.apache.log4j.Logger;
import org.apache.log4j.NDC;
import org.apache.oro.text.perl.Perl5Util;
import org.apache.commons.lang.time.StopWatch;
import org.apache.commons.io.IOUtils;

import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.SocketException;
import java.util.*;

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

    public static void viewDoc(String documentId, HttpServletRequest req,
                         HttpServletResponse res) throws IOException, ServletException {
        ImcmsServices imcref = Imcms.getServices();
        DocumentMapper documentMapper = imcref.getDocumentMapper();
        DocumentDomainObject document = documentMapper.getDocument( documentId );
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
            referringDocument = documentMapper.getDocument(referring_meta_id);
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
            if (imcref.getConfig().isSsoEnabled() && user.isDefaultUser()) {
                KerberosLoginResult loginResult = imcref.getKerberosLoginService().login(req, res);
                
                if (loginResult.getStatus() == KerberosLoginStatus.SUCCESS) {
                    privateGetDoc(document, res, req);
                }
                
                return;
            }
            
            Utility.forwardToLogin(req, res);
            return;
        }

        if ( !document.isPublished() && !user.canEdit(document) ) {
            res.setStatus(HttpServletResponse.SC_FORBIDDEN);
            Utility.setDefaultHtmlContentType(res);
            String tempstring = imcref.getAdminTemplate(NO_ACTIVE_DOCUMENT_URL, user, null);

            byte[] tempbytes = tempstring.getBytes(Imcms.DEFAULT_ENCODING);
            res.setContentLength(tempbytes.length);
            res.getOutputStream().write(tempbytes);
            
            return;
        }

        if ( document instanceof UrlDocumentDomainObject ) {
            String url_ref = ( (UrlDocumentDomainObject) document ).getUrl();
            res.sendRedirect(url_ref);
            // Log to accesslog
            TRACK_LOG.info(documentRequest);
            return ;
        } else if ( document instanceof BrowserDocumentDomainObject ) {

            String br_id = req.getHeader("User-Agent");
            if ( br_id == null ) {
                br_id = "";
            }
            final Object[] parameters = new String[] { "" + document.getId(), br_id };
            String destinationMetaId = (String) imcref.getDatabase().execute(new SqlQueryCommand("select to_meta_id\n"
                                                                                                 + "from browser_docs\n"
                                                                                                 + "join browsers on browsers.browser_id = browser_docs.browser_id\n"
                                                                                                 + "where meta_id = ? and ? like user_agent order by value desc", parameters, Utility.SINGLE_STRING_HANDLER));
            int toMetaId;
            if ( destinationMetaId != null && !"".equals(destinationMetaId) ) {
                toMetaId = Integer.parseInt(destinationMetaId);
            } else {
                Map browserDocumentIdMap = ( (BrowserDocumentDomainObject) document ).getBrowserDocumentIdMap();
                toMetaId = ( (Integer) browserDocumentIdMap.get(BrowserDocumentDomainObject.Browser.DEFAULT) ).intValue();
            }

            res.sendRedirect("GetDoc?meta_id=" + toMetaId);
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

            // Workaround for #11619 - Android device refuses to download a file from build-in browser.
            // Might not help if user agent is changed manually and does not contain "android".
            String browserId = req.getHeader("User-Agent");
            boolean attachment = req.getParameter("download") != null
                    || (browserId != null && browserId.toLowerCase().contains("android"));

            int len = fr.available();
            String content_disposition = (attachment ? "attachment" : "inline")
                                         + "; filename=\""
                                         + filename
                                         + "\"";

            ServletOutputStream out = null;

            try {
                out = res.getOutputStream();

                res.setContentLength(len);
                res.setContentType(mimetype);
                res.setHeader("Content-Disposition", content_disposition);

                try {
                    IOUtils.copy(fr, out);
                } catch ( SocketException ex ) {
                    LOG.debug("Exception occurred", ex);
                }
            } finally {
                IOUtils.closeQuietly(fr);
                IOUtils.closeQuietly(out);
            }
            
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
