
import java.io.*;
import javax.servlet.*;
import javax.servlet.http.*;

import imcode.util.*;
import imcode.server.*;

public class SaveFileUpload extends HttpServlet {

    public void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        String host = req.getHeader("Host");
        IMCServiceInterface imcref = IMCServiceRMI.getIMCServiceInterface(req);
        String start_url = imcref.getStartUrl();
        File file_path = imcode.util.Utility.getDomainPrefPath("file_path", host);

        imcode.server.User user;

        // Check if user logged on
        if ((user = Check.userLoggedOn(req, res, start_url)) == null) {
            return;
        }

        res.setHeader("Cache-Control", "no-cache; must-revalidate;");
        res.setHeader("Pragma", "no-cache;");

        int length = req.getContentLength();

        ServletInputStream in = req.getInputStream();
        Writer out = res.getWriter();

        byte buffer[] = new byte[length];
        int bytes_read = 0;
        while (bytes_read < length) {
            bytes_read += in.read(buffer, bytes_read, length - bytes_read);
        }

        String contentType = req.getContentType();
        MultipartFormdataParser mp = new MultipartFormdataParser(new String(buffer, "8859_1"), contentType);

        String meta_id = mp.getParameter("meta_id");
        int meta_id_int = Integer.parseInt(meta_id);

        res.setContentType("text/html");
        // Check if user has write rights
        if (!imcref.checkDocAdminRights(meta_id_int, user, 65536)) {	// Checking to see if user may edit this
            String output = AdminDoc.adminDoc(meta_id_int, meta_id_int, user, req, res);
            if (output != null) {
                out.write(output);
            }
            return;
        }

        if (mp.getParameter("ok") != null) {
            String file = mp.getParameter("file");
            String filename = mp.getFilename("file");
            String oldname = mp.getParameter("oldname");
            String mime = mp.getParameter("mime"); // The users choice in the listbox (""==autodetect,"other"==other)
            String other = mp.getParameter("other");
            if (null == filename || "".equals(filename)) {
                // We weren't given a new filename to play with.
                // We probably weren't given a file either, but let's not assume that.
                // We use the previous name instead.
                filename = oldname;
            }
            if (filename == null) {
                // We don't have a filename... use the empty string.
                filename = "";
            }

            // Check if a mime-type was chosen
            if (mime == null || "".equals(mime)) {

                // No mime-type chosen?
                // Set the mime-type to the value of the 'other'-field.
                mime = other;

                if (mime == null || "".equals(mime)) {
                    // Nothing in the other-field?

                    // Auto-detect mime-type from filename.
                    if (!"".equals(filename)) {
                        mime = getServletContext().getMimeType(filename.toLowerCase());
                    }

                } else if (mime.indexOf('/') == -1) {
                    // The given mimetype does not contain '/',
                    // and is thus invalid.

                    // Assume it is a file-extension,
                    // and autodetect from that.
                    if (mime.charAt(0) == '.') {
                        mime = getServletContext().getMimeType("_" + mime.toLowerCase());
                    } else {
                        mime = getServletContext().getMimeType("_." + mime.toLowerCase());
                    }
                }

                // If we still don't have a mime-type,
                // use standard unknown binary mime-type.
                if (mime == null || "".equals(mime)) {
                    mime = "application/octet-stream";
                }
            }

            File fn = null;
            if (file.length() > 0) {
                fn = new File(filename);
                filename = fn.getName();
                fn = new File(file_path, meta_id + "_se");
            }

            if (file.length() > 0) {
                imcref.sqlUpdateQuery("update fileupload_docs set filename = ?, mime = ? where meta_id = ?", new String[] {filename,mime,meta_id});
            } else {
                imcref.sqlUpdateQuery("update fileupload_docs set mime = ? where meta_id = ?", new String[]{mime, meta_id});
            }

            // Write the file to disk.
            if (file.length() > 0) {
                FileOutputStream fos = new FileOutputStream(fn);
                fos.write(file.getBytes("8859_1"));
                fos.close();
            }
        }

        imcref.touchDocument(meta_id_int);

        String output = AdminDoc.adminDoc(Integer.parseInt(meta_id), Integer.parseInt(meta_id), user, req, res);
        if (output != null) {
            out.write(output);
        }
        return;

    }

}
