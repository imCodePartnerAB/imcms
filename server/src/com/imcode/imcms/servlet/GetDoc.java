package com.imcode.imcms.servlet;

import com.imcode.imcms.mapping.DocumentMapper;
import com.imcode.db.commands.SqlQueryCommand;
import imcode.server.*;
import imcode.server.document.BrowserDocumentDomainObject;
import imcode.server.document.DocumentDomainObject;
import imcode.server.document.FileDocumentDomainObject;
import imcode.server.document.HtmlDocumentDomainObject;
import imcode.server.document.UrlDocumentDomainObject;
import imcode.server.parser.ParserParameters;
import imcode.server.user.UserDomainObject;
import imcode.util.Utility;
import org.apache.log4j.Logger;
import org.apache.log4j.NDC;
import org.apache.oro.text.perl.Perl5Util;
import org.apache.commons.lang.time.StopWatch;

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

    private final static Logger trackLog = Logger.getLogger(ImcmsConstants.ACCESS_LOG);
    private final static Logger log = Logger.getLogger(GetDoc.class.getName());
    private final static String NO_ACTIVE_DOCUMENT_URL = "no_active_document.html";
    private final static String NO_PAGE_URL = "no_page.html";

    private static final String HTTP_HEADER_REFERRER = "Referer";// Note, intended misspelling of "Referrer", according to the HTTP spec.
    public static final String REQUEST_PARAMETER__FILE_ID = "file_id";

    public void doPost(HttpServletRequest req, HttpServletResponse res) throws IOException, ServletException {
        doGet(req, res);
    }

    /** doGet() */
    public void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {

        ImcmsServices imcref = Imcms.getServices();

        int meta_id;

        try {
            meta_id = Integer.parseInt(req.getParameter("meta_id"));
        } catch ( NumberFormatException ex ) {
            // Find the start-page
            meta_id = imcref.getSystemData().getStartDocument();
        }
        output(meta_id, req, res);
    }

    static void output(int meta_id, HttpServletRequest req,
                       HttpServletResponse res) throws IOException, ServletException {
        NDC.push("" + meta_id);
        try {
            StopWatch stopWatch = new StopWatch();
            stopWatch.start();
            String tempstring = getDoc(meta_id, req, res);
            stopWatch.stop();
            long renderTime = stopWatch.getTime();
            log.trace("Rendering document " + meta_id + " took " + renderTime + "ms.");
            if ( tempstring != null ) {
                byte[] tempbytes = tempstring.getBytes(WebAppGlobalConstants.DEFAULT_ENCODING_WINDOWS_1252);
                res.setContentLength(tempbytes.length);
                res.getOutputStream().write(tempbytes);
            }
        } finally {
            NDC.pop();
        }
    }

    public static String getDoc(int meta_id, HttpServletRequest req, HttpServletResponse res)
            throws IOException, ServletException {
        ImcmsServices imcref = Imcms.getServices();

        HttpSession session = req.getSession(true);

        UserDomainObject user = Utility.getLoggedOnUser(req);
        Stack history = (Stack) req.getSession().getAttribute("history");
        if ( history == null ) {
            history = new Stack();
            req.getSession().setAttribute("history", history);
        }

        Integer meta_int = new Integer(meta_id);
        if ( history.empty() || !history.peek().equals(meta_int) ) {
            history.push(meta_int);
        }

        DocumentMapper documentMapper = imcref.getDocumentMapper();
        DocumentDomainObject document = documentMapper.getDocument(meta_id);
        if ( null == document ) {
            return getDocumentDoesNotExistPage(res, user);
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
            Utility.forwardToLogin(req, res);
            return null;
        }

        if ( !document.isPublished() && !user.canEdit(document) ) {
            res.setStatus(HttpServletResponse.SC_FORBIDDEN);
            Utility.setDefaultHtmlContentType(res);
            return imcref.getAdminTemplate(NO_ACTIVE_DOCUMENT_URL, user, null);
        }

        if ( document instanceof UrlDocumentDomainObject ) {
            String url_ref = ( (UrlDocumentDomainObject) document ).getUrl();
            res.sendRedirect(url_ref);
            // Log to accesslog
            trackLog.info(documentRequest);
            return null;
        } else if ( document instanceof BrowserDocumentDomainObject ) {

            String br_id = req.getHeader("User-Agent");
            if ( br_id == null ) {
                br_id = "";
            }
            final Object[] parameters = new String[] { "" + meta_id, br_id };
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
            trackLog.info(documentRequest);
            return null;
        } else if ( document instanceof HtmlDocumentDomainObject ) {
            Utility.setDefaultHtmlContentType(res);
            String htmlDocumentData = imcref.getHtmlDocumentData(meta_id);
            if ( htmlDocumentData == null ) {
                throw new RuntimeException("Null-frameset encountered.");
            }
            // Log to accesslog
            trackLog.info(documentRequest);
            return htmlDocumentData;
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
                return getDocumentDoesNotExistPage(res, user);
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
                log.debug("Exception occured", ex);
            }
            fr.close();
            out.flush();
            out.close();
            // Log to accesslog
            trackLog.info(documentRequest);
            return null;
        } else {
            Utility.setDefaultHtmlContentType(res);
            user.setTemplateGroup(null);
            ParserParameters paramsToParser = new ParserParameters(documentRequest);

            paramsToParser.setTemplate(req.getParameter("template"));
            paramsToParser.setParameter(req.getParameter("param"));
            // Log to accesslog
            trackLog.info(documentRequest);
            return imcref.parsePage(paramsToParser);
        }
    }

    public static String getDocumentDoesNotExistPage(HttpServletResponse res, UserDomainObject user) {
        ImcmsServices imcref = Imcms.getServices();
        List vec = new ArrayList();
        SystemData sysData = imcref.getSystemData();
        String eMailServerMaster = sysData.getServerMasterAddress();
        vec.add("#EMAIL_SERVER_MASTER#");
        vec.add(eMailServerMaster);
        res.setStatus(HttpServletResponse.SC_NOT_FOUND);
        Utility.setDefaultHtmlContentType(res);
        return imcref.getAdminTemplate(NO_PAGE_URL, user, vec);
    }

}
