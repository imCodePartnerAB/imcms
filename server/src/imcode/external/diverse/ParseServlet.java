package imcode.external.diverse;

import java.io.File;
import java.util.Vector;

public class ParseServlet  {

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


