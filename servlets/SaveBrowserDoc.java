
import imcode.server.IMCServiceInterface;
import imcode.util.Check;
import imcode.util.IMCServiceRMI;
import imcode.util.Parser;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.Writer;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Vector;

/**
 * Save a browserdocument.
 * Shows a change_meta.html which calls SaveMeta
 */
public class SaveBrowserDoc extends HttpServlet {

    /**
     * doPost()
     */
    public void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        String host = req.getHeader("Host");
        IMCServiceInterface imcref = IMCServiceRMI.getIMCServiceInterfaceByHost(host);
        String servlet_url = imcode.util.Utility.getDomainPref("servlet_url", host);
        String start_url = imcref.getStartUrl();

        imcode.server.User user;
        String htmlStr = "";
        int meta_id;
        int parent_meta_id;

        res.setContentType("text/html");
        Writer out = res.getWriter();

        // get meta_id
        meta_id = Integer.parseInt(req.getParameter("meta_id"));

        // get parent_meta_id
        parent_meta_id = Integer.parseInt(req.getParameter("parent_meta_id"));
        log("meta_id:" + meta_id);
        log("parent_meta_id:" + parent_meta_id);
        // Check if user logged on
        if ((user = Check.userLoggedOn(req, res, start_url)) == null) {
            return;
        }
        // Check if user has write rights
        if (!imcref.checkDocAdminRights(meta_id, user, 65536)) {	// Checking to see if user may edit this
            String output = AdminDoc.adminDoc(meta_id, meta_id, user, req, res);
            if (output != null) {
                out.write(output);
            }
            return;
        }

        if (req.getParameter("ok") != null) {
            Enumeration enum = req.getParameterNames();
            while (enum.hasMoreElements()) {
                String param = (String) enum.nextElement();
                if (param.indexOf("bid") == 0) {
                    String bid = param.substring(3);
                    String to = req.getParameter(param);
                    String sqlStr = null;
                    try {
                        int t = Integer.parseInt(to);
                        sqlStr = "update browser_docs set to_meta_id = ? where meta_id = ? and browser_id = ?";
                        imcref.sqlUpdateQuery(sqlStr, new String[]{"" + t, "" + meta_id, bid});
                    } catch (NumberFormatException ex) {
                        if (!bid.equals("0")) {
                            sqlStr = "delete from browser_docs where meta_id = ? and browser_id = ?";
                            imcref.sqlUpdateQuery(sqlStr, new String[]{"" + meta_id, bid});
                        }
                    }
                }
            }

            imcref.touchDocument(meta_id);

            String output = AdminDoc.adminDoc(meta_id, meta_id, user, req, res);
            if (output != null) {
                out.write(output);
            }
            return;

        } else if (req.getParameter("add_browsers") != null) {
            String[] browsers = req.getParameterValues("new_browsers");
            if (browsers != null) {
                for (int i = 0; i < browsers.length; i++) {
                    String sqlStr = "insert into browser_docs (meta_id,to_meta_id,browser_id) values (?,?,?)";
                    imcref.sqlUpdateQuery(sqlStr, new String[]{"" + meta_id, "0", browsers[i]});
                }
            }
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
                    bs += "<tr><td>" + Parser.parseDoc(nm[i], temparr) + ":</td><td><input type=\"text\" size=\"10\" name=\"bid" + b_id[i] + "\" value=\"" + (to[i].equals("0") ? "\">" : to[i] + "\"><a href=\"GetDoc?meta_id=" + to[i] + "&parent_meta_id=" + meta_id + "\">" + to[i] + "</a>") + "</td></tr>";
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
            vec.add("<INPUT TYPE=\"hidden\" NAME=\"doc_type\" VALUE=\"6\">");
            vec.add("#getParentMetaId#");
            vec.add(String.valueOf(parent_meta_id));
            vec.add("#servlet_url#");
            vec.add(servlet_url);
            vec.add("#adminMode#");
            vec.add(imcref.getMenuButtons(meta_id, user));
            String lang_prefix = user.getLangPrefix();

            htmlStr = imcref.parseDoc(vec, "change_browser_doc.html", lang_prefix);
        }
        out.write(htmlStr);
    }
}

