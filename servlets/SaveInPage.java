
import imcode.server.IMCServiceInterface;
import imcode.util.Check;
import imcode.util.IMCServiceRMI;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.Writer;
import java.util.Vector;

/**
 * Save data from editwindow.
 */
public class SaveInPage extends HttpServlet {

    /**
     * doPost()
     */
    public void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        IMCServiceInterface imcref = IMCServiceRMI.getIMCServiceInterface(req);
        String start_url = imcref.getStartUrl();

        imcode.server.User user;

        // get meta_id
        int meta_id = Integer.parseInt(req.getParameter("meta_id"));
        //		int parent_meta_id = Integer.parseInt(req.getParameter("parent_meta_id")) ;

        // get form data
        imcode.server.Table doc = new imcode.server.Table();

        String template = req.getParameter("template");
        String groupId = req.getParameter("group");

        //the template group admin is a ugly mess but lets try to do the best of it
        //we save the group_id but if the group gets deleted else where it doesn't get changed
        //in the text_docs table, but the system vill not crash it only shows an empty group string.
        if (groupId == null) groupId = "-1"; //if there isn'n anyone lets set it to -1

        if (template != null) {
            doc.addField("template", template);
            //    String menu_template  = req.getParameter("menu_template") ;
            doc.addField("menu_template", template);
            //    String text_template  = req.getParameter("text_template") ;
            doc.addField("text_template", template);
            doc.addField("group_id", groupId);
        }
        // Check if user logged on
        if ((user = Check.userLoggedOn(req, res, start_url)) == null) {
            return;
        }
        // Check if user has write rights
        if (!imcref.checkDocAdminRights(meta_id, user, imcode.server.IMCConstants.PERM_DT_TEXT_CHANGE_TEMPLATE)) {	// Checking to see if user may edit this
            res.setContentType("text/html");

            String output = AdminDoc.adminDoc(meta_id, meta_id, user, req, res);
            if (output != null) {
                Writer out = res.getWriter();
                out.write(output);
            }
            return;
        }

        String lang_prefix = user.getLangPrefix();

        if (req.getParameter("update") != null) {
            Writer out = res.getWriter();

            res.setContentType("text/html");
            user.put("flags", new Integer(0));

            if (template == null) {
                Vector vec = new Vector();
                vec.add("#meta_id#");
                vec.add(String.valueOf(meta_id));
                String htmlStr = imcref.parseDoc(vec, "inPage_admin_no_template.html", lang_prefix);
                out.write(htmlStr);
                return;
            }
            // save textdoc
            imcref.saveTextDoc(meta_id, user, doc);

            imcref.touchDocument(meta_id);

            // return page
            String output = AdminDoc.adminDoc(meta_id, meta_id, user, req, res);
            if (output != null) {
                out.write(output);
            }
            return;

        } else if (req.getParameter("preview") != null) {
            if (template == null) { // If the user didn't select a template
                Vector vec = new Vector();
                vec.add("#meta_id#");
                vec.add(String.valueOf(meta_id));
                res.setContentType("text/html");
                String htmlStr = imcref.parseDoc(vec, "inPage_admin_no_template.html", lang_prefix);
                Writer out = res.getWriter();
                out.write(htmlStr);
                return;
            }
            Object[] temp = null;
            temp = imcref.getDemoTemplate(Integer.parseInt(template));
            if (temp != null) {
                String demoTemplateName = template + "." + (String) temp[0];
                // Set content-type depending on type of demo-template.
                res.setContentType(getServletContext().getMimeType(demoTemplateName));
                byte[] bytes = (byte[]) temp[1];
                ServletOutputStream out = res.getOutputStream();
                res.setContentLength(bytes.length);
                out.write(bytes);
                return;
            } else {
                res.setContentType("text/html");
                String htmlStr = imcref.parseDoc(null, "no_demotemplate.html", lang_prefix);
                Writer out = res.getWriter();
                out.write(htmlStr);
                return;
            }
        } else if (req.getParameter("change_group") != null) {
            res.setContentType("text/html");
            Writer out = res.getWriter();

            user.put("flags", new Integer(imcode.server.IMCConstants.PERM_DT_TEXT_CHANGE_TEMPLATE));

            String group = req.getParameter("group");
            if (group != null) {
                user.setTemplateGroup(Integer.parseInt(req.getParameter("group")));
            }

            String output = AdminDoc.adminDoc(meta_id, meta_id, user, req, res);
            if (output != null) {
                out.write(output);
            }
            return;

        }
    }
}
