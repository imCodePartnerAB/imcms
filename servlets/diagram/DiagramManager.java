import java.io.*;
import java.util.*;
import javax.servlet.*;
import javax.servlet.http.*;
import imcode.external.diverse.* ;

public class DiagramManager extends HttpServlet {
	private final static String CVS_REV = "$Revision$" ;
	private final static String CVS_DATE = "$Date$" ;
    String HTML_TEMPLATE ;

    public void doGet(HttpServletRequest req, HttpServletResponse res)
        throws ServletException, IOException {

        // Lets check that we still have a session, well need to pass it later to Janus
        // Get the session
        HttpSession session = req.getSession(true);
        // Does the session indicate this user already logged in?
        Object done = session.getAttribute("logon.isDone");  // marker object
        imcode.server.user.User user = (imcode.server.user.User) done ;

        if (done == null) {
            // No logon.isDone means he hasn't logged in.
            // Save the request URL as the true target and redirect to the login page.
            session.setAttribute("login.target", HttpUtils.getRequestURL(req).toString());
            String serverName = MetaInfo.getServerName(req) ;
            String startUrl = MetaInfo.getStartUrl(req) ;
            // log("StartUrl: " + serverName + startUrl) ;
            res.sendRedirect(serverName + startUrl);
            return  ;
        }


        // Lets get the parameters and validate them, we dont have any own
        // parameters so were just validate the metadata

        MetaInfo metaInf = new MetaInfo() ;
        Properties params = metaInf.getParameters(req) ;
        if (metaInf.checkParameters(params) == false) {
            String msg = "The parameters was not correct in call to DiagramManager." ;
            msg += "The parameters was: " + params.toString() ;
            Error err = new Error(req,res, "ERROR.HTM", msg) ;
            err = null ;
            return ;
        }

        String msg  = "" ;
        // Lets check the action
        String action = req.getParameter("action") ;
        //this.log("DiagramManager: Action=" + action) ;
        if(action == null) {
            action = "" ;
            msg = "Unidentified action was sent to DiagramManager!" + params.toString() ;
        }

        // ********* NEW ********
        if(action.equalsIgnoreCase("NEW")) {
            String url = this.createUrl(req,res, "DiagramCreator", params) ;

            if (url != "") {

                // Lets generate the html file and send it to the browser
                VariableManager vm = new VariableManager() ;
                vm.addProperty("META_ID", params.getProperty("META_ID")) ;
                vm.addProperty("PARENT_META_ID", params.getProperty("PARENT_META_ID")) ;
                vm.addProperty("COOKIE_ID", params.getProperty("COOKIE_ID")) ;

               // vm.addProperty("FAST_META_ID", params.getProperty("META_ID")) ;
               // vm.addProperty("FAST_PARENT_META_ID", params.getProperty("PARENT_META_ID")) ;
                //vm.addProperty("FAST_COOKIE_ID", params.getProperty("COOKIE_ID")) ;
                //vm.addProperty("USER_OBJ", user) ;

                String server = MetaInfo.getServletPath(req) ;
                vm.addProperty("SERVER_URL", server) ;
                vm.addProperty("SERVER_URL2", server) ;

                // this.log("DiagramManager creates url:" + url) ;
                // Lets get the TemplateFolder
                String templateLib = MetaInfo.getExternalTemplateFolder(req) ;
                //log("TemplateLib: " + templateLib) ;
                HtmlGenerator htmlObj = new HtmlGenerator(templateLib, HTML_TEMPLATE) ;
                //log("vm:" + vm.toString()) ;
                String htm = htmlObj.createHtmlString(vm, req) ;
                htmlObj.sendToBrowser(req,res,htm) ;
                return ;
            } else {
                msg = "DiagramManager, parametrar saknades i anropet!" ;
            }

            // ********* VIEW ********
        } else if(action.equalsIgnoreCase("VIEW")) {
            String url = this.createUrl(req,res, "DiagramViewer", params) ;
            this.log("Redirects to:" + url) ;
            if (url != "") {
                res.sendRedirect(url) ;
                return ;
            } else {
                msg	= "DiagramManager, parametrar saknades i anropet!" ;
            }

            // ********* CHANGE ********
        } else if(action.equalsIgnoreCase("CHANGE")) {
            String url = this.createUrl(req,res, "ChangeDiagramCoordinator", params) ;
            this.log("Redirects to:" + url) ;
            if (url != "") {
                res.sendRedirect(url) ;
                return ;
            } else {
                msg	= "DiagramManager, parametrar saknades i anropet!" ;
            }
        }

        // LETS SHOW THE ERROR PAGE
        // Lets check if we should alert the user

        Error err = new Error(req, res,"ERROR.HTM", msg) ;
        err = null ;

    } // end of doGet


/**
 * Create the string which we will send the user to change his diagram
 **/

    public String createUrl(HttpServletRequest req, HttpServletResponse res,
    String aServlet, Properties params)
    throws ServletException, IOException {

        MetaInfo metaInf = new MetaInfo() ;
        String reDirectStr = metaInf.getServletPath(req) ;
        String metaStr = metaInf.passMeta(params) ;
        reDirectStr += aServlet + "?" + metaStr ;
        // this.log("DiagramManager redirects to:" + reDirectStr ) ;
        return reDirectStr ;
    }

        /**
         * Detects paths and filenames.
         */

    public void init(ServletConfig config) throws ServletException {

        super.init(config);
        HTML_TEMPLATE = "template_diagramCreator.htm" ;
    }

/**
 * Log function, will work for both servletexec and Apache
 **/

    public void log( String str) {
        super.log(str) ;
        System.out.println("DiagramManager: " + str ) ;
    }

} // End of class