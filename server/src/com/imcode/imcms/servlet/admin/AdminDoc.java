package com.imcode.imcms.servlet.admin;

import com.imcode.imcms.servlet.GetDoc;
import imcode.server.*;
import imcode.server.document.*;
import imcode.server.parser.ParserParameters;
import imcode.server.user.UserDomainObject;
import imcode.util.Parser;
import imcode.util.Utility;

import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Hashtable;
import java.util.Stack;
import java.util.Vector;

import org.apache.commons.lang.ObjectUtils;

public class AdminDoc extends HttpServlet {

    private static final String PARAMETER__META_ID = "meta_id";
    public static final String PARAMETER__DISPATCH_FLAGS = "flags";

    public void doGet(HttpServletRequest req, HttpServletResponse res) throws IOException, ServletException {
        doPost(req, res);
    }

    public void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {

        int metaId = Integer.parseInt(req.getParameter(PARAMETER__META_ID));
        int flags = Integer.parseInt((String) ObjectUtils.defaultIfNull(req.getParameter(PARAMETER__DISPATCH_FLAGS), "0"));

        DocumentMapper documentMapper = ApplicationServer.getIMCServiceInterface().getDocumentMapper();
        DocumentDomainObject document = documentMapper.getDocument(metaId);
        if (IMCConstants.DISPATCH_FLAG__DOCINFO_PAGE == flags) {
            forwardDocumentToDocumentComposerWithAction(req, res, document, DocumentComposer.ACTION__EDIT_DOCUMENT_INFORMATION);
        } else if (document instanceof BrowserDocumentDomainObject && IMCConstants.DISPATCH_FLAG__EDIT_BROWSER_DOCUMENT == flags) {
            forwardDocumentToDocumentComposerWithAction(req, res, document, DocumentComposer.ACTION__EDIT_BROWSER_DOCUMENT);
        } else if (document instanceof HtmlDocumentDomainObject && IMCConstants.DISPATCH_FLAG__EDIT_HTML_DOCUMENT == flags) {
            forwardDocumentToDocumentComposerWithAction(req, res, document, DocumentComposer.ACTION__EDIT_HTML_DOCUMENT);
        } else if (document instanceof UrlDocumentDomainObject && IMCConstants.DISPATCH_FLAG__EDIT_URL_DOCUMENT == flags) {
            forwardDocumentToDocumentComposerWithAction(req, res, document, DocumentComposer.ACTION__EDIT_URL_DOCUMENT);
        } else if (document instanceof FileDocumentDomainObject && IMCConstants.DISPATCH_FLAG__EDIT_FILE_DOCUMENT == flags) {
            forwardDocumentToDocumentComposerWithAction(req, res, document, DocumentComposer.ACTION__EDIT_FILE_DOCUMENT);
        } else {
            IMCServiceInterface imcref = ApplicationServer.getIMCServiceInterface();

            // Find the start-page
            int start_doc = imcref.getSystemData().getStartDocument();

            Utility.setDefaultHtmlContentType( res );
            ServletOutputStream out = res.getOutputStream();
            int meta_id = Integer.parseInt(req.getParameter("meta_id"));
            int parent_meta_id;
            String parent_meta_str = req.getParameter("parent_meta_id");
            if (parent_meta_str != null) {
                parent_meta_id = Integer.parseInt(parent_meta_str);
            } else {
                parent_meta_id = start_doc;
            }

            UserDomainObject user = Utility.getLoggedOnUser(req);

            String tempstring = AdminDoc.adminDoc(meta_id, parent_meta_id, user, req, res);

            if (tempstring != null) {
                byte[] tempbytes = tempstring.getBytes(WebAppGlobalConstants.DEFAULT_ENCODING_CP1252);
                res.setContentLength(tempbytes.length);
                out.write(tempbytes);
            }

        }
    }

    private void forwardDocumentToDocumentComposerWithAction(HttpServletRequest req, HttpServletResponse res,
                                                             DocumentDomainObject document, String action) throws ServletException, IOException {
        DocumentComposer.addObjectToSessionAndSetSessionAttributeNameInRequest(AdminDoc.class.getName()
                + ".document", document, req, DocumentComposer.REQUEST_ATTR_OR_PARAM__DOCUMENT_SESSION_ATTRIBUTE_NAME);
        req.getRequestDispatcher("DocumentComposer?" + DocumentComposer.REQUEST_ATTR_OR_PARAM__ACTION + "=" + action).forward(req, res);
    }

