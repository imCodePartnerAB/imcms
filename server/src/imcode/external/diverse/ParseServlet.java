package imcode.external.diverse ;

import java.io.*;
import java.util.*;
import javax.servlet.*;
import javax.servlet.http.*;



public class ParseServlet /* extends HttpServlet */ {
	private final static String CVS_REV = "$Revision$" ;
	private final static String CVS_DATE = "$Date$" ;
    String htmlStr ;
    
    public String getHtmlDoc(){
        return htmlStr ;
    }
    
    
    public ParseServlet(File theFile, Vector tags, Vector data) {
        htmlStr = "" ;
        //res.setContentType("text/html");
        //PrintWriter out = res.getWriter();
    /*
    Vector tags = new Vector() ;
    Vector data = new Vector() ;
    tags.addElement("#width#") ;
    tags.addElement("#height#") ;
    data.addElement("800") ;     // width
    data.addElement("600") ;      // height
     */
        ParseDoc doc = new ParseDoc(theFile) ;
        synchronized(doc) {
            doc.readFile() ;
        }
        htmlStr = doc.parse(tags,data) ;
        doc = null ;
        
        //out.println(htmlStr) ;
        //System.out.println(htmlStr ) ;
        
    } // ParseServlet
    
    /*    
    public void doGet(HttpServletRequest req, HttpServletResponse res)
    throws ServletException, IOException {
        String htmlStr = "" ;
        res.setContentType("text/html");
        PrintWriter out = res.getWriter();
        
        Vector tags = new Vector() ;
        Vector data = new Vector() ;
        tags.addElement("#width#") ;
        tags.addElement("#height#") ;
        data.addElement("800") ;     // width
        data.addElement("600") ;      // height
        
        ParseDoc doc = new ParseDoc("c:\\test.txt") ;
        doc.readFile() ;
        htmlStr = doc.parse(tags,data) ;
        doc = null ;
        
        out.println(htmlStr) ;
        System.out.println(htmlStr ) ;
        
    } // end of doGet
    */    
} // end of ParseServlet class


