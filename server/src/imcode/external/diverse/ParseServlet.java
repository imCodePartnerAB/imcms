package imcode.external.diverse;

import java.io.*;
import java.util.*;
import javax.servlet.*;
import javax.servlet.http.*;

public class ParseServlet /* extends HttpServlet */ {

    private String htmlStr;

    public String getHtmlDoc() {
        return htmlStr;
    }

    public ParseServlet(File theFile, Vector tags, Vector data) {
        htmlStr = "";
        ParseDoc doc = new ParseDoc(theFile);
        synchronized (doc) {
            doc.readFile();
        }
        htmlStr = doc.parse(tags, data);
        doc = null;
        
    } // ParseServlet
    
} // end of ParseServlet class


