
import java.io.*;
import javax.servlet.*;
import javax.servlet.http.*;

import imcode.util.*;
import imcode.server.*;

public class Restart extends HttpServlet {

    public void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        IMCServiceInterface imcref = IMCServiceRMI.getIMCServiceInterface(req);
        String start_url = imcref.getStartUrl();

        User user;
        if ((user = Check.userLoggedOn(req, res, start_url)) == null) {
            return;
        }

        if (!imcref.checkAdminRights(user)) {
            Utility.redirect(req, res, start_url);
            return;
        }

        log("Restarting...");
        Prefs.flush();
        log("Flushed preferencescache");
        IMCServiceRMI.flush();
        log("Flushed RMI-interfacecache");
        log("Restart Complete.");
        res.getOutputStream().println("Restart complete.");
    }
}
