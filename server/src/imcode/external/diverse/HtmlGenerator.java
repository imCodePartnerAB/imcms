package imcode.external.diverse;

import java.io.*;
import java.util.*;

import javax.servlet.*;
import javax.servlet.http.*;

import imcode.server.* ;
import imcode.util.* ;

/**
 *  Description of the Class
 *
 *@author     kreiger
 *@created    den 30 augusti 2001
 */
public class HtmlGenerator {

    private File HTML_TEMPLATE;


    /**
     *  Constructor for the HtmlGenerator object
     *
     *@param  path  Description of Parameter
     *@param  file  Description of Parameter
     */
    public HtmlGenerator(File path, String file) {
        this(new File(path, file));
    }


    /**
     *  Constructor for the HtmlGenerator object
     *
     *@param  templateFile  Description of Parameter
     */
    private HtmlGenerator(File templateFile) {
        HTML_TEMPLATE = templateFile;
    }

    /**
     *  Constructor for the HtmlGenerator object
     */
    public HtmlGenerator() {
        HTML_TEMPLATE = null;
    }


    // ************************** CREATE HTML STRING *******************


    // End getTable


    // End getTableHeader


    /**
     *  Description of the Method
     *
     *@param  req                   Description of Parameter
     *@param  res                   Description of Parameter
     *@param  str                   Description of Parameter
     *@exception  ServletException  Description of Exception
     *@exception  IOException       Description of Exception
     */
    public void sendToBrowser(HttpServletRequest req, HttpServletResponse res, String str)
             throws IOException {

        // Lets send settings to a browser
        PrintWriter out = res.getWriter();
        res.setContentType("Text/html");
        out.println(str);
    }


    // End getRmiCaller


    /**
     *  Creates an html string from a Variable Manager object
     *
     *@param  vMan                  Description of Parameter
     *@param  req                   Description of Parameter
     *@return                       Description of the Returned Value
     *@exception  ServletException  Description of Exception
     *@exception  IOException       Description of Exception
     */

    public String createHtmlString(VariableManager vMan, HttpServletRequest req)
             throws IOException {

        Vector htmlTags = vMan.getAllProps();
        Vector data = vMan.getAllValues();
        return createHtmlString(htmlTags, data, req);
    }
    // End of createHtmlString


    // End of createHtmlString


    /**
     *  Creates an html string from 2 vector object
     *
     *@param  htmlTags              Description of Parameter
     *@param  data                  Description of Parameter
     *@param  req                   Description of Parameter
     *@return                       Description of the Returned Value
     *@exception  ServletException  Description of Exception
     *@exception  IOException       Description of Exception
     */

    public String createHtmlString(Vector htmlTags, Vector data, HttpServletRequest req)
             throws IOException {

        // Lets validate the input data, if input data is not correct
        if (htmlTags.isEmpty() || data.isEmpty()) {
            String msg = "Not valid input to HtmlGenerator.createHtmlString";
            msg += "The HTML-File was " + HTML_TEMPLATE;
            return msg;
        }

        // Lets insert hertzmarks to the tags vector
        String tmp = new String();
        for (int i = 0; i < htmlTags.size(); i++) {
            tmp = htmlTags.elementAt(i).toString();
            tmp = "#" + tmp + "#";
            htmlTags.set(i, tmp);
        }

        // Lets get the html file into an string
        String srcHtml = ReadTextFile.getFile(HTML_TEMPLATE);

        // Lets check that we really got something from ReadTextFile, if the template is not
        // found for example
        if (srcHtml.equals("")) {
            String msg = "Nothing was returned from ReadTextFile. Template probably not found ";
            msg += "The html file was: " + HTML_TEMPLATE;
            return msg;
        }

        String host = req.getHeader("Host");
	IMCServiceInterface imcref = IMCServiceRMI.getIMCServiceInterfaceByHost(host) ;
        String theHtml = imcref.parseDoc(srcHtml, htmlTags, data);
        return theHtml;
    }
    // End of createHtmlString


}