    public static String adminDoc(int meta_id, int parent_meta_id, UserDomainObject user, HttpServletRequest req,
                                  HttpServletResponse res) throws IOException {
        IMCServiceInterface imcref = ApplicationServer.getIMCServiceInterface();

        String htmlStr = "";
        String lang_prefix = user.getLanguageIso639_2();

        Stack history = (Stack) user.get("history");
        if (history == null) {
            history = new Stack();
            user.put("history", history);
        }
        Integer meta_int = new Integer(meta_id);
        if (history.empty() || !history.peek().equals(meta_int)) {
            history.push(meta_int);
        }

        int doc_type = imcref.getDocType(meta_id);

        Integer userflags = (Integer) user.remove(PARAMETER__DISPATCH_FLAGS);		// Get the flags from the user-object
        int flags = (userflags == null) ? 0 : userflags.intValue();	// Are there flags? Set to 0 if not.

        DocumentDomainObject document = imcref.getDocumentMapper().getDocument(meta_id);

        try {
            flags = Integer.parseInt(req.getParameter(PARAMETER__DISPATCH_FLAGS));	// Check if we have a "flags" in the request too. In that case it takes precedence.
        } catch (NumberFormatException ex) {
            if (flags == 0) {
                if (doc_type != 1 && doc_type != 2) {
                    Vector vec = new Vector(2);
                    vec.add("#adminMode#");
                    vec.add(imcref.getAdminButtons(user, document));
                    vec.add("#doc_type_description#");
                    vec.add(imcref.getAdminTemplate("adminbuttons/adminbuttons" + doc_type + "_description.html", user, null));
                    return imcref.getAdminTemplate("docinfo.html", user, vec);
                }
            }
        }

        if (!imcref.checkDocAdminRights(meta_id, user, flags)) {
            return GetDoc.getDoc(meta_id, parent_meta_id, req, res);
        }

        String metaImageUri = ImageBrowse.getImageUri(req);

        // Lets detect which view the admin wants
        if ((flags & 4) != 0) { // User rights
            htmlStr = imcode.util.MetaDataParser.parseMetaPermission(String.valueOf(meta_id), String.valueOf(meta_id), user, "docinfo/change_meta_rights.html", metaImageUri);
            return htmlStr;
        }

        switch (doc_type) {

            default:
                DocumentRequest documentRequest = new DocumentRequest(imcref, user, document, null, req);
                ParserParameters parserParameters = new ParserParameters();
                parserParameters.setDocumentRequest(documentRequest);
                parserParameters.setFlags(flags);
                String editingMenuIndexStr = req.getParameter("editmenu");
                if (null != editingMenuIndexStr) {
                    parserParameters.setEditingMenuIndex(Integer.valueOf(editingMenuIndexStr));
                }
                String result = imcref.parsePage(parserParameters);
                return result;

            case DocumentDomainObject.DOCTYPE_DIAGRAM:
            case DocumentDomainObject.DOCTYPE_CONFERENCE:
                if (req == null || res == null) {
                    throw new NullPointerException("Request or response cannot be null for external docs.");
                }
                imcode.server.ExternalDocType ex_doc = imcref.isExternalDoc(meta_id, user);
                if (ex_doc != null) {
                    String paramStr = "?meta_id=" + meta_id + "&";
                    paramStr += "parent_meta_id=" + parent_meta_id + "&";
                    paramStr += "cookie_id=" + "1A" + "&";
                    paramStr += "action=change";
                    Utility.redirect(req, res, ex_doc.getCallServlet() + paramStr);
                    return null;
                }
                break;

            case DocumentDomainObject.DOCTYPE_URL:
                Vector urlvec = new Vector();
                String[] strary = imcref.sqlQuery("select u.url_ref,m.target, m.frame_name from url_docs u, meta m where m.meta_id = u.meta_id and m.meta_id = ?", new String[]{
                    "" + meta_id
                });
                String url_ref = strary[0];
                String target = strary[1];
                String frame_name = "";
                if ("_self".equals(target)) {
                    urlvec.add("#_self#");
                    urlvec.add("checked");
                } else if ("_top".equals(target)) {
                    urlvec.add("#_top#");
                    urlvec.add("checked");
                } else if ("_blank".equals(target)) {
                    urlvec.add("#_blank#");
                    urlvec.add("checked");
                } else if ("_other".equals(target)) {
                    frame_name = strary[2];
                    urlvec.add("#_other#");
                    urlvec.add("checked");
                } else {
                    urlvec.add("#_other#");
                    urlvec.add("checked");
                    frame_name = target;
                }
                urlvec.add("#frame_name#");
                urlvec.add(frame_name);
                urlvec.add("#url_doc_ref#");
                urlvec.add(url_ref);
                urlvec.add("#getMetaId#");
                urlvec.add(String.valueOf(meta_id));
                urlvec.add("#getParentMetaId#");
                urlvec.add(String.valueOf(parent_meta_id));

                htmlStr = imcref.getAdminTemplate("change_url_doc.html", user, urlvec);
                break;

            case DocumentDomainObject.DOCTYPE_HTML:
                Vector fsetvec = new Vector();
                String fset = imcref.sqlQueryStr("select frame_set from frameset_docs where meta_id = ?", new String[]{
                    "" + meta_id
                });
                fsetvec.add("#frame_set#");
                fsetvec.add(fset);
                fsetvec.add("#getMetaId#");
                fsetvec.add(String.valueOf(meta_id));
                fsetvec.add("#getParentMetaId#");
                fsetvec.add(String.valueOf(parent_meta_id));
                htmlStr = imcref.getAdminTemplate("change_frameset_doc.html", user, fsetvec);

                break;

            case DocumentDomainObject.DOCTYPE_BROWSER:
                Vector vec = new Vector();
                String sqlStr = "select name,browsers.browser_id,to_meta_id from browser_docs join browsers on browsers.browser_id = browser_docs.browser_id where meta_id = ? order by value desc,name asc";
                Hashtable hash = imcref.sqlQueryHash(sqlStr, new String[]{"" + meta_id});
                String[] b_id = (String[]) hash.get("browser_id");
                String[] nm = (String[]) hash.get("name");
                String[] to = (String[]) hash.get("to_meta_id");
                String bs = "";
                if (b_id != null) {
                    bs += "<table width=\"50%\" border=\"0\">";
                    for (int i = 0; i < b_id.length; i++) {
                        String[] temparr = {" ", "&nbsp;"};
                        bs += "<tr><td>" + Parser.parseDoc(nm[i], temparr)
                                + ":</td><td><input type=\"text\" size=\"10\" name=\"bid"
                                + b_id[i]
                                + "\" value=\""
                                + (to[i].equals("0")
                                ? "\">"
                                : to[i] + "\"><a href=\"GetDoc?meta_id=" + to[i] + "&parent_meta_id=" + meta_id
                                + "\">"
                                + to[i]
                                + "</a>")
                                + "</td></tr>";
                    }
                    bs += "</table>";
                }
                vec.add("#browsers#");
                vec.add(bs);
                sqlStr = "select browser_id,name from browsers where browser_id not in (select browsers.browser_id from browser_docs join browsers on browsers.browser_id = browser_docs.browser_id where meta_id = ? ) order by value desc,name asc";
                hash = imcref.sqlQueryHash(sqlStr, new String[]{"" + meta_id});
                b_id = (String[]) hash.get("browser_id");
                nm = (String[]) hash.get("name");
                String nb = "";
                if (b_id != null) {
                    for (int i = 0; i < b_id.length; i++) {
                        nb += "<option value=\"" + b_id[i] + "\">" + nm[i] + "</option>";
                    }
                }
                vec.add("#new_browsers#");
                vec.add(nb);
                vec.add("#getMetaId#");
                vec.add(String.valueOf(meta_id));
                vec.add("#getDocType#");
                vec.add("<INPUT TYPE=\"hidden\" NAME=\"doc_type\" VALUE=\"" + doc_type + "\">");
                vec.add("#DocMenuNo#");
                vec.add("");
                vec.add("#getParentMetaId#");
                vec.add(String.valueOf(parent_meta_id));
                htmlStr = imcref.getAdminTemplate("change_browser_doc.html", user, vec);
                break;

            case DocumentDomainObject.DOCTYPE_FILE:
                String[] sqlResult = DocumentMapper.sqlGetFromFileDocs(imcref, meta_id);
                String fileName = sqlResult[0];
                String mimeType = sqlResult[1];
                sqlStr = "select mime,mime_name from mime_types where lang_prefix = ? and mime != 'other'";
                hash = imcref.sqlQueryHash(sqlStr, new String[]{lang_prefix});
                String[] mime = (String[]) hash.get("mime");
                String[] mime_name = (String[]) hash.get("mime_name");
                String optStr = "";
                String other = mimeType;
                String tmp;
                for (int i = 0; i < mime.length; i++) {
                    if (mime[i].equals(mimeType)) {
                        tmp = "\"" + mime[i] + "\" selected";
                        other = "";
                    } else {
                        tmp = "\"" + mime[i] + "\"";
                    }
                    optStr += "<option value=" + tmp + ">" + mime_name[i] + "</option>";
                }
                Vector d = new Vector();
                d.add("#file#");
                d.add(fileName);
                d.add("#mime#");
                d.add(optStr);
                d.add("#other#");
                d.add(other);
                d.add("#meta_id#");
                d.add(String.valueOf(meta_id));
                d.add("#parent_meta_id#");
                d.add(String.valueOf(parent_meta_id));
                htmlStr = imcref.getAdminTemplate("change_fileupload.html", user, d);
                break;
        }
        String[] parsetmp = {"#adminMode#", imcref.getAdminButtons(user, document)};
        htmlStr = imcode.util.Parser.parseDoc(htmlStr, parsetmp);

        return htmlStr;
    }

}
