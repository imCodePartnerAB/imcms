package imcode.external.diverse;

import java.io.*;
import java.util.*;

import javax.servlet.http.*;

import imcode.server.* ;

public class HtmlGenerator {

    private File HTML_TEMPLATE;

    public HtmlGenerator(File path, String file) {
        this(new File(path, file));
    }


    private HtmlGenerator(File templateFile) {
        HTML_TEMPLATE = templateFile;
    }

    public HtmlGenerator() {
        HTML_TEMPLATE = null;
    }

    public void sendToBrowser(HttpServletRequest req, HttpServletResponse res, String str)
             throws IOException {

        // Lets send settings to a browser
        PrintWriter out = res.getWriter();
        res.setContentType("Text/html");
        out.println(str);
    }

    /**
     *  Creates an html string from a Variable Manager object
     */
    public String createHtmlString(VariableManager vMan, HttpServletRequest req)
             throws IOException {

        Vector htmlTags = vMan.getAllProps();
        Vector data = vMan.getAllValues();
        return createHtmlString(htmlTags, data, req);
    }


    /**
     *  Creates an html string from 2 vector object
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


        IMCServiceInterface imcref = ApplicationServer.getIMCServiceInterface() ;
        String theHtml = imcref.parseDoc(srcHtml, htmlTags, data);
        return theHtml;
    }

}
